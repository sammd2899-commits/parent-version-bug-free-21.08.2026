package com.example.mock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Isolated Mock Repositories prepared for future backend swap

object MockChildrenRepository {
    private val _children = MutableStateFlow(
        listOf(
            ChildItem(
                id = "c1",
                name = "Alex",
                age = 8,
                grade = "Grade 3",
                status = "Online",
                deviceName = "Pixel 8 Pro",
                deviceType = "Android",
                batteryPercent = 86,
                internetStatus = "Connected • Wi-Fi",
                lastSync = "Just now",
                screenTimeToday = "3h 12m",
                locationName = "Oakridge Elementary • Safe Zone",
                currentAddress = "124 Oakridge Ave, Building B",
                locationAccuracy = "± 5 meters",
                speed = "0 km/h • Stationary",
                alertsCount = 1,
                isOnline = true
            ),
            ChildItem(
                id = "c2",
                name = "Emma",
                age = 12,
                grade = "Grade 7",
                status = "At School",
                deviceName = "iPhone 15",
                deviceType = "iPhone",
                batteryPercent = 65,
                internetStatus = "5G Active",
                lastSync = "2 mins ago",
                screenTimeToday = "2h 05m",
                locationName = "Lincoln High School",
                currentAddress = "789 Lincoln Blvd, Campus East",
                locationAccuracy = "± 8 meters",
                speed = "0 km/h",
                alertsCount = 0,
                isOnline = true
            ),
            ChildItem(
                id = "c3",
                name = "Liam",
                age = 10,
                grade = "Grade 5",
                status = "At Home",
                deviceName = "Galaxy Tab S9",
                deviceType = "Android",
                batteryPercent = 15,
                internetStatus = "Connected • Wi-Fi",
                lastSync = "5 mins ago",
                screenTimeToday = "1h 20m",
                locationName = "Home • Safe Zone",
                currentAddress = "456 Elm Street, Springfield",
                locationAccuracy = "± 3 meters",
                speed = "0 km/h",
                alertsCount = 1,
                isOnline = true
            ),
            ChildItem(
                id = "c4",
                name = "Chloe",
                age = 10,
                grade = "Grade 5",
                status = "Offline",
                deviceName = "iPad Pro",
                deviceType = "iPad",
                batteryPercent = 92,
                internetStatus = "Offline",
                lastSync = "1 hr ago",
                screenTimeToday = "45m",
                locationName = "Home",
                currentAddress = "456 Elm Street, Springfield",
                locationAccuracy = "± 12 meters",
                speed = "0 km/h",
                alertsCount = 0,
                isOnline = false
            )
        )
    )
    val children: StateFlow<List<ChildItem>> = _children.asStateFlow()

    private val _selectedChildId = MutableStateFlow("c1")
    val selectedChildId: StateFlow<String> = _selectedChildId.asStateFlow()

    fun selectChild(id: String) {
        if (_children.value.any { it.id == id }) {
            _selectedChildId.value = id
        }
    }

    fun getSelectedChild(): ChildItem {
        return _children.value.find { it.id == _selectedChildId.value } ?: _children.value.first()
    }

    fun addDevice(deviceName: String, childName: String, deviceType: String) {
        val newId = "c_${System.currentTimeMillis()}"
        val newItem = ChildItem(
            id = newId,
            name = childName,
            age = 10,
            deviceName = deviceName,
            deviceType = deviceType,
            status = "Online",
            batteryPercent = 100,
            lastSync = "Just now",
            isOnline = true
        )
        _children.value = _children.value + newItem
        _selectedChildId.value = newId
    }

    fun removeDevice(id: String) {
        val updated = _children.value.filter { it.id != id }
        if (updated.isNotEmpty()) {
            _children.value = updated
            if (_selectedChildId.value == id) {
                _selectedChildId.value = updated.first().id
            }
        }
    }

