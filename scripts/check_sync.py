#!/usr/bin/env python3
"""Contract test: Android Free/Pro, web and README must represent the same product.

This catches stale marketing content and missing actual media. It is not a pixel-perfect
certification; actual emulator comparison is performed separately in visual_audit.py.
"""
import pathlib
import hashlib
import re
import struct
from html.parser import HTMLParser

ROOT = pathlib.Path(__file__).resolve().parents[1]
read = lambda relative: (ROOT / relative).read_text(encoding="utf-8")

class HtmlAssets(HTMLParser):
    def __init__(self):
        super().__init__()
        self.sources=[]
        self.links=[]
        self.forms=0
    def handle_starttag(self,tag,attrs):
        a=dict(attrs)
        if tag in ("img","script") and a.get("src"):self.sources.append(a["src"])
        if tag=="link" and a.get("href"):self.sources.append(a["href"])
        if tag=="a" and a.get("href"):self.links.append(a["href"])
        if tag=="form":self.forms+=1

pages=["web/index.html","web/privacy.html","web/terms.html"]
for name in pages:
    p=HtmlAssets();p.feed(read(name))
    assert p.forms == 0, f"Unexpected login/data collection form in {name}"
    for asset in p.sources:
        if not asset.startswith(("https://","http://","data:")):
            f=ROOT/"web"/asset
            assert f.is_file(),f"Broken local asset {name}: {asset}"
    assert "BRENDIGO" in read(name),name
    assert "href=\"privacy.html\"" in read(name) or name.endswith("privacy.html"),name

index=read("web/index.html")
privacy=read("web/privacy.html")
terms=read("web/terms.html")
js=read("web/app.js")
css=read("web/style.css")
theme=read("app/src/main/java/com/brendigo/sublix/ui/Theme.kt")
main=read("app/src/main/java/com/brendigo/sublix/ui/SubliXApp.kt")
pro=read("app/src/main/java/com/brendigo/sublix/ui/ProSuite.kt")
build=read("app/build.gradle.kts")
manifest=read("app/src/main/AndroidManifest.xml")
readme=read("README.md")

assert not (ROOT/".github/workflows/pages.yml").exists(),"GitHub Pages must remain disabled"
assert "fonts.googleapis.com" not in css, "No external web fonts required"
assert "No account" in index or "no account" in index.lower()
assert "un-encrypted" not in privacy.lower()
assert "currently unencrypted" in privacy.lower(), "Export privacy text must reflect app behavior"
assert "does not initiate payments" in terms.lower()
assert 'applicationIdSuffix = ".free"' in build and 'applicationIdSuffix = ".pro"' in build
assert 'android.permission.INTERNET' not in manifest,"Offline apps must not request Internet"
assert 'android:allowBackup="false"' in manifest
assert 'BuildConfig.FLAVOR' in pro and 'Screen.PRO_FORECAST' in main
assert 'ProCategoryBudgetScreen' in main
assert 'ProPriceHistoryScreen' in main
assert 'Open provider management page' in main and 'nextDueAfter' in read("app/src/main/java/com/brendigo/sublix/data/SubscriptionsDb.kt")

# Keep shared brand colors identical in Android and web.
colors=[
    ("--bg","#10121D","Background"),
    ("--surface","#1D2030","Surface"),
    ("--raised","#24263C","Raised"),
    ("--primary","#7866F5","Purple"),
    ("--accent","#A99AFF","Accent"),
    ("--gold","#E8BA7D","Gold"),
    ("--muted","#A7AAC0","Muted")
]
for variable,hex_value,kotlin in colors:
    assert re.search(rf"{re.escape(variable)}:\s*{hex_value}",css,re.I),f"CSS color drift: {variable}"
    rgb=hex_value.removeprefix("#").upper()
    assert re.search(rf"\b{kotlin}\s*=\s*Color\(0xFF{rgb}\)",theme,re.I),f"Android color drift: {kotlin}"

# Web and README contain actual bytes from emulator captures, not SVG mockups.
captures=0
for edition in ("free","pro"):
    android=list(sorted((ROOT/"brending/screenshots-real"/edition).glob("*.png")))
    webshots=list(sorted((ROOT/"web/assets/screenshots"/edition).glob("*.png")))
    assert len(android)==20 and len(webshots)==20, f"{edition}: expected 20 genuine captures in each location"
    for app_photo in android:
        web_photo=ROOT/"web/assets/screenshots"/edition/app_photo.name
        assert web_photo.is_file(), f"Missing web photo: {web_photo}"
        b=web_photo.read_bytes()
        assert b[:8]==b"\x89PNG\r\n\x1a\n",f"Not a real PNG: {web_photo}"
        width,height=struct.unpack(">II",b[16:24])
        assert width>=720 and height>=1280,f"Unexpected screenshot resolution: {web_photo}"
        assert hashlib.sha256(b).digest()==hashlib.sha256(app_photo.read_bytes()).digest(),f"Different screenshot copies: {web_photo}"
        assert str(pathlib.Path("brending/screenshots-real")/edition/app_photo.name) in readme
        assert app_photo.name in js,f"Screenshot absent from interactive web gallery: {app_photo.name}"
        captures+=1

required=[
    ("Forecast","ProForecastScreen","forecasts"),
    ("Category budgets","ProCategoryBudgetScreen","category-specific spending budgets"),
    ("Savings planner","ProWhatIfScreen","what-if savings"),
    ("CSV reports","ProReportsScreen","local csv reports"),
    ("Price history","ProPriceHistoryScreen","price change history"),
]
for label,kotlin,webphrase in required:
    assert kotlin in pro, f"Missing Android Pro feature: {label}"
    assert webphrase in index.lower(),f"Web out of sync with Android: {label}"

assert "0.1.4" in read("VERSION")
assert 'versionName = "0.1.4"' in build
assert "SubliX Free" in index and "SubliX Pro" in index
print(f"Product contract passed: two Android editions, 3 web pages, {captures} real matched PNGs, 7 shared colors and 5 Pro capabilities.")
