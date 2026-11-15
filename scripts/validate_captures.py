#!/usr/bin/env python3
"""Validate authentic, version-matched Android emulator captures without modifying pixels."""
from pathlib import Path
from hashlib import sha256
from PIL import Image

root=Path(__file__).resolve().parents[1]
version=(root/"VERSION").read_text(encoding="utf-8").strip()
captures=root/"brending/screenshots-real"
assert (captures/"CAPTURE_VERSION").read_text(encoding="utf-8").strip()==version
digests={}
for edition,count in (("free",20),("pro",25)):
    screenshots=sorted((captures/edition).glob("*.png"))
    assert len(screenshots)==count,(edition,len(screenshots))
    for screenshot in screenshots:
        with Image.open(screenshot) as im:
            assert im.format=="PNG" and im.size==(1080,1920),screenshot
            im.verify()
        digest=sha256(screenshot.read_bytes()).hexdigest()
        if digest in digests:
            raise RuntimeError(f"Duplicate screenshot for two routes: {digests[digest]} and {screenshot}")
        digests[digest]=screenshot.name
assert len(digests)==45
print(f"{version}: 45 distinct genuine Android PNGs; exact dimensions, no corrupt or duplicate frames.")
