package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mock.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.SentryKidsTheme

import androidx.compose.runtime.saveable.rememberSaveableStateHolder

// Navigation Screen Routes Enum
enum class AppScreen {
    Splash,
    Onboarding,
    Login,
    Register,
    MainTabs,
    DevicesManagement,
    DeviceHealth,
    RemoteControl,
    LiveView,
    CameraPreview,
    FilesStorage,
    CallsSms,
    Automations,
    ContentFiltering,
    SocialContent,
    AiCenter,
    AlertsCenter,
    ReportsAnalytics,
    AiChat,
    Subscription,
    Permissions,
    PermissionAlerts,
    HelpCenter,
    DeveloperDiagnostics,
    About,
    GlobalSearch,
    ScreenTimeUsage,
    NotificationMonitoring,
    SecurityVerification,
    AdvancedFeatures
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SentryKidsTheme {
                SentryKidsApp()
            }
        }
    }
}

@Composable
fun SentryKidsApp() {
    val navigationStack = remember { mutableStateListOf(AppScreen.Splash) }
    val currentScreen = navigationStack.lastOrNull() ?: AppScreen.Splash

    val tabStack = remember { mutableStateListOf(SentryTab.Device) }
    var selectedTab by remember { mutableStateOf(SentryTab.Device) }
    val saveableStateHolder = rememberSaveableStateHolder()

    var selectedDevice by remember { mutableStateOf<DeviceItem?>(null) }
    val alerts by MockAlertsRepository.alerts.collectAsState()

    fun navigateTo(screen: AppScreen) {
        if (navigationStack.lastOrNull() != screen) {
            navigationStack.add(screen)
        }
    }

    fun goBack() {
        if (navigationStack.size > 1) {
            navigationStack.removeAt(navigationStack.lastIndex)
        }
    }

    fun navigateAndClearTo(screen: AppScreen) {
        navigationStack.clear()
        navigationStack.add(screen)
    }

    fun selectTab(tab: SentryTab) {
        if (selectedTab != tab) {
            tabStack.remove(tab)
            tabStack.add(tab)
            selectedTab = tab
        }
    }

    BackHandler(enabled = navigationStack.size > 1 || (currentScreen == AppScreen.MainTabs && tabStack.size > 1)) {
        if (navigationStack.size > 1) {
            goBack()
        } else if (currentScreen == AppScreen.MainTabs && tabStack.size > 1) {
            tabStack.removeAt(tabStack.lastIndex)
            selectedTab = tabStack.last()
        }
    }

    val devices by MockDevicesRepository.devices.collectAsState()
    val defaultDevice = selectedDevice ?: devices.firstOrNull() ?: DeviceItem(
        "d1", "Parent's iPhone 14", "iPhone", "Primary Admin Device", "iOS 17.2", "Active", "Just now", 86, 128, 63, 32, 4.2f, 8.0f, "Excellent"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen == AppScreen.MainTabs) {
                SentryTopAppBar(
                    title = "SentryKids",
                    tagline = "Smart AI Parental Control",
                    unreadAlertsCount = alerts.size,
                    onNotificationClick = { navigateTo(AppScreen.AlertsCenter) },
                    onSearchClick = { navigateTo(AppScreen.GlobalSearch) },
                    onProfileClick = { selectTab(SentryTab.My) }
                )
            }
        },
        bottomBar = {
            if (currentScreen == AppScreen.MainTabs) {
                SentryBottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    AppScreen.Splash -> SplashScreen(
                        onGetStarted = { navigateTo(AppScreen.Onboarding) },
                        onLoginDirect = { navigateTo(AppScreen.Login) }
                    )

                    AppScreen.Onboarding -> OnboardingScreen(
                        onFinishOnboarding = { navigateTo(AppScreen.Login) }
                    )

                    AppScreen.Login -> LoginScreen(
                        onLoginSuccess = { navigateTo(AppScreen.SecurityVerification) },
                        onNavigateRegister = { navigateTo(AppScreen.Register) }
                    )

                    AppScreen.Register -> RegisterScreen(
                        onRegisterSuccess = { navigateTo(AppScreen.SecurityVerification) },
                        onNavigateLogin = { goBack() }
                    )

                    AppScreen.SecurityVerification -> SecurityCodeVerificationScreen(
                        onVerificationSuccess = { navigateAndClearTo(AppScreen.MainTabs) },
                        onBackToLogin = { goBack() }
                    )

                    AppScreen.MainTabs -> {
                        saveableStateHolder.SaveableStateProvider(key = selectedTab) {
                            when (selectedTab) {
                                SentryTab.Device -> DashboardScreen(
                                    onNavigateToLiveView = { navigateTo(AppScreen.LiveView) },
                                    onNavigateToRemoteControl = { navigateTo(AppScreen.RemoteControl) },
                                    onNavigateToCameraPreview = { navigateTo(AppScreen.CameraPreview) },
                                    onNavigateToFiles = { navigateTo(AppScreen.FilesStorage) },
                                    onNavigateToApps = { navigateTo(AppScreen.ContentFiltering) },
                                    onNavigateToSocialContent = { navigateTo(AppScreen.SocialContent) },
                                    onNavigateToCallsSms = { navigateTo(AppScreen.CallsSms) },
                                    onNavigateToAlerts = { navigateTo(AppScreen.AlertsCenter) },
                                    onNavigateToDeviceHealth = { navigateTo(AppScreen.DeviceHealth) },
                                    onNavigateToChildDevices = { navigateTo(AppScreen.DevicesManagement) },
                                    onNavigateToScreenTimeUsage = { navigateTo(AppScreen.ScreenTimeUsage) },
                                    onNavigateToNotificationMonitoring = { navigateTo(AppScreen.NotificationMonitoring) },
                                    onNavigateToContentFiltering = { navigateTo(AppScreen.ContentFiltering) },
                                    onNavigateToAdvancedFeatures = { navigateTo(AppScreen.AdvancedFeatures) },
                                    onNavigateToPermissionAlerts = { navigateTo(AppScreen.PermissionAlerts) }
                                )

                                SentryTab.Location -> LocationMainScreen(
                                    onNavigateToContentFiltering = { navigateTo(AppScreen.ContentFiltering) }
                                )

                                SentryTab.Family -> FamilyGroupScreen(
                                    onNavigateToSocialContent = { navigateTo(AppScreen.SocialContent) },
                                    onNavigateToAiCenter = { navigateTo(AppScreen.AiCenter) }
                                )

                                SentryTab.My -> MyProfileScreen(
                                    onNavigateToDevices = { navigateTo(AppScreen.DevicesManagement) },
                                    onNavigateToSubscription = { navigateTo(AppScreen.Subscription) },
                                    onNavigateToPermissions = { navigateTo(AppScreen.Permissions) },
                                    onNavigateToHelpCenter = { navigateTo(AppScreen.HelpCenter) },
                                    onNavigateToDeveloperDiagnostics = { navigateTo(AppScreen.DeveloperDiagnostics) },
                                    onNavigateToAbout = { navigateTo(AppScreen.About) },
                                    onLogout = { navigateAndClearTo(AppScreen.Login) }
                                )
                            }
                        }
                    }

                    AppScreen.DevicesManagement -> DevicesListScreen(
                        onNavigateToDeviceHealth = { navigateTo(AppScreen.DeviceHealth) },
                        onNavigateToRemoteControl = { navigateTo(AppScreen.RemoteControl) },
                        onNavigateToLiveView = { navigateTo(AppScreen.LiveView) },
                        onNavigateToCameraPreview = { navigateTo(AppScreen.CameraPreview) },
                        onNavigateToFiles = { navigateTo(AppScreen.FilesStorage) },
                        onNavigateToCallsSms = { navigateTo(AppScreen.CallsSms) },
                        onNavigateToAutomations = { navigateTo(AppScreen.Automations) },
                        onBack = { goBack() }
                    )

                    AppScreen.DeviceHealth -> DeviceHealthScreen(
                        device = defaultDevice,
                        onBack = { goBack() }
                    )

                    AppScreen.RemoteControl -> RemoteControlScreen(
                        device = defaultDevice,
                        onBack = { goBack() }
                    )

                    AppScreen.LiveView -> LiveScreenMirroringScreen(
                        device = defaultDevice,
                        onBack = { goBack() }
                    )

                    AppScreen.CameraPreview -> CameraPreviewScreen(
                        device = defaultDevice,
                        onBack = { goBack() }
                    )

                    AppScreen.FilesStorage -> FilesStorageScreen(
                        device = defaultDevice,
                        onBack = { goBack() }
                    )

                    AppScreen.CallsSms -> CallsSmsScreen(
                        device = defaultDevice,
                        onBack = { goBack() }
                    )

                    AppScreen.Automations -> AutomationsScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.ContentFiltering -> DeviceFilteringScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.SocialContent -> SocialContentScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.AiCenter -> AiCenterScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.AlertsCenter -> AlertsCenterScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.ReportsAnalytics -> ReportsAnalyticsScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.AiChat -> GuardianAiChatScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.Subscription -> SubscriptionPlansScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.Permissions -> PermissionsScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.PermissionAlerts -> PermissionAlertsScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.HelpCenter -> HelpCenterScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.DeveloperDiagnostics -> DeveloperDiagnosticsScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.About -> AboutScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.GlobalSearch -> GlobalSearchScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.ScreenTimeUsage -> ScreenTimeUsageScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.NotificationMonitoring -> NotificationMonitoringScreen(
                        onBack = { goBack() }
                    )

                    AppScreen.AdvancedFeatures -> AdvancedFeaturesScreen(
                        onBack = { goBack() }
                    )
                }
            }
        }
    }
}
