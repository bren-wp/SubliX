# SubliX v0.1.13 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 7.2432% | 6.157 |
| free | 02-subscriptions | REVIEW_REQUIRED | 25.0919% | 9.468 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6850% | 0.772 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3592% | 1.295 |
| free | 05-details | REVIEW_REQUIRED | 0.5058% | 0.658 |
| free | 06-calendar | REVIEW_REQUIRED | 23.0346% | 10.351 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5115% | 0.669 |
| free | 08-insights | REVIEW_REQUIRED | 2.7665% | 1.888 |
| free | 09-reminders | REVIEW_REQUIRED | 4.3675% | 2.198 |
| free | 10-settings | REVIEW_REQUIRED | 3.3647% | 1.229 |
| free | 11-categories | REVIEW_REQUIRED | 3.3653% | 1.314 |
| free | 12-history | REVIEW_REQUIRED | 0.5122% | 0.671 |
| free | 13-archive | REVIEW_REQUIRED | 22.3577% | 9.704 |
| free | 14-backup | REVIEW_REQUIRED | 3.3655% | 1.208 |
| free | 15-budget | REVIEW_REQUIRED | 7.7469% | 3.851 |
| free | 16-savings | REVIEW_REQUIRED | 30.8608% | 10.559 |
| free | 17-trials | REVIEW_REQUIRED | 0.5122% | 0.671 |
| free | 18-pro | REVIEW_REQUIRED | 41.2873% | 15.580 |
| free | 19-privacy | REVIEW_REQUIRED | 6.5820% | 2.890 |
| free | 20-about | REVIEW_REQUIRED | 0.7334% | 0.937 |
| pro | 01-home | REVIEW_REQUIRED | 6.4536% | 6.010 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 17.2258% | 9.503 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6765% | 0.758 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3506% | 1.279 |
| pro | 05-details | REVIEW_REQUIRED | 0.4973% | 0.644 |
| pro | 06-calendar | REVIEW_REQUIRED | 23.0210% | 10.253 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.4973% | 0.644 |
| pro | 08-insights | REVIEW_REQUIRED | 4.1946% | 1.977 |
| pro | 09-reminders | REVIEW_REQUIRED | 4.3533% | 2.174 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3505% | 1.205 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3503% | 1.287 |
| pro | 12-history | REVIEW_REQUIRED | 0.4973% | 0.644 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3423% | 9.375 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3505% | 1.181 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5029% | 0.655 |
| pro | 16-savings | REVIEW_REQUIRED | 4.1174% | 1.967 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5029% | 0.655 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3440% | 1.368 |
| pro | 19-privacy | REVIEW_REQUIRED | 6.5727% | 2.874 |
| pro | 20-about | REVIEW_REQUIRED | 0.7241% | 0.920 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.9664% | 2.621 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.6027% | 1.302 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7670% | 2.043 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3369% | 1.993 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5221% | 0.673 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
