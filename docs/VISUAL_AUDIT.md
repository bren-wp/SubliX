# SubliX v0.1.14 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 7.2342% | 6.142 |
| free | 02-subscriptions | REVIEW_REQUIRED | 25.0880% | 9.463 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6809% | 0.766 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3552% | 1.289 |
| free | 05-details | REVIEW_REQUIRED | 0.5018% | 0.653 |
| free | 06-calendar | REVIEW_REQUIRED | 23.0307% | 10.345 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5074% | 0.663 |
| free | 08-insights | REVIEW_REQUIRED | 2.7625% | 1.882 |
| free | 09-reminders | REVIEW_REQUIRED | 4.3635% | 2.192 |
| free | 10-settings | REVIEW_REQUIRED | 3.3611% | 1.222 |
| free | 11-categories | REVIEW_REQUIRED | 3.3612% | 1.307 |
| free | 12-history | REVIEW_REQUIRED | 0.5082% | 0.664 |
| free | 13-archive | REVIEW_REQUIRED | 22.3536% | 9.697 |
| free | 14-backup | REVIEW_REQUIRED | 3.3614% | 1.201 |
| free | 15-budget | REVIEW_REQUIRED | 8.1949% | 4.338 |
| free | 16-savings | REVIEW_REQUIRED | 30.8539% | 10.544 |
| free | 17-trials | REVIEW_REQUIRED | 0.5054% | 0.656 |
| free | 18-pro | REVIEW_REQUIRED | 41.2804% | 15.565 |
| free | 19-privacy | REVIEW_REQUIRED | 6.5752% | 2.875 |
| free | 20-about | REVIEW_REQUIRED | 0.7269% | 0.922 |
| pro | 01-home | REVIEW_REQUIRED | 6.4411% | 5.985 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 17.2270% | 9.502 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6788% | 0.760 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3530% | 1.281 |
| pro | 05-details | REVIEW_REQUIRED | 0.4996% | 0.647 |
| pro | 06-calendar | REVIEW_REQUIRED | 23.0234% | 10.255 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.4996% | 0.647 |
| pro | 08-insights | REVIEW_REQUIRED | 4.1970% | 1.979 |
| pro | 09-reminders | REVIEW_REQUIRED | 4.3557% | 2.176 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3529% | 1.207 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3527% | 1.289 |
| pro | 12-history | REVIEW_REQUIRED | 0.4996% | 0.647 |
| pro | 13-archive | REVIEW_REQUIRED | 22.3446% | 9.377 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3529% | 1.183 |
| pro | 15-budget | REVIEW_REQUIRED | 1.3546% | 1.865 |
| pro | 16-savings | REVIEW_REQUIRED | 4.1197% | 1.969 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5057% | 0.658 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3468% | 1.372 |
| pro | 19-privacy | REVIEW_REQUIRED | 6.5755% | 2.877 |
| pro | 20-about | REVIEW_REQUIRED | 0.7272% | 0.924 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.9683% | 2.622 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.6045% | 1.303 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7652% | 2.038 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3351% | 1.988 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5204% | 0.668 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
