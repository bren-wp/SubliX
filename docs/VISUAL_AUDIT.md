# SubliX v0.1.13 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 7.2352% | 6.504 |
| free | 02-subscriptions | REVIEW_REQUIRED | 25.0976% | 9.838 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6875% | 0.776 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3616% | 1.299 |
| free | 05-details | REVIEW_REQUIRED | 0.5083% | 0.662 |
| free | 06-calendar | REVIEW_REQUIRED | 23.0326% | 10.346 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5094% | 0.664 |
| free | 08-insights | REVIEW_REQUIRED | 2.7644% | 1.884 |
| free | 09-reminders | REVIEW_REQUIRED | 4.3654% | 2.194 |
| free | 10-settings | REVIEW_REQUIRED | 3.3626% | 1.225 |
| free | 11-categories | REVIEW_REQUIRED | 3.3562% | 1.297 |
| free | 12-history | REVIEW_REQUIRED | 0.5032% | 0.654 |
| free | 13-archive | REVIEW_REQUIRED | 22.3487% | 9.687 |
| free | 14-backup | REVIEW_REQUIRED | 3.3564% | 1.191 |
| free | 15-budget | REVIEW_REQUIRED | 8.1928% | 4.336 |
| free | 16-savings | REVIEW_REQUIRED | 30.8518% | 10.542 |
| free | 17-trials | REVIEW_REQUIRED | 0.5032% | 0.654 |
| free | 18-pro | REVIEW_REQUIRED | 41.2783% | 15.563 |
| free | 19-privacy | REVIEW_REQUIRED | 6.5797% | 2.888 |
| free | 20-about | REVIEW_REQUIRED | 0.7310% | 0.934 |
| pro | 01-home | REVIEW_REQUIRED | 6.4512% | 6.008 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 17.2371% | 9.525 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6872% | 0.780 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3614% | 1.301 |
| pro | 05-details | REVIEW_REQUIRED | 0.5080% | 0.667 |
| pro | 06-calendar | REVIEW_REQUIRED | 23.0317% | 10.275 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.5080% | 0.667 |
| pro | 08-insights | REVIEW_REQUIRED | 4.2053% | 2.000 |
| pro | 09-reminders | REVIEW_REQUIRED | 4.3640% | 2.196 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3612% | 1.227 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3610% | 1.310 |
| pro | 12-history | REVIEW_REQUIRED | 0.5080% | 0.667 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3530% | 9.397 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3612% | 1.204 |
| pro | 15-budget | REVIEW_REQUIRED | 1.3619% | 1.884 |
| pro | 16-savings | REVIEW_REQUIRED | 4.1270% | 1.988 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5108% | 0.672 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3519% | 1.385 |
| pro | 19-privacy | REVIEW_REQUIRED | 6.5806% | 2.891 |
| pro | 20-about | REVIEW_REQUIRED | 0.7320% | 0.937 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.9772% | 2.642 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.6135% | 1.323 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7742% | 2.058 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3486% | 2.017 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5340% | 0.697 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
