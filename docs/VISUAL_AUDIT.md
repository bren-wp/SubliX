# SubliX v0.1.12 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 7.2325% | 6.139 |
| free | 02-subscriptions | REVIEW_REQUIRED | 25.0821% | 9.451 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6751% | 0.754 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3493% | 1.277 |
| free | 05-details | REVIEW_REQUIRED | 0.4959% | 0.641 |
| free | 06-calendar | REVIEW_REQUIRED | 23.0248% | 10.333 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5016% | 0.651 |
| free | 08-insights | REVIEW_REQUIRED | 2.7567% | 1.870 |
| free | 09-reminders | REVIEW_REQUIRED | 3.3535% | 1.201 |
| free | 10-settings | REVIEW_REQUIRED | 3.3552% | 1.210 |
| free | 11-categories | REVIEW_REQUIRED | 3.3551% | 1.294 |
| free | 12-history | REVIEW_REQUIRED | 0.5020% | 0.652 |
| free | 13-archive | REVIEW_REQUIRED | 22.3475% | 9.684 |
| free | 14-backup | REVIEW_REQUIRED | 3.3553% | 1.188 |
| free | 15-budget | REVIEW_REQUIRED | 7.7366% | 3.831 |
| free | 16-savings | REVIEW_REQUIRED | 30.8506% | 10.539 |
| free | 17-trials | REVIEW_REQUIRED | 0.5020% | 0.652 |
| free | 18-pro | REVIEW_REQUIRED | 41.2771% | 15.560 |
| free | 19-privacy | REVIEW_REQUIRED | 3.3498% | 1.301 |
| free | 20-about | REVIEW_REQUIRED | 0.7174% | 0.907 |
| pro | 01-home | REVIEW_REQUIRED | 6.4379% | 5.981 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 17.2238% | 9.498 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6756% | 0.756 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3498% | 1.277 |
| pro | 05-details | REVIEW_REQUIRED | 0.4964% | 0.643 |
| pro | 06-calendar | REVIEW_REQUIRED | 23.0203% | 10.251 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.4964% | 0.643 |
| pro | 08-insights | REVIEW_REQUIRED | 4.1939% | 1.975 |
| pro | 09-reminders | REVIEW_REQUIRED | 3.3479% | 1.194 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3497% | 1.203 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3495% | 1.285 |
| pro | 12-history | REVIEW_REQUIRED | 0.4964% | 0.643 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3414% | 9.373 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3497% | 1.179 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5021% | 0.653 |
| pro | 16-savings | REVIEW_REQUIRED | 4.1166% | 1.965 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5021% | 0.653 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3432% | 1.366 |
| pro | 19-privacy | REVIEW_REQUIRED | 3.3553% | 1.312 |
| pro | 20-about | REVIEW_REQUIRED | 0.7222% | 0.914 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.3607% | 1.533 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.3595% | 1.246 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7627% | 2.033 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3326% | 1.983 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5167% | 0.661 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
