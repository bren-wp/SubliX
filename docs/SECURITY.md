# Security & privacy design

SubliX Free and Pro do not request banking credentials or require account registration. Their SQLite records stay inside each edition's Android application sandbox. No INTERNET permission, third-party analytics library or tracking SDK is declared in the initial Android manifest. Automatic Android backup of private app data is disabled; migration is a user-initiated JSON export/import.

## Threat model
- Local records and manually exported backups can expose private spending patterns.
- An attacker with access to an exported backup can read unencrypted JSON.
- A malicious import file might contain oversized or invalid data. Import enforces a 2MB limit, 5,000-entry count, numeric bounds and date validation, and merges by stable IDs.
- Device compromise, malicious keyboard, screenshots or third-party backup software are outside the app's threat boundary.
- Notifications may expose a subscription name and amount on the lock screen, subject to Android settings.
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
