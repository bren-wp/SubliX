# SubliX Free — Google Play compliance and release gate (v0.1.13)

## Noncommercial Free app contract

- **Package:** `com.brendigo.sublix`. The optional separate application `com.brendigo.sublixpro` must NEVER be sold, suggested, advertised, linked as a paid upgrade, or shown as a locked feature inside the Free app.
- **Google Play price:** **EUR 0** (Free). **Ads:** No. **In-app products:** None. **Paid subscriptions:** None. **Commercial upgrade prompts:** None.
- **Account:** None required. **Network access:** no `INTERNET` or `AD_ID` permission. No ad SDK, BillingClient, third-party ad analytics or external checkout in Free.
- The Free build contains usable offline budgeting summaries, renewal calendar, subscriptions, reminders, categories, local backup and getting-started help. Free-only navigation must remain useful independently, without locked features or paywalls.
- Source-of-truth for **intended** configuration: `play-store/free/free-policy.json`. Repository checks validate code, source declarations and public listing text; they **cannot modify or read the Google Play Console account state**.

## Google Play Console — operator must verify before submission

1. **Pricing and distribution:** In the Free app's Play Console settings, select **Free** and verify the listed price is **EUR 0** in supported markets. Never assume a repository metadata file sets store pricing. Google generally permits paid→free but not free→paid conversion for the same application.
2. **App content → Ads:** answer **No, does not contain ads**, based on the verified final release AAB/manifest and real SDK inventory. Changes to monetization require a new accurate declaration.
3. **In-app products/subscriptions:** no Billing library and no active in-app paid products for Free. Do not insert any purchase/upgrade navigation, marketing screens or payment permission into its variant.
4. **Data safety:** enter the actual behavior of the final release. The current app stores records locally and has no Android Internet permission; manually exported **unencrypted** JSON files are user-chosen local documents. Check declared collection/sharing, security practices and any third-party SDKs against the final dependency graph; never claim encrypted exports.
5. **Privacy policy:** publish a directly reachable public policy URL explaining offline storage, notifications, unencrypted optional exports, deletion and contact. `web/privacy.html` is source content, **not evidence of a publicly accessible URL**. Configure that link in Play Console and verify access from an incognito browser.
6. **Content rating / target audience:** complete the forms based on actual features and intended audience. Never assume a child-directed rating without reviewing the Google Families requirements.
7. **Android API and permissions:** app targetSdk **36** (Android 16), minSdk **26**; compileSdk **37.1** is solely for compatible current Compose libraries. Notification access is the only manifest permission; requested at runtime on Android 13+.
8. **Release security:** submit production-signed AAB for the correct package identity using the permanent key / Play App Signing setup; temporary CI preview signatures cannot replace an existing app's authorized signing key.
9. **Store metadata:** localized EN/HR description, short text and release notes must describe Free independently with no Pro upsell or suggested paid restrictions. Real, unaltered Free app screenshots only; no reference concept images as fabricated captures. Console Data safety/ads/pricing answers are distinct from text metadata.
10. **Developer verification:** complete any applicable Google Play developer identity and app package registration checks before release.

## Automated repository gates

`python3 scripts/check_free_play_policy.py` validates intended 0 EUR/no-ads/no-IAP values, forbidden billing/ad dependencies and permissions, Free navigation, no-upgrade screen behavior and EN/HR store copy. It runs via `scripts/check_play_listing.py` in Android/web CI, visual QA and signed-release workflows. Instrumentation tests reject paid/Pro prompts across Home, Plans, Calendar, Insights, Settings, Getting started, Budget and Savings screens.

Run release build, Android Lint and instrumented tests for both variants. Inspect the **merged Free manifest and final AAB**, not source alone, for unexpected transitive SDKs before declaring absence of ads or trackers. Release only after code+media CI succeeds on the final revision. A passing repository gate is **not** Google Play approval.

## Official references

- https://support.google.com/googleplay/android-developer/answer/9859455 — ads and app content declarations
- https://support.google.com/googleplay/android-developer/answer/6334373 — free app pricing
- https://developer.android.com/google/play/requirements/target-sdk — target API requirement
- https://support.google.com/googleplay/android-developer/answer/10787469 — data safety
