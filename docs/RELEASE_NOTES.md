# SubliX 0.1.6 — responsive UI, recurring-cost correctness and 45-screen visual QA

This version includes two separately installable Android applications:
- SubliX Free: com.brendigo.sublix
- SubliX Pro: com.brendigo.sublixpro

## Implemented changes
- Subscription rows adjust to narrow phone layouts by moving the amount below the name/category rather than competing for horizontal space.
- Primary financial figures now constrain long amounts and provide clearer upcoming-payment labels.
- Corrected calendar/forecast recurrence for very old but still active weekly/monthly/quarterly/yearly subscriptions.
- Added seven recurrence regression tests for century-old dates, 29–31 day anchors, leap days, Pro monthly forecasts, archive and reversed time ranges.
- Fixed capture fixture drift by using a deterministic screenshot-only debug clock (15 Nov 2026), never the production clock.
- Established 45 authentic v0.1.5 emulator screenshots as prior-version regression baselines.
- New 45-screen RGB audit, per-screen changed-pixel metrics and review overlays (CI artifacts), with explicit NOT VERIFIED 1:1 design approval.
- New 900×2000, 1.3× font smoke captures for four key screens in Free and Pro.
- Screenshot publishing checks actual dimensions, unique frames and capture version before refreshing website and Play assets.
- Web and README continue to display genuine Android captures; web is source-only on GitHub, not hosted with Pages.

## Release and policy notices
- No login, required backend, bank credentials or automatic provider cancellation.
- These are preview binaries, signed with an ephemeral CI key unless private upload-key secrets are present. Do not submit temporary-signature AABs to Play.
- Google Play requires a publicly accessible privacy policy URL that is not supplied by repository source-only web files.
- 1:1 visual identity with the original 432×960 concept SVGs remains **unverified**. The responsive and RGB test improvements do not replace approved reference assets, TalkBack testing or physical-device interaction checks.
