package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mock.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Main Devices Page - Dedicated Child Device Management
 */
@Composable
fun DevicesListScreen(
    onNavigateToDeviceHealth: (DeviceItem) -> Unit = {},
    onNavigateToRemoteControl: (DeviceItem) -> Unit = {},
    onNavigateToLiveView: (DeviceItem) -> Unit = {},
    onNavigateToCameraPreview: (DeviceItem) -> Unit = {},
    onNavigateToFiles: (DeviceItem) -> Unit = {},
    onNavigateToCallsSms: (DeviceItem) -> Unit = {},
    onNavigateToAutomations: () -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val children by MockChildrenRepository.children.collectAsState()
    val selectedChildId by MockChildrenRepository.selectedChildId.collectAsState()

    // State
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // All, Online, Offline, Android, iOS
    var sortBy by remember { mutableStateOf("Name") } // Name, Battery, Sync

    // Dialogs
    var showAddDeviceModal by remember { mutableStateOf(false) }
    var deviceToDelete by remember { mutableStateOf<ChildItem?>(null) }
    var deviceToEdit by remember { mutableStateOf<ChildItem?>(null) }
    var deviceToControl by remember { mutableStateOf<ChildItem?>(null) }

    // Dialog Inputs
    var editChildName by remember { mutableStateOf("") }
    var editDeviceName by remember { mutableStateOf("") }
    var newChildNameInput by remember { mutableStateOf("") }
    var newDeviceNameInput by remember { mutableStateOf("") }
    var newDeviceTypeInput by remember { mutableStateOf("Android") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Filter & Sort Logic
    val filteredChildren = children.filter { child ->
        val matchesQuery = child.name.contains(searchQuery, ignoreCase = true) || child.deviceName.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Online" -> child.isOnline
            "Offline" -> !child.isOnline
            "Android" -> child.deviceType.contains("Android", ignoreCase = true)
            "iOS" -> child.deviceType.contains("iPhone", ignoreCase = true) || child.deviceType.contains("iPad", ignoreCase = true)
            else -> true
        }
        matchesQuery && matchesFilter
    }.sortedWith { a, b ->
        when (sortBy) {
            "Battery" -> b.batteryPercent.compareTo(a.batteryPercent)
            "Sync" -> a.lastSync.compareTo(b.lastSync)
            else -> a.name.compareTo(b.name, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                    Column {
                        Text(
                            text = "Devices",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Manage paired child devices & active monitoring",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                Button(
                    onClick = { showAddDeviceModal = true },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Device", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search child name or device...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter & Sort Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val filters = listOf("All", "Online", "Offline", "Android", "iOS")
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Sort Chip Toggle
                AssistChip(
                    onClick = {
                        sortBy = when (sortBy) {
                            "Name" -> "Battery"
                            "Battery" -> "Sync"
                            else -> "Name"
                        }
                    },
                    label = { Text("Sort: $sortBy", style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Device Count Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = TrustBlueContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Phonelink, contentDescription = null, tint = TrustBluePrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "${filteredChildren.size} Paired Child Device${if (filteredChildren.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TrustBluePrimary)
                        )
                    }
                    Text(
                        text = "Real-time AI Active",
                        style = MaterialTheme.typography.labelSmall.copy(color = GrowthEmeraldSecondary, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Device Cards List
            if (filteredChildren.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Smartphone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No child devices found", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                    }
                }
            } else {
                filteredChildren.forEach { child ->
                    val isSelectedActive = child.id == selectedChildId

                    BentoCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column {
                            // Top Device Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Child Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelectedActive) TrustBluePrimary else TrustBlueContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = child.name.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelectedActive) Color.White else TrustBluePrimary
                                            )
                                        )
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = child.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "• ${child.deviceName}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            // Online / Offline Indicator
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(if (child.isOnline) GrowthEmeraldSecondary else GuardianError)
                                            )
                                            Text(
                                                text = if (child.isOnline) "Online" else "Offline",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (child.isOnline) GrowthEmeraldSecondary else GuardianError,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text("• ${child.deviceType}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                // Badges
                                Column(horizontalAlignment = Alignment.End) {
                                    if (isSelectedActive) {
                                        StatusChip(text = "Active Dashboard", type = "safe")
                                    } else {
                                        StatusChip(text = "Monitored", type = "info")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Device Info Metrics Row (Battery, Internet, Sync)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Battery
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(
                                        imageVector = if (child.batteryPercent > 20) Icons.Default.BatteryChargingFull else Icons.Default.BatteryAlert,
                                        contentDescription = null,
                                        tint = if (child.batteryPercent > 20) GrowthEmeraldSecondary else GuardianError,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text("${child.batteryPercent}% Battery", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }

                                // Internet
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(
                                        imageVector = if (child.isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                        contentDescription = null,
                                        tint = if (child.isOnline) TrustBluePrimary else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(if (child.isOnline) "WiFi 5G Active" else "Disconnected", style = MaterialTheme.typography.labelSmall)
                                }

                                // Last Sync
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Sync, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                    Text("Sync: ${child.lastSync}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Set as Active Dashboard Device button
                                if (isSelectedActive) {
                                    Button(
                                        onClick = { },
                                        enabled = false,
                                        modifier = Modifier.weight(1.3f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(disabledContainerColor = TrustBluePrimary)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Active Device", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = {
                                            MockChildrenRepository.selectChild(child.id)
                                            coroutineScope.launch { snackbarHostState.showSnackbar("${child.name}'s device set as active dashboard device") }
                                        },
                                        modifier = Modifier.weight(1.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Set Active", style = MaterialTheme.typography.labelSmall)
                                    }
                                }

                                // Remote Actions Button
                                IconButton(
                                    onClick = { deviceToControl = child },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = "Remote Controls", modifier = Modifier.size(18.dp))
                                }

                                // Device Settings Button
                                IconButton(
                                    onClick = {
                                        deviceToEdit = child
                                        editChildName = child.name
                                        editDeviceName = child.deviceName
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = "Device Settings", modifier = Modifier.size(18.dp))
                                }

                                // Delete Device Button
                                IconButton(
                                    onClick = { deviceToDelete = child },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(GuardianError.copy(alpha = 0.1f))
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Unpair Device", tint = GuardianError, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Add Device Action Button
        FloatingActionButton(
            onClick = { showAddDeviceModal = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 20.dp),
            containerColor = TrustBluePrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add New Child Device")
        }

        // ==========================================
        // DIALOGS & MODALS
        // ==========================================

        // ADD DEVICE MODAL
        if (showAddDeviceModal) {
            AlertDialog(
                onDismissRequest = { showAddDeviceModal = false },
                title = { Text("Pair New Child Device", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Enter child and device details to generate a 6-digit pairing code.", style = MaterialTheme.typography.bodySmall)
                        OutlinedTextField(
                            value = newChildNameInput,
                            onValueChange = { newChildNameInput = it },
                            label = { Text("Child Name") },
                            placeholder = { Text("e.g. Leo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDeviceNameInput,
                            onValueChange = { newDeviceNameInput = it },
                            label = { Text("Device Name") },
                            placeholder = { Text("e.g. Leo's Phone") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Device Platform:", style = MaterialTheme.typography.labelSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Android", "iPhone", "iPad", "Chromebook").forEach { type ->
                                FilterChip(
                                    selected = newDeviceTypeInput == type,
                                    onClick = { newDeviceTypeInput = type },
                                    label = { Text(type, style = MaterialTheme.typography.labelSmall) }
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
                                showAddDeviceModal = false
                                newChildNameInput = ""
                                newDeviceNameInput = ""
                                coroutineScope.launch { snackbarHostState.showSnackbar("Device paired successfully!") }
                            }
                        }
                    ) {
                        Text("Pair Device")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDeviceModal = false }) { Text("Cancel") }
                }
            )
        }

        // EDIT DEVICE SETTINGS MODAL
        if (deviceToEdit != null) {
            val dev = deviceToEdit!!
            AlertDialog(
                onDismissRequest = { deviceToEdit = null },
                title = { Text("Device Settings", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = editDeviceName,
                            onValueChange = { editDeviceName = it },
                            label = { Text("Device Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editDeviceName.isNotBlank()) {
                                MockChildrenRepository.renameDevice(dev.id, editDeviceName)
                                deviceToEdit = null
                                coroutineScope.launch { snackbarHostState.showSnackbar("Device settings updated") }
                            }
                        }
                    ) { Text("Save Settings") }
                },
                dismissButton = {
                    TextButton(onClick = { deviceToEdit = null }) { Text("Cancel") }
                }
            )
        }

        // REMOTE CONTROL QUICK DIALOG
        if (deviceToControl != null) {
            val dev = deviceToControl!!
            AlertDialog(
                onDismissRequest = { deviceToControl = null },
                title = { Text("Remote Actions: ${dev.name}", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                deviceToControl = null
                                coroutineScope.launch { snackbarHostState.showSnackbar("Remote Lock Command Sent to ${dev.deviceName}") }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GuardianError),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Instant Screen Lock")
                        }

                        Button(
                            onClick = {
                                deviceToControl = null
                                coroutineScope.launch { snackbarHostState.showSnackbar("Loud Emergency Siren Triggered on ${dev.deviceName}") }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ring Siren Alarm")
                        }

                        Button(
                            onClick = {
                                deviceToControl = null
                                coroutineScope.launch { snackbarHostState.showSnackbar("Internet Paused for ${dev.name}") }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.WifiOff, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pause Internet Access")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { deviceToControl = null }) { Text("Close") }
                }
            )
        }

        // DELETE / UNPAIR CONFIRMATION DIALOG
        if (deviceToDelete != null) {
            val dev = deviceToDelete!!
            AlertDialog(
                onDismissRequest = { deviceToDelete = null },
                title = { Text("Unpair ${dev.name}'s Device?", fontWeight = FontWeight.Bold, color = GuardianError) },
                text = {
                    Text("Are you sure you want to unpair '${dev.deviceName}'? Real-time AI monitoring, location updates, and safety alerts will be stopped for this device.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            MockChildrenRepository.removeDevice(dev.id)
                            deviceToDelete = null
                            coroutineScope.launch { snackbarHostState.showSnackbar("Device unpaired from account") }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GuardianError)
                    ) {
                        Text("Unpair Device")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deviceToDelete = null }) { Text("Cancel") }
                }
            )
        }
    }
}

/**
 * Device Health & Diagnostics Screen
 */
@Composable
fun DeviceHealthScreen(
    device: DeviceItem,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "${device.name} Diagnostics",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        BentoCard {
            Text("System Overview", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("OS Version", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(device.osVersion, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Last Synced", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(device.lastSynced, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Signal Strength", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(device.signalStrength, fontWeight = FontWeight.Bold, color = GrowthEmeraldSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BentoCard {
            Text("Memory & Temperature", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(12.dp))
            Text("RAM Usage (${device.ramUsedGb} GB / ${device.ramTotalGb} GB)", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { device.ramUsedGb / device.ramTotalGb },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = TrustBluePrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("CPU Temperature (${device.cpuTempC}°C)", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (device.cpuTempC / 70f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = GrowthEmeraldSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary)
        ) {
            Text("Back to Devices")
        }
    }
}

/**
 * Data models for Live Stream, Remote Control, and Camera history
 */
data class LiveScreenshotItem(
    val id: String,
    val title: String,
    val timestamp: String,
    val appTag: String
)

data class LiveRecordingItem(
    val id: String,
    val filename: String,
    val duration: String,
    val sizeMb: String,
    val timestamp: String
)

data class CameraMediaItem(
    val id: String,
    val type: String, // "photo" or "video"
    val camera: String, // "Front" or "Rear"
    val timestamp: String,
    val detail: String
)

/**
 * Live Screen Mirroring Interface (TeamViewer / AnyDesk / AirDroid Style)
 */
@Composable
fun LiveScreenMirroringScreen(
    device: DeviceItem,
    onBack: () -> Unit
) {
    var isMirroring by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var activeScreenIndex by remember { mutableIntStateOf(0) } // 0: Home, 1: WhatsApp, 2: YouTube, 3: Chrome, 4: Instagram
    var autoCycle by remember { mutableStateOf(true) }
    var brightness by remember { mutableFloatStateOf(0.85f) }
    var volume by remember { mutableFloatStateOf(0.70f) }
    var rotationDeg by remember { mutableIntStateOf(0) }
    var isFullscreen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val recentScreenshots = remember {
        mutableStateListOf(
            LiveScreenshotItem("s1", "WhatsApp Chat View", "10:42 AM", "WhatsApp"),
            LiveScreenshotItem("s2", "YouTube Educational Video", "10:35 AM", "YouTube")
        )
    }

    val recentRecordings = remember {
        mutableStateListOf(
            LiveRecordingItem("r1", "Clip_20260807_1040.mp4", "00:45", "18.2 MB", "10 mins ago"),
            LiveRecordingItem("r2", "Clip_20260807_1015.mp4", "02:10", "42.6 MB", "35 mins ago")
        )
    }

    // Timer for auto cycling screens to simulate live interaction
    LaunchedEffect(isMirroring, isPaused, autoCycle) {
        while (isMirroring && !isPaused && autoCycle) {
            delay(4000)
            activeScreenIndex = (activeScreenIndex + 1) % 5
        }
    }

    // Timer for recording counter
    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            recordingSeconds++
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Top App Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Alex",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "• ${device.name}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isMirroring && !isPaused) GrowthEmeraldSecondary else GuardianError)
                                )
                                Text(
                                    text = if (!isMirroring) "Stream Stopped" else if (isPaused) "Paused" else "Live Mirroring",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isMirroring && !isPaused) GrowthEmeraldSecondary else GuardianError,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Battery & Signal Indicators
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = GrowthEmeraldSecondary, modifier = Modifier.size(16.dp))
                                Text("${device.batteryPercent}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Wifi, contentDescription = null, tint = TrustBluePrimary, modifier = Modifier.size(16.dp))
                                Text("5G", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Area - Large Live Phone Preview Container (9:19 ratio)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(530.dp)
                        .graphicsLayer(rotationZ = rotationDeg.toFloat())
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF0F172A))
                        .border(4.dp, Color(0xFF334155), RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (isMirroring && !isPaused) {
                        // Phone Content Simulation
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF020617))
                        ) {
                            // Status bar of child phone
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("10:42 AM", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold))
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Icon(Icons.Default.BatteryFull, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }

                            // Dynamic Screen View
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                when (activeScreenIndex) {
                                    0 -> MockHomeScreenView()
                                    1 -> MockWhatsAppScreenView()
                                    2 -> MockYouTubeScreenView()
                                    3 -> MockChromeScreenView()
                                    else -> MockInstagramScreenView()
                                }

                                // Live OSD Tag
                                Surface(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopStart),
                                    shape = CircleShape,
                                    color = GuardianError
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                        Text("LIVE 60FPS", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                                    }
                                }

                                if (isRecording) {
                                    Surface(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .align(Alignment.TopEnd),
                                        shape = CircleShape,
                                        color = Color.Red
                                    ) {
                                        Text(
                                            text = String.format("REC %02d:%02d", recordingSeconds / 60, recordingSeconds % 60),
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Child Phone Soft Navigation Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .background(Color.Black),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(3.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.6f))
                                )
                            }
                        }
                    } else {
                        // Stopped / Paused Overlay
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (!isMirroring) Icons.Default.TvOff else Icons.Default.PauseCircle,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (!isMirroring) "Mirror Stream Terminated" else "Stream Paused by Parent",
                                style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Tap 'Start Live Mirror' to resume stream",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // App Switcher Chips for Demo Control
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    val apps = listOf("Home", "WhatsApp", "YouTube", "Chrome", "Instagram")
                    apps.forEachIndexed { idx, name ->
                        FilterChip(
                            selected = activeScreenIndex == idx,
                            onClick = {
                                activeScreenIndex = idx
                                autoCycle = false
                            },
                            label = { Text(name, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TrustBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Bar below preview
            BentoCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(if (isMirroring && !isPaused) "Connected" else "Idle", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GrowthEmeraldSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Resolution", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("1080x2400", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("FPS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("60 FPS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TrustBluePrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Latency", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("18 ms", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Control Panel
            BentoCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Remote Stream Controls", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))

                // Primary Large Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (!isMirroring) {
                                isMirroring = true
                                isPaused = false
                            } else {
                                isPaused = !isPaused
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isPaused || !isMirroring) TrustBluePrimary else Color(0xFFD97706)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (!isMirroring || isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (!isMirroring) "Start Mirror" else if (isPaused) "Resume" else "Pause", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val timeStr = String.format("%02d:%02d AM", (10..12).random(), (10..59).random())
                            val appNames = listOf("Home Screen", "WhatsApp", "YouTube", "Chrome", "Instagram")
                            recentScreenshots.add(0, LiveScreenshotItem("s_${System.currentTimeMillis()}", "Live Snapshot", timeStr, appNames[activeScreenIndex]))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Screenshot saved to cloud vault")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Screenshot", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (isRecording) {
                                isRecording = false
                                val secs = recordingSeconds
                                recentRecordings.add(0, LiveRecordingItem("r_${System.currentTimeMillis()}", "Clip_${System.currentTimeMillis().toString().takeLast(4)}.mp4", String.format("00:%02d", secs.coerceAtLeast(10)), "14.5 MB", "Just now"))
                                recordingSeconds = 0
                                coroutineScope.launch { snackbarHostState.showSnackbar("Live recording saved") }
                            } else {
                                isRecording = true
                                recordingSeconds = 0
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) GuardianError else Color(0xFF059669)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isRecording) "Stop Rec" else "Start Rec", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { isFullscreen = !isFullscreen },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fullscreen", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Quick Hardware Adjustments
                Text("Quick Adjustments", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LightMode, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(value = brightness, onValueChange = { brightness = it }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${(brightness * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(value = volume, onValueChange = { volume = it }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${(volume * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { rotationDeg = if (rotationDeg == 0) 90 else 0 }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ScreenRotation, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rotate", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    IconButton(onClick = {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Live stream force-refreshed") }
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Refresh Stream", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recent Captures Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Recent Screenshots", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(recentScreenshots) { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.width(140.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(item.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                Text("${item.appTag} • ${item.timestamp}", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Recent Live Recordings", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))

                recentRecordings.forEach { rec ->
                    BentoCard(modifier = Modifier.padding(bottom = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Movie, contentDescription = null, tint = TrustBluePrimary)
                                Column {
                                    Text(rec.filename, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("${rec.duration} • ${rec.sizeMb} • ${rec.timestamp}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = TrustBluePrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Storage Usage
                BentoCard {
                    Text("Cloud Stream Recording Vault", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("1.4 GB / 10.0 GB Cloud Storage Used", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { 0.14f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = TrustBluePrimary
                    )
                }
            }
        }
    }

    // Fullscreen Dialog
    if (isFullscreen) {
        Dialog(onDismissRequest = { isFullscreen = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.Black
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Alex's Live Screen (Fullscreen)", color = Color.White, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { isFullscreen = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center
                        ) {
                            when (activeScreenIndex) {
                                0 -> MockHomeScreenView()
                                1 -> MockWhatsAppScreenView()
                                2 -> MockYouTubeScreenView()
                                3 -> MockChromeScreenView()
                                else -> MockInstagramScreenView()
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Mock Phone Screen Views for Mirroring Simulation
 */
@Composable
private fun MockHomeScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("10:42", style = MaterialTheme.typography.displayMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
        Text("Thursday, Aug 7 • 72° Sunny", style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray))
        
        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar Widget
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search apps or web...", style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Grid
        val apps = listOf(
            "WhatsApp" to Color(0xFF25D366),
            "YouTube" to Color(0xFFFF0000),
            "Chrome" to Color(0xFF4285F4),
            "Instagram" to Color(0xFFE1306C),
            "TikTok" to Color(0xFF00F2FE),
            "Roblox" to Color(0xFF000000),
            "Spotify" to Color(0xFF1DB954),
            "Settings" to Color(0xFF64748B)
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            apps.chunked(4).forEach { rowApps ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowApps.forEach { (name, color) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(name, style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 9.sp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MockWhatsAppScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B141A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1F2C34))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF00A884)), contentAlignment = Alignment.Center) {
                Text("M", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Mom (Sarah)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("online", color = Color(0xFF00A884), fontSize = 10.sp)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(color = Color(0xFF005C4B), shape = RoundedCornerShape(8.dp), modifier = Modifier.align(Alignment.End)) {
                Text("Finished my math homework! 🚀", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
            }
            Surface(color = Color(0xFF202C33), shape = RoundedCornerShape(8.dp), modifier = Modifier.align(Alignment.Start)) {
                Text("Great job Alex! Remember your bag for tomorrow.", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
            }
            Surface(color = Color(0xFF005C4B), shape = RoundedCornerShape(8.dp), modifier = Modifier.align(Alignment.End)) {
                Text("Done! Packing it now 👍", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun MockYouTubeScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color(0xFF212121)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
            Text("03:42 / 12:15", color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp))
        }
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Solar System 3D Astronomy Exploration for Kids", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("Science Channel • 1.2M views", color = Color.Gray, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Up Next", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(color = Color(0xFF272727), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth().height(40.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("How Rockets Launch Into Space", color = Color.White, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun MockChromeScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Surface(color = Color(0xFFF1F5F9), modifier = Modifier.fillMaxWidth().padding(4.dp), shape = RoundedCornerShape(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("https://en.wikipedia.org/wiki/Jupiter", fontSize = 10.sp, color = Color.DarkGray)
            }
        }
        Column(modifier = Modifier.padding(10.dp)) {
            Text("Jupiter - Planet Overview", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Jupiter is the fifth planet from the Sun and the largest in the Solar System. It is a gas giant with a mass more than two and a half times that of all the other planets in the Solar System combined...", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun MockInstagramScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Instagram", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(160.dp).background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                Text("@kids_astronomy", color = Color.White, fontSize = 10.sp)
            }
        }
        Column(modifier = Modifier.padding(8.dp)) {
            Text("1,420 likes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text("kids_astronomy Hubble Telescope latest view of Jupiter's Great Red Spot! 🔭", color = Color.LightGray, fontSize = 10.sp)
        }
    }
}

/**
 * Remote Screen Control Interface (TeamViewer / AnyDesk / AirDroid Remote Input)
 */
@Composable
fun RemoteControlScreen(
    device: DeviceItem,
    onBack: () -> Unit
) {
    var lastTouchPoint by remember { mutableStateOf<Offset?>(null) }
    var terminalOutput by remember { mutableStateOf("Remote touch session active. Tap screen or use soft keys...") }
    var inputMessage by remember { mutableStateOf("") }
    var volume by remember { mutableFloatStateOf(0.7f) }
    var brightness by remember { mutableFloatStateOf(0.8f) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var isDeviceLocked by remember { mutableStateOf(false) }
    var showRebootDialog by remember { mutableStateOf(false) }
    var showClipboardDialog by remember { mutableStateOf(false) }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Column {
                        Text(
                            text = "Remote Screen Control",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Alex • ${device.name}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
                StatusChip(text = "15ms Latency", type = "safe")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Area - Large Interactive Screen Frame with Touch Overlay
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(480.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0F172A))
                        .border(3.dp, TrustBluePrimary, RoundedCornerShape(24.dp))
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                lastTouchPoint = offset
                                terminalOutput = "[TOUCH EVENT] Single Tap registered at X: ${offset.x.toInt()}, Y: ${offset.y.toInt()}"
                            }
                        },
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Simulated Live View inside frame
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Phone status bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("10:42 AM", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 9.sp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                Icon(Icons.Default.BatteryFull, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                        }

                        // Active Interactive Canvas
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            MockHomeScreenView()

                            // Display Touch Pointer Dot if user tapped
                            lastTouchPoint?.let { pos ->
                                Box(
                                    modifier = Modifier
                                        .offset(x = (pos.x / 3.2f).dp, y = (pos.y / 3.2f).dp)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(TrustBluePrimary.copy(alpha = 0.6f))
                                        .border(2.dp, Color.White, CircleShape)
                                )
                            }

                            if (isDeviceLocked) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.85f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = GuardianError, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Device Remotely Locked", color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("SentryKids Parent Guard", color = Color.Gray, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Android Soft Keys Navigation Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .background(Color.Black),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                terminalOutput = "[SYSTEM KEY] Sent 'Back' key event to device"
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = {
                                terminalOutput = "[SYSTEM KEY] Sent 'Home' key event to device"
                            }) {
                                Icon(Icons.Default.RadioButtonUnchecked, contentDescription = "Home", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = {
                                terminalOutput = "[SYSTEM KEY] Sent 'Recent Apps' key event to device"
                            }) {
                                Icon(Icons.Default.CropSquare, contentDescription = "Recents", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Directional Gesture Controls
            BentoCard {
                Text("Touch & Gesture Overlays", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = { terminalOutput = "[GESTURE] Swiped UP on remote screen" }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)
                        Text("Swipe Up", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(onClick = { terminalOutput = "[GESTURE] Swiped DOWN on remote screen" }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                        Text("Swipe Down", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(onClick = { terminalOutput = "[GESTURE] Swiped LEFT on remote screen" }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                        Text("Swipe Left", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(onClick = { terminalOutput = "[GESTURE] Swiped RIGHT on remote screen" }) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                        Text("Swipe Right", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Remote Keyboard Input Field
            BentoCard {
                Text("Remote Keyboard Input", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("Type text/URL to send to child screen...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                terminalOutput = "[REMOTE INPUT] Sent text string: '$inputMessage'"
                                inputMessage = ""
                                coroutineScope.launch { snackbarHostState.showSnackbar("Text sent to active input field") }
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hardware Controls & Commands Grid
            BentoCard {
                Text("Remote System Actions", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionTile(
                        title = if (isDeviceLocked) "Unlock" else "Lock Device",
                        icon = Icons.Default.Lock,
                        iconColor = GuardianError,
                        onClick = {
                            isDeviceLocked = !isDeviceLocked
                            terminalOutput = if (isDeviceLocked) "[SECURITY] Instant Remote Lock Applied" else "[SECURITY] Device Unlocked"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ActionTile(
                        title = "Power / Reboot",
                        icon = Icons.Default.PowerSettingsNew,
                        iconColor = Color(0xFFD97706),
                        onClick = { showRebootDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    ActionTile(
                        title = "Flashlight",
                        icon = Icons.Default.FlashlightOn,
                        iconColor = GrowthEmeraldSecondary,
                        onClick = {
                            isFlashlightOn = !isFlashlightOn
                            terminalOutput = "[HARDWARE] Torch state toggled: ${if (isFlashlightOn) "ON" else "OFF"}"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ActionTile(
                        title = "Clipboard",
                        icon = Icons.Default.ContentCopy,
                        iconColor = TrustBluePrimary,
                        onClick = { showClipboardDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeDown, contentDescription = null)
                    Slider(value = volume, onValueChange = {
                        volume = it
                        terminalOutput = "[SLIDER] Volume set to ${(volume * 100).toInt()}%"
                    }, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.VolumeUp, contentDescription = null)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LightMode, contentDescription = null)
                    Slider(value = brightness, onValueChange = {
                        brightness = it
                        terminalOutput = "[SLIDER] Brightness set to ${(brightness * 100).toInt()}%"
                    }, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Brightness7, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Realtime Terminal Execution Console
            BentoCard {
                Text("Console Log Feed", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(10.dp)
                ) {
                    Text(
                        text = terminalOutput,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF38BDF8))
                    )
                }
            }
        }
    }

    // Reboot Confirmation Dialog
    if (showRebootDialog) {
        AlertDialog(
            onDismissRequest = { showRebootDialog = false },
            title = { Text("Reboot Device?") },
            text = { Text("Are you sure you want to remotely restart Alex's Pixel 8 Pro?") },
            confirmButton = {
                Button(onClick = {
                    showRebootDialog = false
                    terminalOutput = "[SYSTEM] Executing Remote Device Reboot..."
                    coroutineScope.launch { snackbarHostState.showSnackbar("Reboot command dispatched to device") }
                }) {
                    Text("Reboot Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRebootDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Clipboard Dialog
    if (showClipboardDialog) {
        AlertDialog(
            onDismissRequest = { showClipboardDialog = false },
            title = { Text("Remote Clipboard Content") },
            text = {
                Column {
                    Text("Latest text copied on child's device:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "https://school.edu/assignments/math_ch4.pdf",
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showClipboardDialog = false
                    coroutineScope.launch { snackbarHostState.showSnackbar("Copied to parent clipboard") }
                }) {
                    Text("Copy Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClipboardDialog = false }) { Text("Close") }
            }
        )
    }
}

/**
 * Professional Remote Camera Interface
 */
@Composable
fun CameraPreviewScreen(
    device: DeviceItem,
    onBack: () -> Unit
) {
    var selectedCamera by remember { mutableStateOf("Front") } // "Front" vs "Rear"
    var isRecording by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var flashMode by remember { mutableStateOf("Off") } // "Off", "Auto", "On"
    var zoomLevel by remember { mutableStateOf("1x") }
    var resolution by remember { mutableStateOf("1080p") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val cameraHistory = remember {
        mutableStateListOf(
            CameraMediaItem("c1", "photo", "Front", "10:42 AM", "1080p • 2.4 MB"),
            CameraMediaItem("c2", "video", "Rear", "10:20 AM", "1080p (45s) • 28.5 MB")
        )
    }

    // Timer for video recording
    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            recordingSeconds++
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Column {
                            Text("Remote Camera", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Alex • ${device.name}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                    }

                    // Camera Switcher Segmented Control
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = selectedCamera == "Front",
                            onClick = { selectedCamera = "Front" },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text("Front", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = selectedCamera == "Rear",
                            onClick = { selectedCamera = "Rear" },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Text("Rear", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Area - Large Live Camera Viewport Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A))
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedCamera == "Front") {
                    // Simulated Front Camera View
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF1E293B)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = GrowthEmeraldSecondary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Face Tracked • Alex • Study Desk Safe Zone",
                                color = GrowthEmeraldSecondary,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    // Simulated Rear Camera View
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF020617)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Landscape, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Classroom Environment View", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        Text("ISO 200 • f/1.8 • 1080p60", color = Color.Gray, fontSize = 10.sp)
                    }
                }

                // Top Overlay Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = CircleShape, color = GuardianError) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                            Text("LIVE", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp))
                        }
                    }

                    if (isRecording) {
                        Surface(shape = CircleShape, color = Color.Red) {
                            Text(
                                text = String.format("REC %02d:%02d", recordingSeconds / 60, recordingSeconds % 60),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text("2026-08-07 10:42:19", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Camera Settings Quick Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flash Toggle
                IconButton(onClick = {
                    flashMode = when (flashMode) {
                        "Off" -> "Auto"
                        "Auto" -> "On"
                        else -> "Off"
                    }
                }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(if (flashMode == "Off") Icons.Default.FlashOff else Icons.Default.FlashOn, contentDescription = null)
                        Text("Flash: $flashMode", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
                    }
                }

                // Zoom Toggle
                IconButton(onClick = {
                    zoomLevel = when (zoomLevel) {
                        "1x" -> "2x"
                        "2x" -> "5x"
                        else -> "1x"
                    }
                }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ZoomIn, contentDescription = null)
                        Text("Zoom: $zoomLevel", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
                    }
                }

                // Resolution Toggle
                IconButton(onClick = {
                    resolution = if (resolution == "1080p") "720p" else "1080p"
                }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.HighQuality, contentDescription = null)
                        Text(resolution, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Shutter Controls (Capture Photo & Record Video)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Capture Photo Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            val timeStr = String.format("%02d:%02d AM", (10..12).random(), (10..59).random())
                            cameraHistory.add(0, CameraMediaItem("c_${System.currentTimeMillis()}", "photo", selectedCamera, timeStr, "$resolution • 2.6 MB"))
                            coroutineScope.launch { snackbarHostState.showSnackbar("Remote photo snapshot captured") }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(TrustBluePrimary)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Take Photo", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Capture Photo", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                // Record Video Button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            if (isRecording) {
                                isRecording = false
                                val secs = recordingSeconds
                                cameraHistory.add(0, CameraMediaItem("c_${System.currentTimeMillis()}", "video", selectedCamera, "Just now", "$resolution (${secs}s) • 24.2 MB"))
                                recordingSeconds = 0
                                coroutineScope.launch { snackbarHostState.showSnackbar("Remote video clip saved") }
                            } else {
                                isRecording = true
                                recordingSeconds = 0
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) GuardianError else Color(0xFF059669))
                    ) {
                        Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Videocam, contentDescription = "Record Video", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(if (isRecording) "Stop Video" else "Record Video", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Camera Vault / Snapshot History
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Camera Snapshots & History", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(10.dp))

                cameraHistory.forEach { item ->
                    BentoCard(modifier = Modifier.padding(bottom = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    imageVector = if (item.type == "photo") Icons.Default.CameraAlt else Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = TrustBluePrimary
                                )
                                Column {
                                    Text("${item.camera} Camera ${item.type.replaceFirstChar { it.uppercase() }}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("${item.detail} • ${item.timestamp}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row {
                                IconButton(onClick = {}) { Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                                IconButton(onClick = { cameraHistory.remove(item) }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GuardianError) }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full Android File Explorer & Storage Inspector
 */
@Composable
fun FilesStorageScreen(
    device: DeviceItem,
    onBack: () -> Unit
) {
    val selectedChildId by MockChildrenRepository.selectedChildId.collectAsState()
    val allFiles by MockFilesRepository.fileItems.collectAsState()
    val categories = MockFilesRepository.categories

    // State
    var currentFolderPath by remember { mutableStateOf("/Internal Storage") }
    var isGridView by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    // Dialog & Action States
    var selectedFileAction by remember { mutableStateOf<FileExplorerItem?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameInputText by remember { mutableStateOf("") }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Filter files for current folder path AND search / category filters
    val currentDisplayFiles = allFiles.filter { item ->
        val inFolder = if (searchQuery.isNotBlank() || selectedCategoryFilter != "All") {
            true // Global search mode when filter or search is active
        } else {
            item.parentFolderPath == currentFolderPath
        }

        val matchesQuery = item.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedCategoryFilter) {
            "Images" -> item.category == "Pictures" || item.category == "Screenshots" || item.extension.lowercase() in listOf("jpg", "jpeg", "png")
            "Videos" -> item.category == "Movies" || item.category == "Screen Recordings" || item.extension.lowercase() in listOf("mp4", "mkv", "avi")
            "Documents" -> item.category == "Documents" || item.extension.lowercase() in listOf("pdf", "doc", "txt")
            "Downloads" -> item.category == "Downloads"
            "APKs" -> item.category == "APK" || item.extension.lowercase() == "apk"
            "WhatsApp" -> item.category == "WhatsApp"
            else -> true
        }

        inFolder && matchesQuery && matchesCategory
    }

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
            // Top App Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = {
                        if (currentFolderPath != "/Internal Storage") {
                            // Navigate up a folder level
                            currentFolderPath = currentFolderPath.substringBeforeLast("/")
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Column {
                        Text(
                            text = "File Explorer",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "${device.name} • Remote Access",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(if (isGridView) Icons.Default.List else Icons.Default.GridView, contentDescription = "Toggle View")
                    }
                }
            }

            // 1. STORAGE OVERVIEW CARD
            BentoCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Internal Storage", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("${device.storageUsedGb} GB Used of ${device.storageTotalGb} GB Total", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusChip(text = "${((device.storageUsedGb.toFloat() / device.storageTotalGb) * 100).toInt()}% Used", type = "info")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Multi-color breakdown progress bar
                    LinearProgressIndicator(
                        progress = { device.storageUsedGb.toFloat() / device.storageTotalGb },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = TrustBluePrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Categories breakdown chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        categories.take(4).forEach { cat ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(cat.name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                Text("${cat.sizeGb} GB", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. BREADCRUMB FOLDER PATH & SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search files across storage...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, contentDescription = "Clear") }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val catFilters = listOf("All", "Images", "Videos", "Documents", "Downloads", "APKs", "WhatsApp")
                items(catFilters) { filter ->
                    FilterChip(
                        selected = selectedCategoryFilter == filter,
                        onClick = { selectedCategoryFilter = filter },
                        label = { Text(filter, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current Folder Path Breadcrumb Navigation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = TrustBluePrimary, modifier = Modifier.size(18.dp))
                        Text(
                            text = currentFolderPath,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TrustBluePrimary)
                        )
                    }

                    if (currentFolderPath != "/Internal Storage") {
                        TextButton(
                            onClick = { currentFolderPath = "/Internal Storage" },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Root", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. FILE EXPLORER GRID / LIST VIEW
            if (currentDisplayFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("This folder is empty or no files match search", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                    }
                }
            } else if (isGridView) {
                // GRID VIEW LAYOUT
                Column {
                    val rows = currentDisplayFiles.chunked(2)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { item ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(bottom = 12.dp)
                                        .clickable {
                                            if (item.isFolder) {
                                                currentFolderPath = "${item.parentFolderPath}/${item.name}"
                                            } else {
                                                selectedFileAction = item
                                            }
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 2.dp,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (item.isFolder) TrustBlueContainer else MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when {
                                                    item.isFolder -> Icons.Default.Folder
                                                    item.iconType == "image" -> Icons.Default.Image
                                                    item.iconType == "video" -> Icons.Default.Movie
                                                    item.iconType == "pdf" -> Icons.Default.PictureAsPdf
                                                    item.iconType == "apk" -> Icons.Default.Android
                                                    item.iconType == "audio" -> Icons.Default.Audiotrack
                                                    else -> Icons.Default.InsertDriveFile
                                                },
                                                contentDescription = null,
                                                tint = if (item.isFolder) TrustBluePrimary else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            textAlign = TextAlign.Center,
                                            maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = if (item.isFolder) "Folder" else item.sizeFormatted,
                                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                        )
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // LIST VIEW LAYOUT
                Column {
                    currentDisplayFiles.forEach { item ->
                        BentoCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            onClick = {
                                if (item.isFolder) {
                                    currentFolderPath = "${item.parentFolderPath}/${item.name}"
                                } else {
                                    selectedFileAction = item
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (item.isFolder) TrustBlueContainer else MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                item.isFolder -> Icons.Default.Folder
                                                item.iconType == "image" -> Icons.Default.Image
                                                item.iconType == "video" -> Icons.Default.Movie
                                                item.iconType == "pdf" -> Icons.Default.PictureAsPdf
                                                item.iconType == "apk" -> Icons.Default.Android
                                                item.iconType == "audio" -> Icons.Default.Audiotrack
                                                else -> Icons.Default.InsertDriveFile
                                            },
                                            contentDescription = null,
                                            tint = if (item.isFolder) TrustBluePrimary else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column {
                                        Text(item.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Text(
                                            text = if (item.isFolder) "Folder" else "${item.sizeFormatted} • ${item.dateModified}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                }

                                IconButton(onClick = { selectedFileAction = item }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Actions", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // FILE ACTION MODAL & DIALOGS
        // ==========================================

        if (selectedFileAction != null && !showRenameDialog && !showPropertiesDialog && !showDeleteConfirmDialog) {
            val file = selectedFileAction!!
            AlertDialog(
                onDismissRequest = { selectedFileAction = null },
                title = { Text(file.name, fontWeight = FontWeight.Bold, maxLines = 1) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Path: ${file.parentFolderPath}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Divider()

                        // Download Button
                        TextButton(
                            onClick = {
                                coroutineScope.launch { snackbarHostState.showSnackbar("Downloading ${file.name} to Parent Vault...") }
                                selectedFileAction = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download to Parent Vault")
                        }

                        // Rename Button
                        TextButton(
                            onClick = {
                                renameInputText = file.name
                                showRenameDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rename File")
                        }

                        // Share Button
                        TextButton(
                            onClick = {
                                coroutineScope.launch { snackbarHostState.showSnackbar("Encrypted file share link generated") }
                                selectedFileAction = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share Encrypted Link")
                        }

                        // Properties Button
                        TextButton(
                            onClick = { showPropertiesDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Properties & Hash")
                        }

                        // Delete Button
                        TextButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = GuardianError)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete File", color = GuardianError)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedFileAction = null }) { Text("Close") }
                }
            )
        }

        // RENAME DIALOG
        if (showRenameDialog && selectedFileAction != null) {
            val file = selectedFileAction!!
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                title = { Text("Rename File", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = renameInputText,
                        onValueChange = { renameInputText = it },
                        label = { Text("File Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (renameInputText.isNotBlank()) {
                                MockFilesRepository.renameFile(file.id, renameInputText)
                                showRenameDialog = false
                                selectedFileAction = null
                                coroutineScope.launch { snackbarHostState.showSnackbar("File renamed to $renameInputText") }
                            }
                        }
                    ) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
                }
            )
        }

        // PROPERTIES INSPECTOR DIALOG
        if (showPropertiesDialog && selectedFileAction != null) {
            val file = selectedFileAction!!
            AlertDialog(
                onDismissRequest = { showPropertiesDialog = false },
                title = { Text("File Properties", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Name: ${file.name}", fontWeight = FontWeight.Bold)
                        Text("Location: ${file.parentFolderPath}/${file.name}")
                        Text("Type: ${if (file.isFolder) "Folder" else file.extension.uppercase()}")
                        Text("Size: ${file.sizeFormatted}")
                        Text("Modified: ${file.dateModified}")
                        Text("Permissions: rw-r--r-- (0644)")
                        Text("Security SHA-256: 8f4a3e...b19c")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showPropertiesDialog = false
                        selectedFileAction = null
                    }) { Text("Close") }
                }
            )
        }

        // DELETE CONFIRM DIALOG
        if (showDeleteConfirmDialog && selectedFileAction != null) {
            val file = selectedFileAction!!
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Delete File?", fontWeight = FontWeight.Bold, color = GuardianError) },
                text = { Text("Are you sure you want to permanently delete '${file.name}' from the child's device?") },
                confirmButton = {
                    Button(
                        onClick = {
                            MockFilesRepository.deleteFile(file.id)
                            showDeleteConfirmDialog = false
                            selectedFileAction = null
                            coroutineScope.launch { snackbarHostState.showSnackbar("File deleted from device") }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GuardianError)
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

/**
 * Calls & SMS Inspector Screen with Full Timeline and AI Analysis
 */
@Composable
fun CallsSmsScreen(
    device: DeviceItem,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Call Logs State
    val callRecords by MockCallsRepository.callRecords.collectAsState()
    var selectedCallFilter by remember { mutableStateOf("All") }
    var callSearchQuery by remember { mutableStateOf("") }
    var activeAudioPlayingCallId by remember { mutableStateOf<String?>(null) }

    // SMS Conversations State
    val smsConversations by MockMessagesRepository.smsConversations.collectAsState()
    var selectedSmsFilter by remember { mutableStateOf("All") }
    var smsSearchQuery by remember { mutableStateOf("") }
    var activeSmsThread by remember { mutableStateOf<SmsConversation?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Filtered Call Records
    val filteredCalls = callRecords.filter { call ->
        val matchesType = when (selectedCallFilter) {
            "Incoming" -> call.callType == "Incoming"
            "Outgoing" -> call.callType == "Outgoing"
            "Missed" -> call.callType == "Missed"
            "Rejected" -> call.callType == "Rejected"
            "Unknown" -> call.callType == "Unknown" || !call.isSafeContact
            else -> true
        }
        val matchesQuery = call.contactName.contains(callSearchQuery, ignoreCase = true) || call.phoneNumber.contains(callSearchQuery)
        matchesType && matchesQuery
    }

    // Filtered SMS Conversations
    val filteredSms = smsConversations.filter { conv ->
        val matchesFilter = when (selectedSmsFilter) {
            "Flagged" -> conv.isFlagged || conv.hasKeywordAlert
            "Unread" -> conv.unreadCount > 0
            else -> true
        }
        val matchesQuery = conv.contactName.contains(smsSearchQuery, ignoreCase = true) || 
                conv.phoneNumber.contains(smsSearchQuery) || 
                conv.lastMessage.contains(smsSearchQuery, ignoreCase = true)
        matchesFilter && matchesQuery
    }

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
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Column {
                    Text(
                        text = "Call & SMS History",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "${device.name} • Live Log Monitored",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            // Tab Selector
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Call History (${callRecords.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("SMS Conversations (${smsConversations.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // TAB 0: CALL HISTORY TIMELINE
            // ==========================================
            if (selectedTab == 0) {
                // Search Input
                OutlinedTextField(
                    value = callSearchQuery,
                    onValueChange = { callSearchQuery = it },
                    placeholder = { Text("Search by name or number...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Call Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filters = listOf("All", "Incoming", "Outgoing", "Missed", "Rejected", "Unknown")
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedCallFilter == filter,
                            onClick = { selectedCallFilter = filter },
                            label = { Text(filter, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredCalls.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No call logs match current filter", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(filteredCalls) { call ->
                            BentoCard(modifier = Modifier.padding(bottom = 12.dp)) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when (call.callType) {
                                                            "Missed" -> GuardianError.copy(alpha = 0.15f)
                                                            "Rejected" -> GuardianError.copy(alpha = 0.15f)
                                                            "Incoming" -> GrowthEmeraldSecondary.copy(alpha = 0.15f)
                                                            else -> TrustBlueContainer
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = when (call.callType) {
                                                        "Incoming" -> Icons.Default.CallReceived
                                                        "Outgoing" -> Icons.Default.CallMade
                                                        "Missed" -> Icons.Default.CallMissed
                                                        "Rejected" -> Icons.Default.Block
                                                        else -> Icons.Default.PhoneCallback
                                                    },
                                                    contentDescription = null,
                                                    tint = when (call.callType) {
                                                        "Missed", "Rejected" -> GuardianError
                                                        "Incoming" -> GrowthEmeraldSecondary
                                                        else -> TrustBluePrimary
                                                    },
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Column {
                                                Text(call.contactName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                                Text("${call.phoneNumber} • ${call.simSlot}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }

                                        StatusChip(
                                            text = call.callType,
                                            type = when (call.callType) {
                                                "Missed", "Rejected" -> "error"
                                                "Incoming" -> "safe"
                                                else -> "info"
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${call.timestamp} • Duration: ${call.durationFormatted}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )

                                        if (call.durationSeconds > 0) {
                                            IconButton(
                                                onClick = {
                                                    activeAudioPlayingCallId = if (activeAudioPlayingCallId == call.id) null else call.id
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar(if (activeAudioPlayingCallId == call.id) "Playing encrypted call recording..." else "Audio paused")
                                                    }
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (activeAudioPlayingCallId == call.id) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                                    contentDescription = "Play Call Audio",
                                                    tint = TrustBluePrimary
                                                )
                                            }
                                        }
                                    }

                                    if (call.note.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TrustBluePrimary, modifier = Modifier.size(14.dp))
                                                Text("AI Note: ${call.note}", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // TAB 1: SMS CONVERSATIONS THREADS
            // ==========================================
            else {
                // Search Input
                OutlinedTextField(
                    value = smsSearchQuery,
                    onValueChange = { smsSearchQuery = it },
                    placeholder = { Text("Search messages or contacts...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // SMS Filters
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedSmsFilter == "All",
                        onClick = { selectedSmsFilter = "All" },
                        label = { Text("All Messages") }
                    )
                    FilterChip(
                        selected = selectedSmsFilter == "Flagged",
                        onClick = { selectedSmsFilter = "Flagged" },
                        label = { Text("Flagged / Keywords") }
                    )
                    FilterChip(
                        selected = selectedSmsFilter == "Unread",
                        onClick = { selectedSmsFilter = "Unread" },
                        label = { Text("Unread") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredSms.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No SMS messages match search criteria", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(filteredSms) { conv ->
                            BentoCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                onClick = { activeSmsThread = conv }
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(if (conv.isFlagged) GuardianError.copy(alpha = 0.2f) else TrustBlueContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = conv.contactName.take(1).uppercase(),
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (conv.isFlagged) GuardianError else TrustBluePrimary
                                                )
                                            }

                                            Column {
                                                Text(conv.contactName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                                Text(conv.phoneNumber, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(conv.lastTimestamp, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                            if (conv.isFlagged) {
                                                StatusChip(text = "High Sensitivity", type = "error")
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = conv.lastMessage,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // SMS THREAD DETAIL DIALOG
        // ==========================================
        if (activeSmsThread != null) {
            val conv = activeSmsThread!!
            AlertDialog(
                onDismissRequest = { activeSmsThread = null },
                title = {
                    Column {
                        Text(conv.contactName, fontWeight = FontWeight.Bold)
                        Text("${conv.phoneNumber} • Full Thread", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (conv.isFlagged || conv.hasKeywordAlert) {
                            Surface(
                                color = GuardianError.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, GuardianError),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = GuardianError)
                                    Text(
                                        "AI Flagged Conversation: High risk keywords and unapproved location meetups detected in thread.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GuardianError
                                    )
                                }
                            }
                        }

                        conv.messages.forEach { msg ->
                            val isMe = msg.isFromChild
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    color = if (msg.isSensitive) GuardianError.copy(alpha = 0.15f) else if (isMe) TrustBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(14.dp),
                                    border = if (msg.isSensitive) BorderStroke(1.dp, GuardianError) else null
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = msg.senderName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (msg.isSensitive) GuardianError else if (isMe) Color.White else MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isMe && !msg.isSensitive) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.timestamp,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = if (isMe && !msg.isSensitive) Color.LightGray else Color.Gray
                                        )
                                        if (msg.sensitivityReason.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Trigger: ${msg.sensitivityReason}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                                color = GuardianError
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeSmsThread = null }) { Text("Close Thread") }
                }
            )
        }
    }
}

/**
 * Automations Screen
 */
@Composable
fun AutomationsScreen(
    onBack: () -> Unit
) {
    val automations = MockAutomationsRepository.automations

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Automations & Rules",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        automations.forEach { auto ->
            BentoCard(modifier = Modifier.padding(bottom = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(auto.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(auto.schedule, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = auto.isEnabled, onCheckedChange = {})
                }
            }
        }
    }
}
