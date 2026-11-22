package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.BookmarkEntity
import com.example.data.LessonEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val lessons by viewModel.lessons.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBookmarks()
        viewModel.fetchLessons()
    }

    val bookmarkedLessons = remember(bookmarks, lessons) {
        val lessonMap = lessons.associateBy { it.id }
        bookmarks.mapNotNull { bookmark ->
            lessonMap[bookmark.lesson_id]
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "الدروس المحفوظة",
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundDark
                    )
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            if (bookmarkedLessons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = semaIcons.star(),
                        title = "لا توجد دروس محفوظة",
                        message = "اضغط على علامة النجمة بجانب أي درس لحفظه للرجوع إليه لاحقاً."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    items(bookmarkedLessons) { lesson ->
                        BookmarkItem(
                            lesson = lesson,
                            onRemoveBookmark = {
                                viewModel.toggleBookmark(lesson.id)
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun BookmarkItem(
    lesson: LessonEntity,
    onRemoveBookmark: () -> Unit
) {
    semaCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title_ar,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (lesson.hook_text != null) {
                    Text(
                        text = lesson.hook_text,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            semaIconButton(onClick = onRemoveBookmark) {
                Icon(
                    imageVector = semaIcons.star(),
                    contentDescription = "إزالة من المفضلة",
                    tint = XpYellow,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
