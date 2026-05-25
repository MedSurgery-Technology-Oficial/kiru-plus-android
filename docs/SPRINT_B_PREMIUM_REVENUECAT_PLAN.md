# Sprint B — Premium / RevenueCat Plan

**Estado:** BLOQUEADO — esperando precondiciones externas  
**Rama propuesta:** `android/sprint-b-revenuecat`  (nueva rama a partir de `main` tras merge de Sprint A)  
**Prerequisito:** Sprint A (`android/sprint-a-premium-foundation`) mergeado y revisado  
**Última actualización:** 2026-05-24

---

## Objetivo

Conectar el stub `PremiumModule` de Sprint A a RevenueCat real para que:

1. El estado `EntitlementState` refleje la suscripción activa del usuario.
2. `PremiumGate` bloquee contenido premium de forma fiable.
3. El usuario pueda comprar una suscripción y restaurar compras.
4. El sistema degradue con gracia si RevenueCat falla (nunca concede acceso, nunca crashea).

Sprint B **no toca** iOS, Supabase, Edge Functions, App Store Connect ni RevenueCat iOS.

---

## Precondiciones externas — TODAS deben resolverse antes de escribir código

| # | Bloqueo | Quién actúa | Entregable esperado |
|---|---------|-------------|---------------------|
| B1 | Crear app Android en RevenueCat dashboard | Dr. Huerta | API key con prefijo `goog_` |
| B2 | Google Play Console — Developer Account activo | Dr. Huerta | Acceso a Play Console |
| B3 | Crear suscripción(es) en Play Console | Dr. Huerta | Product IDs (e.g. `kiru_premium_monthly`) |
| B4 | Crear Service Account JSON de Play Console | Dr. Huerta | JSON file — NO commitear; guardar en `local.properties` o secretos de CI |
| B5 | Espejo RevenueCat — Products / Entitlements / Offerings | Dr. Huerta | Entitlement ID (e.g. `premium`) configurado en RC |
| B6 | Sentry Android — crear proyecto → obtener DSN | Dr. Huerta | DSN string para `local.properties` y GitHub Secrets |
| B7 | Decisión: qué pantallas serán premium | Dr. Huerta | Lista explícita (ver sección "Pantallas candidatas") |
| B8 | Decisión: texto visible del plan (UI string) | Dr. Huerta | Nombre comercial visible (e.g. "KIRU+ Premium", "KIRU+ Pro") — distinto del nombre técnico `Premium` |

**Regla:** si cualquier precondición falta, Sprint B no arranca. No se hace "implementación parcial con key falsa".

La key `test_uufKoDXUqdeSMclJohjzPKtApVI` es un placeholder iOS inválido para Android. Se trata como no existente. No se usa en ningún código productivo.

---

## Pantallas candidatas para gating premium

Decisión pendiente (B7). Opciones a evaluar:

| Pantalla | Gating propuesto | Notas |
|----------|-----------------|-------|
| Academy (lecciones/quiz) | ✅ Premium | Contenido educativo de alto valor |
| Logbook (bitácora quirúrgica) | ⚠️ A definir | En iOS es premium; en Android podría ser freemium con límite de entradas |
| K-Tools (calculadoras) | ⚠️ A definir | Podría ser parcialmente free (básicas) / premium (avanzadas) |
| Dr. Kapibaya (chat IA) | ⚠️ A definir | En iOS es premium; límite de mensajes posible para free |
| Store (catálogo) | ❌ Free | Navegación al catálogo debe ser libre; compra es el gate |
| Profile / Settings | ❌ Free | Siempre accesible |
| Perlas | ⚠️ A definir | Actualmente sin gating; a confirmar con iOS paridad |

---

## Archivos que se modificarán en Sprint B

