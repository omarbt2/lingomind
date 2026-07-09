package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LingoMindViewModel
import com.example.ui.theme.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person

@Composable
fun MainTabScreen(
    viewModel: LingoMindViewModel,
    onStartLesson: (Int) -> Unit
) {
    var activeTab by remember { mutableStateOf(0) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 8.dp
                ) {
                    // Tab 1: Home
                    NavigationBarItem(
                        selected = (activeTab == 0),
                        onClick = { activeTab = 0 },
                        icon = { 
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "الرئيسية",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                "الرئيسية",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (activeTab == 0) RoyalCobalt else OnSurfaceVariantLight
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RoyalCobalt,
                            unselectedIconColor = OnSurfaceVariantLight,
                            indicatorColor = RoyalCobalt.copy(alpha = 0.15f)
                        )
                    )

                    // Tab 2: Leagues
                    NavigationBarItem(
                        selected = (activeTab == 1),
                        onClick = { activeTab = 1 },
                        icon = { 
                            Icon(
                                imageVector = Icons.Filled.Leaderboard,
                                contentDescription = "الصدارة",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                "الصدارة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (activeTab == 1) RoyalCobalt else OnSurfaceVariantLight
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RoyalCobalt,
                            unselectedIconColor = OnSurfaceVariantLight,
                            indicatorColor = RoyalCobalt.copy(alpha = 0.15f)
                        )
                    )

                    // Tab 3: Quests
                    NavigationBarItem(
                        selected = (activeTab == 2),
                        onClick = { activeTab = 2 },
                        icon = { 
                            Icon(
                                imageVector = Icons.Filled.FlashOn,
                                contentDescription = "المهام",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                "المهام",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (activeTab == 2) RoyalCobalt else OnSurfaceVariantLight
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RoyalCobalt,
                            unselectedIconColor = OnSurfaceVariantLight,
                            indicatorColor = RoyalCobalt.copy(alpha = 0.15f)
                        )
                    )

                    // Tab 4: Shop
                    NavigationBarItem(
                        selected = (activeTab == 3),
                        onClick = { activeTab = 3 },
                        icon = { 
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "المتجر",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                "المتجر",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (activeTab == 3) RoyalCobalt else OnSurfaceVariantLight
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RoyalCobalt,
                            unselectedIconColor = OnSurfaceVariantLight,
                            indicatorColor = RoyalCobalt.copy(alpha = 0.15f)
                        )
                    )

                    // Tab 5: Profile
                    NavigationBarItem(
                        selected = (activeTab == 4),
                        onClick = { activeTab = 4 },
                        icon = { 
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "الملف الشخصي",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                "الملف الشخصي",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (activeTab == 4) RoyalCobalt else OnSurfaceVariantLight
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RoyalCobalt,
                            unselectedIconColor = OnSurfaceVariantLight,
                            indicatorColor = RoyalCobalt.copy(alpha = 0.15f)
                        )
                    )
                }
            },
            containerColor = DarkBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    0 -> HomeScreen(viewModel = viewModel, onStartLesson = onStartLesson)
                    1 -> LeaguesScreen(viewModel = viewModel, onStartLesson = { onStartLesson(1) })
                    2 -> QuestsScreen(viewModel = viewModel, onAuthClick = { activeTab = 4 })
                    3 -> ShopScreen(viewModel = viewModel)
                    4 -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}
