# IronPulse — Gym Reminder & Pelacak Latihan

**IronPulse** adalah aplikasi Android native berbasis **Kotlin** dan **Jetpack Compose (Material Design 3)** untuk mengatur jadwal gym mingguan, menjalankan alarm pengingat presisi di latar belakang, mencatat beban/set/repetisi maupun durasi latihan secara real-time, memantau riwayat kalender sesi, dan menjaga ritme istirahat antar set.

> **Versi saat ini:** `0.0.1-alpha` (`versionCode = 2`)  
> **Application ID:** `com.aistudio.ironpulse.k7m2xp` | **Namespace:** `com.fathan.ironpulse`  
> **Kompatibilitas:** Android 7.0 Nougat (`minSdk = 24`) hingga Android 16 (`compileSdk = 36`, `targetSdk = 36`)  
> **Kredit:** `scripter`: **Fathan, and Artificial intelligence** · `concept`: **fathan.**

---

## Daftar Isi

- [Fitur Utama](#fitur-utama)
- [Sistem 6 Tema UI (Standar & Eksklusif)](#sistem-6-tema-ui-standar--eksklusif)
- [Tech Stack & Dependensi](#tech-stack--dependensi)
- [Struktur Proyek](#struktur-proyek)
- [Izin Android & Komponen Sistem](#izin-android--komponen-sistem)
- [Autentikasi, Keamanan & Sinkronisasi Cloud](#autentikasi-keamanan--sinkronisasi-cloud)
- [Build, Pengujian & CI/CD (GitHub Actions)](#build-pengujian--cicd-github-actions)

---

## Fitur Utama

### 1. Beranda (`Today`) & Maskot Interaktif Pulse
- **Jam Digital Real-Time & Fase Waktu:** Menampilkan jam langsung (`LiveFluidClockSlot`) dengan opsi format `12h` atau `24h` serta sapaan otomatis berdasarkan fase waktu (*Pagi / Morning*, *Siang / Afternoon*, *Malam / Night*).
- **Ringkasan Hari Ini:** Menampilkan status *Gym Day* atau *Rest Day*, judul fokus latihan hari berjalan, jadwal alarm berikutnya, serta pratinjau daftar gerakan hari ini lengkap dengan tombol cepat mulai latihan.
- **Quick Rest Timer:** Preset timer istirahat cepat langsung dari beranda.
- **Maskot Interaktif Pulse:** Memiliki **9 status mood (`MascotMood`)** dan **27 pose berbasis fase waktu** (Pagi, Siang, Malam), lengkap dengan dialog kontekstual (`MascotDialogues`) serta dukungan suara/ucapan opsional melalui `MascotTtsSpeaker` dan efek *chirp* audio.

### 2. Mode Latihan (`Workout`) — Standard Cockpit & Focus HUD
- **Dua Tampilan Latihan:**
  - **Standard Cockpit:** Mendukung mode tampilan *Fokus 1 Gerakan* (navigasi per gerakan) maupun *Semua Gerakan* sekaligus dalam satu daftar.
  - **Focus HUD (`GymFocusHudStage`):** Tampilan khusus penggunaan satu tangan di gym dengan tombol stepper raksasa (`-2.5 / +2.5 kg`, `-1 / +1 rep`, `-10s / +10s`), ring hitung mundur istirahat berukuran besar, dan *sticky floating rest bar*.
- **Dua Mode Pencatatan Gerakan:**
  - **`REPS`:** Pencatatan target set, repetisi, serta beban (`kg`) atau mode *Bodyweight / Tanpa Beban*.
  - **`TIMER`:** Mendukung mode **Countdown** (hitung mundur) dan **Count-Up** (stopwatch).
- **Proteksi & Kenyamanan Sesi:**
  - Modal pengingat jeda antar set (*Set Cooldown Protection*) untuk mencegah pencatatan beruntun yang tidak disengaja.
  - Opsi **Keep Screen On** agar layar tidak mati saat sesi latihan aktif.
  - Kartu ringkasan selesai latihan (`WorkoutSessionSummaryCard`) setelah seluruh gerakan rampung.
- **Rest Timer Background (`RestTimerService`):** Berjalan sebagai *Android Foreground Service* (`specialUse`) sehingga hitung mundur istirahat tetap akurat dan tampil di notifikasi saat aplikasi diminimalkan atau layar terkunci.

### 3. Jadwal Mingguan (`Schedule`) & Program Switcher
- **Jadwal 7 Hari (Senin–Minggu):** Atur masing-masing hari sebagai *Gym Day* atau *Rest Day*, edit judul fokus latihan (misalnya *Push*, *Pull*, *Legs*), nyalakan/matikan alarm harian, atur jam alarm, dan pilih ringtone per hari.
- **Program Switcher (`ScheduleProgramSwitcher`):**
  - **3 Templat Program Resmi Bawaan:**
    1. **PPL × Upper-Lower** *(5 hari — Featured)*
    2. **Push-Pull-Legs** *(6 hari)*
    3. **Upper-Lower** *(4 hari)*
  - **Profil Jadwal Kustom Tersimpan:** Simpan konfigurasi jadwal aktif sebagai profil kustom, terapkan ulang kapan saja, ubah nama, atau hapus—lengkap dengan opsi mempertahankan jam alarm saat beralih program.

### 4. Pustaka 123 Gerakan & Perencana Harian (`Exercises`)
- **Pustaka Bawaan Lengkap (`GymPresets`):** Berisi **123 gerakan bawaan** dengan metadata lengkap (nama ID/EN, kelompok otot, jenis alat, default set/reps/beban/mode tracking).
- **Filter Multi-Kriteria:**
  - **7 Kelompok Otot (`MuscleGroup`):** Dada (*Chest*), Punggung (*Back*), Bahu (*Shoulders*), Kaki (*Legs*), Lengan (*Arms*), Core, dan Kardio (*Cardio*).
  - **8 Kategori Alat (`EquipmentBucket`):** Semua Alat, Tanpa Alat / Bodyweight, Barbel / EZ-Bar, Dumbbell, Kettlebell, Kabel, Mesin, dan Kardio.
- **Perencana Harian Fleksibel:** Tambah banyak gerakan sekaligus (*multi-select batch add*), buat gerakan kustom baru, edit target set/reps/durasi/beban, ubah urutan latihan, atau hapus gerakan (mendukung hingga **30 gerakan per hari**).

### 5. Riwayat Latihan & Kalender Bulanan (`History`)
- **3 Metrik KPI:** Total Sesi Selesai, Streak Mingguan, dan Total Volume Bulanan.
- **Kalender Bulanan Interaktif:** Menampilkan penanda visual untuk hari latihan selesai penuh (*Full*), sebagian (*Partial*), dan hari berjalan (*Today*), disertai rincian log latihan setiap tanggal yang dipilih.

### 6. Alarm Background & Mesin Audio (`Alarm` & `Audio`)
- **Alarm Presisi Latar Belakang (`AlarmScheduler` & `GymAlarmReceiver`):** Menggunakan `AlarmManager` untuk memicu pengingat sesuai jadwal hari aktif.
- **Auto-Reschedule (`BootCompletedReceiver`):** Menjadwalkan ulang semua alarm otomatis saat perangkat restart (`BOOT_COMPLETED`), perubahan waktu sistem (`TIME_SET`), atau perpindahan zona waktu (`TIMEZONE_CHANGED`).
- **Layar Alarm Lock-Screen (`AlarmRingActivity`):** Tampil penuh di atas layar terkunci dan menyalakan layar dengan aksi **Snooze** dan **Dismiss**.
- **5 Ringtone Sintesis Bawaan (`NativeAudioEngine`):** Dihasilkan langsung menggunakan sintesis PCM `AudioTrack` tanpa file MP3 statis yang membebani APK:
  1. *Athletic Bell*
  2. *Arena Horn*
  3. *Digital Pulse*
  4. *Kettlebell Chime*
  5. *Sprint Interval*
- **Ringtone Kustom:** Mendukung impor file audio dari penyimpanan perangkat (mode lokal maupun sinkronisasi cloud), pengaturan volume alarm, durasi snooze (`1–60` menit), dan tombol **Tes Alarm**.

### 7. Pengaturan (`Settings`), Performa, Dwibahasa & Tutorial
- **Hub 6 Kategori:** *Profil*, *Tema*, *Konfigurasi*, *Alarm*, *Sistem*, dan *About* yang membuka sub-halaman penuh dengan dukungan tombol *Back* sistem.
- **Kustomisasi Performa:** Preset **Balanced**, **Battery**, dan **Ultra**, serta toggle individual untuk mematikan animasi, menonaktifkan cahaya *ambient glow*, atau membuat maskot statis.
- **Simulasi / Mode Fase Waktu (`DayTimeMode`):** Pilih *Auto* (mengikuti jam perangkat) atau kunci ke *Morning*, *Afternoon*, maupun *Night*.
- **Dwibahasa & Preferensi:** Beralih instan antara **Bahasa Indonesia (`ID`)** dan **English (`EN`)**, format jam `12h/24h`, haptic feedback (`HapticFeedbackHelper`), suara maskot, serta tombol putar ulang **Tutorial Interaktif Spotlight** (`PulseMascotTutorialOverlay`).

---

## Sistem 6 Tema UI (Standar & Eksklusif)

IronPulse memisahkan tema antarmuka menjadi dua kelompok besar dengan karakteristik tipografi (`ThemeTypography`), ikonografi (`ThemeIcons`), emblem SVG (`ThemeLogos`), dan material permukaan tersendiri:

| Kategori | Nama Tema | Karakteristik Visual & Aturan Warna |
| :--- | :--- | :--- |
| **Tema Standar** | **Material You (M3)** | Gaya modern Material 3 dengan sudut membulat dinamis, permukaan bersih, dan dukungan penuh kustomisasi warna. |
| **Tema Standar** | **Carbon Forge** | Estetika industrial gelap bernuansa serat karbon tegas, kontras tajam, dan aksen bertenaga. |
| **Tema Standar** | **AMOLED Stealth** | Hitam pekat hemat layar OLED dengan border minimalis dan kontras tinggi. |
| **Tema Eksklusif** | **Ubuntu Yaru** | Terinspirasi desktop Ubuntu Yaru; palet terkunci penuh pada **Jingga Yaru (`#E95420`)** dan **Aubergine (`#2E182A`)** dengan tipografi rapi. |
| **Tema Eksklusif** | **Frutiger Aero (Y2K)** | Material kaca **wet-glossy** transparan di seluruh kartu, tombol, dan pill; berpadu gradasi biru langit, aqua, dan nuansa senja Y2K. |
| **Tema Eksklusif** | **XMaterial You** | Permukaan **obsidian gelap bersih** dengan border halus, sudut sangat membulat **`28.dp`** di seluruh kartu, dan aksen metalik adaptif fase waktu (*Morning Gold*, *Afternoon Platinum*, *Night Moonstone*). |

> Pada **Tema Standar**, pengguna dapat memilih dari **9 preset warna aksen**, **9 preset warna permukaan (tint)**, atau meracik warna bebas melalui **`VisualColorPickerDialog`**. Pada **Tema Eksklusif**, palet warna dikunci untuk menjaga keutuhan identitas visual masing-masing tema.

---

## Tech Stack & Dependensi

Konfigurasi build dan pustaka utama mengacu pada `gradle/libs.versions.toml` dan `app/build.gradle.kts`:

- **Bahasa & Toolchain:** Kotlin `2.2.10`, Android Gradle Plugin `9.1.1`, Gradle `9.3.1`, Java `21`
- **UI & Navigasi:**
  - Jetpack Compose (`Compose BOM 2025.05.00`) — UI, Graphics, Foundation, Material 3, Material Icons Extended
  - Navigation Compose `2.8.9`
  - Core SplashScreen `1.2.0`
  - Font lokal terpaket di `app/src/main/res/font/`
- **Arsitektur & Asinkronus:**
  - MVVM (`GymViewModel` + `StateFlow` + `Lifecycle Runtime Compose 2.8.7`)
  - Kotlinx Coroutines (`1.9.0` — `android` & `play-services`)
  - AndroidX ProfileInstaller `1.4.1`
- **Backend, Auth & Penyimpanan:**
  - Firebase BoM `34.1.0` (`firebase-auth`, `firebase-firestore`)
  - Jetpack Credential Manager `1.5.0` & Google Identity (`googleid 1.1.1`) untuk Google Sign-In
  - Penyimpanan lokal terstruktur (`LocalGymCacheStore`, `PreferencesStore`, `ScheduleProfileStore`)
- **Pengujian:**
  - JUnit `4.13.2`, Robolectric `4.16.1`, AndroidX Test Core `1.6.1`, Compose UI Test JUnit4
  - Pengujian rules Firestore (`firestore.test.js`)

---

## Struktur Proyek

```text
.
├── .github/workflows/
│   └── android-apk.yml                 # CI/CD GitHub Actions (Test, Build Debug/Release APK, Release tag)
├── app/
│   ├── build.gradle.kts                # Konfigurasi modul app (SDK 24..36, ProGuard release, signing)
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml     # Deklarasi permission, activity, receiver, & foreground service
│       │   ├── java/com/fathan/ironpulse/
│       │   │   ├── IronPulseApp.kt     # Application class & inisialisasi global
│       │   │   ├── MainActivity.kt     # Host Compose, SplashScreen, & Credential Manager host
│       │   │   ├── alarm/
│       │   │   │   ├── AlarmRingActivity.kt
│       │   │   │   ├── AlarmScheduler.kt
│       │   │   │   ├── BootCompletedReceiver.kt
│       │   │   │   └── GymAlarmReceiver.kt
│       │   │   ├── audio/
│       │   │   │   ├── MascotTtsSpeaker.kt
│       │   │   │   └── NativeAudioEngine.kt
│       │   │   ├── data/
│       │   │   │   ├── FirestoreGymRepository.kt
│       │   │   │   ├── GymPresets.kt
│       │   │   │   ├── LocalGymCacheStore.kt
│       │   │   │   ├── MascotDialogues.kt
│       │   │   │   ├── PreferencesStore.kt
│       │   │   │   ├── ProgramTemplatePresets.kt
│       │   │   │   └── ScheduleProfileStore.kt
│       │   │   ├── lib/
│       │   │   │   └── HapticFeedbackHelper.kt
│       │   │   ├── model/
│       │   │   │   ├── GymModels.kt
│       │   │   │   └── ScheduleProgramModels.kt
│       │   │   ├── timer/
│       │   │   │   └── RestTimerService.kt
│       │   │   └── ui/
│       │   │       ├── IronPulseRoot.kt
│       │   │       ├── components/
│       │   │       │   ├── ChromeAndClock.kt
│       │   │       │   ├── LaunchAndModals.kt
│       │   │       │   ├── MascotComponents.kt
│       │   │       │   ├── Motion.kt
│       │   │       │   ├── PulseMascotTutorialOverlay.kt
│       │   │       │   ├── ScheduleProgramSwitcher.kt
│       │   │       │   ├── ThemeIcons.kt
│       │   │       │   ├── ThemeLogos.kt
│       │   │       │   └── VisualColorPickerDialog.kt
│       │   │       ├── screens/
│       │   │       │   ├── AuthScreen.kt
│       │   │       │   ├── ExercisesScreen.kt
│       │   │       │   ├── GymFocusHudStage.kt
│       │   │       │   ├── HistoryScreen.kt
│       │   │       │   ├── ScheduleScreen.kt
│       │   │       │   ├── SettingsScreen.kt
│       │   │       │   ├── TodayScreen.kt
│       │   │       │   └── WorkoutScreen.kt
│       │   │       ├── theme/
│       │   │       │   ├── MaterialYouPalette.kt
│       │   │       │   ├── Theme.kt
│       │   │       │   └── ThemeTypography.kt
│       │   │       └── viewmodel/
│       │   │           └── GymViewModel.kt
│       │   └── res/                    # Drawables, bundled fonts (.ttf), raw, mipmap, strings, values
│       └── test/java/com/fathan/ironpulse/
│           └── StartupSmokeTest.kt     # Smoke test startup & Robolectric suite
├── gradle/
│   └── libs.versions.toml              # Version catalog terpusat
├── firebase.json                       # Konfigurasi Firebase project & emulator
├── firebase-blueprint.json             # Blueprint skema koleksi Firestore
├── firestore.rules                     # Aturan keamanan zero-trust Cloud Firestore
├── firestore.test.js                   # Unit test validasi firestore.rules
├── build.gradle.kts                    # Konfigurasi Gradle root
└── settings.gradle.kts                 # Definisi repository & modul :app
```

---

## Izin Android & Komponen Sistem

Seluruh izin yang dideklarasikan pada `app/src/main/AndroidManifest.xml` digunakan secara spesifik oleh fitur berikut:

| Permission | Fungsi |
| :--- | :--- |
| `INTERNET`, `ACCESS_NETWORK_STATE` | Autentikasi Google Sign-In & sinkronisasi jadwal/log/ringtone ke Cloud Firestore. |
| `POST_NOTIFICATIONS` | Menampilkan notifikasi pengingat alarm gym serta kontrol notifikasi timer istirahat. |
| `SCHEDULE_EXACT_ALARM` | Menjadwalkan alarm gym harian secara presisi melalui `AlarmManager`. |
| `RECEIVE_BOOT_COMPLETED` | Menjadwalkan ulang seluruh alarm aktif setelah perangkat menyala kembali atau saat jam/zona waktu berubah. |
| `WAKE_LOCK` | Menjaga CPU tetap aktif sesaat ketika alarm berbunyi atau timer istirahat diproses. |
| `VIBRATE` | Memberikan getaran alarm dan umpan balik haptik saat interaksi latihan. |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE` | Menjalankan `RestTimerService` agar hitung mundur istirahat antar set terus berjalan stabil di latar belakang. |
| `USE_FULL_SCREEN_INTENT` | Menampilkan `AlarmRingActivity` secara penuh di atas layar kunci saat alarm berbunyi. |

---

## Autentikasi, Keamanan & Sinkronisasi Cloud

- **Google Sign-In Modern:** Menggunakan `androidx.credentials.CredentialManager` dan `com.google.android.libraries.identity.googleid` yang terhubung ke Firebase Authentication (`AuthScreen`), lengkap dengan alur pengisian *nickname* pengguna baru.
- **Offline-First Cache:** Jadwal, log latihan, preferensi tema, dan profil jadwal tersimpan di cache lokal (`LocalGymCacheStore`, `PreferencesStore`, `ScheduleProfileStore`) sehingga aplikasi tetap responsif saat koneksi terputus dan disinkronkan saat online.
- **Aturan Keamanan Zero-Trust (`firestore.rules`):**
  - Default-deny (`allow read, write: if false`) untuk seluruh path tak terdaftar.
  - Akses baca/tulis dibatasi ketat hanya untuk pemilik akun terverifikasi (`request.auth.uid == userId && request.auth.token.email_verified == true`).
  - Validasi skema, tipe data, panjang string, batas item (maks. 30 gerakan per hari/log, batas ukuran ringtone kustom), dan validasi timestamp (`createdAt` / `updatedAt`) diterapkan di sisi server Firestore pada koleksi `users`, `schedules`, `workoutLogs`, dan `ringtones`.

---

## Build, Pengujian & CI/CD (GitHub Actions)

### Prasyarat Build Lokal
- **JDK 21**
- **Android SDK 36** (`platforms;android-36`, `build-tools;36.0.0`)
- **Gradle 9.3.1** (di lingkungan AI Studio / sistem yang memakai `gradle` terpasang global, gunakan perintah `gradle`; jangan membuat file `local.properties` bila menggunakan `ANDROID_SDK_ROOT`)
- File `app/google-services.json` dari proyek Firebase Anda (untuk mengaktifkan Firebase Auth & Firestore secara penuh pada perangkat)

### Perintah Penting

```bash
# Menjalankan seluruh unit test & Robolectric test
gradle :app:testDebugUnitTest --no-daemon --console=plain

# Membangun Debug APK
gradle :app:assembleDebug --no-daemon --console=plain

# Membangun Release APK (minify + shrinkResources aktif)
gradle :app:assembleRelease --no-daemon --console=plain
```

Output APK dihasilkan di:
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release.apk`

### Pipeline CI/CD (`.github/workflows/android-apk.yml`)
Workflow GitHub Actions otomatis berjalan pada push ke branch `main`, push tag `v*`, atau pemicu manual `workflow_dispatch`:
1. Menjalankan `gradle :app:testDebugUnitTest` (termasuk cache Robolectric).
2. Membangun **Debug APK** dan **Release APK** serta menyimpannya sebagai artifact (`ironpulse-debug-apk`, `ironpulse-release-apk`, `unit-test-reports`).
3. Secara otomatis membuat **GitHub Release** berisi kedua file APK setiap kali ada push tag versi `v*` (otomatis ditandai *prerelease* jika tag mengandung `alpha`, `beta`, atau `rc`).

**GitHub Repository Secrets (Opsional namun direkomendasikan untuk APK produksi/login aktif):**
- `GOOGLE_SERVICES_JSON`: Isi JSON (atau Base64) dari `app/google-services.json` proyek Firebase Anda.
- `FIRESTORE_DATABASE_ID`: ID database Firestore tujuan (default `(default)` jika kosong).
- `DEBUG_KEYSTORE_BASE64`: Keystore penandatanganan dalam format Base64 agar SHA-1 konsisten dengan OAuth Google Sign-In di Firebase Console.

> Jika `app/google-services.json` dan secret tidak tersedia saat CI berjalan, workflow tetap menyediakan konfigurasi fallback sementara agar kompilasi APK dan unit test tetap terverifikasi, namun fitur Google Sign-In/Firestore pada APK tersebut memerlukan konfigurasi Firebase asli.
