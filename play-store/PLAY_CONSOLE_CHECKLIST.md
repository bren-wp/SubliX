# Play Console declarations — draft and reviewer checklist

**Not an automatic declaration, not legal advice.** Must be manually confirmed against final app binaries and every SDK before submission.

## Application identities

| Property | SubliX | SubliX Pro |
| --- | --- | --- |
| Google Play title | SubliX by BRENDIGO | SubliX Pro by BRENDIGO |
| Package ID | `com.brendigo.sublix` | `com.brendigo.sublixpro` |
| Android minSdk | 26 | 26 |
| Target SDK | 36 | 36 |
| Developer / publisher | BRENDIGO | BRENDIGO |
| App language | English UI | English UI |
| Account | Not required; no creation flow | Not required; no creation flow |
| Permissions intentionally requested | POST_NOTIFICATIONS, optional | POST_NOTIFICATIONS, optional |
| Ads SDK | None intentionally integrated | None intentionally integrated |
| Network / backend | No required in-app Internet or account server | Same |
| Data migration | User-directed JSON import/export | Same |
| Local database | Separate Android app sandbox | Separate Android app sandbox |

## Data safety (tentative, subject to binary and SDK audit)

- User-entered subscription names, amounts, categories, renewal details, trial end dates and recorded payments are **stored locally**.
- No deliberate transmission of these records to BRENDIGO or any app analytics server is implemented.
- Export is explicit, user-selected and unencrypted; users can choose cloud-backed document providers, which operate under their own terms.
- External HTTPS management links open in a system browser through an explicit user action; the app does not control or ingest the browser's content.
- If the merged manifest, SDK inventory or packet capture confirms no off-device collection, select the corresponding **no data collected/shared** answers. Do not simply copy this statement without verification. Google considers collection to include data transmitted by any SDK.
- Account deletion: there is no account. Users can erase local data by clearing storage or uninstalling; exported backups are outside the app sandbox.

## Financial features declaration

Every published app must complete the declaration. SubliX is not a lending, banking, wallet, payment transfer, crypto or investment service. Evaluate whether its personal budgeting and cost forecasting fall under a listed "Other" financial feature or whether "My app doesn't provide any financial features" is appropriate. Select the truthful option based on Play Console wording rather than assuming all money-related labels are financial regulated services.

## Content, distribution and review

- Use honest content-rating and target-audience declarations after completing the IARC questionnaire.
- If no advertising libraries or ad placements are in the app, declare no ads.
- Store listings should not say "free", "best", "#1", awards, prices or other promotional claims in their title, icons and marketing artwork.
- Distinguish the two editions clearly: Pro actually contains five additional screens and advanced analysis tools; avoid duplicate or near-identical metadata and screenshots.
- Google Play can require specific closed-testing access depending on developer-account eligibility. The repository cannot verify account requirements.
- Use Google Play App Signing and durable upload keys, not temporary GitHub test signatures.
- Device QA, accessibility, large-screen QA, 16-KB-page native dependencies and privacy URL remain human review gates.

## Action required before Play Console upload

- [ ] Confirm the Play publisher's public contact and website, if applicable.
- [ ] Publish the app-specific privacy policy at a publicly accessible HTTPS URL. Do not enable GitHub Pages contrary to the project decision.
- [ ] Install the final signed Android releases on physical devices and record end-to-end screenshots.
- [ ] Configure and back up permanent upload signing keys securely.
- [ ] Review Android merged manifests, dependency licenses, transitive SDKs and Play SDK Index warnings.
- [ ] Complete Play Console Data safety, financial features, content rating, age audience, app access and ads declarations for **both** apps.
- [ ] Confirm the Pro edition's distinct features meet repetitive-content policy requirements with Play review.
