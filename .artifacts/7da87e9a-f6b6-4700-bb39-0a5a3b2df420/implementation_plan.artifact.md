# Implementation Plan - Fix Firebase Analytics Resolution Error

The project is failing to sync because it's trying to resolve `com.google.firebase:firebase-analytics-ktx`. Starting from Firebase BoM v32.0.0, Kotlin extensions (KTX) have been merged into the main artifacts, and the `-ktx` artifacts are being phased out or are no longer necessary.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///E:/RecipeVault/gradle/libs.versions.toml)
- Remove the `-ktx` suffix from Firebase library names:
    - `firebase-auth-ktx` -> `firebase-auth`
    - `firebase-firestore-ktx` -> `firebase-firestore`
    - `firebase-analytics-ktx` -> `firebase-analytics`
    - `firebase-crashlytics-ktx` -> `firebase-crashlytics`

## Verification Plan

### Automated Tests
- Run Gradle Sync to ensure all dependencies are resolved correctly.
- Execute `./gradlew :app:assembleDebug` to verify the build completes successfully.
