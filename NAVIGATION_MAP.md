# Navigation Map: Spotify-Inspired Music Application

## Overview
This document defines the complete navigation structure for the independent music streaming application, based on patterns observed in the Spotify APK analysis. Designed for Jetpack Navigation Component with Compose.

## Navigation Graph Structure

```
NavGraph (startDestination = "home")
├── Home (Bottom Nav Tab 1)
│   ├── home
│   ├── recentlyPlayedDetail
│   ├── playlistDetail
│   ├── albumDetail
│   └── artistDetail
├── Search (Bottom Nav Tab 2)
│   ├── search
│   ├── searchResults
│   ├── browseCategories
│   ├── categoryDetail
│   ├── playlistDetail
│   ├── albumDetail
│   └── artistDetail
├── Library (Bottom Nav Tab 3)
│   ├── library
│   ├── playlists
│   ├── albums
│   ├── artists
│   ├── likedSongs
│   ├── recentlyPlayed
│   ├── playlistDetail
│   ├── playlistCreate
│   ├── playlistEdit
│   ├── albumDetail
│   └── artistDetail
├── NowPlaying (Bottom Sheet / Full Screen - Global)
│   ├── nowPlaying (full screen)
│   ├── queue
│   └── lyrics
├── Settings (Top-level, not in bottom nav)
│   ├── settings
│   ├── account
│   ├── playback
│   ├── notifications
│   ├── storage
│   ├── about
│   └── legal
├── Profile (Top-level, not in bottom nav)
│   ├── profile
│   ├── editProfile
│   └── followers/following
└── Deep Link Entry Points
    ├── track/:trackId
    ├── album/:albumId
    ├── artist/:artistId
    ├── playlist/:playlistId
    ├── category/:categoryId
    └── search/:query
```

## Screen Definitions

### 1. Home Tab (`home`)

| Route | Arguments | Description |
|-------|-----------|-------------|
| `home` | - | Main home feed with sections |
| `recentlyPlayedDetail` | `itemId`, `itemType` | Full list of recently played |
| `playlistDetail` | `playlistId` | Playlist detail screen |
| `albumDetail` | `albumId` | Album detail screen |
| `artistDetail` | `artistId` | Artist detail screen |

**Navigation Behavior:**
- Bottom nav tab: `home` is start destination
- Pressing Home tab again: scroll to top or pop to `home`
- Back from detail screens: return to `home` preserving scroll position
- Deep links: Direct to detail screens, back goes to appropriate parent

### 2. Search Tab (`search`)

| Route | Arguments | Description |
|-------|-----------|-------------|
| `search` | - | Search home with suggestions/categories |
| `searchResults` | `query`, `filter?` | Results with tabs (Songs, Artists, Albums, Playlists) |
| `browseCategories` | - | All categories grid |
| `categoryDetail` | `categoryId` | Category-specific content |
| `playlistDetail` | `playlistId` | Playlist detail |
| `albumDetail` | `albumId` | Album detail |
| `artistDetail` | `artistId` | Artist detail |

**Navigation Behavior:**
- Search bar focus: Expands to `searchResults` with empty query
- Category tap: `categoryDetail`
- Result tap: Navigate to appropriate detail
- Back from results: Return to `search` preserving query
- Clear query: Return to `search` home

### 3. Library Tab (`library`)

| Route | Arguments | Description |
|-------|-----------|-------------|
| `library` | - | Library home with top tabs |
| `playlists` | - | User playlists grid |
| `albums` | - | Saved albums grid |
| `artists` | - | Followed artists grid |
| `likedSongs` | - | Liked songs list (special playlist) |
| `recentlyPlayed` | - | Recently played list |
| `playlistDetail` | `playlistId` | Playlist detail |
| `playlistCreate` | - | Create new playlist (bottom sheet → full) |
| `playlistEdit` | `playlistId` | Edit playlist (full screen) |
| `albumDetail` | `albumId` | Album detail |
| `artistDetail` | `artistId` | Artist detail |

**Navigation Behavior:**
- Top tabs: Switch between `playlists`, `albums`, `artists`, `likedSongs`, `recentlyPlayed`
- FAB/Empty state: `playlistCreate` as bottom sheet
- Long press playlist: Context menu → Edit navigates to `playlistEdit`
- Back from detail: Return to current library tab

### 4. Now Playing (Global Overlay)

| Route | Arguments | Description |
|-------|-----------|-------------|
| `nowPlaying` | `trackId?`, `queueId?` | Full-screen player |
| `queue` | - | Queue management |
| `lyrics` | `trackId` | Synced lyrics view |

**Navigation Behavior:**
- **Global access**: Available from any screen via miniplayer tap
- **Entry**: Tap miniplayer or track row play action
- **Exit**: Swipe down, back button, or tap miniplayer
- **State**: Preserves queue, position, shuffle/repeat
- **Miniplayer**: Persistent bottom bar (not a destination)

