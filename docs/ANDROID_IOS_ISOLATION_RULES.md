# Android / iOS Isolation Rules

**Last updated:** 2026-05-24  
**Owner:** MedSurgery Technology

---

## Absolute rules

1. **Never modify the iOS repo** (`/Users/dr.huerta.oficial/KIRU+/`) from the Android branch or worktree. The two repos share a Supabase backend but are otherwise independent.
2. **Never copy files between repos without explicit checklist** (see SHARED_ASSETS_REGISTRY.md). Copying without a checksum + intent review is forbidden.
3. **Never commit `local.properties`**. It contains Supabase keys, Sentry DSN, keystore passwords. It is `.gitignore`d. Treat it like `.env`.
4. **Never add secrets to any committed file** — not to `build.gradle.kts`, `strings.xml`, source code, or documentation.
5. **Backend (Supabase) is shared.** Any Edge Function change, schema migration, or RLS policy change affects BOTH platforms simultaneously. Before touching backend: confirm iOS is not in active App Review, notify both platform owners, and add a rollback plan.
6. **RevenueCat** has separate iOS (`appl_` prefix) and Android (`goog_` prefix) API keys. The key in `local.properties` must match the platform. Never use the iOS key in Android code.
7. **Package name** (`com.medsurgery.kiruplus`) must not change without a coordinated Play Console + iOS bundle-ID review.

---

## Repository layout

| Concern | iOS | Android |
|---------|-----|---------|
| Root | `/Users/dr.huerta.oficial/KIRU+/` | `/Users/dr.huerta.oficial/KIRU+ Android/` |
| Language | Swift 6 / SwiftUI | Kotlin 2.1.0 / Jetpack Compose |
| Build system | Xcode 16 / SPM | Gradle KTS / AGP 8.7.3 |
| Min OS | iOS 17 | Android 8.0 (API 26) |
| Git remote | `origin` (separate) | `origin` (separate) |
| Supabase project | `tttxmupjteqpljtfgmgo` | same ← shared |
| RevenueCat key prefix | `appl_` | `goog_` |

---

## Shared backend contracts

- Document every Edge Function call in `API_CONTRACTS.md`.
- If you need to change a function signature, add a **new** function version (e.g., `ask_kapibaya_stream_v2`) rather than breaking the existing one; deprecate the old one only after both platforms ship the new call.
- Schema migrations must be backwards-compatible (additive only) during any active App Review window.
- Never rename or drop tables/columns without a coordinated dual-platform deployment plan.

---

## What IS safe to do on Android independently

- Add/remove Android-only permissions in `AndroidManifest.xml`
- Add Kotlin-only libraries to `app/build.gradle.kts`
- Modify Compose UI, ViewModels, repositories, navigation
- Add Android-only feature flags / BuildConfig fields
- Change `local.properties` (never committed)
- Create or modify GitHub Actions workflows in `.github/`
- Update `docs/` markdown files

---

## Escalation

If a change requires touching the shared Supabase backend, stop and request explicit authorization from the iOS platform owner (Dr. Huerta) before proceeding. Do not deploy Edge Functions, alter RLS, or run migrations under time pressure.