| Archivo | Cambio |
|---------|--------|
| `app/src/main/kotlin/.../core/premium/PremiumModule.kt` | Reemplazar stub por binding real a `EntitlementsService` |
| `app/src/main/kotlin/.../core/premium/EntitlementsService.kt` | **Nuevo** — wrapper RevenueCat → `EntitlementState` |
| `app/src/main/kotlin/.../app/AppViewModel.kt` | Exponer `entitlementState: StateFlow<EntitlementState>` |
| `app/src/main/kotlin/.../feature/paywall/PaywallScreen.kt` | Ampliar con fallback si offerings vacíos |
| `app/src/main/kotlin/.../app/nav/KiruNavHost.kt` | Conectar `onOpenPaywall` desde pantallas gateadas |
| `app/src/main/kotlin/.../feature/main/MainScreen.kt` | Pasar `entitlementFlow` a tabs que lo necesiten |
| Pantallas premium (según B7) | Envolver con `PremiumGate` |
| `app/build.gradle.kts` | `REVENUECAT_API_KEY` leerá `goog_` key real de `local.properties` |
| `.github/workflows/android-ci.yml` | Secret `REVENUECAT_API_KEY` actualizado en repo settings (no en código) |

## Archivos que NO se deben tocar en Sprint B

| Archivo / Recurso | Razón |
|-------------------|-------|
| `/Users/dr.huerta.oficial/KIRU+/` (todo el repo iOS) | Aislamiento absoluto |
| Supabase schemas y migraciones | Compartido; cambios requieren coordinación iOS |
| Edge Functions activas | Compartido; sprint B no las necesita |
| App Store Connect | iOS en review; congelado |
| RevenueCat iOS (`appl_` key, productos iOS) | Plataforma separada |
| `local.properties` | Nunca se commitea |
| `kiru-plus-release.jks` / keystore | Nunca se commitea |
| `google-services.json` | Nunca se commitea (si se agrega Firebase) |
| Service Account JSON | Nunca se commitea |
| `app/src/main/AndroidManifest.xml` permisos de audio | RECORD_AUDIO eliminado en Sprint A; no re-agregar |

---

## Arquitectura propuesta (sin implementar todavía)

### `EntitlementsService.kt`

```
class EntitlementsService @Inject constructor() {

    // Emite EntitlementState según CustomerInfo de RevenueCat.
    // Suscribe a customerInfoStream de Purchases para updates en tiempo real.
    val entitlementState: StateFlow<EntitlementState>

    // Dispara getCustomerInfo() y mapea:
    //   customerInfo.entitlements["premium"]?.isActive == true → Premium
    //   de lo contrario → Free
    //   error de red o RC → Error(message)
    //   pendiente → Loading
    fun refresh()

    // Play Billing: restore purchases
    suspend fun restorePurchases(): Result<EntitlementState>
}
```

**Reglas de mapeo:**
- El entitlement ID en RC debe ser exactamente `"premium"` (confirmar con Dr. Huerta al crear RC setup).
- Nunca devuelve `Premium` en caso de error — fail closed.
- `Loading` solo durante el cold-start; RC cachea `CustomerInfo` localmente.

### Flujo de compra

```
Usuario toca "Ver planes"
    → PremiumGate.onUpgrade()
    → navController.navigate(KiruRoute.Paywall)
    → PaywallScreen muestra RevenueCat Paywall Dialog
    → Usuario selecciona plan → Play Billing procesa
    → RC recibe webhook del backend → actualiza CustomerInfo
    → EntitlementsService emite Premium
    → PremiumGate detecta Premium → muestra contenido
```

### Flujo de restore purchases

```
Usuario toca "Restaurar compra" (en PaywallScreen o ProfileScreen)
    → EntitlementsService.restorePurchases()
    → RC llama Purchases.restorePurchases()
    → Mapea resultado → EmitePremium si hay entitlement activo
    → UI actualiza automáticamente vía StateFlow
```

### Comportamiento si RevenueCat falla

| Escenario | Comportamiento |
|-----------|---------------|
| RC SDK no inicializado (key vacía) | `KiruApp` guarda el skip; `PremiumModule` emite `Free`; paywall no se muestra |
| Red no disponible al iniciar | RC usa caché local de `CustomerInfo`; emite último estado conocido |
| Error en `getCustomerInfo()` | `EntitlementState.Error` → tratado como `Free`; log con Timber sin PII |
| Offerings vacíos en Paywall | `PaywallScreen` muestra fallback estático con "Ver planes disponibles" en lugar de crash |
| Play Billing error durante compra | RC maneja internamente; UI muestra mensaje de error genérico |