### 5. Settings & Profile (Top-Level)

| Route | Arguments | Description |
|-------|-----------|-------------|
| `settings` | - | Settings main |
| `account` | - | Account management |
| `playback` | - | Audio quality, crossfade, gapless |
| `notifications` | - | Push, email preferences |
| `storage` | - | Cache, downloads, offline |
| `about` | - | Version, licenses, open source |
| `legal` | - | Privacy, terms |
| `profile` | - | User profile |
| `editProfile` | - | Edit profile info |

**Navigation Behavior:**
- Accessed from profile icon in top app bar (Home/Search/Library)
- Modal navigation (not in bottom nav)
- Back returns to originating screen

## Deep Link Handling

### Supported Deep Links

| Pattern | Destination | Arguments |
|---------|-------------|-----------|
| `musicapp://track/{trackId}` | `trackDetail` / `nowPlaying` | `trackId` |
| `musicapp://album/{albumId}` | `albumDetail` | `albumId` |
| `musicapp://artist/{artistId}` | `artistDetail` | `artistId` |
| `musicapp://playlist/{playlistId}` | `playlistDetail` | `playlistId` |
| `musicapp://category/{categoryId}` | `categoryDetail` | `categoryId` |
| `musicapp://search?q={query}` | `searchResults` | `query` |
| `https://musicapp.example.com/track/{trackId}` | Same as above | - |

### Deep Link Behavior
1. **Cold start**: Navigate directly to target, build synthetic back stack
2. **Warm start**: Navigate to target, preserve existing back stack where possible
3. **Fallback**: If entity not found → Search results or Home with snackbar

## Navigation Actions & Transitions

### Bottom Navigation Transitions
- **Tab switch**: Cross-fade (200ms), no back stack mixing
- **Same tab reselection**: Scroll to top (if list) or pop to root
- **State preservation**: Each tab maintains independent back stack

### Detail Screen Transitions
- **Shared element**: Album/playlist/artist art → detail header (300ms)
- **Standard**: Slide horizontal (250ms) + fade
- **Bottom sheet**: Slide up (250ms) + backdrop fade

### Now Playing Transitions
- **Expand**: Miniplayer → Full screen shared element (300ms)
- **Collapse**: Reverse with velocity tracking
- **Drag**: Interactive dismissal with spring physics

### Modal Transitions (Settings, Create Playlist)
- **Bottom sheet**: Slide up + backdrop (250ms)
- **Full screen**: Slide horizontal + fade (250ms)

## Back Stack Management

### Rules

1. **Bottom Nav Tabs**: Independent back stacks (5 max each)
2. **Detail Screens**: Push onto current tab's back stack
3. **Now Playing**: Not in back stack (overlay)
4. **Modals**: Not in back stack (dismiss on back)
5. **Deep Links**: Synthetic back stack to Home/Library root

### Synthetic Back Stacks for Deep Links

| Deep Link Target | Synthetic Back Stack |
|------------------|---------------------|
| `albumDetail` | `library` → `albums` → `albumDetail` |
| `artistDetail` | `library` → `artists` → `artistDetail` |
| `playlistDetail` | `library` → `playlists` → `playlistDetail` |
| `searchResults` | `search` → `searchResults` |
| `categoryDetail` | `search` → `browseCategories` → `categoryDetail` |

## Navigation State Persistence

### Saved State (Process Death / Recreation)

| State | Persisted | Restoration |
|-------|-----------|-------------|
| Current tab | Yes | Restore tab selection |
| Tab back stacks | Yes | Restore each tab's stack |
| Scroll positions | Yes (per destination) | Restore on revisit |
| Search query | Yes | Restore in search bar |
| Now Playing queue | Yes (MediaSession) | Auto-restore |
| Playback position | Yes (MediaSession) | Auto-resume |
| Selected playlist filter | Yes | Restore filter state |

### Implementation
- `rememberSaveable` for Compose state
- `NavController.saveState()` / `restoreState()`
- Media3 `MediaSession` for playback state
- Room/DataStore for user preferences

## Conditional Navigation

### Authentication Gates
- **Profile/Settings**: Require sign-in → Redirect to auth flow
- **Premium features**: Check tier → Show upgrade sheet
- **Offline downloads**: Require premium + storage permission

### Permission Gates
- **Local audio import**: `READ_MEDIA_AUDIO` → Request at import time
- **Notifications**: `POST_NOTIFICATIONS` → Request on first playback
- **Bluetooth**: `BLUETOOTH_CONNECT` → Request on device selection

