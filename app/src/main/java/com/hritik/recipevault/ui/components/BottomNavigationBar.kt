package com.hritik.recipevault.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hritik.recipevault.R

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onHomeClick: () -> Unit = {},
    onCollectionsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem(stringResource(id = R.string.nav_recipes), Icons.Outlined.RestaurantMenu, "home", onHomeClick),
            BottomNavItem(stringResource(id = R.string.nav_collections), Icons.Outlined.AutoAwesomeMotion, "collections", onCollectionsClick),
            BottomNavItem(stringResource(id = R.string.nav_profile), Icons.Outlined.Person, "profile", onProfileClick)
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = item.onClick,
                label = { Text(item.title, fontSize = 10.sp) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Color.Black else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFFF0EAE2)
                )
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val onClick: () -> Unit
)
