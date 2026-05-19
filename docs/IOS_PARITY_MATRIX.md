# Matriz de paridad iOS vs Android — KIRU+

> **Updated:** 2026-05-18 · Build iOS de referencia: **18** (Build 17 rechazado, Build 18 in prep)
> **Android commits:** 11 commits (c35e73d → 7f9c3f0 actualización legal URLs)
> **Leyenda prioridad:** P0 (bloqueante para submit) · P1 (requerido v1.0) · P2 (nice to have v1.x) · P3 (futuro)
> **Leyenda estado:** ⛔ no iniciado · 🟡 en progreso · ✅ implementado · ⚠️ con riesgo · n/a no aplica

---

## Resumen de progreso v0.1.0 (2026-05-18)

**Commits Android:**
```
7f9c3f0  fix legal URL slugs iOS aligned          [iOS]
a80a6c4  fix legal URL slugs (-kiru-pro → -kiru-app)
239fb08  docs: 404 finding on legal URLs
b776e0c  E10.1 LocaleApplier + Settings/Account/Store VM tests
217c88b  E10  unit tests auth + ProGuard + Privacy draft
1a36640  E8.1 ProductDetailScreen
76e691b  E8   Store + Stripe Custom Tabs
ec8896c  E5.1 Bottom navigation 5 tabs (Material 3)
d994747  E9   WebView + Disclaimer scroll-gated
cced83e  E5   Settings + Home polish + UserPreferences
58b8525  E1   Visual polish + KIRU+ brand
01637f2  E3   Auth real Supabase + AuthError mapper
c35e73d  E0   Initial scaffolding
```

**Métricas:**
- **47 unit tests** (100% PASS): EmailValidator (10) + AuthErrorMapper (11) + Login (5) + Register (5) + Forgot (5) + Settings (4) + AccountDeletion (4) + Store (3).
- **Build time:** debug `./gradlew assembleDebug` ~15-30 s incremental.
- **APK debug:** ~30 MB.
- **Stack:** Kotlin 2.1 · Compose BOM 2024.12 · Material 3 · Hilt 2.51 · supabase-kt 3.0.2 · DataStore 1.1 · Coil 2.7 · RevenueCat 8.10.4 (presente, no cableado).

---

## 1. Onboarding y autenticación

| Funcionalidad iOS | Prioridad | Estado | Commit/notas |
|---|---|---|---|
| Splash + LaunchScreen | P0 | ✅ | E0 (system splash) + E1 (Compose splash con LogoKIRU PNG 160dp) |
| Disclaimer médico bloqueante (5 secciones) | P0 | ✅ | E9 con **scroll-gate** (canScrollForward) + AnimatedVisibility hint |
| Login email/password | P0 | ✅ | E3 con AuthError mapper localizado |
| Registro nuevo usuario | P0 | ✅ | E3 + EmailValidator + strong password match Supabase policy |
| Recuperación contraseña | P0 | ✅ | E3 con anti-enumeration (errores genéricos silenciados) |
| Sign in with Apple | n/a | n/a | NO portar |
| Google Sign-In | P1 | ⛔ | Pendiente — Supabase OAuth Google |
| Biometric lock | P1 | ⛔ | Pendiente — `androidx.biometric` (dep ya en deps) |
| Session persistente | P0 | ✅ | E3 supabase-kt `autoLoadFromStorage = true` |
| Logout | P0 | ✅ | E5 (SettingsScreen → signOut → Login) |
| Eliminación de cuenta in-app | P0 | ✅ | E3 (AccountDeletionScreen + ack + Edge Function process_account_deletions) |
| Confirmación email post-signup | P0 | 🟡 | E3 muestra mensaje "check your email" tras signUp, pero NO bloquea login con email no-confirmado (Supabase decide) |
| Onboarding (welcome + carrousel) | P1 | ⛔ | Pendiente — actualmente solo Disclaimer |

## 2. Home dashboard y navegación

| Funcionalidad iOS | Prioridad | Estado | Commit/notas |
|---|---|---|---|
| Bottom tab bar 5 tabs | P0 | ✅ | E5.1 NavigationBar M3 (Home/Academy/Logbook/Store/Profile) |
| Tab Home (dashboard) | P0 | 🟡 | E5: WelcomeCard + Quick Actions 2×2. Dashboard rico (Hero/Arena/Pearls) pendiente |
| Tab Academy | P1 | ⛔ | E5.1 placeholder "Coming soon"; contenido pendiente |
| Tab K-Tools (calculadoras) | P1 | ⛔ | NO es tab separado en Android (decisión); accesible como Quick Action en Home |
| Tab Logbook | P1 | ⛔ | E5.1 placeholder; CRUD pendiente |
| Tab Store | P1 | ✅ | E8 catálogo Supabase + E8.1 detalle + Custom Tabs |
| Tab Profile | P0 | 🟡 | E5.1 placeholder con links Settings/Legal/Delete; datos del médico pendientes |
| Disclaimer banner persistente Home | P0 | ✅ | E5 (Card surfaceVariant con `disclaimer_short_banner`) |
| 260 rutas iOS subconjunto | n/a | 🟡 | ~13 rutas en `KiruRoute.kt` (Splash, Disclaimer, Login, Register, ForgotPassword, Home, Settings, AccountDeletion, WebView, ProductDetail, Paywall stub, Store stub) |