### Empty State Navigation
- **Empty Library**: Show "Create Playlist" CTA → `playlistCreate`
- **No Search Results**: Show "Browse Categories" → `browseCategories`
- **Offline**: Show cached content banner → Retry action

## Navigation Analytics Events

| Event | Parameters |
|-------|------------|
| `screen_view` | `screen_name`, `previous_screen`, `tab` |
| `tab_select` | `tab_name`, `previous_tab` |
| `deep_link_open` | `target`, `source` (cold/warm) |
| `back_press` | `from_screen`, `to_screen` |
| `now_playing_open` | `entry_point` (miniplayer, track_row, notification) |
| `search_submit` | `query`, `results_count` |
| `playlist_create` | `source` (fab, empty_state, add_to) |

## Implementation: Navigation Graph (Compose)

```kotlin
// NavGraph.kt
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "home",
    onPlayTrack: (Track) -> Unit
) {
    NavHost(navController, startDestination) {
        // Bottom Navigation Graph (nested)
        navigation(
            startDestination = "home",
            route = "main"
        ) {
            // Home Tab
            composable("home") {
                HomeScreen(onNavigate = { route, args ->
                    navController.navigate("$route/$args")
                }, onPlayTrack = onPlayTrack)
            }
            composable(
                route = "recentlyPlayedDetail",
                arguments = listOf(navArgument("itemId") { type = NavType.StringType }, 
                                   navArgument("itemType") { type = NavType.StringType })
            ) { backStackEntry ->
                RecentlyPlayedDetailScreen(
                    itemId = backStackEntry.getString()!!,
                    itemType = backStackEntry.getString()!!
                )
            }
            
            // Search Tab
            composable("search") {
                SearchScreen(onNavigate = { route, args ->
                    navController.navigate("$route/$args")
                }, onPlayTrack = onPlayTrack)
            }
            composable(
                route = "searchResults",
                arguments = listOf(navArgument("query") { type = NavType.StringType },
                                   navArgument("filter") { type = NavType.StringType, nullable = true })
            ) { backStackEntry ->
                SearchResultsScreen(
                    query = backStackEntry.getString()!!,
                    filter = backStackEntry.getString("filter")
                )
            }
            
            // Library Tab
            composable("library") {
                LibraryScreen(onNavigate = { route, args ->
                    navController.navigate("$route/$args")
                }, onPlayTrack = onPlayTrack)
            }
            // ... playlist, album, artist detail routes
        }
        
        // Now Playing (Global Dialog/BottomSheet)
        composable("nowPlaying") {
            NowPlayingScreen()
        }
        
        // Settings (Modal)
        navigation(startDestination = "settings", route = "settings") {
            composable("settings") { SettingsScreen() }
            composable("account") { AccountScreen() }
            // ... other settings screens
        }
        
        // Deep Link Entry Points
        composable(
            route = "deepLink/track/{trackId}",
            deepLinks = listOf(navDeepLink { uriPattern = "musicapp://track/{trackId}" })
        ) { backStackEntry ->
            val trackId = backStackEntry.getString()!!
            onPlayTrack(TrackRepository.getById(trackId)!!)
        }
        // ... other deep links
    }
}
```

## Bottom Navigation Implementation

```kotlin
// BottomNavBar.kt
@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<BottomNavItem> = BottomNavItem.DefaultItems
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selected,
                onClick = { 
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    companion object {
        val DefaultItems = listOf(
            BottomNavItem("home", "Home", Icons.Default.Home),
            BottomNavItem("search", "Search", Icons.Default.Search),
            BottomNavItem("library", "Library", Icons.Default.LibraryMusic)
        )
    }
}
```

## Testing Navigation

### Unit Tests
- NavController navigation actions
- Deep link parsing
- Back stack operations
- State save/restore

### Integration Tests
- Tab switching preserves state
- Detail navigation and back
- Now playing overlay behavior
- Deep link cold/warm starts
- Permission gated flows

### E2E Test Scenarios
1. Cold start → Home → Play track → Now Playing → Back → Home
2. Search → Query → Result → Album → Play → Now Playing
3. Library → Playlists → Create → Add tracks → Play
4. Deep link → Album → Back → Library (correct tab)
5. Notification tap → Now Playing → Swipe down → Previous screen

## Summary

| Aspect | Count |
|--------|-------|
| Top-level Destinations | 3 (Bottom Nav) + 2 (Modal) + 1 (Overlay) |
| Detail Destinations | 8 (Playlist, Album, Artist, Track, Category, Search Results, Queue, Lyrics) |
| Settings/Profile Destinations | 9 |
| Deep Link Patterns | 6 |
| Synthetic Back Stacks | 5 |
| Navigation Transitions | 4 types |
| Conditional Gates | 3 (Auth, Premium, Permissions) |

This navigation map provides the complete blueprint for implementing navigation in the independent music application.