#!/usr/bin/env bash
# Test the exact launcher-ANR signature from GitHub's rejected screenshots.
set -euo pipefail
source scripts/emulator_recovery.sh
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
export LOG="$tmp/adb.log"
adb() { printf '%s\n' "$*" >> "$LOG"; }
sleep() { :; }

printf '%s\n' 'mCurrentFocus=Window{7f43b7f u0 Application Not Responding: com.android.launcher3}' > "$tmp/launcher.txt"
recover_emulator_launcher_anr "$tmp/launcher.txt"
grep -Fxq 'shell am force-stop com.android.launcher3' "$LOG"

# Never dismiss SubliX itself; a real application ANR must fail screenshot QA.
: > "$LOG"
printf '%s\n' 'mCurrentFocus=Window{7f43b7f u0 Application Not Responding: com.brendigo.sublix}' > "$tmp/app.txt"
recover_emulator_launcher_anr "$tmp/app.txt"
test ! -s "$LOG"

# Do not respond to unrelated launcher text when no ANR dialog has focus.
printf '%s\n' 'mFocusedApp=ActivityRecord{com.android.launcher3}' > "$tmp/normal.txt"
recover_emulator_launcher_anr "$tmp/normal.txt"
test ! -s "$LOG"
echo 'Launcher-only ANR recovery regression tests passed'
