package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    initialUsername: String,
    initialEmail: String,
    onSave: (username: String, email: String) -> Unit,
    onBack: () -> Unit
) {
    var username by remember { mutableStateOf(initialUsername) }
    var email by remember { mutableStateOf(initialEmail) }
    var hasChanges by remember { mutableStateOf(false) }

    LaunchedEffect(username, email) {
        hasChanges = username != initialUsername || email != initialEmail
    }

    var contentLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); contentLoaded = true }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "تعديل الملف الشخصي",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = semaIcons.arrowBack(),
                                contentDescription = "رجوع",
                                tint = TextPrimary
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
            FadeInContent(visible = contentLoaded) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Avatar section
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    semaAvatar(size = 96.dp)
                }

                // Form fields
                semaCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Username field
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "اسم المستخدم",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                modifier = Modifier.fillMaxWidth(),
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

                        // Email field
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "البريد الإلكتروني",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.fillMaxWidth(),
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
                    }
                }

                // Save button
                semaButton(
                    onClick = { onSave(username, email) },
                    enabled = hasChanges,
                    backgroundColor = if (hasChanges) PrimaryBlue else SurfaceVariant
                ) {
                    Text(
                        text = "حفظ التغييرات",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
            }
        }
    }
}
