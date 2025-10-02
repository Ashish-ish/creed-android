# Creed Sports Ground Booking App

## Build & Run

### Android
1. Open in Android Studio.
2. Select `composeApp` run configuration.
3. Run on emulator or device.

### iOS
1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Select a simulator or device.
3. Build & run.

## Modules
- **composeApp**: UI, ViewModels, Navigation (Compose Multiplatform)
- **domain**: Pure Kotlin models, use-cases, repository interfaces
- **data**: Ktor client, DTOs, mappers, repository implementations, storage
- **di**: Koin modules, expect/actual for platform DI

## Features
- Clean Architecture
- Compose Navigation
- Ktor 3.x + Serialization
- Firebase Auth (OTP)
- RazorPay KMP
- Moko-Geo Location
- DataStore/NSUserDefaults
- Koin DI

## Notes
- 100% Kotlin (no Swift/XML)
- All business logic in domain/data
- UI logic in composeApp
- Platform-specific code via expect/actual

---

For more, see each module's README or source files.

