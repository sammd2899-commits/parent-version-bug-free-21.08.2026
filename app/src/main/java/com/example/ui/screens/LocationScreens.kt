package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mock.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Completely Redesigned LOCATION Screen
 * Map occupies ~75% screen height with Google Maps style vector canvas, floating chips, FABs & floating bottom sheet.
 */
@Composable
fun LocationMainScreen(
    onNavigateToContentFiltering: () -> Unit = {}
) {
    val children by MockChildrenRepository.children.collectAsState()
    val selectedChildId by MockChildrenRepository.selectedChildId.collectAsState()
    val activeChild = children.find { it.id == selectedChildId } ?: children.firstOrNull() ?: ChildItem(
        id = "c1", name = "Alex", age = 8, deviceName = "Pixel 8 Pro"
    )

    var mapType by remember { mutableStateOf("Default") } // Default, Satellite, Terrain
    var showRouteHistory by remember { mutableStateOf(true) }
    var showSafeZones by remember { mutableStateOf(true) }
    var activeSheetTab by remember { mutableStateOf("Details") } // Details, History, SafeZones
    var isRefreshing by remember { mutableStateOf(false) }

    val safeZones = MockLocationRepository.safeZones
    val locationHistory = MockLocationRepository.locationHistory

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // MAP VIEW CONTAINER (OCCUPIES ~75% OF SCREEN HEIGHT)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.75f)
                    .background(
                        when (mapType) {
                            "Satellite" -> Color(0xFF1A2332)
                            "Terrain" -> Color(0xFFE8ECE9)
                            else -> Color(0xFFF1F5F9) // Light Google Maps canvas
                        }
                    )
            ) {
                // VECTOR GOOGLE MAPS STYLE CANVAS
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Background Park/Water shapes
                    val parkColor = if (mapType == "Satellite") Color(0xFF1E3A2B) else Color(0xFFE2F0D9)
                    val waterColor = if (mapType == "Satellite") Color(0xFF153350) else Color(0xFFC5E1A5).copy(alpha = 0.6f)

                    drawCircle(color = parkColor, radius = w * 0.35f, center = Offset(w * 0.2f, h * 0.3f))
                    drawCircle(color = waterColor, radius = w * 0.25f, center = Offset(w * 0.85f, h * 0.2f))

                    // Road Grid Lines
                    val roadColor = if (mapType == "Satellite") Color(0xFF334155) else Color.White
                    val majorRoadColor = if (mapType == "Satellite") Color(0xFF475569) else Color(0xFFFFE0B2)

                    // Secondary Roads
                    drawLine(color = roadColor, start = Offset(0f, h * 0.25f), end = Offset(w, h * 0.25f), strokeWidth = 12f)
                    drawLine(color = roadColor, start = Offset(0f, h * 0.65f), end = Offset(w, h * 0.65f), strokeWidth = 12f)
                    drawLine(color = roadColor, start = Offset(w * 0.2f, 0f), end = Offset(w * 0.2f, h), strokeWidth = 12f)
                    drawLine(color = roadColor, start = Offset(w * 0.8f, 0f), end = Offset(w * 0.8f, h), strokeWidth = 12f)

                    // Major Expressway / Boulevard
                    drawLine(color = majorRoadColor, start = Offset(0f, h * 0.45f), end = Offset(w, h * 0.45f), strokeWidth = 22f)
                    drawLine(color = majorRoadColor, start = Offset(w * 0.5f, 0f), end = Offset(w * 0.5f, h), strokeWidth = 22f)

                    // Safe Zone Translucent Circle (Geofence)
                    if (showSafeZones) {
                        drawCircle(
                            color = GrowthEmeraldSecondary.copy(alpha = 0.2f),
                            radius = 160f,
                            center = Offset(w * 0.5f, h * 0.45f)
                        )
                        drawCircle(
                            color = GrowthEmeraldSecondary,
                            radius = 160f,
                            center = Offset(w * 0.5f, h * 0.45f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                            )
                        )
                    }

                    // Route History Polyline
                    if (showRouteHistory) {
                        val pathPoints = listOf(
                            Offset(w * 0.2f, h * 0.65f),
                            Offset(w * 0.2f, h * 0.45f),
                            Offset(w * 0.5f, h * 0.45f)
                        )
                        for (i in 0 until pathPoints.size - 1) {
                            drawLine(
                                color = TrustBluePrimary,
                                start = pathPoints[i],
                                end = pathPoints[i + 1],
                                strokeWidth = 10f,
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
                            )
                        }
                    }
                }

                // CHILD AVATAR MAP PIN (CENTER OVERLAY)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-20).dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = TrustBluePrimary,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeChild.name.take(1),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TrustBluePrimary
                                        )
                                    )
                                }
                                Text(
                                    text = "${activeChild.name} • ${activeChild.lastSync}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Child Location Pin",
                            tint = TrustBluePrimary,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                // TOP OVERLAY BAR & FLOATING CHIPS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Top Bar: Active Child Selector, Battery, Refresh
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TrustBluePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = activeChild.name.take(1),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }

                                Column {
                                    Text(
                                        text = activeChild.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = activeChild.deviceName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = CircleShape,
                                    color = GrowthEmeraldContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = GrowthEmeraldSecondary, modifier = Modifier.size(14.dp))
                                        Text("${activeChild.batteryPercent}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = OnGrowthEmeraldContainer))
                                    }
                                }

                                IconButton(
                                    onClick = { isRefreshing = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh GPS", tint = TrustBluePrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // TOP FLOATING CHIPS ROW
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = showRouteHistory,
                                onClick = { showRouteHistory = !showRouteHistory },
                                label = { Text("Route History") },
                                leadingIcon = { Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }

                        item {
                            FilterChip(
                                selected = showSafeZones,
                                onClick = { showSafeZones = !showSafeZones },
                                label = { Text("Geofence") },
                                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }

                        item {
                            AssistChip(
                                onClick = {
                                    mapType = when (mapType) {
                                        "Default" -> "Satellite"
                                        "Satellite" -> "Terrain"
                                        else -> "Default"
                                    }
                                },
                                label = { Text("Map: $mapType") },
                                leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }

                        item {
                            AssistChip(
                                onClick = {
                                    val childList = children
                                    val nextIndex = (childList.indexOfFirst { it.id == activeChild.id } + 1) % childList.size
                                    MockChildrenRepository.selectChild(childList[nextIndex].id)
                                },
                                label = { Text("Device Switch") },
                                leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }

                // FLOATING ACTION BUTTONS (FAB) RIGHT SIDE
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FloatingActionButton(
                        onClick = { /* Locate Me */ },
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = TrustBluePrimary,
                        modifier = Modifier
                            .semantics { testTag = "locate_me_fab" }
                            .size(48.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Locate Me")
                    }

                    FloatingActionButton(
                        onClick = { activeSheetTab = "SafeZones" },
                        containerColor = GrowthEmeraldContainer,
                        contentColor = OnGrowthEmeraldContainer,
                        modifier = Modifier
                            .semantics { testTag = "create_safe_zone_fab" }
                            .size(48.dp)
                    ) {
                        Icon(Icons.Default.AddLocationAlt, contentDescription = "Create Safe Zone")
                    }
                }
            }

            // BOTTOM FLOATING SHEET (OCCUPIES ~25% OF SCREEN HEIGHT)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
                    .semantics { testTag = "location_bottom_sheet" },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Sheet Drag Indicator Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Address Header & Real-time Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeChild.locationName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = activeChild.currentAddress,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                maxLines = 1
                            )
                        }

                        StatusChip(text = "In Safe Zone", type = "safe")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Location Technical Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.GpsFixed, contentDescription = null, tint = TrustBluePrimary, modifier = Modifier.size(14.dp))
                            Text(activeChild.locationAccuracy, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Text(activeChild.speed, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = GrowthEmeraldSecondary, modifier = Modifier.size(14.dp))
                            Text("${activeChild.batteryPercent}% • Active", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons: Navigate, Refresh, History
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Navigate", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Refresh", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = { activeSheetTab = if (activeSheetTab == "History") "Details" else "History" },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("History", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
