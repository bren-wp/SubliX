# SubliX v0.1.10 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 13.3458% | 6.903 |
| free | 02-subscriptions | REVIEW_REQUIRED | 31.2018% | 10.228 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6712% | 0.749 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3454% | 1.272 |
| free | 05-details | REVIEW_REQUIRED | 0.4921% | 0.636 |
| free | 06-calendar | REVIEW_REQUIRED | 29.1445% | 11.110 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.4977% | 0.646 |
| free | 08-insights | REVIEW_REQUIRED | 8.8767% | 2.647 |
| free | 09-reminders | REVIEW_REQUIRED | 3.3496% | 1.196 |
| free | 10-settings | REVIEW_REQUIRED | 3.3513% | 1.205 |
| free | 11-categories | REVIEW_REQUIRED | 3.3510% | 1.289 |
| free | 12-history | REVIEW_REQUIRED | 0.4980% | 0.646 |
| free | 13-archive | REVIEW_REQUIRED | 22.3434% | 9.679 |
| free | 14-backup | REVIEW_REQUIRED | 3.3523% | 1.184 |
| free | 15-budget | REVIEW_REQUIRED | 0.4991% | 0.647 |
| free | 16-savings | REVIEW_REQUIRED | 0.4934% | 0.637 |
| free | 17-trials | REVIEW_REQUIRED | 0.4934% | 0.637 |
| free | 18-pro | REVIEW_REQUIRED | 0.4934% | 0.637 |
| free | 19-privacy | REVIEW_REQUIRED | 3.3466% | 1.296 |
| free | 20-about | REVIEW_REQUIRED | 0.7165% | 0.904 |
| pro | 01-home | REVIEW_REQUIRED | 12.5579% | 6.758 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 23.3441% | 10.275 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6700% | 0.745 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3442% | 1.266 |
| pro | 05-details | REVIEW_REQUIRED | 0.4908% | 0.632 |
| pro | 06-calendar | REVIEW_REQUIRED | 29.1373% | 11.022 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.4908% | 0.632 |
| pro | 08-insights | REVIEW_REQUIRED | 10.3113% | 2.747 |
| pro | 09-reminders | REVIEW_REQUIRED | 3.3423% | 1.183 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3442% | 1.193 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3440% | 1.276 |
| pro | 12-history | REVIEW_REQUIRED | 0.4910% | 0.633 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3359% | 9.363 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3442% | 1.170 |
| pro | 15-budget | REVIEW_REQUIRED | 0.4966% | 0.643 |
| pro | 16-savings | REVIEW_REQUIRED | 3.3498% | 1.288 |
| pro | 17-trials | REVIEW_REQUIRED | 0.4966% | 0.643 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3377% | 1.356 |
| pro | 19-privacy | REVIEW_REQUIRED | 3.3498% | 1.302 |
| pro | 20-about | REVIEW_REQUIRED | 0.7197% | 0.910 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.3566% | 1.528 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.3554% | 1.241 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7590% | 2.027 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3342% | 1.988 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5069% | 0.656 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
