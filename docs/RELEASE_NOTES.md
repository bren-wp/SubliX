# SubliX 0.1.2 — Screen coverage & offline workflows preview

Two separately installable Android flavors, Free and Pro, still without user accounts or server storage.

Changes:
- Added Compose UI navigation targets for all 20 design-specification views.
- Service template library, custom local categories, dedicated reminder settings, details for recorded payments, offline backup hub, standalone Pro overview, privacy and about.
- Trial-end tracking, cancellation-management URL field (HTTPS only; no external cancellation automation), usage frequency and Pro savings scenarios.
- Separate Pro monthly budget and review of low-usage subscriptions.
- SQLite version 1→2 non-destructive migration adds trial and usage metadata; existing records remain on device.
- Backup format v2 persists new optional fields and still accepts version 1 imports; both editions remain offline-first.
- Real Android screenshot capture updated to 20 views per edition; screenshots are only added when successfully captured from emulator sessions.
- Public website remains source-only under /web; no GitHub Pages deployment.

**Preview caution:** No claim of pixel-perfect 1:1 visual equivalence until emulator images have been captured and evaluated against reference artwork, including enlarged text and device sizes. Pro APKs publicly published here are not commercially restricted. When permanent signing secrets are absent, GitHub builds use a temporary, untrusted CI signing key. Test only; not Play Store ready.
