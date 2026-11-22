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

# The freshly booted API 35 emulator may still be starting its launcher and
# Android system services. Let it settle before the first foreground capture.
# Grant the declared notification permission in this DEBUG-only fixture so that
# the first app launch cannot be blocked by Android's runtime permission dialog.
for package in com.brendigo.sublix com.brendigo.sublixpro; do
  adb shell pm grant "$package" android.permission.POST_NOTIFICATIONS
done
sleep 20

# Keep rejected frames ONLY in a CI diagnostic artifact, never the real capture,
# website, or Play media folders. A failing check must remain a failing check.
record_capture_failure() {
  local edition="$1" name="$2" attempt="$3" output="$4"
  local destination="build/visual-rejected/$edition"
  mkdir -p "$destination"
  cp "$output" "$destination/$name-attempt-$attempt.png"
  adb shell dumpsys window > "$destination/$name-attempt-$attempt-window.txt" 2>&1 || true
  adb shell dumpsys activity activities > "$destination/$name-attempt-$attempt-activities.txt" 2>&1 || true
  adb logcat -d -b crash -t 200 > "$destination/$name-attempt-$attempt-crashes.txt" 2>&1 || true
}

# Shared launcher-only recovery, independently regression-tested in CI.
source scripts/emulator_recovery.sh

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
  local attempt
  for attempt in 1 2 3; do
    adb shell am force-stop "$package" || true
    adb shell am start -W -n "$package/com.brendigo.sublix.MainActivity" --ez sublix_screenshot true --es capture_screen "$screen"
    sleep 1
    adb exec-out screencap -p > "$output"
    if python3 - "$output" <<'PY'
import pathlib, struct, sys
sys.path.insert(0, str(pathlib.Path("scripts").resolve()))
from validate_captures import validate_capture_image
p=pathlib.Path(sys.argv[1])
b=p.read_bytes()
assert b[:8]==b'\x89PNG\r\n\x1a\n', f"Not a PNG: {p}"
width,height=struct.unpack(">II",b[16:24])
assert width>=720 and height>=1280 and len(b)>10000, f"Invalid screenshot: {p} ({width}x{height}, {len(b)} bytes)"
validate_capture_image(p)
print("Verified actual Android emulator frame:",p,width,height,len(b))
PY
    then
      return 0
    fi
    # Retain evidence only in the explicitly rejected CI diagnostic directory.
    # Never keep invalid frames in the screenshot folder or publish them to Play.
    record_capture_failure "$edition" "$name" "$attempt" "$output"
    recover_emulator_launcher_anr "build/visual-rejected/$edition/$name-attempt-$attempt-window.txt"
    rm -f "$output"
    echo "Capture of $edition/$name failed validation (attempt $attempt/3)" >&2
    sleep 2
  done
  echo "Unable to capture clean frame for $edition/$name" >&2
  return 1
}

# One source of truth for every Free and Pro route; audit every route
# at the standard viewport and at both enlarged text scales.
CAPTURE_ROUTES=(
  'free HOME 01-home'
  'free PLANS 02-subscriptions'
  'free EDIT 03-add-subscription'
  'free LIBRARY 04-service-library'
  'free DETAILS 05-details'
  'free CALENDAR 06-calendar'
  'free PAYMENT 07-payment-details'
  'free INSIGHTS 08-insights'
  'free REMINDERS 09-reminders'
  'free SETTINGS 10-settings'
  'free CATEGORIES 11-categories'
  'free HISTORY 12-history'
  'free ARCHIVE 13-archive'
  'free BACKUP 14-backup'
  'free BUDGET 15-budget'
  'free SAVINGS 16-savings'
  'free TRIALS 17-trials'
  'free PRO 18-pro'
  'free PRIVACY 19-privacy'
  'free ABOUT 20-about'
  'pro HOME 01-home'
  'pro PLANS 02-subscriptions'
  'pro EDIT 03-add-subscription'
  'pro LIBRARY 04-service-library'
  'pro DETAILS 05-details'
  'pro CALENDAR 06-calendar'
  'pro PAYMENT 07-payment-details'
  'pro INSIGHTS 08-insights'
  'pro REMINDERS 09-reminders'
  'pro SETTINGS 10-settings'
  'pro CATEGORIES 11-categories'
  'pro HISTORY 12-history'
  'pro ARCHIVE 13-archive'
  'pro BACKUP 14-backup'
  'pro BUDGET 15-budget'
  'pro SAVINGS 16-savings'
  'pro TRIALS 17-trials'
  'pro PRO 18-pro'
  'pro PRIVACY 19-privacy'
  'pro ABOUT 20-about'
  'pro PRO_FORECAST 21-pro-forecast'
  'pro PRO_CATEGORY_BUDGETS 22-pro-category-budgets'
  'pro PRO_PLANNER 23-pro-planner'
  'pro PRO_REPORTS 24-pro-reports'
  'pro PRO_PRICE_HISTORY 25-pro-price-history'
)
for entry in "${CAPTURE_ROUTES[@]}"; do
  read -r edition screen name <<< "$entry"
  capture "$edition" "$screen" "$name"
