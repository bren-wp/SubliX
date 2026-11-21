#!/usr/bin/env python3
"""Fail closed on documented Android security, edition and dead-code regressions.

This checks repository source invariants. It is NOT a penetration test, dependency
CVE scan, runtime accessibility certificate, or pixel-perfect design approval.
"""
from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
read = lambda path: (ROOT / path).read_text(encoding="utf-8")
main = read("app/src/main/java/com/brendigo/sublix/ui/SubliXApp.kt")
extra = read("app/src/main/java/com/brendigo/sublix/ui/ExtraScreens.kt")
pro = read("app/src/main/java/com/brendigo/sublix/ui/ProSuite.kt")
db = read("app/src/main/java/com/brendigo/sublix/data/SubscriptionsDb.kt")
codec = read("app/src/main/java/com/brendigo/sublix/data/BackupCodec.kt")
reminders = read("app/src/main/java/com/brendigo/sublix/notifications/Reminders.kt")
repo = read("app/src/main/java/com/brendigo/sublix/data/SubscriptionRepository.kt")
gradle = read("app/build.gradle.kts")
manifest_path = ROOT / "app/src/main/AndroidManifest.xml"
manifest = ET.parse(manifest_path).getroot()
android = "{http://schemas.android.com/apk/res/android}"

def require(condition: bool, message: str) -> None:
    if not condition:
        raise AssertionError(message)

permissions = [tag.get(android + "name") for tag in manifest.findall("uses-permission")]
require(permissions == ["android.permission.POST_NOTIFICATIONS"],
        f"Unexpected Android permissions: {permissions}")
app = manifest.find("application")
require(app is not None, "No Android application element")
require(app.get(android + "allowBackup") == "false", "Android backup enabled")
require(app.get(android + "usesCleartextTraffic") == "false", "Cleartext transport allowed")
require(app.get(android + "dataExtractionRules") == "@xml/data_extraction_rules",
        "Modern backup exclusions not configured")
rules = ET.parse(ROOT / "app/src/main/res/xml/data_extraction_rules.xml").getroot()
all_storage_domains = {
    "root", "file", "database", "sharedpref", "external",
    "device_root", "device_file", "device_database", "device_sharedpref"
}
for target in ("cloud-backup", "device-transfer"):
    node = rules.find(target)
    require(node is not None, f"Missing {target} extraction policy")
    excluded = {
        child.get("domain") for child in node.findall("exclude")
        if child.get("path") == "."
    }
    require(all_storage_domains.issubset(excluded),
            f"Sensitive {target} domains not excluded: {sorted(all_storage_domains - excluded)}")
activities = app.findall("activity")
require(len(activities) == 1 and
        activities[0].get(android + "name") == ".MainActivity" and
        activities[0].get(android + "exported") == "true",
        "Unexpected exported activity")
require('"com.brendigo.sublix"' in gradle and '"com.brendigo.sublixpro"' in gradle,
        "Missing separate Free/Pro package identity")
require("android.permission.INTERNET" not in read("app/src/main/AndroidManifest.xml"),
        "App must remain offline")
require("if(isPro) row(\"Advanced tools\"" in main and
        "else row(\"Getting started\"" in main, "Free/Pro navigation contract changed")
require('Available in SubliX Pro' not in extra and
        'Upgrade to Pro' not in extra and 'ProScreen(' not in extra and
        'FreeGuideScreen(' in extra, "Free has an upgrade/purchase prompt")
require("ProSuiteHome" in pro and
        "ProForecastScreen" in pro and
        "ProCategoryBudgetScreen" in pro and
        "ProReportsScreen" in pro, "Pro advanced tools missing")
require("private val proEdition" not in pro and "budgetOverage(" not in
        read("app/src/main/java/com/brendigo/sublix/data/ProAnalytics.kt"),
        "Known dead helper or unreachable paywall branch returned")
