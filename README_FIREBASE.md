# Firebase Setup for Creed KMP App

This document explains how to set up Firebase for both Android and iOS platforms in your Kotlin Multiplatform Mobile (KMP) project.

## Prerequisites

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable the following services in your Firebase project:
   - Authentication (Phone Number provider)
   - Analytics
   - Crashlytics
   - Performance Monitoring

## Android Setup

### 1. Add Android App to Firebase Project

1. Go to Firebase Console → Project Settings → General
2. Click "Add app" → Select Android
3. Enter your Android package name (e.g., `com.creed.android`)
4. Download `google-services.json`

### 2. Place Configuration File

Place the downloaded `google-services.json` file in:
```
composeApp/src/androidMain/
```

### 3. Gradle Dependencies

The following dependencies are already configured in `composeApp/build.gradle.kts`:

```kotlin
android {
    // ... existing config
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-perf")
    implementation("com.google.firebase:firebase-analytics")
}
```

### 4. ProGuard Rules

Add the following rules to `proguard-rules.pro`:

```proguard
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
```

## iOS Setup

### 1. Add iOS App to Firebase Project

1. Go to Firebase Console → Project Settings → General
2. Click "Add app" → Select iOS
3. Enter your iOS bundle ID (e.g., `com.creed.iosApp`)
4. Download `GoogleService-Info.plist`

### 2. Place Configuration File

Place the downloaded `GoogleService-Info.plist` file in:
```
iosApp/iosApp/
```

Make sure to add it to your Xcode project target.

### 3. CocoaPods Dependencies

Add the following to `iosApp/Podfile`:

```ruby
target 'iosApp' do
  use_frameworks!
  platform :ios, '14.1'
  
  # Firebase dependencies
  pod 'Firebase/Auth', '~> 10.27.0'
  pod 'Firebase/Crashlytics', '~> 10.27.0'
  pod 'Firebase/Performance', '~> 10.27.0'
  pod 'Firebase/Analytics', '~> 10.27.0'
  
  # Your other pods...
end
```

Then run:
```bash
cd iosApp
pod install
```

### 4. Info.plist Configuration

Add the following to `iosApp/iosApp/Info.plist` (replace `REVERSED_CLIENT_ID` with the actual value from `GoogleService-Info.plist`):

```xml
<key>CFBundleURLTypes</key>
<array>
  <dict>
    <key>CFBundleURLName</key>
    <string>REVERSED_CLIENT_ID from GoogleService-Info.plist</string>
    <key>CFBundleURLSchemes</key>
    <array>
      <string>REVERSED_CLIENT_ID value</string>
    </array>
  </dict>
</array>
```

### 5. Initialize Firebase in iOS App

Add Firebase initialization to your iOS app delegate or main app file:

```swift
import Firebase

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

## Usage Examples

### Phone Authentication

```kotlin
// Send OTP
signInWithPhoneNumber(
    phone = "+1234567890",
    onCodeSent = { 
        // Navigate to OTP input screen
    },
    onVerificationCompleted = { token ->
        // Auto-verification completed (Android only)
        // Save token and proceed
    },
    onError = { error ->
        // Handle error
    }
)

// Verify OTP
verifyCode(
    code = "123456",
    onSuccess = { token ->
        // Save Firebase ID token
        // Call your backend API with token
    },
    onError = { error ->
        // Handle verification error
    }
)
```

### Analytics

```kotlin
// Log custom events
logAnalyticsEvent("login_success", mapOf(
    "method" to "phone",
    "user_type" to "new"
))

logAnalyticsEvent("arena_viewed", mapOf(
    "arena_id" to "arena_123",
    "sport_type" to "football"
))
```

### Crashlytics

```kotlin
// Set user identifier
setCrashlyticsUserId("user_123")

// Record non-fatal exceptions
try {
    // Some code that might throw
} catch (e: Exception) {
    recordCrashlyticsException(e)
    // Handle gracefully
}
```

### Performance Monitoring

```kotlin
// Custom traces
val trace = startPerformanceTrace("fetch_arenas")
try {
    // Your network call or expensive operation
    val arenas = arenaRepository.getNearby(lat, lng)
} finally {
    trace.stop()
}
```

## Authentication Flow

1. **Phone Number Input**: User enters phone number
2. **OTP Request**: Call `signInWithPhoneNumber()`
3. **OTP Verification**: User enters received code, call `verifyCode()`
4. **Token Exchange**: Use Firebase ID token to authenticate with your backend
5. **Session Management**: Store auth tokens securely

## Security Notes

1. **Phone Auth**: Ensure proper rate limiting on your Firebase project
2. **Token Validation**: Always validate Firebase ID tokens on your backend
3. **ProGuard**: Keep Firebase classes to prevent obfuscation issues
4. **Bundle IDs**: Ensure bundle IDs match exactly between Firebase console and your apps

## Troubleshooting

### Android
- Ensure `google-services.json` is in the correct location
- Check that Firebase project has Phone Auth enabled
- Verify ProGuard rules are applied

### iOS
- Ensure `GoogleService-Info.plist` is added to Xcode project
- Check that bundle ID matches Firebase configuration
- Verify CocoaPods installation is successful
- Ensure Firebase is initialized in app delegate

### Common Issues
- **Invalid Phone Number**: Ensure phone numbers include country code
- **Quota Exceeded**: Check Firebase usage quotas in console
- **Network Errors**: Implement proper retry logic
- **Token Expiry**: Handle token refresh appropriately
