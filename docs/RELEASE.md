# SubliX — Build & release contract

## Products
Two standalone apps from one source tree:
- Free: `com.brendigo.sublix`
- Pro: `com.brendigo.sublixpro`

Both work without sign-in, backend or bank connectivity. Android stores their data in different sandboxes; Free-to-Pro transfer requires **explicit JSON export/import**. No automatic hidden sync.

## Required output with every verified release
1. `SubliX-Free-vX.Y.Z.apk`
2. `SubliX-Pro-vX.Y.Z.apk`
3. `SubliX-Free-vX.Y.Z.aab`
4. `SubliX-Pro-vX.Y.Z.aab`
5. `SubliX-Source-vX.Y.Z.zip` and `SHA256SUMS.txt`

GitHub itself also offers automatic source ZIP and TAR archives.

## Configure signing (private GitHub Actions secrets)
**Never** commit a signing key. Generate and retain a keystore offline. Put these secrets in repository Settings > Secrets and variables > Actions:
- `SUBLIX_KEYSTORE_BASE64` — base64-encoded .jks file
- `SUBLIX_STORE_PASSWORD`
- `SUBLIX_KEY_ALIAS`
- `SUBLIX_KEY_PASSWORD`

Signing is required for production-ready release artifacts. If **some but not all** private signing secrets are configured, publishing fails closed. If **all four** are configured, CI uses those secrets. If **none** are configured, CI generates a new temporary untrusted signing key and publishes a **clearly labeled test-only preview** with both APKs, both AABs, and source ZIP. These previews are NOT suitable for Play Store submission; previews from separate CI runs cannot update each other. APK signature and AAB JAR signature are verified before a GitHub Release is created. Keep keystore backups; losing a signing key may prevent sideloaded-app updates. For Google Play, follow Play App Signing's upload-key process.

## Workflow
1. Update `VERSION`, `versionCode`, and `versionName` consistently.
2. Commit changes to a pull request, await green checks, peer-review, and merge to main.
3. `release.yml` builds and tests both flavors on main, validates signatures, and publishes a **new version** as a GitHub pre-release. Existing tags are never overwritten.
4. Website is retained only as source code under `/web`. There is no GitHub Pages publishing workflow.
5. Only mark a release stable when device testing, security, accessibility, and store policies have passed.

**Important:** Publicly published Pro APK/AAB files and publicly licensed Pro sources can be downloaded without paying. A paid storefront strategy requires revisiting the public-distribution policy. There is no legitimate offline-only copy-protection mechanism that can guarantee the app cannot be copied.

## Known current limitations
- The initial implementation is an early development milestone, not a confirmed production-ready app.
- UI mockups in `brending/screens` are concepts; require real screenshots prior to Play listing.
- App uses EUR only; no guessed exchange-rate conversions.
- JSON backups are manually exported and **unencrypted**; tell users to store them securely.
- Reminders use WorkManager and are approximate, not exact alarms.
- Basic dashboards, subscription edits, archives, history, reminders and Pro budget are in scope of the starter implementation. Additional 20-screen specification features remain roadmap until implemented/tested.
- No release can be claimed until GitHub Actions confirms all five archives and a GitHub Release.

## Website
**Do not enable GitHub Pages.** The site stays within `/web` as static source code. Historical `github-pages` environments/deployments may remain visible in GitHub history even after workflow deletion; remove/disable them using GitHub repository Settings > Pages / Environments if accessible with admin permission.

## Real Android captures
A dedicated Android emulator workflow creates actual PNG images of the built applications with synthetic debug-only subscription data. README switches to real images only after screenshots exist. See [screenshots](SCREENSHOTS.md).

## Website and Android parity (v0.1.4)
The website source in `/web` uses identical color tokens and **real** Android emulator capture PNGs. Run `python3 scripts/check_sync.py` in CI. The web is intentionally not hosted through Pages or any other deployment workflow; releases contain source code for both Android apps and the static site.

## Important: new Android application identities (v0.1.5)

| Edition | Immutable Android / Play package ID | Gradle flavor |
| --- | --- | --- |
| Free | `com.brendigo.sublix` | `free` |
| Pro | `com.brendigo.sublixpro` | `pro` |

Both use the common code namespace `com.brendigo.sublix`; the namespace is **not** the Android install/package identity. The Gradle source-set names (`free`, `pro`) and generated artifact names remain unchanged. Verify final package IDs from the APK manifest in CI before publishing.

Changing an application ID **does not update** an already-installed app under the old package ID. Earlier test builds `com.brendigo.sublix.free` / `com.brendigo.sublix.pro` are considered distinct apps: they will not be upgraded in place and their app-private data is not automatically copied. Export JSON from the old test app before uninstalling it, then manually import into the new one. Android signing keys must also remain stable for future updates to these new IDs.

**Google Play listing IDs are permanent after initial publication**; if either old package ID is already published on Play, the new ID requires a separate new Play listing. Verify this in Play Console before publishing. Never publish an app under an unintended ID.
