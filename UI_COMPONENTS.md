# UI Components Reference: Spotify APK Analysis

## Overview
This document catalogs the UI components identified from the Spotify APK resource analysis, organized by screen and component type. Used as reference for building an independent implementation with original design.

## Component Inventory by Category

### 1. Navigation Components

#### Bottom Navigation Bar
- **Type**: `BottomNavigationView` / `NavigationBarView`
- **Items**: 3-5 destinations (Home, Search, Library, [Premium], [Profile])
- **States**: Selected, unselected, badge indicators
- **Animation**: Icon transitions, label fade
- **Material 3**: `NavigationBarView` with `NavigationBarItemView`

#### Top App Bars
- **Standard**: `MaterialToolbar` with title, actions
- **Collapsing**: `CollapsingToolbarLayout` with parallax image
- **Contextual**: Action mode for selection
- **Search**: `SearchView` integrated in toolbar

#### Navigation Drawer (Legacy/Tablet)
- **Type**: `NavigationView` in `DrawerLayout`
- **Sections**: Library, Playlists, Settings, Profile

### 2. Content Display Components

#### Card Variants
| Variant | Use Case | Layout |
|---------|----------|--------|
| **Album Card** | Grid of albums | Square image + title + artist |
| **Playlist Card** | Grid of playlists | Cover art (4-images collage) + title + owner |
| **Artist Card** | Grid of artists | Circular image + name + follower count |
| **Track Card** | List of tracks | Small art + title + artist + duration + menu |
| **Show/Episode Card** | Podcasts | Rectangular art + title + date |
| **Category Card** | Browse categories | Icon + label, rounded corners |

#### List Items
- **Track Row**: Leading art, title/artist, album, duration, trailing menu
- **Playlist Row**: Cover, title, description, track count
- **Artist Row**: Circular avatar, name, verification badge
- **Album Row**: Cover, title, artist, year, type badge
- **Section Header**: Title, "See all" action, optional filter chips

#### Grids
- **Album/Playlist Grid**: 2-column (phone), 3-5 column (tablet)
- **Artist Grid**: 2-3 column circular avatars
- **Category Grid**: 2-3 column icon cards
- **Adaptive**: `GridLayoutManager` with span size lookup

### 3. Home Screen Components

#### Hero Section
- **Featured Carousel**: Horizontal `ViewPager2` / `RecyclerView`
- **Content**: Promoted playlists, new releases, personalized
- **Indicators**: Page dots, auto-scroll with pause on touch

#### Section Components (Repeated Pattern)
```
Section Header (Title + Action)
├── Horizontal Scroll (RecyclerView, LinearLayoutManager.HORIZONTAL)
│   ├── Item 1 (Card)
│   ├── Item 2 (Card)
│   └── ...
└── "See All" → Navigate to full list
```

#### Section Types
1. **Recently Played** - Horizontal, circular indicators
2. **Made For You** - Personalized playlists
3. **Jump Back In** - Resume playback
4. **New Releases** - Albums from followed artists
5. **Recommended** - Algorithm-driven suggestions
6. **Popular Near You** - Location-based
7. **Genres/Moods** - Category chips navigation

#### Dynamic Feed
- **Type**: Single `RecyclerView` with multiple view types
- **Sections**: Header + content + divider
- **View Types**: Carousel, Grid, List, Banner, Promo

### 4. Search Screen Components

#### Search Bar
- **States**: Default, focused, with text, with suggestions
- **Features**: Voice input, recent searches, clear action
- **Animation**: Expand on focus, morph to results

#### Search Suggestions
- **Recent Searches**: User history with clear option
- **Trending**: Real-time popular searches
- **Categories**: Genre/mood chips
- **Artists/Albums/Tracks**: Type-ahead results

#### Search Results
- **Tabs/Sections**: Top Result, Songs, Artists, Albums, Playlists, Profiles, Podcasts
- **Filter Chips**: Horizontal scrollable filters
- **Empty State**: Illustration + message + action

#### Browse Categories (When No Query)
- **Grid**: Category cards with icons
- **Sections**: Music, Podcasts, Genres & Moods, Charts

### 5. Library Screen Components

