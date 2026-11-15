"""Unit tests for the capture-integrity gate; no fabricated store screenshots."""
import tempfile
import unittest
from pathlib import Path
from PIL import Image, ImageDraw
from validate_captures import has_blocking_light_overlay, validate_capture_image


class CaptureOverlayDetectionTest(unittest.TestCase):
    def test_dark_app_screen_is_accepted(self):
        img = Image.new("RGB", (1080, 1920), (21, 22, 33))
        painter = ImageDraw.Draw(img)
        painter.rounded_rectangle((54, 400, 1026, 700), radius=45, fill=(97, 88, 146))
        self.assertFalse(has_blocking_light_overlay(img))

    def test_central_android_anr_dialog_is_rejected(self):
        img = Image.new("RGB", (1080, 1920), (21, 22, 33))
        painter = ImageDraw.Draw(img)
        painter.rounded_rectangle((72, 738, 1008, 1182),
                                  radius=60, fill=(244, 243, 250))
        self.assertTrue(has_blocking_light_overlay(img))

    def test_valid_capture_is_verified_before_decoding(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "valid.png"
            Image.new("RGB", (320, 400), (21, 22, 33)).save(path)
            validate_capture_image(path, expected_size=(320, 400))

    def test_capture_with_system_dialog_is_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "anr.png"
            img = Image.new("RGB", (320, 400), (21, 22, 33))
            ImageDraw.Draw(img).rectangle((35, 150, 285, 250),
                                          fill=(244, 243, 250))
            img.save(path)
            with self.assertRaisesRegex(AssertionError, "BLOCKING SYSTEM DIALOG"):
                validate_capture_image(path, expected_size=(320, 400))

    def test_corrupt_png_is_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "broken.png"
            Image.new("RGB", (320, 400), (21, 22, 33)).save(path)
            path.write_bytes(path.read_bytes()[:-32])
            with self.assertRaises(Exception):
                validate_capture_image(path, expected_size=(320, 400))

    def test_small_light_controls_do_not_trigger_false_positive(self):
        img = Image.new("RGB", (1080, 1920), (21, 22, 33))
        painter = ImageDraw.Draw(img)
        painter.rectangle((400, 950, 680, 1020), fill=(255, 255, 255))
        self.assertFalse(has_blocking_light_overlay(img))


if __name__ == "__main__":
    unittest.main()
