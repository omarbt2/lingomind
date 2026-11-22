package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.screens.auth.*
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.semaTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            semaTheme {
                var currentScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.SignIn) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    when (currentScreen) {
                        AuthScreen.SignIn -> SignInScreen(
                            onSignInSuccess = { 
                                currentScreen = AuthScreen.Onboarding 
                            },
                            onSwitchToSignUp = { currentScreen = AuthScreen.SignUp },
                            onSwitchToForgotPassword = { currentScreen = AuthScreen.ForgotPassword }
                        )
                        AuthScreen.SignUp -> SignUpScreen(
                            onSignUpSuccess = { 
                                currentScreen = AuthScreen.Onboarding 
                            },
                            onSwitchToSignIn = { currentScreen = AuthScreen.SignIn }
                        )
                        AuthScreen.ForgotPassword -> ForgotPasswordScreen(
                            onBackToSignIn = { currentScreen = AuthScreen.SignIn },
                            onResetEmailSent = { /* show success */ }
                        )
                        AuthScreen.Onboarding -> OnboardingScreen(
                            onComplete = { goToMain() },
                            onSkip = { goToMain() }
                        )
                    }
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

enum class AuthScreen {
    SignIn, SignUp, ForgotPassword, Onboarding
}
