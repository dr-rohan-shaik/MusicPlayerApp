package com.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navArgument
import androidx.navigation.NavType
import com.musicapp.core.model.Track
import com.musicapp.feature.home.ui.HomeScreen
import com.musicapp.feature.search.ui.SearchScreen
import com.musicapp.feature.library.ui.LibraryScreen
import com.musicapp.feature.playback.ui.NowPlayingScreen
import com.musicapp.feature.playback.ui.QueueScreen
import com.musicapp.feature.playback.ui.LyricsScreen
import com.musicapp.feature.playlist.ui.PlaylistDetailScreen
import com.musicapp.feature.playlist.ui.PlaylistCreateScreen
import com.musicapp.feature.playlist.ui.PlaylistEditScreen
import com.musicapp.feature.settings.ui.SettingsScreen
import com.musicapp.feature.profile.ui.ProfileScreen
import com.musicapp.feature.home.ui.RecentlyPlayedDetailScreen
import com.musicapp.feature.search.ui.SearchResultsScreen
import com.musicapp.feature.search.ui.BrowseCategoriesScreen
import com.musicapp.feature.search.ui.CategoryDetailScreen
import com.musicapp.feature.library.ui.PlaylistsScreen
import com.musicapp.feature.library.ui.AlbumsScreen
import com.musicapp.feature.library.ui.ArtistsScreen
import com.musicapp.feature.library.ui.LikedSongsScreen
import com.musicapp.feature.library.ui.RecentlyPlayedScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "home",
    onPlayTrack: (Track, List<Track>, Int) -> Unit
) {
    NavHost(navController, startDestination) {
        // Main bottom navigation graph
        navigation(
            startDestination = "home",
            route = "main"
        ) {
            // Home Tab
            composable("home") {
                HomeScreen(
                    onNavigate = { route, args -> navController.navigate("$route/$args") },
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) }
                )
            }
            composable(
                route = "recentlyPlayedDetail",
                arguments = listOf(navArgument("itemId") { type = NavType.StringType }, navArgument("itemType") { type = NavType.StringType })
            ) { backStackEntry ->
                RecentlyPlayedDetailScreen(
                    itemId = backStackEntry.getString()!!,
                    itemType = backStackEntry.getString()!!,
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) },
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }

            // Search Tab
            composable("search") {
                SearchScreen(
                    onNavigate = { route, args -> navController.navigate("$route/$args") },
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) }
                )
            }
            composable(
                route = "searchResults",
                arguments = listOf(
                    navArgument("query") { type = NavType.StringType },
                    navArgument("filter") { type = NavType.StringType, nullable = true }
                )
            ) { backStackEntry ->
                SearchResultsScreen(
                    query = backStackEntry.getString()!!,
                    filter = backStackEntry.getString("filter"),
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) },
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }
            composable("browseCategories") {
                BrowseCategoriesScreen(
                    onNavigate = { route, args -> navController.navigate("$route/$args") },
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) }
                )
            }
            composable(
                route = "categoryDetail/{categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) { backStackEntry ->
                CategoryDetailScreen(
                    categoryId = backStackEntry.getString()!!,
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) },
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }

            // Library Tab
            composable("library") {
                LibraryScreen(
                    onNavigate = { route, args -> navController.navigate("$route/$args") },
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) }
                )
            }
            composable("playlists") {
                PlaylistsScreen(
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) },
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }
            composable("albums") {
                AlbumsScreen(
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) },
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }
            composable("artists") {
                ArtistsScreen(
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) },
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }
            composable("likedSongs") {
                LikedSongsScreen(
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) },
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }
            composable("recentlyPlayed") {
                RecentlyPlayedScreen(
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) },
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }

            // Playlist Detail
            composable(
                route = "playlistDetail/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
            ) { backStackEntry ->
                PlaylistDetailScreen(
                    playlistId = backStackEntry.getString()!!,
                    onPlayTrack = { track, queue -> onPlayTrack(track, queue, 0) },
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }
            composable("playlistCreate") {
                PlaylistCreateScreen(
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }
            composable(
                route = "playlistEdit/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
            ) { backStackEntry ->
                PlaylistEditScreen(
                    playlistId = backStackEntry.getString()!!,
                    onNavigate = { route, args -> navController.navigate("$route/$args") }
                )
            }
        }

        // Now Playing (Global Dialog/BottomSheet)
        dialog("nowPlaying") {
            NowPlayingScreen(
                onDismiss = { navController.popBackStack() }
            )
        }
        dialog("queue") {
            QueueScreen(
                onDismiss = { navController.popBackStack() }
            )
        }
        dialog("lyrics") {
            LyricsScreen(
                onDismiss = { navController.popBackStack() }
            )
        }

        // Settings (Modal Navigation)
        navigation(startDestination = "settings", route = "settings") {
            composable("settings") { SettingsScreen() }
            composable("account") { com.musicapp.feature.settings.ui.AccountScreen() }
            composable("playback") { com.musicapp.feature.settings.ui.PlaybackSettingsScreen() }
            composable("notifications") { com.musicapp.feature.settings.ui.NotificationsScreen() }
            composable("storage") { com.musicapp.feature.settings.ui.StorageScreen() }
            composable("about") { com.musicapp.feature.settings.ui.AboutScreen() }
            composable("legal") { com.musicapp.feature.settings.ui.LegalScreen() }
        }

        // Profile (Modal Navigation)
        navigation(startDestination = "profile", route = "profile") {
            composable("profile") { ProfileScreen() }
            composable("editProfile") { com.musicapp.feature.profile.ui.EditProfileScreen() }
        }

        // Deep Link Entry Points
        composable(
            route = "deepLink/track/{trackId}",
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "musicapp://track/{trackId}" })
        ) { backStackEntry ->
            val trackId = backStackEntry.getString()!!
            // Handle deep link - would need to fetch track and play
        }
        composable(
            route = "deepLink/album/{albumId}",
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "musicapp://album/{albumId}" })
        ) { backStackEntry ->
            val albumId = backStackEntry.getString()!!
            navController.navigate("albumDetail/$albumId")
        }
        composable(
            route = "deepLink/artist/{artistId}",
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "musicapp://artist/{artistId}" })
        ) { backStackEntry ->
            val artistId = backStackEntry.getString()!!
            navController.navigate("artistDetail/$artistId")
        }
        composable(
            route = "deepLink/playlist/{playlistId}",
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "musicapp://playlist/{playlistId}" })
        ) { backStackEntry ->
            val playlistId = backStackEntry.getString()!!
            navController.navigate("playlistDetail/$playlistId")
        }
        composable(
            route = "deepLink/search/{query}",
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "musicapp://search?q={query}" })
        ) { backStackEntry ->
            val query = backStackEntry.getString()!!
            navController.navigate("searchResults/$query")
        }
    }
}