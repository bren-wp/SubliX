#!/usr/bin/env python3
"""Reproducible Google Play listing art from original SubliX vectors and REAL emulator PNGs.

Outputs are marketing media only. Source captures are never retouched or overlaid.
A future Play submission still requires human policy, accessibility, security, and
store signature review, plus a publicly accessible privacy policy.
"""
from __future__ import annotations
import argparse
import hashlib
import json
import math
from pathlib import Path
import shutil
import struct
import sys

from PIL import Image, ImageDraw, ImageFont, ImageFilter
import cairosvg

ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "play-store"
COLORS = {
    "free": {"main": (169,154,255), "bright": (123,95,249), "label": "SubliX"},
    "pro": {"main": (232,186,125), "bright": (191,136,71), "label": "SubliX Pro"},
}
SHOTS = {
    "free": [
        ("01-home.png","Home dashboard"), ("02-subscriptions.png","Subscription list"),
        ("03-add-subscription.png","Add subscription"), ("05-details.png","Subscription details"),
        ("06-calendar.png","Renewal calendar"), ("08-insights.png","Spending insights"),
        ("09-reminders.png","Reminder settings"), ("14-backup.png","Local backup")
    ],
    "pro": [
        ("01-home.png","Pro home"), ("02-subscriptions.png","Subscription controls"),
        ("08-insights.png","Spending analytics"),
        ("21-pro-forecast.png","Multi-month forecast"), ("22-pro-category-budgets.png","Category budgets"),
        ("23-pro-planner.png","What-if savings planner"), ("24-pro-reports.png","CSV report export"),
        ("25-pro-price-history.png","Price change history")
    ]
}

def get_font(size: int, bold: bool=False) -> ImageFont.FreeTypeFont:
    paths = [
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
        "/usr/share/fonts/truetype/liberation2/LiberationSans-Bold.ttf" if bold else "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf",
    ]
    for path in paths:
        if Path(path).exists(): return ImageFont.truetype(path,size)
    raise RuntimeError("Standard installed font not found")

def icon(edition: str) -> None:
    folder=OUTPUT/edition
    folder.mkdir(parents=True,exist_ok=True)
    svg=(ROOT/"brending"/f"icon-{edition}.svg").read_bytes()
    png=cairosvg.svg2png(bytestring=svg,output_width=512,output_height=512)
    from io import BytesIO
    result=Image.open(BytesIO(png)).convert("RGBA")
    result.save(folder/"app_icon_512.png",format="PNG",optimize=True)
    assert (folder/"app_icon_512.png").stat().st_size<=1024*1024

def feature(edition:str) -> None:
    c=COLORS[edition]
    width,height=1024,500
    rgb=Image.new("RGB",(width,height))
    px=rgb.load()
    for y in range(height):
        for x in range(width):
            dx=x/width
            dy=y/height
            radial=math.exp(-((dx-.77)**2/.14+(dy-.47)**2/.29))
            vertical=y/height
            base=(16,18,29)
            px[x,y]=tuple(min(255,int(base[i]+(20 if i==2 else 11)*vertical+
                c["bright"][i]*(0.18 if i==0 else 0.15)*radial)) for i in range(3))
    layer=Image.new("RGBA",(width,height),(0,0,0,0))
    dr=ImageDraw.Draw(layer)
    shade=c["main"]
    # Abstract renewal orbits. They are deliberately not fake UI/screenshots.
    for idx,r in enumerate((108,160,215)):
        xy=(760-r,246-r,760+r,246+r)
        dr.arc(xy,start=215+idx*24,end=530-idx*11,
               fill=(*shade,235-idx*50),width=8 if idx==0 else 5)
    dr.ellipse((699,186,823,310),fill=(*shade,27),outline=(*shade,180),width=3)
    dr.arc((716,202,809,295),35,320,fill=(243,241,255,220),width=8)
    dr.ellipse((899,135,915,151),fill=(249,233,206,255))
    dr.ellipse((624,362,643,381),fill=(*shade,245))
    dr.ellipse((867,358,877,368),fill=(251,246,255,230))
    blur=layer.filter(ImageFilter.GaussianBlur(28))
    blur.putalpha(blur.getchannel("A").point(lambda a:int(a*.35)))
    rgb=Image.alpha_composite(rgb.convert("RGBA"),blur)
    rgb=Image.alpha_composite(rgb,layer)
    d=ImageDraw.Draw(rgb)
    d.text((85,92),c["label"],font=get_font(69,True),fill=(246,244,255))
    d.text((89,182),"BY BRENDIGO",font=get_font(21,True),fill=(*c["main"],))
    d.rounded_rectangle((88,230,141,236),radius=3,fill=(*c["main"],))
    d.text((85,274),"Every subscription.",font=get_font(30,True),fill=(239,236,254))
    d.text((85,315),"Under control.",font=get_font(30,True),fill=(239,236,254))
    d.text((87,419),"PRIVATE  /  OFFLINE-FIRST  /  ANDROID",font=get_font(15,False),fill=(183,180,203))
    rgb.convert("RGB").save(OUTPUT/edition/"feature_graphic_1024x500.png",optimize=True)

