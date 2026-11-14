#!/usr/bin/env python3
"""Inspect both AAB outputs for native .so libraries and 16 KB page-size exposure.
Pure Kotlin/Compose projects normally have none, but transitive SDKs may add them.
If any appear, stop the release and require an ELF alignment and real-device audit.
"""
import pathlib,zipfile
root=pathlib.Path(__file__).resolve().parents[1]
bundles=[
 root/"app/build/outputs/bundle/freeRelease/app-free-release.aab",
 root/"app/build/outputs/bundle/proRelease/app-pro-release.aab"
]
for bundle in bundles:
    if not bundle.is_file():
        raise SystemExit(f"Missing built Android bundle: {bundle}")
    with zipfile.ZipFile(bundle) as zf:
        native=[info.filename for info in zf.infolist() if info.filename.endswith(".so")]
        if native:
            raise SystemExit(
                f"{bundle.name}: native libraries detected. Manual 16 KB ELF/ZIP page-alignment "
                f"and ARM64 ABI review REQUIRED before release:\n"+"\n".join(native))
    print(f"{bundle.name}: no native shared objects bundled; no native-ELF alignment audit needed for these build outputs")
