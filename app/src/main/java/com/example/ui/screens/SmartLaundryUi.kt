package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.viewmodel.LaundryViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartLaundryApp(viewModel: LaundryViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val pendingWa by viewModel.pendingWaNotification.collectAsState()
    val context = LocalContext.current

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != Screen.SPLASH && currentScreen != Screen.ROLE_SELECTION) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocalLaundryService,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentScreen) {
                                    Screen.CASHIER_LOGIN -> "Masuk Kasir"
                                    Screen.ADMIN_DASHBOARD -> "Owner Dashboard"
                                    Screen.CASHIER_DASHBOARD -> "Kasir Dashboard"
                                    Screen.KELOLA_KASIR -> "Kelola Kasir"
                                    Screen.KELOLA_OUTLET -> "Kelola Outlet"
                                    Screen.CARI_PELANGGAN -> "Cari & Kelola Pelanggan"
                                    Screen.ANALISA_PELANGGAN -> "Analisa Pelanggan"
                                    Screen.ANALISA_LAYANAN -> "Analisa Layanan"
                                    Screen.LAPORAN_PEMESANAN -> "Laporan Pemesanan"
                                    Screen.LAPORAN_KAS -> "Laporan Kas"
                                    Screen.SETTING_KASIR -> "Pengaturan"
                                    Screen.ORDER_BARU -> "Buat Order Baru"
                                    Screen.CRUD_PELANGGAN -> "Kelola Pelanggan"
                                    Screen.MUTASI_KAS -> "Mutasi Kas"
                                    else -> "CucianKu"
                                },
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        if (currentScreen != Screen.ADMIN_DASHBOARD && currentScreen != Screen.CASHIER_DASHBOARD && currentScreen != Screen.CASHIER_LOGIN) {
                            IconButton(
                                onClick = {
                                    if (currentScreen == Screen.LAPORAN_PEMESANAN ||
                                        currentScreen == Screen.LAPORAN_KAS ||
                                        currentScreen == Screen.ANALISA_PELANGGAN ||
                                        currentScreen == Screen.ANALISA_LAYANAN
                                    ) {
                                        viewModel.adminSelectedTab.value = 1
                                    }
                                    viewModel.navigateBack()
                                },
                                modifier = Modifier.testTag("back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali"
                                )
                            }
                        }
                    },
                    actions = {
                        if (currentScreen == Screen.ADMIN_DASHBOARD || currentScreen == Screen.CASHIER_DASHBOARD) {
                            IconButton(
                                onClick = {
                                    viewModel.selectedRole.value = null
                                    viewModel.activeCashier.value = null
                                    viewModel.navigateTo(Screen.ROLE_SELECTION)
                                },
                                modifier = Modifier.testTag("logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Logout,
                                    contentDescription = "Keluar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main navigation container
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    Screen.SPLASH -> SplashScreen(viewModel)
                    Screen.ROLE_SELECTION -> RoleSelectionScreen(viewModel)
                    Screen.CASHIER_LOGIN -> CashierLoginScreen(viewModel)
                    Screen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel)
                    Screen.CASHIER_DASHBOARD -> CashierDashboardScreen(viewModel)
                    Screen.KELOLA_KASIR -> KelolaKasirScreen(viewModel)
                    Screen.KELOLA_OUTLET -> KelolaOutletScreen(viewModel)
                    Screen.CARI_PELANGGAN -> CariPelangganScreen(viewModel)
                    Screen.ANALISA_PELANGGAN -> AnalisaPelangganScreen(viewModel)
                    Screen.ANALISA_LAYANAN -> AnalisaLayananScreen(viewModel)
                    Screen.LAPORAN_PEMESANAN -> LaporanPemesananScreen(viewModel)
                    Screen.LAPORAN_KAS -> LaporanKasScreen(viewModel)
                    Screen.SETTING_KASIR -> SettingKasirScreen(viewModel)
                    Screen.ORDER_BARU -> OrderBaruScreen(viewModel)
                    Screen.CRUD_PELANGGAN -> CRUDPelangganScreen(viewModel)
                    Screen.MUTASI_KAS -> MutasiKasScreen(viewModel)
                }
            }

            // WhatsApp Notification or System Alert Overlay (Simulated)
            pendingWa?.let { waMessage ->
                val isWhatsApp = waMessage.startsWith("Kirim WA ke")
                val isWarning = waMessage.contains("ditolak", ignoreCase = true) || 
                                waMessage.contains("gagal", ignoreCase = true) || 
                                waMessage.contains("Harap lengkapi", ignoreCase = true) || 
                                waMessage.contains("Akses ditolak", ignoreCase = true)

                val themeColor = when {
                    isWhatsApp -> Color(0xFF25D366) // WhatsApp Green
                    isWarning -> MaterialTheme.colorScheme.error // Warning Red/Amber
                    else -> MaterialTheme.colorScheme.primary // System primary
                }

                val titleText = when {
                    isWhatsApp -> "WhatsApp Terkirim! (Simulasi)"
                    isWarning -> "Peringatan Sistem"
                    else -> "Notifikasi Sistem"
                }

                val titleColor = when {
                    isWhatsApp -> Color(0xFF1E7E34)
                    isWarning -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.primary
                }

                val iconVector = when {
                    isWhatsApp -> Icons.AutoMirrored.Filled.Send
                    isWarning -> Icons.Filled.Warning
                    else -> Icons.Filled.CheckCircle
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = true) { /* Consume taps */ },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(themeColor, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = if (isWhatsApp) "WhatsApp" else "System",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = titleText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = titleColor
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = waMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { viewModel.clearPendingWaNotification() },
                                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("wa_ok_button")
                            ) {
                                Text("Selesai & Tutup", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(viewModel: LaundryViewModel) {
    LaunchedEffect(Unit) {
        delay(2000)
        viewModel.navigateTo(Screen.ROLE_SELECTION)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalLaundryService,
                    contentDescription = "Logo CucianKu",
                    tint = Color.White,
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "CucianKu",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Smart Laundry Management",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(viewModel: LaundryViewModel, initialTab: Int = 0) {
    val cashiers by viewModel.cashiersState.collectAsState()
    val outlets by viewModel.outletsState.collectAsState()

    var selectedTab by remember { mutableStateOf(initialTab) } // 0 = Admin, 1 = Kasir

    // Admin state
    var adminUsername by remember { mutableStateOf("admin") }
    var adminPassword by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var showForgotDialog by remember { mutableStateOf(false) }

    // Kasir state
    var selectedCashier by remember { mutableStateOf<Cashier?>(null) }
    var selectedOutlet by remember { mutableStateOf<Outlet?>(null) }
    var selectedShift by remember { mutableStateOf("Pagi") }

    LaunchedEffect(cashiers, outlets) {
        if (cashiers.isNotEmpty() && selectedCashier == null) {
            selectedCashier = cashiers.firstOrNull()
        }
        if (outlets.isNotEmpty() && selectedOutlet == null) {
            selectedOutlet = outlets.firstOrNull()
        }
    }

    var cashierExpanded by remember { mutableStateOf(false) }
    var outletExpanded by remember { mutableStateOf(false) }
    var shiftExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Logo and Title Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalLaundryService,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CucianKu",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Smart Laundry Management System",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // TAB SELECTOR (Admin vs Kasir)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(26.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab Admin
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (selectedTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .testTag("tab_admin"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Admin / Owner",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }

                // Tab Kasir
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (selectedTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .testTag("tab_cashier"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kasir POS",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DYNAMIC FORM CONTENT CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedTab == 0) {
                        // ===================================
                        // ADMIN LOGIN FORM
                        // ===================================
                        Text(
                            text = "Login Administrator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = adminUsername,
                            onValueChange = { 
                                adminUsername = it
                                loginError = null
                            },
                            label = { Text("Username") },
                            placeholder = { Text("Masukkan username") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_username_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = adminPassword,
                            onValueChange = { 
                                adminPassword = it
                                loginError = null
                            },
                            label = { Text("Kata Sandi") },
                            placeholder = { Text("Masukkan password") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = null)
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (loginError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = loginError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (adminUsername == "admin" && adminPassword == "admin123") {
                                    viewModel.selectedRole.value = "Admin"
                                    viewModel.navigateTo(Screen.ADMIN_DASHBOARD)
                                } else if (adminUsername.isEmpty() || adminPassword.isEmpty()) {
                                    loginError = "Username dan kata sandi tidak boleh kosong."
                                } else {
                                    loginError = "Username atau kata sandi salah. Gunakan default: admin / admin123"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("admin_login_submit"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Masuk sebagai Admin", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = { showForgotDialog = true },
                            modifier = Modifier.testTag("forgot_password_button")
                        ) {
                            Text(
                                text = "Lupa Kata Sandi?",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                    } else {
                        // ===================================
                        // KASIR LOGIN FORM
                        // ===================================
                        Text(
                            text = "Mulai Shift Kasir",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Dropdown Kasir
                        ExposedDropdownMenuBox(
                            expanded = cashierExpanded,
                            onExpandedChange = { cashierExpanded = !cashierExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCashier?.name ?: "Pilih Kasir",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("ID Kasir / Petugas") },
                                leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cashierExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("cashier_select"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = cashierExpanded,
                                onDismissRequest = { cashierExpanded = false }
                            ) {
                                cashiers.forEach { cashier ->
                                    DropdownMenuItem(
                                        text = { Text("${cashier.cashierId} - ${cashier.name}") },
                                        onClick = {
                                            selectedCashier = cashier
                                            cashierExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dropdown Shift
                        ExposedDropdownMenuBox(
                            expanded = shiftExpanded,
                            onExpandedChange = { shiftExpanded = !shiftExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedShift,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Shift Kerja") },
                                leadingIcon = { Icon(Icons.Filled.Schedule, contentDescription = null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shiftExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("shift_select"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = shiftExpanded,
                                onDismissRequest = { shiftExpanded = false }
                            ) {
                                listOf("Pagi", "Siang", "Malam").forEach { shift ->
                                    DropdownMenuItem(
                                        text = { Text(shift) },
                                        onClick = {
                                            selectedShift = shift
                                            shiftExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dropdown Outlet
                        ExposedDropdownMenuBox(
                            expanded = outletExpanded,
                            onExpandedChange = { outletExpanded = !outletExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedOutlet?.name ?: "Pilih Outlet",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Lokasi Outlet") },
                                leadingIcon = { Icon(Icons.Filled.Storefront, contentDescription = null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = outletExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("outlet_select"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = outletExpanded,
                                onDismissRequest = { outletExpanded = false }
                            ) {
                                outlets.forEach { outlet ->
                                    DropdownMenuItem(
                                        text = { Text(outlet.name) },
                                        onClick = {
                                            selectedOutlet = outlet
                                            outletExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (selectedCashier != null && selectedOutlet != null) {
                                    viewModel.selectedRole.value = "Kasir"
                                    viewModel.activeCashier.value = selectedCashier
                                    viewModel.activeOutlet.value = selectedOutlet
                                    viewModel.activeShift.value = selectedShift
                                    viewModel.navigateTo(Screen.CASHIER_DASHBOARD)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("cashier_login_submit"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Mulai Bertugas (Masuk)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Forgot Password Alert Dialog
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lupa Kata Sandi", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Untuk mengatur ulang atau mengganti kata sandi administrator CucianKu, silakan hubungi Pengembang Sistem atau Administrator Utama Anda di Kantor Pusat.\n\nAkun demo default:\nUsername: admin\nPassword: admin123",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { showForgotDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Mengerti")
                }
            }
        )
    }
}

@Composable
fun CashierLoginScreen(viewModel: LaundryViewModel) {
    RoleSelectionScreen(viewModel = viewModel, initialTab = 1)
}

@Composable
fun AdminDashboardScreen(viewModel: LaundryViewModel) {
    val selectedTab by viewModel.adminSelectedTab.collectAsState()
 
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("admin_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.adminSelectedTab.value = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda") },
                    modifier = Modifier.testTag("nav_beranda")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.adminSelectedTab.value = 1 },
                    icon = { Icon(Icons.Filled.Assessment, contentDescription = "Laporan") },
                    label = { Text("Laporan") },
                    modifier = Modifier.testTag("nav_laporan")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.adminSelectedTab.value = 2 },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Pengaturan") },
                    label = { Text("Pengaturan") },
                    modifier = Modifier.testTag("nav_pengaturan")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AdminHomeTab(viewModel)
                1 -> AdminLaporanTab(viewModel)
                2 -> AdminPengaturanTab(viewModel)
            }
        }
    }
}

@Composable
fun AdminHomeTab(viewModel: LaundryViewModel) {
    val orders by viewModel.ordersState.collectAsState()
    val customers by viewModel.customersState.collectAsState()
    val outlets by viewModel.outletsState.collectAsState()
    val adminSelectedOutlet by viewModel.adminSelectedOutlet.collectAsState()

    // Filter orders by selected outlet
    val ordersFiltered = if (adminSelectedOutlet == "Semua Outlet") {
        orders
    } else {
        orders.filter { it.outletName == adminSelectedOutlet }
    }

    // Calculate Admin stats for "Today"
    val calToday = Calendar.getInstance()
    val todayStart = calToday.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis

    val ordersToday = ordersFiltered.filter { it.timestamp >= todayStart && it.status != "Batal" }
    val totalRevenueToday = ordersToday.sumOf { it.finalTotal }
    
    val completedCount = ordersFiltered.count { it.status == "Selesai" || it.status == "Diambil" }
    val progressCount = ordersFiltered.count { it.status != "Selesai" && it.status != "Diambil" && it.status != "Batal" }

    // Gemini states
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiResponse by viewModel.aiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Outlet Selector Dropdown Card
        item {
            var expanded by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Filled.Store,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Outlet Terpilih",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = adminSelectedOutlet,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Box {
                        Button(
                            onClick = { expanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("Pilih Outlet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Semua Outlet", fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.adminSelectedOutlet.value = "Semua Outlet"
                                    expanded = false
                                }
                            )
                            outlets.forEach { outlet ->
                                DropdownMenuItem(
                                    text = { Text(outlet.name) },
                                    onClick = {
                                        viewModel.adminSelectedOutlet.value = outlet.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Welcoming card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Halo Owner CucianKu!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        // Easy data dummy filler button
                        IconButton(
                            onClick = { viewModel.addDummyTodayOrders() },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Isi Data Dummy Hari Ini",
                                tint = Color.White
                            )
                        }
                    }
                    Text(
                        text = "Berikut ringkasan performa sistem laundry Anda secara real-time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Ketuk tombol '+' di kanan atas untuk mengisi data simulasi transaksi Kiloan, Satuan & Meteran hari ini.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        title = "Order Hari Ini",
                        value = "${ordersToday.size} Transaksi",
                        icon = Icons.Filled.ShoppingCart,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pendapatan Hari Ini",
                        value = "Rp ${String.format("%,.0f", totalRevenueToday)}",
                        icon = Icons.Filled.MonetizationOn,
                        modifier = Modifier.weight(1.2f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        title = "Jumlah Pelanggan",
                        value = "${customers.size} Orang",
                        icon = Icons.Filled.Group,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Selesai / Proses",
                        value = "$completedCount / $progressCount",
                        icon = Icons.Filled.LocalLaundryService,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Search Section (High-Value Feature!)
        item {
            var searchType by remember { mutableStateOf(0) } // 0 = Pesanan, 1 = Pelanggan
            var searchQuery by remember { mutableStateOf("") }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pencarian Cepat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Temukan transaksi laundry atau biodata pelanggan secara instan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Tab Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (searchType == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { searchType = 0 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cari Pesanan",
                                fontWeight = FontWeight.Bold,
                                color = if (searchType == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (searchType == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { searchType = 1 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cari Pelanggan",
                                fontWeight = FontWeight.Bold,
                                color = if (searchType == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { 
                            Text(
                                text = if (searchType == 0) "Cari nama, WA, jenis layanan..." else "Cari nama pelanggan, nomor WA...",
                                fontSize = 12.sp
                            ) 
                        },
                        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Bersihkan", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("quick_search_field"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    if (searchQuery.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        if (searchType == 0) {
                            // Search Orders
                            val matchedOrders = ordersFiltered.filter {
                                it.customerName.contains(searchQuery, ignoreCase = true) ||
                                        it.customerPhone.contains(searchQuery) ||
                                        it.serviceType.contains(searchQuery, ignoreCase = true) ||
                                        it.status.contains(searchQuery, ignoreCase = true)
                            }.take(5)

                            Text(
                                text = "Hasil Pesanan (${matchedOrders.size} ditemukan):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            if (matchedOrders.isEmpty()) {
                                Text("Tidak ada pesanan yang cocok.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    matchedOrders.forEach { ord ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(ord.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("WA: ${ord.customerPhone} • ${ord.serviceType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text("Outlet: ${ord.outletName}", fontSize = 10.sp, color = Color.Gray)
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text("Rp ${String.format("%,.0f", ord.finalTotal)}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                when(ord.status) {
                                                                    "Selesai", "Diambil" -> Color(0xFFE8F5E9)
                                                                    "Batal" -> Color(0xFFFFEBEE)
                                                                    else -> Color(0xFFFFF3E0)
                                                                },
                                                                shape = RoundedCornerShape(4.dp)
                                                            )
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = ord.status,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = when(ord.status) {
                                                                "Selesai", "Diambil" -> Color(0xFF2E7D32)
                                                                "Batal" -> Color(0xFFC62828)
                                                                else -> Color(0xFFEF6C00)
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Search Customers
                            val matchedCustomers = customers.filter {
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                        it.phone.contains(searchQuery)
                            }.take(5)

                            Text(
                                text = "Hasil Pelanggan (${matchedCustomers.size} ditemukan):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            if (matchedCustomers.isEmpty()) {
                                Text("Tidak ada pelanggan yang cocok.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    matchedCustomers.forEach { cust ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(cust.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("WA: ${cust.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            if (cust.isInactive) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                                            shape = RoundedCornerShape(4.dp)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = if (cust.isInactive) "Tidak Aktif" else "Aktif",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (cust.isInactive) Color(0xFFC62828) else Color(0xFF2E7D32)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Gemini Advisor Panel (High-Value Feature!)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.SmartToy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Konsultan Pertumbuhan AI",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        if (!isAiLoading) {
                            IconButton(
                                onClick = { viewModel.getAiGrowthConsultation() },
                                modifier = Modifier.testTag("ai_trigger_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Muat Ulang AI",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isAiLoading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Menganalisis performa laundry Anda...", fontSize = 12.sp)
                        }
                    } else if (aiResponse != null) {
                        Text(
                            text = aiResponse ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text(
                                text = "Minta asisten AI menganalisis data pesanan, kepuasan pelanggan, dan memberikan rekomendasi strategis untuk Anda secara instan.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.getAiGrowthConsultation() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Tanya AI Advisor")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminLaporanTab(viewModel: LaundryViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Laporan & Analisa Bisnis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pantau statistik harian, performa layanan, pendapatan, dan perilaku pelanggan.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.LAPORAN_PEMESANAN) },
                modifier = Modifier.fillMaxWidth().testTag("menu_laporan_pemesanan"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Laporan Pemesanan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Tinjau pesanan harian, mingguan, bulanan, dan tahunan.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.LAPORAN_KAS) },
                modifier = Modifier.fillMaxWidth().testTag("menu_laporan_kas"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Laporan Arus Kas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Pantau saldo kasir, uang masuk, uang keluar, dan mutasi kas.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.ANALISA_PELANGGAN) },
                modifier = Modifier.fillMaxWidth().testTag("menu_analisa_pelanggan"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.tertiaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Analisa Pelanggan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Ketahui pelanggan terbaik & ingatkan pelanggan yang pasif.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                }
            }
        }

        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.ANALISA_LAYANAN) },
                modifier = Modifier.fillMaxWidth().testTag("menu_analisa_layanan"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.errorContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Analisa Layanan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Analisis perbandingan jenis layanan terlaris & profitnya.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }

        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.CARI_PELANGGAN) },
                modifier = Modifier.fillMaxWidth().testTag("menu_cari_pelanggan"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cari & Kelola Pelanggan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Database seluruh pelanggan laundry, no WA & riwayat.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun AdminPengaturanTab(viewModel: LaundryViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Pengaturan & Konfigurasi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Kelola data master operasional laundry, harga layanan, hak akses kasir, dan integrasi WhatsApp.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.KELOLA_KASIR) },
                modifier = Modifier.fillMaxWidth().testTag("menu_kelola_kasir"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Group, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Kelola Petugas Kasir", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Atur identitas, shift kerja, dan cabang penugasan kasir.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.KELOLA_OUTLET) },
                modifier = Modifier.fillMaxWidth().testTag("menu_kelola_outlet"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Kelola Cabang / Outlet", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Tambah, ubah nama, dan alamat outlet operasional.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.SETTING_KASIR) },
                modifier = Modifier.fillMaxWidth().testTag("menu_system_settings"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.tertiaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Konfigurasi Sistem Utama", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Atur kebijakan pembatalan, batasan diskon, harga kiloan/satuan, dan notifikasi WA.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun MenuGridItem(
    title: String,
    desc: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CashierDashboardScreen(viewModel: LaundryViewModel) {
    val activeCashier by viewModel.activeCashier.collectAsState()
    val activeOutlet by viewModel.activeOutlet.collectAsState()
    val activeShift by viewModel.activeShift.collectAsState()
    val orders by viewModel.ordersState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()

    // Filter Today's transactions for active cashier & outlet
    val cal = Calendar.getInstance()
    val todayStart = cal.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis

    val ordersToday = orders.filter {
        it.timestamp >= todayStart &&
                it.cashierName == (activeCashier?.name ?: "") &&
                it.outletName == (activeOutlet?.name ?: "")
    }

    val totalAmtToday = ordersToday.filter { it.status != "Batal" }.sumOf { it.finalTotal }

    // Toggle for stats view (governed by Settings)
    val showStats = settings?.statistikHarianEnabled == true

    var selectedTab by remember { mutableStateOf(0) }
    val nonCompletedOrders = orders.filter { it.status != "Diambil" && it.status != "Batal" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Menu Pelayanan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalLaundryService,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Antrean Cucian",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (nonCompletedOrders.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(MaterialTheme.colorScheme.error, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = nonCompletedOrders.size.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            )
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome and shift detail
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Selamat Bertugas, ${activeCashier?.name ?: "Kasir"}!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Shift: $activeShift",
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = activeOutlet?.name ?: "CucianKu",
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Today's Stats (Conditional ON/OFF governed by Setting Admin)
                if (showStats) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard(
                                title = "Order Hari Ini",
                                value = "${ordersToday.size} Laundry",
                                icon = Icons.Filled.ShoppingCart,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Nominal Hari Ini",
                                value = "Rp ${String.format("%,.0f", totalAmtToday)}",
                                icon = Icons.Filled.MonetizationOn,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Primary cash POS buttons
                item {
                    Text(
                        text = "Menu Pelayanan Kasir",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // FAB or big action button for New Order
                        Button(
                            onClick = { viewModel.navigateTo(Screen.ORDER_BARU) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("new_order_pos_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buat Order Laundry Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MenuGridItem(
                                title = "Kelola Pelanggan",
                                desc = "Tambah, ubah, cari pelanggan",
                                icon = Icons.Filled.Group,
                                onClick = { viewModel.navigateTo(Screen.CRUD_PELANGGAN) },
                                modifier = Modifier.weight(1f)
                            )
                            MenuGridItem(
                                title = "Mutasi Kasir",
                                desc = "Catat kas masuk/keluar harian",
                                icon = Icons.Filled.MonetizationOn,
                                onClick = {
                                    if (settings?.permissionBolehLihatLaporanKas == true) {
                                        viewModel.navigateTo(Screen.MUTASI_KAS)
                                    } else {
                                        viewModel.pendingWaNotification.value = "Akses ditolak: Anda tidak memiliki ijin (permission) dari owner untuk mengelola kas mutasi."
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Live laundry active queue status management
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Status & Alur Laundry Terkini",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "Real-time",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (nonCompletedOrders.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp, horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocalLaundryService,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Tidak ada cucian aktif di antrean.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(nonCompletedOrders) { order ->
                        OrderQueueCard(order = order, viewModel = viewModel, settings = settings)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderQueueCard(order: Order, viewModel: LaundryViewModel, settings: AppSettings?) {
    val statuses = listOf("Order Masuk", "Dicuci", "Disetrika", "Packing", "Selesai", "Diambil")
    val currentIndex = statuses.indexOf(order.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.customerName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${order.serviceType} • ${order.weightQty} Qty • Rp ${String.format("%,.0f", order.finalTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Current status Pill
                Surface(
                    color = when (order.status) {
                        "Order Masuk" -> MaterialTheme.colorScheme.primaryContainer
                        "Dicuci" -> MaterialTheme.colorScheme.secondaryContainer
                        "Disetrika" -> Color(0xFFFFECB3)
                        "Packing" -> Color(0xFFE1BEE7)
                        "Selesai" -> Color(0xFFC8E6C9)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            "Order Masuk" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "Dicuci" -> MaterialTheme.colorScheme.onSecondaryContainer
                            "Disetrika" -> Color(0xFFE65100)
                            "Packing" -> Color(0xFF4A148C)
                            "Selesai" -> Color(0xFF1B5E20)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (order.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Catatan: ${order.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row for moving statuses forward or cancellation
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cancel Button (checks permission)
                if (order.status != "Batal" && order.status != "Diambil") {
                    OutlinedButton(
                        onClick = {
                            if (settings?.permissionBolehBatal == true) {
                                viewModel.updateOrderStatus(order, "Batal")
                            } else {
                                viewModel.pendingWaNotification.value = "Akses ditolak: Anda tidak memiliki ijin (permission) dari owner untuk membatalkan pesanan."
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Batal", fontSize = 12.sp)
                    }
                }

                // Step status button
                if (currentIndex >= 0 && currentIndex < statuses.size - 1) {
                    val nextStatus = statuses[currentIndex + 1]
                    Button(
                        onClick = {
                            if (settings?.permissionBolehEdit == true) {
                                viewModel.updateOrderStatus(order, nextStatus)
                            } else {
                                viewModel.pendingWaNotification.value = "Akses ditolak: Anda tidak memiliki ijin (permission) dari owner untuk memperbarui status pesanan."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Update: $nextStatus", fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaKasirScreen(viewModel: LaundryViewModel) {
    val cashiers by viewModel.cashiersState.collectAsState()
    val outlets by viewModel.outletsState.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var selectedShift by remember { mutableStateOf("Pagi") }
    var isActiveInput by remember { mutableStateOf(true) }
    var selectedOutletName by remember { mutableStateOf("CucianKu Pusat") }
    var outletDropdownExpanded by remember { mutableStateOf(false) }

    var editingCashier by remember { mutableStateOf<Cashier?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (editingCashier == null) "Tambah Kasir Baru" else "Edit Kasir ${editingCashier?.cashierId}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nama Kasir") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cashier_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Pilih Shift Kerja", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Pagi", "Siang", "Malam").forEach { s ->
                            FilterChip(
                                selected = selectedShift == s,
                                onClick = { selectedShift = s },
                                label = { Text(s) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dropdown Outlet Placement
                    Text("Penempatan Lokasi Outlet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = outletDropdownExpanded,
                        onExpandedChange = { outletDropdownExpanded = !outletDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedOutletName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Lokasi Tugas") },
                            leadingIcon = { Icon(Icons.Filled.Storefront, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = outletDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("cashier_outlet_select"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = outletDropdownExpanded,
                            onDismissRequest = { outletDropdownExpanded = false }
                        ) {
                            // If outlets list is empty, default options are shown
                            val outletOptions = if (outlets.isEmpty()) {
                                listOf("CucianKu Pusat", "CucianKu Sudirman")
                            } else {
                                outlets.map { it.name }
                            }
                            
                            outletOptions.forEach { outName ->
                                DropdownMenuItem(
                                    text = { Text(outName) },
                                    onClick = {
                                        selectedOutletName = outName
                                        outletDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Status Kasir (Aktif/Nonaktif)", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = isActiveInput,
                            onCheckedChange = { isActiveInput = it },
                            modifier = Modifier.testTag("cashier_active_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (editingCashier != null) {
                            OutlinedButton(
                                onClick = {
                                    editingCashier = null
                                    nameInput = ""
                                    selectedShift = "Pagi"
                                    isActiveInput = true
                                    selectedOutletName = "CucianKu Pusat"
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Batal")
                            }
                        }

                        Button(
                            onClick = {
                                if (nameInput.isNotBlank()) {
                                    val currentEditing = editingCashier
                                    if (currentEditing == null) {
                                        viewModel.addCashier(nameInput, selectedShift, selectedOutletName, isActiveInput)
                                    } else {
                                        viewModel.updateCashier(
                                            currentEditing.copy(
                                                name = nameInput,
                                                shift = selectedShift,
                                                isActive = isActiveInput,
                                                assignedOutlet = selectedOutletName
                                            )
                                        )
                                        editingCashier = null
                                    }
                                    nameInput = ""
                                    selectedShift = "Pagi"
                                    isActiveInput = true
                                    selectedOutletName = "CucianKu Pusat"
                                }
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("cashier_save_button")
                        ) {
                            Text(if (editingCashier == null) "Simpan Kasir" else "Ubah Kasir")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Daftar Petugas Kasir",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (cashiers.isEmpty()) {
            item {
                Text("Belum ada kasir terdaftar.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(cashiers) { cashier ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${cashier.cashierId} - ${cashier.name}",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Outlet: ${cashier.assignedOutlet}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Shift: ${cashier.shift} • Status: ${if (cashier.isActive) "Aktif" else "Nonaktif"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                editingCashier = cashier
                                nameInput = cashier.name
                                selectedShift = cashier.shift
                                isActiveInput = cashier.isActive
                                selectedOutletName = cashier.assignedOutlet
                            }) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KelolaOutletScreen(viewModel: LaundryViewModel) {
    val outlets by viewModel.outletsState.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }
    var editingOutlet by remember { mutableStateOf<Outlet?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (editingOutlet == null) "Tambah Outlet Baru" else "Edit Outlet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nama Outlet") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("outlet_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("Alamat") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("outlet_address_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (editingOutlet != null) {
                            OutlinedButton(
                                onClick = {
                                    editingOutlet = null
                                    nameInput = ""
                                    addressInput = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Batal")
                            }
                        }

                        Button(
                            onClick = {
                                if (nameInput.isNotBlank() && addressInput.isNotBlank()) {
                                    val currentEditing = editingOutlet
                                    if (currentEditing == null) {
                                        viewModel.addOutlet(nameInput, addressInput)
                                    } else {
                                        viewModel.updateOutlet(currentEditing.copy(name = nameInput, address = addressInput))
                                        editingOutlet = null
                                    }
                                    nameInput = ""
                                    addressInput = ""
                                }
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("outlet_save_button")
                        ) {
                            Text(if (editingOutlet == null) "Simpan Outlet" else "Ubah Outlet")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Daftar Outlet CucianKu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (outlets.isEmpty()) {
            item {
                Text("Belum ada outlet terdaftar.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(outlets) { outlet ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = outlet.name,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = outlet.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                editingOutlet = outlet
                                nameInput = outlet.name
                                addressInput = outlet.address
                            }) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { viewModel.deleteOutlet(outlet) }) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CariPelangganScreen(viewModel: LaundryViewModel) {
    val customers by viewModel.customersState.collectAsState()
    var searchInput by remember { mutableStateOf("") }

    val filteredCustomers = customers.filter {
        it.name.contains(searchInput, ignoreCase = true) ||
                it.phone.contains(searchInput) ||
                it.id.toString().contains(searchInput)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchInput,
            onValueChange = { searchInput = it },
            placeholder = { Text("Cari berdasar nama, nomor WA, ID") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_customer_field"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hasil Pencarian (${filteredCustomers.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (filteredCustomers.isEmpty()) {
                item {
                    Text("Pelanggan tidak ditemukan.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(filteredCustomers) { cust ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "ID: PLG${cust.id.toString().padStart(4, '0')} • ${cust.name}", fontWeight = FontWeight.Bold)
                            Text(text = "WA: ${cust.phone}", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = "Status: ${if (cust.isInactive) "Tidak Aktif" else "Aktif"}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (cust.isInactive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CRUDPelangganScreen(viewModel: LaundryViewModel) {
    val customers by viewModel.customersState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var isInactiveInput by remember { mutableStateOf(false) }

    var editingCustomer by remember { mutableStateOf<Customer?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (editingCustomer == null) "Tambah Pelanggan Baru" else "Edit Pelanggan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cust_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Nomor WhatsApp (Contoh: 081234...)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cust_phone_input"),
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tandai Tidak Aktif (Inactive)", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = isInactiveInput,
                            onCheckedChange = { isInactiveInput = it },
                            modifier = Modifier.testTag("cust_inactive_toggle")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (editingCustomer != null) {
                            OutlinedButton(
                                onClick = {
                                    editingCustomer = null
                                    nameInput = ""
                                    phoneInput = ""
                                    isInactiveInput = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Batal")
                            }
                        }

                        Button(
                            onClick = {
                                if (nameInput.isNotBlank() && phoneInput.isNotBlank()) {
                                    val currentEditing = editingCustomer
                                    if (currentEditing == null) {
                                        viewModel.addCustomer(nameInput, phoneInput)
                                    } else {
                                        viewModel.updateCustomer(
                                            currentEditing.copy(
                                                name = nameInput,
                                                phone = phoneInput,
                                                isInactive = isInactiveInput
                                            )
                                        )
                                        editingCustomer = null
                                    }
                                    nameInput = ""
                                    phoneInput = ""
                                    isInactiveInput = false
                                }
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("cust_save_button")
                        ) {
                            Text(if (editingCustomer == null) "Simpan Pelanggan" else "Ubah Pelanggan")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Daftar Pelanggan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (customers.isEmpty()) {
            item {
                Text("Belum ada pelanggan terdaftar.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(customers) { cust ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PLG${cust.id.toString().padStart(4, '0')} - ${cust.name}",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "WA: ${cust.phone} • Status: ${if (cust.isInactive) "Inactive" else "Aktif"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                editingCustomer = cust
                                nameInput = cust.name
                                phoneInput = cust.phone
                                isInactiveInput = cust.isInactive
                            }) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                            }

                            IconButton(
                                onClick = {
                                    if (settings?.permissionBolehHapusPelanggan == true) {
                                        viewModel.deleteCustomer(cust)
                                    } else {
                                        viewModel.pendingWaNotification.value = "Akses ditolak: Anda tidak memiliki ijin (permission) dari owner untuk menghapus pelanggan."
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Hapus",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalisaPelangganScreen(viewModel: LaundryViewModel) {
    val customers by viewModel.customersState.collectAsState()
    val orders by viewModel.ordersState.collectAsState()
    val selectedOutlet by viewModel.adminSelectedOutlet.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var selectedPeriod by remember { mutableStateOf(0) } // 0 = Hari Ini vs Kemarin, 1 = Minggu Ini vs Minggu Lalu, 2 = Bulan Ini vs Bulan Lalu
    var selectedTab by remember { mutableStateOf(0) } // 0 = Top Customer, 1 = Pelanggan Inactive

    // Filter orders by selected outlet
    val ordersFiltered = if (selectedOutlet == "Semua Outlet") {
        orders
    } else {
        orders.filter { it.outletName == selectedOutlet }
    }

    // Date calculations
    val startOfToday = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val startOfYesterday = startOfToday - 24 * 60 * 60 * 1000L
    val startOfSevenDaysAgo = startOfToday - 7 * 24 * 60 * 60 * 1000L
    val startOfFourteenDaysAgo = startOfToday - 14 * 24 * 60 * 60 * 1000L
    val startOfThirtyDaysAgo = startOfToday - 30 * 24 * 60 * 60 * 1000L
    val startOfSixtyDaysAgo = startOfToday - 60 * 24 * 60 * 60 * 1000L

    val ordersA = when (selectedPeriod) {
        0 -> ordersFiltered.filter { it.timestamp >= startOfToday && it.status != "Batal" }
        1 -> ordersFiltered.filter { it.timestamp >= startOfSevenDaysAgo && it.status != "Batal" }
        else -> ordersFiltered.filter { it.timestamp >= startOfThirtyDaysAgo && it.status != "Batal" }
    }

    val ordersB = when (selectedPeriod) {
        0 -> ordersFiltered.filter { it.timestamp >= startOfYesterday && it.timestamp < startOfToday && it.status != "Batal" }
        1 -> ordersFiltered.filter { it.timestamp >= startOfFourteenDaysAgo && it.timestamp < startOfSevenDaysAgo && it.status != "Batal" }
        else -> ordersFiltered.filter { it.timestamp >= startOfSixtyDaysAgo && it.timestamp < startOfThirtyDaysAgo && it.status != "Batal" }
    }

    val labelA = when (selectedPeriod) {
        0 -> "Hari Ini"
        1 -> "Minggu Ini"
        else -> "Bulan Ini"
    }

    val labelB = when (selectedPeriod) {
        0 -> "Kemarin"
        1 -> "Minggu Lalu"
        else -> "Bulan Lalu"
    }

    // Spend mapping
    val customerSpendingMapA = ordersA.groupBy { it.customerPhone }.mapValues { it.value.sumOf { it.finalTotal } }
    val customerSpendingMapB = ordersB.groupBy { it.customerPhone }.mapValues { it.value.sumOf { it.finalTotal } }

    val topCustomers = customers.map { cust ->
        val totalSpent = customerSpendingMapA[cust.phone] ?: 0.0
        val spentPrev = customerSpendingMapB[cust.phone] ?: 0.0
        val diff = totalSpent - spentPrev
        Triple(cust, totalSpent, diff)
    }.filter { it.second > 0.0 || it.third != 0.0 }.sortedByDescending { it.second }

    // Inactive customers: marked isInactive, or 0 spending in A
    val inactiveCustomers = customers.filter { cust ->
        cust.isInactive || (customerSpendingMapA[cust.phone] ?: 0.0) == 0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Analisa Pelanggan CucianKu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Bandingkan statistik belanja pelanggan antar periode waktu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Outlet Dropdown Selector
        var outletExpanded by remember { mutableStateOf(false) }
        val outlets by viewModel.outletsState.collectAsState()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { outletExpanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Store,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Cabang / Outlet Terpilih:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedOutlet,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Box {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DropdownMenu(
                        expanded = outletExpanded,
                        onDismissRequest = { outletExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Semua Outlet", fontWeight = if (selectedOutlet == "Semua Outlet") FontWeight.Bold else FontWeight.Normal) },
                            onClick = {
                                viewModel.adminSelectedOutlet.value = "Semua Outlet"
                                outletExpanded = false
                            }
                        )
                        outlets.forEach { outlet ->
                            DropdownMenuItem(
                                text = { Text(outlet.name, fontWeight = if (selectedOutlet == outlet.name) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    viewModel.adminSelectedOutlet.value = outlet.name
                                    outletExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Period Dropdown Selector
        var periodExpanded by remember { mutableStateOf(false) }
        val periodOptions = listOf("Hari Ini vs Kemarin", "Minggu Ini vs Minggu Lalu", "Bulan Ini vs Bulan Lalu")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { periodExpanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Periode Analisis:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = periodOptions[selectedPeriod],
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Box {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DropdownMenu(
                        expanded = periodExpanded,
                        onDismissRequest = { periodExpanded = false }
                    ) {
                        periodOptions.forEachIndexed { index, title ->
                            DropdownMenuItem(
                                text = { Text(title, fontWeight = if (selectedPeriod == index) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    selectedPeriod = index
                                    periodExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Date range comparison subtext
        val formatEpochToDate: (Long) -> String = { epoch ->
            val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
            sdf.format(java.util.Date(epoch))
        }

        val dateLabelA = when (selectedPeriod) {
            0 -> formatEpochToDate(startOfToday)
            1 -> "${formatEpochToDate(startOfSevenDaysAgo)} - ${formatEpochToDate(System.currentTimeMillis())}"
            else -> "${formatEpochToDate(startOfThirtyDaysAgo)} - ${formatEpochToDate(System.currentTimeMillis())}"
        }

        val dateLabelB = when (selectedPeriod) {
            0 -> formatEpochToDate(startOfYesterday)
            1 -> "${formatEpochToDate(startOfFourteenDaysAgo)} - ${formatEpochToDate(startOfSevenDaysAgo - 1000L)}"
            else -> "${formatEpochToDate(startOfSixtyDaysAgo)} - ${formatEpochToDate(startOfThirtyDaysAgo - 1000L)}"
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$labelA ($dateLabelA) vs $labelB ($dateLabelB)",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Top Customer", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Pelanggan Inactive", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (selectedTab == 0) {
                if (topCustomers.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Tidak ada transaksi tercatat untuk $selectedOutlet pada periode ini.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(topCustomers) { (cust, spent, diff) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = cust.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = "WA: ${cust.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    // Comparison subtext
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val icon = if (diff > 0) Icons.Filled.TrendingUp else if (diff < 0) Icons.Filled.TrendingDown else Icons.Filled.HorizontalRule
                                        val tint = if (diff > 0) Color(0xFF2E7D32) else if (diff < 0) MaterialTheme.colorScheme.error else Color.Gray
                                        val text = if (diff > 0) {
                                            "+Rp ${String.format("%,.0f", diff)} vs $labelB"
                                        } else if (diff < 0) {
                                            "-Rp ${String.format("%,.0f", -diff)} vs $labelB"
                                        } else {
                                            "Stabil vs $labelB"
                                        }
                                        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = text, style = MaterialTheme.typography.bodySmall, color = tint, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rp ${String.format("%,.0f", spent)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(text = "Belanja $labelA", style = MaterialTheme.typography.bodySmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                if (inactiveCustomers.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Semua pelanggan aktif bertransaksi pada periode ini.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(inactiveCustomers) { cust ->
                        val spent = customerSpendingMapB[cust.phone] ?: 0.0
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = cust.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = "WA: ${cust.phone}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Transaksi di $labelB: Rp ${String.format("%,.0f", spent)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Belum belanja di $labelA",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                // Quick action to contact via WhatsApp
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${cust.phone}"))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Follow Up WA", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DayRevenueInfo(
    val dayLabel: String,
    val dateLabel: String,
    val totalRevenue: Double,
    val revenueByOutlet: Map<String, Double>,
    val txByOutlet: Map<String, Int>,
    val totalTx: Int
)

@Composable
fun colorForOutlet(outletName: String): Color {
    val hash = outletName.hashCode()
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        Color(0xFFE65100), // Orange
        Color(0xFF00796B), // Teal
        Color(0xFF7B1FA2), // Purple
        Color(0xFFC2185B), // Pink
        Color(0xFF1976D2), // Blue
        Color(0xFF388E3C)  // Green
    )
    return colors[Math.abs(hash) % colors.size]
}

@Composable
fun InteractiveOutletRevenueChart(orders: List<Order>, outlets: List<Outlet>) {
    var selectedDayIndex by remember { mutableStateOf(6) } // Default to today (last item)

    // Gather unique outlets from database + orders to ensure complete coverage
    val outletList = remember(outlets, orders) {
        (outlets.map { it.name } + orders.map { it.outletName })
            .distinct()
            .filter { it.isNotEmpty() }
    }

    // Generate data for the last 7 days (ending today)
    val dayData = remember(orders, outletList) {
        (0..6).map { index ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -(6 - index))
            val year = cal.get(Calendar.YEAR)
            val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)

            val sdfDay = java.text.SimpleDateFormat("EEE", java.util.Locale("id", "ID"))
            val sdfDate = java.text.SimpleDateFormat("dd/MM", java.util.Locale("id", "ID"))
            val dayLabel = sdfDay.format(cal.time)
            val dateLabel = sdfDate.format(cal.time)

            val dayOrders = orders.filter { order ->
                val calOrder = Calendar.getInstance().apply { timeInMillis = order.timestamp }
                order.status != "Batal" &&
                        calOrder.get(Calendar.YEAR) == year &&
                        calOrder.get(Calendar.DAY_OF_YEAR) == dayOfYear
            }

            // Map each outlet to its revenue on this day
            val revenueByOutlet = outletList.associateWith { name ->
                dayOrders.filter { it.outletName == name }.sumOf { it.finalTotal }
            }

            val txByOutlet = outletList.associateWith { name ->
                dayOrders.filter { it.outletName == name }.size
            }

            val totalRevenue = dayOrders.sumOf { it.finalTotal }

            DayRevenueInfo(
                dayLabel = dayLabel,
                dateLabel = dateLabel,
                totalRevenue = totalRevenue,
                revenueByOutlet = revenueByOutlet,
                txByOutlet = txByOutlet,
                totalTx = dayOrders.size
            )
        }
    }

    // Find max value for scaling the chart
    val maxDayRevenue = remember(dayData) {
        val maxVal = dayData.maxOfOrNull { it.totalRevenue } ?: 0.0
        if (maxVal == 0.0) 100000.0 else maxVal
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Komparasi Pendapatan Outlet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "7 Hari Terakhir",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Klik pada kolom chart untuk memantau performa harian masing-masing cabang.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // The Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Background Grid Lines and Y Axis Labels
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(1.0f, 0.5f, 0.0f).forEach { ratio ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (ratio == 0.0f) "0" else "Rp ${String.format("%,.0f", maxDayRevenue * ratio / 1000)}k",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                            )
                        }
                    }
                }

                // Interactive Columns & Highlight
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 45.dp, end = 4.dp, top = 8.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dayData.forEachIndexed { index, data ->
                        val isSelected = selectedDayIndex == index
                        val heightRatio = if (maxDayRevenue > 0.0) (data.totalRevenue / maxDayRevenue).toFloat() else 0f

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedDayIndex = index
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                // Background Highlight Band
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                    )
                                }

                                // Stacked / Total bar representing revenue
                                if (data.totalRevenue > 0.0) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .fillMaxHeight(heightRatio)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                            )
                                    )
                                } else {
                                    // Empty state placeholder tiny bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .height(4.dp)
                                            .background(
                                                MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(2.2f.dp)
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = data.dayLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tooltip / Interactive Details Panel
            val selectedData = dayData.getOrNull(selectedDayIndex)
            if (selectedData != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Detail Tanggal: ${selectedData.dateLabel} (${selectedData.dayLabel})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Total: Rp ${String.format("%,.0f", selectedData.totalRevenue)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        if (selectedData.totalRevenue == 0.0) {
                            Text(
                                text = "Tidak ada transaksi tercatat pada hari ini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            // Render each outlet's share
                            outletList.forEach { outletName ->
                                val revenue = selectedData.revenueByOutlet[outletName] ?: 0.0
                                val tx = selectedData.txByOutlet[outletName] ?: 0
                                val shareRatio = if (selectedData.totalRevenue > 0.0) (revenue / selectedData.totalRevenue).toFloat() else 0f

                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(
                                                        colorForOutlet(outletName),
                                                        shape = CircleShape
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = outletName,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Text(
                                            text = "Rp ${String.format("%,.0f", revenue)} ($tx trx)",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Custom visual share horizontal bar (Progress style)
                                    LinearProgressIndicator(
                                        progress = { shareRatio },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(CircleShape),
                                        color = colorForOutlet(outletName),
                                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ServiceComp(
    val type: String,
    val countA: Int,
    val revenueA: Double,
    val countDiff: Int,
    val revenueDiff: Double
)

@Composable
fun AnalisaLayananScreen(viewModel: LaundryViewModel) {
    val orders by viewModel.ordersState.collectAsState()
    val selectedOutlet by viewModel.adminSelectedOutlet.collectAsState()
    val outlets by viewModel.outletsState.collectAsState()

    var selectedPeriod by remember { mutableStateOf(0) } // 0 = Hari Ini vs Kemarin, 1 = Minggu Ini vs Minggu Lalu, 2 = Bulan Ini vs Bulan Lalu

    // Filter orders by selected outlet
    val ordersFiltered = if (selectedOutlet == "Semua Outlet") {
        orders
    } else {
        orders.filter { it.outletName == selectedOutlet }
    }

    // Limits
    val startOfToday = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val startOfYesterday = startOfToday - 24 * 60 * 60 * 1000L
    val startOfSevenDaysAgo = startOfToday - 7 * 24 * 60 * 60 * 1000L
    val startOfFourteenDaysAgo = startOfToday - 14 * 24 * 60 * 60 * 1000L
    val startOfThirtyDaysAgo = startOfToday - 30 * 24 * 60 * 60 * 1000L
    val startOfSixtyDaysAgo = startOfToday - 60 * 24 * 60 * 60 * 1000L

    val ordersA = when (selectedPeriod) {
        0 -> ordersFiltered.filter { it.timestamp >= startOfToday && it.status != "Batal" }
        1 -> ordersFiltered.filter { it.timestamp >= startOfSevenDaysAgo && it.status != "Batal" }
        else -> ordersFiltered.filter { it.timestamp >= startOfThirtyDaysAgo && it.status != "Batal" }
    }
    
    val ordersB = when (selectedPeriod) {
        0 -> ordersFiltered.filter { it.timestamp >= startOfYesterday && it.timestamp < startOfToday && it.status != "Batal" }
        1 -> ordersFiltered.filter { it.timestamp >= startOfFourteenDaysAgo && it.timestamp < startOfSevenDaysAgo && it.status != "Batal" }
        else -> ordersFiltered.filter { it.timestamp >= startOfSixtyDaysAgo && it.timestamp < startOfThirtyDaysAgo && it.status != "Batal" }
    }

    val labelA = when (selectedPeriod) {
        0 -> "Hari Ini"
        1 -> "Minggu Ini"
        else -> "Bulan Ini"
    }
    
    val labelB = when (selectedPeriod) {
        0 -> "Kemarin"
        1 -> "Minggu Lalu"
        else -> "Bulan Lalu"
    }

    // Service types analysis
    val services = listOf("Kiloan", "Satuan", "Meteran", "Express")
    val totalRevenueA = ordersA.sumOf { it.finalTotal }

    val serviceStats = services.map { type ->
        val typeOrdersA = ordersA.filter { it.serviceType == type }
        val countA = typeOrdersA.size
        val revenueA = typeOrdersA.sumOf { it.finalTotal }

        val typeOrdersB = ordersB.filter { it.serviceType == type }
        val countB = typeOrdersB.size
        val revenueB = typeOrdersB.sumOf { it.finalTotal }

        val countDiff = countA - countB
        val revenueDiff = revenueA - revenueB

        ServiceComp(type, countA, revenueA, countDiff, revenueDiff)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Analisa Layanan Laundry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Statistik omset & perbandingan performa layanan laundry.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Outlet Dropdown Selector
        item {
            var outletExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { outletExpanded = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Store,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Cabang / Outlet Terpilih:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedOutlet,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Box {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        DropdownMenu(
                            expanded = outletExpanded,
                            onDismissRequest = { outletExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Semua Outlet", fontWeight = if (selectedOutlet == "Semua Outlet") FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    viewModel.adminSelectedOutlet.value = "Semua Outlet"
                                    outletExpanded = false
                                }
                            )
                            outlets.forEach { outlet ->
                                DropdownMenuItem(
                                    text = { Text(outlet.name, fontWeight = if (selectedOutlet == outlet.name) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        viewModel.adminSelectedOutlet.value = outlet.name
                                        outletExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Period Dropdown Selector
        item {
            var periodExpanded by remember { mutableStateOf(false) }
            val periodOptions = listOf("Hari Ini vs Kemarin", "Minggu Ini vs Minggu Lalu", "Bulan Ini vs Bulan Lalu")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { periodExpanded = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Periode Analisis:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = periodOptions[selectedPeriod],
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Box {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        DropdownMenu(
                            expanded = periodExpanded,
                            onDismissRequest = { periodExpanded = false }
                        ) {
                            periodOptions.forEachIndexed { index, title ->
                                DropdownMenuItem(
                                    text = { Text(title, fontWeight = if (selectedPeriod == index) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        selectedPeriod = index
                                        periodExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Data Visualization Chart comparing Daily Revenue across Outlets (Recharts-style native Compose visualizer)
        item {
            InteractiveOutletRevenueChart(orders = orders, outlets = outlets)
        }

        // Date range comparison subtext item
        item {
            val formatEpochToDate: (Long) -> String = { epoch ->
                val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
                sdf.format(java.util.Date(epoch))
            }

            val dateLabelA = when (selectedPeriod) {
                0 -> formatEpochToDate(startOfToday)
                1 -> "${formatEpochToDate(startOfSevenDaysAgo)} - ${formatEpochToDate(System.currentTimeMillis())}"
                else -> "${formatEpochToDate(startOfThirtyDaysAgo)} - ${formatEpochToDate(System.currentTimeMillis())}"
            }

            val dateLabelB = when (selectedPeriod) {
                0 -> formatEpochToDate(startOfYesterday)
                1 -> "${formatEpochToDate(startOfFourteenDaysAgo)} - ${formatEpochToDate(startOfSevenDaysAgo - 1000L)}"
                else -> "${formatEpochToDate(startOfSixtyDaysAgo)} - ${formatEpochToDate(startOfThirtyDaysAgo - 1000L)}"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$labelA ($dateLabelA) vs $labelB ($dateLabelB)",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(serviceStats) { stat ->
            val ratio = if (totalRevenueA > 0.0) (stat.revenueA / totalRevenueA).toFloat() else 0f
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stat.type, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = "${stat.countA} Transaksi ($labelA)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Rp ${String.format("%,.0f", stat.revenueA)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "Omset $labelA", style = MaterialTheme.typography.bodySmall, fontSize = 9.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Compare traffic differences
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = if (stat.countDiff > 0) Icons.Filled.TrendingUp else if (stat.countDiff < 0) Icons.Filled.TrendingDown else Icons.Filled.HorizontalRule
                            val tint = if (stat.countDiff > 0) Color(0xFF2E7D32) else if (stat.countDiff < 0) MaterialTheme.colorScheme.error else Color.Gray
                            val countText = if (stat.countDiff > 0) {
                                "+${stat.countDiff} order vs $labelB"
                            } else if (stat.countDiff < 0) {
                                "${stat.countDiff} order vs $labelB"
                            } else {
                                "Sama vs $labelB"
                            }
                            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = countText, style = MaterialTheme.typography.bodySmall, color = tint, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Kontribusi: ${String.format("%.1f", ratio * 100)}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Revenue growth comparison
                    val revDiffTint = if (stat.revenueDiff > 0) Color(0xFF2E7D32) else if (stat.revenueDiff < 0) MaterialTheme.colorScheme.error else Color.Gray
                    val revDiffText = if (stat.revenueDiff > 0) {
                        "Omset Naik Rp ${String.format("%,.0f", stat.revenueDiff)} vs $labelB"
                    } else if (stat.revenueDiff < 0) {
                        "Omset Turun Rp ${String.format("%,.0f", -stat.revenueDiff)} vs $labelB"
                    } else {
                        "Omset Stabil vs $labelB"
                    }
                    Text(
                        text = revDiffText,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = revDiffTint,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LaporanPemesananScreen(viewModel: LaundryViewModel) {
    val orders by viewModel.ordersState.collectAsState()
    val selectedOutlet by viewModel.adminSelectedOutlet.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Harian, 1: Mingguan, 2: Bulanan, 3: Tahunan

    // Filter orders by selected outlet
    val ordersFiltered = if (selectedOutlet == "Semua Outlet") {
        orders
    } else {
        orders.filter { it.outletName == selectedOutlet }
    }

    val now = Calendar.getInstance()
    val filteredOrders = ordersFiltered.filter { order ->
        val calOrder = Calendar.getInstance().apply { timeInMillis = order.timestamp }
        when (selectedTab) {
            0 -> { // Today
                now.get(Calendar.YEAR) == calOrder.get(Calendar.YEAR) &&
                        now.get(Calendar.DAY_OF_YEAR) == calOrder.get(Calendar.DAY_OF_YEAR)
            }
            1 -> { // Last 7 days
                val diff = now.timeInMillis - order.timestamp
                diff <= 7L * 24 * 60 * 60 * 1000
            }
            2 -> { // This month
                now.get(Calendar.YEAR) == calOrder.get(Calendar.YEAR) &&
                        now.get(Calendar.MONTH) == calOrder.get(Calendar.MONTH)
            }
            else -> { // This year
                now.get(Calendar.YEAR) == calOrder.get(Calendar.YEAR)
            }
        }
    }

    val totalIncome = filteredOrders.filter { it.status != "Batal" }.sumOf { it.finalTotal }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var outletExpanded by remember { mutableStateOf(false) }
        val outlets by viewModel.outletsState.collectAsState()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { outletExpanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Store,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Laporan Cabang / Outlet:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedOutlet,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Box {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DropdownMenu(
                        expanded = outletExpanded,
                        onDismissRequest = { outletExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Semua Outlet", fontWeight = if (selectedOutlet == "Semua Outlet") FontWeight.Bold else FontWeight.Normal) },
                            onClick = {
                                viewModel.adminSelectedOutlet.value = "Semua Outlet"
                                outletExpanded = false
                            }
                        )
                        outlets.forEach { outlet ->
                            DropdownMenuItem(
                                text = { Text(outlet.name, fontWeight = if (selectedOutlet == outlet.name) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    viewModel.adminSelectedOutlet.value = outlet.name
                                    outletExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        ScrollableTabRow(selectedTabIndex = selectedTab) {
            listOf("Harian", "Mingguan", "Bulanan", "Tahunan").forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }) {
                    Text(text = title, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big summary total
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Pendapatan Terpilih",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Rp ${String.format("%,.0f", totalIncome)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${filteredOrders.size} Transaksi Laundry",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Daftar Transaksi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (filteredOrders.isEmpty()) {
                item {
                    Text("Tidak ada transaksi untuk periode ini.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(filteredOrders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = order.customerName, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Rp ${String.format("%,.0f", order.finalTotal)}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.status == "Batal") Color.Gray else MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "Layanan: ${order.serviceType} • Status: ${order.status}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            Text(
                                text = formatter.format(Date(order.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LaporanKasScreen(viewModel: LaundryViewModel) {
    val orders by viewModel.ordersState.collectAsState()
    val mutations by viewModel.cashMutationsState.collectAsState()
    val selectedOutlet by viewModel.adminSelectedOutlet.collectAsState()

    // Filter orders by selected outlet
    val ordersFiltered = if (selectedOutlet == "Semua Outlet") {
        orders
    } else {
        orders.filter { it.outletName == selectedOutlet }
    }

    // Calculate balances
    val tunaiOrders = ordersFiltered.filter { it.status != "Batal" && it.paymentMethod == "Tunai" }
    val transferOrders = ordersFiltered.filter { it.status != "Batal" && it.paymentMethod == "Transfer" }
    val qrisOrders = ordersFiltered.filter { it.status != "Batal" && it.paymentMethod == "QRIS" }
    val ewalletOrders = ordersFiltered.filter { it.status != "Batal" && it.paymentMethod == "E-Wallet" }

    val totalTunaiIncome = tunaiOrders.sumOf { it.finalTotal }
    val totalNonTunaiIncome = (transferOrders + qrisOrders + ewalletOrders).sumOf { it.finalTotal }

    val initialKas = mutations.filter { it.type == "Kas masuk" && it.notes.contains("Awal", true) }.sumOf { it.amount }
    val otherKasMasuk = mutations.filter { it.type == "Kas masuk" && !it.notes.contains("Awal", true) }.sumOf { it.amount }
    
    val kasKeluar = mutations.filter { it.type == "Kas keluar" || it.type == "Refund" || it.type == "Pengeluaran operasional" }.sumOf { it.amount }

    val totalSaldoAkhir = (initialKas + otherKasMasuk + totalTunaiIncome) - kasKeluar

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            var outletExpanded by remember { mutableStateOf(false) }
            val outlets by viewModel.outletsState.collectAsState()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .clickable { outletExpanded = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Store,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Laporan Keuangan Outlet:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedOutlet,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Box {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        DropdownMenu(
                            expanded = outletExpanded,
                            onDismissRequest = { outletExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Semua Outlet", fontWeight = if (selectedOutlet == "Semua Outlet") FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    viewModel.adminSelectedOutlet.value = "Semua Outlet"
                                    outletExpanded = false
                                }
                            )
                            outlets.forEach { outlet ->
                                DropdownMenuItem(
                                    text = { Text(outlet.name, fontWeight = if (selectedOutlet == outlet.name) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        viewModel.adminSelectedOutlet.value = outlet.name
                                        outletExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Estimasi Saldo Kas Tunai Saat Ini", color = Color.White, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "Rp ${String.format("%,.0f", totalSaldoAkhir)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Rincian Keuangan Harian", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    FinanceRow(label = "Saldo Awal Kasir", amount = initialKas)
                    FinanceRow(label = "Pendapatan POS Tunai", amount = totalTunaiIncome)
                    FinanceRow(label = "Pendapatan POS Non-Tunai", amount = totalNonTunaiIncome)
                    FinanceRow(label = "Lain-lain Kas Masuk", amount = otherKasMasuk)
                    FinanceRow(label = "Total Kas Keluar / Operasional", amount = kasKeluar, isNegative = true)
                }
            }
        }

        item {
            Text(text = "Histori Alur Kas Mutasi", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }

        if (mutations.isEmpty()) {
            item {
                Text("Tidak ada mutasi kas hari ini.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(mutations) { mut ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = mut.type, fontWeight = FontWeight.Bold)
                            Text(text = mut.notes, style = MaterialTheme.typography.bodySmall)
                            val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
                            Text(text = formatter.format(Date(mut.timestamp)), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Text(
                            text = "Rp ${String.format("%,.0f", mut.amount)}",
                            fontWeight = FontWeight.Bold,
                            color = if (mut.type.contains("keluar", true) || mut.type.contains("operasional", true) || mut.type.contains("Refund", true)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FinanceRow(label: String, amount: Double, isNegative: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "${if (isNegative) "- " else ""}Rp ${String.format("%,.0f", amount)}",
            fontWeight = FontWeight.Bold,
            color = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SettingKasirScreen(viewModel: LaundryViewModel) {
    val settings by viewModel.settingsState.collectAsState()

    var statistikHarianEnabled by remember { mutableStateOf(true) }
    var permissionBolehBatal by remember { mutableStateOf(true) }
    var permissionBolehEdit by remember { mutableStateOf(true) }
    var permissionBolehHapusPelanggan by remember { mutableStateOf(true) }
    var permissionBolehLihatLaporanKas by remember { mutableStateOf(true) }
    var permissionBolehLihatSaldo by remember { mutableStateOf(true) }

    var maksDiskonInput by remember { mutableStateOf("") }
    var priceKiloanInput by remember { mutableStateOf("") }
    var priceSatuanInput by remember { mutableStateOf("") }
    var priceMeteranInput by remember { mutableStateOf("") }
    var priceExpressInput by remember { mutableStateOf("") }

    var isWaOnOrderCreated by remember { mutableStateOf(true) }
    var isWaOnOrderFinished by remember { mutableStateOf(true) }
    var isWaOnOrderCancelled by remember { mutableStateOf(true) }

    LaunchedEffect(settings) {
        settings?.let { s ->
            statistikHarianEnabled = s.statistikHarianEnabled
            permissionBolehBatal = s.permissionBolehBatal
            permissionBolehEdit = s.permissionBolehEdit
            permissionBolehHapusPelanggan = s.permissionBolehHapusPelanggan
            permissionBolehLihatLaporanKas = s.permissionBolehLihatLaporanKas
            permissionBolehLihatSaldo = s.permissionBolehLihatSaldo
            maksDiskonInput = s.maksDiskonManual.toInt().toString()
            priceKiloanInput = s.priceKiloan.toInt().toString()
            priceSatuanInput = s.priceSatuan.toInt().toString()
            priceMeteranInput = s.priceMeteran.toInt().toString()
            priceExpressInput = s.priceExpress.toInt().toString()
            isWaOnOrderCreated = s.isWaOnOrderCreated
            isWaOnOrderFinished = s.isWaOnOrderFinished
            isWaOnOrderCancelled = s.isWaOnOrderCancelled
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Statistik Dashboard Kasir", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tampilkan Statistik Harian Kasir")
                    Switch(
                        checked = statistikHarianEnabled,
                        onCheckedChange = { statistikHarianEnabled = it },
                        modifier = Modifier.testTag("setting_stats_toggle")
                    )
                }
            }
        }

        item {
            Text(text = "Permission / Ijin Kasir", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PermissionToggle(label = "Boleh Batalkan Pesanan", checked = permissionBolehBatal, onCheckedChange = { permissionBolehBatal = it })
                    PermissionToggle(label = "Boleh Edit / Update Status", checked = permissionBolehEdit, onCheckedChange = { permissionBolehEdit = it })
                    PermissionToggle(label = "Boleh Hapus Pelanggan", checked = permissionBolehHapusPelanggan, onCheckedChange = { permissionBolehHapusPelanggan = it })
                    PermissionToggle(label = "Boleh Kelola Mutasi Kas", checked = permissionBolehLihatLaporanKas, onCheckedChange = { permissionBolehLihatLaporanKas = it })
                    PermissionToggle(label = "Boleh Lihat Saldo", checked = permissionBolehLihatSaldo, onCheckedChange = { permissionBolehLihatSaldo = it })
                }
            }
        }

        item {
            Text(text = "Daftar Harga Layanan (Rp)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = priceKiloanInput, onValueChange = { priceKiloanInput = it }, label = { Text("Harga Kiloan / Kg") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = priceSatuanInput, onValueChange = { priceSatuanInput = it }, label = { Text("Harga Satuan / Pcs") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = priceMeteranInput, onValueChange = { priceMeteranInput = it }, label = { Text("Harga Meteran / Meter") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = priceExpressInput, onValueChange = { priceExpressInput = it }, label = { Text("Harga Express") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = maksDiskonInput, onValueChange = { maksDiskonInput = it }, label = { Text("Maksimal Diskon Manual (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
            }
        }

        item {
            Text(text = "Notifikasi WhatsApp", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PermissionToggle(label = "Kirim WA Saat Order Dibuat", checked = isWaOnOrderCreated, onCheckedChange = { isWaOnOrderCreated = it })
                    PermissionToggle(label = "Kirim WA Saat Laundry Selesai", checked = isWaOnOrderFinished, onCheckedChange = { isWaOnOrderFinished = it })
                    PermissionToggle(label = "Kirim WA Saat Order Dibatalkan", checked = isWaOnOrderCancelled, onCheckedChange = { isWaOnOrderCancelled = it })
                }
            }
        }

        item {
            Button(
                onClick = {
                    val updated = AppSettings(
                        1,
                        statistikHarianEnabled,
                        permissionBolehBatal,
                        permissionBolehEdit,
                        permissionBolehHapusPelanggan,
                        permissionBolehLihatLaporanKas,
                        permissionBolehLihatSaldo,
                        maksDiskonInput.toDoubleOrNull() ?: 50000.0,
                        priceKiloanInput.toDoubleOrNull() ?: 6000.0,
                        priceSatuanInput.toDoubleOrNull() ?: 10000.0,
                        priceMeteranInput.toDoubleOrNull() ?: 12000.0,
                        priceExpressInput.toDoubleOrNull() ?: 15000.0,
                        isWaOnOrderCreated,
                        isWaOnOrderFinished,
                        isWaOnOrderCancelled
                    )
                    viewModel.updateSettings(updated)
                    viewModel.pendingWaNotification.value = "Pengaturan berhasil disimpan ke sistem!"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_settings_button")
            ) {
                Text("Simpan Seluruh Pengaturan", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PermissionToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBaruScreen(viewModel: LaundryViewModel) {
    val settings by viewModel.settingsState.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("Kiloan") }
    var weightQty by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var discountInput by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Tunai") }

    var serviceExpanded by remember { mutableStateOf(false) }
    var paymentExpanded by remember { mutableStateOf(false) }

    // Real-time calculation
    val basePrice = when (serviceType) {
        "Kiloan" -> settings?.priceKiloan ?: 6000.0
        "Satuan" -> settings?.priceSatuan ?: 10000.0
        "Meteran" -> settings?.priceMeteran ?: 12000.0
        "Express" -> settings?.priceExpress ?: 15000.0
        else -> 6000.0
    }

    val qtyDouble = weightQty.toDoubleOrNull() ?: 0.0
    val totalRaw = basePrice * qtyDouble
    val discDouble = discountInput.toDoubleOrNull() ?: 0.0
    val finalTotal = (totalRaw - discDouble).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Rincian Pelanggan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Nama Pelanggan") }, modifier = Modifier.fillMaxWidth().testTag("order_cust_name"))
                    OutlinedTextField(value = customerPhone, onValueChange = { customerPhone = it }, label = { Text("Nomor WhatsApp") }, modifier = Modifier.fillMaxWidth().testTag("order_cust_phone"), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                }
            }
        }

        item {
            Text(text = "Detail Layanan Laundry", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    
                    // Dropdown Service Type
                    ExposedDropdownMenuBox(
                        expanded = serviceExpanded,
                        onExpandedChange = { serviceExpanded = !serviceExpanded }
                    ) {
                        OutlinedTextField(
                            value = serviceType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jenis Laundry") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = serviceExpanded,
                            onDismissRequest = { serviceExpanded = false }
                        ) {
                            listOf("Kiloan", "Satuan", "Meteran", "Express").forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("$s (Rp ${String.format("%,.0f", when(s){ "Kiloan" -> settings?.priceKiloan ?: 6000.0; "Satuan" -> settings?.priceSatuan ?: 10000.0; "Meteran" -> settings?.priceMeteran ?: 12000.0; else -> settings?.priceExpress ?: 15000.0 })})") },
                                    onClick = {
                                        serviceType = s
                                        serviceExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = weightQty,
                        onValueChange = { weightQty = it },
                        label = { Text("Berat / Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("order_qty")
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan Pesanan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Text(text = "Pembayaran & Diskon", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    
                    OutlinedTextField(
                        value = discountInput,
                        onValueChange = { discountInput = it },
                        label = { Text("Potongan Diskon Manual (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("order_discount")
                    )

                    // Dropdown Payment Method
                    ExposedDropdownMenuBox(
                        expanded = paymentExpanded,
                        onExpandedChange = { paymentExpanded = !paymentExpanded }
                    ) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Metode Pembayaran") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = paymentExpanded,
                            onDismissRequest = { paymentExpanded = false }
                        ) {
                            listOf("Tunai", "Transfer", "QRIS", "E-Wallet").forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p) },
                                    onClick = {
                                        paymentMethod = p
                                        paymentExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live calculation summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                        Text("Rp ${String.format("%,.0f", totalRaw)}", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Diskon", style = MaterialTheme.typography.bodyMedium)
                        Text("- Rp ${String.format("%,.0f", discDouble)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Pembayaran", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Rp ${String.format("%,.0f", finalTotal)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (customerName.isNotBlank() && customerPhone.isNotBlank() && qtyDouble > 0.0) {
                        val maxDisc = settings?.maksDiskonManual ?: 50000.0
                        if (discDouble > maxDisc) {
                            viewModel.pendingWaNotification.value = "Diskon ditolak! Diskon manual tidak boleh melebihi batas owner sebesar Rp ${String.format("%,.0f", maxDisc)}"
                        } else {
                            viewModel.createOrder(
                                customerName,
                                customerPhone,
                                serviceType,
                                qtyDouble,
                                notes,
                                discDouble,
                                paymentMethod
                            )
                            viewModel.navigateBack()
                        }
                    } else {
                        viewModel.pendingWaNotification.value = "Harap lengkapi semua field pelanggan dan jumlah berat laundry!"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_order_button")
            ) {
                Text("Buat Pesanan Baru", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MutasiKasScreen(viewModel: LaundryViewModel) {
    var amountInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Kas keluar") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Catat Kas Mutasi Baru", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    
                    Text("Tipe Kas Mutasi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Kas masuk", "Kas keluar", "Refund", "Pengeluaran operasional").forEach { t ->
                            FilterChip(
                                selected = selectedType == t,
                                onClick = { selectedType = t },
                                label = { Text(t, fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Jumlah Uang (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("mutation_amount"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Keterangan Catatan") },
                        modifier = Modifier.fillMaxWidth().testTag("mutation_notes"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val amt = amountInput.toDoubleOrNull() ?: 0.0
                            if (amt > 0.0 && notesInput.isNotBlank()) {
                                viewModel.addCashMutation(selectedType, amt, notesInput)
                                amountInput = ""
                                notesInput = ""
                                viewModel.navigateBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_mutation_button")
                    ) {
                        Text("Simpan Kas Mutasi")
                    }
                }
            }
        }
    }
}
