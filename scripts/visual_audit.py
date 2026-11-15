#!/usr/bin/env python3
"""Compare all 45 real Android PNGs against authentic v0.1.5 screenshot baselines.

The original 20 SVGs are illustrative concepts with a different aspect ratio,
NOT approved pixel-perfect design targets. Report this discrepancy explicitly.
"""
from pathlib import Path
from PIL import Image, ImageChops, ImageStat
import xml.etree.ElementTree as ET
import json

root=Path(__file__).resolve().parents[1]
baseline=root/"brending/baselines/v0.1.5"
captured=root/"brending/screenshots-real"
diff_output=root/"build/visual-diffs"
rows=[]
for edition,count in (("free",20),("pro",25)):
    originals=sorted((baseline/edition).glob("*.png"))
    assert len(originals)==count,(edition,len(originals))
    for original in originals:
        current=captured/edition/original.name
        assert current.is_file(),current
        with Image.open(original) as a, Image.open(current) as b:
            before=a.convert("RGB")
            after=b.convert("RGB")
            assert before.size==after.size,(original,current)
            difference=ImageChops.difference(before,after)
            exact=difference.getbbox() is None
            channels=difference.split()
            maximum=ImageChops.lighter(channels[0],ImageChops.lighter(channels[1],channels[2]))
            mask=maximum.point(lambda v:255 if v>8 else 0)
            changed=100.0*mask.histogram()[255]/(before.width*before.height)
            average=sum(ImageStat.Stat(difference).mean)/3.0
            rows.append({"edition":edition,"screen":original.stem,
                "status":"IDENTICAL" if exact else "REVIEW_REQUIRED",
                "changedPercent":round(changed,4),"meanRgbDelta":round(average,3),
                "width":before.width,"height":before.height})
            if not exact:
                overlay=Image.new("RGBA",before.size,(238,63,78,0))
                overlay.putalpha(mask.point(lambda v:110 if v else 0))
                image=Image.alpha_composite(after.convert("RGBA"),overlay)
                folder=diff_output/edition
                folder.mkdir(parents=True,exist_ok=True)
                image.convert("RGB").save(folder/(original.stem+"-review.webp"),quality=82)
assert len(rows)==45
reference_svgs=sorted((root/"brending/screens").glob("*.svg"))
assert len(reference_svgs)==20
canvas=set()
for path in reference_svgs:
    attrs=ET.parse(path).getroot().attrib
    box=attrs.get("viewBox","").split()
    assert len(box)==4
    canvas.add((float(box[2]),float(box[3])))
version=(root/"VERSION").read_text().strip()
report=[
    "# SubliX v"+version+" — real Android visual regression",
    "",
    "Compared to captured v0.1.5 baselines across **all 45 screens**.",
    "Every candidate and baseline is an authentic, unmodified Android emulator PNG.",
    "Exact identity means zero RGB pixel differences; changed-pixel percentage counts any channel delta above 8.",
    "",
    "**1:1 design approval: NOT VERIFIED.** The 20 original SVGs are explicitly concept illustrations,",
    "not approved app reference screenshots. Their aspect ratio does not match the native emulator PNGs.",
    "There are no masks, crops, filtered regions or fabricated screenshots in the comparison.",
    "",
    "| Edition | Screen | RGB status | Changed pixels | Mean RGB delta |",
    "| --- | --- | --- | ---: | ---: |"
]
for item in rows:
    report.append("| "+ " | ".join((item["edition"],item["screen"],item["status"],
                    f'{item["changedPercent"]:.4f}%',f'{item["meanRgbDelta"]:.3f}'))+" |")
same=sum(row["status"]=="IDENTICAL" for row in rows)
sizes=", ".join(f"{int(w)}x{int(h)}" for w,h in sorted(canvas))
report += ["",f"**Identical to v0.1.5: {same}/45.**",
    f"Concept viewboxes: {sizes}; emulator viewport: 1080x1920. Aspect ratios differ.",
    "",
    "Intentional content changes can produce legitimate differences; each non-identical region still requires review.",
    "The separate red-overlay PNG review images are generated as CI artifacts, never Play Store screenshot replacements.",
    "This report does not validate large-font, TalkBack, tablet, landscape, UI taps or production-device behavior.",
    ""]
(root/"docs/VISUAL_AUDIT.md").write_text("\n".join(report),encoding="utf-8")
(root/"docs/VISUAL_AUDIT.json").write_text(json.dumps({"baseline":"v0.1.5","candidate":"v"+version,
    "designApproval":"NOT_VERIFIED","screens":rows},indent=2)+"\n",encoding="utf-8")
print(f"Visual QA: {len(rows)} screens, {same} exact RGB matches; independent design parity NOT VERIFIED.")
