# SubliX — device and TalkBack acceptance matrix (v0.1.10)

**State: NOT PERFORMED ON PHYSICAL DEVICES.** GitHub-hosted x86_64 Android emulators are not real Android hardware. Do not confuse a passing emulator with a manual TalkBack or Play Console certification.

| Required physical configuration | Free | Pro | Status |
| --- | --- | --- | --- |
| Small phone around 360dp, font 1.3×/2× | Pending | Pending | NOT TESTED |
| Standard phone around 393–412dp, gesture navigation | Pending | Pending | NOT TESTED |
| Android tablet and expanded layout | Pending | Pending | NOT TESTED |
| Foldable portrait/landscape | Pending | Pending | NOT TESTED |
| Real 16KB-page Android phone when available | Pending | Pending | NOT TESTED |

## Manual interaction and accessibility acceptance

1. Install separately signed Free (`com.brendigo.sublix`) and Pro (`com.brendigo.sublixpro`) on controlled test devices without overwriting real personal data.
2. Enable **Android Settings → Accessibility → TalkBack**. Navigate Home, Plans, Archive and Pro tools using swipes and double-tap, with Android accessibility focus visible.
3. Verify a subscription row is announced **once**, including its name, price, category, billing period and *actual* upcoming charge date. A weekly plan must expose distinct dates for separate renewals.
4. Open Archive from Settings, inspect each card’s **Details** and **Restore** controls; verify one logical card grouping, distinct service-specific labels, minimum touch-target height, and absence of nested clickable actions.
5. Restore and re-archive a synthetic record without removing payment history; verify undo/data persistence and screen reader focus after the change.
6. Create, edit, search, filter, pay (user-confirmed only), export/import JSON backups via SAF, and use Pro forecast/CSV with known sample data. Deny/re-enable notification permission and repeat.
7. Test 1.3×/2× font, high-contrast and display-size settings, physical keyboards, rotation, tablet dimensions, notification permission and offline behavior. Inspect clipping/overlapping labels at the top and bottom system insets.
8. Record make, model, Android version, density, text scale, build commit, screenshot files and observed pass/fail before changing a row to PASSED.
9. Compare uncropped app PNGs against **separately approved same-viewport reference images**. The illustrative SVG concept files alone are *not* an approved pixel-perfect target.

## Automated checks

- Android emulator UI test suite: `connectedFreeDebugAndroidTest` and `connectedProDebugAndroidTest`.
- JVM regression unit tests for renewal occurrence ordering and correct dates.
- 45 real emulator screenshots, eight 1.3× font narrow captures and a complete RGB difference report in CI.
- These automated checks are meaningful regression guards, but do not replace manual TalkBack/physical hardware approval.

## v0.1.9 adaptive Archive/Home checklist

- At 1.3× and 2× font, verify the Home annual-equivalent and upcoming-month amounts are readable without cropping.
- At 320–360dp widths, verify Archive's Details and Restore remain separate, labeled, and at least 48dp tall.
- Search inactive items by notes and categories; ensure active plans never appear in Archive results. Rotate and recreate the Activity to verify the search draft survives.
- Record device dimensions, font scale, and screenshot evidence before any claim of pixel-perfect 1:1 design approval.

## v0.1.10 manual acceptance checklist

- On physical phones at 1.3× and 2× text scale verify Calendar date, amount, focus order and filter chips on both editions.
- Verify switching month periods around December/January, and a crowded month with many weekly plans.
- Enter a Service Library search with no result, rotate or recreate the Activity and use the custom-subscription action. Ensure the editor opens for a new service without modifying existing plans.
- Keep physical TalkBack and approved same-viewport pixel-perfect design signoff as NOT TESTED until independently recorded.
