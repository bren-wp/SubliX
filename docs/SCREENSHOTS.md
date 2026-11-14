# Capturing real SubliX application screenshots

The GitHub Action `screenshots.yml` builds the current Kotlin/Compose Free and Pro debug editions, starts an Android emulator, installs both APKs, starts each actual Activity with a **debug-only** preview fixture, and captures PNG frames using `adb exec-out screencap -p`.

- Sample data are synthetic and only injected in debug builds when the `sublix_screenshot` intent extra is set.
- The release variants contain only a no-op preview class and no synthetic fixture.
- The capture job targets 20 mapped views per edition (40 PNGs total). Screenshots are automatically pushed to `brending/screenshots-real/{free,pro}` on the feature branch.
- The README switches from vector concepts to **real** PNG screenshots only after files have been created and minimally validated.
- These screenshots prove what rendered on the emulator, **not** pixel-perfect parity with the 20 reference concepts. Review on-device visual differences manually.
- The website remains source-only in the `web/` folder. No GitHub Pages workflow is used.
- Screenshot CI is intentionally scoped to the active PR branch so it does not create another public website deployment or trigger publication on `main`.

If the emulator fails or produces a crash, the workflow fails and the README is not switched to misleading images. Run `bash scripts/capture_screenshots.sh` on a test Android emulator with both debug APKs installed when working locally.
