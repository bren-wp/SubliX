# SubliX v0.1.7 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 10.6197% | 4.222 |
| free | 02-subscriptions | REVIEW_REQUIRED | 9.6274% | 4.132 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6902% | 0.784 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3644% | 1.307 |
| free | 05-details | REVIEW_REQUIRED | 0.5110% | 0.671 |
| free | 06-calendar | REVIEW_REQUIRED | 8.4108% | 2.519 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5099% | 0.669 |
| free | 08-insights | REVIEW_REQUIRED | 8.8886% | 2.671 |
| free | 09-reminders | REVIEW_REQUIRED | 3.3614% | 1.221 |
| free | 10-settings | REVIEW_REQUIRED | 3.3632% | 1.230 |
| free | 11-categories | REVIEW_REQUIRED | 3.3590% | 1.305 |
| free | 12-history | REVIEW_REQUIRED | 0.5051% | 0.664 |
| free | 13-archive | REVIEW_REQUIRED | 13.4511% | 7.499 |
| free | 14-backup | REVIEW_REQUIRED | 3.3583% | 1.200 |
| free | 15-budget | REVIEW_REQUIRED | 0.5051% | 0.664 |
| free | 16-savings | REVIEW_REQUIRED | 0.5051% | 0.664 |
| free | 17-trials | REVIEW_REQUIRED | 0.5051% | 0.664 |
| free | 18-pro | REVIEW_REQUIRED | 0.5051% | 0.664 |
| free | 19-privacy | REVIEW_REQUIRED | 3.3583% | 1.323 |
| free | 20-about | REVIEW_REQUIRED | 0.7150% | 0.916 |
| pro | 01-home | REVIEW_REQUIRED | 9.4131% | 3.917 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 9.4797% | 3.935 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6815% | 0.771 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3558% | 1.292 |
| pro | 05-details | REVIEW_REQUIRED | 0.5024% | 0.658 |
| pro | 06-calendar | REVIEW_REQUIRED | 8.4008% | 2.507 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.5024% | 0.658 |
| pro | 08-insights | REVIEW_REQUIRED | 10.3229% | 2.773 |
| pro | 09-reminders | REVIEW_REQUIRED | 3.3539% | 1.209 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3556% | 1.218 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3554% | 1.301 |
| pro | 12-history | REVIEW_REQUIRED | 0.5024% | 0.658 |
| pro | 13-archive | REVIEW_REQUIRED | 13.4485% | 7.189 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3557% | 1.192 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5070% | 0.665 |
| pro | 16-savings | REVIEW_REQUIRED | 3.3602% | 1.310 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5070% | 0.665 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3481% | 1.378 |
| pro | 19-privacy | REVIEW_REQUIRED | 3.3602% | 1.324 |
| pro | 20-about | REVIEW_REQUIRED | 0.7169% | 0.917 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.3685% | 1.551 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.3672% | 1.264 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 1.8107% | 0.863 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3449% | 2.010 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5122% | 0.674 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
