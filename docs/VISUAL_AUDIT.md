# SubliX emulator visual regression audit

These are **measured actual emulator screenshots**, not generated mockups.
Comparison: candidate v0.1.2 emulator captures versus newly rendered emulator captures.
A 1:1 pass requires independently approved design baselines, supported device sizes, and **zero unintended pixel deviations**; a build passing CI is not proof of 1:1.
Expected differences from new features, emulator status bars, timestamps and layout changes need human review. This report does not auto-approve them.

| Edition | Screen | Pixel identity vs prior capture | Changed pixels (>8 luminance) | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | DIFFERENT | 0.7649% | 0.997 |
| free | 02-subscriptions | DIFFERENT | 1.1122% | 1.487 |
| free | 03-add-subscription | DIFFERENT | 0.1630% | 0.251 |
| free | 04-service-library | DIFFERENT | 0.1496% | 0.236 |
| free | 05-details | DIFFERENT | 1.5611% | 2.797 |
| free | 06-calendar | DIFFERENT | 1.6644% | 2.471 |
| free | 07-payment-details | DIFFERENT | 0.1706% | 0.271 |
| free | 08-insights | DIFFERENT | 0.2933% | 0.462 |
| free | 09-reminders | DIFFERENT | 0.1624% | 0.258 |
| free | 10-settings | DIFFERENT | 0.1620% | 0.259 |
| free | 11-categories | DIFFERENT | 0.1571% | 0.252 |
| free | 12-history | DIFFERENT | 0.1670% | 0.261 |
| free | 13-archive | DIFFERENT | 0.1671% | 0.26 |
| free | 14-backup | DIFFERENT | 0.1571% | 0.252 |
| free | 15-budget | DIFFERENT | 0.2338% | 0.378 |
| free | 16-savings | DIFFERENT | 0.2690% | 0.425 |
| free | 17-trials | DIFFERENT | 0.1674% | 0.26 |
| free | 18-pro | DIFFERENT | 0.1552% | 0.248 |
| free | 19-privacy | DIFFERENT | 0.1552% | 0.248 |
| free | 20-about | DIFFERENT | 0.1705% | 0.261 |
| pro | 01-home | DIFFERENT | 11.3148% | 5.093 |
| pro | 02-subscriptions | DIFFERENT | 19.7187% | 6.28 |
| pro | 03-add-subscription | DIFFERENT | 0.1665% | 0.26 |
| pro | 04-service-library | DIFFERENT | 0.1531% | 0.244 |
| pro | 05-details | DIFFERENT | 1.5645% | 2.805 |
| pro | 06-calendar | DIFFERENT | 1.6557% | 2.427 |
| pro | 07-payment-details | DIFFERENT | 0.1683% | 0.261 |
| pro | 08-insights | DIFFERENT | 32.3019% | 11.55 |
| pro | 09-reminders | DIFFERENT | 0.1587% | 0.254 |
| pro | 10-settings | DIFFERENT | 0.1587% | 0.254 |
| pro | 11-categories | DIFFERENT | 0.1602% | 0.259 |
| pro | 12-history | DIFFERENT | 0.1701% | 0.268 |
| pro | 13-archive | DIFFERENT | 0.1702% | 0.268 |
| pro | 14-backup | DIFFERENT | 0.1602% | 0.259 |
| pro | 15-budget | DIFFERENT | 0.2714% | 0.422 |
| pro | 16-savings | DIFFERENT | 2.5795% | 2.297 |
| pro | 17-trials | DIFFERENT | 0.1767% | 0.28 |
| pro | 18-pro | DIFFERENT | 30.0805% | 12.564 |
| pro | 19-privacy | DIFFERENT | 0.1645% | 0.267 |
| pro | 20-about | DIFFERENT | 0.1819% | 0.284 |

**Exact matches to prior release: 0/40.**

The five additional Pro screens (Forecast, Category Budgets, What-if Planner, Reports, Price History) are new in v0.1.3 and have no prior approved screenshot baseline.
The 20 original SVG design concepts are illustrative rather than validated design-system specifications. Their content and aspect ratios differ from emulator screenshots. Achieving literal 1:1 with those requires a design review and rebaselining process.
