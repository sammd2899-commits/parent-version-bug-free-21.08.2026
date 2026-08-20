package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mock.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Redesigned DEVICE (HOME) Dashboard
 * Focuses strictly on the currently selected child device from "My > Child Devices"
 */
@Composable
fun DashboardScreen(
    onNavigateToLiveView: () -> Unit = {},
    onNavigateToRemoteControl: () -> Unit = {},
    onNavigateToCameraPreview: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    onNavigateToApps: () -> Unit = {},
    onNavigateToSocialContent: () -> Unit = {},
    onNavigateToCallsSms: () -> Unit = {},
    onNavigateToAlerts: () -> Unit = {},
    onNavigateToDeviceHealth: () -> Unit = {},
    onNavigateToChildDevices: () -> Unit = {},
    onNavigateToScreenTimeUsage: () -> Unit = {},
    onNavigateToNotificationMonitoring: () -> Unit = {},
    onNavigateToContentFiltering: () -> Unit = {},
    onNavigateToAdvancedFeatures: () -> Unit = {},
    onNavigateToPermissionAlerts: () -> Unit = {}
) {
    val children by MockChildrenRepository.children.collectAsState()
    val selectedChildId by MockChildrenRepository.selectedChildId.collectAsState()
    val activeChild = children.find { it.id == selectedChildId } ?: children.firstOrNull() ?: ChildItem(
        id = "c1", name = "Alex", age = 8, deviceName = "Pixel 8 Pro", status = "Online"
    )

    val alerts by MockAlertsRepository.alerts.collectAsState()
    var isInstantLockActive by remember { mutableStateOf(false) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var isRinging by remember { mutableStateOf(false) }
    var brightnessLevel by remember { mutableFloatStateOf(0.75f) }
    var showDevicePickerDialog by remember { mutableStateOf(false) }
    var visibleActivityCount by remember { mutableIntStateOf(3) }
    var snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val allActivities = remember {
        listOf(
            DashboardActivityItem(1, "10:42 AM", "Keyword Detected", "Unsafe term flagged in web browser", GuardianError),
            DashboardActivityItem(2, "08:15 AM", "Arrived at School", "Entered Safe Zone: Oakridge Elementary", GrowthEmeraldSecondary),
            DashboardActivityItem(3, "07:50 AM", "Left Home", "Transit started • Speed: 15 km/h", TrustBluePrimary),
            DashboardActivityItem(4, "Yesterday 09:30 PM", "Screen Time Limit", "Device locked automatically for Bedtime Schedule", TrustBluePrimary),
            DashboardActivityItem(5, "Yesterday 08:15 PM", "App Blocked", "Attempted to open Roblox during Study Time", GuardianError),
            DashboardActivityItem(6, "Yesterday 07:00 PM", "Safe Search Filter", "Blocked 2 inappropriate web domain requests", TrustBluePrimary),
            DashboardActivityItem(7, "Yesterday 05:45 PM", "Battery Warning", "Device battery reached 15% charge", Color(0xFFF59E0B)),
            DashboardActivityItem(8, "Yesterday 04:20 PM", "Arrived at Home", "Entered Safe Zone: Home (Neighborhood)", GrowthEmeraldSecondary),
            DashboardActivityItem(9, "Yesterday 03:30 PM", "Left School", "Exited Safe Zone: Oakridge Elementary", TrustBluePrimary),
            DashboardActivityItem(10, "Yesterday 02:15 PM", "Call Monitored", "Incoming call from Unknown Number answered", Color(0xFFF59E0B)),
            DashboardActivityItem(11, "Yesterday 11:10 AM", "App Installed", "New app downloaded: Duolingo (Approved)", GrowthEmeraldSecondary),
            DashboardActivityItem(12, "Yesterday 09:00 AM", "Device Connected", "Connected to Oakridge_Guest Wi-Fi network", TrustBluePrimary),
            DashboardActivityItem(13, "2 Days Ago 08:00 PM", "Screen Time Warning", "15 minutes remaining before bedtime lock", Color(0xFFF59E0B)),
            DashboardActivityItem(14, "2 Days Ago 06:30 PM", "Location Check-in", "Manual location ping refreshed by parent", TrustBluePrimary),
            DashboardActivityItem(15, "2 Days Ago 05:10 PM", "Safe Zone Entry", "Entered Safe Zone: City Community Center", GrowthEmeraldSecondary),
            DashboardActivityItem(16, "2 Days Ago 03:00 PM", "Content Filtered", "Blocked social media site attempt on Chrome", GuardianError),
            DashboardActivityItem(17, "2 Days Ago 01:20 PM", "Notification Alert", "Sensitive text keyword flagged in SMS", GuardianError),
            DashboardActivityItem(18, "2 Days Ago 10:05 AM", "One-Way Audio", "30-second ambient audio check completed", TrustBluePrimary),
            DashboardActivityItem(19, "3 Days Ago 08:45 PM", "System Permission", "Usage Stats permission re-verified", GrowthEmeraldSecondary),
            DashboardActivityItem(20, "3 Days Ago 06:15 PM", "Speed Alert", "Vehicle speed exceeded 60 km/h in transit", Color(0xFFF59E0B)),
            DashboardActivityItem(21, "3 Days Ago 04:00 PM", "Battery Fully Charged", "Device unplugged at 100% battery level", GrowthEmeraldSecondary)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        // Top App Bar Greeting Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            TrustBluePrimary,
                            TrustBlueContainer
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Good Morning, Sarah",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            // Top Header Device Switcher Pill
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { showDevicePickerDialog = true }
                                    .semantics { testTag = "top_header_device_switcher" }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Smartphone,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "${activeChild.name}'s ${activeChild.deviceName}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Switch Device",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Connection Speed / Ping Badge (Req 9)
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (activeChild.isOnline) GrowthEmeraldSecondary else GuardianError)
                                    )
                                    Text(
                                        text = "Ping ${
                                            when (activeChild.id) {
                                                "c1" -> 32
                                                "c2" -> 18
                                                "c3" -> 45
                                                "c4" -> 88
                                                else -> 24
                                            }
                                        } ms",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GrowthEmeraldContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = OnGrowthEmeraldContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Protected",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnGrowthEmeraldContainer
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // CURRENTLY SELECTED CHILD DEVICE CARD (COMPACT DEVICE-STATUS HEADER)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(TrustBlueContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeChild.name.take(1),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TrustBluePrimary
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(if (activeChild.isOnline) GrowthEmeraldSecondary else GuardianError)
                                            .border(1.5.dp, Color.White, CircleShape)
                                            .align(Alignment.BottomEnd)
                                    )
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = activeChild.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (activeChild.isOnline) GrowthEmeraldContainer else GuardianErrorContainer
                                        ) {
                                            Text(
                                                text = if (activeChild.isOnline) "Online" else "Offline",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (activeChild.isOnline) OnGrowthEmeraldContainer else OnGuardianErrorContainer
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${activeChild.deviceName} • ${activeChild.deviceType}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showDevicePickerDialog = true },
                                modifier = Modifier.semantics { testTag = "card_device_switcher_arrow" }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch Selected Device",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // PRESERVED ALL METRICS (BATTERY, INTERNET, SYNC)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GrowthEmeraldContainer.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.BatteryChargingFull,
                                        contentDescription = null,
                                        tint = GrowthEmeraldSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Battery: ${activeChild.batteryPercent}%",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OnGrowthEmeraldContainer),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TrustBlueContainer.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Wifi,
                                        contentDescription = null,
                                        tint = TrustBluePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = activeChild.internetStatus,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = TrustBluePrimary),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Sync: ${activeChild.lastSync}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TODAY'S SUMMARY CARD (UPDATED REVISED 2x2 GRID)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(
                title = "Today's Summary",
                subtitle = "Real-time metrics for ${activeChild.name}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Screen Time Metric (Clickable -> Screen Time Usage Page)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToScreenTimeUsage() },
                        shape = RoundedCornerShape(16.dp),
                        color = TrustBlueContainer
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = TrustBluePrimary, modifier = Modifier.size(20.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = "Open", tint = TrustBluePrimary, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Screen Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(activeChild.screenTimeToday, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TrustBluePrimary))
                        }
                    }

                    // 2. Most Used Apps Metric (Clickable -> Screen Time Usage Page)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToScreenTimeUsage() },
                        shape = RoundedCornerShape(16.dp),
                        color = GrowthEmeraldContainer
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Apps, contentDescription = null, tint = GrowthEmeraldSecondary, modifier = Modifier.size(20.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = "Open", tint = GrowthEmeraldSecondary, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Most Used App", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("YouTube • 1h 15m", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GrowthEmeraldSecondary), maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 3. AI Social Safety Score (Replacing Alerts)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToSocialContent() },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TrustBluePrimary, modifier = Modifier.size(20.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("AI Safety Score", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("84% Safe", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface))
                        }
                    }

                    // 4. Keyword Scanner Status (Replacing Location)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToContentFiltering() },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FindInPage, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Keyword Alerts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("5 Detected", maxLines = 1, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // QUICK CONTROLS (2-COLUMN GRID)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(
                title = "Quick Controls",
                subtitle = "Instant remote access & monitoring"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickControlLargeCard(
                        modifier = Modifier.weight(1f),
                        title = "Live Screen",
                        subtitle = "Real-time stream",
                        icon = Icons.Default.Tv,
                        tag = "live_screen_card",
                        onClick = onNavigateToLiveView
                    )
                    QuickControlLargeCard(
                        modifier = Modifier.weight(1f),
                        title = "Remote Control",
                        subtitle = "Touch gestures",
                        icon = Icons.Default.SettingsRemote,
                        tag = "remote_control_card",
                        onClick = onNavigateToRemoteControl
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickControlLargeCard(
                        modifier = Modifier.weight(1f),
                        title = "Front Camera",
                        subtitle = "Remote snapshot",
                        icon = Icons.Default.CameraFront,
                        tag = "front_camera_card",
                        onClick = onNavigateToCameraPreview
                    )
                    QuickControlLargeCard(
                        modifier = Modifier.weight(1f),
                        title = "Rear Camera",
                        subtitle = "Surrounding view",
                        icon = Icons.Default.CameraRear,
                        tag = "rear_camera_card",
                        onClick = onNavigateToCameraPreview
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickControlLargeCard(
                        modifier = Modifier.weight(1f),
                        title = "Files",
                        subtitle = "Photos & downloads",
                        icon = Icons.Default.Folder,
                        tag = "files_card",
                        onClick = onNavigateToFiles
                    )
                    QuickControlLargeCard(
                        modifier = Modifier.weight(1f),
                        title = "Apps",
                        subtitle = "Limits & blocklist",
                        icon = Icons.Default.Apps,
                        tag = "apps_card",
                        onClick = onNavigateToApps
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickControlLargeCard(
                        modifier = Modifier.weight(1f),
                        title = "One-Way Audio",
                        subtitle = "Listen ambient sound",
                        icon = Icons.Default.Mic,
                        tag = "one_way_audio_card",
                        onClick = onNavigateToLiveView
                    )
                    QuickControlLargeCard(
                        modifier = Modifier.weight(1f),
                        title = "Permission Alerts",
                        subtitle = "7 Action Required",
                        icon = Icons.Default.AdminPanelSettings,
                        tag = "permission_alerts_card",
                        badgeText = "7 Missing",
                        onClick = onNavigateToPermissionAlerts
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CONTENT SAFETY SECTION (CORRECTED NAVIGATION ROUTING)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(
                title = "Content Safety",
                subtitle = "AI text, keyword, & communication monitoring"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 1. Social Content Detection -> Social Media Safety Monitor
                BentoCard(onClick = onNavigateToSocialContent) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TrustBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = TrustBluePrimary)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Social Content Detection", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Flagged messages in WhatsApp, Instagram, Snapchat", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusChip(text = "1 Flagged", type = "error")
                    }
                }

                // 2. Keyword Detection -> Keyword Detection Page
                BentoCard(onClick = onNavigateToContentFiltering) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GrowthEmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FindInPage, contentDescription = null, tint = GrowthEmeraldSecondary)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Keyword Detection", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Web searches, keylogger & input monitoring", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusChip(text = "Active", type = "safe")
                    }
                }

                // 3. Calls & SMS -> Calls & SMS History Page
                BentoCard(onClick = onNavigateToCallsSms) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Calls & SMS", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Call logs, text history & unknown number filter", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // 4. Notification Monitoring -> Notification Monitoring Page
                BentoCard(onClick = onNavigateToNotificationMonitoring) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GrowthEmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = GrowthEmeraldSecondary)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Notification Monitoring", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Filters suspicious push notifications & chat alerts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusChip(text = "Clean", type = "safe")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SCREEN TIME CARD WITH CIRCULAR CHART
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(
                title = "Screen Time Analysis",
                subtitle = "App category distribution for today"
            )

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Circular Chart Representation
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(100.dp)) {
                            val strokeWidth = 14.dp.toPx()
                            // Social Segment (Blue)
                            drawArc(
                                color = TrustBluePrimary,
                                startAngle = -90f,
                                sweepAngle = 180f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            // Gaming Segment (Rose Error)
                            drawArc(
                                color = GuardianError,
                                startAngle = 95f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            // Education Segment (Emerald)
                            drawArc(
                                color = GrowthEmeraldSecondary,
                                startAngle = 190f,
                                sweepAngle = 75f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(activeChild.screenTimeToday, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TrustBluePrimary))
                            Text("Social (50%)", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                            Text("1h 36m", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(GuardianError))
                            Text("Gaming (25%)", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                            Text("48m", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(GrowthEmeraldSecondary))
                            Text("Education (25%)", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                            Text("48m", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Allowance: ${activeChild.screenTimeLimit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = onNavigateToApps) {
                        Text("Manage App Limits", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DEVICE CONTROLS
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(
                title = "Device Controls",
                subtitle = "Immediate actions for ${activeChild.deviceName}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Instant Lock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TrustBluePrimary)
                            Column {
                                Text("Instant Lock", fontWeight = FontWeight.Bold)
                                Text("Lock screen immediately", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isInstantLockActive,
                            onCheckedChange = { isInstantLockActive = it }
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Flashlight
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.FlashlightOn, contentDescription = null, tint = GrowthEmeraldSecondary)
                            Column {
                                Text("Flashlight", fontWeight = FontWeight.Bold)
                                Text("Toggle device LED light", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isFlashlightOn,
                            onCheckedChange = { isFlashlightOn = it }
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Ring Device
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column {
                                Text("Ring Device", fontWeight = FontWeight.Bold)
                                Text("Play high-volume alarm for 1 min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Button(
                            onClick = { isRinging = !isRinging },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRinging) GuardianError else TrustBluePrimary
                            ),
                            shape = CircleShape
                        ) {
                            Text(if (isRinging) "Stop Ringing" else "Ring Now")
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Restart Device
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                            Column {
                                Text("Restart Device", fontWeight = FontWeight.Bold)
                                Text("Reboot ${activeChild.deviceName} remotely", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        OutlinedButton(onClick = { }, shape = CircleShape) {
                            Text("Restart")
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Brightness Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.WbSunny, contentDescription = null, tint = TrustBluePrimary)
                                Text("Display Brightness", fontWeight = FontWeight.Bold)
                            }
                            Text("${(brightnessLevel * 100).toInt()}%", fontWeight = FontWeight.Bold, color = TrustBluePrimary)
                        }
                        Slider(
                            value = brightnessLevel,
                            onValueChange = { brightnessLevel = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // RECENT ACTIVITY & DIAGNOSTICS
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionHeader(
                title = "Recent Activity & Timeline",
                subtitle = "Events & AI safety recommendations"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Timeline Card
            BentoCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val displayedActivities = allActivities.take(visibleActivityCount)
                    displayedActivities.forEachIndexed { index, activity ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(activity.color)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${activity.time} • ${activity.title}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = activity.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (index < displayedActivities.size - 1) {
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    if (visibleActivityCount < allActivities.size) {
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        TextButton(
                            onClick = {
                                visibleActivityCount = (visibleActivityCount + 6).coerceAtMost(allActivities.size)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { testTag = "view_more_activities_button" },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = TrustBluePrimary
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "View More Activities",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TrustBluePrimary
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Load more activities",
                                    tint = TrustBluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    } else {
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Text(
                            text = "All activities loaded",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Recommendation Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GrowthEmeraldContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OnGrowthEmeraldContainer, modifier = Modifier.size(28.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI Recommendation", fontWeight = FontWeight.Bold, color = OnGrowthEmeraldContainer)
                        Text("Shifting Bedtime Downtime 30 mins earlier improves sleep consistency for ${activeChild.name}.", style = MaterialTheme.typography.bodySmall, color = OnGrowthEmeraldContainer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Device Health Tile
            BentoCard(onClick = onNavigateToDeviceHealth) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Healing, contentDescription = null, tint = TrustBluePrimary)
                        Column {
                            Text("Device Health & Diagnostics", fontWeight = FontWeight.Bold)
                            Text("CPU 31°C • RAM 3.2 GB / 6 GB • Storage 70 GB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    StatusChip(text = "Excellent", type = "safe")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ADVANCED FEATURES ENTRY SECTION (REQ 1 & 4,5,6 ENTRY)
            SectionHeader(
                title = "Advanced Features",
                subtitle = "Manage website restrictions, browsing history & device permissions"
            )

            Spacer(modifier = Modifier.height(12.dp))

            BentoCard(
                onClick = onNavigateToAdvancedFeatures,
                modifier = Modifier.semantics { testTag = "advanced_features_entry_card" }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(TrustBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = TrustBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Explore Advanced Features",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Website Restriction • Browsing History • Missing Permissions",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Advanced Features",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // TOP HEADER DEVICE SWITCHER DIALOG
    if (showDevicePickerDialog) {
        AlertDialog(
            onDismissRequest = { showDevicePickerDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Devices, contentDescription = null, tint = TrustBluePrimary)
                    Text("Select Monitored Device", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Choose a child device to monitor in the dashboard:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    children.forEach { child ->
                        val isSelected = child.id == activeChild.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) TrustBlueContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, TrustBluePrimary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    MockChildrenRepository.selectChild(child.id)
                                    showDevicePickerDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) TrustBluePrimary else Color.Gray.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = child.name.take(1),
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "${child.name}'s ${child.deviceName}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${child.deviceType} • Ping ${
                                                when (child.id) {
                                                    "c1" -> 32
                                                    "c2" -> 18
                                                    "c3" -> 45
                                                    "c4" -> 88
                                                    else -> 24
                                                }
                                            } ms",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = TrustBluePrimary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDevicePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun QuickControlLargeCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .semantics { testTag = tag }
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .border(
                1.dp,
                if (badgeText != null) GuardianError.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(20.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (badgeText != null) GuardianErrorContainer else TrustBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (badgeText != null) GuardianError else TrustBluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = if (badgeText != null) GuardianError else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (badgeText != null) FontWeight.Bold else FontWeight.Normal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class DashboardActivityItem(
    val id: Int,
    val time: String,
    val title: String,
    val description: String,
    val color: Color
)
