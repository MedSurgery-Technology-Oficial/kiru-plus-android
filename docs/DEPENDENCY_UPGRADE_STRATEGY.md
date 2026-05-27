# KIRU+ Android — Dependency Upgrade Strategy

**Fecha:** 2026-05-26  
**Estado:** documentado, pendiente de ejecución  
**Origen:** 54 warnings de `GradleDependency` + 6 de `AndroidGradlePluginVersion` en lintDebug  
**Restricción:** NO ejecutar ningún upgrade hasta que Play Console esté desbloqueado y haya un ciclo de QA disponible.

---

## Versiones actuales (libs.versions.toml — 2026-05-26)

| Componente | Versión actual | Última estable conocida | Riesgo de upgrade |
|---|---|---|---|
| **AGP** | 8.7.3 | 9.2.1 | 🔴 ALTO — major version |
| **Kotlin** | 2.1.0 | 2.1.x | 🟡 medio |
| **KSP** | 2.1.0-1.0.29 | debe coincidir con Kotlin | 🔴 ALTO — acoplado a Kotlin |
| **composeBom** | 2024.12.01 | 2026.05.01 | 🔴 ALTO — 18 meses de lag |
| **lifecycle** | 2.8.7 | 2.10.0 | 🟡 medio |
| **activityCompose** | 1.9.3 | 1.13.0 | 🟡 medio |
| **navigation** | 2.8.5 | 2.9.8 | 🟡 medio — puede tener API breaks |
| **hilt** | 2.51.1 | 2.56.x | 🟡 medio |
| **hiltNavigationCompose** | 1.2.0 | 1.3.0 | 🟢 bajo |
| **datastore** | 1.1.1 | 1.2.1 | 🟢 bajo |
| **browser** | 1.8.0 | 1.10.0 | 🟢 bajo |
| **splashscreen** | 1.0.1 | 1.2.0 | 🟢 bajo |
| **appcompat** | 1.7.0 | 1.7.1 | 🟢 bajo — patch |
| **material** | 1.12.0 | 1.14.0 | 🟡 medio |
| **kotlinxCoroutines** | 1.10.1 | 1.10.2 | 🟢 bajo — patch |
| **supabase** | 3.0.2 | 3.x | 🟡 medio — check changelogs |
| **ktor** | 3.0.3 | 3.x | 🟡 medio — acoplado a Supabase |
| **coil** | 2.7.0 | 3.x | 🔴 ALTO — Coil 3 tiene API breaks |
| **sentry** | 7.20.1 | 8.x | 🟡 medio |
| **revenuecat** | 10.6.1 | 10.x | 🟢 bajo |
| **junitExt** | 1.2.1 | 1.3.0 | 🟢 bajo |
| **espresso** | 3.6.1 | 3.7.0 | 🟢 bajo |

---

## Estrategia por batches (orden de ejecución)

### Batch 0 — Prerequisito: entorno de CI o QA manual disponible
Antes de cualquier upgrade, necesitas:
- [ ] Emulador funcional para `connectedDebugAndroidTest`
- [ ] `./gradlew testDebugUnitTest` como gate mínimo post-upgrade
- [ ] Play Console desbloqueado para validar que el build sube correctamente

---

### Batch 1 — Bajo riesgo (patch bumps y deps sin API changes)
**Hacer juntos, commit único:**
- `appcompat`: 1.7.0 → 1.7.1
- `kotlinxCoroutines`: 1.10.1 → 1.10.2
- `hiltNavigationCompose`: 1.2.0 → 1.3.0
- `datastore`: 1.1.1 → 1.2.1
- `browser`: 1.8.0 → 1.10.0
- `junitExt`: 1.2.1 → 1.3.0
- `espresso`: 3.6.1 → 3.7.0

**Gate:** `testDebugUnitTest` + `assembleDebug`

---

### Batch 2 — Medio riesgo (minor version bumps con posibles cambios de API)
**Un dep a la vez, o juntos si son coherentes:**
- `lifecycle`: 2.8.7 → 2.10.0 (con `activityCompose`)
- `material`: 1.12.0 → 1.14.0
- `hilt`: 2.51.1 → latest (verificar KSP compat)
- `sentry`: 7.20.1 → 8.x (revisar changelogs — puede tener API breaks)
- `splashscreen`: 1.0.1 → 1.2.0
- `navigation`: 2.8.5 → 2.9.8 (revisar si Type-Safe Navigation tiene breaking changes)

**Gate:** `testDebugUnitTest` + `lintDebug` + `assembleDebug`

---

### Batch 3 — Alto riesgo (major bumps que requieren cambios de código)

#### Kotlin + KSP (acoplados — actualizar juntos)
Kotlin 2.1.0 → 2.2.x requiere actualizar KSP a la versión correspondiente.  
KSP version format: `kotlin-version-ksp-patch`, e.g., `2.2.0-1.0.x`.  
Verificar compatibilidad con Hilt antes de actualizar.

#### composeBom 2024.12.01 → 2026.05.01
Este es el upgrade más crítico. 18 meses de Compose BOM implica múltiples Compose compiler updates.  
Pasos:
1. Actualizar solo el BOM, mantener todo lo demás
2. Compilar y resolver deprecations (pueden ser muchas)
3. Correr `testDebugUnitTest` + `lintDebug`
4. Hacer smoke test visual en emulador (todas las pantallas principales)

#### Coil 2.7.0 → 3.x
Coil 3 tiene una API completamente diferente: `AsyncImage` y `rememberAsyncImagePainter` cambian.  
Buscar todos los usos de `coil-compose` en el proyecto antes de actualizar:
```bash
grep -rn "AsyncImage\|rememberAsyncImagePainter\|ImageRequest" app/src/main/kotlin/
```
Planificar la migración con la guía oficial: https://coil-kt.github.io/coil/migrating_to_coil3/

---

### Batch 4 — AGP 8.7.3 → 9.x (sprint dedicado)
AGP 9.x cambia el behavior de varios plugins, la estructura de build variants, y requiere Gradle wrapper update.  
**No mezclar con upgrades de deps** — hacer en sprint separado.  
Pasos:
1. Revisar AGP 9.x release notes y migration guide
2. Actualizar Gradle wrapper a la versión compatible con AGP 9.x
3. Actualizar `agp` en libs.versions.toml
4. Resolver cualquier error de build
5. Verificar que KSP, Hilt y Compose compiler plugins siguen siendo compatibles

---

## Notas adicionales

### supabase + ktor (acoplados)
Supabase BOM incluye versiones de ktor internamente. Al actualizar supabase, NO actualizar ktor por separado — dejar que el BOM de Supabase dicte la versión de ktor para evitar conflictos de `io.ktor:*`.

### Cómo verificar versiones actuales de una dep
```bash
# Ver la versión más nueva disponible en Maven Central
curl -s "https://search.maven.org/solrsearch/select?q=g:io.coil-kt+AND+a:coil-compose&rows=5&wt=json" | python3 -m json.tool | grep "latestVersion"

# O simplemente ejecutar lintDebug y revisar el HTML report:
./gradlew lintDebug
open app/build/reports/lint-results-debug.html
```
