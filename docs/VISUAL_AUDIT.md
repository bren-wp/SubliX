# SubliX v0.1.7 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 99.5157% | 50.434 |
| free | 02-subscriptions | REVIEW_REQUIRED | 99.6911% | 57.359 |
| free | 03-add-subscription | REVIEW_REQUIRED | 99.6456% | 55.250 |
| free | 04-service-library | REVIEW_REQUIRED | 99.6175% | 55.144 |
| free | 05-details | REVIEW_REQUIRED | 99.6670% | 58.523 |
| free | 06-calendar | REVIEW_REQUIRED | 99.5249% | 56.148 |
| free | 07-payment-details | REVIEW_REQUIRED | 99.6305% | 53.691 |
| free | 08-insights | REVIEW_REQUIRED | 99.4644% | 56.962 |
| free | 09-reminders | REVIEW_REQUIRED | 99.6770% | 56.578 |
| free | 10-settings | REVIEW_REQUIRED | 99.6933% | 55.748 |
| free | 11-categories | REVIEW_REQUIRED | 99.7915% | 56.961 |
| free | 12-history | REVIEW_REQUIRED | 99.7909% | 55.730 |
| free | 13-archive | REVIEW_REQUIRED | 99.7620% | 53.424 |
| free | 14-backup | REVIEW_REQUIRED | 99.5588% | 60.260 |
| free | 15-budget | REVIEW_REQUIRED | 99.5778% | 52.674 |
| free | 16-savings | REVIEW_REQUIRED | 99.6528% | 53.328 |
| free | 17-trials | REVIEW_REQUIRED | 99.6975% | 52.854 |
| free | 18-pro | REVIEW_REQUIRED | 99.5932% | 54.163 |
| free | 19-privacy | REVIEW_REQUIRED | 99.6697% | 55.515 |
| free | 20-about | REVIEW_REQUIRED | 99.7252% | 55.652 |
| pro | 01-home | REVIEW_REQUIRED | 99.6526% | 50.865 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 99.7980% | 57.652 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 99.6546% | 55.258 |
| pro | 04-service-library | REVIEW_REQUIRED | 99.6250% | 55.154 |
| pro | 05-details | REVIEW_REQUIRED | 99.6744% | 58.210 |
| pro | 06-calendar | REVIEW_REQUIRED | 99.5222% | 56.137 |
| pro | 07-payment-details | REVIEW_REQUIRED | 99.6191% | 53.592 |
| pro | 08-insights | REVIEW_REQUIRED | 99.3507% | 56.551 |
| pro | 09-reminders | REVIEW_REQUIRED | 99.6656% | 56.532 |
| pro | 10-settings | REVIEW_REQUIRED | 99.6906% | 55.734 |
| pro | 11-categories | REVIEW_REQUIRED | 99.7846% | 56.717 |
| pro | 12-history | REVIEW_REQUIRED | 99.7928% | 55.728 |
| pro | 13-archive | REVIEW_REQUIRED | 99.7653% | 53.404 |
| pro | 14-backup | REVIEW_REQUIRED | 99.5519% | 59.901 |
| pro | 15-budget | REVIEW_REQUIRED | 99.7561% | 57.873 |
| pro | 16-savings | REVIEW_REQUIRED | 99.2399% | 56.395 |
| pro | 17-trials | REVIEW_REQUIRED | 99.6895% | 52.799 |
| pro | 18-pro | REVIEW_REQUIRED | 99.5439% | 56.392 |
| pro | 19-privacy | REVIEW_REQUIRED | 99.6608% | 55.467 |
| pro | 20-about | REVIEW_REQUIRED | 99.7247% | 55.607 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 99.5942% | 57.596 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 99.7235% | 57.063 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 99.1855% | 55.307 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 99.5743% | 62.949 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 99.6932% | 53.169 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
