# KIRU+ Android — Play Store Screenshot Guide

## Overview

Google Play requires at minimum 2 screenshots and allows up to 8 per device type. All screenshots must meet Google's quality bar: no blurry, dark, or cropped content. Use a physical device or high-DPI emulator for best quality.

---

## Technical Specs

### Phone Screenshots
| Property | Requirement |
|---|---|
| Aspect ratio | 9:16 or 9:20 (portrait), or 16:9 (landscape) |
| Minimum size | 1080 × 1920 px |
| Recommended size | 1080 × 2400 px (modern tall phone) |
| File format | PNG or JPG |
| Max file size | 8 MB per screenshot |
| Quantity | 2–8 screenshots |

### 7-inch Tablet Screenshots (optional but recommended)
| Property | Requirement |
|---|---|
| Aspect ratio | ~4:3 |
| Minimum size | 1200 × 1920 px |
| File format | PNG or JPG |

### 10-inch Tablet Screenshots (optional)
| Property | Requirement |
|---|---|
| Minimum size | 1600 × 2560 px |
| File format | PNG or JPG |

---

## How to Take Screenshots

### Option A — Android Emulator (recommended for consistency)

1. Open Android Studio → Device Manager → Launch **Pixel 7 Pro** or **Pixel 8** at API 35.
2. Set the emulator to **1080 × 2400, 420dpi**.
3. Run the app in **release** mode (or use `assembleDebug` with production data populated).
4. Navigate to each screen below.
5. Use the emulator's camera button or press **Ctrl+S** (Cmd+S on Mac) to capture.
6. Screenshots save to `~/Desktop` or the emulator's screenshot folder.

### Option B — Physical Device

1. Connect a device with USB debugging enabled.
2. Install the release APK: `adb install -r app/build/outputs/apk/release/app-release.apk`
3. Navigate to each screen and press **Power + Volume Down** simultaneously.
4. Transfer screenshots via `adb pull /sdcard/DCIM/Screenshots/`.

### Option C — adb screencap (scriptable)

```bash
adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png ~/Desktop/kiru_screen_01.png
```

---

## Required Screens (Priority Order)

### Screenshot 1 — Home Dashboard
**Screen**: `HomeScreen` (bottom nav tab "Inicio")
**What to show**: The full home dashboard with all quick-action cards visible (Dr. Kapibaya, K-Tools, K-CORTEX, Fármacos). Show a logged-in state with a real user name if possible.
**Tips**: Scroll to show cards, ensure status bar is clean (no notification icons). Use Do Not Disturb mode.

---

### Screenshot 2 — Dr. Kapibaya AI Chat
**Screen**: `KapibayaVoiceView` or the Kapibaya chat screen
**What to show**: An active conversation with a meaningful surgical question and a detailed AI response. Example prompt: "¿Cuál es el manejo inicial del trauma abdominal cerrado según ATLS?"
**Tips**: Let a full response load before screenshotting. The response should show structured content with bullet points if possible.

---

### Screenshot 3 — K-CORTEX Case Analysis
**Screen**: K-CORTEX analysis result screen
**What to show**: A completed analysis for a clinical case — differential diagnosis, severity score, management recommendations clearly visible.
**Tips**: Use a case with rich output so the screen looks full. Scroll to a compelling section before capturing.

---

### Screenshot 4 — K-Tools Calculator Menu
**Screen**: `KToolsScreen` (bottom nav tab "KTools" or "Calculadoras")
**What to show**: The full grid/list of all 9 calculators with their icons and names visible.
**Tips**: Ensure all calculator tiles are visible without scrolling if possible.

---

### Screenshot 5 — Calculator Result
**Screen**: Any calculator result screen (SOFA recommended — most visual)
**What to show**: A completed SOFA score calculation with the 6 system breakdown and total score prominently displayed. Include the severity interpretation label.
**Tips**: Enter realistic values (not all zeros) to make the score meaningful (e.g., total SOFA 8 = high risk).

---

