# Security & privacy design

SubliX Free and Pro do not request banking credentials or require account registration. Their SQLite records stay inside each edition's Android application sandbox. No INTERNET permission, third-party analytics library or tracking SDK is declared in the initial Android manifest. Automatic Android backup of private app data is disabled; migration is a user-initiated JSON export/import.

## Threat model
- Local records and manually exported backups can expose private spending patterns.
- An attacker with access to an exported backup can read unencrypted JSON.
- A malicious import file might contain oversized or invalid data. Import enforces a 2MB limit, 5,000-entry count, numeric bounds and date validation, and merges by stable IDs.
- Device compromise, malicious keyboard, screenshots or third-party backup software are outside the app's threat boundary.
- Notification details appear in the shade when the device is unlocked. The app supplies a redacted public version for the lock screen; user and device notification privacy settings still determine display behavior.
- Separate Pro APK source code does not constitute anti-piracy protection; no client-only licensing scheme should be claimed secure.

## Hardening work before 1.0 stable
- Full manual security audit and Android lint; tests for SQLite migrations and malformed backups.
- Confirm notifications on real devices in Doze/battery saver mode, document approximation.
- Consider opt-in encrypted backups with secure key handling instead of naming current JSON encryption.
- Test TalkBack, color contrast, large fonts, rotation, locale/date edge cases.
- Make app build reproducible and publish SBOM and dependency vulnerabilities review.
- Audit web accessibility and ensure hosted privacy policy reflects actual infrastructure.
- Create real on-device screenshots; compare per-screen references and approve changes.

## Reporting
Report potential vulnerabilities privately to BRENDIGO using the verified contact method on https://brendigo.com. Do not post private subscriber records in public GitHub issues.

## v0.1.3 local Pro extensions
- Price history records changes to local subscription amounts only. It never reads banking accounts and it has no billing/checkout capability.
- Category budgets are stored as local preferences. Pro CSV reports are user-triggered through Android's document picker; they may contain sensitive personal spending information and are **not encrypted**.
- Export logic prefixes spreadsheet-formula-looking values with apostrophes and quotes CSV fields to reduce spreadsheet injection risk. Additional malformed-input testing remains necessary.
- User-visible cancellation savings are scenarios only; they do **not** attempt to cancel plans at any provider.
- Separate Free and Pro apps remain separate sandboxes; exports/imports must remain user initiated.

## v0.1.12 source audit and fixes

See [v0.1.12 security/dead-code review](AUDIT_V0.1.12.md).
The current version rejects malformed/truncated JSON import fields and validates
every SQLite write; records are updated transactionally. Notifications have a
redacted public/lock-screen version, optional reminder work is unscheduled when
disabled, release builds omit database exception traces, and modern OS backup
and transfer extraction are disabled. These controls do not make unencrypted
local JSON/CSV exports confidential once saved outside the app sandbox.
No independent penetration test, full third-party vulnerability scan or
physical-device privacy certification has been completed.

## v0.1.13 — Free advertising and billing release contract

The standalone Free app is designed for **0 EUR**, with **no ads**, **no in-app purchases**, **no paid subscription to SubliX**, **no account requirement**, and **no Pro upgrade or paywall prompts**. The Free Android manifest does not request `INTERNET`, `AD_ID`, or Google Play Billing permissions; declared Gradle dependencies contain no advertising or billing SDK.

The automated `scripts/check_free_play_policy.py` gate validates source metadata, Free navigation, store descriptions and known monetization dependencies. This static validation does not prove the final merged AAB contains no unknown transitive SDK, or set the actual Play Console pricing/ads declaration. Before publishing, inspect the final merged Free manifest, runtime dependency tree and AAB, then confirm **Free / EUR 0**, **Ads: No**, **no in-app products**, accurate **Data safety**, and the public privacy-policy URL in Google Play Console.

See [Google Play Free compliance checklist](PLAY_FREE_COMPLIANCE.md).

## Post-v0.1.13 transactional payment integrity (v0.1.14 candidate)

Previously, a renewal confirmation compared only the subscription ID and
`next_due`. Editing a subscription's price, name, billing cycle or anchor day
without changing the renewal date could let an old screen snapshot record
an incorrect payment amount/name or advance the wrong schedule.

The payment update now requires the stored name, amount, billing cycle and
anchor day to match the selected record as well as its ID and renewal date.
If any billing detail changed, the SQLite transaction rolls back **both**
the already-attempted payment history insert and the renewal update. This
does not alter the database schema or existing data.

Three Android SQLite instrumentation regressions cover stale amount, stale
recurrence/anchor and renamed service. Passing these regressions in the final
GitHub Actions run remains an acceptance gate, not a claimed result here.

The published v0.1.13 release describes its APK/AAB signature as a temporary
CI preview key. Its artifacts are **not** permanent-key Google Play updates.
Before any production release, verify the correct upload signing certificate
against the existing Free and Pro Play App Signing identities.