    fun renameDevice(id: String, newDeviceName: String) {
        _children.value = _children.value.map {
            if (it.id == id) it.copy(deviceName = newDeviceName) else it
        }
    }
}

object MockDevicesRepository {
    private val _devices = MutableStateFlow(
        listOf(
            DeviceItem("d1", "Parent's iPhone 14", "iPhone", "Primary Admin Device", "iOS 17.2", "Active", "Just now", 86, 128, 63, 32, 4.2f, 8.0f, "Excellent"),
            DeviceItem("d2", "Leo's iPad", "iPad", "Monitored Device", "iPadOS 16.5", "Active", "5 mins ago", 85, 128, 84, 34, 3.8f, 8.0f, "Good"),
            DeviceItem("d3", "Mia's Chromebook", "Chromebook", "School Device", "ChromeOS 118", "Offline", "Yesterday, 4:30 PM", 40, 64, 45, 30, 2.5f, 4.0f, "Weak"),
            DeviceItem("d4", "Alex's Phone", "Android", "Monitored Device", "Android 14", "Active", "Just now", 84, 128, 70, 31, 3.2f, 6.0f, "Excellent"),
            DeviceItem("d5", "Chloe's iPad Pro", "iPad", "Monitored Device", "iPadOS 17.1", "Active", "Just now", 92, 256, 110, 33, 4.0f, 8.0f, "Excellent")
        )
    )
    val devices: StateFlow<List<DeviceItem>> = _devices.asStateFlow()
}

object MockAlertsRepository {
    private val _alerts = MutableStateFlow(
        listOf(
            AlertItem("a1", "Unusual Activity Detected", "AI flagged potentially sensitive keywords in recent chat application usage on Device: Tablet 1.", "AI Detection", "High", "Just now", "Chloe's iPad Pro"),
            AlertItem("a2", "Emma Arrived at School", "Emma's phone entered the designated Safe Zone: 'Lincoln High School'.", "Location Event", "Medium", "10 mins ago", "Emma's Phone"),
            AlertItem("a3", "Low Battery Alert", "Liam's Phone battery is at 15% and dropping quickly.", "Device Status", "Low", "1 hr ago", "Liam's Phone"),
            AlertItem("a4", "Weekly Security Summary", "Your weekly SentryKids AI summary is ready. 0 critical issues found.", "Security", "Low", "Yesterday", "Family System"),
            AlertItem("a5", "Unrecognized App Installed", "SentryKids AI detected 'SneakyChat' was installed on Leo's iPad. This app is flagged for security risks.", "AI Detection", "High", "2 mins ago", "Leo's iPad")
        )
    )
    val alerts: StateFlow<List<AlertItem>> = _alerts.asStateFlow()

    fun dismissAlert(id: String) {
        _alerts.value = _alerts.value.filter { it.id != id }
    }
}

object MockAppsRepository {
    private val _apps = MutableStateFlow(
        listOf(
            AppItem("app1", "WhatsApp", "Social", 135, "2h 15m", isBlocked = false, limitStatus = "Within Limit"),
            AppItem("app2", "Instagram", "Social", 0, "0m", isBlocked = true, limitStatus = "Blocked"),
            AppItem("app3", "YouTube Kids", "Entertainment", 45, "45m", isBlocked = false, limitStatus = "Within Limit"),
            AppItem("app4", "Reading Eggs", "Education", 30, "30m", isBlocked = false, limitStatus = "Within Limit"),
            AppItem("app5", "Roblox", "Gaming", 255, "4h 15m", isBlocked = false, limitStatus = "Limit Reached"),
            AppItem("app6", "Duolingo", "Education", 80, "1h 20m", isBlocked = false, limitStatus = "Within Limit"),
            AppItem("app7", "Chrome", "Browser", 20, "20m", isBlocked = false, limitStatus = "Safe Search On")
        )
    )
    val apps: StateFlow<List<AppItem>> = _apps.asStateFlow()

