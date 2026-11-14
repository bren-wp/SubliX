# SubliX emulator visual regression audit

These are **measured actual emulator screenshots**, not generated mockups.
Comparison: candidate v0.1.2 emulator captures versus newly rendered emulator captures.
A 1:1 pass requires independently approved design baselines, supported device sizes, and **zero unintended pixel deviations**; a build passing CI is not proof of 1:1.
Expected differences from new features, emulator status bars, timestamps and layout changes need human review. This report does not auto-approve them.

| Edition | Screen | Pixel identity vs prior capture | Changed pixels (>8 luminance) | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | DIFFERENT | 0.7586% | 0.982 |
| free | 02-subscriptions | DIFFERENT | 1.1349% | 1.524 |
| free | 03-add-subscription | DIFFERENT | 0.1422% | 0.226 |
| free | 04-service-library | DIFFERENT | 0.1422% | 0.226 |
| free | 05-details | DIFFERENT | 1.5562% | 2.79 |
| free | 06-calendar | DIFFERENT | 1.6644% | 2.479 |
| free | 07-payment-details | DIFFERENT | 0.1550% | 0.248 |
| free | 08-insights | DIFFERENT | 0.2859% | 0.452 |
| free | 09-reminders | DIFFERENT | 0.1550% | 0.248 |
| free | 10-settings | DIFFERENT | 0.1550% | 0.248 |
| free | 11-categories | DIFFERENT | 0.1546% | 0.25 |
| free | 12-history | DIFFERENT | 0.1507% | 0.242 |
| free | 13-archive | DIFFERENT | 0.1507% | 0.242 |
| free | 14-backup | DIFFERENT | 0.1507% | 0.242 |
| free | 15-budget | DIFFERENT | 0.2275% | 0.368 |
| free | 16-savings | DIFFERENT | 0.2626% | 0.415 |
| free | 17-trials | DIFFERENT | 0.1488% | 0.238 |
| free | 18-pro | DIFFERENT | 0.1488% | 0.238 |
| free | 19-privacy | DIFFERENT | 0.1488% | 0.238 |
| free | 20-about | DIFFERENT | 0.1685% | 0.259 |
| pro | 01-home | DIFFERENT | 11.3105% | 5.087 |
| pro | 02-subscriptions | DIFFERENT | 19.7154% | 6.274 |
| pro | 03-add-subscription | DIFFERENT | 0.1488% | 0.238 |
| pro | 04-service-library | DIFFERENT | 0.1488% | 0.238 |
| pro | 05-details | DIFFERENT | 1.5628% | 2.802 |
| pro | 06-calendar | DIFFERENT | 1.6592% | 2.436 |
| pro | 07-payment-details | DIFFERENT | 0.1488% | 0.238 |
| pro | 08-insights | DIFFERENT | 32.2920% | 11.533 |
| pro | 09-reminders | DIFFERENT | 0.1509% | 0.242 |
| pro | 10-settings | DIFFERENT | 0.1509% | 0.242 |
| pro | 11-categories | DIFFERENT | 0.1524% | 0.247 |
| pro | 12-history | DIFFERENT | 0.1524% | 0.247 |
| pro | 13-archive | DIFFERENT | 0.1524% | 0.247 |
| pro | 14-backup | DIFFERENT | 0.1524% | 0.247 |
| pro | 15-budget | DIFFERENT | 0.2646% | 0.414 |
| pro | 16-savings | DIFFERENT | 2.5731% | 2.286 |
| pro | 17-trials | DIFFERENT | 0.1580% | 0.257 |
| pro | 18-pro | DIFFERENT | 30.0740% | 12.554 |
| pro | 19-privacy | DIFFERENT | 0.1580% | 0.257 |
| pro | 20-about | DIFFERENT | 0.1778% | 0.278 |

**Exact matches to prior release: 0/40.**

The five additional Pro screens (Forecast, Category Budgets, What-if Planner, Reports, Price History) are new in v0.1.3 and have no prior approved screenshot baseline.
The 20 original SVG design concepts are illustrative rather than validated design-system specifications. Their content and aspect ratios differ from emulator screenshots. Achieving literal 1:1 with those requires a design review and rebaselining process.
