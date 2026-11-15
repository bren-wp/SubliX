# SubliX v0.1.7 — real Android visual regression

Compared to captured v0.1.6 baselines across **all 45 screens**.
Every candidate and baseline is an authentic, unmodified Android emulator PNG.
Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.

**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,
not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.
There are no masks, crops, filtered regions or fabricated screenshots in the comparison.

| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | REVIEW_REQUIRED | 10.6056% | 4.185 |
| free | 02-subscriptions | REVIEW_REQUIRED | 31.2499% | 10.327 |
| free | 03-add-subscription | REVIEW_REQUIRED | 0.7193% | 0.848 |
| free | 04-service-library | REVIEW_REQUIRED | 3.3935% | 1.371 |
| free | 05-details | REVIEW_REQUIRED | 0.5401% | 0.735 |
| free | 06-calendar | REVIEW_REQUIRED | 8.4432% | 2.591 |
| free | 07-payment-details | REVIEW_REQUIRED | 0.5447% | 0.744 |
| free | 08-insights | REVIEW_REQUIRED | 8.9232% | 2.746 |
| free | 09-reminders | REVIEW_REQUIRED | 3.3962% | 1.293 |
| free | 10-settings | REVIEW_REQUIRED | 3.3980% | 1.302 |
| free | 11-categories | REVIEW_REQUIRED | 3.3938% | 1.377 |
| free | 12-history | REVIEW_REQUIRED | 0.5408% | 0.734 |
| free | 13-archive | REVIEW_REQUIRED | 13.4868% | 7.569 |
| free | 14-backup | REVIEW_REQUIRED | 3.3940% | 1.271 |
| free | 15-budget | REVIEW_REQUIRED | 0.5408% | 0.734 |
| free | 16-savings | REVIEW_REQUIRED | 0.5408% | 0.734 |
| free | 17-trials | REVIEW_REQUIRED | 0.5408% | 0.734 |
| free | 18-pro | REVIEW_REQUIRED | 0.5408% | 0.734 |
| free | 19-privacy | REVIEW_REQUIRED | 3.3940% | 1.393 |
| free | 20-about | REVIEW_REQUIRED | 0.7508% | 0.986 |
| pro | 01-home | REVIEW_REQUIRED | 9.4488% | 3.987 |
| pro | 02-subscriptions | REVIEW_REQUIRED | 23.3914% | 10.372 |
| pro | 03-add-subscription | REVIEW_REQUIRED | 0.7182% | 0.844 |
| pro | 04-service-library | REVIEW_REQUIRED | 3.3924% | 1.365 |
| pro | 05-details | REVIEW_REQUIRED | 0.5391% | 0.731 |
| pro | 06-calendar | REVIEW_REQUIRED | 8.4393% | 2.583 |
| pro | 07-payment-details | REVIEW_REQUIRED | 0.5410% | 0.734 |
| pro | 08-insights | REVIEW_REQUIRED | 10.3615% | 2.849 |
| pro | 09-reminders | REVIEW_REQUIRED | 3.3981% | 1.296 |
| pro | 10-settings | REVIEW_REQUIRED | 3.3999% | 1.304 |
| pro | 11-categories | REVIEW_REQUIRED | 3.3997% | 1.387 |
| pro | 12-history | REVIEW_REQUIRED | 0.5467% | 0.744 |
| pro | 13-archive | REVIEW_REQUIRED | 13.4928% | 7.277 |
| pro | 14-backup | REVIEW_REQUIRED | 3.3999% | 1.281 |
| pro | 15-budget | REVIEW_REQUIRED | 0.5478% | 0.746 |
| pro | 16-savings | REVIEW_REQUIRED | 3.4009% | 1.391 |
| pro | 17-trials | REVIEW_REQUIRED | 0.5478% | 0.746 |
| pro | 18-pro | REVIEW_REQUIRED | 3.3867% | 1.456 |
| pro | 19-privacy | REVIEW_REQUIRED | 3.3988% | 1.402 |
| pro | 20-about | REVIEW_REQUIRED | 0.7555% | 0.995 |
| pro | 21-pro-forecast | REVIEW_REQUIRED | 3.4049% | 1.626 |
| pro | 22-pro-category-budgets | REVIEW_REQUIRED | 3.4036% | 1.339 |
| pro | 23-pro-planner | REVIEW_REQUIRED | 1.8471% | 0.938 |
| pro | 24-pro-reports | REVIEW_REQUIRED | 3.3795% | 2.083 |
| pro | 25-pro-price-history | REVIEW_REQUIRED | 0.5469% | 0.747 |

**Identical to v0.1.6: 0/45.**
Concept viewboxes: 432x960; emulator viewport: 1080x1920. Aspect ratios differ.

Intentional content changes can produce legitimate differences; each non-identical region still requires review.
The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.
This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.
