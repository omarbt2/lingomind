package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

private val RadiusPill = RoundedCornerShape(percent = 50)
private val RadiusItem = RoundedCornerShape(16.dp)

data class semaNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

@Composable
fun semaBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RadiusPill,
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(RadiusPill)
                .background(SurfaceDark.copy(alpha = 0.85f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RadiusPill
                )
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                semaNavItem("الرئيسية", semaIcons.home(), semaIcons.home()),
                semaNavItem("الصدارة", semaIcons.emojiEvents(), semaIcons.emojiEvents()),
                semaNavItem("المهام", semaIcons.flashOn(), semaIcons.flashOn()),
                semaNavItem("المتجر", semaIcons.shoppingBag(), semaIcons.shoppingBag()),
                semaNavItem("حسابي", semaIcons.person(), semaIcons.person())
            )
            navItems.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index

                // Shared animations across all tabs
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.12f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "scale"
                )
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) PrimaryBlue else TextSecondary,
                    animationSpec = tween(200),
                    label = "iconColor"
                )
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else Color.Transparent,
                    animationSpec = tween(200),
                    label = "bgColor"
                )

                Box(
                    modifier = Modifier
                        .clip(RadiusItem)
                        .background(bgColor)
                        .clickable(
                            onClick = {
                                HapticHelper.performHapticClick(context)
                                onItemSelected(index)
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                        .size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                    )
                }
            }
        }
    }
}
