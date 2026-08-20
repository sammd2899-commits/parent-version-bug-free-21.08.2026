package com.example.mock

data class ChildItem(
    val id: String,
    val name: String,
    val age: Int,
    val grade: String = "Grade ${(age - 5).coerceAtLeast(1)}",
    val status: String = "Online",
    val deviceName: String = "Pixel 8 Pro",
    val deviceType: String = "Android", // "Android", "iPhone", "iPad", "Tablet"
    val osVersion: String = "Android 14",
    val avatarUrl: String = "",
    val batteryPercent: Int = 86,
    val internetStatus: String = "Connected • Wi-Fi",
    val storageUsedGb: Int = 70,
    val storageTotalGb: Int = 128,
    val lastSync: String = "Just now",
    val screenTimeToday: String = "3h 12m",
    val screenTimeLimit: String = "4h 00m",
    val locationName: String = "Oakridge Elementary • Safe Zone",
    val currentAddress: String = "124 Oakridge Ave, Building B",
    val locationAccuracy: String = "± 5 meters (GPS)",
    val speed: String = "0 km/h • Stationary",
    val alertsCount: Int = 1,
    val isOnline: Boolean = true
)

data class DeviceItem(
    val id: String,
    val name: String,
    val type: String, // "iPhone", "iPad", "Chromebook", "Android"
    val role: String, // "Primary Admin Device", "Monitored Device", "School Device"
    val osVersion: String,
    val status: String, // "Active", "Offline"
    val lastSynced: String,
    val batteryPercent: Int,
    val storageTotalGb: Int,
    val storageUsedGb: Int,
    val cpuTempC: Int,
    val ramUsedGb: Float,
    val ramTotalGb: Float,
    val signalStrength: String // "Excellent", "Good", "Weak"
)

data class AlertItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "AI Detection", "Location Event", "Device Status", "Security"
    val priority: String, // "High", "Medium", "Low"
    val timestamp: String,
    val childOrDevice: String,
    val isRead: Boolean = false
)

data class AppItem(
    val id: String,
    val name: String,
    val category: String, // "Social", "Gaming", "Entertainment", "Education"
    val usageMinutesToday: Int,
    val usageString: String,
    val isBlocked: Boolean = false,
    val limitStatus: String = "Within Limit" // "Within Limit", "75% Used", "Limit Reached"
)

data class ReportSummary(
    val period: String, // "Daily", "Weekly", "Monthly"
    val uptimePercent: Float = 99.8f,
    val avgDailyActiveHours: Float = 3.2f,
    val positivePercent: Int = 85,
    val neutralPercent: Int = 12,
    val riskPercent: Int = 3
)

data class SafeZone(
    val id: String,
    val name: String,
    val radiusMeters: Int,
    val alertOnArrival: Boolean = true,
    val alertOnDeparture: Boolean = true
)

data class LocationHistoryItem(
    val id: String,
    val title: String,
    val locationName: String,
    val timestamp: String,
    val aiTag: String? = null
)

// Legacy compatibility models
data class CallItem(
    val id: String,
    val contactName: String,
    val phoneNumber: String,
    val timestamp: String,
    val duration: String,
    val isIncoming: Boolean,
    val isMissed: Boolean,
    val isSafeContact: Boolean
)

data class MessageItem(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isFlagged: Boolean,
    val flagTag: String? = null
)

// Detailed Call History Model
data class CallRecord(
    val id: String,
    val childId: String,
    val contactName: String,
    val phoneNumber: String,
    val timestamp: String,
    val time: String,
    val dateGroup: String, // "Today", "Yesterday", "Last 7 Days", "Older"
    val durationSeconds: Int,
    val durationFormatted: String,
    val callType: String, // "Incoming", "Outgoing", "Missed", "Rejected", "Unknown"
    val simSlot: String = "SIM 1", // "SIM 1", "SIM 2"
    val isSafeContact: Boolean = true,
    val note: String = ""
)

// Detailed SMS History Models
data class SmsConversation(
    val id: String,
    val childId: String,
    val contactName: String,
    val phoneNumber: String,
    val unreadCount: Int = 0,
    val isFlagged: Boolean = false,
    val hasKeywordAlert: Boolean = false,
    val lastMessage: String,
    val lastTimestamp: String,
    val messages: List<SmsMessageItem> = emptyList()
)

data class SmsMessageItem(
    val id: String,
    val senderName: String,
    val isFromChild: Boolean,
    val text: String,
    val timestamp: String,
    val date: String = "Today",
    val deliveryStatus: String = "Delivered", // "Delivered", "Read", "Sent", "Failed"
    val keywordHighlights: List<String> = emptyList(),
    val isSensitive: Boolean = false,
    val sensitivityReason: String = ""
)

// File Explorer Models
data class FileExplorerItem(
    val id: String,
    val childId: String,
    val name: String,
    val isFolder: Boolean,
    val extension: String = "",
    val sizeBytes: Long = 0,
    val sizeFormatted: String = "",
    val dateModified: String = "",
    val parentFolderPath: String = "/Internal Storage",
    val category: String = "Internal Storage",
    val iconType: String = "folder", // "folder", "image", "video", "doc", "audio", "apk", "zip", "pdf"
    val isFavorite: Boolean = false,
    val isTrash: Boolean = false
)

// Keyword Detection Models
data class MonitoredAppItem(
    val id: String,
    val childId: String,
    val appName: String, // "WhatsApp", "Instagram", "Telegram", "Facebook", "Messenger", "Snapchat", "Discord", "Chrome", "SMS", "Notes"
    val packageName: String,
    val category: String,
    val detectedKeywordsCount: Int,
    val riskBadge: String, // "High Risk", "Medium Risk", "Low Risk", "Safe"
    val lastActivity: String
)

data class KeywordAlertItem(
    val id: String,
    val appId: String,
    val childId: String,
    val keyword: String,
    val date: String,
    val time: String,
    val conversationPreview: String,
    val riskLevel: String, // "Critical", "High", "Medium", "Low"
    val severityColor: String // "red", "orange", "yellow", "green"
)

data class FamilyMember(
    val id: String,
    val name: String,
    val role: String, // "Admin", "Child", "Parent"
    val avatarUrl: String = "",
    val statusText: String
)

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val priceString: String,
    val billingPeriod: String,
    val description: String,
    val features: List<String>,
    val isRecommended: Boolean = false,
    val isCurrent: Boolean = false
)

data class AiInsight(
    val id: String,
    val title: String,
    val content: String,
    val riskLevel: String, // "Low", "Medium", "High"
    val category: String,
    val actionText: String? = null
)

data class StorageCategory(
    val name: String,
    val fileCount: Int,
    val sizeGb: Float
)

data class FileItem(
    val id: String,
    val name: String,
    val category: String,
    val sizeMb: String,
    val dateModified: String
)

data class AutomationItem(
    val id: String,
    val title: String,
    val schedule: String,
    val triggers: List<String>,
    val actions: List<String>,
    val isEnabled: Boolean
)

data class ChatMessage(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val timestamp: String,
    val appBreakdown: List<Pair<String, String>> = emptyList(),
    val locationPreview: String? = null
)

