# KIRU+ Android Port — Plan Maestro

> **Estado original:** Draft 1 · 2026-05-17 · Build iOS de referencia: 17 (en App Review)
> **Update 2026-05-18:** v0.1.0 base implementada (**11 commits**, **47 unit tests** PASS). iOS Build 17 **rechazado** → Build 18 pushed con fixes. URLs legales corregidas y alineadas iOS+Android.
>
> **Documentos vivos (consultar antes que este Plan Maestro):**
> - [`IOS_PARITY_MATRIX.md`](./IOS_PARITY_MATRIX.md) — estado actualizado feature por feature (single source of truth de progreso).
> - [`ROADMAP_V2.md`](./ROADMAP_V2.md) — plan forward priorizado (Sprints A→H con estimaciones).
> - [`GOOGLE_PLAY_CHECKLIST.md`](./GOOGLE_PLAY_CHECKLIST.md) — checklist Play Console pre-submit.
> - [`PRIVACY_AND_DATA_SAFETY.md`](./PRIVACY_AND_DATA_SAFETY.md) — Data Safety form draft (con BLOQUEANTE follow-up resuelto).
>
> Las secciones 1-N originales de este Plan Maestro permanecen como **referencia histórica** del diseño inicial. Para el estado actual y siguientes pasos, ir a los docs vivos arriba.
>
> **Autor técnico:** Arquitectura móvil KIRU+
> **Objetivo:** Portar la app iOS KIRU+ a Android conservando paridad funcional, reutilizando 100% del backend (Supabase + Render + Node + Python ML), y minimizando riesgo en Google Play Console.

---

## 0. Resumen ejecutivo

KIRU+ es una app médica/educativa iOS SwiftUI con arquitectura modular madura, backend Supabase consolidado (60+ tablas, 16 Edge Functions activas) y stack de monetización dual (RevenueCat para suscripciones, Stripe para productos físicos). El feature insignia es **Dr. Kapibaya**, un tutor de voz médico con TTS/STT/VAD/barge-in que ya delega TTS al backend (`kapibaya-tts` Edge Function), lo que hace el port a Android técnicamente viable sin reentrenar nada.

**Decisión técnica:** **Kotlin 2.1 + Jetpack Compose + Material 3 + Hilt** como stack único Android, en su propio repositorio (`MedSurgery-Technology-Oficial/kiru-plus-android`), reutilizando el backend Supabase del repo iOS sin duplicar nada. Sin cross-platform framework (React Native, Flutter, KMP) en esta primera entrega — la complejidad del feature de voz, las exigencias de Google Play Health Apps, y la necesidad de paridad fina con SwiftUI sugieren nativo puro como ruta de menor riesgo.

**Ruta crítica:** auth → disclaimer médico → home → premium gating → store WebView checkout → Kapibaya voice (último, por complejidad de audio Android). Estimación realista: **3–4 meses a producción** con 1 dev full-time o **6–8 semanas** con 2 devs senior.

---

## 1. Auditoría iOS (FASE 1)

### 1.1 Arquitectura y módulos

| Capa | Ubicación | Notas |
|------|-----------|-------|
| Entry point | [App/KIRUPlusApp.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/App/KIRUPlusApp.swift) | `@main`, AppObjects DI container, state machine `disclaimer → onboarding → main` |
| Navegación | [Core/Navigation/AppRouter.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Core/Navigation/AppRouter.swift), [Core/Navigation/AppRoute.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Core/Navigation/AppRoute.swift) | 2499 líneas en router, **260 rutas** en enum, navegación typed (no NavigationPath) para evitar crashes iOS 26. Per-tab `[AppRoute]` arrays. |
| Design system | `Core/DesignSystem/` | Tokens `KiruTypography`, `KiruColor`; dark mode forzado donde aplica |
| Features | `Features/` — 40+ subcarpetas | Academy, Arena_KIRU_Plus, Calculators, K-Cortex, K-Pharma, Kapibaya, KiruPardy, KiruSimulator, KiruVision, Logbook, NOM004, Onboarding, Paywall, Profile, Store, etc. |
| Networking | `Core/Networking/SupabaseManager.swift` | Singleton `@MainActor`, credenciales vía Info.plist desde xcconfig |
| Telemetría | `Telemetry/` | Sentry + LogRocket + analytics_events table |
| Backend interno | `api/` (Node), `backend/` (Node servicios), `kcortex_engine/` (Python ML) | Desplegados en Render |

iOS deployment target: **iOS 18.0**. Swift 5/6 mixto. Build 17 enviado a App Review 2026-05-17.