#### Top Navigation
- **Tabs**: Playlists, Albums, Artists, Tracks, [Podcasts]
- **Sort/Filter**: Dropdown or chip group (recently added, alphabetical, creator)
- **View Toggle**: Grid/List switch

#### Content Areas
- **Playlists**: User created + followed, create button prominent
- **Albums**: Saved albums grid
- **Artists**: Followed artists grid
- **Liked Songs**: Special "playlist" with heart toggle
- **Recently Played**: Horizontal scroll of recent

#### Empty States
- **Illustration**: Custom empty state art
- **Message**: Contextual guidance
- **Action**: Primary button (Create playlist, Follow artists)

### 6. Now Playing Components

#### Full-Screen Player
- **Background**: Blurred album art with scrim
- **Artwork**: Centered, large, animated entrance
- **Metadata**: Track title, artist, album (navigable)
- **Progress**: Seek bar with current/total time
- **Primary Controls**: Shuffle, Previous, Play/Pause (large), Next, Repeat
- **Secondary**: Queue, Lyrics, Devices, Share, Like, More
- **Miniplayer**: Collapsed bottom sheet, drag to expand

#### Queue Screen
- **Now Playing**: Highlighted current track
- **Up Next**: Reorderable list (drag handle)
- **History**: Previously played (collapsible)
- **Actions**: Clear queue, save as playlist

#### Lyrics View
- **Sync**: Time-synced highlighting
- **Scroll**: Auto-scroll with manual override
- **Translation**: Toggle for translated lyrics

### 7. Playlist Components

#### Playlist Detail
- **Header**: Cover art (editable), title, description, owner, follower count
- **Actions**: Play, Shuffle, Follow, More (edit, share, delete)
- **Track List**: Numbered rows with drag reorder
- **Empty**: "Add songs" prompt

#### Playlist Creation/Edit
- **Cover**: Image picker (camera, gallery, search)
- **Title/Description**: Text fields with validation
- **Privacy**: Public/Private toggle
- **Collaborative**: Toggle for shared editing

#### Add to Playlist
- **Bottom Sheet**: Search + existing playlists + "New Playlist"
- **Multi-select**: Checkbox mode for bulk add

### 8. Artist Page Components

#### Header
- **Hero Image**: Full-width banner/background
- **Avatar**: Circular, overlapping hero
- **Verification**: Badge
- **Stats**: Monthly listeners, followers
- **Actions**: Follow, Share, More

#### Tabs
- **Popular**: Top tracks
- **Albums**: Studio albums, singles, compilations
- **Singles & EPs**: Separate section
- **Appears On**: Features, compilations
- **About**: Bio, related artists

#### Related Artists
- **Horizontal Scroll**: Circular avatar cards

### 9. Album Page Components

#### Header
- **Cover Art**: Square, large
- **Metadata**: Title, artist (navigable), year, track count, duration
- **Type Badge**: Album / Single / EP / Compilation
- **Actions**: Play, Shuffle, Save, Share, More

#### Track List
- **Numbered Rows**: Track number, title, duration, explicit badge
- **Play from Here**: Context menu option

### 10. Common Interactive Components

#### Buttons
| Type | Style | Use Case |
|------|-------|----------|
| **Primary** | Filled, rounded | Play, Save, Follow |
| **Secondary** | Outlined, rounded | Shuffle, More |
| **Tertiary** | Text only | See all, Cancel |
| **Icon Button** | Circular ripple | Menu, Like, Share |
| **FAB** | Extended/Standard | Create playlist |

#### Chips
- **Filter Chip**: Single selection, toggle
- **Choice Chip**: Radio group behavior
- **Action Chip**: One-tap action (Play, Shuffle)
- **Suggestion Chip**: Search suggestions

#### Dialogs/Bottom Sheets
- **Bottom Sheet**: Primary for actions (Add to playlist, Share, Queue)
- **Full-Screen Dialog**: Settings, Edit playlist
- **Confirmation**: Delete, Remove, Sign out
- **Picker**: Date, Time, Image source

#### Menus
- **Context Menu**: Long-press track → options
- **Overflow Menu**: Toolbar three-dot
- **Popup Menu**: Inline actions

### 11. Media Control Components

