#!/usr/bin/env bash
# Offline regression test for Gradle's signed and unsigned release APK naming.
# The real APK identity/permission checks still run in Android CI.
set -euo pipefail
root="$(mktemp -d)"
trap 'rm -rf "$root"' EXIT

mkdir -p "$root/scripts" "$root/app/build/outputs/apk/free/release" "$root/app/build/outputs/apk/pro/release"
cp scripts/verify_built_release_apks.sh "$root/scripts/verify_built_release_apks.sh"
cat > "$root/scripts/verify_application_ids.sh" <<'VERIFY'
#!/usr/bin/env bash
set -euo pipefail
[[ -s "$1" && -s "$2" ]]
printf 'Identities checked in fixture: %s and %s\n' "$1" "$2"
VERIFY

run() {( cd "$root" && bash scripts/verify_built_release_apks.sh );}

# Unsigned Free + signed Pro are valid CI output naming conventions.
printf 'fixture\n' > "$root/app/build/outputs/apk/free/release/app-free-release-unsigned.apk"
printf 'fixture\n' > "$root/app/build/outputs/apk/pro/release/app-pro-release.apk"
run

# Never pass by checking only Free while Pro is absent.
rm "$root/app/build/outputs/apk/pro/release/app-pro-release.apk"
if run; then echo "Missing Pro APK did not fail" >&2; exit 1; fi
printf 'fixture\n' > "$root/app/build/outputs/apk/pro/release/app-pro-release-unsigned.apk"

# Never silently pick a random APK if two candidates are present.
printf 'fixture\n' > "$root/app/build/outputs/apk/free/release/app-free-release.apk"
if run; then echo "Ambiguous Free APK list did not fail" >&2; exit 1; fi
rm "$root/app/build/outputs/apk/free/release/app-free-release-unsigned.apk"
run
echo "Release APK discovery regression tests passed"
