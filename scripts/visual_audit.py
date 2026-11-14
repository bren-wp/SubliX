#!/usr/bin/env python3
"""Compare true emulator PNGs against the previous version's emulator captures.
A zero-diff regression is not proof of matching the independent design concept.
"""
from pathlib import Path
from PIL import Image, ImageChops, ImageStat
import json

root=Path(__file__).resolve().parents[1]
baseline=root/"brending/baselines/v0.1.2"
captured=root/"brending/screenshots-real"
rows=[]
for edition in ("free","pro"):
    for reference in sorted((baseline/edition).glob("*.png")):
        current=captured/edition/reference.name
        if not current.exists():
            rows.append({"edition":edition,"screen":reference.stem,"status":"MISSING","changed":None})
            continue
        with Image.open(reference) as old, Image.open(current) as now:
            before=old.convert("RGB"); after=now.convert("RGB")
            if before.size!=after.size:
                rows.append({"edition":edition,"screen":reference.stem,"status":"DIFFERENT_SIZE","changed":None})
                continue
            diff=ImageChops.difference(before,after)
            exact=diff.getbbox() is None
            # The threshold is only for a readable change metric. Exact match still requires
            # byte-identical rendered RGB pixels after decoding.
            gray=diff.convert("L").point(lambda value:255 if value>8 else 0)
            hist=gray.histogram()
            changed=100.0*hist[255]/(after.width*after.height)
            mean=sum(ImageStat.Stat(diff).mean)/3.0
            rows.append({"edition":edition,"screen":reference.stem,
                         "status":"IDENTICAL" if exact else "DIFFERENT",
                         "changed":round(changed,4),"meanRgbDelta":round(mean,3),
                         "size":list(after.size)})
assert len(rows)==40, f"Expected 40 previous capture baselines, found {len(rows)}"
out=root/"docs/VISUAL_AUDIT.md"
lines=[
    "# SubliX emulator visual regression audit",
    "",
    "These are **measured actual emulator screenshots**, not generated mockups.",
    "Comparison: candidate v0.1.2 emulator captures versus newly rendered emulator captures.",
    "A 1:1 pass requires independently approved design baselines, supported device sizes, and **zero unintended pixel deviations**; a build passing CI is not proof of 1:1.",
    "Expected differences from new features, emulator status bars, timestamps and layout changes need human review. This report does not auto-approve them.",
    "",
    "| Edition | Screen | Pixel identity vs prior capture | Changed pixels (>8 luminance) | Mean RGB delta |",
    "| --- | --- | --- | ---: | ---: |"
]
for r in rows:
    lines.append("| "+ " | ".join([r["edition"],r["screen"],r["status"],
        f'{r["changed"]:.4f}%' if r["changed"] is not None else "—",
        str(r.get("meanRgbDelta","—"))])+" |")
exact=sum(r["status"]=="IDENTICAL" for r in rows)
lines+=["",f"**Exact matches to prior release: {exact}/40.**","",
    "The five additional Pro screens (Forecast, Category Budgets, What-if Planner, Reports, Price History) are new in v0.1.3 and have no prior approved screenshot baseline.",
    "The 20 original SVG design concepts are illustrative rather than validated design-system specifications. Their content and aspect ratios differ from emulator screenshots. Achieving literal 1:1 with those requires a design review and rebaselining process.",
    ""]
out.write_text("\n".join(lines),encoding="utf-8")
(root/"docs/VISUAL_AUDIT.json").write_text(json.dumps(rows,indent=2),encoding="utf-8")
print(f"Audited {len(rows)} screenshots; {exact} had zero RGB delta.")