def copy_real_screenshots(edition:str) -> None:
    folder=OUTPUT/edition/"phone-screenshots"
    folder.mkdir(parents=True,exist_ok=True)
    for i,(name,label) in enumerate(SHOTS[edition],start=1):
        original=ROOT/"brending"/"screenshots-real"/edition/name
        if not original.is_file(): raise FileNotFoundError(f"No genuine Android screenshot: {original}")
        with Image.open(original) as im:
            if im.size!=(1080,1920):
                raise ValueError(f"{original} is {im.size}; expected 1080x1920 native emulator output")
            if im.format!="PNG": raise ValueError(f"{original} is not an original PNG")
            # One color-model conversion only: Play requires RGB PNG (no alpha).
            im.convert("RGB").save(folder/f"{i:02d}-{name}",format="PNG",optimize=True)
        target=folder/f"{i:02d}-{name}"
        with Image.open(target) as im:
            assert im.mode=="RGB" and im.size==(1080,1920)
        if target.stat().st_size>8*1024*1024:
            raise ValueError(f"Screenshot too large for Play: {target}")

def write_manifest() -> None:
    output=[]
    for edition in ("free","pro"):
        folder=OUTPUT/edition
        for file in sorted(folder.rglob("*.png")):
            with Image.open(file) as pic: width,height=pic.size;mode=pic.mode
            output.append({
                "edition":edition,"file":file.relative_to(OUTPUT).as_posix(),
                "width":width,"height":height,"colorMode":mode,
                "bytes":file.stat().st_size,"sha256":hashlib.sha256(file.read_bytes()).hexdigest(),
                "provenance":"original BRENDIGO vector" if "screenshots" not in file.parts else "actual Android emulator PNG, RGB conversion only"
            })
    (OUTPUT/"asset-manifest.json").write_text(json.dumps(output,indent=2,ensure_ascii=False)+"\n",encoding="utf-8")
    print(f"Verified {len(output)} Play assets: 2 icons, 2 feature graphics and 16 real Android captures")

def validate_only() -> None:
    manifest=json.loads((OUTPUT/"asset-manifest.json").read_text(encoding="utf-8"))
    assert len(manifest)==20,f"Unexpected asset count: {len(manifest)}"
    for entry in manifest:
        p=OUTPUT/entry["file"]
        assert p.is_file() and hashlib.sha256(p.read_bytes()).hexdigest()==entry["sha256"],p
        with Image.open(p) as im:
            assert tuple(im.size)==(entry["width"],entry["height"]),p
            if "app_icon" in p.name:
                assert im.mode=="RGBA" and im.size==(512,512) and p.stat().st_size<=1024*1024
            elif "feature_graphic" in p.name:
                assert im.mode=="RGB" and im.size==(1024,500)
            else:
                assert im.mode=="RGB" and im.size==(1080,1920)
    print("Google Play asset file metadata checks passed")

def main():
    ap=argparse.ArgumentParser()
    ap.add_argument("--check",action="store_true")
    args=ap.parse_args()
    if args.check:
        validate_only();return
    for edition in ("free","pro"):
        icon(edition);feature(edition);copy_real_screenshots(edition)
    write_manifest()
    validate_only()

if __name__=="__main__":
    main()
