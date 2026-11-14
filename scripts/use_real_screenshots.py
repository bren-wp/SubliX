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
    "brending/screenshots-real/free/02-subscriptions.png",
    "brending/screenshots-real/free/03-calendar.png",
    "brending/screenshots-real/free/04-insights.png",
    "brending/screenshots-real/free/05-add-subscription.png",
    "brending/screenshots-real/free/06-details.png",
    "brending/screenshots-real/free/07-settings.png",
    "brending/screenshots-real/pro/02-insights-pro.png",
]
for name in images:
    p=root/name
    assert p.is_file() and p.stat().st_size>10000, f'Missing actual emulator screenshot: {name}'
new_section = '''## Real screenshots · Android emulator

The PNG images below are **genuine frames from the running Kotlin/Compose Android apps** on an Android emulator, with **synthetic preview subscription records** and the dark UI. They are not SVG drawings, AI-generated screenshots, or photographs of a physical phone.

| Home · Free | Subscriptions · Free | Calendar · Free | Insights · Free |
|:---:|:---:|:---:|:---:|
| <img src="brending/screenshots-real/free/01-home.png" width="190" alt="Real Android emulator screenshot: Free Home" /> | <img src="brending/screenshots-real/free/02-subscriptions.png" width="190" alt="Real Android emulator screenshot: Free Subscriptions" /> | <img src="brending/screenshots-real/free/03-calendar.png" width="190" alt="Real Android emulator screenshot: Free Calendar" /> | <img src="brending/screenshots-real/free/04-insights.png" width="190" alt="Real Android emulator screenshot: Free Insights" /> |

| Add subscription · Free | Details · Free | Settings · Free | Insights · Pro |
|:---:|:---:|:---:|:---:|
| <img src="brending/screenshots-real/free/05-add-subscription.png" width="190" alt="Real Android emulator screenshot: Add Subscription" /> | <img src="brending/screenshots-real/free/06-details.png" width="190" alt="Real Android emulator screenshot: Subscription Details" /> | <img src="brending/screenshots-real/free/07-settings.png" width="190" alt="Real Android emulator screenshot: Settings" /> | <img src="brending/screenshots-real/pro/02-insights-pro.png" width="190" alt="Real Android emulator screenshot: Pro Insights" /> |

Other real screen captures and original branding references are in [brending/](brending/). Visual parity with the 20 design concepts still requires iterative comparison and implementation of missing screens.

'''
readme.write_text(text[:start]+new_section+text[end:],encoding='utf-8')
print("README now uses genuine emulator screenshots.")