### 1.2 Backend Supabase confirmado (snapshot 2026-05-17)

**Project URL:** `https://tttxmupjteqpljtfgmgo.supabase.co`

**Tablas críticas con datos en producción:**
- `kapibaya_conversation_turns` (174 filas — memoria conversacional activa, K6A CERRADO)
- `pearls` (200 filas — perlas clínicas)
- `content_items` (4515 filas — biblioteca educativa)
- `analytics_events` (34 filas — event bus telemetría)
- `profiles` (2 filas — usuarios actuales, RLS activo)
- `patients` (2 filas — paciente del consent flow)
- `clinical_nom004_notes` (1 fila — apartado 8 NOM-004)
- `kiru_control_demo_codes` (1 fila — hashed demo codes)
- `patient_ai_processing_consents` (1 fila — consent K-CORTEX)

**Tablas con propósito y comentarios autoritativos** (extracto):
- `iap_entitlements` — *"Fuente de verdad de la suscripción Pro. Escribe SOLO Edge Function verify_iap_receipt o webhook RevenueCat."*
- `surgical_logs` — *"escritura vía Edge Function ingest_surgical_log"*
- `account_deletion_requests` — *"GDPR Art. 17 / LFPDPPP Art. 23 · solicitudes con 48h de gracia"*
- `data_export_requests` — *"GDPR/LFPDPPP Art. 15 · payload en storage gdpr-exports"*
- `kcortex_jobs` — *"job ledger; hemisferio izquierdo (Render) escribe results via service_role"*
- `wellbeing_alerts` — *"NEVER store full conversations or PHI. summary_redacted ≤ 500 chars"*
- `institution_audit_log` — *"ip_hash y user_agent_hash MUST be SHA-256"*

**Las 16 Edge Functions activas (todas reutilizables sin cambios):**

| Función | JWT | Propósito |
|---------|-----|-----------|
| `ask_kapibaya` | sí | Tutor IA principal (Gemini) |
| `ask_kapibaya_stream` | no | Streaming responses |
| `ask_kapibaya_debug` | no | Debug endpoint |
| `kapibaya-tts` | sí | TTS cloud (voz Dr. Kapibaya) |
| `kapibaya-weekly-drops-push` | sí | Push semanal de contenido |
| `verify_iap_receipt` | sí | Verificación IAP iOS (lado App Store) |
| `revenuecat_webhook` | no | Webhook RevenueCat ↔ `iap_entitlements` |
| `medsurgery-store` | no | Proxy tienda física (Stripe) |
| `mux-webhook` | no | Webhook video (MUX) |
| `video-catalog` | no | Catálogo video |
| `issue_license_signed_url` | sí | Signed URL para upload cédula |
| `process_data_export` | sí | GDPR data export |
| `process_account_deletions` | no | GDPR account deletion (48h grace) |
| `weekly-safety-report` | no | Reporte semanal NOM-004 |
| `_backfill_embeddings` | no | Backfill embeddings (mantenimiento) |
| `kiru-control-demo-redeem` | no | Redime demo codes hasheados |

### 1.3 Integraciones críticas iOS

