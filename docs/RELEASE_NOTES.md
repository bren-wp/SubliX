# SubliX v0.1.4 — Unified Android Free/Pro & source-only website

## Android
- Both standalone Kotlin/Compose editions retain no-login, offline-first subscription tracking, local reminders, renewals, archive/history and JSON backup/restore.
- Fix: recording a payment on a long-overdue subscription now advances the recurring schedule to the next future due date instead of remaining in the past. Anchored day-of-month dates are preserved across month-end and leap years.
- A saved HTTPS provider-management URL can be opened explicitly from subscription details. Opening the link never cancels a provider subscription automatically.
- Pro retains category budgets, 6/12/24-month forecasts, savings what-if planner, local CSV reports, advanced filters and price-change history. These are separate Pro edition actions.

## Website and branding
- Rebuilt the static presentation site with the Android dark palette and differentiated gold Pro branding.
- Replaced old vector mockups in website hero/gallery with **40 authentic Android emulator PNG captures**, 20 Free + 20 Pro, copied byte-for-byte from the repository's prior screenshot verification run.
- Added accessible Free/Pro screenshot gallery tabs, expand-all view, keyboard controls and image preview; no external framework, tracking SDK, font CDN or registration.
- Updated the website Privacy and Terms pages to correctly describe existing manual JSON and CSV exports, no auto-sync and optional reminders.
- Added automated consistency checks for brand colors, screenshot identity, actual Pro capabilities, no Pages workflow and Free/Pro application IDs.

## QA and limits
- Cross-platform consistency checks and new JVM tests cover payment renewal roll-forward, including overdue and leap-date cases.
- Previous screenshots are marked with their actual capture version v0.1.2; newer changes do not retroactively make them v0.1.4 screenshots.
- Measured 1:1 parity across every viewport, typography scale and state remains open until RGB comparison plus interaction and accessibility acceptance are complete.
- GitHub release artifacts use a temporary **test-only** signing identity unless all private production signing secrets are configured.
- Free and Pro source and binaries remain publicly viewable on GitHub; public Pro APKs are not technically restricted.
- The web remains in `/web` in the public repository only, with no GitHub Pages deployment.
