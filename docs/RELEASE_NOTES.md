# SubliX v0.1.10 — Month-scoped calendar and service-library reliability

## Changes
- Service Library search is now case- and accent-insensitive, supports multiple terms and remembers its input through Android Activity recreation. A search with no matches provides a real custom-subscription action.
- Service templates expose a single accessible selectable card, without a duplicate icon description.
- Renewal Calendar gains All/This month/Next month/In 2 months filters that retain their selection through Activity recreation. Month-specific queries use an independent bounded recurrence calculation so a crowded earlier month cannot hide later events.
- Calendar entries announce accurate recurrence date and EUR amount; compact screens and enlarged fonts avoid single-line truncation.
- Add Kotlin/JUnit tests for searchable templates, year-boundary and dense-month renewals. Add Free/Pro Compose UI tests for changing months, search state retention and opening the custom editor.
- Both editions: `versionName=0.1.10`, monotonically increased `versionCode=11`, existing package IDs and SQLite schema unchanged. Offline-first, no sign-in, no new permissions.

## QA and release acceptance
- Require passing Android Free/Pro build, JVM and instrumented UI tests, web checks, 20 Free + 25 Pro authentic screenshot captures, rejection of Android overlays, deterministic full RGB audit, website screenshot sync, Play assets and release preflight.
- Strict **1:1 VERIFIED remains NOT VERIFIED** because SVG concepts are 432×960 versus 1080×1920 emulator frames and independently approved identical-viewport reference masters have not been supplied. Historical v0.1.6 comparisons are a regression indicator, not a design signoff.
- Physical-device 2× text, tablet, foldable, TalkBack and all relevant dialog states remain NOT TESTED outside emulator evidence.
- Publish exactly seven release files: Free APK, Pro APK, Free AAB, Pro AAB, Play media ZIP, source ZIP, SHA256SUMS.txt. Temporary CI signing is TEST ONLY and cannot be used as a production Play update or guaranteed in-place app update.
