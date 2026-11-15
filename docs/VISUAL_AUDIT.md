# SubliX v0.1.6 — real Android visual regression

Compared to captured v0.1.5 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 4.7866% | 1.997 |
| free | 02-subscriptions | REVIEW_REQUIRED | 8.0673% | 3.928 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.1623% | 0.250 |
| free | 04-service-library | REVIEW_REQUIRED | 0.1489% | 0.234 |
| free | 05-details | REVIEW_REQUIRED | 0.1642% | 0.248 |
| free | 06-calendar | REVIEW_REQUIRED | 0.3804% | 0.492 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.1701% | 0.260 |
| free | 08-insights | REVIEW_REQUIRED | 0.1549% | 0.243 |
| free | 09-reminders | REVIEW_REQUIRED | 0.1549% | 0.243 |
| free | 10-settings | REVIEW_REQUIRED | 0.1553% | 0.244 |
| free | 11-categories | REVIEW_REQUIRED | 0.1551% | 0.246 |
| free | 12-history | REVIEW_REQUIRED | 0.1594% | 0.245 |
| free | 13-archive | REVIEW_REQUIRED | 5.4684% | 2.355 |
| free | 14-backup | REVIEW_REQUIRED | 0.1495% | 0.236 |
| free | 15-budget | REVIEW_REQUIRED | 0.1495% | 0.236 |
| free | 16-savings | REVIEW_REQUIRED | 0.1495% | 0.236 |
| free | 17-trials | REVIEW_REQUIRED | 0.1617% | 0.248 |
| free | 18-pro | REVIEW_REQUIRED | 0.1495% | 0.236 |
| free | 19-privacy | REVIEW_REQUIRED | 0.1495% | 0.236 |
| free | 20-about | REVIEW_REQUIRED | 0.1587% | 0.242 |
| pro | 01-home | REVIEW_REQUIRED | 0.2265% | 0.306 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 1.4923% | 1.448 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.1611% | 0.249 |
| pro | 04-service-library | REVIEW_REQUIRED | 0.1477% | 0.233 |
| pro | 05-details | REVIEW_REQUIRED | 0.1630% | 0.247 |
| pro | 06-calendar | REVIEW_REQUIRED | 0.3678% | 0.455 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.1576% | 0.239 |
| pro | 08-insights | REVIEW_REQUIRED | 0.1425% | 0.222 |
| pro | 09-reminders | REVIEW_REQUIRED | 0.1425% | 0.222 |
| pro | 10-settings | REVIEW_REQUIRED | 0.1425% | 0.222 |
| pro | 11-categories | REVIEW_REQUIRED | 0.1425% | 0.222 |
| pro | 12-history | REVIEW_REQUIRED | 0.1524% | 0.231 |
| pro | 13-archive | REVIEW_REQUIRED | 5.4614% | 2.326 |
| pro | 14-backup | REVIEW_REQUIRED | 0.1425% | 0.222 |
| pro | 15-budget | REVIEW_REQUIRED | 0.1481% | 0.232 |
| pro | 16-savings | REVIEW_REQUIRED | 0.1481% | 0.232 |
| pro | 17-trials | REVIEW_REQUIRED | 0.1603% | 0.245 |
| pro | 18-pro | REVIEW_REQUIRED | 0.1481% | 0.232 |
| pro | 19-privacy | REVIEW_REQUIRED | 0.1481% | 0.232 |
| pro | 20-about | REVIEW_REQUIRED | 0.1573% | 0.239 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 0.1500% | 0.235 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 0.1500% | 0.235 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 0.1504% | 0.234 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 0.1500% | 0.235 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.1603% | 0.245 |

**Identical to v0.1.5: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
