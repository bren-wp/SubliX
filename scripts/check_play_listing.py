#!/usr/bin/env python3
"""Google Play listing limits and screenshot authenticity checks."""
from __future__ import annotations
from pathlib import Path
import argparse
import re

ROOT=Path(__file__).resolve().parents[1]
PLAY=ROOT/"play-store"
SLOTS={"free":8,"pro":8}
LOCALES=["en-US","hr-HR"]

parser=argparse.ArgumentParser()
parser.add_argument("--metadata-only",action="store_true",help="Validate store listing text while emulator images are being captured")
args=parser.parse_args()
if not args.metadata_only:
    from PIL import Image, ImageChops

for edition,count in SLOTS.items():
    folder=PLAY/edition
    if not args.metadata_only:
        assert (folder/"app_icon_512.png").is_file(),edition
        assert (folder/"feature_graphic_1024x500.png").is_file(),edition
        files=sorted((folder/"phone-screenshots").glob("*.png"))
        assert len(files)==count, f"{edition}: {len(files)} phone captures, expected {count}"
        for file in files:
            with Image.open(file) as screenshot:
                assert screenshot.mode=="RGB" and screenshot.size==(1080,1920),f"Invalid Play screenshot: {file}"
                rawname=re.sub(r"^\d{2}-","",file.name)
                with Image.open(ROOT/"brending/screenshots-real"/edition/rawname) as native:
                    # Content must be the real Android screenshot. No retouch, overlays, AI, cropping.
                    diff=ImageChops.difference(screenshot,native.convert("RGB"))
                    assert diff.getbbox() is None, f"Play screenshot differs from original app capture: {file}"
    for locale in LOCALES:
        folder=PLAY/edition/"listing"/locale
        metadata={}
        limits={"title":30,"short-description":80,"full-description":4000,"whats-new":500}
        for field,limit in limits.items():
            p=folder/(field+".txt")
            assert p.is_file(),f"Missing {p}"
            value=p.read_text(encoding="utf-8").strip()
            assert 0<len(value)<=limit, f"{p}: {len(value)} chars, limit {limit}"
            metadata[field]=value
        assert "BRENDIGO" in metadata["title"],edition
        if edition=="pro": assert "Pro" in metadata["title"]
        else: assert "Pro" not in metadata["title"]
        assert not re.search(r"\bfree\b|\bdiscount\b|\b#1\b",metadata["title"],re.I),f"Promo in Play title: {folder}"
        assert "Google Play" not in metadata["short-description"],locale
        if locale=="hr-HR":
            assert "engleskom" in metadata["full-description"].lower(),f"Croatian listing must disclose English-only UI"
        assert "No account" in metadata["full-description"] or locale=="hr-HR",f"Offline privacy missing: {folder}"

version=(ROOT/"VERSION").read_text(encoding="utf-8").strip()
if not args.metadata_only:
    assert (ROOT/"brending/screenshots-real/CAPTURE_VERSION").read_text(encoding="utf-8").strip()==version,"Screenshots are from an older app build"
assert version=="0.1.5",version
manifest=(ROOT/"app/src/main/AndroidManifest.xml").read_text(encoding="utf-8")
gradle=(ROOT/"app/build.gradle.kts").read_text(encoding="utf-8")
assert "targetSdk = 36" in gradle
assert "compileSdk = 36" in gradle
assert "minSdk = 26" in gradle
assert "android.permission.INTERNET" not in manifest
assert "android.permission.POST_NOTIFICATIONS" in manifest
print("Google Play art, actual screenshot RGB identity, 2 editions × 2 locale listings, metadata limits and SDK policy checks passed.")
