# SubliX v0.1.3 — Pro toolkit & real Android screenshots

Two standalone Android applications: **SubliX Free** and **SubliX Pro**, both offline-first, requiring no user account.

## Functional changes
- Free retains unlimited recurring subscription entries, categories, renewal calendar, optional local notifications, manual payment history, and explicit JSON backup/restore.
- Pro receives a 6/12/24-month payment forecast, individual category budgets, a what-if savings planner (never cancels services), local CSV reports for subscriptions/forecasts/recorded payments, and on-device price-change history.
- Pro also offers subscription sorting by name/cost/due date and category filtering, plus existing total budget and low-usage review.
- SQLite v3 performs additive non-destructive migrations from v1 and v2; price changes are logged when an existing entry's amount changes.
- CSV export neutralizes spreadsheet-formula prefixes and requires a user-selected save location; files are unencrypted.
- Added 40 authentic prior-release Android emulator screenshots to README and a screenshot-regression candidate baseline.
- New screenshot workflow creates 40 refreshed Free+Pro screenshots and five additional Pro-tool screenshots, and writes a measured visual difference report.

## Limitations / important safety information
- App calculations rely exclusively on user-entered data; there is no banking integration or external subscription cancellation.
- Real screenshots are not proof that 20 hand-authored SVG concepts are pixel-identical to Compose screens. All 20 design baseline approvals remain open.
- Pro APK publicly distributed on GitHub can be downloaded without paying.
- GitHub preview APK and AAB packages signed with temporary CI keys are not Google Play-ready and will not necessarily update prior preview installs.
- The website remains source-only in `/web`, without GitHub Pages, server-side storage or accounts.
