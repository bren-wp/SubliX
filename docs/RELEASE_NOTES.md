# SubliX v0.1.14 — payment integrity and Android build update

## Free and Pro: reliable payment history
- Reject confirmations based on stale subscription records after the service name, amount, billing cycle or renewal anchor day has changed.
- Roll back both the attempted payment history entry and the renewal update in one SQLite transaction if the on-screen data is stale. Refresh the plan and retry.
- Three new real Android SQLite regression tests cover stale amount, schedule and service name; the amount test also verifies that the refreshed plan can still be paid correctly.
- Existing user records and database schema remain intact. Free and Pro package IDs are unchanged.

## Build and compliance
- Android Gradle Plugin updated from 9.5.1 to 9.5.2; versionCode is 15 for both standalone applications.
- Free remains a zero-cost, ad-free offline app with no Billing SDK, ads, account requirement or Pro upsell. Pro remains a separate offline app with advanced local tools.
- Play listing notes are updated for both applications in English and Croatian.
- Play Console price, Data safety, permanent signing identity and actual Google Play approval require authorized external verification.

## QA and distribution limits
- PR #22 Android/web and Visual QA checks passed on the merged code prior to this version bump. A **fresh v0.1.14 capture** of 45 standard and 135 adaptive real Android frames was generated and published by [screenshot workflow #44973895921](https://github.com/bren-wp/SubliX/actions/runs/44973895921) at screenshot commit `b52778c`. The final code+media head must still pass independent Android/web and Visual QA checks; the screenshot bot's `action_required` check markers do not count as passing CI.
- Automated RGB capture integrity is not signed-off pixel-perfect 1:1 equivalence with approved masters.
- The release workflow uses a temporary per-run CI key when permanent signing secrets are absent. Such artifacts must be explicitly labeled **TEST ONLY**, cannot update existing Play installations, and are not valid production Google Play uploads. Never uninstall an older app containing local data just to install an incompatible preview.
- SHA-256 checksums, both APKs/AABs, Play assets and source archive are required for the GitHub release.

---
# SubliX v0.1.13 — robust Pro budgets and all-screen adaptive QA

## Free edition: 0 EUR, no advertising, no purchases

- Free is intended to be distributed at **0 EUR** without ads, purchases, in-app subscriptions, paid unlocks or promotion of Pro as a required upgrade. All listed Free tools remain independently usable without a paid account.
- Updated Free Google Play metadata in English and Croatian. An explicit `play-store/free/free-policy.json` contract and `scripts/check_free_play_policy.py` block in-app sales messaging, forbidden ad/billing integrations and misleading store descriptions.
- The release and PR checks also scan the **resolved Free runtime dependency graph**, catching transitive ad/billing SDKs omitted from the primary Gradle file. No `INTERNET`, `AD_ID`, or billing permission is declared.
- Updated pinned Android dependencies: Compose BOM 2026.11.01, Core 1.19.1, Activity 1.13.0, Lifecycle 2.11.0, WorkManager 2.12.0, AndroidX Test, AGP 9.5.1, Gradle 9.6.0, Kotlin Compose plugin 2.4.21, Pillow 13 and CairoSVG 2.9.1. Compile SDK 37.1, target API 36 and min API 26.
- **Google Play Console pricing, Ads declaration, Data safety, privacy-policy URL and permanent production signing still require verification in the developer account**. A passing repository check is not Play Store approval. See `docs/PLAY_FREE_COMPLIANCE.md`.

## User-visible improvements
- Pro category budgets are now stored with unambiguous per-category keys. Existing saved budgets remain readable where their historic key is unique; possible legacy hash collisions are explicitly flagged for confirmation.
- Pro forecast month choice, what-if selections and unsaved category-limit amounts persist after activity recreation.
- Forecast, category-limit actions and price-history cards reflow at large system font sizes and narrow widths; button tap targets remain at least 48dp.
- Notifications and Privacy screens accurately explain the redacted lock-screen preview. Both Free and Pro remain private and offline, with no upgrade or paywall navigation inside Free.

### Verified screenshot publication

- [Genuine Free/Pro capture and publication](https://github.com/bren-wp/SubliX/actions/runs/44954802899): **PASS**. Published all 45 standard native screenshots for v0.1.13, synchronized website and Play PNGs, and validated 90 larger-font and 45 tablet/landscape QA-only frames.
- `CAPTURE_VERSION=0.1.13` on screenshot media commit `cbc7847`.
- This documentation commit triggers independent Android/web and Visual QA checks on the **combined final source and published media**, rather than treating the screenshot bot's `action_required` checks as successful.
- **Design 1:1 remains REVIEW_REQUIRED** absent independently approved same-viewport visual masters and physical-device tests.

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
