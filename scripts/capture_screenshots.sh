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
  local package="com.brendigo.sublix.$edition"
  local output="brending/screenshots-real/$edition/$name.png"
  mkdir -p "$(dirname "$output")"
  adb shell am force-stop "$package" || true
  adb shell am start -W -n "$package/com.brendigo.sublix.MainActivity" --ez sublix_screenshot true --es capture_screen "$screen"
  sleep 3
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
capture free CALENDAR 03-calendar
capture free INSIGHTS 04-insights
capture free EDIT 05-add-subscription
capture free DETAILS 06-details
capture free SETTINGS 07-settings
capture free HISTORY 08-history
capture free ARCHIVE 09-archive
capture free ABOUT 10-about
capture pro HOME 01-home
capture pro INSIGHTS 02-insights-pro
capture pro DETAILS 03-details

# Detect crashing activity from current emulator session, rather than mislabeling a crash dialog as a screenshot.
if adb logcat -d -b crash | grep -E 'FATAL EXCEPTION.*|Process: com.brendigo.sublix\.(free|pro)' ; then
  echo "Android app crash detected during screenshot capture" >&2
  exit 1
fi
