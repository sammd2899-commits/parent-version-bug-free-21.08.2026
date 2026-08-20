package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BentoCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.*

data class MonitoredNotificationItem(
    val id: String,
    val appName: String,
    val senderName: String,
    val time: String,
    val messagePreview: String,
    val riskLevel: String, // "High Risk", "Suspicious", "Safe"
    val icon: ImageVector,
    val iconBgColor: Color
)

@Composable
fun NotificationMonitoringScreen(
    deviceName: String = "Leo's iPad",
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val allNotifications = remember {
        listOf(
            MonitoredNotificationItem(
                id = "n1",
                appName = "Discord",
                senderName = "ShadowUser",
                time = "10:45 AM",
                messagePreview = "Hey, check this link out...",
                riskLevel = "High Risk",
                icon = Icons.Default.SportsEsports,
                iconBgColor = GuardianError
            ),
            MonitoredNotificationItem(
                id = "n2",
                appName = "Instagram",
                senderName = "Stranger",
                time = "09:15 AM",
                messagePreview = "Where do you live?",
                riskLevel = "Suspicious",
                icon = Icons.Default.CameraAlt,
                iconBgColor = Color(0xFFFFB300)
            ),
            MonitoredNotificationItem(
                id = "n3",
                appName = "WhatsApp",
                senderName = "Mom",
                time = "08:30 AM",
                messagePreview = "Dinner is ready",
                riskLevel = "Safe",
                icon = Icons.Default.Chat,
                iconBgColor = GrowthEmeraldSecondary
            ),
            MonitoredNotificationItem(
                id = "n4",
                appName = "YouTube",
                senderName = "YouTube",
                time = "Yesterday",
                messagePreview = "New video from MrBeast",
                riskLevel = "Safe",
                icon = Icons.Default.PlayCircle,
                iconBgColor = TrustBluePrimary
            )
        )
    }

    val filteredNotifications = allNotifications.filter { item ->
        val matchesSearch = item.appName.contains(searchQuery, ignoreCase = true) ||
                item.senderName.contains(searchQuery, ignoreCase = true) ||
                item.messagePreview.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Suspicious" -> item.riskLevel == "High Risk" || item.riskLevel == "Suspicious"
            "Chat" -> item.appName == "WhatsApp" || item.appName == "Discord" || item.appName == "Instagram"
            "Clean" -> item.riskLevel == "Safe"
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Scaffold(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Column {
                        Text(
                            text = "Notification Monitoring",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "$deviceName • Today",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Summary & AI Section Cards (Compact Side-by-Side Layout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Summary Card
                Box(modifier = Modifier.weight(0.4f)) {
                    BentoCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GrowthEmeraldSecondary)
                            )
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GrowthEmeraldSecondary
                                ),
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "42",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )

                        Text(
                            text = "Analyzed Today",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // AI Security Assessment Card
                Box(modifier = Modifier.weight(0.6f)) {
                    BentoCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = TrustBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "AI Assessment",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Patterns safe. 2 Discord alerts flagged for review.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search notifications...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val chips = listOf("All", "Suspicious", "Chat", "Clean")
                items(chips) { chip ->
                    val isSelected = selectedFilter == chip
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = chip },
                        label = { Text(chip, style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = if (chip == "Suspicious") {
                            { Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp), tint = GuardianError) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notifications List
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notifications match current filters",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotifications) { item ->
                        BentoCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(item.iconBgColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = item.iconBgColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.weight(1f, fill = false)
                                        ) {
                                            Text(
                                                text = item.senderName,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "• ${item.appName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Text(
                                            text = item.time,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = item.messagePreview,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                StatusChip(
                                    text = item.riskLevel,
                                    type = when (item.riskLevel) {
                                        "High Risk" -> "error"
                                        "Suspicious" -> "warning"
                                        else -> "safe"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
