#!/usr/bin/env python3
"""Switch marketing README from SVG illustrations to real emulator-captured images."""
from pathlib import Path

root = Path(__file__).resolve().parents[1]
readme = root / 'README.md'
text = readme.read_text(encoding='utf-8')
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
    "brending/screenshots-real/pro/21-pro-forecast.png",
    "brending/screenshots-real/pro/22-pro-category-budgets.png",
    "brending/screenshots-real/pro/23-pro-planner.png",
    "brending/screenshots-real/pro/24-pro-reports.png",
    "brending/screenshots-real/pro/25-pro-price-history.png",
]
for name in images:
    p=root/name
    assert p.is_file() and p.stat().st_size>10000, f'Missing actual emulator screenshot: {name}'
# Retain the original verified screenshot gallery and add new genuine Pro screens.
if '## SubliX Pro advanced tools · actual emulator captures' not in text:
    extra = """## SubliX Pro advanced tools · actual emulator captures

These are screenshots from the **running Pro edition** on an Android emulator with synthetic records. The 0.1.2 reference PNGs remain under `brending/baselines/v0.1.2`.

| 12-month forecast | Category budgets | What-if planner | CSV reports | Price history |
| :---: | :---: | :---: | :---: | :---: |
| <img src="brending/screenshots-real/pro/21-pro-forecast.png" width="180" alt="Real Pro forecast Android screenshot" /> | <img src="brending/screenshots-real/pro/22-pro-category-budgets.png" width="180" alt="Real Pro category budget Android screenshot" /> | <img src="brending/screenshots-real/pro/23-pro-planner.png" width="180" alt="Real Pro savings scenario screenshot" /> | <img src="brending/screenshots-real/pro/24-pro-reports.png" width="180" alt="Real Pro reports Android screenshot" /> | <img src="brending/screenshots-real/pro/25-pro-price-history.png" width="180" alt="Real Pro price history Android screenshot" /> |

For numerical visual regression metrics (not a 1:1 certification), see [visual audit](docs/VISUAL_AUDIT.md).

"""
    text=text.replace("## Why SubliX?",extra+"## Why SubliX?")
    readme.write_text(text,encoding='utf-8')
    print("README enhanced with genuine Pro screenshots.")
else:
    print("Pro screenshot gallery already present.")
