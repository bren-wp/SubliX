#!/usr/bin/env python3
"""Switch marketing README from SVG illustrations to real emulator-captured images."""
from pathlib import Path

root = Path(__file__).resolve().parents[1]
readme = root / 'README.md'
start_marker = '## Designed around clarity'
end_marker = '## Why SubliX?'
text = readme.read_text(encoding='utf-8')
start = text.index(start_marker)
end = text.index(end_marker)
images = [
    "brending/screenshots-real/free/01-home.png",
    "brending/screenshots-real/pro/01-home.png",
    "brending/screenshots-real/free/02-subscriptions.png",
    "brending/screenshots-real/pro/02-subscriptions.png",
    "brending/screenshots-real/free/03-add-subscription.png",
    "brending/screenshots-real/pro/03-add-subscription.png",
    "brending/screenshots-real/free/04-service-library.png",
    "brending/screenshots-real/pro/04-service-library.png",
    "brending/screenshots-real/free/05-details.png",
    "brending/screenshots-real/pro/05-details.png",
    "brending/screenshots-real/free/06-calendar.png",
    "brending/screenshots-real/pro/06-calendar.png",
    "brending/screenshots-real/free/07-payment-details.png",
    "brending/screenshots-real/pro/07-payment-details.png",
    "brending/screenshots-real/free/08-insights.png",
    "brending/screenshots-real/pro/08-insights.png",
    "brending/screenshots-real/free/09-reminders.png",
    "brending/screenshots-real/pro/09-reminders.png",
    "brending/screenshots-real/free/10-settings.png",
    "brending/screenshots-real/pro/10-settings.png",
    "brending/screenshots-real/free/11-categories.png",
    "brending/screenshots-real/pro/11-categories.png",
    "brending/screenshots-real/free/12-history.png",
    "brending/screenshots-real/pro/12-history.png",
    "brending/screenshots-real/free/13-archive.png",
    "brending/screenshots-real/pro/13-archive.png",
    "brending/screenshots-real/free/14-backup.png",
    "brending/screenshots-real/pro/14-backup.png",
    "brending/screenshots-real/free/15-budget.png",
    "brending/screenshots-real/pro/15-budget.png",
    "brending/screenshots-real/free/16-savings.png",
    "brending/screenshots-real/pro/16-savings.png",
    "brending/screenshots-real/free/17-trials.png",
    "brending/screenshots-real/pro/17-trials.png",
    "brending/screenshots-real/free/18-pro.png",
    "brending/screenshots-real/pro/18-pro.png",
    "brending/screenshots-real/free/19-privacy.png",
    "brending/screenshots-real/pro/19-privacy.png",
    "brending/screenshots-real/free/20-about.png",
    "brending/screenshots-real/pro/20-about.png",
]
for name in images:
    p=root/name
    assert p.is_file() and p.stat().st_size>10000, f'Missing actual emulator screenshot: {name}'
new_section = "## Real Android screenshots · Free and Pro\n\nThe following PNGs are captured from **running Android emulator installations** of SubliX Free and Pro using synthetic sample subscriptions. They are not design mockups or AI-generated screenshots. All 20 mapped screens are captured per edition for interface review. Pixel-perfect 1:1 parity with the reference artwork requires separate manual/automated visual comparison.\n\n### SubliX Free — actual captures\n\n| Home | Subscriptions | Add Subscription | Service Library |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/free/01-home.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 01-home\" /> | <img src=\"brending/screenshots-real/free/02-subscriptions.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 02-subscriptions\" /> | <img src=\"brending/screenshots-real/free/03-add-subscription.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 03-add-subscription\" /> | <img src=\"brending/screenshots-real/free/04-service-library.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 04-service-library\" /> |\n\n| Details | Calendar | Payment Details | Insights |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/free/05-details.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 05-details\" /> | <img src=\"brending/screenshots-real/free/06-calendar.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 06-calendar\" /> | <img src=\"brending/screenshots-real/free/07-payment-details.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 07-payment-details\" /> | <img src=\"brending/screenshots-real/free/08-insights.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 08-insights\" /> |\n\n| Reminders | Settings | Categories | History |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/free/09-reminders.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 09-reminders\" /> | <img src=\"brending/screenshots-real/free/10-settings.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 10-settings\" /> | <img src=\"brending/screenshots-real/free/11-categories.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 11-categories\" /> | <img src=\"brending/screenshots-real/free/12-history.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 12-history\" /> |\n\n| Archive | Backup | Budget | Savings |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/free/13-archive.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 13-archive\" /> | <img src=\"brending/screenshots-real/free/14-backup.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 14-backup\" /> | <img src=\"brending/screenshots-real/free/15-budget.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 15-budget\" /> | <img src=\"brending/screenshots-real/free/16-savings.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 16-savings\" /> |\n\n| Trials | Pro | Privacy | About |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/free/17-trials.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 17-trials\" /> | <img src=\"brending/screenshots-real/free/18-pro.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 18-pro\" /> | <img src=\"brending/screenshots-real/free/19-privacy.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 19-privacy\" /> | <img src=\"brending/screenshots-real/free/20-about.png\" width=\"190\" alt=\"Actual SubliX free Android screenshot: 20-about\" /> |\n\n### SubliX Pro — actual captures\n\n| Home | Subscriptions | Add Subscription | Service Library |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/pro/01-home.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 01-home\" /> | <img src=\"brending/screenshots-real/pro/02-subscriptions.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 02-subscriptions\" /> | <img src=\"brending/screenshots-real/pro/03-add-subscription.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 03-add-subscription\" /> | <img src=\"brending/screenshots-real/pro/04-service-library.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 04-service-library\" /> |\n\n| Details | Calendar | Payment Details | Insights |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/pro/05-details.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 05-details\" /> | <img src=\"brending/screenshots-real/pro/06-calendar.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 06-calendar\" /> | <img src=\"brending/screenshots-real/pro/07-payment-details.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 07-payment-details\" /> | <img src=\"brending/screenshots-real/pro/08-insights.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 08-insights\" /> |\n\n| Reminders | Settings | Categories | History |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/pro/09-reminders.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 09-reminders\" /> | <img src=\"brending/screenshots-real/pro/10-settings.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 10-settings\" /> | <img src=\"brending/screenshots-real/pro/11-categories.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 11-categories\" /> | <img src=\"brending/screenshots-real/pro/12-history.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 12-history\" /> |\n\n| Archive | Backup | Budget | Savings |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/pro/13-archive.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 13-archive\" /> | <img src=\"brending/screenshots-real/pro/14-backup.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 14-backup\" /> | <img src=\"brending/screenshots-real/pro/15-budget.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 15-budget\" /> | <img src=\"brending/screenshots-real/pro/16-savings.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 16-savings\" /> |\n\n| Trials | Pro | Privacy | About |\n| :---: | :---: | :---: | :---: |\n| <img src=\"brending/screenshots-real/pro/17-trials.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 17-trials\" /> | <img src=\"brending/screenshots-real/pro/18-pro.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 18-pro\" /> | <img src=\"brending/screenshots-real/pro/19-privacy.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 19-privacy\" /> | <img src=\"brending/screenshots-real/pro/20-about.png\" width=\"190\" alt=\"Actual SubliX pro Android screenshot: 20-about\" /> |\n\nFor layout specifications, original vector references and known open visual gaps, see [Screen Spec](docs/SCREEN_SPEC.md) and [Brand Guide](brending/BRAND_GUIDE.md).\n\n"
readme.write_text(text[:start]+new_section+text[end:],encoding='utf-8')
print("README now uses genuine emulator screenshots.")
