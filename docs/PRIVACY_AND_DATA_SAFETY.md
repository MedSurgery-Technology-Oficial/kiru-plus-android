# KIRU+ Android — Privacy & Data Safety (Google Play Console draft)

Versión: 1.0 (E10) · Fecha: 2026-05-18 · Para revisión legal antes de submit.

Este documento es el **draft** del formulario "Data safety" del Play Console.
Refleja el comportamiento real del binary commit `1a36640` (post-E8.1) más los
follow-ups conocidos. Toda answer aquí debe ser revisada por legal antes de
publicarla.

## Resumen ejecutivo

KIRU+ es una herramienta educativa para profesionales y estudiantes de salud.
La app **no procesa pagos in-app**, **no rastrea ubicación**, **no comparte
datos con terceros para publicidad**, y **soporta eliminación de cuenta**
end-to-end vía un Edge Function de Supabase con grace period de 48 h.

## Data types collected

| Categoría | Tipo | Recopilado | Required / Optional | Compartido con terceros | Almacenado en |
|---|---|---|---|---|---|
| Personal info | Email address | Sí | Required (Sign up / Sign in) | No | Supabase (`auth.users`) |
| Personal info | User ID | Sí | Required | No | Supabase (`auth.users.id`) |
| Authentication | Password | Sí | Required | No (nunca sale del cliente en texto plano) | Supabase Auth (bcrypt hash) |
| App activity | Crashes & ANRs | Sí | Optional (Sentry) | Sentry (procesador) | Sentry SDK |
| App activity | Diagnostic logs | Sí | Optional (Timber → Sentry breadcrumbs) | Sentry | Local + Sentry |
| Audio | Microphone | No (Kapibaya post v1.0) | — | — | — |
| Photos / Video | — | No | — | — | — |
| Location | — | No | — | — | — |
| Contacts | — | No | — | — | — |
| Calendar | — | No | — | — | — |
| Health & fitness | — | No | — | — | — |
| Files & docs | — | No | — | — | — |
| Web browsing | — | No | — | — | — |

Notas:
- **Email + password** se envían a Supabase Auth únicamente sobre TLS 1.3 vía
  `supabase-kt v3`. El password nunca se persiste local en el cliente.
- **Sentry** está deshabilitado por defecto (`io.sentry.auto-init = false`); se
  activa solo si `BuildConfig.SENTRY_DSN` no está vacío. En producción la
  decisión final de activar Sentry es del operador.
- **RECORD_AUDIO** está declarado en el manifest pero no se solicita runtime en
  v1.0. Reservado para Dr. Kapibaya en releases futuros.

## Data sharing

- **Pagos**: la app abre el `permalink` del producto en Chrome Custom Tabs;
  el checkout corre en la web (medsurgery.shop con Stripe). Ningún dato de
  pago entra al binary del app.
- **Third-party SDKs en el binary**:
  - `supabase-kt` (Supabase, S.A.) — backend principal.
  - `revenuecat-purchases` (RevenueCat) — gestión de suscripciones (E7+).
  - `coil` (Coil) — image loading; no envía data del usuario.
  - `sentry-android` (Sentry, S.A.) — crash reporting opt-in.
  - `okhttp` (Square) — librería HTTP base.

## Data security (Google Play questions)

- **Data encrypted in transit**: ✅ TLS 1.3 vía OkHttp default + `network_security_config.xml`
  (`usesCleartextTraffic = false`).
- **Data encrypted at rest**: ✅ Supabase Postgres encrypta at rest por defecto;
  el cliente Android no almacena PII en disco fuera de DataStore (que solo
  guarda preferencias: idioma, tema, haptics).
- **Users can request data deletion**: ✅ Profile > Eliminar cuenta dispara la
  Edge Function `process_account_deletions` con grace period de 48 h.
  Cumplimiento GDPR Art. 17 y LFPDPPP Art. 23.
- **Independent security review**: pendiente (E10 follow-up).
- **Follows Families Policy**: N/A (app categorizada como profesional médica,
  no dirigida a menores).

## Account deletion (Google Play policy)

Ruta del usuario:
1. Profile tab → toca "Eliminar mi cuenta".
2. Pantalla AccountDeletionScreen muestra warning + GDPR Art. 17 + checkbox
   "Entiendo que mis datos serán eliminados permanentemente".
3. Botón "Confirmar eliminación" (rojo) llama `process_account_deletions`
   (Edge Function).
4. La sesión local se cierra y el usuario regresa a Login.
5. Backend procesa la eliminación en ≤ 48 h (grace period para revertir).

Endpoint público para Account Deletion (Play Console requiere URL):
`https://www.medsurgery.academy/eliminar-cuenta` (verificar con legal — URL
sujeta a confirmación).

## Permisos solicitados

| Permission | Required | Justificación |
|---|---|---|
| `INTERNET` | ✅ | Supabase + Custom Tabs + Sentry |
| `ACCESS_NETWORK_STATE` | ✅ | OfflineBanner en WebViewScreen (E9) |
| `POST_NOTIFICATIONS` | ✅ | Push opcional post-v1.0 |
| `RECORD_AUDIO` | ⏸️ | Manifest declarado, no runtime en v1.0 (reservado Kapibaya) |

## Follow-ups requeridos antes del Play Console submit

### ⚠️ BLOQUEANTE — URLs legales no existen en producción (2026-05-18)
Confirmado por curl HEAD: las 3 URLs configuradas en `BuildConfig` (Android)
y en `KIRUPlusApp-Info.plist` (iOS) responden **HTTP 404**:
- `https://www.medsurgery.academy/politica-de-privacidad-kiru-pro` → 404
- `https://www.medsurgery.academy/terminos-de-uso-y-licencia-kiru-pro` → 404
- `https://www.medsurgery.academy/politica-de-suscripciones-kiru-pro` → 404

Probadas también 20 variaciones de slugs (con/sin `www`, `/kiru/...`,
`/legal/...`, ES/EN, etc.) — ninguna devuelve 200. El dominio raíz
(`/` y `/kiru`) sí responde 200, lo que confirma que el host está vivo
pero las páginas legales **no han sido publicadas** (o fueron eliminadas).

**Implicación**: la misma 404 puede llegar a aparecer al revisor de Apple App
Review (Build 17 ya submitted al iOS) y a Google Play (cuando se suba el
Android). Es un blocker de compliance para ambas tiendas, no un bug del
cliente.

**Acción requerida — equipo marketing/legal**:
1. Publicar las 3 páginas en `https://www.medsurgery.academy/` con los
   slugs ya configurados, o
2. Comunicar las nuevas URLs canónicas para actualizar BuildConfig + Info.plist
   simultáneamente.

Hasta entonces, **no cambiar las URLs sólo en Android** — desalinearía el
binario respecto a iOS.

### Otros follow-ups
1. Confirmar URL pública del flujo de Account Deletion para el Play Console.
2. Habilitar Sentry solo después de revisar política de retención y opt-in del
   usuario (puede vivir como flag en Settings).
3. Si Health Apps Declaration aplica (categoría "Medical"), preparar el
   formulario adicional con CMCG endorsement y disclaimer legal.

## Bibliografía / standards aplicados

- GDPR Art. 17 — Right to erasure.
- LFPDPPP Art. 23 — Derecho ARCO de cancelación (México).
- Google Play Health Apps policy (categoría Medical).
- Google Play Data safety section (requerido para todas las apps).