| Sistema | Archivo iOS | Conexión Android |
|---------|-------------|------------------|
| Supabase Auth/DB/Storage | [Core/Networking/SupabaseManager.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Core/Networking/SupabaseManager.swift) | `supabase-kt` v3+ (Auth/Postgrest/Storage/Realtime/Functions) |
| RevenueCat | [Data/Services/StudyContent/RevenueCatManager.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Data/Services/StudyContent/RevenueCatManager.swift) | `com.revenuecat.purchases:purchases:8.x` (Google Play Billing) |
| Paywall | [Features/Paywall/UnifiedPaywallSheet.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Features/Paywall/UnifiedPaywallSheet.swift), [Features/Paywall/PaywallView.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Features/Paywall/PaywallView.swift), [Features/Paywall/RevenueCatPaywallView.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Features/Paywall/RevenueCatPaywallView.swift) | RevenueCat Paywalls v2 (Compose) + fallback custom |
| Stripe checkout (productos físicos) | [Store/StripePaymentService.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Store/StripePaymentService.swift), [Store/SafariCheckoutView.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Store/SafariCheckoutView.swift), [Store/WebCheckoutManager.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Store/WebCheckoutManager.swift) | Chrome Custom Tabs + `medsurgery-store` Edge Function (idéntico flujo) |
| Voice engine | [Features/Kapibaya/Voice/KapibayaVoiceEngine.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Features/Kapibaya/Voice/KapibayaVoiceEngine.swift), [Features/Kapibaya/Voice/KapibayaPresenceEngine.swift](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Features/Kapibaya/Voice/KapibayaPresenceEngine.swift) | Android `SpeechRecognizer` o Whisper cloud; TTS vía `kapibaya-tts` Edge Function (1:1); VAD vía Silero TF Lite |
| Consent + firmas NOM-004 | `Features/Surgical/InformedConsentView.swift` (PencilKit + SHA-256 + PDF) | Compose Canvas `DrawScope` + signature library + PdfDocument API |
| Telemetría | [Telemetry/](https://github.com/MedSurgery-Technology-Oficial/kiru-plus-main/blob/main/Telemetry/) | Sentry Android + analytics_events table igual |

### 1.4 Permisos iOS declarados

Solo dos `NSXxxUsageDescription` keys:
- `NSMicrophoneUsageDescription` — voz Dr. Kapibaya + dictado clínico
- `NSSpeechRecognitionUsageDescription` — STT del mismo flujo

UIBackgroundModes: `audio`, `fetch`, `remote-notification`.

**Implicación Android:** Permisos mínimos. `RECORD_AUDIO`, `POST_NOTIFICATIONS` (Android 13+), `INTERNET`. NO se necesitan `READ_CONTACTS`, `ACCESS_FINE_LOCATION`, ni `CAMERA`. Eso reduce drásticamente el riesgo en Google Play Data Safety Form.

### 1.5 Compliance médico-legal iOS

Sólido. KIRU+ ya tiene:
- **Disclaimer médico 5 secciones** (bloqueante, primera ejecución, persistido en `UserSettings.hasAcceptedMedicalDisclaimer`). Texto explícito: *"NO constituye consejo médico, diagnóstico ni tratamiento PARA CUALQUIER PERSONA QUE NO CUENTE CON CEDULA DE MÉDICO. NO DEBE SER USADA por el público en general"*.
- **3 URLs legales** en medsurgery.academy: política de privacidad, política de suscripciones, términos.
- **NOM-004 informed consent** con 4 firmas, hash SHA-256, PDF firmado.
- **GDPR/LFPDPPP**: tablas `account_deletion_requests` (48h grace) y `data_export_requests` ya cableadas con sus Edge Functions.
- **Validación cédula profesional**: `medical_validations` table con flag PII.
- **Wellbeing alerts** con `summary_redacted ≤ 500 chars` — NUNCA almacena conversación completa.

---

## 2. Decisión tecnológica (FASE 2)

### 2.1 Comparativa objetiva

| Criterio | A. Kotlin + Compose (nativo) | B. Flutter | C. React Native | D. Kotlin Multiplatform |
|----------|------------------------------|------------|-----------------|-------------------------|
| Velocidad inicial | Media | Alta | Alta | Media-baja |
| Calidad UI Android nativa | **Excelente** | Buena (renderizado propio) | Buena | Excelente (mismo Compose) |
| Reutilización lógica iOS | 0% (re-escribir) | 0% | 0% | **Alto si refactoramos Swift→common** |
| Acceso APIs nativas (audio, VAD, SpeechRecognizer, MediaProjection) | **Directo** | Plugin obligatorio | Plugin obligatorio | Directo |
| Compose/SwiftUI paridad | N/A | Bridges | Bridges | Compose Multiplatform en alpha estable |
| Tamaño del binario | Pequeño | Medio (engine embedded) | Grande (JS bundle + bridge) | Pequeño |
| Compatibilidad RevenueCat | SDK nativo oficial v8.x | Plugin oficial | Plugin oficial | Vía Android target |
| Compatibilidad Supabase | `supabase-kt` v3+ (oficial) | `supabase_flutter` (oficial) | `supabase-js` (oficial) | `supabase-kt` |
| Performance audio en tiempo real (Kapibaya) | **Nativo** | Plugins arriesgados | Bridges con latencia | Nativo |
| Riesgo Google Play | Bajo | Bajo-medio (algunos rechazos por bundle size) | Medio (declaraciones JS SDK) | Bajo |
| Mantenimiento a largo plazo | **Excelente** (Google es dueño) | Bueno | Volátil (RN breaking changes) | Emergente |
| Talento disponible | Amplio | Amplio | Amplio | Reducido |
| Madurez para apps de salud | **Alta** (referencia: Google Fit) | Media | Media | Baja |

### 2.2 Justificación de la elección — Kotlin + Jetpack Compose

1. **El feature crítico (Dr. Kapibaya) es real-time audio.** Bridges JS→Java→native añaden 30–60 ms de latencia en cada salto. En un tutor de voz con barge-in (interrupción del usuario al asistente), ese retraso degrada la UX y obliga a re-calibrar el VAD. Nativo elimina ese problema.

2. **No hay código iOS que sea trivialmente compartible.** Swift y Kotlin son lenguajes distintos. KMP solo agrega valor cuando la lógica de dominio (no la UI) está ya separada y portable. La app iOS hoy no tiene esa separación: lógica de negocio vive en ViewModels SwiftUI, no en módulos puros. Refactorizar la app iOS para KMP costaría más que reescribir el Android desde cero con Kotlin/Compose.

3. **El backend YA es la fuente compartida.** Supabase + Edge Functions + tablas con RLS + servidor Render: todo eso es la "capa común" real. La parte que se duplica entre iOS y Android es solo la capa de presentación + integración SDK, y ahí Kotlin/Compose es lo más mantenible.

4. **Google Play Health Apps + Data Safety:** apps médicas reciben revisión adicional. Stack nativo simplifica las declaraciones de SDK (sólo Google Play SDK Index inspecciona Android directo), reduciendo fricción.

5. **Compose ↔ SwiftUI son hermanos arquitectónicos.** Declarativos, state-driven, recomposición. Los developers que entendieron SwiftUI bien aprenden Compose en días. La paridad visual se logra con esfuerzo razonable.

6. **RevenueCat Paywalls v2 son Compose-first.** El SDK Android v8 ofrece `Paywall()` composable que renderiza el mismo template configurado en el dashboard de RevenueCat que usa iOS. Eso es paridad gratis.

7. **Soporte oficial de Supabase para Kotlin.** `supabase-kt` (Jan Schultke) está oficialmente endorsado por Supabase. Auth, Postgrest, Storage, Realtime y Functions con coroutines + Flow. Idiomatic Kotlin.

### 2.3 Lo que **no** se va a hacer (y por qué)

- ❌ **WebView/PWA**: tendríamos que portar AVSpeechSynthesizer y SFSpeechRecognizer a Web Speech API, perderíamos background audio, push notifications quality drops. Apple ya rechazaría una PWA disfrazada; Google Play también lo flaggea bajo Webview wrapper policy.
- ❌ **Flutter en esta fase**: re-aprender Dart cuando ya queremos paridad fina con SwiftUI nativo es desperdicio. Su mejor caso es startups que parten desde cero sin un iOS maduro al lado.
- ❌ **React Native**: SDK Supabase y RevenueCat son menos pulidos, audio nativo en tiempo real es bridge-heavy, Hermes/JSI hace más volátil el debugging clínico.
- ❌ **KMP en esta primera entrega**: dejaremos la puerta abierta a migrar ViewModels y modelos a `:shared` en el futuro, pero la primera versión Android NO depende de KMP.

---

## 3. Arquitectura Android objetivo (FASE 3)

```
.                                   # raíz del repo kiru-plus-android
├── settings.gradle.kts             # módulos
├── build.gradle.kts                # root + plugins
├── gradle.properties               # JVM args, AndroidX
├── gradle/libs.versions.toml       # version catalog
├── app/                            # único módulo en E1; modularizamos en E6
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/com/medsurgery/kiruplus/
│       │   ├── KiruApp.kt          # @HiltAndroidApp
│       │   ├── MainActivity.kt     # @AndroidEntryPoint + setContent
│       │   ├── app/
│       │   │   ├── App.kt          # Root composable + Theme
│       │   │   ├── nav/            # NavHost + AppRoute (sealed class espejo de iOS)
│       │   │   └── di/             # Hilt modules
│       │   ├── core/
│       │   │   ├── designsystem/   # Theme, Color, Typography (mapping de KiruTypography iOS)
│       │   │   ├── network/        # SupabaseClient + config
│       │   │   ├── auth/           # AuthRepository
│       │   │   ├── session/        # SessionManager
│       │   │   ├── premium/        # EntitlementsService + RevenueCat
│       │   │   ├── analytics/      # SentryAnalytics + Supabase analytics_events
│       │   │   ├── legal/          # MedicalDisclaimerManager
│       │   │   └── ui/             # WebViewScreen, LoadingState, EmptyState
│       │   ├── feature/
│       │   │   ├── auth/           # Login, Register, ForgotPassword, AccountDeletion
│       │   │   ├── onboarding/     # Splash, MedicalDisclaimer, Welcome
│       │   │   ├── home/           # HomeScreen + tabs
│       │   │   ├── profile/        # ProfileScreen, Settings
│       │   │   ├── paywall/        # RevenueCat Paywall + custom fallback
│       │   │   ├── legal/          # WebView shells para 3 URLs legales
│       │   │   ├── store/          # Chrome Custom Tabs → Stripe checkout
│       │   │   └── kapibaya/       # ViewModel + Voice services (último)
│       │   ├── domain/
│       │   │   ├── model/          # Profile, Entitlement, Pearl, ConversationTurn...
│       │   │   └── repository/     # interfaces puras
│       │   └── data/
│       │       ├── dto/            # @Serializable mirrors de Supabase tables
│       │       ├── repository/     # impls que llaman Postgrest/Functions
│       │       └── remote/         # SupabaseService helpers
│       └── res/
│           ├── values/strings.xml         # en
│           ├── values-es/strings.xml      # es (default)
│           ├── values-night/themes.xml    # dark mode
│           ├── values/colors.xml
│           ├── values/themes.xml
│           ├── xml/                       # locales_config, network_security
│           └── mipmap/                    # ic_launcher (placeholders en E0)
└── docs/
    ├── ANDROID_PORT_PLAN.md
    ├── GOOGLE_PLAY_CHECKLIST.md
    └── IOS_PARITY_MATRIX.md
```

### 3.1 Capas y responsabilidades

| Capa | Responsable de | Herramientas |
|------|----------------|--------------|
| **UI** | Composables sin lógica de negocio | Compose Material 3, RevenueCatUI |
| **ViewModel** | Estado UI, side effects | `androidx.lifecycle.ViewModel`, `StateFlow` |
| **Domain** | Modelos puros + interfaces de repositorio | Pure Kotlin, no dependencias Android |
| **Data** | Implementaciones de repositorio, DTOs Supabase | `supabase-kt`, `kotlinx.serialization` |
| **Network** | Cliente Supabase singleton | `supabase-kt` (Auth, Postgrest, Storage, Realtime, Functions) |
| **DI** | Inyección | Hilt |
| **Navigation** | NavHost type-safe | `androidx.navigation.compose` 2.8+ con type-safe routes |

### 3.2 Manejo de estado

Mismo modelo conceptual que iOS:
- `StateFlow<UiState>` por pantalla → equivalente de `@Published` + `ObservableObject`
- `SharedFlow<UiEvent>` para one-shot effects (navegación, snackbars) → equivalente de `.send(...)` de Combine
- `viewModelScope` + `Dispatchers.IO` para llamadas Supabase → equivalente de `Task { ... }`

**Regla de oro:** la UI nunca conoce Supabase. Pasa por ViewModel → Repository interface → impl Supabase.

### 3.3 Navegación

`androidx.navigation.compose` 2.8+ con `@Serializable` typed routes. Mapeo 1:1 del enum `AppRoute` de iOS a un `sealed interface KiruRoute`. Bottom nav de 5 tabs (iguales a iOS: Home / Academy / Logbook / Store / Profile). Stack per-tab vía `rememberSaveable`.

### 3.4 Offline / cache

- `DataStore Preferences` para flags y settings (equivalente a `UserDefaults`)
- `Room` para cache local de pearls, content_items, conversation_turns (equivalente a CoreData)
- `Coil` para imágenes con cache disk + memory (equivalente a `CachedAsyncImage`)

### 3.5 Localización

`values/strings.xml` (en, default fallback) + `values-es/strings.xml` (español). El idioma del usuario se respeta con Android per-app language settings (Android 13+). String catalog parsing manual desde iOS `.xcstrings` se hará en E5.

### 3.6 Theming

Material 3 con `dynamicColor = false` para no romper la identidad KIRU+. Tokens de color migrados de `KiruColor` a `colors.xml`. Tipografía: Material 3 `Typography` con familias mapeadas desde iOS `KiruTypography`. Dark mode soportado vía `values-night/`.

### 3.7 Accesibilidad

- `contentDescription` en todos los IconButton
- Tamaños mínimos 48 dp (Material guideline)
- TalkBack: probar onboarding y disclaimer
- Soporte para dynamic font scale (sp en todas las textos)

### 3.8 Seguridad

- **Sin secrets hardcoded.** `local.properties` para `SUPABASE_URL`, `SUPABASE_ANON_KEY`, `REVENUECAT_API_KEY` en debug. En CI/release: variables de entorno o Google Secret Manager.
- **Network Security Config:** TLS 1.2+ forzado, no plaintext.
- **Encrypted DataStore:** para tokens de sesión.
- **ProGuard/R8:** habilitado en release con keep rules para Supabase, Sentry, RevenueCat.
- **Biometric auth:** opcional, igual que iOS BiometricLockScreen (`androidx.biometric:biometric`).

### 3.9 Testing

- Unit tests: JUnit 5 + Turbine (Flow) + MockK (mocks)
- UI tests: Compose UI testing + Hilt test
- Snapshot tests: `Paparazzi` (sin emulador)
- Integration: Maestro (E2E)

### 3.10 CI/CD

- GitHub Actions: lint, ktlint, detekt, unit tests, instrumented tests opcionales
- Build variants: `debug` (logs, dev URL), `staging` (staging Supabase), `release` (prod, signing)
- Release: Fastlane Android + Play Store internal track automatizado

---

## 4. Reutilización de infraestructura (FASE 4)

| Infraestructura | Estado para Android | Acción |
|-----------------|---------------------|--------|
| Supabase Auth | ✅ Reutilizable 1:1 | Cambiar SDK (swift → kt). Misma tabla `auth.users`. Mismo trigger `handle_new_user` recientemente reescrito (2026-05-17). |
| Tabla `profiles` (2 filas hoy) | ✅ Reutilizable 1:1 | RLS por `auth.uid()`. DTOs Kotlin con `@Serializable`. |
| Tabla `iap_entitlements` | ✅ Reutilizable 1:1 | Misma fuente de verdad. RevenueCat Android publica al mismo webhook `revenuecat_webhook`. |
| Tabla `kapibaya_conversation_turns` (174 turnos) | ✅ Reutilizable 1:1 | Esquema `user_id text + auth_user_id uuid NULL` (memory K6A). |
| Tabla `surgical_logs`, `clinical_nom004_notes` | ✅ Reutilizable 1:1 | Edge Function `ingest_surgical_log` ya valida. |
| Tabla `pearls` (200) y `content_items` (4515) | ✅ Reutilizable 1:1 | Read-only desde mobile, RLS authenticated. |
| Storage buckets (perlas, podcasts, gdpr-exports, licenses) | ✅ Reutilizable 1:1 | Signed URLs vía Edge Functions ya existentes. |
| Edge Function `ask_kapibaya` | ✅ Reutilizable 1:1 | JWT requerido, Gemini backend, llamada desde Android idéntica. |
| Edge Function `ask_kapibaya_stream` | ✅ Reutilizable 1:1 | Streaming SSE; Ktor de `supabase-kt` lo soporta nativo. |
| Edge Function `kapibaya-tts` | ✅ Reutilizable 1:1 | Devuelve audio bytes; ExoPlayer/AudioTrack lo reproduce. |
| Edge Function `verify_iap_receipt` | ⚠️ Solo iOS | Para Android no usamos esto: RevenueCat Android publica directo al webhook. |
| Edge Function `revenuecat_webhook` | ✅ Reutilizable 1:1 | Recibe eventos de las dos plataformas. |
| Edge Function `medsurgery-store` (Stripe proxy) | ✅ Reutilizable 1:1 | Android abre Chrome Custom Tabs hacia el checkout URL que devuelve esta función. |
| Edge Function `process_account_deletions` | ✅ Reutilizable 1:1 | 48h grace period, igual flujo desde Android. |
| Edge Function `process_data_export` | ✅ Reutilizable 1:1 | GDPR Art. 15. |
| Edge Function `mux-webhook`, `video-catalog` | ✅ Reutilizable 1:1 | Android usa MUX SDK Android para reproducción. |
| Edge Function `issue_license_signed_url` | ✅ Reutilizable 1:1 | Upload cédula desde Android Gallery picker. |
| Edge Function `weekly-safety-report` | ✅ Reutilizable 1:1 | Cron job; sin cambios. |
| URLs legales (medsurgery.academy) | ✅ Reutilizable 1:1 | WebView shells. |
| Política privacidad / términos / suscripciones | ✅ Reutilizable 1:1 | Mismas URLs. Para Google Play hay que añadir referencia en Data Safety form. |
| Disclaimer médico texto | ✅ Reutilizable 1:1 | Mismos 5 párrafos; tradúzcanse a Compose con `Text(stringResource(R.string.disclaimer_section_1))`. |
| Sentry DSN | ✅ Reutilizable 1:1 | Sentry Android SDK toma el mismo DSN. |
| MUX, Stripe pk, RevenueCat pk | ⚠️ Cambian | RevenueCat usa una API key por plataforma (`goog_xxx` vs `appl_xxx`). Stripe usa la misma pk pública. MUX igual. |
| LogRocket | ⚠️ Opcional Android | LogRocket sí ofrece Android SDK; evaluar privacy implications con datos médicos. |
| `kcortex_engine/` (Python ML en Render) | ✅ Reutilizable 1:1 | Llamado vía endpoint HTTP que ya existe. |
| `api/tutor.js`, `api/send-email.js` | ✅ Reutilizable 1:1 | Render endpoints HTTP. |

**Resumen FASE 4:** ~95% del backend es reutilizable sin cambios. Los únicos puntos de divergencia plataforma son: (a) la API key de RevenueCat por plataforma, (b) la verificación de receipt IAP que en Android usa la integración nativa de RevenueCat con Google Play Billing en vez de `verify_iap_receipt`.

---

## 5. Checklist Google Play (FASE 5)

Ver documento dedicado: [GOOGLE_PLAY_CHECKLIST.md](GOOGLE_PLAY_CHECKLIST.md).

---

## 6. Matriz de paridad iOS vs Android (FASE 6)

Ver documento dedicado: [IOS_PARITY_MATRIX.md](IOS_PARITY_MATRIX.md).

---

## 7. Plan de ejecución por etapas (FASE 7)

### E0 — Preparación del repositorio (✅ EN CURSO HOY)

**Objetivo:** Crear repositorio `kiru-plus-android` con scaffolding mínimo + docs estratégicos + Gradle config + manifest, sin código de negocio aún.
**Archivos:** este `ANDROID_PORT_PLAN.md`, `GOOGLE_PLAY_CHECKLIST.md`, `IOS_PARITY_MATRIX.md`, `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, esqueleto Kotlin.
**Criterio de aceptación:** El proyecto abre en Android Studio Iguana o superior. `./gradlew assembleDebug` compila (requiere `gradle wrapper` previo).
**Comando de verificación:** `cd android && ./gradlew help` (después de bootstrap del wrapper).

### E1 — Setup del proyecto Android

**Objetivo:** Wrapper Gradle materializado, Compose preview rendera, theme y typography aplicados, idiomas en/es funcionando.
**Archivos clave:** `app/src/main/kotlin/.../app/App.kt`, `core/designsystem/Theme.kt`, `core/designsystem/Color.kt`, `core/designsystem/Type.kt`, `res/values{,-es,-night}/`.
**Riesgo:** versiones desalineadas entre AGP/Kotlin/Compose Compiler.
**Criterio:** Splash screen + ícono + theme dark/light + textos en es/en.

### E2 — Diseño base y navegación

**Objetivo:** NavHost con 5 destinos (Splash, MedicalDisclaimer, Login, Home, Profile). Bottom nav.
**Archivos:** `app/nav/KiruRoute.kt`, `app/nav/KiruNavHost.kt`, `feature/onboarding/...`, `feature/home/...`.
**Criterio:** El usuario navega Splash → Disclaimer (bloqueante) → Login → Home → Profile.

### E3 — Supabase Auth

**Objetivo:** Login con email/password. Sesión persistente en EncryptedDataStore.
**Archivos:** `core/network/SupabaseClient.kt`, `core/auth/AuthRepository.kt`, `data/repository/AuthRepositoryImpl.kt`, `feature/auth/LoginViewModel.kt`, `feature/auth/LoginScreen.kt`, `feature/auth/RegisterScreen.kt`, `feature/auth/ForgotPasswordScreen.kt`.
**Criterio:** Login + logout + recuperación de contraseña funcionan contra prod Supabase.

### E4 — Sesión y perfil

**Objetivo:** Lectura/escritura tabla `profiles`. Eliminación de cuenta vía `process_account_deletions`.
**Archivos:** `feature/profile/...`, `core/session/SessionManager.kt`, `data/repository/ProfileRepositoryImpl.kt`, `feature/auth/AccountDeletionScreen.kt`.
**Criterio:** Cumple Google Play Account Deletion requirement (in-app + web URL).

### E5 — Core screens

**Objetivo:** Home dashboard con disclaimers visibles, settings.
**Archivos:** `feature/home/HomeScreen.kt`, `feature/home/HomeViewModel.kt`, `core/legal/MedicalDisclaimerBanner.kt`.
**Criterio:** Home rendera, disclaimer permanente visible.

### E6 — Modularización (si se justifica)

**Objetivo:** Dividir `app/` en `:core:designsystem`, `:core:network`, `:core:auth`, `:feature:onboarding`, `:feature:auth`, etc., para acelerar build y enforzar boundaries.
**Decisión:** ejecutar solo cuando el monolito `app/` supere 100 archivos. En E2-E5 vivimos monolíticos.

### E7 — Premium / Paywall

**Objetivo:** RevenueCat configurado con `goog_xxx` key. `Paywall()` composable. Gating premium.
**Archivos:** `core/premium/RevenueCatManager.kt`, `core/premium/EntitlementsService.kt`, `feature/paywall/PaywallScreen.kt`.
**Criterio:** Compra sandbox funciona. Webhook `revenuecat_webhook` escribe en `iap_entitlements`.

### E8 — Store física (productos)

**Objetivo:** Chrome Custom Tabs → Stripe Checkout vía `medsurgery-store`.
**Archivos:** `feature/store/StoreScreen.kt`, `feature/store/StoreViewModel.kt`, `core/ui/CustomTabsLauncher.kt`.
**Criterio:** Catálogo lista, checkout abre, retorna a app.

### E9 — Legal / privacy / disclaimers

**Objetivo:** WebView shells para 3 URLs medsurgery.academy. Disclaimer médico modal bloqueante en primera ejecución.
**Archivos:** `feature/legal/WebViewScreen.kt`, `feature/onboarding/MedicalDisclaimerScreen.kt`, strings (5 secciones traducidas).
**Criterio:** Texto idéntico al iOS, persistencia en DataStore.

### E10 — Testing

Unit + UI + snapshot básico.

### E11 — Google Play readiness

Data Safety form, content rating, App Bundle (.aab), Privacy Policy en consola, Account Deletion URL.

### E12 — Internal testing

Build firmado, Play Console internal track.

### E13 — Closed testing

15+ testers, formulario de feedback.

### E14 — Production release

Staged rollout 10% → 50% → 100% con monitoring.

---

## 8. Riesgos y mitigaciones

| Riesgo | Severidad | Mitigación |
|--------|-----------|------------|
| Rechazo Google Play por contenido médico | Alta | Disclaimer 5 secciones (texto ya pulido), audiencia "Profesionales médicos", AI-generated content declaration honesta, Health Apps policy compliance desde E11 |
| Latencia audio en Kapibaya | Media | Engine nativo `AudioRecord` + `AudioTrack`, OpenSL ES si hace falta low-latency, TTS streaming desde Edge Function ya validada |
| Diferencia visual con iOS | Media | Design tokens unificados; QA paralela device-by-device en E5 |
| RevenueCat API key leakage | Alta | API keys en `local.properties` (gitignored) + `BuildConfig` injection; en CI usar variables protegidas |
| Cambios de versiones Compose/AGP | Baja | Version catalog (`libs.versions.toml`) y Renovate bot |
| Migración trigger `handle_new_user` rompe nuevos signUps en Android | Alta | El trigger fue reescrito 2026-05-17 (project memory). Validar primer signUp Android antes de E11 |

---

## 9. Próximos pasos accionables

1. ✅ Crear scaffolding Android (este commit).
2. ⏭️ Pedir al equipo: API key RevenueCat Android (`goog_xxx...`) y configurar en `local.properties`.
3. ⏭️ Bootstrap del wrapper Gradle (`cd android && gradle wrapper --gradle-version 8.11.1`) — requiere instalar Gradle CLI o abrir el proyecto en Android Studio.
4. ⏭️ Validar que `./gradlew assembleDebug` compila.
5. ⏭️ Empezar E3 (Auth) en una nueva sesión.

---

## 10. Decisiones técnicas registradas en este commit

| Decisión | Razón |
|----------|-------|
| Kotlin 2.1 + Compose + Material 3 + Hilt | Justificado en §2 |
| Single `app/` module en E0–E5; modularizar después | Reducir fricción inicial; modularizar cuando arroje retorno |
| `supabase-kt` v3 (Auth + Postgrest + Storage + Realtime + Functions) | SDK oficial endorsed; soporte coroutines + Flow |
| `androidx.navigation:navigation-compose` con typed routes `@Serializable` | Type safety equivalente al enum `AppRoute` de iOS |
| Hilt para DI | Estándar Google; menos boilerplate que manual; compatible con `@AssistedInject` |
| Material 3 sin dynamic color | Preservar identidad de marca KIRU+ |
| Compose BOM 2024.12.x | Estable y reciente |
| min SDK 26 (Android 8.0) | Cubre ~96% devices; necesario para audio APIs modernas |
| target SDK 35 | Requerido por Google Play desde Aug 2025 |
| Java 17 | Soporte AGP 8.x |
| Package name `com.medsurgery.kiruplus` | Coherente con bundle ID iOS y dominio medsurgery.academy |
| App label visible: `KIRU+` | Idéntico al iOS |
