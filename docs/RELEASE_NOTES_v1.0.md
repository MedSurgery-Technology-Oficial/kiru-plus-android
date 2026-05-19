# KIRU+ Android — Release Notes v1.0.0

**Build:** 1  
**AAB:** `app/build/outputs/bundle/release/app-release.aab` (29 MB)  
**Date:** 2026-05-19

---

## What's new in v1.0.0

### Core app
- Full Material 3 design system matching iOS brand identity (KIRU Navy Blue, Surgical Red, Inter/Roboto typography)
- Adaptive dark/light theme with system-follow option
- Splash screen with KIRU+ logo
- Per-app language selector (Español / English / System)
- Haptic feedback toggle
- Anonymous crash reporting via Sentry (opt-in)

### Authentication
- Email + password sign-up and sign-in via Supabase
- Email confirmation flow
- Forgot password (recovery link via email)
- Account deletion with 48-hour grace period (Google Play requirement)
- GDPR data export request

### Medical Disclaimer
- Scroll-to-end gate before accepting
- Inline links to Privacy Policy, Terms of Use, and Subscription Policy

### Home
- Quick actions to all major features
- Academy, Logbook, Store, K-Tools, Dr. Kapibaya shortcuts

### Academy
- Lessons list (Supabase-backed, real-time)
- Lesson detail with rich text
- Quizzes by specialty (multiple-choice, explanation on reveal, score screen)
- Clinical Pearls with detail view

### Surgical Logbook
- Add surgical log: procedure picker, date, complexity, outcome, notes
- Swipe-to-delete with confirmation
- Supabase persistence with RLS per user

### Store
- Product catalog (Supabase-backed)
- Product detail with hero image
- Purchase via Stripe Checkout in Chrome Custom Tab (Google Play compliant)

### K-Tools — 10 offline clinical calculators
- **Alvarado Score** — appendicitis probability
- **AIR Score** — appendicitis inflammatory response
- **ASA Physical Status** — anesthesia risk
- **Child-Pugh Score** — hepatic function (Class A/B/C)
- **BISAP Score** — pancreatitis severity (5-point)
- **SOFA Score** — multi-organ failure (6 organ systems)
- **APACHE II** — ICU mortality prediction
- **Marshall Score** — organ failure in pancreatitis
- **RIPASA Score** — appendicitis (includes 0.5-pt items)
- **p-POSSUM** — surgical morbidity/mortality (logistic regression)

All calculators are fully offline, no network required.

### Dr. Kapibaya
- Streaming SSE chat via Supabase Edge Function
- Typing bubble and streaming content bubble
- Error recovery with retry

### Legal
- Privacy Policy, Terms of Use, Subscription Policy via in-app WebView
- Offline banner and error state
- GDPR Art. 15 data portability, GDPR Art. 17 right to erasure

---

## Known limitations in v1.0
- RevenueCat paywall (subscription management) not yet active — requires Google Play Developer Account and `goog_xxx` API key
- Dr. Kapibaya conversation memory requires active internet; no offline cache
- Academy content requires internet; no offline download

---

## Build verification
```
jarsigner -verify app/build/outputs/bundle/release/app-release.aab
# → jar verified.
# Signer cert expires 2053-10-04
```
