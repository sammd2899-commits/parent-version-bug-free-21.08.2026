package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mock.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Main Family Group & Child Profiles Screen
 */
@Composable
fun FamilyGroupScreen(
    onNavigateToChildProfile: (ChildItem) -> Unit = {},
    onNavigateToSocialContent: () -> Unit = {},
    onNavigateToAiCenter: () -> Unit = {}
) {
    val familyMembers = MockFamilyRepository.familyMembers
    val children by MockChildrenRepository.children.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(
            title = "Children Profiles",
            subtitle = "Detailed settings, app limits & emergency contacts"
        )

        Spacer(modifier = Modifier.height(12.dp))

        children.forEach { child ->
            BentoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                onClick = { onNavigateToChildProfile(child) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(TrustBluePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = child.name.take(1),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TrustBluePrimary
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${child.name} (${child.age} yrs)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            StatusChip(text = child.status, type = if (child.status == "Online" || child.status == "At Home") "safe" else "info")
                        }

                        Text(
                            text = "${child.grade} • Device: ${child.deviceName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Screen Time: ${child.screenTimeToday}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "Location: ${child.locationName}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Social Content & Sentiment Analysis Screen
 */
@Composable
fun SocialContentScreen(
    onBack: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val flaggedInteractions = remember {
        listOf(
            SocialFlaggedItem(
                id = "sf1",
                sender = "Jason S.",
                sourceApp = "Instagram DM",
                time = "10:42 AM",
                date = "2026-08-07",
                messagePreview = "Hey let's meet at the abandoned lot after school. Don't tell anyone...",
                flagType = "High Secrecy Flag",
                riskLevel = "High",
                appIcon = Icons.Default.CameraAlt,
                accentColor = GuardianError
            ),
            SocialFlaggedItem(
                id = "sf2",
                sender = "GamerX_99",
                sourceApp = "Discord",
                time = "08:15 PM",
                date = "2026-08-06",
                messagePreview = "Send me your password and I'll give you free Robux codes...",
                flagType = "Phishing / Scam Alert",
                riskLevel = "Critical",
                appIcon = Icons.Default.SportsEsports,
                accentColor = GuardianError
            ),
            SocialFlaggedItem(
                id = "sf3",
                sender = "Class Chat Group",
                sourceApp = "WhatsApp Group",
                time = "03:30 PM",
                date = "2026-08-06",
                messagePreview = "Nobody likes you at school today, why do you even come...",
                flagType = "Bullying & Harassment",
                riskLevel = "Medium",
                appIcon = Icons.Default.Chat,
                accentColor = Color(0xFFFFB300)
            )
        )
    }

    val filteredList = flaggedInteractions.filter { item ->
        when (selectedFilter) {
            "High Risk" -> item.riskLevel == "Critical" || item.riskLevel == "High"
            "Secrecy Flags" -> item.flagType.contains("Secrecy", ignoreCase = true)
            "Bullying Flags" -> item.flagType.contains("Bullying", ignoreCase = true)
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text(
                    text = "Social Media Safety Monitor",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "AI Sentiment Analysis & Chat Scanner",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Hero Score Card
        BentoCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Overall Social Safety Score",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "348 messages scanned today across 4 platforms",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GrowthEmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "84%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = OnGrowthEmeraldContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "348",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TrustBluePrimary
                            )
                        )
                        Text("Scanned Today", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Divider(modifier = Modifier.height(36.dp).width(1.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "3",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GuardianError
                            )
                        )
                        Text("Flagged Alerts", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Divider(modifier = Modifier.height(36.dp).width(1.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "4",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GrowthEmeraldSecondary
                            )
                        )
                        Text("Monitored Apps", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        Text("Flagged Chat Interactions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "High Risk", "Secrecy Flags", "Bullying Flags").forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Flagged Items List
        filteredList.forEach { item ->
            BentoCard(modifier = Modifier.padding(bottom = 12.dp)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(item.accentColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.appIcon,
                                    contentDescription = null,
                                    tint = item.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.sender,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${item.sourceApp} • ${item.time}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        StatusChip(
                            text = item.flagType,
                            type = if (item.riskLevel == "Critical" || item.riskLevel == "High") "error" else "warning"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${item.messagePreview}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TrustBluePrimary, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Triggered NLP sentiment scanner on ${item.date}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                        TextButton(
                            onClick = {},
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("Review Conversation", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

private data class SocialFlaggedItem(
    val id: String,
    val sender: String,
    val sourceApp: String,
    val time: String,
    val date: String,
    val messagePreview: String,
    val flagType: String,
    val riskLevel: String,
    val appIcon: ImageVector,
    val accentColor: Color
)

/**
 * AI Center Dashboard Screen
 */
@Composable
fun AiCenterScreen(
    onBack: () -> Unit = {}
) {
    val insights = MockAiRepository.insights

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
                text = "AI Safety & Behavior Center",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )
        }

        BentoCard {
            Text("Sleep & Routine Analysis", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Average nightly rest: 8 hours 15 minutes. No late night unlocked device sessions recorded this week.", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("AI Safety Recommendations", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))

        insights.forEach { insight ->
            BentoCard(modifier = Modifier.padding(bottom = 10.dp)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(insight.title, fontWeight = FontWeight.Bold)
                        StatusChip(text = insight.category, type = if (insight.category == "Positive") "safe" else "info")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(insight.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (insight.actionText != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = TrustBluePrimary), shape = CircleShape) {
                            Text(insight.actionText, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
