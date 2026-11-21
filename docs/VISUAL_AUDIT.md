# SubliX v0.1.13 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 7.2346% | 6.142 |
| free | 02-subscriptions | REVIEW_REQUIRED | 25.0877% | 9.460 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6807% | 0.763 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3549% | 1.286 |
| free | 05-details | REVIEW_REQUIRED | 0.5015% | 0.650 |
| free | 06-calendar | REVIEW_REQUIRED | 23.0304% | 10.342 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5071% | 0.660 |
| free | 08-insights | REVIEW_REQUIRED | 2.7622% | 1.879 |
| free | 09-reminders | REVIEW_REQUIRED | 4.3632% | 2.189 |
| free | 10-settings | REVIEW_REQUIRED | 3.3608% | 1.219 |
| free | 11-categories | REVIEW_REQUIRED | 3.3610% | 1.306 |
| free | 12-history | REVIEW_REQUIRED | 0.5079% | 0.663 |
| free | 13-archive | REVIEW_REQUIRED | 22.3533% | 9.696 |
| free | 14-backup | REVIEW_REQUIRED | 3.3612% | 1.200 |
| free | 15-budget | REVIEW_REQUIRED | 7.7425% | 3.842 |
| free | 16-savings | REVIEW_REQUIRED | 30.8565% | 10.551 |
| free | 17-trials | REVIEW_REQUIRED | 0.5079% | 0.663 |
| free | 18-pro | REVIEW_REQUIRED | 41.2829% | 15.571 |
| free | 19-privacy | REVIEW_REQUIRED | 6.5777% | 2.882 |
| free | 20-about | REVIEW_REQUIRED | 0.7326% | 0.933 |
| pro | 01-home | REVIEW_REQUIRED | 6.4472% | 5.997 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 17.2331% | 9.514 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6837% | 0.768 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3579% | 1.290 |
| pro | 05-details | REVIEW_REQUIRED | 0.5045% | 0.655 |
| pro | 06-calendar | REVIEW_REQUIRED | 23.0284% | 10.263 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.5045% | 0.655 |
| pro | 08-insights | REVIEW_REQUIRED | 4.2020% | 1.988 |
| pro | 09-reminders | REVIEW_REQUIRED | 4.3606% | 2.185 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3578% | 1.216 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3576% | 1.298 |
| pro | 12-history | REVIEW_REQUIRED | 0.5045% | 0.655 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3495% | 9.385 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3578% | 1.192 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5102% | 0.665 |
| pro | 16-savings | REVIEW_REQUIRED | 4.1247% | 1.978 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5102% | 0.665 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3513% | 1.378 |
| pro | 19-privacy | REVIEW_REQUIRED | 6.5800% | 2.884 |
| pro | 20-about | REVIEW_REQUIRED | 0.7313% | 0.931 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.9737% | 2.632 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.6118% | 1.316 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7726% | 2.051 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3425% | 2.001 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5277% | 0.682 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
