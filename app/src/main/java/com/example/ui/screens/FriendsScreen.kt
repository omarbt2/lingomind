package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.components.semaIconButton

data class Friend(
    val id: Int,
    val username: String,
    val xp: Int,
    val isOnline: Boolean,
    val isFollowing: Boolean,
    val isFollowedBy: Boolean
)

enum class FriendsTab(val label: String) {
    FOLLOWING("المتابَعون"),
    FOLLOWERS("المتابعون"),
    FIND("بحث")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    friends: List<Friend>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onInviteClick: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(FriendsTab.FOLLOWING) }

    val filteredFriends = remember(friends, selectedTab, searchQuery) {
        when (selectedTab) {
            FriendsTab.FOLLOWING -> friends.filter { it.isFollowing }
            FriendsTab.FOLLOWERS -> friends.filter { it.isFollowedBy }
            FriendsTab.FIND -> if (searchQuery.isBlank()) emptyList()
            else friends.filter { it.username.contains(searchQuery, ignoreCase = true) }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "الأصدقاء",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        semaIconButton(onClick = onBack) {
                            Icon(
                                imageVector = semaIcons.arrowBack(),
                                contentDescription = "رجوع",
                                tint = TextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
actions = {
                        semaIconButton(onClick = onInviteClick) {
                            Icon(
                                imageVector = semaIcons.personAdd(),
                                contentDescription = "دعوة صديق",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundDark
                    )
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tab row
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = BackgroundDark,
                    contentColor = PrimaryBlue,
                    indicator = { tabPositions ->
                        if (selectedTab.ordinal < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                                height = 3.dp,
                                color = PrimaryBlue
                            )
                        }
                    },
                    divider = {
                        HorizontalDivider(color = SurfaceVariant)
                    }
                ) {
                    FriendsTab.entries.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selectedTab == tab) PrimaryBlue else TextSecondary
                                )
                            }
                        )
                    }
                }

                // Search bar (visible on FIND tab)
                if (selectedTab == FriendsTab.FIND) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        placeholder = {
                            Text(
                                text = "ابحث عن صديق...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = semaIcons.search(),
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = TextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = SurfaceVariant,
                            focusedContainerColor = SurfaceVariant,
                            unfocusedContainerColor = SurfaceVariant,
                            cursorColor = PrimaryBlue
                        ),
                        shape = MaterialTheme.shapes.small
                    )
                }

                // Friends list
                if (filteredFriends.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            icon = semaIcons.people(),
                            title = when (selectedTab) {
                                FriendsTab.FOLLOWING -> "لا تتابع أحداً"
                                FriendsTab.FOLLOWERS -> "لا أحد يتابعك"
                                FriendsTab.FIND -> "ابحث عن أصدقاء"
                            },
                            message = when (selectedTab) {
                                FriendsTab.FIND -> "اكتب اسم صديق للبحث عنه"
                                else -> "ابدأ بمتابعة أصدقائك!"
                            }
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(filteredFriends) { index, friend ->
                            StaggeredEntrance(index = index) {
                                FriendRow(
                                    friend = friend,
                                    onToggleFollow = { /* Toggle follow */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendRow(
    friend: Friend,
    onToggleFollow: () -> Unit
) {
    semaCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online indicator
            Box {
                semaAvatar(
                    size = 44.dp,
                    backgroundColor = if (friend.isOnline) SuccessGreen.copy(alpha = 0.15f)
                    else SurfaceVariant
                )
                if (friend.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.username,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${friend.xp} XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (friend.isFollowing) {
                semaButton(
                    onClick = onToggleFollow,
                    backgroundColor = SurfaceVariant,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(
                        text = "متابَع",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            } else {
                semaButton(
                    onClick = onToggleFollow,
                    backgroundColor = PrimaryBlue,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(
                        text = "متابعة",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
