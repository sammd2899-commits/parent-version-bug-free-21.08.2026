package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mock.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Redesigned MY PAGE - Parent Account, Device Management, Family, Subscription, Settings, Support & About
 */
@Composable
fun MyProfileScreen(
    onNavigateToDevices: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onNavigateToPermissions: () -> Unit = {},
    onNavigateToHelpCenter: () -> Unit = {},
    onNavigateToDeveloperDiagnostics: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val children by MockChildrenRepository.children.collectAsState()
    val selectedChildId by MockChildrenRepository.selectedChildId.collectAsState()

    // Dialog & Action States
    var showAddDeviceDialog by remember { mutableStateOf(false) }
    var deviceToRename by remember { mutableStateOf<ChildItem?>(null) }
    var deviceToTransfer by remember { mutableStateOf<ChildItem?>(null) }
    var pairingType by remember { mutableStateOf<String?>(null) } // "QR" or "Email"
    var showInviteParentDialog by remember { mutableStateOf(false) }
    var showEmergencyContactDialog by remember { mutableStateOf(false) }
    var showCouponDialog by remember { mutableStateOf(false) }

    // Dialog Inputs
    var newDeviceNameInput by remember { mutableStateOf("") }
    var newChildNameInput by remember { mutableStateOf("") }
    var newDeviceTypeInput by remember { mutableStateOf("Android") }
    var renameInput by remember { mutableStateOf("") }
    var couponInput by remember { mutableStateOf("") }
    var parentEmailInput by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 1. PROFILE CARD
            // ==========================================
            BentoCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(TrustBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ST",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sarah Thompson",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "parent@sentrykids.com",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        StatusChip(text = "Pro Family Plan • Active", type = "safe")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // 2. CHILD DEVICES SECTION
            // ==========================================
            SectionHeader(title = "Devices")

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard {
                ProfileMenuTile(
                    title = "Child Devices",
                    subtitle = "Manage paired child devices, active device & pairing",
                    icon = Icons.Default.Smartphone,
                    onClick = onNavigateToDevices
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // 3. SUBSCRIPTION
            // ==========================================
            SectionHeader(title = "Subscription & Billing")

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard {
                ProfileMenuTile(
                    title = "Current Plan: Pro Family ($9.99/mo)",
                    subtitle = "Up to 5 devices • Next billing Sep 1, 2026",
                    icon = Icons.Default.Star,
                    onClick = onNavigateToSubscription
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Upgrade or Change Plan",
                    subtitle = "Explore Unlimited Family & School licenses",
                    icon = Icons.Default.Upgrade,
                    onClick = onNavigateToSubscription
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Payment History & Receipts",
                    subtitle = "View past monthly invoices & VAT receipts",
                    icon = Icons.Default.ReceiptLong,
                    onClick = onNavigateToSubscription
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Redeem Coupon / Gift Code",
                    subtitle = "Apply promotional discount codes",
                    icon = Icons.Default.CardGiftcard,
                    onClick = { showCouponDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // 4. FAMILY MANAGEMENT
            // ==========================================
            SectionHeader(title = "Family Members & Contacts")

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard {
                ProfileMenuTile(
                    title = "Family Members (3)",
                    subtitle = "Sarah (Admin), Mark (Co-Parent), 3 Children",
                    icon = Icons.Default.People,
                    onClick = { }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Invite Parent or Guardian",
                    subtitle = "Grant co-parenting dashboard permissions",
                    icon = Icons.Default.PersonAdd,
                    onClick = { showInviteParentDialog = true }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Emergency Contacts",
                    subtitle = "Manage trusted contacts for SOS alerts",
                    icon = Icons.Default.ContactPhone,
                    onClick = { showEmergencyContactDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // 5. SETTINGS
            // ==========================================
            SectionHeader(title = "Settings & Security")

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard {
                ProfileMenuTile(
                    title = "Notifications Preferences",
                    subtitle = "Configure push, SMS & emergency sound alerts",
                    icon = Icons.Default.Notifications,
                    onClick = { }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Privacy & Data Protection",
                    subtitle = "Location tracking consent & screenshot masking",
                    icon = Icons.Default.Security,
                    onClick = { }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "AI Detection Sensitivity",
                    subtitle = "Adjust strictness for keyword & social chat alerts",
                    icon = Icons.Default.Tune,
                    onClick = { }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Theme & Appearance",
                    subtitle = "System Default / Clean Utility Light Theme",
                    icon = Icons.Default.Palette,
                    onClick = { }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Language",
                    subtitle = "English (US)",
                    icon = Icons.Default.Language,
                    onClick = { }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Data Backup & Export",
                    subtitle = "Export PDF activity logs & cloud backups",
                    icon = Icons.Default.CloudDownload,
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // 6. SUPPORT
            // ==========================================
            SectionHeader(title = "Support & Help")

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard {
                ProfileMenuTile(
                    title = "Help Center & Setup Guides",
                    subtitle = "Step-by-step pairing and permission walkthroughs",
                    icon = Icons.Default.HelpOutline,
                    onClick = onNavigateToHelpCenter
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Send Feedback",
                    subtitle = "Report bugs or suggest new parental control features",
                    icon = Icons.Default.Feedback,
                    onClick = { }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Live Chat Support",
                    subtitle = "Chat with SentryKids support specialists 24/7",
                    icon = Icons.Default.HeadsetMic,
                    onClick = { }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Frequently Asked Questions",
                    subtitle = "Answers regarding stealth mode, battery & VPNs",
                    icon = Icons.Default.QuestionAnswer,
                    onClick = onNavigateToHelpCenter
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // 7. ABOUT
            // ==========================================
            SectionHeader(title = "About")

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard {
                ProfileMenuTile(
                    title = "SentryKids Version 2.4.0",
                    subtitle = "Build 2026.08 • Latest security patches installed",
                    icon = Icons.Default.Info,
                    onClick = onNavigateToAbout
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Privacy Policy",
                    subtitle = "COPPA & GDPR compliant child privacy disclosure",
                    icon = Icons.Default.Policy,
                    onClick = onNavigateToAbout
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Terms of Service",
                    subtitle = "User agreement & licensing terms",
                    icon = Icons.Default.Gavel,
                    onClick = onNavigateToAbout
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ProfileMenuTile(
                    title = "Open Source Licenses",
                    subtitle = "Jetpack Compose, Kotlin & Android dependencies",
                    icon = Icons.Default.Code,
                    onClick = onNavigateToAbout
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .semantics { testTag = "profile_logout_button" }
                    .fillMaxWidth()
                    .height(52.dp),
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GuardianError)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        // ==========================================
        // MODALS & DIALOGS
        // ==========================================

        // ADD DEVICE DIALOG
        if (showAddDeviceDialog) {
            AlertDialog(
                onDismissRequest = { showAddDeviceDialog = false },
                title = { Text("Add Child Device", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newChildNameInput,
                            onValueChange = { newChildNameInput = it },
                            label = { Text("Child Name") },
                            placeholder = { Text("e.g. Lucas") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDeviceNameInput,
                            onValueChange = { newDeviceNameInput = it },
                            label = { Text("Device Name") },
                            placeholder = { Text("e.g. Lucas's Tablet") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Android", "iPhone", "iPad").forEach { type ->
                                FilterChip(
                                    selected = newDeviceTypeInput == type,
                                    onClick = { newDeviceTypeInput = type },
                                    label = { Text(type) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newChildNameInput.isNotBlank() && newDeviceNameInput.isNotBlank()) {
                                MockChildrenRepository.addDevice(newDeviceNameInput, newChildNameInput, newDeviceTypeInput)
                                newChildNameInput = ""
                                newDeviceNameInput = ""
                                showAddDeviceDialog = false
                            }
                        }
                    ) {
                        Text("Add Device")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDeviceDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // RENAME DEVICE DIALOG
        if (deviceToRename != null) {
            val dev = deviceToRename!!
            AlertDialog(
                onDismissRequest = { deviceToRename = null },
                title = { Text("Rename ${dev.name}'s Device", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = renameInput,
                        onValueChange = { renameInput = it },
                        label = { Text("Device Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (renameInput.isNotBlank()) {
                                MockChildrenRepository.renameDevice(dev.id, renameInput)
                                deviceToRename = null
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deviceToRename = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // PAIRING DIALOG (QR / Email)
        if (pairingType != null) {
            AlertDialog(
                onDismissRequest = { pairingType = null },
                title = { Text(if (pairingType == "QR") "Scan QR Code to Pair" else "Email Companion Link", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (pairingType == "QR") {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(TrustBlueContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = "QR Code", modifier = Modifier.size(120.dp), tint = TrustBluePrimary)
                            }
                            Text("Open SentryKids Companion on child's phone and scan this code.", style = MaterialTheme.typography.bodySmall)
                        } else {
                            OutlinedTextField(
                                value = parentEmailInput,
                                onValueChange = { parentEmailInput = it },
                                label = { Text("Child or Co-Parent Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text("We will send an instant installation link with automatic pairing credentials.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { pairingType = null }) {
                        Text("Done")
                    }
                }
            )
        }

        // REDEEM COUPON DIALOG
        if (showCouponDialog) {
            AlertDialog(
                onDismissRequest = { showCouponDialog = false },
                title = { Text("Redeem Coupon Code", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = couponInput,
                        onValueChange = { couponInput = it },
                        label = { Text("Coupon or Promo Code") },
                        placeholder = { Text("e.g. SENTRY2026") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCouponDialog = false
                            couponInput = ""
                        }
                    ) {
                        Text("Apply Code")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCouponDialog = false }) { Text("Cancel") }
                }
            )
        }

        // INVITE PARENT DIALOG
        if (showInviteParentDialog) {
            AlertDialog(
                onDismissRequest = { showInviteParentDialog = false },
                title = { Text("Invite Co-Parent", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = parentEmailInput,
                        onValueChange = { parentEmailInput = it },
                        label = { Text("Co-Parent Email Address") },
                        placeholder = { Text("coparent@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = { showInviteParentDialog = false }) {
                        Text("Send Invite")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showInviteParentDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun ProfileMenuTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TrustBlueContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TrustBluePrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * Secondary Screens
 */
/**
 * Keyword Detection & Content Filtering Screen
 */
@Composable
fun DeviceFilteringScreen(onBack: () -> Unit) {
    val monitoredApps by MockKeywordsRepository.monitoredApps.collectAsState()
    val keywordAlerts by MockKeywordsRepository.keywordAlerts.collectAsState()

    var customKeywordInput by remember { mutableStateOf("") }
    var showAddKeywordDialog by remember { mutableStateOf(false) }
    var customKeywordList by remember { mutableStateOf(mutableStateListOf("abandoned lot", "Don't tell anyone", "secret party", "cheat code bypass", "hate school")) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                Column {
                    Text("Keyword Detection & Web Filter", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text("Real-time AI NLP Scanner Active", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // 1. RISK SCORE & KEYWORDS TODAY GAUGE CARD
            BentoCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Sentiment Risk Score", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Evaluates chat conversations & browser queries", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusChip(text = "Moderate (58/100)", type = "warning")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("5", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GuardianError))
                            Text("Keywords Today", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("8", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TrustBluePrimary))
                            Text("Monitored Apps", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Divider(modifier = Modifier.height(30.dp).width(1.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${customKeywordList.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GrowthEmeraldSecondary))
                            Text("Custom Rules", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. CRITICAL KEYWORD ALERTS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Keyword Alerts", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                StatusChip(text = "${keywordAlerts.size} Alerts", type = "error")
            }

            Spacer(modifier = Modifier.height(10.dp))

            keywordAlerts.forEach { alert ->
                BentoCard(modifier = Modifier.padding(bottom = 10.dp)) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = GuardianError, modifier = Modifier.size(20.dp))
                                Text("Keyword: \"${alert.keyword}\"", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            }
                            StatusChip(text = alert.riskLevel, type = if (alert.riskLevel == "Critical" || alert.riskLevel == "High") "error" else "warning")
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("Context: \"${alert.conversationPreview}\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("Detected at ${alert.time} on ${alert.date}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. MONITORED APPS LIST
            Text("Monitored Applications & Keyloggers", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Toggle real-time keyword scanning for specific platforms", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(10.dp))

            monitoredApps.forEach { app ->
                var isEnabled by remember { mutableStateOf(true) }
                BentoCard(modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TrustBlueContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (app.appName) {
                                        "WhatsApp" -> Icons.Default.Chat
                                        "Instagram" -> Icons.Default.CameraAlt
                                        "Telegram" -> Icons.Default.Send
                                        "Snapchat" -> Icons.Default.PhotoCamera
                                        "Discord" -> Icons.Default.SportsEsports
                                        "Chrome" -> Icons.Default.Language
                                        "SMS Messages" -> Icons.Default.Sms
                                        else -> Icons.Default.Apps
                                    },
                                    contentDescription = null,
                                    tint = TrustBluePrimary
                                )
                            }

                            Column {
                                Text(app.appName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("${app.category} • ${app.detectedKeywordsCount} detections today", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. CUSTOM WATCHLIST KEYWORDS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Custom Keyword Watchlist", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                IconButton(onClick = { showAddKeywordDialog = true }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Keyword", tint = TrustBluePrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(customKeywordList) { kw ->
                    InputChip(
                        selected = true,
                        onClick = {},
                        label = { Text(kw) },
                        trailingIcon = {
                            IconButton(onClick = { customKeywordList.remove(kw) }, modifier = Modifier.size(16.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Remove")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. WEB CATEGORY BLOCKLIST
            BentoCard {
                Column {
                    Text("Web Category Blocklist", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(10.dp))

                    val categories = listOf(
                        "Adult & NSFW Content" to true,
                        "Gambling & Lottery Sites" to true,
                        "Cyberbullying & Violence" to true,
                        "Drugs & Alcohol References" to true,
                        "Game & Social Bypasses" to false
                    )

                    categories.forEach { (catName, defaultState) ->
                        var state by remember { mutableStateOf(defaultState) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(catName, style = MaterialTheme.typography.bodyMedium)
                            Switch(checked = state, onCheckedChange = { state = it })
                        }
                    }
                }
            }
        }

        // ADD KEYWORD DIALOG
        if (showAddKeywordDialog) {
            AlertDialog(
                onDismissRequest = { showAddKeywordDialog = false },
                title = { Text("Add Custom Trigger Keyword", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = customKeywordInput,
                        onValueChange = { customKeywordInput = it },
                        label = { Text("Enter word or phrase (e.g. 'skip class')") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (customKeywordInput.isNotBlank()) {
                            customKeywordList.add(customKeywordInput.trim())
                            showAddKeywordDialog = false
                            coroutineScope.launch { snackbarHostState.showSnackbar("Added '${customKeywordInput}' to keyword watchlist") }
                            customKeywordInput = ""
                        }
                    }) { Text("Add Keyword") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddKeywordDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun SubscriptionPlansScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Subscription Plans", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(16.dp))
        BentoCard {
            Text("Pro Family Plan ($9.99/mo)", fontWeight = FontWeight.Bold, color = TrustBluePrimary)
            Text("Active • Renews Sep 1, 2026", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), shape = CircleShape) { Text("Current Active Plan") }
        }
    }
}

private data class PermissionAlertData(
    val id: String,
    val name: String,
    val description: String,
    val status: String,
    val category: String,
    val icon: ImageVector,
    val isGranted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionAlertsScreen(onBack: () -> Unit) {
    var selectedFilter by remember { mutableStateOf("Action Required") }
    val requestedMap = remember { mutableStateMapOf<String, Boolean>() }
    var allRequested by remember { mutableStateOf(false) }

    val permissionsList = remember {
        listOf(
            PermissionAlertData(
                id = "p1",
                name = "Accessibility Service",
                description = "Required for app blocking, web filtering, keylog detection, and anti-uninstall protection.",
                status = "Missing",
                category = "Core Protection",
                icon = Icons.Default.Accessibility
            ),
            PermissionAlertData(
                id = "p2",
                name = "Location Access (Always Allow)",
                description = "Required for real-time GPS tracking, geofence boundary alerts, and location history.",
                status = "Action Required",
                category = "Core Protection",
                icon = Icons.Default.LocationOn
            ),
            PermissionAlertData(
                id = "p3",
                name = "Notification Access",
                description = "Required to monitor incoming chat notifications, social media alerts, and flagged keywords.",
                status = "Not Granted",
                category = "Monitoring",
                icon = Icons.Default.NotificationsActive
            ),
            PermissionAlertData(
                id = "p4",
                name = "Usage Stats Access",
                description = "Required to track daily app screen time and enforce schedule limits.",
                status = "Missing",
                category = "Monitoring",
                icon = Icons.Default.DataUsage
            ),
            PermissionAlertData(
                id = "p5",
                name = "Display Over Other Apps (Overlay)",
                description = "Required to display instant screen lock overlays when time limits expire.",
                status = "Action Required",
                category = "Core Protection",
                icon = Icons.Default.Layers
            ),
            PermissionAlertData(
                id = "p6",
                name = "SMS & Call Log Access",
                description = "Required for detecting emergency SMS keywords and unknown caller monitoring.",
                status = "Not Granted",
                category = "Monitoring",
                icon = Icons.Default.Sms
            ),
            PermissionAlertData(
                id = "p7",
                name = "Background Battery Optimization Bypass",
                description = "Required to keep SentryKids background monitoring active without sleeping.",
                status = "Action Required",
                category = "System Safeguards",
                icon = Icons.Default.BatterySaver
            ),
            PermissionAlertData(
                id = "p8",
                name = "Camera & Screen Capture",
                description = "Required for remote camera snapshots and screen mirroring capabilities.",
                status = "Granted",
                category = "Remote Control",
                icon = Icons.Default.CameraAlt,
                isGranted = true
            ),
            PermissionAlertData(
                id = "p9",
                name = "Microphone Access",
                description = "Required for ambient one-way audio listening during emergency alerts.",
                status = "Granted",
                category = "Remote Control",
                icon = Icons.Default.Mic,
                isGranted = true
            ),
            PermissionAlertData(
                id = "p10",
                name = "Files & Media Storage",
                description = "Required to scan downloaded media files for inappropriate content.",
                status = "Granted",
                category = "System Safeguards",
                icon = Icons.Default.FolderSpecial,
                isGranted = true
            )
        )
    }

    val actionRequiredCount = permissionsList.count { !it.isGranted }

    val filteredList = when (selectedFilter) {
        "Action Required" -> permissionsList.filter { !it.isGranted }
        "Granted" -> permissionsList.filter { it.isGranted }
        else -> permissionsList
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Compact Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Permission Alerts",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Alex's Pixel 8 Pro • $actionRequiredCount Permissions Missing",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Warning Banner Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = GuardianErrorContainer,
            border = BorderStroke(1.dp, GuardianError.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GuardianError),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Action Required on Child Device",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnGuardianErrorContainer
                            )
                        )
                        Text(
                            text = "Essential permissions are missing. SentryKids cannot enforce protection until granted.",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnGuardianErrorContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        allRequested = true
                        permissionsList.forEach { if (!it.isGranted) requestedMap[it.id] = true }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GuardianError),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (allRequested) "Permission Request Signals Sent ✓" else "Request All Fixes from Child Device",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Action Required", "All", "Granted").forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = {
                        val count = when (filter) {
                            "Action Required" -> actionRequiredCount
                            "Granted" -> permissionsList.size - actionRequiredCount
                            else -> permissionsList.size
                        }
                        Text("$filter ($count)")
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Permission Items List
        filteredList.forEach { item ->
            val isRequested = requestedMap[item.id] == true || allRequested

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                border = BorderStroke(
                    1.dp,
                    if (!item.isGranted) GuardianError.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (item.isGranted) GrowthEmeraldContainer else GuardianErrorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (item.isGranted) GrowthEmeraldSecondary else GuardianError,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.weight(1f)
                                )

                                StatusChip(
                                    text = item.status,
                                    type = if (item.isGranted) "safe" else "danger"
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )

                            if (!item.isGranted) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = { requestedMap[item.id] = true },
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isRequested) GrowthEmeraldSecondary else MaterialTheme.colorScheme.primary
                                        ),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (isRequested) GrowthEmeraldSecondary else MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isRequested) Icons.Default.Check else Icons.Default.Send,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isRequested) "Request Sent ✓" else "Fix Permission",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PermissionsScreen(onBack: () -> Unit) {
    PermissionAlertsScreen(onBack = onBack)
}

@Composable
fun HelpCenterScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Help & Guides", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(16.dp))
        BentoCard {
            Text("Quick Pairing Walkthrough", fontWeight = FontWeight.Bold)
            Text("Learn how to connect child devices via QR code.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun DeveloperDiagnosticsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Developer Diagnostics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(16.dp))
        BentoCard {
            Text("Environment: Android Build Workspace", fontWeight = FontWeight.Bold)
            Text("Mock State Flow Active", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("About SentryKids", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(16.dp))
        BentoCard {
            Text("SentryKids Parent v2.4.0", fontWeight = FontWeight.Bold, color = TrustBluePrimary)
            Text("Smart AI Parental Control System", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun GlobalSearchScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Global Search", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search devices, settings, apps...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
    }
}
