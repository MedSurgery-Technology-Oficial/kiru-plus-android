# Google Play — Internal Testing Upload Guide

## Prerequisites
- Google Play Console account with Developer role
- KIRU+ app created in Play Console (`com.medsurgery.kiruplus`)
- Signed AAB at `app/build/outputs/bundle/release/app-release.aab`

---

## Step 1 — Create the app (first time only)

1. Open [play.google.com/console](https://play.google.com/console)
2. Click **Create app**
3. App name: **KIRU+**
4. Default language: **Spanish (es-419)**
5. App or game: **App**
6. Free or paid: **Free**
7. Accept Developer Program Policies → **Create app**

---

## Step 2 — Complete Store Listing

Go to **Store presence → Main store listing**:

| Field | Value |
|-------|-------|
| App name | KIRU+ |
| Short description | See `PLAY_STORE_LISTING.md` |
| Full description | See `PLAY_STORE_LISTING.md` |
| App icon | 1024×1024 PNG (KIRU+ logo) |
| Feature graphic | 1024×500 PNG |
| Screenshots | At least 2 phone screenshots |
| Category | Medical |
| Email | review@medsurgery.academy |
| Privacy Policy URL | https://www.medsurgery.academy/politica-de-privacidad-kiru-app |

---

## Step 3 — App Content declarations

Go to **Policy → App content** and complete:

- **Privacy Policy** — enter URL
- **Ads** — No ads
- **App access** — Some functionality requires a sign-in → provide test credentials:
  - Email: `review@medsurgery.academy`
  - Password: *(see 1Password: "Google Play review account")*
- **Content rating** — complete IARC questionnaire (Medical, no violence/gambling/etc.) → should get **Everyone**
- **Target audience** — Adults (18+), health professionals
- **Data safety** — complete the form:
  - Data collected: Email address (account management)
  - Data shared: No third-party sharing
  - Security practices: Data encrypted in transit (TLS); user can request deletion

---

## Step 4 — Upload the AAB (Internal Testing)

1. Go to **Testing → Internal testing**
2. Click **Create new release**
3. Upload `app/build/outputs/bundle/release/app-release.aab`
4. Release name: `1.0.0 (1)`
5. Release notes:
   ```
   es-419:
   Primera versión interna de KIRU+.
   Calculadoras K-Tools offline, logbook quirúrgico, Dr. Kapibaya streaming y Academia.
   
   en-US:
   First internal release of KIRU+.
   Offline K-Tools calculators, surgical logbook, Dr. Kapibaya streaming chat, and Academy.
   ```
6. Click **Save** → **Review release** → **Start rollout to Internal testing**

---

## Step 5 — Add Internal Testers

1. Go to **Testing → Internal testing → Testers**
2. Create a tester list (e.g., "MedSurgery Team")
3. Add emails of team members
4. Share the opt-in link with testers

Testers install via the Play Store opt-in URL (not sideloading).

---

## Step 6 — Monitor

- Check **Android vitals** after 24h for crash rate
- Check **Pre-launch report** (Google runs automated tests on Firebase Test Lab)
- Fix any reported issues before promoting to **Closed testing (Alpha)** or **Open testing (Beta)**

---

## Keystore safety checklist

Before uploading to Play Console, confirm:

- [ ] `kiru-plus-release.jks` is backed up in 1Password (vault: KIRU+ / Android Release Keystore)
- [ ] `local.properties` is backed up in 1Password (NOT committed to git)
- [ ] Play App Signing is enrolled (Google manages distribution key; your upload key is `kiru-plus-release.jks`)
- [ ] Keystore password is stored in 1Password: `6w7ZjarDRMVnTX4XoIl-NQ`

**WARNING:** If the upload keystore is lost and Play App Signing is NOT enrolled, the app cannot be updated. Enroll in Play App Signing on first upload.

---

## Play App Signing enrollment (recommended)

When uploading the first release:
1. Play Console prompts you to opt into **Play App Signing**
2. Accept → Google generates a distribution key separate from your upload key
3. Your `kiru-plus-release.jks` becomes the upload key only
4. Google's distribution key signs APKs delivered to devices

This is strongly recommended: if your upload key is ever compromised, you can rotate it. You cannot rotate if not enrolled.

---

## Promoting to Production

Once internal testing passes:
1. Internal → **Closed testing** → alpha track → gather feedback
2. Closed → **Open testing** → public beta
3. Open → **Production** → staged rollout (10% → 50% → 100%)

Minimum requirement for production: fill in all required store listing fields + Data Safety section approved.

---

## Pending before production

- [ ] RevenueCat Android SDK configured (`goog_xxx` API key)
- [ ] Play Console Developer Account fully verified
- [ ] Service Account JSON for RevenueCat Play billing validation
- [ ] At least 20 internal testers sign off
- [ ] No P1/P2 crashes in Android Vitals