    fun toggleAppBlock(id: String) {
        _apps.value = _apps.value.map {
            if (it.id == id) it.copy(isBlocked = !it.isBlocked) else it
        }
    }
}

object MockReportsRepository {
    val currentReport = ReportSummary("Weekly", uptimePercent = 99.8f, avgDailyActiveHours = 3.2f)
}

object MockLocationRepository {
    val safeZones = listOf(
        SafeZone("sz1", "Home", 150),
        SafeZone("sz2", "School", 200),
        SafeZone("sz3", "City Park", 300)
    )

    val locationHistory = listOf(
        LocationHistoryItem("lh1", "Arrived at School", "Oakridge Elementary", "08:15 AM", "On time arrival"),
        LocationHistoryItem("lh2", "Left Home", "123 Elm Street", "07:50 AM"),
        LocationHistoryItem("lh3", "Arrived at Home", "123 Elm Street", "Yesterday, 3:30 PM")
    )

    val detectedKeywords = listOf(
        Pair("Bullying", "Detected 4 times • SMS & Search"),
        Pair("Depression", "Detected 1 time • Educational article")
    )
}

object MockCallsRepository {
    val callsList = listOf(
        CallItem("call1", "Mom (Home)", "555-0102", "Today, 2:45 PM", "12 mins", isIncoming = true, isMissed = false, isSafeContact = true),
        CallItem("call2", "(555) 123-4567", "555-123-4567", "Yesterday, 8:15 PM", "2 mins", isIncoming = false, isMissed = false, isSafeContact = false),
        CallItem("call3", "Alex (School)", "555-0105", "Yesterday, 3:30 PM", "0 mins", isIncoming = true, isMissed = true, isSafeContact = true)
    )

    private val _callRecords = MutableStateFlow(
        listOf(
            CallRecord("cr1", "c1", "Mom (Sarah)", "+1 (555) 012-3456", "Today, 2:45 PM", "2:45 PM", "Today", 860, "14m 20s", "Incoming", "SIM 1", true, "Discussed afternoon pickup"),
            CallRecord("cr2", "c1", "Unsaved Number", "+1 (555) 987-6543", "Today, 11:15 AM", "11:15 AM", "Today", 0, "0s (Missed)", "Missed", "SIM 1", false, "Flagged as Unknown Caller by AI"),
            CallRecord("cr3", "c1", "Dad (Mark)", "+1 (555) 019-8765", "Today, 09:30 AM", "09:30 AM", "Today", 185, "3m 05s", "Outgoing", "SIM 1", true, "Asked about science project"),
            CallRecord("cr4", "c1", "Spam / Robocall", "+1 (800) 555-9999", "Yesterday, 6:10 PM", "6:10 PM", "Yesterday", 0, "0s (Rejected)", "Rejected", "SIM 2", false, "Auto-blocked spam number"),
            CallRecord("cr5", "c1", "Coach David", "+1 (555) 444-3322", "Yesterday, 4:20 PM", "4:20 PM", "Yesterday", 420, "7m 00s", "Incoming", "SIM 1", true, "Soccer practice update"),
            CallRecord("cr6", "c1", "Unknown Contact", "+1 (555) 222-1100", "2026-08-04", "1:15 PM", "Last 7 Days", 15, "15s", "Unknown", "SIM 1", false, "Short unknown duration call")
        )
    )
    val callRecords: StateFlow<List<CallRecord>> = _callRecords.asStateFlow()
}

object MockMessagesRepository {
    val smsList = listOf(
        MessageItem("msg1", "Jason S.", "Hey, let's meet at the abandoned lot after school. Don't tell anyone you're coming.", "10:42 AM", isFlagged = true, flagTag = "High Sensitivity"),
        MessageItem("msg2", "Sarah (Coach)", "Practice is moved to 4 PM today due to the rain. Please make sure to bring your indoor shoes.", "Yesterday", isFlagged = false)
    )

