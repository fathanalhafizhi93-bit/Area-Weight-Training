package com.fathan.ironpulse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.fathan.ironpulse.alarm.AlarmScheduler
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.IronPulseRoot
import com.fathan.ironpulse.ui.theme.IronPulseTheme
import com.fathan.ironpulse.ui.viewmodel.AppTab
import com.fathan.ironpulse.ui.viewmodel.GymViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: GymViewModel by viewModels()
    private var googleSignInInFlight: Boolean = false

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.refreshDiagnostics(
            hasNotificationPermission = hasNotificationPermission(),
            canScheduleExactAlarms = AlarmScheduler.canScheduleExactAlarms(this)
        )
        if (granted) {
            viewModel.updateProfile(notificationsEnabled = true)
        }
    }

    private val audioPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            viewModel.importCustomRingtone(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIncomingIntent(intent)

        setContent {
            val themeUi by viewModel.themeUiFlow.collectAsStateWithLifecycle()
            val isLightTheme = themeUi.prefs.uiTheme == UiTheme.FRUTIGER_AERO &&
                themeUi.timePhase != DayTimePhase.NIGHT
            SideEffect {
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                controller.isAppearanceLightStatusBars = isLightTheme
                controller.isAppearanceLightNavigationBars = isLightTheme
            }
            IronPulseTheme(
                prefs = themeUi.prefs,
                timePhase = themeUi.timePhase
            ) {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                IronPulseRoot(
                    state = state,
                    viewModel = viewModel,
                    onRequestGoogleSignIn = ::launchGoogleSignIn,
                    onRequestNotificationPermission = ::requestNotificationPermissionIfNeeded,
                    onOpenExactAlarmSettings = ::openExactAlarmSettings,
                    onPickCustomRingtone = ::launchAudioFilePicker
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshDateClock()
        viewModel.refreshDiagnostics(
            hasNotificationPermission = hasNotificationPermission(),
            canScheduleExactAlarms = AlarmScheduler.canScheduleExactAlarms(this)
        )
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_OPEN_WORKOUT, false) == true) {
            viewModel.selectTab(AppTab.WORKOUT)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun launchGoogleSignIn() {
        if (googleSignInInFlight) return
        val serverClientId = runCatching { getString(R.string.default_web_client_id) }
            .getOrNull()
            ?.trim()
            .orEmpty()
        if (serverClientId.isEmpty()) {
            viewModel.setAuthError(
                "Konfigurasi Google Sign-In tidak ditemukan (default_web_client_id kosong)."
            )
            return
        }

        val credentialManager = CredentialManager.create(this)
        googleSignInInFlight = true

        lifecycleScope.launch {
            viewModel.setAuthLoading(true)
            viewModel.setAuthError(null)
            try {
                val response = requestGoogleCredential(credentialManager, serverClientId)
                val cred = response.credential
                if (cred is CustomCredential &&
                    cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCred = GoogleIdTokenCredential.createFrom(cred.data)
                    viewModel.signInWithGoogleToken(googleCred.idToken)
                } else {
                    Log.e(TAG, "Unexpected credential type: ${cred.type}")
                    viewModel.setAuthLoading(false)
                    viewModel.setAuthError(
                        "Tipe kredensial tidak dikenali (${cred.type}). Silakan coba akun Google lain."
                    )
                }
            } catch (e: CancellationException) {
                viewModel.setAuthLoading(false)
                throw e
            } catch (e: GetCredentialCancellationException) {
                val msg = e.errorMessage?.toString().orEmpty()
                Log.w(TAG, "Google Sign-In cancelled/dismissed: $msg", e)
                viewModel.setAuthLoading(false)
                val lower = msg.lowercase()
                val isProviderOrSystemFailure = msg.isNotBlank() &&
                    !lower.contains("cancelled by the user") &&
                    !lower.contains("canceled by user") &&
                    !lower.contains("user canceled") &&
                    !lower.contains("user cancelled")
                if (isProviderOrSystemFailure) {
                    viewModel.setAuthError(
                        "Pemilihan akun Google terhenti ($msg). Pastikan koneksi & Google Play Services aktif, lalu coba lagi."
                    )
                }
            } catch (e: NoCredentialException) {
                Log.e(TAG, "No Google credential available: ${e.message}", e)
                viewModel.setAuthLoading(false)
                viewModel.setAuthError(
                    "Akun Google tidak tersedia atau ditolak oleh Play Services. Periksa akun Google di perangkat & koneksi internet."
                )
            } catch (e: GetCredentialProviderConfigurationException) {
                Log.e(TAG, "Credential provider configuration error: ${e.message}", e)
                viewModel.setAuthLoading(false)
                viewModel.setAuthError(
                    "Layanan Credential Manager / Play Services tidak siap pada perangkat ini."
                )
            } catch (e: GetCredentialUnsupportedException) {
                Log.e(TAG, "Credential Manager unsupported: ${e.message}", e)
                viewModel.setAuthLoading(false)
                viewModel.setAuthError(
                    "Perangkat tidak mendukung Credential Manager Google."
                )
            } catch (e: GetCredentialInterruptedException) {
                Log.w(TAG, "Google Sign-In interrupted: ${e.message}", e)
                viewModel.setAuthLoading(false)
                viewModel.setAuthError(
                    "Proses login Google terputus. Silakan ketuk Masuk lagi."
                )
            } catch (e: GoogleIdTokenParsingException) {
                Log.e(TAG, "Invalid Google ID token payload: ${e.message}", e)
                viewModel.setAuthLoading(false)
                viewModel.setAuthError(
                    "Token akun Google gagal diproses (${e.localizedMessage ?: "parsing error"})."
                )
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Google Sign-In credential error (${e.type}): ${e.errorMessage}", e)
                viewModel.setAuthLoading(false)
                viewModel.setAuthError(
                    e.errorMessage?.toString()?.takeIf { it.isNotBlank() }
                        ?: e.localizedMessage
                        ?: "Gagal masuk dengan Google. Pastikan akun Google aktif di perangkat."
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected Google Sign-In failure: ${e.message}", e)
                viewModel.setAuthLoading(false)
                viewModel.setAuthError(
                    e.localizedMessage ?: "Gagal masuk dengan Google. Pastikan akun Google aktif di perangkat."
                )
            } finally {
                googleSignInInFlight = false
            }
        }
    }

    private suspend fun requestGoogleCredential(
        credentialManager: CredentialManager,
        serverClientId: String
    ): GetCredentialResponse {
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId).build()
        val primaryRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()
        return try {
            credentialManager.getCredential(this, primaryRequest)
        } catch (_: NoCredentialException) {
            val fallbackOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()
            val fallbackRequest = GetCredentialRequest.Builder()
                .addCredentialOption(fallbackOption)
                .build()
            credentialManager.getCredential(this, fallbackRequest)
        }
    }

    private fun launchAudioFilePicker() {
        audioPickerLauncher.launch(arrayOf("audio/*"))
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasNotificationPermission()) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.updateProfile(notificationsEnabled = true)
            }
        } else {
            viewModel.updateProfile(notificationsEnabled = true)
        }
    }

    private fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            runCatching {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }
    }

    companion object {
        private const val TAG = "IronPulseAuth"
        const val EXTRA_OPEN_WORKOUT = "extra_open_workout"
    }
}
