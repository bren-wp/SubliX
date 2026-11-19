# SubliX v0.1.11 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 7.2299% | 6.133 |
| free | 02-subscriptions | REVIEW_REQUIRED | 25.0828% | 9.448 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6757% | 0.751 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3500% | 1.274 |
| free | 05-details | REVIEW_REQUIRED | 0.4966% | 0.638 |
| free | 06-calendar | REVIEW_REQUIRED | 23.0321% | 10.345 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5089% | 0.663 |
| free | 08-insights | REVIEW_REQUIRED | 2.7640% | 1.882 |
| free | 09-reminders | REVIEW_REQUIRED | 3.3608% | 1.213 |
| free | 10-settings | REVIEW_REQUIRED | 3.3625% | 1.222 |
| free | 11-categories | REVIEW_REQUIRED | 3.3625% | 1.307 |
| free | 12-history | REVIEW_REQUIRED | 0.5095% | 0.665 |
| free | 13-archive | REVIEW_REQUIRED | 22.3549% | 9.697 |
| free | 14-backup | REVIEW_REQUIRED | 3.3627% | 1.201 |
| free | 15-budget | REVIEW_REQUIRED | 0.5095% | 0.665 |
| free | 16-savings | REVIEW_REQUIRED | 0.5095% | 0.665 |
| free | 17-trials | REVIEW_REQUIRED | 0.5095% | 0.665 |
| free | 18-pro | REVIEW_REQUIRED | 0.5095% | 0.665 |
| free | 19-privacy | REVIEW_REQUIRED | 3.3627% | 1.324 |
| free | 20-about | REVIEW_REQUIRED | 0.7242% | 0.924 |
| pro | 01-home | REVIEW_REQUIRED | 6.4452% | 5.993 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 17.2311% | 9.510 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6811% | 0.766 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3553% | 1.287 |
| pro | 05-details | REVIEW_REQUIRED | 0.5019% | 0.652 |
| pro | 06-calendar | REVIEW_REQUIRED | 23.0258% | 10.261 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.5019% | 0.652 |
| pro | 08-insights | REVIEW_REQUIRED | 4.1994% | 1.985 |
| pro | 09-reminders | REVIEW_REQUIRED | 3.3534% | 1.204 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3534% | 1.209 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3533% | 1.291 |
| pro | 12-history | REVIEW_REQUIRED | 0.5002% | 0.649 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3451% | 9.379 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3534% | 1.185 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5058% | 0.659 |
| pro | 16-savings | REVIEW_REQUIRED | 3.3590% | 1.304 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5058% | 0.659 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3470% | 1.372 |
| pro | 19-privacy | REVIEW_REQUIRED | 3.3591% | 1.318 |
| pro | 20-about | REVIEW_REQUIRED | 0.7206% | 0.918 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.3651% | 1.542 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.3638% | 1.255 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7670% | 2.042 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3370% | 1.992 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5141% | 0.665 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
