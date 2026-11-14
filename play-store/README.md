# Google Play store-listing media — SubliX by BRENDIGO

This directory contains distinct **SubliX** and **SubliX Pro** Play Store asset sets and localized metadata for each separate Android application.

## Folder structure

- `free/app_icon_512.png` and `pro/app_icon_512.png`: 512×512 RGBA PNG, each under 1 MB.
- `free/feature_graphic_1024x500.png` and `pro/feature_graphic_1024x500.png`: 1024×500 RGB PNG (no transparency).
- `free/phone-screenshots/` and `pro/phone-screenshots/`: eight 1080×1920 RGB PNG screenshots per edition, taken directly from **actual v0.1.4 Android emulator captures**.
- `{edition}/listing/{en-US,hr-HR}/`: Play title, short description, full description and release notes, ready for direct copy to Play Console.
- `asset-manifest.json`: SHA-256 digests, dimensions, formats and screenshot origin.
- `../scripts/build_play_assets.py`: deterministic builder using original vector identity and authentic emulator captures.

## What is and isn't real

The phone screenshots are **the app itself running in an Android emulator**, with synthetic sample subscriptions. No screenshot is generated, recomposed, retouched, framed, overlaid with fake phone UI, or AI-generated. The only transformation from native emulator PNG to Play media is `RGBA → RGB`, preserving the actual UI's pixel colors. Promotional feature graphics are original brand artwork, not depictions of a functional screen.

`SubliX` and `SubliX Pro` logos and colorways are derived from `brending/icon-{free,pro}.svg`. Both are distinguished without adding prohibited words such as *Free*, *Discount* or ranking claims to the Play listing title, icon or feature graphic. The actual launcher shortcuts can still display the edition-specific local app label.

## Verify

```bash
python3 -m pip install -r scripts/play_assets_requirements.txt
python3 scripts/build_play_assets.py
python3 scripts/build_play_assets.py --check
python3 scripts/check_play_listing.py
```

The build validates exact icon and feature sizes, screenshot dimensions, screenshot identity, file size limits, language metadata length and asset hashes.

## Google Play Console prerequisites NOT completed by code alone

1. **Public Privacy Policy URL:** Google Play requires an active, worldwide-accessible HTTPS webpage with the app-specific privacy policy. The user's presentation site remains intentionally source-only on GitHub. `web/privacy.html` is drafted, **but a public hosted policy URL is still required before submitting to Play**. A link that only downloads HTML or displays GitHub source is not a guaranteed substitute.
2. **Upload signing identity:** GitHub preview APK/AAB builds use temporary CI keys. Do not submit them to Google Play. Configure permanent upload keystore secrets separately for long-term update continuity and Play App Signing.
3. **Play Console forms:** Complete Data safety for each app after reviewing all SDKs and merged manifests; content rating, target audience, ads declaration, financial features declaration and app-access declarations. No account is required; answer account questions truthfully.
4. **Store policy review:** Google prohibits repetitive content. SubliX Pro has materially differentiated forecasting, budgets, scenario planning and reporting, but Google ultimately determines whether two separate editions offer sufficiently distinct value.
5. **Testing and approval:** Test on real devices, large screens, TalkBack, gesture navigation, offline conditions, exported files and notification permissions. Perform 16 KB native-library checks where applicable. Passing CI is not Google Play approval.
6. **Localization:** Android UI is currently English; Croatian store metadata explicitly states this. Screenshots are English captures. Do not imply a localized Croatian UI.
7. **Payment handling:** This app tracks user-entered recurring expenses. It does not sell subscriptions or process external subscription payments. Any future payment model must be reviewed against Google Play Billing policy before deployment.

### Sources (official Google documentation)

- [Store listing graphic requirements](https://support.google.com/googleplay/android-developer/answer/9866151)
- [Metadata policy and best practices](https://support.google.com/googleplay/android-developer/answer/13393723)
- [Target API requirements](https://support.google.com/googleplay/android-developer/answer/11926878)
- [Prepare app for review and public privacy policy](https://support.google.com/googleplay/android-developer/answer/9859455)
- [Financial features declaration](https://support.google.com/googleplay/android-developer/answer/13849271)
- [Data safety requirements](https://support.google.com/googleplay/android-developer/answer/10787469)
- [Repetitive content](https://support.google.com/googleplay/android-developer/answer/9899034)
- [16 KB native page sizes](https://developer.android.com/guide/practices/page-sizes)