    private val _smsConversations = MutableStateFlow(
        listOf(
            SmsConversation(
                id = "sms_conv_1",
                childId = "c1",
                contactName = "Jason S.",
                phoneNumber = "+1 (555) 321-7654",
                unreadCount = 2,
                isFlagged = true,
                hasKeywordAlert = true,
                lastMessage = "Hey, let's meet at the abandoned lot after school. Don't tell anyone you're coming.",
                lastTimestamp = "10:42 AM",
                messages = listOf(
                    SmsMessageItem("m1", "Jason S.", false, "Hey Alex, you got the math homework answers?", "Yesterday, 4:10 PM"),
                    SmsMessageItem("m2", "Alex", true, "Yeah page 42 questions 1 to 5.", "Yesterday, 4:12 PM"),
                    SmsMessageItem("m3", "Jason S.", false, "Cool thanks! By the way, are you going to that secret party?", "Yesterday, 8:30 PM", keywordHighlights = listOf("secret party"), isSensitive = true, sensitivityReason = "Unsupervised event reference"),
                    SmsMessageItem("m4", "Jason S.", false, "Hey, let's meet at the abandoned lot after school. Don't tell anyone you're coming.", "Today, 10:42 AM", keywordHighlights = listOf("abandoned lot", "Don't tell anyone"), isSensitive = true, sensitivityReason = "High risk location & secret behavior")
                )
            ),
            SmsConversation(
                id = "sms_conv_2",
                childId = "c1",
                contactName = "Mom (Sarah)",
                phoneNumber = "+1 (555) 012-3456",
                unreadCount = 0,
                isFlagged = false,
                hasKeywordAlert = false,
                lastMessage = "Don't forget to grab your lunch box before leaving school!",
                lastTimestamp = "Yesterday, 3:15 PM",
                messages = listOf(
                    SmsMessageItem("m201", "Mom", false, "Have a great day at school Alex!", "Yesterday, 7:45 AM"),
                    SmsMessageItem("m202", "Alex", true, "Thanks Mom, see you later!", "Yesterday, 7:46 AM"),
                    SmsMessageItem("m203", "Mom", false, "Don't forget to grab your lunch box before leaving school!", "Yesterday, 3:15 PM")
                )
            ),
            SmsConversation(
                id = "sms_conv_3",
                childId = "c1",
                contactName = "Coach David",
                phoneNumber = "+1 (555) 444-3322",
                unreadCount = 0,
                isFlagged = false,
                hasKeywordAlert = false,
                lastMessage = "Practice schedule updated on the team portal.",
                lastTimestamp = "2026-08-04",
                messages = listOf(
                    SmsMessageItem("m301", "Coach David", false, "Practice schedule updated on the team portal.", "2026-08-04")
                )
            )
        )
    )
    val smsConversations: StateFlow<List<SmsConversation>> = _smsConversations.asStateFlow()
}

object MockKeywordsRepository {
    private val _monitoredApps = MutableStateFlow(
        listOf(
            MonitoredAppItem("ma1", "c1", "WhatsApp", "com.whatsapp", "Social", 4, "High Risk", "10 mins ago"),
            MonitoredAppItem("ma2", "c1", "Instagram", "com.instagram.android", "Social", 2, "Medium Risk", "35 mins ago"),
            MonitoredAppItem("ma3", "c1", "Telegram", "org.telegram.messenger", "Messaging", 1, "Medium Risk", "1 hr ago"),
            MonitoredAppItem("ma4", "c1", "Snapchat", "com.snapchat.android", "Social", 3, "High Risk", "2 hrs ago"),
            MonitoredAppItem("ma5", "c1", "Discord", "com.discord", "Gaming Chat", 0, "Safe", "Yesterday"),
            MonitoredAppItem("ma6", "c1", "Chrome", "com.android.chrome", "Browser Search", 2, "Medium Risk", "Yesterday"),
            MonitoredAppItem("ma7", "c1", "SMS Messages", "com.google.android.apps.messaging", "System SMS", 3, "High Risk", "10:42 AM"),
            MonitoredAppItem("ma8", "c1", "Notes", "com.google.android.keep", "Productivity", 0, "Safe", "3 days ago")
        )
    )
    val monitoredApps: StateFlow<List<MonitoredAppItem>> = _monitoredApps.asStateFlow()

