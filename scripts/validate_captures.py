#!/usr/bin/env python3
"""Validate version-matched Android emulator PNGs, including blocking-overlay rejection.

Image dimensions and distinct hashes alone are insufficient: older SubliX captures
were contaminated by an Android "Pixel Launcher isn't responding" system dialog.
A large pale modal covering the center of a dark-theme app is a capture failure.
This is a *capture integrity* gate, not proof of 1:1 design conformance.
"""
from pathlib import Path
from hashlib import sha256
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]


def has_blocking_light_overlay(image: Image.Image) -> bool:
    """Reject near-white, screen-blocking system dialogs in dark-theme captures.

    All official gallery frames use SubliX's dark theme and capture normal pages,
    not Android system pickers. Sample the entire central viewport as a test,
    WITHOUT masking/cropping any pixels in the screenshots or RGB report.
    """
    width, height = image.size
    center = image.crop((int(width * .15), int(height * .40),
                         int(width * .85), int(height * .60)))
    samples = center.convert("RGB").resize((80, 48), Image.Resampling.NEAREST)
    bright = sum(1 for rgb in samples.getdata() if min(rgb) >= 225)
    return bright / (80 * 48) >= .80


def validate_capture_image(screenshot: Path, expected_size=(1080, 1920)) -> None:
    """Verify PNG structure before image decode; never modify source pixels."""
    with Image.open(screenshot) as im:
        assert im.format == "PNG" and im.size == expected_size, screenshot
        im.verify()
    with Image.open(screenshot) as im:
        if has_blocking_light_overlay(im):
            raise AssertionError(
                f"BLOCKING SYSTEM DIALOG / BRIGHT OVERLAY: {screenshot}. "
                "Re-capture from a responsive emulator; do not publish to Play.")


def validate_captures(root: Path = ROOT) -> int:
    version = (root / "VERSION").read_text(encoding="utf-8").strip()
    captures = root / "brending/screenshots-real"
    assert (captures / "CAPTURE_VERSION").read_text(encoding="utf-8").strip() == version
    digests = {}
    for edition, count in (("free", 20), ("pro", 25)):
        screenshots = sorted((captures / edition).glob("*.png"))
        assert len(screenshots) == count, (edition, len(screenshots))
        for screenshot in screenshots:
            validate_capture_image(screenshot)
            digest = sha256(screenshot.read_bytes()).hexdigest()
            if digest in digests:
                raise RuntimeError(
                    f"Duplicate screenshot for two routes: {digests[digest]} and {screenshot}")
            digests[digest] = screenshot
    assert len(digests) == 45
    return len(digests)


if __name__ == "__main__":
    count = validate_captures()
    print(f"Validated {count} distinct, version-matched Android PNGs without blocking light dialogs.")
