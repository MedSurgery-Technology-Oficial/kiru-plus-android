# Matriz de paridad iOS vs Android — KIRU+

> **Updated:** 2026-05-17 · Build iOS de referencia: 17
> **Leyenda prioridad:** P0 (bloqueante para submit) · P1 (requerido v1.0) · P2 (nice to have v1.x) · P3 (futuro)
> **Leyenda estado:** ⛔ no iniciado · 🟡 en progreso · ✅ implementado · ⚠️ con riesgo · n/a no aplica

## 1. Onboarding y autenticación

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización backend | Complejidad | Estado | Acción necesaria |
|---|---|---|---|---|---|---|
| Splash + LaunchScreen | Necesario | P0 | n/a (UI) | Baja | ⛔ | E1 — `SplashScreen.kt` con drawable y mismo color brand |
| Disclaimer médico bloqueante (5 secciones) | Necesario | P0 | Reutiliza textos iOS | Baja | ⛔ | E2 — `MedicalDisclaimerScreen.kt` + 5 stringResources |
| Onboarding (welcome + carrousel) | Necesario | P1 | n/a | Media | ⛔ | E5 |
| Login email/password | Necesario | P0 | `supabase.auth.signInWithEmail()` | Baja | ⛔ | E3 |
| Registro nuevo usuario | Necesario | P0 | `supabase.auth.signUp()` + trigger `handle_new_user` | Baja | ⛔ | E3 — confirmar trigger no rompe (memory project_supabase_signup_unblock) |
| Recuperación contraseña (forgot password) | Necesario | P0 | `supabase.auth.resetPasswordForEmail()` | Baja | ⛔ | E3 |
| Sign in with Apple | n/a (iOS only) | n/a | n/a | n/a | n/a | NO portar; opcional Google Sign-In como equivalente Android |
| Google Sign-In | Necesario para paridad | P1 | Supabase OAuth Google | Media | ⛔ | E3 stretch |
| Biometric lock (cara/huella) | Necesario para paridad HIPAA | P1 | `androidx.biometric` | Media | ⛔ | E4 — `BiometricLockScreen.kt` |
| Session persistente | Necesario | P0 | EncryptedDataStore | Baja | ⛔ | E3 |
| Logout | Necesario | P0 | `supabase.auth.signOut()` | Baja | ⛔ | E4 |
| Eliminación de cuenta in-app | **Bloqueante Google Play** | P0 | Edge Function `process_account_deletions` (48h grace) | Media | ⛔ | E4 |
| Confirmación email | Necesario | P0 | Supabase email magic link | Baja | ⛔ | E3 |

## 2. Home dashboard y navegación

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Bottom tab bar (5 tabs en propuesta Android) | Necesario | P0 | n/a | Baja | ⛔ | E2 |
| Tab Home (dashboard) | Necesario | P0 | Lecturas Supabase | Media | ⛔ | E5 |
| Tab Academy | Necesario | P1 | `content_items` 4515 filas | Alta | ⛔ | post-v1.0 |
| Tab K-Tools (calculadoras) | Necesario | P1 | Lógica local | Media | ⛔ | post-v1.0 |
| Tab Logbook | Necesario | P1 | `surgical_logs` + Edge Function `ingest_surgical_log` | Alta | ⛔ | post-v1.0 |
| Tab Store | Necesario | P1 | `store_products` + Stripe via `medsurgery-store` | Media | ⛔ | E8 |
| Tab Profile | Necesario | P0 | `profiles` table | Baja | ⛔ | E4 |
| Disclaimer banner persistente en Home | Necesario | P0 | n/a | Baja | ⛔ | E5 |
| 260 rutas iOS (`AppRoute`) | Subconjunto P0+P1 | n/a | n/a | n/a | ⛔ | Mapear las críticas en `KiruRoute.kt`; el resto en releases v1.x |

## 3. Contenido educativo y médico

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Perlas clínicas (200) | Necesario | P1 | tabla `pearls` | Media | ⛔ | post-v1.0 |
| Resúmenes (jsons) | Necesario | P1 | tabla `resumenes_files` | Media | ⛔ | post-v1.0 |
| Flash cards | Deseable | P2 | local + cloud | Media | ⛔ | post-v1.0 |
| KiruPardy (juegos) | Deseable | P2 | `kirupardy_*` tablas | Alta | ⛔ | post-v1.0 |
| Arena (cases) | Deseable | P2 | `arena_*` tablas | Alta | ⛔ | post-v1.0 |
| Trivia rápida | Deseable | P2 | n/a | Media | ⛔ | post-v1.0 |
| Búsqueda global | Necesario | P1 | Edge Function _backfill_embeddings (vector) | Alta | ⛔ | post-v1.0 |
| Vademécum K-PHARMA | Deseable | P2 | catálogo CIE-10/CIE-9 | Media | ⛔ | post-v1.0 |
| Video player (MUX) | Necesario | P1 | MUX Android SDK + `mux-webhook` | Media | ⛔ | post-v1.0 |
| Podcasts | Deseable | P2 | `podcast_episodes` + Storage | Media | ⛔ | post-v1.0 |
| Biblioteca de libros médicos | Deseable | P2 | `medical_books`, `medical_chapters` | Media | ⛔ | post-v1.0 |

