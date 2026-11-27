# Music Player App

A fully functional Android music streaming application inspired by Spotify's visual design and user experience, built with modern Android technologies.

## Features

- **Home**: Personalized recommendations, recently played, new releases
- **Search**: Real-time search with suggestions, categories, trending
- **Library**: Playlists, albums, artists, liked songs, recently played
- **Now Playing**: Full-screen player with queue, lyrics, background playback
- **Playlist Management**: Create, edit, reorder, share playlists
- **Offline Support**: Local audio import and cached playback

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose, Material 3 |
| Architecture | Clean Architecture + MVVM |
| DI | Hilt |
| Database | Room |
| Networking | Retrofit + OkHttp |
| Media | Media3 / ExoPlayer |
| Images | Coil |
| Async | Coroutines + Flow |
| Navigation | Navigation Compose |
| Preferences | DataStore |

## Project Structure

```
MusicPlayerApp/
├── app/                          # Application module
├── core/
│   ├── common/                   # Shared utilities, theme, components
│   ├── data/                     # Repository interfaces
│   ├── domain/                   # Use cases, domain models
│   ├── model/                    # Data models (Parcelable)
│   └── testing/                  # Test utilities
├── data/
│   ├── local/                    # Room database, DAOs
│   ├── remote/                   # Retrofit API service
│   └── repository/               # Repository implementations
├── feature/
│   ├── home/                     # Home screen feature
│   ├── search/                   # Search feature
│   ├── library/                  # Library feature
│   ├── playback/                 # Playback/Now Playing feature
│   ├── playlist/                 # Playlist management
│   ├── settings/                 # Settings feature
│   └── profile/                  # Profile feature
├── media/                        # Media3/ExoPlayer integration
└── build-logic/                  # Convention plugins
```

## Requirements

- Android Studio Koala (2024.1.1) or later
- JDK 17
- Android SDK 34
- Minimum SDK: 24 (Android 7.0)

## Getting Started

### 1. Clone and Open

```bash
git clone <repository-url>
cd MusicPlayerApp
```

Open in Android Studio.

### 2. Configure Secrets

Create `local.properties` in project root:

```properties
# API Configuration
API_BASE_URL=https://your-api.example.com/v1/

# Keystore (for release builds)
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

### 3. Build

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

### 4. Run

Connect device/emulator and run `app` configuration.

## Architecture Overview

### Clean Architecture Layers

```
Presentation (Feature Modules)
    ↓
Domain (UseCases, Repository Interfaces, Entities)
    ↓
Data (Repository Implementations, Room, Retrofit)
    ↓
Platform (Media3, System Services)
```

### Key Patterns

- **Repository Pattern**: Abstract data sources behind interfaces
- **UseCase Pattern**: Single-responsibility business logic
- **MVVM**: ViewModels with StateFlow for reactive UI
- **Dependency Injection**: Hilt for compile-time safety

## Development

### Adding a New Feature

1. Create feature module in `feature/`
2. Add to `settings.gradle.kts`
3. Define domain interfaces in `core/domain`
4. Implement repository in `data/repository`
5. Create ViewModel and UI in feature module
6. Add navigation routes in `AppNavHost`

### Code Style

- ktlint for formatting (official Kotlin style)
- Detekt for static analysis
- Follow Material 3 design guidelines
- Use sealed classes for UI state

## Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device)
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

## Build Variants

- `debug`: Debuggable, minify disabled
- `release`: Minified, obfuscated, signed

## Release Process

1. Update version in `gradle.properties`
2. Update `CHANGELOG.md`
3. Create release tag
4. Build release AAB: `./gradlew bundleRelease`
5. Upload to Play Console

## License

This project is for educational purposes. Do not use for commercial purposes without proper music licensing.

## Acknowledgments

- Spotify for UI/UX inspiration
- Android Media3 team for ExoPlayer
- Material Design team for Material 3
- All open-source libraries used