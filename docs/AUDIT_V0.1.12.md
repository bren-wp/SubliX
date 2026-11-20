# SubliX v0.1.12 — source security & dead-code review

## Scope and method

Reviewed the production Android Kotlin tree (16 source files), the Android manifest and
build flavors, debug/release preview entry points, Android instrumentation/unit suites,
Gradle and GitHub Actions jobs, static website JavaScript, Play media generation and
offline import/export paths. Source-level findings below were checked against their
call sites and corrected in this PR. The new `scripts/audit_release.py` fails the
web CI job on a defined set of important regressions; Android Lint is run for both
release variants. This is a **repository-wide static review** — not a substitute
for runtime penetration testing, a CVE advisory scan, independent code review or
physical-device accessibility testing.

## Findings remediated

| ID | Severity | Finding | Fix / evidence |
| --- | --- | --- | --- |
| UX-01 | Medium | Free showed unavailable Pro features, an edition comparison and an explanation about publicly released APKs. | Free now opens a Getting started guide instead. Budget shows read-only recurring totals and Savings lists high-cost plans. Pro retains editable budgets, usage controls, forecasts, CSVs, category budgets and scenario planning. |
| SEC-01 | Medium | Backup decoder silently truncated category/note fields and accepted untrusted control characters in IDs/names. | Reject oversized/malformed fields, preserve atomic rollback, add hostile-backup instrumentation tests. |
| SEC-02 | Medium | Database save and direct batch import trusted callers for amount/text/URL validation. | Validate records immediately before SQLite write, inside the transaction. Verify a malformed second entry rolls back the whole import. |
| SEC-03 | Medium | Reminder notifications could expose service names and expected amounts on the lock screen. | Use private notification visibility and a redacted public version. Android user privacy controls still apply. |
| SEC-04 | Low | Reminder WorkManager job was queued even when disabled. | Schedule only when enabled; cancel unique work on disable; cache configured reminder days per run. |
| SEC-05 | Low | Release builds could log database exception stack traces. | Only log detailed DB errors when BuildConfig.DEBUG is true. User-facing errors remain generic. |
| SEC-06 | Medium | Modern OS data extraction/transfer exclusions were not explicitly declared. | Add android:dataExtractionRules and exclude the app root from cloud and device-transfer extraction, retain allowBackup=false and no INTERNET permission. |
| PERF-01 | Low | Subscription ViewModel did not explicitly release its SQLite helper. | Close on onCleared. |
| DEAD-01 | Low | Pro-only composable included a branch for a non-Pro edition that cannot reach it in normal navigation. | Remove the unreachable flavor check and paywall text. |
| DEAD-02 | Low | ProAnalytics.budgetOverage had no production call sites. | Remove the unused helper and its redundant unit assertion. |
| DEAD-03 | Low | Reminder screen created an unused SharedPreferences reference. | Remove the unused local reference. |
| QA-01 | Medium | Free/Pro navigation difference had no explicit regression coverage. | Test absence of Pro navigation in Free and presence of Pro forecast in Pro, plus source contract assertions. |

## Existing controls verified in source

- Offline app permissions: notification permission only; no INTERNET permission, account sign-in, web API client or ad SDK in declared app dependencies.
- Separate Free and Pro package IDs and Android private data sandboxes; no implicit cross-app database sharing.
- User-initiated SAF import/export; 2MB / 5000-entry JSON decode caps; parameterized SQLite queries, transactional writes and rollback.
- Optional HTTPS management URLs validated before ACTION_VIEW; not fetched by SubliX.
- CSV export escapes formula-like leading characters and doubles quotes; user-facing text warns that exported files are unencrypted.
- Debug screenshot fixtures are behind BuildConfig.DEBUG and are no-ops in release source.
- CI validates two APK variants, two AAB variants, store metadata, native screenshot provenance and RGB differences separately from visual design approval.

## Open risks and release blockers

1. JSON backups, Pro CSV exports and local SQLite records are not encrypted by SubliX; they rely on the device OS sandbox and the user's chosen export destination. Do not advertise encryption at rest or biometric protection.
2. A separate Pro APK distributed publicly has no entitlement/license enforcement. The UI is differentiated, but a downloaded public APK is not proof of payment. A future paid distribution model requires a legitimate store/license design.
3. Transitive dependency CVE review, SBOM, runtime exploit/penetration testing, supply-chain provenance of signing keys, Android Lint findings and production signature verification still require independent confirmation.
4. Physical devices, notification Doze behavior, TalkBack, keyboard/rotation, large datasets, multi-language review and all dialogs remain manual QA items.
5. Pixel-perfect 1:1 design signoff is **NOT VERIFIED**. Historical Android PNGs are regression references, not matching-viewport, approved source designs. Keep the report's REVIEW_REQUIRED flags.
6. A source audit cannot mathematically prove no dead code across generated Compose, WorkManager, Android framework lifecycle, reflection or flavor build variants. Continue build-time shrinking/lint and explicit unused-symbol analysis as part of every release.

## Acceptance

Merge and publish only after Android Free+Pro unit/instrumentation, Android Lint,
source audit, web/Play metadata, 45 native screenshots, 98 adaptive frames and
authentic-media/RGB checks pass on the final source+media revision. Release
signatures and seven artifacts must be verified independently. Do not claim
Google Play acceptance, a production signature or 1:1 visual certification
based on source or emulator checks alone.
