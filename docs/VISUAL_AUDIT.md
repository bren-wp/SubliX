# SubliX emulator visual regression audit

These are **measured actual emulator screenshots**, not generated mockups.
Comparison: candidate v0.1.2 emulator captures versus newly rendered emulator captures.
A 1:1 pass requires independently approved design baselines, supported device sizes, and **zero unintended pixel deviations**; a build passing CI is not proof of 1:1.
Expected differences from new features, emulator status bars, timestamps and layout changes need human review. This report does not auto-approve them.

| Edition | Screen | Pixel identity vs prior capture | Changed pixels (>8 luminance) | Mean RGB delta |
| --- | --- | --- | ---: | ---: |
| free | 01-home | DIFFERENT | 0.7567% | 0.976 |
| free | 02-subscriptions | DIFFERENT | 1.0890% | 1.44 |
| free | 03-add-subscription | DIFFERENT | 0.0433% | 0.068 |
| free | 04-service-library | DIFFERENT | 0.0433% | 0.068 |
| free | 05-details | DIFFERENT | 1.4573% | 2.632 |
| free | 06-calendar | DIFFERENT | 1.5517% | 2.29 |
| free | 07-payment-details | DIFFERENT | 0.0419% | 0.061 |
| free | 08-insights | DIFFERENT | 0.1728% | 0.265 |
| free | 09-reminders | DIFFERENT | 0.0419% | 0.061 |
| free | 10-settings | DIFFERENT | 0.0414% | 0.06 |
| free | 11-categories | DIFFERENT | 0.0470% | 0.07 |
| free | 12-history | DIFFERENT | 0.0414% | 0.06 |
| free | 13-archive | DIFFERENT | 0.0414% | 0.06 |
| free | 14-backup | DIFFERENT | 0.0414% | 0.06 |
| free | 15-budget | DIFFERENT | 0.1182% | 0.186 |
| free | 16-savings | DIFFERENT | 0.1586% | 0.245 |
| free | 17-trials | DIFFERENT | 0.0448% | 0.068 |
| free | 18-pro | DIFFERENT | 0.0448% | 0.068 |
| free | 19-privacy | DIFFERENT | 0.0448% | 0.068 |
| free | 20-about | DIFFERENT | 0.0622% | 0.085 |
| pro | 01-home | DIFFERENT | 11.2065% | 4.917 |
| pro | 02-subscriptions | DIFFERENT | 19.6114% | 6.105 |
| pro | 03-add-subscription | DIFFERENT | 0.0448% | 0.068 |
| pro | 04-service-library | DIFFERENT | 0.0448% | 0.068 |
| pro | 05-details | DIFFERENT | 1.4588% | 2.632 |
| pro | 06-calendar | DIFFERENT | 1.5541% | 2.266 |
| pro | 07-payment-details | DIFFERENT | 0.0437% | 0.067 |
| pro | 08-insights | DIFFERENT | 32.1869% | 11.363 |
| pro | 09-reminders | DIFFERENT | 0.0437% | 0.067 |
| pro | 10-settings | DIFFERENT | 0.0437% | 0.067 |
| pro | 11-categories | DIFFERENT | 0.0478% | 0.08 |
| pro | 12-history | DIFFERENT | 0.0478% | 0.08 |
| pro | 13-archive | DIFFERENT | 0.0478% | 0.08 |
| pro | 14-backup | DIFFERENT | 0.0478% | 0.08 |
| pro | 15-budget | DIFFERENT | 0.1600% | 0.246 |
| pro | 16-savings | DIFFERENT | 2.4685% | 2.119 |
| pro | 17-trials | DIFFERENT | 0.0535% | 0.09 |
| pro | 18-pro | DIFFERENT | 29.9695% | 12.387 |
| pro | 19-privacy | DIFFERENT | 0.0535% | 0.09 |
| pro | 20-about | DIFFERENT | 0.0709% | 0.107 |

**Exact matches to prior release: 0/40.**

The five additional Pro screens (Forecast, Category Budgets, What-if Planner, Reports, Price History) are new in v0.1.3 and have no prior approved screenshot baseline.
The 20 original SVG design concepts are illustrative rather than validated design-system specifications. Their content and aspect ratios differ from emulator screenshots. Achieving literal 1:1 with those requires a design review and rebaselining process.
