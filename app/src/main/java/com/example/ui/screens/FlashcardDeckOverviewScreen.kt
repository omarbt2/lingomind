package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.theme.*

data class FlashcardDeck(
    val id: Int,
    val name: String,
    val totalCards: Int,
    val masteredCards: Int,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardDeckOverviewScreen(
    decks: List<FlashcardDeck>,
    onDeckClick: (FlashcardDeck) -> Unit,
    onBack: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "أرصدة البطاقات",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        semaIconButton(
                            onClick = onBack,
                            content = {
                                Icon(
                                    imageVector = semaIcons.arrowBack(),
                                    contentDescription = "رجوع",
                                    tint = TextPrimary
                                )
                            }
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BackgroundDark
                    )
                )
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            if (decks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = semaIcons.style(),
                        title = "لا توجد أرصدة",
                        message = "لم يتم إنشاء أي أرصدة بطاقات بعد"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    itemsIndexed(decks) { index, deck ->
                        StaggeredEntrance(index = index) {
                            DeckCard(
                                deck = deck,
                                onClick = { onDeckClick(deck) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckCard(
    deck: FlashcardDeck,
    onClick: () -> Unit
) {
    val masteryPercent = if (deck.totalCards > 0) {
        deck.masteredCards.toFloat() / deck.totalCards
    } else 0f

    val masteryColor = when {
        masteryPercent >= 0.8f -> SuccessGreen
        masteryPercent >= 0.5f -> XpYellow
        else -> PrimaryBlue
    }

    semaCard(
        modifier = Modifier.fillMaxWidth(),
        highlighted = masteryPercent >= 0.8f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deck.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = deck.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "${deck.totalCards}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = masteryColor
                )
            }

            semaProgressBar(
                progress = masteryPercent,
                color = masteryColor,
                label = "${deck.masteredCards} / ${deck.totalCards} مُتقَن"
            )

            semaButton(
                onClick = onClick,
                backgroundColor = PrimaryBlue
            ) {
                Text(
                    text = "ابدأ المراجعة",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
