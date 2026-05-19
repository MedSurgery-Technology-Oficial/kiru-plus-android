# KIRU+ Android — Roadmap V2 (post-v0.1.0)

> **Created:** 2026-05-18 · **Author:** Plan de trabajo actualizado tras 11 commits del Android (E0→slug fix).
> Reemplaza la sección "ruta crítica" de `ANDROID_PORT_PLAN.md` (que era pre-implementation).

---

## Donde estamos

`v0.1.0` cierra la base: scaffolding, auth completa, design system, navegación 5 tabs, Store + checkout externo, WebView legal con offline banner, Settings con idioma/tema persistente, account deletion in-app, 47 unit tests, ProGuard hardened, Privacy/Data Safety draft.

**Bloqueantes externos al cierre:**
1. **RevenueCat `goog_xxx` API key** — necesaria para empezar E7 Paywall (admin RevenueCat debe generarla en el dashboard del proyecto Android).
2. **Google Play Console — Account Deletion URL pública** — marketing/legal debe publicarla (puede reutilizar slug `eliminar-cuenta-kiru-app`).
3. **Google Play Console — Returns/Shipping URL pública** — marketing/legal.
4. **Play Store signing key** — generar en Play Console o local + subir keystore a CI.

**Bloqueantes resueltos en esta sesión:**
- Legal URLs canónicas (`kiru-app` slugs) — verified HTTP 200, iOS + Android alineados.

---

## Sprint A — Compliance & Cuenta (1 semana, no bloqueado)

**Objetivo:** dejar el Profile usable, GDPR export visible, telemetría con opt-in real, CI corriendo en cada PR.

| ID | Tarea | Justificación | Effort |
|---|---|---|---|
| E4 | Profile editing screen + upload de cédula via `issue_license_signed_url` | P0 — sin esto el Profile tab es solo links | 1.5 d |
| E10.2 | Sentry opt-in toggle en Settings (DataStore flag + reconfigurar SDK) | P1 — privacidad usuario | 0.5 d |
| E10.3 | GDPR data export request screen (calls `process_data_export`) | P0 | 0.5 d |
| E11 | GitHub Actions CI: `./gradlew test lint assembleDebug` en PR + main | P0 ops | 0.5 d |
| E11.1 | Pre-commit Husky equivalente (ktlint + tests) | P2 polish | 0.25 d |
| E10.4 | Tests instrumented (Espresso/Compose UI) básicos: Disclaimer scroll gate + Login fields | P1 cobertura | 1 d |

**Salida:** v0.2.0 candidata para Internal Testing **sin Paywall**. App tiene profile real, GDPR completo (export + delete), CI green, instrumented smoke.

---

## Sprint B — RevenueCat + Premium (1 semana, bloqueado hasta tener `goog_xxx`)

**Objetivo:** monetización funcional. Requisito explícito de Play Store si la app tiene suscripciones.

| ID | Tarea | Justificación | Effort |
|---|---|---|---|
| E7.0 | Setup RevenueCat con `goog_xxx` + offerings espejo iOS en Play Console (`kiruplus_monthly`, `kiruplus_annual`, etc.) | P0 | 0.5 d (admin) |
| E7.1 | `Paywall()` Composable usando `revenuecat-purchases-ui` | P0 | 1 d |
| E7.2 | `EntitlementsService` (lee `iap_entitlements`) + `PremiumGate` composable | P0 | 1 d |
| E7.3 | Restore purchases UI + silent restore banner (paridad iOS commit `6f6b72a`) | P0 | 0.5 d |
| E7.4 | Customer Center Android (cancelaciones, billing history) | P0 | 0.5 d |
| E7.5 | Apple Review premium bridge → Android equivalente (feature flag reviewer Play Console) | P1 | 0.5 d |
| E7.6 | Unit tests EntitlementsService + IntegrationTest mock RevenueCat | P1 | 0.5 d |

**Salida:** v0.3.0 con monetización funcional. Cumple requisito Play Console para apps con IAP.

---

## Sprint C — Pearls + Academy básico (1.5 semanas)

**Objetivo:** primer contenido educativo viewable en el Android para que la app no se sienta vacía post-login.

| ID | Tarea | Justificación | Effort |
|---|---|---|---|
| E12 | `PearlsRepository` + `PearlsScreen` (feed LazyColumn) — tabla `pearls` (200 filas) | P1 | 1 d |
| E12.1 | `PearlDetailScreen` (cuerpo completo + sharing + favorito local) | P1 | 0.5 d |
| E13 | `AcademyRepository` + `LessonsBrowserScreen` — tabla `content_items` (4515 filas) | P1 | 1.5 d |
| E13.1 | `LessonDetailScreen` con render Markdown + video MUX embed básico | P1 | 1 d |
| E13.2 | Quiz player composable (lee preguntas de `content_items` con tipo `quiz`) | P1 | 1.5 d |

**Salida:** v0.4.0 con biblioteca educativa + perlas. Hace al app "interesante" para usuarios beta.

---

## Sprint D — Logbook quirúrgico (1 semana)

**Objetivo:** feature critically-iOS del bookbinder. Diferenciador para residentes.

| ID | Tarea | Justificación | Effort |
|---|---|---|---|
| E14 | `LogbookRepository` + `LogbookListScreen` (filtros por fecha/procedimiento) | P1 | 1 d |
| E14.1 | `NewSurgicalLogForm` (procedimiento, fecha, role, complicaciones) | P1 | 1 d |
| E14.2 | `EditSurgicalLogForm` + delete con confirmación | P1 | 0.5 d |
| E14.3 | Edge Function `ingest_surgical_log` integration | P1 | 0.5 d |
| E14.4 | Estadísticas básicas (procedimientos por mes, top categorías) | P2 | 1 d |
| E14.5 | Tests unitarios LogbookViewModel + Repository | P1 | 0.5 d |

