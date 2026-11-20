# SubliX v0.1.12 — Free/Pro edition clarity and local privacy hardening

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
