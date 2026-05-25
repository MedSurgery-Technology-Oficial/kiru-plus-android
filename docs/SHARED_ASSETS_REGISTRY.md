# Shared Assets Registry

**Last updated:** 2026-05-24 (updated with 3 clinical JSON assets)  
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
| `curriculum_index_v1.json` | `app/src/main/assets/curriculum/curriculum_index_v1.json` | `Resources/DataBaseInformation/Curriculum/curriculum_index_v1.json` | `e7696d612354f04cdd7098c978ca826e0a943bb635507e9eb034e0f125e94cab` | 2026-05-24 | Copia versionada, sin symlink, sin dependencia viva del repo iOS. Contenido propio MedSurgery. 4 bloques curriculares, 27 KB. |
| `ChapterExams.json` | `app/src/main/assets/curriculum/ChapterExams.json` | `Resources/DataBaseInformation/Curriculum/ChapterExams.json` | `ce5baa9885dfa8c9afb3bfb2a613ab13dbd2aad7850cba31ae6a9fb37dc845d3` | 2026-05-24 | Copia versionada, sin symlink, sin dependencia viva del repo iOS. Contenido propio MedSurgery. 100 exámenes de capítulo con preguntas y rationale, 616 KB. |
| `StudyModules.json` | `app/src/main/assets/modules/StudyModules.json` | `Resources/DataBaseInformation/StudyModules.json` | `0a5a048e7ee515e99da362b463f4eb8a178f22b45deb361bccfb9c33cfa0a8a1` | 2026-05-24 | Copia versionada, sin symlink, sin dependencia viva del repo iOS. Contenido propio MedSurgery. 88 módulos clínicos perioperatorios, 599 KB. |

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
