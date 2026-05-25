# Shared Assets Registry

**Last updated:** 2026-05-24  
**Owner:** MedSurgery Technology

> Tracks any asset (image, font, JSON, audio) that was intentionally ported from iOS to Android.  
> **Never copy an asset without adding an entry here.** This file is the audit trail.

---

## How to add an entry

1. Copy the file into the Android repo.
2. Compute SHA-256: `shasum -a 256 <file>`
3. Add a row to the table below.
4. Commit the asset AND this file in the same commit.
5. Note any legal restrictions (e.g., licensed font, stock photo).

---

## Registry

| Asset | Android path | iOS source path | SHA-256 (Android copy) | Ported date | Notes |
|-------|-------------|-----------------|------------------------|-------------|-------|
| *(no entries yet)* | | | | | |

---

## Assets intentionally NOT ported

| Asset type | Reason |
|------------|--------|
| KIRU+ logo SVG/PNG | Embedded as Android vector drawable (`ic_launcher` + `logo_kiru`) — re-exported from design system, not copied from iOS bundle |
| App icons | Generated via Android Studio Image Asset Studio from master PNG; not a copy of iOS `AppIcon.appiconset` |
| Supabase curriculum JSON | Fetched at runtime from Supabase Storage; not bundled locally in Android |
| ElevenLabs voice samples | Not present in either platform yet (Sprint E) |

---

## Checklist before copying any iOS asset

- [ ] Is the asset licensed for redistribution? (fonts, stock images)
- [ ] Does the Android build system need a format conversion? (e.g., PDF vector → AVD XML)
- [ ] Does the asset contain PII or patient data?
- [ ] Has SHA-256 been verified against the iOS source after copy?
- [ ] Is the entry added to this registry?