    private val _keywordAlerts = MutableStateFlow(
        listOf(
            KeywordAlertItem("ka1", "ma1", "c1", "abandoned lot", "2026-08-07", "10:42 AM", "meet at the abandoned lot after school...", "Critical", "red"),
            KeywordAlertItem("ka2", "ma1", "c1", "Don't tell anyone", "2026-08-07", "10:42 AM", "Don't tell anyone you're coming...", "High", "red"),
            KeywordAlertItem("ka3", "ma2", "c1", "secret party", "2026-08-06", "08:30 PM", "are you going to that secret party tonight?", "Medium", "orange"),
            KeywordAlertItem("ka4", "ma4", "c1", "hate school", "2026-08-06", "04:15 PM", "I really hate school so much today...", "Low", "yellow"),
            KeywordAlertItem("ka5", "ma6", "c1", "cheat code bypass", "2026-08-05", "06:10 PM", "search query: cheat code bypass restriction...", "Medium", "orange")
        )
    )
    val keywordAlerts: StateFlow<List<KeywordAlertItem>> = _keywordAlerts.asStateFlow()
}

object MockFamilyRepository {
    val familyMembers = listOf(
        FamilyMember("fm1", "Alex", "Admin", statusText = "Online"),
        FamilyMember("fm2", "Mia", "Child", statusText = "At School"),
        FamilyMember("fm3", "Leo", "Child", statusText = "Online")
    )
}

object MockSubscriptionRepository {
    val plans = listOf(
        SubscriptionPlan("p1", "Basic", "Free", "/forever", "Essential monitoring for a single device.", listOf("1 Device Limit", "Basic Activity Logs (24h)"), isCurrent = true),
        SubscriptionPlan("p2", "Pro", "$9.99", "/mo", "Advanced AI insights and extended history.", listOf("Up to 5 Devices", "Real-time AI Alerts", "30-Day History"), isRecommended = true),
        SubscriptionPlan("p3", "Family", "$14.99", "/mo", "Complete coverage for the whole household.", listOf("Unlimited Devices", "Priority Support", "Custom AI Rules"))
    )
}

object MockAiRepository {
    val insights = listOf(
        AiInsight("ai1", "Routine established successfully", "Consistent safe zone arrivals all week.", "Low", "Positive"),
        AiInsight("ai2", "Slight delay in afternoon transit", "Average 10 min longer return time.", "Medium", "Neutral"),
        AiInsight("ai3", "Shift Bedtime Routine", "Moving device downtime 30 minutes earlier could improve sleep latency.", "Low", "Suggested", actionText = "Apply Rule")
    )

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage("m1", false, "Good morning! I'm monitoring Timmy's devices and location. Everything looks secure. How can I help you today?", "9:41 AM"),
            ChatMessage("m2", true, "How is Timmy's screen time today?", "9:42 AM"),
            ChatMessage("m3", false, "Timmy has used his iPad for 1 hour and 15 minutes so far today. This is 30 minutes less than his average for a weekday morning.", "9:42 AM", appBreakdown = listOf("YouTube Kids" to "45m", "Reading Eggs" to "30m")),
            ChatMessage("m4", true, "Check location", "9:43 AM"),
            ChatMessage("m5", false, "Timmy is at School in the Safe Zone.", "9:43 AM", locationPreview = "School District - Safe Zone Active")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun sendMessage(userText: String) {
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(ChatMessage("user_${System.currentTimeMillis()}", true, userText, "Just now"))
        
        val aiReply = when {
            userText.contains("alert", ignoreCase = true) -> ChatMessage("ai_${System.currentTimeMillis()}", false, "No critical safety alerts detected in the past 24 hours.", "Just now")
            userText.contains("pause", ignoreCase = true) -> ChatMessage("ai_${System.currentTimeMillis()}", false, "Internet paused on Timmy's iPad for 30 minutes.", "Just now")
            else -> ChatMessage("ai_${System.currentTimeMillis()}", false, "I've logged your request. All child devices remain in protected status.", "Just now")
        }
        currentList.add(aiReply)
        _chatMessages.value = currentList
    }
}

