package com.musicapp.core.common.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavDestination
import androidx.navigation.compose.NavGraph
import androidx.navigation.compose.NavHostController
import com.musicapp.core.common.theme.MaterialTheme
import com.musicapp.core.common.theme.MusicColors
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<BottomNavItem> = BottomNavItem.DefaultItems,
    onPlayTrack: ((com.musicapp.core.model.Track, List<com.musicapp.core.model.Track>, Int) -> Unit)? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MusicColors.MiniplayerBackground,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
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
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    companion object {
        val DefaultItems = listOf(
            BottomNavItem("home", "Home", Home),
            BottomNavItem("search", "Search", Search),
            BottomNavItem("library", "Library", LibraryMusic)
        )
    }
}

@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    showProfile: Boolean = true,
    onProfileClick: (() -> Unit)? = null,
    actions: List<TopAppBarAction> = emptyList()
) {
    androidx.compose.material3.TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            com.musicapp.core.common.theme.ScreenTitle(text = title)
        },
        navigationIcon = { /* Back handled by nav controller */ },
        actions = {
            actions.forEach { action ->
                androidx.compose.material3.IconButton(
                    onClick = action.onClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription
                    )
                }
            }

            if (showProfile) {
                androidx.compose.material3.IconButton(
                    onClick = onProfileClick ?: {},
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

data class TopAppBarAction(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)

@Composable
fun SearchAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            androidx.compose.material3.SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = query,
                onValueChange = onQueryChange,
                placeholder = { Text("What do you want to listen to?") },
                leadingIcon = { Icon(androidx.compose.material.icons.filled.Search, contentDescription = "Search") },
                trailingIcons = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(androidx.compose.material.icons.filled.Close, contentDescription = "Clear")
                        }
                    }
                    onFilterClick?.let {
                        IconButton(onClick = it) {
                            Icon(androidx.compose.material.icons.filled.Tune, contentDescription = "Filter")
                        }
                    }
                },
                colors = androidx.compose.material3.SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    hintColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun CollapsingAppBar(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = nil,
    actions: List<TopAppBarAction> = emptyList(),
    imageLoader: coil.ImageLoader
) {
    androidx.compose.material3.TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            com.musicapp.core.common.theme.ScreenTitle(text = title)
        },
        navigationIcon = {
            onBackClick?.let {
                IconButton(onClick = it) {
                    Icon(androidx.compose.material.icons.filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            actions.forEach { action ->
                IconButton(
                    onClick = action.onClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription
                    )
                }
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}