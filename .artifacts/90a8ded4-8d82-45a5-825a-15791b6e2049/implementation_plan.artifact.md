# Implementation Plan - Fix BillingHelper Compilation Error

The project is using Play Billing Library version 9.1.0 (as per `libs.versions.toml`). In version 8.0.0 and above, the parameterless `enablePendingPurchases()` method has been removed and must be replaced with `enablePendingPurchases(PendingPurchasesParams)`.

## User Review Required

> [!IMPORTANT]
> The fix involves using `PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()`. This is functionally equivalent to the old parameterless call for one-time products. If the app also needs to support pending purchases for prepaid subscription plans, `.enablePrepaidPlans()` should also be called on the builder. For now, I will enable only one-time products to match previous behavior.

## Proposed Changes

### Billing Data Layer

#### [MODIFY] [BillingHelper.kt](file:///E:/RecipeVault/app/src/main/java/com/hritik/recipevault/data/billing/BillingHelper.kt)

- Update the `billingClient` initialization to use `enablePendingPurchases(PendingPurchasesParams)`.
- Ensure all other PBL 9.1.0 changes are handled. (It seems `queryProductDetailsAsync` is already using the new result-based callback).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` to verify that the compilation error is resolved.

### Manual Verification
- N/A (Compilation fix)