## 4. Dr. Kapibaya (voice + AI tutor)

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Chat texto con Dr. Kapibaya | Necesario | P1 | Edge Function `ask_kapibaya` | Media | ⛔ | E10+ |
| Streaming responses | Necesario | P1 | Edge Function `ask_kapibaya_stream` (SSE) | Media | ⛔ | E10+ |
| Voice mode (TTS hablado) | Deseable v1 / requerido v1.x | P2 | `kapibaya-tts` Edge Function | Alta (low-latency audio) | ⛔ | post-v1.0 |
| STT (speech-to-text) | Deseable v1 / requerido v1.x | P2 | Android SpeechRecognizer o cloud Whisper | Alta | ⛔ | post-v1.0 |
| VAD + barge-in | Deseable | P3 | Silero VAD TF Lite | Muy alta | ⛔ | post-v1.0 |
| Conversation memory | Necesario para voice | P2 | tabla `kapibaya_conversation_turns` | Media | ⛔ | post-v1.0 |
| Avatar floating button | Deseable | P3 | UI | Baja | ⛔ | post-v1.0 |

## 5. Premium / Paywall / Suscripciones

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Paywall RevenueCat nativo | Necesario | P0 | RC dashboard offerings | Media | ⛔ | E7 — `Paywall()` composable RC SDK Android v8 |
| Paywall fallback custom | Necesario | P1 | StoreKit→Google Play Billing | Media | ⛔ | E7 |
| Premium gating | Necesario | P0 | `EntitlementsService` espejo iOS | Baja | ⛔ | E7 |
| Restore purchases | Necesario | P0 | RC `restorePurchases()` | Baja | ⛔ | E7 |
| Silent restore banner | Deseable | P1 | iOS commit 6f6b72a | Baja | ⛔ | E7 |
| Apple Review premium bridge | Equivalente Android | P1 | Feature flag para reviewers Play Console | Media | ⛔ | E7 |
| Subscription management UI | Necesario | P0 | RC Customer Center Android | Baja | ⛔ | E7 |
| Subscription tier "Residente Base" | Necesario | P0 | RC offering | Baja | ⛔ | E7 |
| Subscription tier "Profesional Clínico" | Necesario | P0 | RC offering | Baja | ⛔ | E7 |
| Webhook RevenueCat → `iap_entitlements` | Ya existe | P0 | Edge Function `revenuecat_webhook` | Baja | ✅ | Solo cambiar `app_user_id` mapping |

## 6. Store de productos físicos

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Catálogo productos | Necesario | P1 | tabla `store_products` | Baja | ⛔ | E8 |
| Carrito (CartManager) | Necesario | P1 | local + Edge Function | Media | ⛔ | E8 |
| Checkout Stripe via web | Necesario | P1 | Chrome Custom Tabs + `medsurgery-store` Edge Function | Media | ⛔ | E8 |
| Imagen prefetcher | Deseable | P2 | Coil prefetch | Baja | ⛔ | E8 |
| Favoritos | Deseable | P2 | DataStore local | Baja | ⛔ | post-v1.0 |
| Shopify Admin Service | n/a hoy iOS (stub) | n/a | n/a | n/a | n/a | NO portar (stub iOS) |
| Returns / shipping policy URL | Necesario para Play | P0 | URL medsurgery.academy | Baja | ⛔ | E8 |

