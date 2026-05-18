# KIRU+ Android — ProGuard / R8 rules

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.medsurgery.kiruplus.**$$serializer { *; }
-keepclassmembers class com.medsurgery.kiruplus.** {
    *** Companion;
}
-keepclasseswithmembers class com.medsurgery.kiruplus.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Supabase / Ktor
-keep class io.ktor.** { *; }
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.ktor.**

# RevenueCat
-keep class com.revenuecat.purchases.** { *; }
-keep class com.revenuecat.purchases.kmp.** { *; }

# Sentry
-keep class io.sentry.** { *; }

# Hilt
-keep class dagger.hilt.android.internal.managers.** { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Compose
-keep class androidx.compose.** { *; }
