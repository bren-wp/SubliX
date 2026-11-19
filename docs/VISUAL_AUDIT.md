# SubliX v0.1.11 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 7.2289% | 6.129 |
| free | 02-subscriptions | REVIEW_REQUIRED | 25.0808% | 9.442 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6739% | 0.745 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3480% | 1.269 |
| free | 05-details | REVIEW_REQUIRED | 0.4947% | 0.632 |
| free | 06-calendar | REVIEW_REQUIRED | 23.0239% | 10.323 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5007% | 0.641 |
| free | 08-insights | REVIEW_REQUIRED | 2.7557% | 1.860 |
| free | 09-reminders | REVIEW_REQUIRED | 3.3522% | 1.192 |
| free | 10-settings | REVIEW_REQUIRED | 3.3606% | 1.216 |
| free | 11-categories | REVIEW_REQUIRED | 3.3603% | 1.301 |
| free | 12-history | REVIEW_REQUIRED | 0.5073% | 0.658 |
| free | 13-archive | REVIEW_REQUIRED | 22.3528% | 9.691 |
| free | 14-backup | REVIEW_REQUIRED | 3.3605% | 1.195 |
| free | 15-budget | REVIEW_REQUIRED | 0.5017% | 0.648 |
| free | 16-savings | REVIEW_REQUIRED | 0.5017% | 0.648 |
| free | 17-trials | REVIEW_REQUIRED | 0.5017% | 0.648 |
| free | 18-pro | REVIEW_REQUIRED | 0.5017% | 0.648 |
| free | 19-privacy | REVIEW_REQUIRED | 3.3549% | 1.307 |
| free | 20-about | REVIEW_REQUIRED | 0.7165% | 0.906 |
| pro | 01-home | REVIEW_REQUIRED | 6.4430% | 5.987 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 17.2289% | 9.504 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6788% | 0.759 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3530% | 1.280 |
| pro | 05-details | REVIEW_REQUIRED | 0.4997% | 0.645 |
| pro | 06-calendar | REVIEW_REQUIRED | 23.0234% | 10.254 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.4997% | 0.645 |
| pro | 08-insights | REVIEW_REQUIRED | 4.1953% | 1.975 |
| pro | 09-reminders | REVIEW_REQUIRED | 3.3494% | 1.193 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3511% | 1.202 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3509% | 1.284 |
| pro | 12-history | REVIEW_REQUIRED | 0.4979% | 0.642 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3429% | 9.372 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3511% | 1.179 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5036% | 0.652 |
| pro | 16-savings | REVIEW_REQUIRED | 3.3567% | 1.297 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5036% | 0.652 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3447% | 1.365 |
| pro | 19-privacy | REVIEW_REQUIRED | 3.3568% | 1.311 |
| pro | 20-about | REVIEW_REQUIRED | 0.7184% | 0.911 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.3605% | 1.529 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.3593% | 1.242 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7625% | 2.029 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3324% | 1.979 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5136% | 0.659 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