## 7. Compliance médico-legal y privacidad

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Disclaimer médico modal bloqueante | Necesario | P0 | 5 secciones texto iOS | Baja | ⛔ | E2 |
| Disclaimer banner persistente Home | Necesario | P0 | Strings iOS | Baja | ⛔ | E5 |
| Privacy policy WebView | Necesario | P0 | URL medsurgery.academy | Baja | ⛔ | E9 |
| Terms of service WebView | Necesario | P0 | URL medsurgery.academy | Baja | ⛔ | E9 |
| Subscription policy WebView | Necesario | P0 | URL medsurgery.academy | Baja | ⛔ | E9 |
| GDPR data export request | Necesario | P0 | Edge Function `process_data_export` | Media | ⛔ | E9 |
| GDPR data deletion request | Necesario | P0 | Edge Function `process_account_deletions` | Media | ⛔ | E4 |
| Validación cédula profesional | Necesario | P1 | `medical_validations` + Edge Function `issue_license_signed_url` | Media | ⛔ | post-v1.0 |
| NOM-004 informed consent (4 firmas + PDF + SHA-256) | Necesario para paridad pro | P2 | Compose Canvas + PdfDocument | Alta | ⛔ | post-v1.0 |
| Wellbeing alerts (redacted) | Deseable | P3 | tabla `wellbeing_alerts` | Media | ⛔ | post-v1.0 |
| AI consent K-CORTEX | Necesario si usamos K-CORTEX en v1 | P2 | tabla `patient_ai_processing_consents` | Media | ⛔ | post-v1.0 |

## 8. Configuración y soporte

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Settings screen | Necesario | P0 | DataStore | Baja | ⛔ | E4 |
| Cambio de idioma (es/en) | Necesario | P0 | Android 13 per-app locale | Baja | ⛔ | E1 |
| Theme dark/light | Necesario | P0 | `values-night/` | Baja | ⛔ | E1 |
| Notificaciones preferences | Necesario | P1 | DataStore + Notification channels | Baja | ⛔ | E5 |
| About / version info | Necesario | P0 | BuildConfig | Baja | ⛔ | E4 |
| Soporte (mailto medsurgery) | Necesario | P0 | Intent ACTION_SENDTO | Baja | ⛔ | E4 |

## 9. Multimedia y permisos

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Permiso micrófono | Necesario para Kapibaya voice | P2 | `RECORD_AUDIO` runtime permission | Baja | ⛔ | post-v1.0 con Kapibaya |
| Permiso speech recognition | n/a (Android no requiere permiso separado) | n/a | n/a | n/a | n/a | Solo iOS |
| Permiso notificaciones (Android 13+) | Necesario | P1 | `POST_NOTIFICATIONS` | Baja | ⛔ | E5 |
| Upload imagen cédula | Necesario | P1 | Photo Picker (sin permiso) | Baja | ⛔ | post-v1.0 |
| Background audio (Kapibaya speaking) | Deseable | P2 | Foreground service | Media | ⛔ | post-v1.0 |
| Push notifications (FCM) | Necesario | P1 | Firebase Cloud Messaging | Media | ⛔ | E5 |

## 10. Manejo de errores y estados

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Errores Supabase con feedback amigable | Necesario | P0 | n/a | Baja | ⛔ | E3 |
| Estados vacíos (empty states) | Necesario | P0 | Compose components | Baja | ⛔ | E5 |
| Estado offline (no network) | Necesario | P0 | ConnectivityManager | Media | ⛔ | E5 |
| Loading skeletons | Deseable | P1 | shimmer | Baja | ⛔ | E5 |
| Recovery de crash (Sentry) | Necesario | P1 | Sentry Android | Baja | ⛔ | E10 |

## 11. Analytics y telemetría

| Funcionalidad iOS | Existe Android | Prioridad | Reutilización | Complejidad | Estado | Acción |
|---|---|---|---|---|---|---|
| Sentry crash reporting | Necesario | P0 | Sentry Android SDK | Baja | ⛔ | E10 |
| analytics_events table (event bus) | Necesario | P1 | Supabase insert | Baja | ⛔ | E10 |
| MUX video analytics | Necesario si tab video | P1 | MUX Android SDK | Media | ⛔ | post-v1.0 |
| LogRocket | Opcional | P3 | Evaluar privacy con datos médicos | Media | ⛔ | ⚠️ Probablemente skip en Android |
| Opt-out telemetría | Recomendado | P1 | DataStore flag | Baja | ⛔ | E5 |

---

## Resumen de paridad mínima para v1.0 Android

**Bloqueantes (P0) para primer submit a Google Play internal track:**
1. Auth (login/register/forgot/logout/delete account)
2. Disclaimer médico (modal + banner)
3. Legal WebViews (privacy/terms/subscriptions)
4. Profile + Settings + idioma + theme
5. Paywall RevenueCat funcional (signup + restore + management)
6. Home dashboard (vacío o con cards básicas)
7. Tab Store con catálogo + Stripe checkout
8. Sentry + telemetría básica

**Diferido para v1.x sin bloquear submit:**
- Dr. Kapibaya voice (chat texto sí puede entrar en v1.0)
- Logbook quirúrgico
- NOM-004 consent flow
- Academy / videos / podcasts / pearls
- Arena / KiruPardy / juegos
- Validación cédula automatizada

Estrategia: shippear v1.0 minimal pero pulida, captar usuarios Android, iterar con telemetría real.