### Screenshot 6 — Fármacos / Drug Reference
**Screen**: Drug reference or formulary screen
**What to show**: A drug detail page with dosing, indications, and surgical relevance clearly laid out.
**Tips**: Pick a common perioperative drug (e.g., cefazolin, metronidazol, ketorolaco) for recognizability.

---

### Screenshot 7 — Surgical Logbook
**Screen**: `LogbookScreen` (bottom nav tab "Logbook")
**What to show**: A list of logged surgical procedures with dates, procedure names, and complexity indicators. At least 4–5 entries populated.
**Tips**: Pre-populate with realistic dummy entries for the screenshot session. Use apendicectomía, colecistectomía laparoscópica, hernioplastia as entries.

---

### Screenshot 8 — Academy / Quiz
**Screen**: Academy lesson list or quiz screen
**What to show**: Either a lesson category list (trauma, hepatobiliar, etc.) or an active quiz question with options.
**Tips**: The quiz option with 4 choices and a "Confirmar" button photographs well. Use a question about ATLS or pancreatitis.

---

## Screenshot Preparation Checklist

Before capturing any screenshot:

- [ ] Device in **Do Not Disturb** mode (no notification badges)
- [ ] Battery at a neutral level (avoid showing low battery icon)
- [ ] Time set to a clean value like **9:41** (Apple/Google convention) or hide status bar via developer options
- [ ] App is logged in with a real or realistic test account
- [ ] No debug overlays visible (disable "Show layout bounds", "GPU rendering")
- [ ] Screen brightness at maximum for vibrant colors
- [ ] App language set to **Spanish (es-MX)** for primary screenshots
- [ ] Run `adb shell settings put global development_settings_enabled 0` to hide dev mode indicators

---

## Post-Processing (Optional)

Google Play allows device frames via the Play Console upload interface. Alternatively:

- Use **Figma** or **Canva** to place screenshots inside a Pixel 8 device mockup
- Add a 1-line caption at the top or bottom of each screenshot (max 30 chars) in Montserrat Bold, white on navy (#0A1628) banner
- Export at 1080 × 2400 (or 1080 × 2160 with banner)

Example captions:
1. "Tu academia quirúrgica con IA"
2. "Dr. Kapibaya — Tutor 24/7"
3. "Análisis clínico inteligente"
4. "9 calculadoras offline"
5. "SOFA, APACHE, Child-Pugh y más"
6. "Referencia farmacológica quirúrgica"
7. "Registra cada procedimiento"
8. "Aprende con casos reales"

---

## Feature Graphic (1024 × 500 px)

The feature graphic appears at the top of the Play Store listing on phones. It must be:

- **Size**: exactly 1024 × 500 px
- **Format**: PNG or JPG (no transparency in JPG)
- **No text within 50 px of any edge** (gets cropped on some devices)

**Recommended design**:
- Background: deep navy blue (#0A1628) gradient to dark teal (#0F2A44)
- Center: KIRU+ logo (white, ~300 px wide)
- Below logo: tagline "Academia Quirúrgica con IA" in Montserrat Light, white
- Right side: subtle illustration of a scalpel or anatomical silhouette (low opacity)
- Bottom right corner: "Ver. 1.0 — Mayo 2026" in caption size

---

## App Icon (1024 × 1024 px)

- **Size**: 1024 × 1024 px
- **Format**: PNG, no transparency (Play Console adds the adaptive icon mask)
- **Safe zone**: Keep logo within center 66% (680 × 680 px) to avoid clipping on all adaptive icon shapes
- **Design**: Navy blue (#0A1628) square, white KIRU+ mark centered

---

## Submission Checklist

- [ ] App icon 1024×1024 PNG uploaded
- [ ] Feature graphic 1024×500 PNG/JPG uploaded
- [ ] Minimum 2 phone screenshots uploaded (8 recommended)
- [ ] All screenshots pass Play Console quality bar (no warnings shown)
- [ ] Short description ≤ 80 characters in all active locales
- [ ] Full description ≤ 4000 characters in all active locales
- [ ] Privacy policy URL returns HTTP 200
- [ ] Content rating questionnaire completed (target: Everyone / IARC)
- [ ] Data safety form completed (see PRIVACY_AND_DATA_SAFETY.md)
