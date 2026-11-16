package com.trackwell.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.unit.max
import com.trackwell.app.ui.home.components.NeuBottomBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.trackwell.app.data.local.database.DatabaseSeeder
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import com.trackwell.app.notification.GoogleDriveBackupScheduler
import com.trackwell.app.ui.common.HapticFeedbackHelper
import com.trackwell.app.ui.common.Icon3DHome
import com.trackwell.app.ui.common.Icon3DDiary
import com.trackwell.app.ui.common.Icon3DAnalytics
import com.trackwell.app.ui.common.Icon3DSettings
import com.trackwell.app.ui.common.Icon3DDocumentOutline
import com.trackwell.app.ui.common.Icon3DStethoscope
import com.trackwell.app.ui.common.Icon3DBell
import com.trackwell.app.ui.common.Icon3DWaterDrop
import com.trackwell.app.ui.common.Icon3DUrineDrop
import com.trackwell.app.ui.common.Icon3DSugarDrop
import com.trackwell.app.ui.common.Icon3DScale
import com.trackwell.app.ui.common.Icon3DHeart
import com.trackwell.app.ui.common.Icon3DPill
import android.content.Intent
import com.trackwell.app.notification.medicine.MedicineNotificationManager
import com.trackwell.app.ui.screens.medicine.MedicineScreen
import com.trackwell.app.ui.screens.medicine.components.ActiveReminderHost
import com.trackwell.app.ui.navigation.Screen
import com.trackwell.app.ui.screens.daydetail.DayDetailScreen
import com.trackwell.app.ui.screens.exportdata.ExportScreen
import com.trackwell.app.ui.screens.history.HistoryScreen
import com.trackwell.app.ui.screens.importdata.ImportHistoryScreen
import com.trackwell.app.ui.screens.importdata.ImportScreen
import com.trackwell.app.ui.screens.settings.SettingsScreen
import com.trackwell.app.ui.screens.today.TodayScreen
import com.trackwell.app.ui.screens.today.TodayViewModel
import com.trackwell.app.ui.screens.weighthistory.WeightHistoryScreen
import com.trackwell.app.ui.screens.bloodsugarhistory.BloodSugarHistoryScreen
import com.trackwell.app.ui.screens.fluidhistory.FluidHistoryScreen
import com.trackwell.app.ui.screens.today.components.EditWeightDialog
import com.trackwell.app.ui.screens.today.components.FluidEntryBottomSheet
import com.trackwell.app.ui.screens.today.components.QuickAddBottomSheet
import com.trackwell.app.ui.screens.today.components.SugarEntryBottomSheet
import com.trackwell.app.ui.home.components.RenalBodyFlowBackdrop
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.theme.IntakeAqua
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TrackWellTheme
import com.trackwell.app.ui.theme.UrineTopaz
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    @Inject
    lateinit var googleDriveBackupScheduler: GoogleDriveBackupScheduler

    private val pendingNavigationRoute = mutableStateOf<String?>(null)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntentNavigation(intent)
    }

    private fun handleIntentNavigation(intent: Intent?) {
        val navTo = intent?.getStringExtra(MedicineNotificationManager.EXTRA_NAVIGATE_TO)
        if (navTo == "medicine") {
            pendingNavigationRoute.value = Screen.Medicine.route
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        handleIntentNavigation(intent)

        // Pre-seed clinical data (Weeks 1-17) if not already populated
        lifecycleScope.launch(Dispatchers.IO) {
            databaseSeeder.seedIfNeeded()
        }

        val settings = runBlocking { userPreferences.settingsFlow.first() }
        val requiresBiometric = settings.isBiometricEnabled && !BuildConfig.DEBUG

        // Initialize Google Drive daily auto-backup schedule
        googleDriveBackupScheduler.scheduleDailyBackup(settings)

        setContent {
            TrackWellTheme {
                var isUnlocked by remember { mutableStateOf(!requiresBiometric) }

                if (!isUnlocked) {
                    BiometricLockScreen(
                        onAuthenticate = {
                            showBiometricPrompt { success ->
                                if (success) isUnlocked = true
                            }
                        }
                    )
                } else {
                    MainAppScaffold(
                        pendingNavRoute = pendingNavigationRoute.value,
                        onNavRouteHandled = { pendingNavigationRoute.value = null }
                    )
                }
            }
        }

        if (requiresBiometric) {
            showBiometricPrompt { success ->
                if (success) {
                    setContent {
                        TrackWellTheme {
                            MainAppScaffold(
                                pendingNavRoute = pendingNavigationRoute.value,
                                onNavRouteHandled = { pendingNavigationRoute.value = null }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt(onResult: (Boolean) -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onResult(false)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onResult(false)
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock TrackWell")
            .setSubtitle("Authenticate using your biometric credential to view health routines")
            .setNegativeButtonText("Cancel")
            .build()

        prompt.authenticate(promptInfo)
    }
}

@Composable
fun BiometricLockScreen(onAuthenticate: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "TrackWell is Locked",
                style = MaterialTheme.typography.titleLarge
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onAuthenticate) {
                Text("Unlock with Biometrics")
            }
        }
    }
}

data class BottomNavItem(
    val index: Int,
    val screen: Screen?,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    pendingNavRoute: String? = null,
    onNavRouteHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val todayViewModel: TodayViewModel = hiltViewModel()
    val todayUiState by todayViewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(pendingNavRoute) {
        if (pendingNavRoute != null) {
            navController.navigate(pendingNavRoute) {
                popUpTo(Screen.Today.route) { saveState = true }
                launchSingleTop = true
            }
            onNavRouteHandled()
        }
    }

    var showGlobalQuickActionSheet by remember { mutableStateOf(false) }
    var showGlobalFluidSheet by remember { mutableStateOf(false) }
    var globalFluidType by remember { mutableStateOf(FluidType.INTAKE) }
    var showGlobalSugarSheet by remember { mutableStateOf(false) }
    var showGlobalWeightDialog by remember { mutableStateOf(false) }

    val navItems = listOf(
        BottomNavItem(0, Screen.Today, "Today"),
        BottomNavItem(1, Screen.History, "History"),
        BottomNavItem(2, Screen.Medicine, "Medicines"),
        BottomNavItem(3, Screen.Settings, "Settings")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = navItems.any { it.screen?.route == currentRoute }

    val isDark = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TargetTokens.bg)
    ) {
        // Global living vascular & wellness body flow background matching Home
        VascularBackground(modifier = Modifier.fillMaxSize())
        RenalBodyFlowBackdrop(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
        )

        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Today.route) {
                TodayScreen(
                    viewModel = todayViewModel,
                    onNavigateToDayDetail = { date ->
                        navController.navigate(Screen.DayDetail.createRoute(date))
                    },
                    onNavigateToHistory = {
                        navController.navigate(Screen.History.route)
                    },
                    onNavigateToWeightHistory = {
                        navController.navigate(Screen.WeightHistory.route)
                    },
                    onNavigateToBloodSugarHistory = {
                        navController.navigate(Screen.BloodSugarHistory.route)
                    }
                )
            }

            composable(Screen.WeightHistory.route) {
                WeightHistoryScreen(
                    viewModel = hiltViewModel(),
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateToDayDetail = { date ->
                        navController.navigate(Screen.DayDetail.createRoute(date))
                    }
                )
            }

            composable(Screen.BloodSugarHistory.route) {
                BloodSugarHistoryScreen(
                    viewModel = hiltViewModel(),
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateToDayDetail = { date ->
                        navController.navigate(Screen.DayDetail.createRoute(date))
                    }
                )
            }

            composable(Screen.History.route) {
                FluidHistoryScreen(
                    viewModel = hiltViewModel(),
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateToDayDetail = { date ->
                        navController.navigate(Screen.DayDetail.createRoute(date))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToImport = { navController.navigate(Screen.ImportData.route) },
                    onNavigateToExport = { navController.navigate(Screen.ExportData.route) }
                )
            }

            composable(Screen.ImportData.route) {
                ImportScreen(
                    viewModel = hiltViewModel(),
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateToHistory = { navController.navigate("import_history") }
                )
            }

            composable("import_history") {
                ImportHistoryScreen(
                    viewModel = hiltViewModel(),
                    onNavigateUp = { navController.navigateUp() }
                )
            }

            composable(Screen.ExportData.route) {
                ExportScreen(
                    viewModel = hiltViewModel(),
                    onNavigateUp = { navController.navigateUp() }
                )
            }

            composable(Screen.DayDetail.route) {
                DayDetailScreen(
                    viewModel = hiltViewModel(),
                    onNavigateUp = { navController.navigateUp() }
                )
            }

            composable(Screen.Medicine.route) {
                MedicineScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }

        if (showBottomBar) {
            val selectedIdx = when (currentRoute) {
                Screen.Today.route -> 0
                Screen.History.route -> 1
                Screen.Medicine.route -> 2
                Screen.Settings.route -> 3
                else -> 0
            }

            val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            val bottomMargin = max(25.dp, bottomInset + 1.dp)

            NeuBottomBar(
                selectedIndex = selectedIdx,
                onTabSelected = { index ->
                    HapticFeedbackHelper.performClickHaptic(context)
                    val target = navItems.getOrNull(index)?.screen
                    if (target == null) {
                        showGlobalQuickActionSheet = true
                    } else if (currentRoute != target.route) {
                        navController.navigate(target.route) {
                            popUpTo(Screen.Today.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = bottomMargin)
            )
        }
    }

    // Global AURA Quick Action Speed-Dial Sheet (8 Actions)
    if (showGlobalQuickActionSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showGlobalQuickActionSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            containerColor = Color(0xFFF8FBFF),
            scrimColor = Color.Black.copy(alpha = 0.40f),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .size(width = 40.dp, height = 4.5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFCBD5E1))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AURA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• QUICK LOG",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Record Routine for ${com.trackwell.app.domain.util.DateTimeUtils.formatDisplayDate(todayUiState.selectedDate)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // 3D Elevated Circular Close 'X' Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                spotColor = Color(0x20000000),
                                ambientColor = Color(0x10000000)
                            )
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    HapticFeedbackHelper.performClickHaptic(context)
                                    showGlobalQuickActionSheet = false
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3D Tactile Grid of Quick Log Actions (Behance Health Claymorphic Theme)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClaymorphicGridActionTile(
                            title = "Water In",
                            subtitle = "Fluid Intake",
                            icon = { Icon3DWaterDrop(size = 36.dp) },
                            onClick = {
                                showGlobalQuickActionSheet = false
                                globalFluidType = FluidType.INTAKE
                                showGlobalFluidSheet = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ClaymorphicGridActionTile(
                            title = "Urine Out",
                            subtitle = "Diuresis",
                            icon = { Icon3DUrineDrop(size = 36.dp) },
                            onClick = {
                                showGlobalQuickActionSheet = false
                                globalFluidType = FluidType.URINE
                                showGlobalFluidSheet = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClaymorphicGridActionTile(
                            title = "Blood Sugar",
                            subtitle = "Glucose & Dose",
                            icon = { Icon3DSugarDrop(size = 36.dp) },
                            onClick = {
                                showGlobalQuickActionSheet = false
                                showGlobalSugarSheet = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ClaymorphicGridActionTile(
                            title = "Dry Weight",
                            subtitle = "Morning Baseline",
                            icon = { Icon3DScale(size = 36.dp) },
                            onClick = {
                                showGlobalQuickActionSheet = false
                                showGlobalWeightDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClaymorphicGridActionTile(
                            title = "Vitals / BP",
                            subtitle = "Cardiovascular",
                            icon = { Icon3DHeart(size = 36.dp) },
                            onClick = {
                                showGlobalQuickActionSheet = false
                                navController.navigate(Screen.DayDetail.createRoute(todayUiState.selectedDate))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ClaymorphicGridActionTile(
                            title = "Medications",
                            subtitle = "Reminders",
                            icon = { Icon3DPill(size = 36.dp) },
                            onClick = {
                                showGlobalQuickActionSheet = false
                                navController.navigate(Screen.Medicine.route)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Triggered Global Fluid Sheet
    if (showGlobalFluidSheet) {
        val presets = todayUiState.appSettings.quickPresetsMl
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }

        FluidEntryBottomSheet(
            initialType = globalFluidType,
            selectedDateIso = todayUiState.selectedDate,
            customPresets = presets,
            onDismiss = { showGlobalFluidSheet = false },
            onSave = { amount, type, timestamp ->
                todayViewModel.logFluid(amount, type, timestamp)
                showGlobalFluidSheet = false
            }
        )
    }

    // Triggered Global Sugar Sheet
    if (showGlobalSugarSheet) {
        val slot = SugarSlot.suggestCurrentSlot()
        val currentSlotEntry = todayUiState.dayOverview.sugarEntries[slot]
        SugarEntryBottomSheet(
            initialSlot = slot,
            existingEntry = currentSlotEntry,
            selectedDateIso = todayUiState.selectedDate,
            thresholds = todayUiState.appSettings.thresholdSettings,
            onDismiss = { showGlobalSugarSheet = false },
            onSave = { s, reading, dose, timestamp, note ->
                todayViewModel.logSugar(s, reading, dose, timestamp, note)
                showGlobalSugarSheet = false
            },
            onDelete = { id ->
                todayViewModel.deleteSugarEntry(id)
                showGlobalSugarSheet = false
            }
        )
    }

    // Triggered Global Weight Dialog
    if (showGlobalWeightDialog) {
        EditWeightDialog(
            initialWeight = todayUiState.dayOverview.morningWeightKg,
            onDismiss = { showGlobalWeightDialog = false },
            onConfirm = { weight ->
                todayViewModel.updateWeight(weight)
                showGlobalWeightDialog = false
            }
        )
    }

    // Active Medicine Reminder In-App Bottom Sheet Host
    ActiveReminderHost()
}

@Composable
fun NavTabItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val debossedBg = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF14181F), Color(0xFF1B202A)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFE5E9EF), Color(0xFFDFE4EB)))
    }

    val debossedBorder = BorderStroke(
        1.dp,
        if (isDark) Brush.verticalGradient(listOf(Color(0x30000000), Color.White.copy(alpha = 0.08f)))
        else Brush.verticalGradient(listOf(Color(0x18000000), Color.White.copy(alpha = 0.8f)))
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(width = 54.dp, height = 38.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(debossedBg)
                    .border(debossedBorder, RoundedCornerShape(19.dp)),
                contentAlignment = Alignment.Center
            ) {
                Render3DNavIcon(index = item.index, isSelected = true)
            }
        } else {
            Box(
                modifier = Modifier.size(width = 46.dp, height = 38.dp),
                contentAlignment = Alignment.Center
            ) {
                Render3DNavIcon(index = item.index, isSelected = false)
            }
        }
    }
}

@Composable
fun Render3DNavIcon(index: Int, isSelected: Boolean) {
    when (index) {
        0 -> Icon3DHome(isSelected = isSelected, size = 22.dp)
        1 -> Icon3DDocumentOutline(isSelected = isSelected, size = 22.dp)
        2 -> Icon3DBell(isSelected = isSelected, size = 22.dp)
        3 -> Icon3DSettings(isSelected = isSelected, size = 22.dp)
        else -> Icon3DHome(isSelected = isSelected, size = 22.dp)
    }
}

@Composable
fun ClaymorphicGridActionTile(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) Color(0xFF1E232B) else Color.White

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        shadowElevation = 5.dp,
        border = BorderStroke(
            1.2.dp,
            Brush.verticalGradient(
                if (isDark) listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.04f), Color(0x20000000))
                else listOf(Color.White, Color.White.copy(alpha = 0.60f), Color(0x0C000000))
            )
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            if (isDark) listOf(Color(0xFF2B3340), Color(0xFF1B2028))
                            else listOf(Color(0xFFF8FAFC), Color(0xFFEDF2F7))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
        }
    }
}
