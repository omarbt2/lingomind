package com.example.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.SupabaseClient
import com.example.ui.components.semaButton
import com.example.ui.components.semaCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackToSignIn: () -> Unit,
    onResetEmailSent: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        Text(
            text = "sema",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black
        )
        Text(
            text = "إعادة تعيين كلمة المرور",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        }

        Spacer(modifier = Modifier.height(32.dp))

        semaCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "أدخل بريدك الإلكتروني وسنرسل لك رابطاً لإعادة تعيين كلمة المرور",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("البريد الإلكتروني", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = PrimaryBlue,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PrimaryBlue
                    ),
                    shape = MaterialTheme.shapes.medium
                )

                errorMessage?.let { msg ->
                    Text(text = msg, color = ErrorRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 4.dp)
                    )
                }

                successMessage?.let { msg ->
                    Text(text = msg, color = SuccessGreen, style = MaterialTheme.typography.bodySmall, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                semaButton(
                    onClick = {
                        if (email.isNotBlank()) {
                            isLoading = true
                            errorMessage = null
                            successMessage = null
                            SupabaseClient.resetPassword(email) { result ->
                                isLoading = false
                                if (result.isSuccess) {
                                    successMessage = "تم إرسال رابط إعادة التعيين إلى بريدك الإلكتروني"
                                } else {
                                    errorMessage = result.exceptionOrNull()?.message ?: "فشل الإرسال"
                                }
                            }
                        } else {
                            errorMessage = "يرجى إدخال البريد الإلكتروني"
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("أرسل رابط الاستعادة", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onBackToSignIn) {
            Text("العودة لتسجيل الدخول", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
        }
    }
}