## 3. Contenido educativo y médico

| Funcionalidad iOS | Prioridad | Estado | Notas |
|---|---|---|---|
| Perlas clínicas (200) | P1 | ⛔ | Tabla `pearls` lista — feed Compose pendiente |
| Resúmenes (jsons) | P1 | ⛔ | Tabla `resumenes_files` |
| Flash cards | P2 | ⛔ | |
| KiruPardy (juegos) | P2 | ⛔ | |
| Arena (cases) | P2 | ⛔ | |
| Trivia rápida | P2 | ⛔ | |
| Búsqueda global vectorial | P1 | ⛔ | Edge Function `_backfill_embeddings` ya existe |
| Vademécum K-PHARMA | P2 | ⛔ | |
| Video player (MUX) | P1 | ⛔ | MUX Android SDK pendiente |
| Podcasts | P2 | ⛔ | |
| Biblioteca de libros médicos | P2 | ⛔ | |

## 4. Dr. Kapibaya (voice + AI tutor)

| Funcionalidad iOS | Prioridad | Estado | Notas |
|---|---|---|---|
| Chat texto con Dr. Kapibaya | P1 | ⛔ | Edge Function `ask_kapibaya` lista |
| Streaming responses (SSE) | P1 | ⛔ | Edge Function `ask_kapibaya_stream` lista |
| Voice mode (TTS hablado) | P2 | ⛔ | `kapibaya-tts` Edge Function lista (cloud TTS) |
| STT (speech-to-text) | P2 | ⛔ | Android SpeechRecognizer o cloud Whisper |
| VAD + barge-in | P3 | ⛔ | Silero VAD TF Lite |
| Conversation memory (174 turns activos) | P2 | ⛔ | Tabla `kapibaya_conversation_turns` lista |
| Avatar floating button | P3 | ⛔ | |

## 5. Premium / Paywall / Suscripciones — **E7 BLOQUEADO por API key**

| Funcionalidad iOS | Prioridad | Estado | Notas |
|---|---|---|---|
| Paywall RevenueCat nativo | P0 | ⛔ | Dep RevenueCat 8.10.4 ya en classpath; falta `goog_xxx` API key + offerings en Play Console |
| Paywall fallback custom | P1 | ⛔ | |
| Premium gating | P0 | ⛔ | `EntitlementsService` pendiente |
| Restore purchases | P0 | ⛔ | RC `Purchases.shared.restorePurchases()` |
| Silent restore banner | P1 | ⛔ | |
| Apple Review premium bridge → Android equiv | P1 | ⛔ | Feature flag para reviewers Play Console |
| Subscription management UI | P0 | ⛔ | RC Customer Center Android |
| Webhook RevenueCat → `iap_entitlements` | P0 | ✅ | Edge Function `revenuecat_webhook` ya existe (compartido con iOS) |

## 6. Store de productos físicos

| Funcionalidad iOS | Prioridad | Estado | Commit/notas |
|---|---|---|---|
| Catálogo productos | P1 | ✅ | E8 con Supabase Postgrest + Coil |
| Detalle de producto | P1 | ✅ | E8.1 ProductDetailScreen con hero 280dp |
| Carrito (CartManager) | P1 | ⛔ | E8/E8.1 son single-product checkout |
| Checkout Stripe via web | P1 | ✅ | E8 Chrome Custom Tabs con `kiru_navy_blue` toolbar |
| Imagen prefetcher | P2 | 🟡 | Coil hace prefetch automático en LazyColumn |
| Favoritos | P2 | ⛔ | |
| Returns/shipping policy URL | P0 | ⛔ | URL pendiente verificación con marketing |

## 7. Compliance médico-legal y privacidad

| Funcionalidad iOS | Prioridad | Estado | Commit/notas |
|---|---|---|---|
| Disclaimer médico modal bloqueante | P0 | ✅ | E9 con scroll-gate dual (accepted + scrolledToEnd) |
| Disclaimer banner persistente Home | P0 | ✅ | E5 |
| Privacy policy WebView | P0 | ✅ | E9 + slug fix `7f9c3f0` (URL responde 200) |
| Terms of service WebView | P0 | ✅ | E9 + slug fix (URL responde 200) |
| Subscription policy WebView | P0 | ✅ | E9 + slug fix (URL responde 200) |
| GDPR data export request | P0 | ⛔ | Edge Function `process_data_export` lista, falta UI |
| GDPR data deletion request | P0 | ✅ | E3 AccountDeletionScreen (48h grace) |
| Validación cédula profesional | P1 | ⛔ | Edge Function `issue_license_signed_url` lista + Photo Picker |
| NOM-004 informed consent (4 firmas + PDF + SHA-256) | P2 | ⛔ | Compose Canvas + PdfDocument |
| Wellbeing alerts (redacted) | P3 | ⛔ | |
| AI consent K-CORTEX | P2 | ⛔ | |
| **Account Deletion URL pública** | P0 | ⛔ | Google Play exige URL externa; pendiente con marketing |

