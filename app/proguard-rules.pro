# IronPulse Native Android - R8 / ProGuard Keep Rules

# Preserve line numbers and source file names for crash diagnostics
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature,InnerClasses,EnclosingMethod,Exceptions,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations

# Application & Manifest Components
-keep class com.fathan.ironpulse.IronPulseApp { *; }
-keep class com.fathan.ironpulse.MainActivity { *; }
-keep class com.fathan.ironpulse.alarm.** { *; }
-keep class com.fathan.ironpulse.timer.RestTimerService { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep domain models and enums intact
-keep class com.fathan.ironpulse.model.** { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Credential Manager, ServiceLoader providers & Google Identity (Release R8 Full Mode safety)
-keep class androidx.credentials.** { *; }
-keep class androidx.credentials.playservices.** { *; }
-keep class * implements androidx.credentials.CredentialProvider { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }
-dontwarn androidx.credentials.**

# Firebase, Auth, Firestore, reCAPTCHA & Google Play Services
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.google.android.recaptcha.** { *; }
-keep class com.google.android.play.core.** { *; }
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn sun.misc.Unsafe
-dontwarn com.google.android.play.core.**

# Kotlin Coroutines & Play Services Tasks bridge
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlinx.coroutines.tasks.** { *; }
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# R8 Release APK optimization: strip debug/verbose logs & formatting overhead
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
