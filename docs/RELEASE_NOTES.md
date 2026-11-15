# SubliX v0.1.8 — safer management links, backup validation and accessible Pro selections

## Changes
- Validate subscription management URLs consistently in the editor, local JSON backup import and external-link action. Reject invalid HTTPS URLs, whitespace/control characters, embedded credentials and malformed authorities.
- Preserve user data during invalid JSON imports. An invalid management URL causes backup decoding to fail before an import transaction begins; it does not modify existing subscriptions or payment/price history.
- Show a success message after archive restoration only when the local SQLite operation has completed successfully, with an error message if it fails.
- Improve the Pro what-if savings planner: one accessible checkbox/toggle action per subscription rather than nested click handlers.
- Keep the Free/Pro apps entirely offline-first with separate storage and existing Android application IDs.
- Add URL-policy JVM regression tests and Android SQLite/backup tests for valid and invalid management URLs.
- Maintain 20 Free and 25 Pro authentic emulator screenshot checks, full RGB regression audit, and real Google Play media verification.
- Move both Android editions to `versionName=0.1.8`, `versionCode=9`. No database schema migration is needed.

## Release and QA requirements
- Release only after both edition builds, unit tests, Android instrumentation, screenshot capture integrity, Play asset validation and GitHub release build succeed.
- GitHub CI signs binaries with configured private signing secrets if all exist. Without those secrets, a **temporary untrusted test-only key** is used and the resulting GitHub release is a **preview**, not a Google Play update.
- Exact **1:1 design approval remains NOT VERIFIED**. SVG concept viewboxes are 432×960 versus native Android capture frames of 1080×1920, and approved same-viewport pixel-perfect master images have not been established. Report and review every difference; do not mask or retouch captured screenshots.
- Physical phone, TalkBack, rotation/tablet and full production Google Play Console acceptance remain NOT TESTED unless independently evidenced.
- Existing `v0.1.7` release is not replaced. Preview builds signed by different temporary CI keys cannot update one another in place; export private backups securely before replacing installs.
