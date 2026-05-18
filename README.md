# KIRU+ Android

App Android nativa de **KIRU+** (Kotlin + Jetpack Compose + Material 3).

> **Estado:** E0 — scaffolding inicial. NO listo para release. Ver [`docs/ANDROID_PORT_PLAN.md`](docs/ANDROID_PORT_PLAN.md) para el roadmap completo.
>
> Backend: **comparte el mismo Supabase del repo iOS** (proyecto `tttxmupjteqpljtfgmgo`). Auth, tablas, RLS, storage y Edge Functions son fuente única de verdad para ambas plataformas.

## Requisitos

- **Android Studio** Iguana (2023.2.1) o superior
- **JDK 17** (incluido en Android Studio)
- **Android SDK 35** instalado (`compileSdk` y `targetSdk`)
- macOS / Linux / Windows

## Quick start

```bash
git clone https://github.com/MedSurgery-Technology-Oficial/kiru-plus-android.git
cd kiru-plus-android

# Copia el ejemplo y rellena tus claves locales (Supabase/RevenueCat/Sentry).
cp local.properties.example local.properties
$EDITOR local.properties

# Build debug
./gradlew assembleDebug

# Instalar en emulador/device conectado
./gradlew installDebug

# Tests unitarios
./gradlew test
```

> Si tu carpeta local del SDK Android no es `~/Library/Android/sdk`, ajusta `sdk.dir` en `local.properties`.
> Si no tienes Gradle CLI, abre el proyecto en Android Studio: descarga el wrapper automáticamente.

## Configuración local

Crea `local.properties` en la raíz (NO commitear — está en `.gitignore`):

```properties
sdk.dir=/Users/<tu-usuario>/Library/Android/sdk

# Supabase (mismo proyecto que iOS)
SUPABASE_URL=https://tttxmupjteqpljtfgmgo.supabase.co
SUPABASE_ANON_KEY=sb_publishable_xxxxxxxxxxxx

# RevenueCat (Android key — distinta de la iOS appl_xxx)
REVENUECAT_API_KEY=goog_xxxxxxxxxxxx

# Sentry
SENTRY_DSN=https://xxxx@sentry.io/xxxx
```

Las keys se inyectan via `BuildConfig` durante la compilación. **Nunca** commitear `local.properties` ni hardcodear secretos en código fuente.

## Estructura

```
.
├── docs/                   # plan maestro, checklist GP, matriz paridad
├── gradle/                 # version catalog + wrapper
├── app/                    # módulo principal
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/com/medsurgery/kiruplus/
│       │   ├── KiruApp.kt
│       │   ├── MainActivity.kt
│       │   ├── app/          # composable root + navegación + DI
│       │   ├── core/         # designsystem, network, auth, premium, legal, ui
│       │   ├── feature/      # auth, onboarding, home, profile, paywall, legal, store, kapibaya
│       │   ├── domain/       # modelos puros + interfaces repos
│       │   └── data/         # impls repos + DTOs Supabase
│       └── res/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── local.properties.example
└── README.md
```

## Stack

- **Kotlin 2.1** + **Jetpack Compose** + **Material 3**
- **Navigation Compose 2.8+** (typed `@Serializable` routes)
- **Hilt** (DI)
- **supabase-kt v3** (Auth + Postgrest + Storage + Realtime + Functions) — comparte el mismo proyecto Supabase que iOS
- **Ktor 3** (runtime de supabase-kt)
- **RevenueCat 8** (Google Play Billing wrapped) — webhook unificado con iOS contra `iap_entitlements`
- **Sentry** (crash reporting)
- **Coil** (imágenes)
- **DataStore** (preferencias + encrypted)
- **Coroutines + Flow** (concurrencia)

## Convenciones

- **Idioma del código:** inglés (identificadores y comments)
- **Idioma de docs:** español
- **Idioma de UI:** español (default `values-es`), inglés (`values`) como fallback
- **Linter:** ktlint
- **Análisis estático:** detekt
- **Nombres de pantallas:** `XxxScreen` (Composable), `XxxViewModel` (estado)

## Decisiones técnicas

Ver [`docs/ANDROID_PORT_PLAN.md`](docs/ANDROID_PORT_PLAN.md) sección 2 (decisión stack) y sección 10 (decisiones registradas).

## Cumplimiento Google Play

Ver [`docs/GOOGLE_PLAY_CHECKLIST.md`](docs/GOOGLE_PLAY_CHECKLIST.md).

## Paridad con iOS

Ver [`docs/IOS_PARITY_MATRIX.md`](docs/IOS_PARITY_MATRIX.md). Repo iOS de referencia: [`MedSurgery-Technology-Oficial/kiru-plus-main`](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main).

## Backend compartido

Este proyecto **no contiene** backend propio. Comparte 100% el stack Supabase del repo iOS:

- 60+ tablas con RLS (`profiles`, `iap_entitlements`, `kapibaya_conversation_turns`, `surgical_logs`, `account_deletion_requests`, `pearls`, `content_items`, `store_products`, etc.)
- 16 Edge Functions activas (`ask_kapibaya`, `kapibaya-tts`, `revenuecat_webhook`, `medsurgery-store`, `process_account_deletions`, `process_data_export`, etc.)
- Buckets de Storage (`perlas`, `podcasts`, `gdpr-exports`, licenses)

Cuando en el futuro se decida versionar formalmente migraciones SQL + Edge Functions + RLS policies como IaC, se hará en un repo backend separado. Hoy no es necesario.