## 8. Configuración y soporte

| Funcionalidad iOS | Prioridad | Estado | Commit/notas |
|---|---|---|---|
| Settings screen | P0 | ✅ | E5 con 4 secciones (General/Legal/Account/About) |
| Cambio de idioma (es/en) | P0 | ✅ | E5 + LocaleApplier (E10.1) — per-app language API |
| Theme dark/light | P0 | ✅ | E5 dinámico via AppViewModel observando DataStore |
| Haptic feedback toggle | P1 | ✅ | E5 (DataStore flag) |
| Notificaciones preferences | P1 | ⛔ | DataStore + Notification channels |
| About / version info | P0 | ✅ | E5 con `BuildConfig.VERSION_NAME + VERSION_CODE` |
| Soporte (mailto medsurgery) | P0 | ⛔ | Intent ACTION_SENDTO |

## 9. Multimedia y permisos

| Funcionalidad iOS | Prioridad | Estado | Commit/notas |
|---|---|---|---|
| Permiso micrófono (Kapibaya voice) | P2 | 🟡 | E0 manifest declarado; runtime permission pendiente con Kapibaya |
| Permiso notificaciones (Android 13+) | P1 | 🟡 | E0 manifest declarado; runtime permission + canal pendiente |
| Upload imagen cédula | P1 | ⛔ | Photo Picker (sin permiso) |
| Background audio | P2 | ⛔ | Foreground service para Kapibaya speaking |
| Push notifications (FCM) | P1 | ⛔ | |

## 10. Manejo de errores y estados

| Funcionalidad iOS | Prioridad | Estado | Commit/notas |
|---|---|---|---|
| Errores Supabase con feedback amigable | P0 | ✅ | E3 AuthError mapper (8 cases + Unknown) |
| Estados vacíos (empty states) | P0 | 🟡 | E8 Store tiene Empty; otros pendientes |
| Estado offline (no network) | P0 | 🟡 | E9 OfflineBanner en WebView; ConnectivityManager global pendiente |
| Loading skeletons (shimmer) | P1 | ⛔ | |
| Recovery de crash (Sentry) | P1 | ✅ | E0 configurado opt-in via `BuildConfig.SENTRY_DSN`; falta toggle UI |

## 11. Analytics y telemetría

| Funcionalidad iOS | Prioridad | Estado | Commit/notas |
|---|---|---|---|
| Sentry crash reporting | P0 | ✅ | E0 dep + opt-in via DSN; `io.sentry.auto-init=false` |
| analytics_events table (event bus) | P1 | ⛔ | Supabase insert |
| MUX video analytics | P1 | ⛔ | |
| LogRocket | P3 | ⚠️ | Skip Android por riesgo privacy con datos médicos |
| Opt-out telemetría | P1 | ⛔ | Falta toggle en Settings (Sentry hoy es opt-in via build, no runtime) |

---

## Resumen P0 al cierre de v0.1.0

**P0 bloqueantes ✅ cubiertos:**
1. Auth completa (login/register/forgot/logout/delete) ✅
2. Disclaimer médico (modal scroll-gated + banner Home) ✅
3. Legal WebViews (privacy/terms/subscriptions) con URLs live 200 ✅
4. Settings + idioma + theme + about ✅
5. Tab Store con catálogo + Stripe Custom Tabs ✅
6. Sentry config + telemetría base ✅
7. AuthError mapper localizado ✅

**P0 bloqueantes ⛔ pendientes (próximo sprint):**
1. **Paywall RevenueCat funcional** — bloqueado por `goog_xxx` API key
2. **Premium gating** — depende del paywall
3. **Profile mínimo con datos del médico**
4. **Sentry opt-in toggle en Settings**
5. **Account Deletion URL pública** — pendiente con marketing
6. **Returns/shipping policy URL** — pendiente con marketing
7. **GDPR data export UI** — Edge Function ya existe

**P0 nice to have antes de Internal Testing:**
- Confirmación email post-signup como UX flow explícito
- Soporte (mailto medsurgery) en Settings
- Sentry opt-in toggle real

---

## Estrategia release

**v0.1.0 (actual, no para Play Console):** core funcional + tests + URLs OK.

**v0.2.0 (siguiente milestone):** Paywall + Profile + GDPR export UI + Sentry toggle. Internal Testing track.

**v1.0.0 (Play Console submit):** v0.2.0 + Pearls + Logbook básico + Dr. Kapibaya chat texto + Closed Testing.

**v1.1+:** Voice mode, K-CORTEX, NOM-004, Academy completo, etc.