#### Notification (Background Playback)
- **Compact**: Art, title, artist, play/pause, next, dismiss
- **Expanded**: + progress bar, previous, shuffle/repeat
- **Actions**: PendingIntents for MediaController

#### Lock Screen
- **MediaSession**: Integrated with system UI
- **Artwork**: Full bleed on supported devices

#### Wear OS / Auto
- **Simplified**: Large touch targets, essential controls only

### 12. State & Feedback Components

#### Loading States
- **Skeleton/Shimmer**: Placeholder shapes matching content
- **Progress Bar**: Circular (indeterminate), Linear (determinate)
- **Pull-to-Refresh**: SwipeRefreshLayout on lists

#### Empty States
- **Illustration**: Custom empty state graphics
- **Message**: Descriptive text
- **Action**: Primary CTA button

#### Error States
- **Inline**: Snackbar, Banner
- **Full Screen**: Retry button, error illustration
- **Network**: Offline indicator, cached content notice

#### Success/Confirmation
- **Snackbar**: "Added to playlist", "Downloaded"
- **Toast**: Brief confirmations

### 13. Typography Scale (Material 3)

| Style | Size | Weight | Use Case |
|-------|------|--------|----------|
| Display Large | 57sp | Regular | Hero headlines |
| Display Medium | 45sp | Regular | Screen titles |
| Display Small | 36sp | Regular | Section headers |
| Headline Large | 32sp | Regular | Card titles |
| Headline Medium | 28sp | Regular | Album/playlist titles |
| Headline Small | 24sp | Regular | Artist names |
| Title Large | 22sp | Medium | Button text |
| Title Medium | 16sp | Medium | List items primary |
| Title Small | 14sp | Medium | Metadata, chips |
| Body Large | 16sp | Regular | Descriptions |
| Body Medium | 14sp | Regular | Standard text |
| Body Small | 12sp | Regular | Captions, timestamps |
| Label Large | 14sp | Medium | Button labels |
| Label Medium | 12sp | Medium | Chip labels |
| Label Small | 11sp | Medium | Overline, badges |

### 14. Color System (Dark Theme Primary)

