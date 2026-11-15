#!/usr/bin/env python3
"""Verify actual ELF PT_LOAD page alignment of native libraries in both AABs.

Having native libraries is not itself a Play error. Require 16 KB ELF
load-segment alignment on 64-bit ABIs and 4 KB on legacy 32-bit ABIs.
Generated Play APK ZIP alignment and actual device testing remain additional gates.
"""
from __future__ import annotations
import pathlib
import shutil
import subprocess
import sys
import tempfile
import zipfile

ROOT=pathlib.Path(__file__).resolve().parents[1]
BUNDLES=[
    ROOT/"app/build/outputs/bundle/freeRelease/app-free-release.aab",
    ROOT/"app/build/outputs/bundle/proRelease/app-pro-release.aab"
]
ABIS={"arm64-v8a":16384,"x86_64":16384,"armeabi-v7a":4096,"x86":4096}

def inspect(binary:bytes,name:str,minimum:int)->int:
    if binary[:4] != b"\x7fELF":
        raise ValueError(f"{name}: invalid ELF magic")
    with tempfile.NamedTemporaryFile(suffix=".so") as temp:
        temp.write(binary)
        temp.flush()
        result=subprocess.run(["readelf","-lW",temp.name],text=True,capture_output=True,check=False)
    if result.returncode:
        raise ValueError(f"{name}: readelf failed: {result.stderr.strip()}")
    loads=[line.split() for line in result.stdout.splitlines() if line.lstrip().startswith("LOAD ")]
    if not loads:
        raise ValueError(f"{name}: no ELF PT_LOAD segments")
    for fields in loads:
        if len(fields)<5:
            raise ValueError(f"{name}: incomplete PT_LOAD segment")
        file_offset=int(fields[1],16)
        virtual_address=int(fields[2],16)
        alignment=int(fields[-1],16)
        if alignment < minimum or file_offset % minimum != virtual_address % minimum:
            raise ValueError(f"{name}: PT_LOAD p_align={alignment}, offset={file_offset}, vaddr={virtual_address}; need {minimum}. Rebuild the dependency.")
    return len(loads)

def run()->int:
    if not shutil.which("readelf"):
        print("::error::binutils readelf is required for native ELF alignment QA",file=sys.stderr)
        return 1
    count=0
    try:
        for bundle in BUNDLES:
            if not bundle.is_file():
                raise ValueError(f"Missing AAB: {bundle}")
            with zipfile.ZipFile(bundle) as archive:
                entries=sorted(e for e in archive.namelist() if e.endswith(".so"))
                for path in entries:
                    abi=path.split("/")[-2]
                    if abi not in ABIS:
                        raise ValueError(f"{path}: unknown native ABI")
                    n=inspect(archive.read(path),path,ABIS[abi])
                    count+=1
                    print(f"{bundle.name}: {path} passes {ABIS[abi]}-byte ELF alignment ({n} PT_LOAD segments)")
                if not entries:
                    print(f"{bundle.name}: no native shared libraries")
    except (ValueError,zipfile.BadZipFile,OSError) as err:
        print(f"::error::{err}",file=sys.stderr)
        return 1
    print(f"Native alignment audit complete: {count} ELF libraries checked.")
    return 0

if __name__=="__main__":
    raise SystemExit(run())