**Salida:** v0.5.0 con Logbook básico, paridad funcional iOS en feature insignia.

---

## Sprint E — Dr. Kapibaya texto (1.5 semanas)

**Objetivo:** chat con AI tutor médico — el "killer feature" del producto. Texto primero, voz después.

| ID | Tarea | Justificación | Effort |
|---|---|---|---|
| E15 | `KapibayaChatScreen` UI básica (lista de turns + input bar) | P1 | 1 d |
| E15.1 | Integración con Edge Function `ask_kapibaya` (request/response) | P1 | 0.5 d |
| E15.2 | Streaming SSE con `ask_kapibaya_stream` (Compose typing animation) | P1 | 1 d |
| E15.3 | Conversation memory persistence (`kapibaya_conversation_turns`) | P1 | 1 d |
| E15.4 | Sugerencias de prompts (chips) | P2 | 0.5 d |
| E15.5 | Citations / source links cuando Kapibaya cita contenido | P2 | 1 d |
| E15.6 | AI consent K-CORTEX (modal pre-first-use) | P2 | 0.5 d |

**Salida:** v0.6.0 con Dr. Kapibaya texto. Diferenciador competitivo activado.

---

## Sprint F — K-Tools calculadoras (1 semana)

**Objetivo:** calculadoras clínicas. Acceso desde Home Quick Action.

| ID | Tarea | Justificación | Effort |
|---|---|---|---|
| E16 | `KToolsHubScreen` (lista de calculadoras agrupadas) | P1 | 0.5 d |
| E16.1 | Framework `CalculatorScaffold` (inputs + result + interpretación) | P1 | 1 d |
| E16.2 | Implementar 10 calculadoras core (ASA, Glasgow, Apgar, BMI, etc.) | P1 | 2 d |
| E16.3 | Favoritos de calculadora + recientes | P2 | 0.5 d |

**Salida:** v0.7.0 con K-Tools funcional.

---

## Sprint G — Release prep + Internal Testing (1 semana)

**Objetivo:** primera subida a Google Play Internal Testing track.

| ID | Tarea | Justificación | Effort |
|---|---|---|---|
| E17 | Signed release build (keystore CI-managed) + AAB | P0 | 0.5 d |
| E17.1 | Crash & ANR baseline (Sentry sample + Crashlytics si se decide) | P1 | 0.5 d |
| E17.2 | Privacy Policy + Data Safety form Play Console (usando `docs/PRIVACY_AND_DATA_SAFETY.md`) | P0 | 0.5 d |
| E17.3 | App Bundle Explorer review + reduce size con baseline profile | P2 | 0.5 d |
| E17.4 | Internal testing track con 5-10 testers seleccionados | P0 | 0.5 d |
| E17.5 | Plan de rollback (cómo bajar release) documentado | P1 | 0.25 d |
| E18 | Closed Testing (50-100 usuarios, ≥14 días feedback) | P0 | feedback gating |
| E19 | Production rollout 10% → 50% → 100% (3-5 días entre etapas) | P0 | feedback gating |

**Salida:** v1.0.0 en Google Play production.

---

## Sprint H+ — Post v1.0

**v1.1:** Dr. Kapibaya voice (TTS + STT + VAD + barge-in). Sprint dedicado de 2-3 semanas.
**v1.2:** K-CORTEX (LLM education). Sprint 2 semanas.
**v1.3:** NOM-004 informed consent + Diploma Verification.
**v1.4:** KiruPardy + Arena (gamification).
**v1.5+:** ARSimulation, Push notifications avanzadas, Wear OS companion (si aplica).

---

## Cronograma estimado (1 dev senior full-time)

| Sprint | Semana | Bloqueo |
|---|---|---|
| A · Compliance & Cuenta | 1 | — |
| B · RevenueCat Paywall | 2 | goog_xxx API key |
| C · Pearls + Academy | 3-4 | — |
| D · Logbook | 5 | — |
| E · Kapibaya texto | 6-7 | — |
| F · K-Tools | 8 | — |
| G · Release prep + Internal | 9 | signing key + URLs marketing |
| Closed Testing | 10-11 | feedback gating |
| Production rollout | 12 | — |

**Total a v1.0 production:** ~12 semanas (3 meses) con 1 dev senior, o ~6-7 semanas con 2 devs senior.

---

## Riesgos identificados

| Riesgo | Probabilidad | Impacto | Mitigación |
|---|---|---|---|
| Apple rechazo Build 18 retrasa decisiones cross-platform | Media | Alto | Mantener Android desacoplado; iOS Build 18 fixes ya pushed |
| RevenueCat dashboard sin Android app configurada | Alta | Bloqueante E7 | Solicitar al admin **ahora**, mientras se trabaja Sprint A |
| Account Deletion URL pública no creada | Media | Bloqueante Play Console | Marketing/legal — escalar |
| Marketing site re-cambia slugs legales | Baja | Alto | Mantener constants centralizadas (`BuildConfig`/`Info.plist`); script CI para validar URLs 200 |
| MUX Android SDK costos | Baja | Medio | Evaluar costo antes del Sprint C; alternativa: HLS player nativo Exoplayer |
| ProGuard rompe Hilt en release | Media | Alto | Tests instrumented + signed release smoke en Sprint G |

---

## Métricas de éxito v1.0

- 47 → ≥ 80 unit tests (cobertura > 60% en VMs + repositorios).
- 0 crashes en Sentry durante 7 días post-release (production).
- 4.0+ rating Play Store inicial.
- Crash-free sessions > 99.5%.
- Time-to-first-paywall-view < 3 s (medida desde launch).
