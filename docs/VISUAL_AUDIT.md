# SubliX v0.1.8 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 10.6122% | 4.205 |
| free | 02-subscriptions | REVIEW_REQUIRED | 31.2152% | 10.247 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.6845% | 0.768 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3587% | 1.292 |
| free | 05-details | REVIEW_REQUIRED | 0.5054% | 0.655 |
| free | 06-calendar | REVIEW_REQUIRED | 8.4051% | 2.504 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5065% | 0.657 |
| free | 08-insights | REVIEW_REQUIRED | 8.8829% | 2.656 |
| free | 09-reminders | REVIEW_REQUIRED | 3.3558% | 1.205 |
| free | 10-settings | REVIEW_REQUIRED | 3.3575% | 1.214 |
| free | 11-categories | REVIEW_REQUIRED | 3.3531% | 1.289 |
| free | 12-history | REVIEW_REQUIRED | 0.5000% | 0.646 |
| free | 13-archive | REVIEW_REQUIRED | 13.4460% | 7.481 |
| free | 14-backup | REVIEW_REQUIRED | 3.3533% | 1.183 |
| free | 15-budget | REVIEW_REQUIRED | 0.5000% | 0.646 |
| free | 16-savings | REVIEW_REQUIRED | 0.5067% | 0.661 |
| free | 17-trials | REVIEW_REQUIRED | 0.5067% | 0.661 |
| free | 18-pro | REVIEW_REQUIRED | 0.5067% | 0.661 |
| free | 19-privacy | REVIEW_REQUIRED | 3.3600% | 1.320 |
| free | 20-about | REVIEW_REQUIRED | 0.7105% | 0.903 |
| pro | 01-home | REVIEW_REQUIRED | 9.4148% | 3.914 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 23.3574% | 10.299 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.6838% | 0.772 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3581% | 1.293 |
| pro | 05-details | REVIEW_REQUIRED | 0.5047% | 0.658 |
| pro | 06-calendar | REVIEW_REQUIRED | 8.4031% | 2.508 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.5047% | 0.658 |
| pro | 08-insights | REVIEW_REQUIRED | 10.3252% | 2.774 |
| pro | 09-reminders | REVIEW_REQUIRED | 3.3562% | 1.210 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3579% | 1.219 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3577% | 1.301 |
| pro | 12-history | REVIEW_REQUIRED | 0.5047% | 0.658 |
| pro | 13-archive | REVIEW_REQUIRED | 13.4508% | 7.192 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3579% | 1.195 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5092% | 0.667 |
| pro | 16-savings | REVIEW_REQUIRED | 3.3607% | 1.309 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5075% | 0.664 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3486% | 1.377 |
| pro | 19-privacy | REVIEW_REQUIRED | 3.3607% | 1.323 |
| pro | 20-about | REVIEW_REQUIRED | 0.7113% | 0.906 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.3667% | 1.543 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.3655% | 1.257 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 2.7687% | 2.044 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3431% | 2.002 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5104% | 0.666 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