require("MAX_BYTES = 2_000_000" in codec and
        "items.length()<=5000" in codec and
        "category.length<=80" in codec and "note.length<=500" in codec,
        "Untrusted backup bounds weakened")
require("db.beginTransaction()" in db and
        "db.setTransactionSuccessful()" in db and
        "item.amountCents in 0L..99_999_999_999L" in db,
        "SQLite transaction or write validation missing")
require("VISIBILITY_PRIVATE" in reminders and
        "setPublicVersion" in reminders and
        "cancelUniqueWork" in reminders,
        "Notification privacy or disabled-background-work gate weakened")
require("if (BuildConfig.DEBUG) Log.e(" in repo,
        "Release database exception logging enabled")
require("override fun onCleared()" in repo and "db.close()" in repo,
        "SQLite lifecycle cleanup missing")
require("if (BuildConfig.DEBUG) PreviewSeed.prepare" in
        read("app/src/main/java/com/brendigo/sublix/MainActivity.kt"),
        "Release preview fixture reachable")
tests = read("app/src/androidTest/java/com/brendigo/sublix/data/SubscriptionImportTransactionTest.kt")
require("oversizedBackupFieldsAreRejectedWithoutTruncation" in tests and
        "directInvalidRecordsAbortTheEntireSQLiteImport" in tests,
        "Security regression tests missing")
nav_tests = read("app/src/androidTest/java/com/brendigo/sublix/NavigationAccessibilityTest.kt")
require("freeEditionHasNoProPurchaseOrUpgradeNavigation" in nav_tests and
        "proForecastIsAccessibleInProEdition" in nav_tests,
        "Edition navigation tests missing")

budget_keys = read("app/src/main/java/com/brendigo/sublix/data/CategoryBudgetKeys.kt")
require("Base64.getUrlEncoder().withoutPadding()" in budget_keys and
        "ambiguousLegacyKeys" in budget_keys,
        "Collision-safe Pro category budget keys or migration guard missing")
require("CategoryBudgetKeys.current(category)" in pro and
        "rememberSaveable(category)" in pro,
        "Pro budgets must use independent preference keys and saveable drafts")
require("var months by rememberSaveable" in pro and
        "var selectedIds by rememberSaveable" in pro,
        "Pro forecast and savings draft must survive Activity recreation")
require("category-limit-" in pro, "Category budget accessibility/test tags missing")

# Detect mistakenly exposed development labels, rather than forbidding
# honest technical discussion in developer-facing comments/documentation.
visible_labels = re.compile(
    r'\\b(?:Text|ScreenHeading|SectionTitle|ProHeading)\\s*\\(\\s*"[^"\\n]*'
    r'(?:debug mode|developer menu|demo mode|preview build|upgrade to pro|coming soon)',
    re.IGNORECASE,
)
for screen_path in (
    "app/src/main/java/com/brendigo/sublix/ui/SubliXApp.kt",
    "app/src/main/java/com/brendigo/sublix/ui/ExtraScreens.kt",
    "app/src/main/java/com/brendigo/sublix/ui/ProSuite.kt",
):
    require(visible_labels.search(read(screen_path)) is None,
            f"Development label visible in {screen_path}")

# Search all production Kotlin and active web JS for accidentally shipped
# debugging calls; excluded debug-only preview fixtures are intentional.
production = list((ROOT/"app/src/main").rglob("*.kt"))
web = [ROOT/"web/app.js"]
for path in production+web:
    data = path.read_text(encoding="utf-8")
    require(not re.search(r"\b(?:printStackTrace|System\.out\.println|console\.log|debugger)\s*(?:\(|;)", data),
            f"Debugging call shipped in {path.relative_to(ROOT)}")
print(f"Source audit passed: {len(production)} Android Kotlin files, offline "
      "manifest/backup rules, Free/Pro separation, import limits, notification "
      "privacy, lifecycle cleanup and instrumentation guards.")
