# SubliX v0.1.13 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 7.2297% | 6.131 |
| free | 02-subscriptions | REVIEW_REQUIRED | 25.0807% | 9.441 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6737% | 0.745 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3479% | 1.268 |
| free | 05-details | REVIEW_REQUIRED | 0.4946% | 0.631 |
| free | 06-calendar | REVIEW_REQUIRED | 23.0234% | 10.324 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5002% | 0.642 |
| free | 08-insights | REVIEW_REQUIRED | 2.7553% | 1.861 |
| free | 09-reminders | REVIEW_REQUIRED | 4.3562% | 2.171 |
| free | 10-settings | REVIEW_REQUIRED | 3.3605% | 1.215 |
| free | 11-categories | REVIEW_REQUIRED | 3.3602% | 1.300 |
| free | 12-history | REVIEW_REQUIRED | 0.5072% | 0.657 |
| free | 13-archive | REVIEW_REQUIRED | 22.3526% | 9.690 |
| free | 14-backup | REVIEW_REQUIRED | 3.3604% | 1.194 |
| free | 15-budget | REVIEW_REQUIRED | 8.1967% | 4.339 |
| free | 16-savings | REVIEW_REQUIRED | 30.8558% | 10.545 |
| free | 17-trials | REVIEW_REQUIRED | 0.5072% | 0.657 |
| free | 18-pro | REVIEW_REQUIRED | 41.2822% | 15.566 |
| free | 19-privacy | REVIEW_REQUIRED | 6.5770% | 2.876 |
| free | 20-about | REVIEW_REQUIRED | 0.7227% | 0.912 |
| pro | 01-home | REVIEW_REQUIRED | 6.4429% | 5.986 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 17.2288% | 9.503 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6787% | 0.758 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3529% | 1.279 |
| pro | 05-details | REVIEW_REQUIRED | 0.4995% | 0.645 |
| pro | 06-calendar | REVIEW_REQUIRED | 23.0233% | 10.253 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.4995% | 0.645 |
| pro | 08-insights | REVIEW_REQUIRED | 4.1970% | 1.978 |
| pro | 09-reminders | REVIEW_REQUIRED | 4.3556% | 2.174 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3510% | 1.202 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3508% | 1.284 |
| pro | 12-history | REVIEW_REQUIRED | 0.4978% | 0.641 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3427% | 9.371 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3510% | 1.178 |
| pro | 15-budget | REVIEW_REQUIRED | 1.3528% | 1.859 |
| pro | 16-savings | REVIEW_REQUIRED | 4.1179% | 1.964 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5034% | 0.651 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3446% | 1.364 |
| pro | 19-privacy | REVIEW_REQUIRED | 6.5733% | 2.870 |
| pro | 20-about | REVIEW_REQUIRED | 0.7245% | 0.917 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.9655% | 2.612 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.6017% | 1.293 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7624% | 2.029 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3323% | 1.979 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5176% | 0.659 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