### Comportamiento sin premium

- `PremiumGate` muestra `PremiumUpgradePrompt` — prompt estático.
- **No hay redirección automática al paywall.** El usuario decide si toca "Ver planes".
- Las tabs del navigation bar siguen visibles — el usuario puede navegar libremente.
- No se ocultan features del menú principal; el gate actúa dentro de la pantalla.

---

## Logging seguro

```kotlin
// Correcto — sin PII
Timber.d("EntitlementState updated: ${state::class.simpleName}")
Timber.w("RC error: ${error.code}")  // error.code es un enum, no contiene datos de usuario

// Prohibido — PII
Timber.d("User ${user.email} purchased ${productId}")  // NUNCA
```

Sentry: `isSendDefaultPii = false` ya activo desde Sprint A. `event.user = null` en `beforeSend`. No agregar breadcrumbs con datos clínicos o de usuario.

---

## Criterio de salida de Sprint B

- [ ] `./gradlew test` — 0 fallos
- [ ] `./gradlew lintDebug` — 0 errores
- [ ] `./gradlew assembleDebug` — BUILD SUCCESSFUL
- [ ] `./gradlew assembleRelease` — BUILD SUCCESSFUL (con signing config)
- [ ] Smoke test manual en emulador: compra de suscripción de prueba → `EntitlementState.Premium` emitido
- [ ] Smoke test manual: restore purchases → estado correcto
- [ ] Smoke test: sin red → app no crashea, `PremiumGate` muestra estado anterior
- [ ] Smoke test: RC key inválida → `KiruApp` guarda skip, app abre sin crash
- [ ] `PremiumGate` probado con `Free` / `Premium` / `Loading` / `Error` — ninguno produce loop
- [ ] iOS `git status` vacío al cierre de Sprint B
- [ ] Ningún secret en `git diff`

---

## Comandos de test/build

```bash
# Tests unitarios
./gradlew test

# Lint
./gradlew lintDebug

# Build debug
./gradlew assembleDebug

# Build release (requiere keystore en local.properties)
./gradlew assembleRelease

# Tests + lint + build en un solo paso
./gradlew test lintDebug assembleDebug
```

---

## Riesgos

| Riesgo | Probabilidad | Mitigación |
|--------|-------------|------------|
| Play Billing API 3-day review delay | Media | Usar cuentas de prueba (license testers) para smoke en emulador |
| RC webhook no llega → entitlement no actualiza | Baja | RC también actualiza al iniciar app vía `getCustomerInfo()`; doble mecanismo |
| `CustomerInfo` cache corrupta | Muy baja | `restorePurchases()` como escape hatch |
| PremiumGate aplicado a pantalla equivocada → Free users bloqueados | Media | Aplicar solo tras aprobación de lista B7; probar en emulador con usuario free |
| goog_ key commiteada accidentalmente | Baja | `local.properties` en `.gitignore`; CI usa GitHub Secrets exclusivamente |
| Google Play rechaza APK por permiso faltante post-Sprint B | Baja | No re-agregar RECORD_AUDIO; BILLING_PERMISSION se agrega vía dependencia RC, no manual |
| iOS App Review afectada por cambio en webhook compartido | Media | Backend congelado durante Sprint B; RC webhook es server-to-server, no cliente |

---

## Notas de naming

- **Nombre técnico (código):** `EntitlementState.Premium`, `isPremium`, `PremiumGate`, `PremiumModule` — estables, no cambian con marketing.
- **Nombre visible en UI (strings):** pendiente decisión B8. Placeholder actual: "KIRU+" sin calificador de tier.
- **Entitlement ID en RevenueCat:** debe acordarse antes de crear el producto — una vez creado en RC no se puede renombrar sin recrearlo. Recomendación: `"premium"` (minúsculas, sin espacios).
- **Product ID en Play Console:** sugerencia `kiru_premium_monthly` / `kiru_premium_annual` — siguiendo convención `app_tier_period`.
