#!/usr/bin/env python3
"""Check the actual resolved Free runtime graph for monetization SDKs.

Input is the stdout from:
gradle :app:dependencies --configuration freeReleaseRuntimeClasspath
Static Gradle declarations alone cannot detect transitive dependencies.
"""
from pathlib import Path
import re
import sys

if len(sys.argv) != 2:
    raise SystemExit("Usage: check_free_dependency_graph.py <gradle-dependencies.txt>")
path = Path(sys.argv[1])
text = path.read_text(encoding="utf-8")
if "freeReleaseRuntimeClasspath" not in text or len(text) < 500:
    raise SystemExit("No resolved Free release dependency graph")
if "FAILED" in text or "Could not resolve" in text or "No matching variant" in text:
    raise SystemExit("Dependency graph contains unresolved artifacts")

forbidden = {
    "Google Play Billing": r"com\.android\.billingclient",
    "Google Mobile Ads": r"com\.google\.android\.gms:play-services-ads",
    "AdMob mediation": r"com\.google\.ads\.",
    "Firebase Ads": r"com\.google\.firebase:firebase-ads",
    "Facebook Audience Network": r"com\.facebook\.android:audience-network-sdk",
    "AppLovin": r"com\.applovin",
    "Unity Ads": r"com\.unity3d\.ads",
    "ironSource": r"com\.ironsource|com\.unity3d\.mediation",
    "RevenueCat": r"com\.revenuecat",
}
for label,pattern in forbidden.items():
    if re.search(pattern,text,re.IGNORECASE):
        raise SystemExit(f"Forbidden Free runtime SDK detected: {label}")
print("Resolved Free release runtime graph: no advertising or billing SDK found.")
