#!/usr/bin/env python3
"""Fail closed when the standalone 0 EUR Free edition is monetized or promoted.

Source and store-listing contract only. Does not change Play Console settings;
the operator must verify Pricing, Ads and Data safety declarations in Console.
"""
from pathlib import Path
import json
import re

root = Path(__file__).resolve().parents[1]
read = lambda path: (root / path).read_text(encoding="utf-8")
declaration = json.loads(read("play-store/free/free-policy.json"))

assert declaration["packageName"] == "com.brendigo.sublix"
assert type(declaration["priceEur"]) in (int, float) and declaration["priceEur"] == 0
for field in ("containsAds", "inAppProducts", "paidSubscriptions", "externalUpgradePrompts", "onlineAccountRequired"):
    assert declaration[field] is False, f"Free monetization setting changed: {field}"
assert declaration["playConsoleStatus"] == "REQUIRES_MANUAL_VERIFICATION"

gradle = read("app/build.gradle.kts")
manifest = read("app/src/main/AndroidManifest.xml")
root_gradle = read("build.gradle.kts")
app = read("app/src/main/java/com/brendigo/sublix/ui/SubliXApp.kt")
extra = read("app/src/main/java/com/brendigo/sublix/ui/ExtraScreens.kt")
assert 'applicationId = "com.brendigo.sublix"' in gradle
assert 'targetSdk = 36' in gradle, "Google Play target API has changed"
assert 'android.permission.INTERNET' not in manifest
assert 'android.permission.POST_NOTIFICATIONS' in manifest
assert 'com.android.application' in root_gradle
assert 'if(isPro) row("Advanced tools"' in app
assert 'else row("Getting started"' in app
assert 'Screen.PRO -> if(isPro)' in app
assert 'FreeGuideScreen' in extra
assert 'Available in SubliX Pro' not in extra

# Billing, advertising and tracking integrations are not allowed in this
# explicitly free-of-charge, ad-free application. Check all build manifests,
# including test, product-flavor and buildSrc variants.
for path in sorted((root / "app").rglob("*.gradle*")) + [root / "build.gradle.kts"]:
    source = path.read_text(encoding="utf-8")
    assert not re.search(
        r'com\.android\.billingclient|play-services-ads|google-mobile-ads|'
        r'firebase-ads|facebook\.android:audience-network-sdk|'
        r'com\.applovin|com\.unity3d\.ads|ironsource|'
        r'com\.revenuecat|admob|purchases-android',
        source, re.I), f"Ad/billing SDK in {path}"
for path in sorted((root / "app/src").rglob("AndroidManifest.xml")):
    source = path.read_text(encoding="utf-8")
    assert not re.search(r'android\.permission\.(?:INTERNET|AD_ID|ACCESS_ADSERVICES_\w+)',source), path
    assert 'com.android.vending.BILLING' not in source, path
    assert 'com.google.android.gms.permission.AD_ID' not in source, path

# Free Play listing must not name the Pro edition, compare tiers, promise an
# unlock or suggest that core features require a purchase.
for locale in ("en-US","hr-HR"):
    directory = root / "play-store" / "free" / "listing" / locale
    for field in ("title", "short-description", "full-description", "whats-new"):
        text = (directory / f"{field}.txt").read_text(encoding="utf-8")
        assert not re.search(
            r'\bpro\b|premium|upgrade|unlock|paywall|paid feature|'
            r'\bkupite\b|\bkupi\b|nadograd|otključ|ograničen[aeio]?|'
            r'only (?:with|in) (?:the )?pro|available in pro',
            text, re.I), f"Forbidden Free upsell or limitation: {locale}/{field}"
    description = (directory / "full-description.txt").read_text(encoding="utf-8")
    if locale == "en-US":
        assert "EUR 0" in description and "no ads" in description.lower() and "no in-app purchases" in description.lower()
    else:
        assert "0 EUR" in description and "bez oglasa" in description.lower() and "nema reklama" in description.lower()

# Do not permit a user-visible Free help screen to grow a tier comparison.
guide = extra.split("@Composable internal fun FreeGuideScreen(",1)[1].split(
    "@Composable internal fun PrivacyScreen(",1)[0]
assert not re.search(r'\b(?:Pro|upgrade|premium|unlock|buy|purchase|kupi|nadograd)\b',guide,re.I),     "Free guide contains a paid upgrade or comparison"

tests = read("app/src/androidTest/java/com/brendigo/sublix/NavigationAccessibilityTest.kt")
assert "freeEditionNeverRendersPaidOrUpgradeMessaging" in tests
assert "freeEditionHasNoProPurchaseOrUpgradeNavigation" in tests
print("Free Play policy: 0 EUR, ad-free, no purchases, no Pro upsell, no Internet, safe manifest and store copy: PASS")
