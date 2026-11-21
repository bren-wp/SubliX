#!/usr/bin/env bash
# Verify the APKs built by this CI job, regardless of Gradle's unsigned suffix.
# A release without signing credentials is named *-unsigned.apk; store releases
# use the regular signed APK naming convention. Never skip a missing edition.
set -euo pipefail

find_release_apk() {
    local edition="$1" directory="app/build/outputs/apk/$1/release"
    local -a packages=()
    if [[ ! -d "$directory" ]]; then
        echo "::error::Release APK directory missing: $directory" >&2
        return 1
    fi
    mapfile -t packages < <(find "$directory" -maxdepth 1 -type f -name '*.apk' | sort)
    if [[ "${#packages[@]}" -ne 1 ]]; then
        echo "::error::Expected exactly one $edition release APK, found ${#packages[@]} in $directory" >&2
        printf 'Discovered: %s\n' "${packages[@]}" >&2
        return 1
    fi
    [[ -s "${packages[0]}" ]] || {
        echo "::error::Empty release APK: ${packages[0]}" >&2
        return 1
    }
    printf '%s\n' "${packages[0]}"
}

free_apk="$(find_release_apk free)"
pro_apk="$(find_release_apk pro)"
echo "Checking built release APKs: $free_apk and $pro_apk"
bash scripts/verify_application_ids.sh "$free_apk" "$pro_apk"