done

# Adaptive frames are separate QA evidence, NEVER published as Play/screenshots.
# Do not resize, crop, paint over or normalize screenshots to hide bad layouts.
capture_adaptive() {
  local profile="$1" edition="$2" screen="$3" name="$4" width="$5" height="$6"
  local package
  if [ "$edition" = free ]; then package="com.brendigo.sublix"; else package="com.brendigo.sublixpro"; fi
  local output="build/adaptive/$profile/$edition/$name.png"
  mkdir -p "$(dirname "$output")"
  adb shell am force-stop "$package" || true
  adb shell am start -W -n "$package/com.brendigo.sublix.MainActivity" --ez sublix_screenshot true --es capture_screen "$screen"
  sleep 1
  adb exec-out screencap -p > "$output"
  python3 - "$output" "$width" "$height" <<'PY'
import pathlib, sys
sys.path.insert(0, str(pathlib.Path("scripts").resolve()))
from validate_captures import validate_capture_image
image=pathlib.Path(sys.argv[1])
validate_capture_image(image,expected_size=(int(sys.argv[2]),int(sys.argv[3])))
assert image.stat().st_size>10000, f"Empty or invalid adaptive screenshot: {image}"
print("Adaptive screenshot verified:",image)
PY
}

for profile in "large-text" "extra-large-text"; do
  adb shell wm size 900x2000
  adb shell wm density 420
  if [ "$profile" = large-text ]; then
    adb shell settings put system font_scale 1.3
  else
    adb shell settings put system font_scale 2.0
  fi
  for entry in "${CAPTURE_ROUTES[@]}"; do
    read -r edition screen name <<< "$entry"
    capture_adaptive "$profile" "$edition" "$screen" "$name" 900 2000
  done
done

# Check every Free and Pro route at an expanded landscape viewport.
# These are QA-only originals; do not publish them as Google Play captures.
adb shell wm size 1600x1200
adb shell wm density 320
adb shell settings put system font_scale 1.0
for entry in "${CAPTURE_ROUTES[@]}"; do
  read -r edition screen name <<< "$entry"
  capture_adaptive "tablet-landscape" "$edition" "$screen" "$name" 1600 1200
done
adb shell wm size 1080x1920
adb shell wm density 420
adb shell settings put system font_scale 1.0
python3 - <<'PY'
from pathlib import Path
counts = {
  "large-text": (20,25),
  "extra-large-text": (20,25),
  "tablet-landscape": (20,25),
}
for profile,(free,pro) in counts.items():
    for edition,expected in (("free",free),("pro",pro)):
        found=len(list((Path("build/adaptive")/profile/edition).glob("*.png")))
        assert found==expected,(profile,edition,found,expected)
print("Verified 90 enlarged-text frames and all 45 tablet/landscape frames.")
PY

# Only report crashes from our package IDs, not unrelated system processes.
if adb logcat -d -b crash | grep -E 'Process: com\.brendigo\.(sublix|sublixpro)(,|$)' ; then
  echo "SubliX application crash detected" >&2
  exit 1
fi
