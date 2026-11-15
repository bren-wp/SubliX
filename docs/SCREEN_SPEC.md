# SubliX — Screen specification

The 20 concept layouts in `brending/screens` define direction, not proof of pixel-perfect delivery. Compare emulator screenshots to approved baselines at 360x800, 393x873, 412x915, 600x960, and 1280x800 dp; do not crop errors.

| # | Screen | Required interactions |
|---|---|---|
| 01 | Home | Open upcoming payments and add plan |
| 02 | Subscriptions | Search, filter, add and select plan |
| 03 | Add subscription | Validate fields and save |
| 04 | Service library | Pick preset or custom |
| 05 | Subscription details | Edit, archive, delete and view payments |
| 06 | Calendar | Browse scheduled charges |
| 07 | Payment details | Confirm payment only after user action |
| 08 | Insights | Change range and category |
| 09 | Reminders | Set reminder preferences |
| 10 | Settings | Theme, export, permissions |
| 11 | Categories | Create and rename categories |
| 12 | History | Review payment history |
| 13 | Archive | Restore or delete archived |
| 14 | Backup & restore | Export and import with validation |
| 15 | Budget | Configure limit |
| 16 | Savings insights | Review potential savings |
| 17 | Trial tracker | Manage trial deadlines |
| 18 | SubliX Pro | Explain standalone Pro edition |
| 19 | Privacy | View transparent privacy policy |
| 20 | About SubliX | Licenses and company details |

## Accessibility and quality
All screens: content descriptions for icon-only actions, 48dp targets, screen reader labels, focus order, keyboard insets, large type, no light flash at startup, protected destructive actions, non-misleading zero and error states. Financial figures distinguish projected recurring equivalent from user-confirmed payments. The separate Pro app has its own package and local database; migration requires explicit export/import.

## v0.1.2 implemented screen-navigation targets (requires verification)

All 20 planned screen names now have explicit Compose routes, with Free and Pro scoped actions. This is implementation coverage only, **not 20/20 pixel-perfect acceptance**. Functionality includes local service templates, custom categories, reminders, budgets, trial metadata, usage and savings scenarios. There is no bank or provider account connection and no automatic subscription cancellation.

### Visual acceptance process
1. Build and install both flavors on an emulator, capture 20 PNGs per edition with synthetic fixtures, and verify absence of ANR/Activity crash.
2. Compare each screenshot to its corresponding SVG artwork at the same canvas, scale and aspect ratio. Record alignment discrepancies for typography, padding, colors, card radius and icon shapes; vector concepts must first be rendered to identical dimensions.
3. Test with system font sizes 1.0x and 1.3x, portrait and landscape, 360/393/412 dp phone widths and medium/expanded adaptive widths.
4. Validate TalkBack labels, navigation, input focus, edge-to-edge, color contrast, and all controls.
5. Only check an individual screen as **1:1 approved** when the comparison and interaction test are demonstrably successful.

UI acceptance table starts as **unverified** for all 20 views until screenshot capture and comparison reports are available. Do not present reference SVGs as device screenshots.

## v0.1.3 — Pro-only screens & screenshot parity tracking

Free and Pro share the 20 planned main screens and no-login offline records. Pro now additionally exposes five purpose-built views: 12-month/24-month renewal forecast, category budget limits, what-if subscription cancellation planner, local CSV reports and price change history. Pro users also gain category search/filter/sorting and existing savings and monthly budget tools.

Previous version's **40 real emulator screenshots** are preserved in `brending/baselines/v0.1.2/` as **candidate regression baselines**, not approved design references. New 40+5 emulator PNGs are captured using actual compiled apps. The workflow generates `docs/VISUAL_AUDIT.md` with measured RGB deltas per shared screen and labels unavoidable open visual review items. Do not claim all screens 1:1 until the measured baseline is approved, and do not crop problematic UI areas to make the metric pass.

## v0.1.4 — Cross-platform visual and functional consistency

Web hero and image gallery reference real Android emulator PNG files copied byte-for-byte from `brending/screenshots-real/free` and `brending/screenshots-real/pro`. The 40 images are labeled as v0.1.2 captures; they are not claimed to be screenshots of the v0.1.4 build. The web design tokens are validated against Kotlin `Theme.kt` by `scripts/check_sync.py`. Both Android editions expose an explicitly user-initiated saved HTTPS management link on subscription details; no automatic provider cancellation occurs.

Acceptance remains **unverified 1:1** for design-reference parity until systematic screenshot and UI interaction review at all supported sizes. Exact RGB identity to an old version is a regression signal, not a substitute for comparing approved design references.

## v0.1.6 — visual acceptance matrix

- **Free:** 20/20 routes are captured on a 1080×1920 emulator with synthetic data.
- **Pro:** 25/25 routes are captured, including the separate forecast, category budgets, scenarios, CSV reports and price-change history.
- **Extra:** Home, Plans, Insights and Pro / edition overview are smoke-tested at 900×2000 and 1.3× system font in both editions.
- **Baseline:** v0.1.5 real captured PNGs, not the earlier 0.1.2 images. See docs/VISUAL_AUDIT.json and the CI diff-overlay artifact.
- **Design reference:** existing SVG concept canvases use viewBox 432×960; capture viewport is 1080×1920. Literal 1:1 approval requires viewport-adapted and independently accepted design screenshots. Zero unintended pixel differences against those targets remain the final goal. No masks or crops to conceal mismatches.
- **Functional acceptance:** tap-test navigation, editing, backup, permission denial, budget, forecast, dialogs, and record restoration on real devices still need a dedicated interactive/instrumentation suite. Screenshot capture alone does not assert all controls.

## v0.1.7 acceptance criteria

- Home: dates reflect *each* expected charge, not the original saved due date; repeated weekly renewals show distinct dates.
- Plans: accessible subscription rows describe service, amount, cycle, category and true due date in one focusable action.
- Archive: one layer of card padding; separate 48dp+ Details and Restore touch targets and TalkBack labels.
- Emulator: Free and Pro Compose interaction tests run before capturing 45 genuine screenshots, plus eight narrow 1.3× font captures.
- Baseline: real v0.1.6 emulator captures, never the old v0.1.5 snapshots; differences are reviewed, not suppressed.
- On-device TalkBack, physical phones/tablets and final literal 1:1 review remain NOT TESTED/NOT APPROVED until actual evidence is available.