#### Core Colors
| Role | Description |
|------|-------------|
| `primary` | Brand green (#1DB954) - primary actions |
| `onPrimary` | White - text on primary |
| `primaryContainer` | Muted green - hover/pressed |
| `onPrimaryContainer` | Dark green - text on container |
| `secondary` | Accent color for variety |
| `surface` | Dark background (#121212) |
| `onSurface` | White - primary text |
| `surfaceVariant` | Elevated surfaces (#1E1E1E) |
| `onSurfaceVariant` | Muted text |
| `outline` | Borders, dividers |
| `error` | Red for destructive actions |
| `background` | App background (#000000 / #121212) |

#### Semantic Colors
- **Play Button**: `primary` container with `onPrimary`
- **Liked/Heart**: Red accent (#E22134)
- **Explicit Badge**: `outline` container with `onSurfaceVariant`
- **Offline**: `surfaceVariant` with `onSurfaceVariant`

### 15. Iconography

#### System Icons (Material)
- Navigation: home, search, library, settings
- Playback: play, pause, skip-next, skip-previous, shuffle, repeat
- Actions: favorite, share, download, more-vert, add
- Media: music-note, album, person, playlist-play

#### Custom Icons
- Spotify logo (not replicated)
- Podcast, video, live, explicit badges
- Connect/devices icon
- Lyrics, queue, sleep timer

#### Icon Sizes
- **Small**: 16dp (chips, inline)
- **Medium**: 24dp (standard toolbar, list items)
- **Large**: 32dp (FAB, primary actions)
- **XL**: 48-56dp (empty states, hero)

### 16. Spacing & Elevation

#### Spacing Scale (4dp base)
| Token | Value | Use Case |
|-------|-------|----------|
| `space-1` | 4dp | Icon gaps, chip padding |
| `space-2` | 8dp | Standard padding, item spacing |
| `space-3` | 12dp | Card internal padding |
| `space-4` | 16dp | Screen margins, section gaps |
| `space-5` | 20dp | Large gaps |
| `space-6` | 24dp | Section headers |
| `space-8` | 32dp | Major sections |
| `space-10` | 40dp | Hero sections |
| `space-12` | 48dp | Full-screen padding |

#### Elevation
| Level | DP | Use Case |
|-------|-----|----------|
| 0 | 0dp | Background, flat cards |
| 1 | 1dp | Cards, sheets (resting) |
| 2 | 3dp | Raised cards, FAB (resting) |
| 3 | 6dp | Bottom sheets, dialogs |
| 4 | 8dp | Modal bottom sheets |
| 5 | 12dp | Navigation drawer, menus |

### 17. Motion & Animation

#### Durations
| Type | Duration | Easing |
|------|----------|--------|
| Fast | 150ms | Button press, ripple |
| Medium | 250ms | Screen transitions, expand/collapse |
| Slow | 350ms | Hero animations, shared element |
| Extra Slow | 500ms | Full-screen transitions |

#### Key Animations
- **Play/Pause Morph**: AnimatedVectorDrawable (250ms)
- **Miniplayer Expand**: Shared element + scale (300ms)
- **Card Press**: Scale 0.95 + elevation (100ms)
- **Bottom Sheet**: Slide up + fade (250ms)
- **Tab Switch**: Cross-fade + indicator slide (200ms)
- **Shimmer Loading**: Continuous sweep (1.5s loop)

### 18. Accessibility

#### Content Descriptions
- All icon buttons: descriptive labels
- Images: alt text for album art, artist photos
- Interactive elements: role + state + action

#### Touch Targets
- Minimum: 48x48dp
- Preferred: 56x56dp for primary actions

#### Color Contrast
- Text on surface: 7:1 (WCAG AAA)
- Non-text elements: 3:1
- Interactive states: visible focus indicators

#### Screen Reader
- Semantic heading structure
- Live regions for dynamic content (queue updates)
- Proper landmark roles (navigation, main, complementary)

## Implementation Notes for Independent App

### Component Library Structure
```
ui/
├── component/
│   ├── card/           # AlbumCard, PlaylistCard, TrackCard, ArtistCard
│   ├── list/           # TrackRow, SectionHeader, Divider
│   ├── navigation/     # BottomNav, TopAppBar, SearchBar
│   ├── playback/       # PlayerControls, ProgressBar, Miniplayer, Queue
│   ├── dialog/         # BottomSheets, ConfirmDialog, Picker
│   ├── button/         # Primary, Secondary, Icon, FAB, Chip
│   ├── state/          # Loading, Empty, Error, Skeleton
│   └── typography/     # Text styles, TextAppearance
├── theme/
│   ├── Color.kt        # Dark theme colors
│   ├── Type.kt         # Typography scale
│   ├── Shape.kt        # Corner radii
│   └── Motion.kt       # Animation specs
└── screen/
    ├── home/
    ├── search/
    ├── library/
    ├── nowplaying/
    ├── playlist/
    └── artist/
```

### Compose vs XML Decision
**Recommendation**: Jetpack Compose for all new development
- Matches Spotify's modern stack (layout-v33 indicates Compose)
- Better theming, animation, state management
- Easier component composition and reuse
- Future-proof for Android development

### Design Token Approach
- Define all tokens in `theme/` package
- Use `@Composable` functions for component variants
- Centralized theming via `MaterialTheme`
- Support dynamic color (Material You) as enhancement

## Summary Statistics

| Component Category | Count (Estimated) | Priority |
|--------------------|-------------------|----------|
| Navigation | 5 | P0 - Core |
| Cards | 8 variants | P0 - Core |
| Lists/Rows | 6 variants | P0 - Core |
| Grids | 4 layouts | P0 - Core |
| Home Sections | 7 types | P0 - Core |
| Search Components | 6 | P0 - Core |
| Library Sections | 5 | P0 - Core |
| Now Playing | 4 screens | P0 - Core |
| Playlist Management | 5 | P1 - High |
| Artist/Album Pages | 4 | P1 - High |
| Media Controls | 4 | P0 - Core |
| State/Feedback | 6 | P1 - High |
| Dialogs/Sheets | 5 | P1 - High |

**Total Unique Components**: ~65
**Estimated Implementation Effort**: 3 weeks (Phase 3)