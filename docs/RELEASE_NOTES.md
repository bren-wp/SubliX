# SubliX v0.1.11 — Navigation and adaptive-screen consistency

## Changes
- Free and Pro share an explicit route history. Toolbar Back and Android system Back return to the actual preceding page, including service library and nested Settings routes, rather than incorrectly opening an unsaved editor or jumping to Settings.
- Route history is saved across Activity recreation. A completed subscription edit returns to Details without reopening a completed form; navigation tabs reset the history.
- The Android activity explicitly enables dark edge-to-edge system UI, with safe-drawing insets handled by the common Material 3 Scaffold so app controls avoid Android gesture bars and display cutouts.
- Added independent Android Compose navigation tests for library, nested Settings/Archive, activity recreation and system Back in both Free and Pro.
- The emulator quality pipeline captures all 20 Free and 25 Pro routes at 1080×1920, at 900×2000 and 1.3× font, again at 900×2000 and 2× font, plus eight landscape/tablet smoke frames at 1600×1200. Adaptation images are QA-only, not Play Store media. Each authentic capture must pass PNG integrity and system-dialog detection.
- Version name 0.1.11 and monotonically incremented Android versionCode 12; both package IDs and database schema remain unchanged.

## Acceptance gates
- Require passing Free/Pro unit, build and Compose instrumentation checks, 45 main captures, 90 additional large-font captures, eight tablet/landscape captures, RGB audit, web/Play media identity, and release preflight before merge.
- **Pixel-perfect design approval: NOT VERIFIED.** The available 432×960 concept SVGs are illustrative, not approved same-viewport UI references; historical v0.1.6 Android captures are regression inputs only. No masking, fabricated PASS or retouched store screenshots.
- Physical-device testing, TalkBack, manual inspection of every dialog state and externally signed-off 1:1 design targets remain outstanding. Do not claim these checks passed from emulator-only evidence.
- Publish seven versioned artifacts only after passing CI: Free/Pro APK and AAB, Play media ZIP, source ZIP, and SHA256SUMS.txt. Without permanent signing secrets the release is **TEST-ONLY** and not a production update.
