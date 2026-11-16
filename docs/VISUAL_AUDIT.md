# SubliX v0.1.9 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 13.3634% | 6.934 |
| free | 02-subscriptions | REVIEW_REQUIRED | 31.2133% | 10.242 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6826% | 0.763 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3569% | 1.286 |
| free | 05-details | REVIEW_REQUIRED | 0.5035% | 0.650 |
| free | 06-calendar | REVIEW_REQUIRED | 8.4067% | 2.506 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5080% | 0.659 |
| free | 08-insights | REVIEW_REQUIRED | 8.8933% | 2.675 |
| free | 09-reminders | REVIEW_REQUIRED | 3.3662% | 1.225 |
| free | 10-settings | REVIEW_REQUIRED | 3.3679% | 1.234 |
| free | 11-categories | REVIEW_REQUIRED | 3.3638% | 1.309 |
| free | 12-history | REVIEW_REQUIRED | 0.5107% | 0.666 |
| free | 13-archive | REVIEW_REQUIRED | 22.3561% | 9.699 |
| free | 14-backup | REVIEW_REQUIRED | 3.3640% | 1.203 |
| free | 15-budget | REVIEW_REQUIRED | 0.5163% | 0.677 |
| free | 16-savings | REVIEW_REQUIRED | 0.5163% | 0.677 |
| free | 17-trials | REVIEW_REQUIRED | 0.5163% | 0.677 |
| free | 18-pro | REVIEW_REQUIRED | 0.5163% | 0.677 |
| free | 19-privacy | REVIEW_REQUIRED | 3.3696% | 1.336 |
| free | 20-about | REVIEW_REQUIRED | 0.7244% | 0.925 |
| pro | 01-home | REVIEW_REQUIRED | 12.5809% | 6.798 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 23.3671% | 10.315 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6936% | 0.788 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3679% | 1.309 |
| pro | 05-details | REVIEW_REQUIRED | 0.5088% | 0.664 |
| pro | 06-calendar | REVIEW_REQUIRED | 8.4072% | 2.514 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.5088% | 0.664 |
| pro | 08-insights | REVIEW_REQUIRED | 10.3276% | 2.776 |
| pro | 09-reminders | REVIEW_REQUIRED | 3.3586% | 1.212 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3603% | 1.221 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3601% | 1.303 |
| pro | 12-history | REVIEW_REQUIRED | 0.5071% | 0.661 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3520% | 9.391 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3603% | 1.197 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5116% | 0.670 |
| pro | 16-savings | REVIEW_REQUIRED | 3.3648% | 1.315 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5116% | 0.670 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3528% | 1.383 |
| pro | 19-privacy | REVIEW_REQUIRED | 3.3649% | 1.329 |
| pro | 20-about | REVIEW_REQUIRED | 0.7197% | 0.918 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.3709% | 1.553 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.3696% | 1.266 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7728% | 2.053 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3438% | 2.004 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5166% | 0.671 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
