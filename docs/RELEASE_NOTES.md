# SubliX v0.1.7 — Home, Plans and Archive interaction and accessibility refinement

## Implemented
- Show **each actual occurrence date** for recurring weekly subscriptions in the Home upcoming-payment list; deterministic sorting avoids duplicate-key/row confusion.
- Plan cards now announce name, complete EUR amount, category, billing cycle and displayed date to assistive technology, with explicit button roles.
- Remove doubled/nested archive card backgrounds; one card per record, with independent 48dp-minimum Details and Restore actions and service-specific TalkBack descriptions.
- Improve Home heading layout in compact widths.
- Four JVM regression tests for occurrence dates, sorting and archived records, plus four Compose instrumentation test flows for Free and Pro on a running Android emulator.
- Real v0.1.6 screenshots are the new 45-screen visual baseline, with version-aligned v0.1.7 photos to refresh the source-only website and Play Store assets after emulator capture.

## Limits
- Pixel-perfect 1:1 design acceptance is **NOT VERIFIED**; concept SVGs have non-matching viewports and have not been approved as exact baselines.
- Actual manual TalkBack traversal and real-physical-device tests are **NOT PERFORMED**. See `docs/DEVICE_QA_MATRIX.md`.
- A passing emulator/CI does not establish that an app meets all Google Play policies; permanent signing and a public privacy policy URL remain necessary for publication.