object MockFilesRepository {
    val categories = listOf(
        StorageCategory("Images", 2401, 48.5f),
        StorageCategory("Videos", 142, 27.2f),
        StorageCategory("Documents", 89, 16.3f),
        StorageCategory("Downloads", 45, 8.1f),
        StorageCategory("APKs", 12, 4.2f),
        StorageCategory("Audio", 304, 4.7f)
    )

    val recentFiles = listOf(
        FileItem("f1", "Screenshot_20240512_1422.png", "Images", "2.4 MB", "Today, 2:22 PM"),
        FileItem("f2", "Homework_Math_Ch4.pdf", "Documents", "1.1 MB", "Yesterday"),
        FileItem("f3", "Rec_0034.mp4", "Videos", "45.8 MB", "May 10")
    )

    // Full Folder Grid / Categories
    val folderCategories = listOf(
        "Internal Storage", "Downloads", "Documents", "Pictures", "DCIM", "Camera",
        "Movies", "Music", "Audio", "Screenshots", "Screen Recordings", "WhatsApp",
        "Telegram", "Android", "APK", "Compressed Files", "Recent", "Favorites", "Trash", "Cloud"
    )

    private val _fileItems = MutableStateFlow(
        listOf(
            // Internal Storage Root Folders
            FileExplorerItem("fe1", "c1", "Downloads", isFolder = true, parentFolderPath = "/Internal Storage", category = "Downloads", iconType = "folder"),
            FileExplorerItem("fe2", "c1", "DCIM", isFolder = true, parentFolderPath = "/Internal Storage", category = "Pictures", iconType = "folder"),
            FileExplorerItem("fe3", "c1", "Documents", isFolder = true, parentFolderPath = "/Internal Storage", category = "Documents", iconType = "folder"),
            FileExplorerItem("fe4", "c1", "WhatsApp", isFolder = true, parentFolderPath = "/Internal Storage", category = "WhatsApp", iconType = "folder"),
            FileExplorerItem("fe5", "c1", "Telegram", isFolder = true, parentFolderPath = "/Internal Storage", category = "Telegram", iconType = "folder"),
            FileExplorerItem("fe6", "c1", "Android", isFolder = true, parentFolderPath = "/Internal Storage", category = "Android", iconType = "folder"),
            FileExplorerItem("fe7", "c1", "Music", isFolder = true, parentFolderPath = "/Internal Storage", category = "Music", iconType = "folder"),

            // Inside Downloads
            FileExplorerItem("f_dl1", "c1", "Math_Homework_Ch4.pdf", isFolder = false, extension = "pdf", sizeBytes = 2411724, sizeFormatted = "2.4 MB", dateModified = "2026-08-06 14:20", parentFolderPath = "/Internal Storage/Downloads", category = "Downloads", iconType = "pdf"),
            FileExplorerItem("f_dl2", "c1", "SentryKids_Agent.apk", isFolder = false, extension = "apk", sizeBytes = 38240120, sizeFormatted = "38.2 MB", dateModified = "2026-08-05 09:12", parentFolderPath = "/Internal Storage/Downloads", category = "APK", iconType = "apk"),
            FileExplorerItem("f_dl3", "c1", "Game_Assets.zip", isFolder = false, extension = "zip", sizeBytes = 142800100, sizeFormatted = "142.8 MB", dateModified = "2026-08-04 18:45", parentFolderPath = "/Internal Storage/Downloads", category = "Compressed Files", iconType = "zip"),

            // Inside DCIM
            FileExplorerItem("f_dc1", "c1", "Camera", isFolder = true, parentFolderPath = "/Internal Storage/DCIM", category = "Camera", iconType = "folder"),
            FileExplorerItem("f_dc2", "c1", "Screenshots", isFolder = true, parentFolderPath = "/Internal Storage/DCIM", category = "Screenshots", iconType = "folder"),

            // Inside Camera
            FileExplorerItem("f_cam1", "c1", "IMG_20260807_1012.jpg", isFolder = false, extension = "jpg", sizeBytes = 4120900, sizeFormatted = "4.1 MB", dateModified = "Today, 10:12 AM", parentFolderPath = "/Internal Storage/DCIM/Camera", category = "Pictures", iconType = "image", isFavorite = true),
            FileExplorerItem("f_cam2", "c1", "VID_20260806_1530.mp4", isFolder = false, extension = "mp4", sizeBytes = 85200100, sizeFormatted = "85.2 MB", dateModified = "Yesterday, 3:30 PM", parentFolderPath = "/Internal Storage/DCIM/Camera", category = "Movies", iconType = "video"),

            // Inside Screenshots
            FileExplorerItem("f_sc1", "c1", "Screenshot_20260807_0940.png", isFolder = false, extension = "png", sizeBytes = 1820400, sizeFormatted = "1.8 MB", dateModified = "Today, 09:40 AM", parentFolderPath = "/Internal Storage/DCIM/Screenshots", category = "Screenshots", iconType = "image"),
            FileExplorerItem("f_sc2", "c1", "ScreenRecording_20260806.mp4", isFolder = false, extension = "mp4", sizeBytes = 124000100, sizeFormatted = "124.0 MB", dateModified = "Yesterday, 8:15 PM", parentFolderPath = "/Internal Storage/DCIM/Screenshots", category = "Screen Recordings", iconType = "video"),

            // Inside WhatsApp
            FileExplorerItem("f_wa1", "c1", "WhatsApp Documents", isFolder = true, parentFolderPath = "/Internal Storage/WhatsApp", category = "WhatsApp", iconType = "folder"),
            FileExplorerItem("f_wa2", "c1", "WhatsApp Images", isFolder = true, parentFolderPath = "/Internal Storage/WhatsApp", category = "WhatsApp", iconType = "folder"),
            FileExplorerItem("f_wa3", "c1", "AUDIO_20260807_WA0001.opus", isFolder = false, extension = "opus", sizeBytes = 840000, sizeFormatted = "840 KB", dateModified = "Today, 11:05 AM", parentFolderPath = "/Internal Storage/WhatsApp", category = "Audio", iconType = "audio")
        )
    )
    val fileItems: StateFlow<List<FileExplorerItem>> = _fileItems.asStateFlow()

    fun deleteFile(id: String) {
        _fileItems.value = _fileItems.value.filter { it.id != id }
    }

    fun renameFile(id: String, newName: String) {
        _fileItems.value = _fileItems.value.map {
            if (it.id == id) it.copy(name = newName) else it
        }
    }
}

object MockAutomationsRepository {
    val automations = listOf(
        AutomationItem("aut1", "Lock at Bedtime", "Every day at 10:00 PM", listOf("Bedtime Schedule Trigger"), listOf("Front Door Lock", "Back Door Lock", "Device Lock"), isEnabled = true),
        AutomationItem("aut2", "Alert on School Arrival", "When Emma arrives at School", listOf("Geofence Arrival: School"), listOf("Send Push Notification"), isEnabled = true)
    )
}
