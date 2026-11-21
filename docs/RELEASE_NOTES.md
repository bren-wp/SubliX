# SubliX v0.1.13 — robust Pro budgets and all-screen adaptive QA

## User-visible improvements
- Pro category budgets are now stored with unambiguous per-category keys. Existing saved budgets remain readable where their historic key is unique; possible legacy hash collisions are explicitly flagged for confirmation.
- Pro forecast month choice, what-if selections and unsaved category-limit amounts persist after activity recreation.
- Forecast, category-limit actions and price-history cards reflow at large system font sizes and narrow widths; button tap targets remain at least 48dp.
- Notifications and Privacy screens accurately explain the redacted lock-screen preview. Both Free and Pro remain private and offline, with no upgrade or paywall navigation inside Free.

## Technical validation
- Added JVM unit tests for historical hash collisions, Unicode category names and locale-independent new keys.
- Added Pro Activity recreation test for an unsaved category budget draft.
- Android, web, Free/Pro instrumented tests, Lint, security source audit and screenshot/pixel QA must pass on the final branch head before merging.
- Capture all 20 Free + 25 Pro routes at standard 1080×1920, 1.3× and 2× fonts at 900×2000, and 1600×1200 tablet/landscape: **180 real emulator frames**. Publish only native standard captures; the others are QA artifacts.
- No signed-off 1:1 comparison against approved same-viewport design masters, physical TalkBack test or external penetration test. Exported backups remain unencrypted.

---
## SubliX v0.1.12 — Free/Pro edition clarity and local privacy hardening

## Android Free
- Replaced all in-app Pro upgrade/purchase notices with useful Free-only guidance.
- Getting started offers renewal, reminder, backup and provider-cancellation tips.
- Monthly budget provides a readable spending overview, and Savings insights lists the most expensive active plans without requiring Pro.

## Android Pro
- Pro-only toolkit retains forecasts, editable spending limits, advanced sorting/filtering, usage patterns, hypothetical savings, price history and CSV exports.
- Removed unreachable flavor/premium warning logic.

## Security, reliability and maintenance
- Malformed or oversized JSON backup fields are rejected, never silently truncated. Records are validated again at the SQLite write boundary; failed batch imports roll back.
- Renewal notifications redact private spending details on locked devices; disabled reminders do not keep scheduling periodic work.
- Modern Android data extraction rules explicitly exclude the application data sandbox from backups/transfers, in addition to allowBackup=false.
- Production database error stacks no longer appear in logcat; SQLite helper is closed with its ViewModel.
- Removed unused helper and unused reminder preferences reference; new security regression tests, flavor navigation tests, static audit and Android Lint gates.
- Free and Pro remain completely separate package IDs and local data sandboxes.

## Delivery and limitations
- Both APKs, AABs, Play media, source ZIP and checksums are released only after passing source+media CI and signing verification.
- Exported JSON and CSV remain **unencrypted**, and any publicly downloadable Pro APK has no purchase entitlement enforcement.
- **Pixel-perfect design approval NOT VERIFIED.** Emulator RGB regression checks are not an approved 1:1 reference comparison. Manual accessibility, physical-device checks and independent security review remain outstanding.

Audit: [docs/AUDIT_V0.1.12.md](docs/AUDIT_V0.1.12.md)
