package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.semaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun NotificationSettingsScreen(
    viewModel: semaViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var dailyReminder by remember { mutableStateOf(true) }
    var streakWarning by remember { mutableStateOf(true) }
    var newLessons by remember { mutableStateOf(true) }
    var achievementAlerts by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var reminderHour by remember { mutableIntStateOf(20) }

    val hasNotificationPermission = if (Build.VERSION.SDK_INT >= 33) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else true

    var showTimePicker by remember { mutableStateOf(false) }

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
                text = "إعدادات الإشعارات",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Box(modifier = Modifier.size(48.dp))
        }

        HorizontalDivider(color = SurfaceDark, thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Permission status
            if (!hasNotificationPermission) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = XpYellow.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = semaIcons.bell(),
                            contentDescription = null,
                            tint = XpYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "صلاحية الإشعارات",
                                color = TextPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "يرجى تفعيل الإشعارات من إعدادات النظام",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Section: تذكيرات
            Text(
                text = "التذكيرات",
                color = TextSecondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            SettingToggle(
                title = "تذكير يومي",
                subtitle = "تذكير يومي $reminderHour:00 لإكمال درس",
                checked = dailyReminder,
                onCheckedChange = { dailyReminder = it },
                icon = semaIcons.bell()
            )

            SettingToggle(
                title = "تحذير انقطاع السلسلة",
                subtitle = "تنبيه إذا لم تكمل درساً اليوم",
                checked = streakWarning,
                onCheckedChange = { streakWarning = it },
                icon = semaIcons.localFireDepartment()
            )

            // Section: المحتوى
            Text(
                text = "المحتوى",
                color = TextSecondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            SettingToggle(
                title = "دروس جديدة",
                subtitle = "إشعار عند إضافة دروس جديدة",
                checked = newLessons,
                onCheckedChange = { newLessons = it },
                icon = semaIcons.graduationCap()
            )

            SettingToggle(
                title = "إنجازات",
                subtitle = "إشعار عند تحقيق إنجاز جديد",
                checked = achievementAlerts,
                onCheckedChange = { achievementAlerts = it },
                icon = semaIcons.emojiEvents()
            )

            // Section: الصوت
            Text(
                text = "الصوت",
                color = TextSecondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            SettingToggle(
                title = "صوت الإشعارات",
                subtitle = "تشغيل صوت مع الإشعارات",
                checked = soundEnabled,
                onCheckedChange = { soundEnabled = it },
                icon = semaIcons.bell()
            )

            Spacer(Modifier.weight(1f))

            // Save button
            semaButton(
                onClick = {
                    viewModel.updateUserSettings(
                        sound = soundEnabled,
                        anim = true,
                        notif = dailyReminder || streakWarning
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ الإعدادات", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = PrimaryBlue,
                    checkedThumbColor = TextPrimary,
                    uncheckedTrackColor = SurfaceVariant,
                    uncheckedThumbColor = TextSecondary
                )
            )
        }
    }
}