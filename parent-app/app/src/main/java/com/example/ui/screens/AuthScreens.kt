package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SentryShieldLogo
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Brand Splash Screen
 */
@Composable
fun SplashScreen(
    onGetStarted: () -> Unit,
    onLoginDirect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TrustBluePrimary)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SentryShieldLogo(size = 90.dp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SentryKids",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
            )
            Text(
                text = "Smart AI Parental Control",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = OnTrustBlueContainer,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onGetStarted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GrowthEmeraldContainer,
                    contentColor = OnGrowthEmeraldContainer
                ),
                shape = CircleShape,
                modifier = Modifier
                    .semantics { testTag = "splash_get_started_button" }
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onLoginDirect,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = CircleShape,
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.5f))),
                modifier = Modifier
                    .semantics { testTag = "splash_login_button" }
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "I already have an account",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

/**
 * Onboarding Flow
 */
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val slides = listOf(
        Triple("Intelligent AI Protection", "SentryKids continuous AI keeps your children safe from harmful content, cyberbullying, and unwanted contacts in real time.", Icons.Default.Shield),
        Triple("Geofence & Safe Zones", "Set up safe zones around home and school with instant alerts when your kids arrive or depart safely.", Icons.Default.LocationOn),
        Triple("Empower Healthy Habits", "Manage app limits, screen time, bedtime schedules, and lock devices with one tap.", Icons.Default.Schedule)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onFinishOnboarding,
                modifier = Modifier.semantics { testTag = "onboarding_skip_button" }
            ) {
                Text("Skip", style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary))
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val slide = slides[page]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(GrowthEmeraldContainer.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = slide.third,
                        contentDescription = null,
                        tint = TrustBluePrimary,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = slide.first,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = slide.second,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Page Indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(if (pagerState.currentPage == index) 24.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (pagerState.currentPage == index) TrustBluePrimary else MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        Button(
            onClick = {
                if (pagerState.currentPage < 2) {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinishOnboarding()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary),
            shape = CircleShape,
            modifier = Modifier
                .semantics { testTag = "onboarding_next_button" }
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == 2) "Get Started Now" else "Next",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

/**
 * Login Screen
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    var email by remember { mutableStateOf("parent@sentrykids.com") }
    var password by remember { mutableStateOf("••••••••") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SentryShieldLogo(size = 64.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "Sign in to monitor & manage SentryKids",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Parent Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .semantics { testTag = "login_email_input" }
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
                .semantics { testTag = "login_password_input" }
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {}) {
                Text("Forgot password?", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginSuccess,
            colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary),
            shape = CircleShape,
            modifier = Modifier
                .semantics { testTag = "login_submit_button" }
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Sign In", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have a parent account? ", style = MaterialTheme.typography.bodyMedium)
            TextButton(
                onClick = onNavigateRegister,
                modifier = Modifier.semantics { testTag = "login_register_link" }
            ) {
                Text("Register", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            }
        }
    }
}

/**
 * Register Screen
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("Sarah Thompson") }
    var email by remember { mutableStateOf("sarah@sentrykids.com") }
    var password by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SentryShieldLogo(size = 56.dp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Create Parent Account",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "Start protecting your family with AI in 2 minutes",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = agreeTerms,
                onCheckedChange = { agreeTerms = it }
            )
            Text(
                text = "I agree to SentryKids Terms of Service & Privacy Policy",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRegisterSuccess,
            enabled = agreeTerms,
            colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary),
            shape = CircleShape,
            modifier = Modifier
                .semantics { testTag = "register_submit_button" }
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Create Account", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already registered? ", style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onNavigateLogin) {
                Text("Sign In", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            }
        }
    }
}

/**
 * Owner Security Code Verification Screen
 * Additional verification step after login/signup
 */
@Composable
fun SecurityCodeVerificationScreen(
    onVerificationSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var securityCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SentryShieldLogo(size = 64.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Owner Security Verification",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter the permanent unique security code issued by your app owner or administrator to unlock access.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = securityCode,
            onValueChange = {
                securityCode = it.uppercase()
                if (errorMessage != null) errorMessage = null
            },
            label = { Text("Owner Security Code") },
            placeholder = { Text("e.g. SENTRY-8821") },
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
            singleLine = true,
            isError = errorMessage != null,
            modifier = Modifier
                .semantics { testTag = "security_code_input" }
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GuardianErrorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = GuardianError,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnGuardianErrorContainer,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // TODO: Replace prototype acceptance with backend owner-issued unique code verification before production.
                val cleaned = securityCode.trim()
                if (cleaned.isBlank()) {
                    errorMessage = "Security code is required. Please enter your owner security code."
                } else {
                    onVerificationSuccess()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary),
            shape = CircleShape,
            modifier = Modifier
                .semantics { testTag = "verify_security_code_button" }
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Verify & Continue", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.semantics { testTag = "back_to_login_button" }
        ) {
            Text("Return to Login", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}

