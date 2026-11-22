#!/usr/bin/env bash
# Recover a *system launcher* ANR on disposable CI emulator only.
# The app's own ANRs are NOT dismissed, and every image is still validated.
recover_emulator_launcher_anr() {
  local window_dump="$1"
  if grep -Eq 'mCurrentFocus=.*Application Not Responding: com\.android\.launcher3' "$window_dump"; then
    echo "::warning::Emulator Quickstep ANR detected. Restarting launcher; the next frame must pass unmodified validation." >&2
    adb shell am force-stop com.android.launcher3 || true
    sleep 6
  fi
}
