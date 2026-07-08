# Newzero — Cross-Platform RSS Reader

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.21-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.11.0-4285F4?logo=jetpackcompose)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Android](https://img.shields.io/badge/Android-14%2B-3DDC84?logo=android)](https://developer.android.com)
[![iOS](https://img.shields.io/badge/iOS-16%2B-000000?logo=apple)]()
[![Desktop](https://img.shields.io/badge/Desktop-JVM-FF6F00)]()

**Newzero** is a modern, cross-platform RSS/Atom feed reader built entirely with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It delivers a consistent, native-quality experience across Android, iOS, and Desktop — all from a single shared codebase.

---

## Screenshots

### Mobile (Android & iOS)

<table>
  <tr>
    <td align="center"><strong>Android</strong></td>
    <td align="center"><strong>iOS</strong></td>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/rajumark/Newzero/main/screenshots/screenshot_light.png" alt="Android screenshot" width="280"/></td>
    <td><img src="https://raw.githubusercontent.com/rajumark/Newzero/main/screenshots/iphone17_light.png" alt="iOS screenshot" width="280"/></td>
  </tr>
</table>

### Desktop (macOS / Windows / Linux)

<p align="center">
  <img src="https://raw.githubusercontent.com/rajumark/Newzero/main/screenshots/desktop_light.png" alt="Desktop screenshot" width="600"/>
</p>

---

## Features

| Feature | Description |
|---|---|
| **RSS 2.0 & Atom Feed Support** | Parses both feed formats via Ktor XML content negotiation with `kotlinx.serialization`. Custom `@XmlSerialName` annotations map the XML structure directly to domain models. |
| **Offline-First Architecture** | Two-tier cache (in-memory + JSON-persisted via `multiplatform-settings`) ensures articles are available without a network connection. |
| **Pull-to-Refresh** | Material Design 3 styled pull-to-refresh with forced re-fetch on pull-down gesture. |
| **Compose Multiplatform UI** | Shared UI layer renders natively on Android (Jetpack Compose), iOS (SwiftUI interop), and Desktop (Swing/Window). |
| **Material Design 3 Theming** | Dynamic light/dark color schemes with custom Slate palette. Theme modes: `AUTO` (follows system), `LIGHT`, `DARK`. |
| **Async Image Loading** | Image thumbnails and feed icons rendered with Coil 3, supporting HTTP/HTTPS via Ktor engine. |
| **Background Sync (Android)** | WorkManager-based periodic hourly refresh keeps feeds up to date. |
| **Custom Redux Architecture** | Lightweight `NanoStore` pattern — unidirectional data flow with `State`, `Action`, and `Effect` sealed classes. |
| **Error Dialogs with Copy Support** | Network/parse errors displayed in a clean dialog with one-tap clipboard copy for debugging. |

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.3.21 |
| **UI Framework** | Compose Multiplatform 1.11.0 + Compose Material 3 |
| **Navigation** | Jetpack Navigation Compose (Multiplatform) |
| **Networking** | Ktor 3.4.3 (OkHttp engine on Android/JVM, Darwin engine on iOS) |
| **XML Parsing** | `xmlutil` 0.91.3 + `ktor-serialization-kotlinx-xml` |
| **Image Loading** | Coil 3.4.0 (KMP) |
| **Dependency Injection** | Koin 4.2.1 |
| **Persistence** | `multiplatform-settings` 1.3.0 (SharedPreferences/NSUserDefaults/Properties) |
| **Serialization** | `kotlinx.serialization` 1.11.0 |
| **Coroutines** | `kotlinx.coroutines` 1.11.0 |
| **Background Sync** | Android WorkManager 2.11.2 |
| **Logging** | Napier 2.7.1 |
| **Build System** | Gradle 9.x + AGP 9.0.1 |

---

## Architecture

```
┌────────────────────────────────────────────────────────────────────┐
│                      Compose Multiplatform UI                      │
│  ┌──────────┐  ┌──────────┐  ┌─────────────┐  ┌───────────────┐   │
│  │ PostList  │  │ FeedList │  │ Settings UI  │  │ Error Dialog  │   │
│  └────┬─────┘  └────┬─────┘  └──────┬──────┘  └───────┬───────┘   │
│       │              │               │                 │           │
│       └──────────────┴───────────────┴─────────────────┘           │
│                              │                                     │
│                    ┌─────────▼──────────┐                          │
│                    │   ArticleStore     │   Redux-like Store       │
│                    │  (NanoStore impl)  │   (State / Action /      │
│                    │                    │    Effect pattern)       │
│                    └─────────┬──────────┘                          │
└──────────────────────────────┼─────────────────────────────────────┘
                               │
               ┌───────────────┼───────────────┐
               ▼               ▼               ▼
        ┌────────────┐  ┌───────────┐  ┌──────────────┐
        │ FeedManager│  │ FeedCache │  │ RssService   │
        │(Core Logic)│  │(Persistence)│ │ (Ktor HTTP)  │
        └──────┬─────┘  └───────────┘  └──────┬───────┘
               │                               │
               ▼                               ▼
        ┌───────────┐                 ┌───────────────┐
        │Settings   │                 │  RSS / Atom   │
        │(Multi-    │                 │  XML API      │
        │ platform) │                 │               │
        └───────────┘                 └───────────────┘
```

### Project Structure

```
Newzero/
├── shared/                         # KMP shared module (commonMain, androidMain, iosMain)
│   └── src/
│       └── commonMain/kotlin/com/rajumark/newzero/
│           ├── app/                # Application state (Redux store)
│           │   ├── ArticleStore.kt # Action dispatch, async side effects
│           │   └── ReduxCore.kt    # NanoStore interface
│           ├── core/
│           │   ├── FeedManager.kt  # Business logic (add/remove/load feeds)
│           │   └── NetworkClient.kt# Ktor HttpClient with XML plugins
│           ├── datasource/
│           │   ├── network/
│           │   │   └── RssService.kt # HTTP + XML deserialization
│           │   └── storage/
│           │       └── FeedCache.kt  # In-memory + disk cache
│           ├── domain/
│           │   └── ArticleFeed.kt  # Domain models (RSS + Atom)
│           └── ui/                 # Compose Multiplatform shared UI
│               ├── ArticleList.kt  # Post list with image cards
│               ├── FeedDialogs.kt  # Add/Delete/Error dialogs
│               ├── FeedView.kt     # Main feed + article bar
│               ├── Navigation.kt   # Screen routing + top bar
│               ├── Settings.kt     # Theme toggle settings
│               ├── SourceIcon.kt   # Favicon loading with Coil
│               ├── SourceList.kt   # Feed management screen
│               ├── Theme.kt        # MD3 light/dark color schemes
│               └── Samples.kt      # Demo data for development
├── androidApp/                     # Android platform module
│   └── src/main/kotlin/com/rajumark/newzero/
│       ├── App.kt                  # Application class + Koin init
│       └── sync/SyncWorker.kt      # WorkManager background refresh
├── iosApp/                         # iOS Xcode wrapper project
├── desktopApp/                     # Desktop JVM application
└── build.gradle.kts                # Root build configuration
```

---

## Data Flow

```
User taps "Add Feed"
       │
       ▼
dispatch(ArticleAction.Add(url))
       │
       ▼
ArticleStore.addSource(url)
       │
       ├─ FeedManager.addSource(url)
       │     ├─ RssService.fetchByUrl(url)
       │     │     └─ Ktor HTTP GET → XML Response
       │     │
       │     │     ├─ Content-Type: application/rss+xml
       │     │     │   └─ Deserialize as ArticleFeed (RSS 2.0)
       │     │     │
       │     │     ├─ Content-Type: application/atom+xml
       │     │     │   └─ Deserialize as AtomFeed → convert to ArticleFeed
       │     │     │
       │     │     └─ Content-Type: application/xml or text/xml
       │     │         └─ Try RSS parser first
       │     │
       │     └─ FeedCache.save(feed)
       │           ├─ memCache[url] = feed
       │           └─ diskCache = memCache (JSON → platform storage)
       │
       ├─ loadAllSources() → re-read cache
       │
       └─ dispatch(ArticleAction.Data(sources))
             │
             ▼
          UI recomposes with new feed list
```

---

## Getting Started

### Prerequisites

- **Android Studio Ladybug** or newer (or IntelliJ IDEA with KMP plugin)
- **JDK 21** (for Desktop target)
- **Xcode 16+** (for iOS target — macOS only)
- Android SDK 36 (for Android target)

### Clone & Build

```bash
git clone https://github.com/rajumark/newzero.git
cd newzero

# Build all shared module targets
./gradlew :shared:build

# Build & install Android app
./gradlew :androidApp:installDebug

# Run Desktop app
./gradlew :desktopApp:run

# Build iOS framework (macOS only)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
# Then open iosApp/iosApp.xcodeproj in Xcode and run on simulator
```

### Quick Start

To verify the build:

```bash
./gradlew :shared:compileKotlinJvm
```

---

## Platform Support

| Platform | Status | Notes |
|---|---|---|
| Android | ✅ Fully supported | Target SDK 36, minSdk 26, WorkManager background sync |
| iOS | ✅ Supported | SwiftUI wrapper via `UIViewControllerRepresentable`, simulator + device |
| Desktop (JVM) | ✅ Supported | macOS/Windows/Linux via Compose Desktop, native distributions (DMG/MSI/DEB) |

---

## Key Design Decisions

### 1. Why a Custom Redux Pattern Instead of a Library?

The `NanoStore` interface (`ReduxCore.kt:10-14`) is deliberately minimal — just three primitives: `stateFlow()`, `effectFlow()`, and `dispatch()`. This avoids heavyweight state management libraries and keeps the entire store implementation in ~140 lines of well-tested code. The sealed `Action` / `Effect` classes make every possible state transition explicit and auditable.

### 2. Why Two-Tier Caching?

Feeds are cached in memory (`memCache`) for instant reads during the session, and serialized to JSON on disk (`diskCache`) for persistence across app restarts. The disk cache is only flushed when feeds change (add/remove/refresh), minimizing IO. This gives the best of both worlds: speed and durability.

### 3. Handling Both RSS and Atom with the Same Pipeline

The `RssService` inspects the response `Content-Type` header:
- `application/rss+xml` → deserialized directly as `ArticleFeed`
- `application/atom+xml` → deserialized as `AtomFeed`, then mapped to `ArticleFeed`
- `application/xml` / `text/xml` → also registered as XML deserializable types

This transparent conversion means the entire UI layer only ever deals with a single `ArticleFeed` model, regardless of the feed format.

---

## Release Process

1. Bump `versionCode` and `versionName` in `androidApp/build.gradle.kts`
2. Commit with a message containing `#go` to trigger CI/CD
3. GitHub Actions builds signed APK and creates a GitHub Release

---

## License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

---

<p align="center">
  Built with ❤️ using Kotlin Multiplatform and Compose Multiplatform
</p>
