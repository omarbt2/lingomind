package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                semaIconButton(onClick = onBack) {
                    Icon(semaIcons.arrowBack(), contentDescription = "رجوع", tint = TextPrimary)
                }
                Text(
                    text = "عن Sema",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Box(modifier = Modifier.size(48.dp))
            }

            HorizontalDivider(color = SurfaceDark, thickness = 1.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // App icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(PrimaryBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = semaIcons.graduationCap(),
                        contentDescription = "Sema",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Text(
                    text = "Sema",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "الإصدار 1.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                HorizontalDivider(
                    modifier = Modifier.width(60.dp),
                    color = PrimaryBlue.copy(alpha = 0.3f),
                    thickness = 2.dp
                )

                // Brand story
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "لماذا Sema؟",
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )

                        val paragraphs = listOf(
                            "اللغة ليست مجرد كلمات نصفها في كتاب، بل هي طريقتك في ترك أثرك في هذا العالم.",
                            "حين بحثنا عن اسم لهذا التطبيق، رفضنا كل الأسماء المستهلكة والتقليدية التي تذكرك بمقاعد الدراسة والدروس المملة. كنا نبحث عن كلمة حية، تنبض بالثقة، وتعبر عن غايتك الحقيقية: أن تتحدث وتُسمع.",
                            "من هنا ولد اسم Sema. في جذور اللغة السواحيلية، Sema تعني ببساطة: أن تتكلم... أن تقول ما تعنيه. وفي عمق الفلسفة اليونانية القديمة، هي أصل كلمة Semantics التي تعني صناعة المعنى والفهم.",
                            "لقد اخترنا هذا الاسم ليكون عهداً بيننا وبينك: ألا نبقيك صامتاً تترجم الحروف في رأسك، بل نأخذ بيديك لتعبر عن أفكارك بطلاقة وثقة، وتجعل صوتك مسموعاً بلا حدود ولا تردد."
                        )

                        paragraphs.forEach { paragraph ->
                            Text(
                                text = paragraph,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary,
                                lineHeight = 28.sp,
                                textAlign = TextAlign.Start
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "مع Sema... قل ما تعنيه، وكن أنت المعنى.",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}