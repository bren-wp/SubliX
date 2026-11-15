#!/usr/bin/env bash
set -euo pipefail

# Captures are produced exclusively by running the compiled Android apps on an emulator.
# Sample data are synthetic and injected into DEBUG builds only.
adb wait-for-device
adb shell settings put system font_scale 1.0
adb shell wm size 1080x1920
adb shell wm density 420

adb install -r app/build/outputs/apk/free/debug/app-free-debug.apk
adb install -r app/build/outputs/apk/pro/debug/app-pro-debug.apk

capture() {
  local edition="$1" screen="$2" name="$3"
  local package
  case "$edition" in
    free) package="com.brendigo.sublix" ;;
    pro) package="com.brendigo.sublixpro" ;;
    *) echo "Unsupported edition: $edition" >&2; return 1 ;;
  esac
  local output="brending/screenshots-real/$edition/$name.png"
  mkdir -p "$(dirname "$output")"
  adb shell am force-stop "$package" || true
  adb shell am start -W -n "$package/com.brendigo.sublix.MainActivity" --ez sublix_screenshot true --es capture_screen "$screen"
  sleep 1
  adb exec-out screencap -p > "$output"
  file "$output"
  python3 - "$output" <<'PY'
import struct, pathlib, sys
p=pathlib.Path(sys.argv[1]); b=p.read_bytes()
assert b[:8]==b'\x89PNG\r\n\x1a\n', f"Not a PNG: {p}"
width,height=struct.unpack(">II",b[16:24])
assert width>=720 and height>=1280 and len(b)>10000, f"Invalid/suspicious screenshot: {p} ({width}x{height}, {len(b)} bytes)"
print("Captured authentic Android emulator frame:",p,width,height,len(b))
PY
}

capture free HOME 01-home
capture free PLANS 02-subscriptions
capture free EDIT 03-add-subscription
capture free LIBRARY 04-service-library
capture free DETAILS 05-details
capture free CALENDAR 06-calendar
capture free PAYMENT 07-payment-details
capture free INSIGHTS 08-insights
capture free REMINDERS 09-reminders
capture free SETTINGS 10-settings
capture free CATEGORIES 11-categories
capture free HISTORY 12-history
capture free ARCHIVE 13-archive
capture free BACKUP 14-backup
capture free BUDGET 15-budget
capture free SAVINGS 16-savings
capture free TRIALS 17-trials
capture free PRO 18-pro
capture free PRIVACY 19-privacy
capture free ABOUT 20-about
capture pro HOME 01-home
capture pro PLANS 02-subscriptions
capture pro EDIT 03-add-subscription
capture pro LIBRARY 04-service-library
capture pro DETAILS 05-details
capture pro CALENDAR 06-calendar
capture pro PAYMENT 07-payment-details
capture pro INSIGHTS 08-insights
capture pro REMINDERS 09-reminders
capture pro SETTINGS 10-settings
capture pro CATEGORIES 11-categories
capture pro HISTORY 12-history
capture pro ARCHIVE 13-archive
capture pro BACKUP 14-backup
capture pro BUDGET 15-budget
capture pro SAVINGS 16-savings
capture pro TRIALS 17-trials
capture pro PRO 18-pro
capture pro PRIVACY 19-privacy
capture pro ABOUT 20-about
capture pro PRO_FORECAST 21-pro-forecast
capture pro PRO_CATEGORY_BUDGETS 22-pro-category-budgets
capture pro PRO_PLANNER 23-pro-planner
capture pro PRO_REPORTS 24-pro-reports
capture pro PRO_PRICE_HISTORY 25-pro-price-history

# Detect crashing activity from current emulator session, rather than mislabeling a crash dialog as a screenshot.
# Additional adaptive smoke: ~343dp at 1.3x system font; no retouching.
# These eight frames are QA-only and are never used as Google Play screenshots.
mkdir -p build/adaptive/free build/adaptive/pro
adb shell wm size 900x2000
adb shell wm density 420
adb shell settings put system font_scale 1.3
for edition in free pro; do
  if [ "$edition" = free ]; then package="com.brendigo.sublix"; else package="com.brendigo.sublixpro"; fi
  for screen in HOME PLANS INSIGHTS PRO; do
    adb shell am force-stop "$package" || true
    adb shell am start -W -n "$package/com.brendigo.sublix.MainActivity" --ez sublix_screenshot true --es capture_screen "$screen"
    sleep 1
    adb exec-out screencap -p > "build/adaptive/$edition/$screen.png"
  done
done
adb shell wm size 1080x1920
adb shell settings put system font_scale 1.0
python3 - <<'PY'
from pathlib import Path
import struct
files=list(Path("build/adaptive").glob("*/*.png"))
assert len(files)==8, f"Expected 8 adaptive captures; got {len(files)}"
for f in files:
    b=f.read_bytes()
    assert b[:8]==b'\x89PNG\r\n\x1a\n',f
    assert struct.unpack(">II",b[16:24])==(900,2000),f
    assert len(b)>10000,f
print("8 adaptive screenshots verified at 900x2000, 1.3x system font.")
PY

# Only report crashes from our package IDs, not unrelated system processes.
if adb logcat -d -b crash | grep -E 'Process: com\.brendigo\.(sublix|sublixpro)(,|$)' ; then
  echo "SubliX application crash detected" >&2
  exit 1
fi
