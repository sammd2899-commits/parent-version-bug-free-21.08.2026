package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mock.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Alert Center Screen
 */
@Composable
fun AlertsCenterScreen(
    onBack: () -> Unit = {}
) {
    val alerts by MockAlertsRepository.alerts.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "AI Detection", "Location Event", "Device Status", "Security")

    val filteredAlerts = if (selectedCategory == "All") alerts else alerts.filter { it.category == selectedCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Alert Center & Notifications",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TrustBluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredAlerts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No alerts in this category", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            filteredAlerts.forEach { alert ->
                BentoCard(modifier = Modifier.padding(bottom = 12.dp)) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when (alert.priority) {
                                        "High" -> GuardianErrorContainer
                                        "Medium" -> Color(0xFFFFF3C4)
                                        else -> GrowthEmeraldContainer.copy(alpha = 0.4f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (alert.priority) {
                                    "High" -> Icons.Default.Warning
                                    "Medium" -> Icons.Default.LocationOn
                                    else -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = when (alert.priority) {
                                    "High" -> OnGuardianErrorContainer
                                    "Medium" -> Color(0xFF7A5900)
                                    else -> OnGrowthEmeraldContainer
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(alert.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text(alert.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(alert.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusChip(text = "${alert.category} • ${alert.priority}", type = if (alert.priority == "High") "error" else "info")

                                TextButton(onClick = { MockAlertsRepository.dismissAlert(alert.id) }) {
                                    Text("Dismiss", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reports & Analytics Screen
 */
@Composable
fun ReportsAnalyticsScreen(
    onBack: () -> Unit = {}
) {
    val report = MockReportsRepository.currentReport
    val apps by MockAppsRepository.apps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Reports & Analytics",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )
        }

        // Export PDF Bar
        BentoCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Weekly Family Safety Report", fontWeight = FontWeight.Bold)
                    Text("PDF Export ready for May 2024", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export PDF", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics Summary Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BentoCard(modifier = Modifier.weight(1f)) {
                Text("Device Uptime", style = MaterialTheme.typography.labelSmall)
                Text("${report.uptimePercent}%", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = GrowthEmeraldSecondary))
            }
            BentoCard(modifier = Modifier.weight(1f)) {
                Text("Avg Active/Day", style = MaterialTheme.typography.labelSmall)
                Text("${report.avgDailyActiveHours} hrs", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = TrustBluePrimary))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Usage Breakdown List
        BentoCard {
            Text("App Usage Breakdown", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(12.dp))

            apps.forEach { app ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(TrustBluePrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(app.name.take(1), fontWeight = FontWeight.Bold, color = TrustBluePrimary)
                        }
                        Column {
                            Text(app.name, fontWeight = FontWeight.Bold)
                            Text(app.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(app.usageString, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        StatusChip(text = app.limitStatus, type = if (app.isBlocked) "error" else "safe")
                    }
                }
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}
