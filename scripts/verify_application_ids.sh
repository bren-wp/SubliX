# SubliX — Android package identity regression guard
# Used by both CI and release jobs AFTER building the APKs.
set -euo pipefail
AAPT="${ANDROID_HOME}/build-tools/37.0.0/aapt"
if [ ! -x "$AAPT" ]; then echo "Android aapt missing at $AAPT" >&2; exit 1; fi
verify() {
    local apk="$1" expected="$2" actual
    test -s "$apk"
    actual=$("$AAPT" dump badging "$apk" | sed -n "s/^package: name='\([^']*\)'.*/\1/p" | head -n1)
    if [ "$actual" != "$expected" ]; then
        echo "::error::Unexpected package ID in $apk; expected $expected, actual $actual" >&2
        exit 1
    fi
    echo "Verified $apk => $actual"
}
verify "$1" "com.brendigo.sublix"
verify "$2" "com.brendigo.sublixpro"

# Inspect permissions actually merged into the Free APK, including transitive
# dependencies. Source AndroidManifest.xml alone is not a reliable guarantee.
permissions=$("$AAPT" dump permissions "$1")
if printf '%s\n' "$permissions" | grep -E "uses-permission: name='(android\.permission\.INTERNET|android\.permission\.AD_ID|com\.google\.android\.gms\.permission\.AD_ID|com\.android\.vending\.BILLING|android\.permission\.ACCESS_ADSERVICES_[A-Z_]+)'"; then
    echo "::error::Free APK contains a forbidden network, ads or billing permission" >&2
    exit 1
fi
echo "Verified Free merged APK has no Internet, advertising ID or Billing permission"
