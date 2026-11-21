# SubliX v0.1.13 — code, security and UI quality review

## Scope

Incremental source review of both Android editions, the Pro category-budget
preference store, Compose state restoration, all 45 screen routes, screenshot
automation, web and localized store text. The prior broad review is documented
in [v0.1.12 audit](AUDIT_V0.1.12.md).

## Findings addressed

| Area | Issue | Remedy and evidence |
| --- | --- | --- |
| Pro data integrity | The original budget preference key used a 32-bit `String.hashCode`; different category names can collide, causing accidental shared limits. | `CategoryBudgetKeys.current` uses unambiguous URL-safe Base64 of normalized UTF-8 category names. JVM test proves real legacy collision (`ajspybqz`, `olelwpsn`) no longer shares a new key. |
| Existing user data | Replacing hash keys without compatibility handling would lose saved limits. | Read a unique legacy key until the category is next saved; after save remove only the unambiguous legacy key. Ambiguous historical collisions do not auto-assign values to either category: UI exposes the old value for review, without deleting it. |
| Configuration changes | Unsaved Pro category limit inputs, forecast month selection and what-if scenario checked items were not saveable across Activity recreation. | Saveable Compose state, plus a real emulator UI test for a category-budget draft and JVM tests for key stability. |
| Adaptive layout | Pro forecast and price-history rows, and side-by-side budget actions, risk clipping/overlap at 1.3×/2× text or compact displays. | Stack values and actions when space or font scale is constrained; keep 48dp minimum actionable height for budget controls. |
| User privacy copy | Reminder UI said that lock screens could expose the same full renewal information, although private notification previews had been introduced. | Update user-facing Privacy and Reminders explanations to match private/redacted notification behavior. |
| QA coverage/dead steps | Tablet/landscape capture covered only eight routes; a second eight-frame adaptive capture duplicated routes already included at 1.3×. | Delete the duplicate capture branch; extend tablet/landscape QA to all 45 routes. Each capture validates dimensions/PNG integrity and screens are checked for crash dialogs. |

## Automated checks

- JVM tests: category budget key collision, Unicode independence and locale normalization
- Android instrumentation: Free and Pro navigation, input restoration, Pro category draft after activity recreation
- Existing Android Free/Pro unit, release Lint and manifest/security source audits
- Three web pages, CSS color parity, localized Google Play metadata, image synchronization, no Pro upsell in Free
- Four viewport profiles for all 45 routes: 1080×1920 standard, 900×2000 at 1.3× and 2× fonts, and 1600×1200 landscape: **180 original emulator frames**; 45 released + 135 QA-only
- Historical RGB regression metrics for native standard captures, not claimed as approved design reference comparison

## Remaining acceptance limits

A source review and successful emulator checks **cannot prove pixel-perfect 1:1 equivalence** without approved reference artwork at the same exact viewport, density and font state. The available 432×960 SVG concepts are design directions with a different aspect ratio from the native 1080×1920 captures. No cropped, masked or cosmetically edited screenshot should be substituted for this acceptance. Physical phone/tablet testing, TalkBack, all keyboard and dialog states, dependency-vulnerability review, an independent penetration test and Play production signing still require separate evidence.

Subscription records and manual exports remain local; JSON/CSV exports are **unencrypted**. Category limits in old versions with colliding hashes may have already overwritten each other; the original association cannot be recovered programmatically. The v2 scheme prevents future collisions and surfaces ambiguous historic values for review rather than guessing.
