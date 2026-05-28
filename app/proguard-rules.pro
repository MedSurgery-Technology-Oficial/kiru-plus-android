# KIRU+ Android — ProGuard / R8 rules
#
# Estas reglas se aplican únicamente al buildType `release` (isMinifyEnabled = true).
# El default de R8 ya hace 95% del trabajo; aquí cubrimos los casos donde la
# reflexión runtime o el dispatcher KSP de Hilt necesita preservar símbolos.

# --- Kotlinx Serialization -------------------------------------------------
# Generadores `$$serializer` y método estático `Companion.serializer()`.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.medsurgery.kiruplus.**$$serializer { *; }
-keepclassmembers class com.medsurgery.kiruplus.** {
    *** Companion;
}
-keepclasseswithmembers class com.medsurgery.kiruplus.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Kotlinx Serialization (@Serializable annotated classes) ---------------
-keep @kotlinx.serialization.Serializable class * { *; }

# --- Supabase / Ktor -------------------------------------------------------
-keep class io.ktor.** { *; }
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.ktor.**
-dontwarn io.github.jan.supabase.**

# --- RevenueCat -----------------------------------------------------------
# El SDK incluye `consumer-rules.pro` en su AAR — no necesitamos keep rules manuales.
# https://www.revenuecat.com/docs/getting-started/installation/android

# --- Sentry ---------------------------------------------------------------
# El SDK incluye `consumer-rules.pro` en su AAR — no necesitamos keep rules manuales.

# --- Hilt / Dagger --------------------------------------------------------
-keep class dagger.hilt.android.internal.managers.** { *; }
# Hilt necesita conocer todos los ViewModels para resolver el factory
# generado (`HiltViewModelFactory`).
-keep class * extends androidx.lifecycle.ViewModel { *; }
# `@HiltViewModel` annotation runtime processing
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# --- OkHttp ---------------------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**

# --- Compose --------------------------------------------------------------
# Compose ya trae sus propias consumer-rules; R8 maneja la mayoría automáticamente.

# --- Coil (image loading) -------------------------------------------------
# Coil ya trae sus propias reglas con consumer-rules.pro, pero suprimimos
# warnings de transitivas que no usamos.
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

# --- Coroutines -----------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# --- AndroidX Browser (Custom Tabs) ---------------------------------------
# CustomTabsIntent.Builder + service connection runtime — el SDK ya está
# minify-safe pero dejamos el namespace por si la app expone CCT callbacks.
-keep class androidx.browser.customtabs.** { *; }
