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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.GemTransactionEntity
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GemHistoryScreen(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val gemTransactions by viewModel.gemTransactions.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchGemTransactions()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "سجل الجواهر",
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
            if (gemTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = semaIcons.diamond(),
                        title = "لا توجد معاملات",
                        message = "سجل الجواهر سيكون متاحاً هنا عندما تبدأ في كسب أو إنفاق الجواهر."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    items(gemTransactions) { transaction ->
                        GemTransactionItem(transaction = transaction)
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun GemTransactionItem(
    transaction: GemTransactionEntity
) {
    val isEarning = transaction.amount > 0
    val amountText = if (isEarning) "+${transaction.amount}" else "${transaction.amount}"
    val amountColor = if (isEarning) SuccessGreen else ErrorRed
    val reasonText = when (transaction.reason) {
        "exercise_correct" -> "إجابة صحيحة في الدرس"
        "lesson_complete" -> "إكمال الدرس"
        "hearts_recharge" -> "تعبئة القلوب"
        "streak_freeze" -> "شراء مجمد السلسلة"
        else -> transaction.reason
    }

    val formattedDate = try {
        val instant = java.time.Instant.parse(transaction.created_at)
        val localDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        val today = java.time.LocalDate.now()
        when {
            localDate == today -> "اليوم"
            localDate == today.minusDays(1) -> "أمس"
            else -> localDate.toString()
        }
    } catch (e: Exception) {
        transaction.created_at.take(10)
    }

    semaCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isEarning) SuccessGreen.copy(alpha = 0.15f)
                        else ErrorRed.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = semaIcons.diamond(),
                    contentDescription = null,
                    tint = if (isEarning) SuccessGreen else ErrorRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reasonText,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Text(
                text = amountText,
                style = MaterialTheme.typography.titleMedium,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
