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

## Verified pre-merge CI evidence (native Free and Pro)

- [Android and website checks](https://github.com/bren-wp/SubliX/actions/runs/44424297151): **PASS** on source commit `5298152`.
- [Native Compose interaction tests, capture of all 143 QA frames, deterministic RGB audit, and web/Google Play sync](https://github.com/bren-wp/SubliX/actions/runs/44424297158): **PASS** on source commit `5298152`.
- [Authentic Free/Pro screenshot publisher](https://github.com/bren-wp/SubliX/actions/runs/44424289141): **PASS**; 45 current-version 1080×1920 captures, web gallery, Play assets and audit published at generated commit `7d5ca47`, `CAPTURE_VERSION=0.1.11`. Extra 98 adaptive frames remain QA-only artifacts.
- The screenshot bot's follow-on PR checks report `action_required` without running real jobs. This ordinary documentation change triggers the final Android/web/visual checks on the combined source + committed-media revision.
- Latest v0.1.11 RGB audit: **0/45 identical to historical v0.1.6**. This is not a failed capture check, and does not certify design parity. All screens retain `REVIEW_REQUIRED` for 1:1 approval until independently approved same-viewport masters exist.

## PR #16 — Archive recreation regression corrected

- Previous post-media Visual QA [run 44426411803](https://github.com/bren-wp/SubliX/actions/runs/44426411803) reported a **genuine instrumented-test failure** in `archiveSearchAndClearSurviveActivityRecreation`: the search query was not preserved after Activity recreation in that run.
- `d79eb0a` hoists the Archive query into `SubliXApp` with saveable state and explicit controlled input. `cc0b85a` and `72b77ed` test committed text before recreation, returning to Archive through navigation, and a meaningful Clear action assertion.
- Android and website [run 44427254497](https://github.com/bren-wp/SubliX/actions/runs/44427254497): **PASS** on `72b77ed`; instrumentation for both editions passed before capture. [Screenshot publisher run 44427245396](https://github.com/bren-wp/SubliX/actions/runs/44427245396): **PASS**, refreshed source-consistent native Free/Pro screenshots, web and Play media at `c756ee5`.
- The `72b77ed` visual job was cancelled by the new screenshot bot commit. This normal documentation commit triggers **fresh, non-bot PR checks** on the complete current source and screenshots. Do not merge until the new Android/web and visual workflows both report PASS.
- These repairs do not certify visual 1:1 design equivalence: approved matching-viewport references and physical-device/TalkBack reviews are still outstanding.
