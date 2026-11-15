# SubliX v0.1.9 — adaptive Home, searchable Archive and reliable screenshots

## Changes
- Home summary card stacks annual equivalent and upcoming-month totals on narrow phones or at system font scale 1.3× and above. Long monetary amounts can wrap instead of truncating to a single line.
- Archive now provides offline search by subscription name, category and private note, with Unicode/diacritic-insensitive matching, an archived-result count and a Clear search action. Search state is saved through Activity recreation. Active plans never leak into the archive results.
- Archive retains exactly one card per subscription with separate service-specific Details and Restore controls. On narrow phones or large text these controls stack vertically, maintaining 48dp minimum target height.
- Add deterministic JVM archive matching tests and real Free/Pro Android Compose tests for archive search, clearing and lifecycle recreation.
- Screenshot workflow now runs Free/Pro instrumentation on a separate emulator from authentic screenshot capture, rejecting blocking Android system overlays rather than masking them. Test artifact names use the commit SHA.
- Both Free and Pro use `versionName=0.1.9` and monotonically increasing `versionCode=10`. Android package IDs, offline storage, backup format and SQLite schema are unchanged.

## Acceptance and limitations
- CI must build and test both Android editions, verify the website and Play metadata, capture 20 Free plus 25 Pro real Android screenshots, reject contaminated frames, compute full 45-screen RGB report, and validate identical Play screenshot content. A green compile is not sufficient.
- Design approval **1:1 VERIFIED remains NOT ESTABLISHED**. Original SVG concept references have different aspect ratios from 1080×1920 Android captures and are not separately approved matching-viewport baselines. Existing v0.1.6 snapshots measure regression, not perfect compliance.
- Manual TalkBack, real phone/tablet/foldable, orientation and 2× text-scale acceptance are NOT TESTED on physical devices.
- A new release must provide seven files: Free APK, Pro APK, Free AAB, Pro AAB, Play assets ZIP, source ZIP and SHA256SUMS.txt. If permanent GitHub signing secrets are not configured, artifacts are **untrusted test-only previews** that cannot update installations signed with different keys.
