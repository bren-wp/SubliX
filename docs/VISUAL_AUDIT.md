# SubliX v0.1.10 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 13.3653% | 6.938 |
| free | 02-subscriptions | REVIEW_REQUIRED | 31.2168% | 10.251 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6861% | 0.772 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3604% | 1.295 |
| free | 05-details | REVIEW_REQUIRED | 0.5070% | 0.658 |
| free | 06-calendar | REVIEW_REQUIRED | 29.1578% | 11.129 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5109% | 0.665 |
| free | 08-insights | REVIEW_REQUIRED | 8.8896% | 2.667 |
| free | 09-reminders | REVIEW_REQUIRED | 3.3624% | 1.216 |
| free | 10-settings | REVIEW_REQUIRED | 3.3641% | 1.225 |
| free | 11-categories | REVIEW_REQUIRED | 3.3589% | 1.300 |
| free | 12-history | REVIEW_REQUIRED | 0.5058% | 0.657 |
| free | 13-archive | REVIEW_REQUIRED | 22.3513% | 9.690 |
| free | 14-backup | REVIEW_REQUIRED | 3.3591% | 1.194 |
| free | 15-budget | REVIEW_REQUIRED | 0.5115% | 0.667 |
| free | 16-savings | REVIEW_REQUIRED | 0.5115% | 0.667 |
| free | 17-trials | REVIEW_REQUIRED | 0.5115% | 0.667 |
| free | 18-pro | REVIEW_REQUIRED | 0.5115% | 0.667 |
| free | 19-privacy | REVIEW_REQUIRED | 3.3647% | 1.326 |
| free | 20-about | REVIEW_REQUIRED | 0.7345% | 0.934 |
| pro | 01-home | REVIEW_REQUIRED | 12.5760% | 6.788 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 23.3622% | 10.305 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6888% | 0.778 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3573% | 1.289 |
| pro | 05-details | REVIEW_REQUIRED | 0.5032% | 0.651 |
| pro | 06-calendar | REVIEW_REQUIRED | 29.1498% | 11.041 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.5032% | 0.651 |
| pro | 08-insights | REVIEW_REQUIRED | 10.3237% | 2.766 |
| pro | 09-reminders | REVIEW_REQUIRED | 3.3547% | 1.202 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3564% | 1.211 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3562% | 1.294 |
| pro | 12-history | REVIEW_REQUIRED | 0.5032% | 0.651 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3481% | 9.381 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3564% | 1.188 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5088% | 0.661 |
| pro | 16-savings | REVIEW_REQUIRED | 3.3620% | 1.306 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5088% | 0.661 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3500% | 1.374 |
| pro | 19-privacy | REVIEW_REQUIRED | 3.3621% | 1.320 |
| pro | 20-about | REVIEW_REQUIRED | 0.7318% | 0.928 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.3681% | 1.544 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.3668% | 1.257 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7700% | 2.045 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3399% | 1.995 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5127% | 0.662 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
