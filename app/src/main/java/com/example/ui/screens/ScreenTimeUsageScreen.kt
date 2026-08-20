package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BentoCard
import com.example.ui.theme.*

data class AppUsageItem(
    val appName: String,
    val timeSpent: String,
    val percentage: Int,
    val icon: ImageVector,
    val accentColor: Color
)

data class AppCategoryItem(
    val categoryName: String,
    val timeSpent: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun ScreenTimeUsageScreen(
    childName: String = "Leo",
    deviceName: String = "Leo's iPad",
    onBack: () -> Unit = {}
) {
    var selectedPeriod by remember { mutableStateOf("Daily") }
    var selectedHourTooltip by remember { mutableStateOf<String?>(null) }

    val categories = remember {
        listOf(
            AppCategoryItem("Social", "1h 12m", Icons.Default.Forum, Color(0xFFAB47BC)),
            AppCategoryItem("Entertainment", "1h 15m", Icons.Default.Movie, Color(0xFF42A5F5)),
            AppCategoryItem("Games", "55m", Icons.Default.SportsEsports, Color(0xFF66BB6A)),
            AppCategoryItem("Education", "20m", Icons.Default.School, Color(0xFFA1887F))
        )
    }

    val mostUsedApps = remember {
        listOf(
            AppUsageItem("YouTube", "1h 15m", 34, Icons.Default.PlayCircle, GuardianError),
            AppUsageItem("Roblox", "55m", 25, Icons.Default.SportsEsports, GrowthEmeraldSecondary),
            AppUsageItem("TikTok", "42m", 19, Icons.Default.MusicNote, TrustBluePrimary),
            AppUsageItem("WhatsApp", "30m", 14, Icons.Default.Chat, GrowthEmeraldSecondary),
            AppUsageItem("Duolingo", "20m", 8, Icons.Default.Translate, Color(0xFFFFB300))
        )
    }

    Scaffold(
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
            // Top App Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Column {
                        Text(
                            text = "Screen Time Usage",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "$deviceName • Today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total Time Summary Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOTAL TIME",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (selectedPeriod == "Daily") "3h 42m" else "24h 15m",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 44.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = GrowthEmeraldContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = OnGrowthEmeraldContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "-12% vs yesterday",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnGrowthEmeraldContainer
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Daily / Weekly Selector Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        listOf("Daily", "Weekly").forEach { period ->
                            val isSelected = selectedPeriod == period
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) TrustBluePrimary else Color.Transparent
                                    )
                                    .clickable { selectedPeriod = period }
                                    .padding(horizontal = 28.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = period,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Usage Chart Card ("Usage Over Time")
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                BentoCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Usage Over Time",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Hourly Bar Graph
                        val barHeights = remember { listOf(0.1f, 0.4f, 0.75f, 0.15f, 0.10f, 0.05f, 0.30f, 0.90f, 0.60f, 0.20f) }
                        val hourLabels = remember { listOf("6a", "8a", "10a", "12p", "2p", "4p", "6p", "8p", "10p", "12a") }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            // Grid Lines & Y-Axis Labels
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf("1h", "30m", "0").forEach { label ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.width(28.dp)
                                        )
                                        Divider(
                                            modifier = Modifier.weight(1f),
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                        )
                                    }
                                }
                            }

                            // Bars Overlay
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 32.dp, bottom = 20.dp, top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                barHeights.forEachIndexed { idx, heightFactor ->
                                    val isPeak = heightFactor >= 0.7f
                                    val hourText = hourLabels.getOrElse(idx) { "" }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.6f)
                                                .fillMaxHeight(heightFactor)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(
                                                    if (isPeak) GrowthEmeraldSecondary else TrustBluePrimary.copy(alpha = 0.6f)
                                                )
                                                .clickable {
                                                    selectedHourTooltip = "$hourText: ${(heightFactor * 60).toInt()}m spent"
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        // X-Axis Labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("6a", "10a", "2p", "6p", "10p").forEach { time ->
                                Text(
                                    text = time,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (selectedHourTooltip != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = TrustBlueContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = selectedHourTooltip!!,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TrustBluePrimary),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // By Category Section (Horizontal Scrollable)
            Column {
                Text(
                    text = "By Category",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { cat ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.width(150.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(cat.accentColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        tint = cat.accentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = cat.categoryName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )

                                Text(
                                    text = cat.timeSpent,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Most Used Apps Card List
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                BentoCard {
                    Column {
                        Text(
                            text = "Most Used Apps",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        mostUsedApps.forEachIndexed { index, app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(app.accentColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = app.icon,
                                        contentDescription = null,
                                        tint = app.accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = app.appName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${app.percentage}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = app.timeSpent,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    LinearProgressIndicator(
                                        progress = app.percentage / 100f,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = app.accentColor,
                                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                    )
                                }
                            }

                            if (index < mostUsedApps.lastIndex) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
