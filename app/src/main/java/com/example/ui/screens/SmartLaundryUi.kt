package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
    val selectedReceiptOrder by viewModel.selectedReceiptOrder.collectAsState()
    val context = LocalContext.current

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            if (currentScreen != Screen.SPLASH && currentScreen != Screen.ROLE_SELECTION) {
                TopAppBar(
                    windowInsets = WindowInsets.statusBars,
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
        val isDashboard = currentScreen == Screen.ADMIN_DASHBOARD || currentScreen == Screen.CASHIER_DASHBOARD
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(if (!isDashboard) Modifier.navigationBarsPadding() else Modifier)
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

                            val parsedWa = if (isWhatsApp) {
                                try {
                                    val phoneStart = waMessage.indexOf('(')
                                    val phoneEnd = waMessage.indexOf(')')
                                    if (phoneStart != -1 && phoneEnd != -1 && phoneEnd > phoneStart) {
                                        val phone = waMessage.substring(phoneStart + 1, phoneEnd).filter { it.isDigit() }
                                        val messageStart = waMessage.indexOf('\n')
                                        if (messageStart != -1) {
                                            var msg = waMessage.substring(messageStart + 1)
                                            if (msg.startsWith("\"") && msg.endsWith("\"")) {
                                                msg = msg.substring(1, msg.length - 1)
                                            }
                                            Pair(phone, msg)
                                        } else null
                                    } else null
                                } catch (e: Exception) { null }
                            } else null

                            if (parsedWa != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            var cleanPhone = parsedWa.first
                                            if (cleanPhone.startsWith("0")) {
                                                cleanPhone = "62" + cleanPhone.substring(1)
                                            }
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                data = android.net.Uri.parse("https://api.whatsapp.com/send?phone=${cleanPhone}&text=${android.net.Uri.encode(parsedWa.second)}")
                                            }
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.weight(1.2f).testTag("wa_send_real_button")
                                    ) {
                                        Text("Kirim WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.clearPendingWaNotification() },
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.weight(0.8f).testTag("wa_ok_button")
                                    ) {
                                        Text("Tutup", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            } else {
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

            // Receipt Dialog Overlay (Simulated Thermal Print receipt)
            selectedReceiptOrder?.let { order ->
                ReceiptDialog(order = order, viewModel = viewModel)
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
    var adminUsername by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    // Kasir state
    var selectedCashier by remember { mutableStateOf<Cashier?>(null) }
    var selectedOutlet by remember { mutableStateOf<Outlet?>(null) }
    var selectedShift by remember { mutableStateOf("Pagi") }

    var cashierExpanded by remember { mutableStateOf(false) }
    var outletExpanded by remember { mutableStateOf(false) }
    var shiftExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCashier, outlets) {
        selectedCashier?.let { cashier ->
            val matchedOutlet = outlets.find { it.name.trim().equals(cashier.assignedOutlet.trim(), ignoreCase = true) }
            if (matchedOutlet != null) {
                selectedOutlet = matchedOutlet
            } else if (cashier.assignedOutlet.isNotEmpty()) {
                selectedOutlet = Outlet(name = cashier.assignedOutlet, address = "")
            }
        } ?: run {
            selectedOutlet = null
        }
    }

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

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

                            TextButton(
                                onClick = { showRegisterDialog = true },
                                modifier = Modifier.testTag("register_account_button")
                            ) {
                                Text(
                                    text = "Daftar Akun Baru",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val expectedUsername = viewModel.adminUsernameState.value
                                val expectedPassword = viewModel.adminPasswordState.value
                                if (adminUsername == expectedUsername && adminPassword == expectedPassword) {
                                    viewModel.selectedRole.value = "Admin"
                                    viewModel.navigateTo(Screen.ADMIN_DASHBOARD)
                                    loginError = null
                                } else if (adminUsername.isEmpty() || adminPassword.isEmpty()) {
                                    loginError = "Username dan kata sandi tidak boleh kosong."
                                } else {
                                    loginError = "Username atau kata sandi salah."
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
                                cashiers.filter { it.isActive }.forEach { cashier ->
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

                        // Lokasi Outlet (Otomatis berdasarkan Kasir, tidak bisa dipilih manual)
                        OutlinedTextField(
                            value = selectedOutlet?.name ?: "Pilih Kasir Terlebih Dahulu",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Lokasi Outlet") },
                            leadingIcon = { Icon(Icons.Filled.Storefront, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("outlet_select"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            )
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

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (selectedCashier != null && selectedOutlet != null) {
                                    viewModel.selectedRole.value = "Kasir"
                                    viewModel.activeCashier.value = selectedCashier
                                    viewModel.activeOutlet.value = selectedOutlet
                                    viewModel.activeShift.value = selectedShift
                                    viewModel.navigateTo(Screen.CASHIER_DASHBOARD)
                                    loginError = null
                                } else {
                                    loginError = "Harap lengkapi pilihan Kasir dan Outlet Anda."
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

    // Forgot Password Alert Dialog (Interactive Reset Flow)
    if (showForgotDialog) {
        var recoveryUsername by remember { mutableStateOf("") }
        var recoveryPin by remember { mutableStateOf("") }
        var newSecretPassword by remember { mutableStateOf("") }
        var resetError by remember { mutableStateOf<String?>(null) }
        var resetSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LockReset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Atur Ulang Sandi Admin", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (!resetSuccess) {
                        Text(
                            text = "Masukkan detail akun Admin dan PIN Pemulihan Default (1234) untuk menyetel kata sandi baru.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = recoveryUsername,
                            onValueChange = { recoveryUsername = it },
                            label = { Text("Username Admin") },
                            placeholder = { Text("Contoh: admin") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = recoveryPin,
                            onValueChange = { recoveryPin = it },
                            label = { Text("PIN Pemulihan") },
                            placeholder = { Text("Default: 1234") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newSecretPassword,
                            onValueChange = { newSecretPassword = it },
                            label = { Text("Kata Sandi Baru") },
                            placeholder = { Text("Minimal 5 karakter") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (resetError != null) {
                            Text(
                                text = resetError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Atur Ulang Sandi Berhasil!",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Silakan masuk menggunakan kata sandi baru Anda.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (!resetSuccess) {
                    Button(
                        onClick = {
                            if (recoveryUsername.trim().lowercase() != viewModel.adminUsernameState.value.lowercase()) {
                                resetError = "Username admin tidak cocok."
                            } else if (recoveryPin != "1234") {
                                resetError = "PIN Pemulihan salah! Gunakan default: 1234"
                            } else if (newSecretPassword.length < 5) {
                                resetError = "Sandi baru minimal 5 karakter."
                            } else {
                                viewModel.adminPasswordState.value = newSecretPassword
                                resetError = null
                                resetSuccess = true
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan Sandi Baru")
                    }
                } else {
                    Button(
                        onClick = {
                            showForgotDialog = false
                            adminUsername = viewModel.adminUsernameState.value
                            adminPassword = newSecretPassword
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Selesai & Masuk")
                    }
                }
            },
            dismissButton = {
                if (!resetSuccess) {
                    TextButton(onClick = { showForgotDialog = false }) {
                        Text("Batal")
                    }
                }
            }
        )
    }

    // Register Account Dialog (Owner Registration Flow)
    if (showRegisterDialog) {
        var newUsername by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var registerError by remember { mutableStateOf<String?>(null) }
        var registerSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pendaftaran Akun Owner", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (!registerSuccess) {
                        Text(
                            text = "Daftarkan akun administrator / owner baru untuk aplikasi Smart Laundry ini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = newUsername,
                            onValueChange = { newUsername = it },
                            label = { Text("Username Baru") },
                            placeholder = { Text("Masukkan username baru") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("register_username_input")
                        )
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Kata Sandi Baru") },
                            placeholder = { Text("Minimal 5 karakter") },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("register_password_input")
                        )
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Konfirmasi Kata Sandi") },
                            placeholder = { Text("Ulangi kata sandi baru") },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("register_confirm_password_input")
                        )
                        if (registerError != null) {
                            Text(
                                text = registerError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pendaftaran Berhasil!",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Akun Owner Anda telah berhasil didaftarkan ke sistem.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (!registerSuccess) {
                    Button(
                        onClick = {
                            if (newUsername.trim().isEmpty()) {
                                registerError = "Username tidak boleh kosong."
                            } else if (newPassword.length < 5) {
                                registerError = "Sandi baru minimal 5 karakter."
                            } else if (newPassword != confirmPassword) {
                                registerError = "Konfirmasi kata sandi tidak cocok."
                            } else {
                                viewModel.adminUsernameState.value = newUsername.trim()
                                viewModel.adminPasswordState.value = newPassword
                                registerError = null
                                registerSuccess = true
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Daftar Akun")
                    }
                } else {
                    Button(
                        onClick = {
                            showRegisterDialog = false
                            adminUsername = newUsername.trim()
                            adminPassword = newPassword
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Masuk Sekarang")
                    }
                }
            },
            dismissButton = {
                if (!registerSuccess) {
                    TextButton(onClick = { showRegisterDialog = false }) {
                        Text("Batal")
                    }
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
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val navItems = listOf(
                    Triple(0, Icons.Filled.Home, "Beranda"),
                    Triple(1, Icons.Filled.Assessment, "Laporan"),
                    Triple(2, Icons.Filled.Settings, "Pengaturan")
                )
                navItems.forEach { (index, icon, label) ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.adminSelectedTab.value = index },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag(
                            if (index == 0) "nav_beranda" 
                            else if (index == 1) "nav_laporan" 
                            else "nav_pengaturan"
                        )
                    )
                }
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
                else -> AdminHomeTab(viewModel)
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

    // Filter customers by selected outlet
    val customersFiltered = if (adminSelectedOutlet == "Semua Outlet") {
        customers
    } else {
        val phonesWithOrders = ordersFiltered.map { it.customerPhone }.toSet()
        customers.filter { it.registeredOutletName == adminSelectedOutlet || it.phone in phonesWithOrders }
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
        // 1. Premium Header Banner (Combines Welcome Greeting & Outlet Selector elegantly)
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
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Halo Owner! 👋",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Kelola & pantau performa laundry Anda.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        // Compact, integrated Outlet Dropdown Button
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            Button(
                                onClick = { expanded = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Store,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (adminSelectedOutlet == "Semua Outlet") "Semua" else adminSelectedOutlet,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
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
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Outlet Terpilih:",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = adminSelectedOutlet,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
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
                        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pendapatan Hari Ini",
                        value = "Rp ${String.format("%,.0f", totalRevenueToday)}",
                        icon = Icons.Filled.MonetizationOn,
                        iconContainerColor = Color(0xFFE8F5E9),
                        iconColor = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1.2f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        title = "Jumlah Pelanggan",
                        value = "${customersFiltered.size} Orang",
                        icon = Icons.Filled.Group,
                        iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Selesai / Proses",
                        value = "$completedCount / $progressCount",
                        icon = Icons.Filled.LocalLaundryService,
                        iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        iconColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Dedicated Outlet Financial Summary Card
        item {
            OutletFinancialSummaryCard(
                outletName = adminSelectedOutlet,
                orders = orders
            )
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
                            val matchedCustomers = customersFiltered.filter {
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val orders by viewModel.ordersState.collectAsState()
    val mutations by viewModel.cashMutationsState.collectAsState()
    val expenses by viewModel.expensesState.collectAsState()
    val outlets by viewModel.outletsState.collectAsState()
    
    val selectedOutlet by viewModel.adminSelectedOutlet.collectAsState()
    var outletDropdownExpanded by remember { mutableStateOf(false) }
    
    // We can control navigation to Penambahan Kas or Pengurangan Kas via subScreen state
    var currentSubScreen by remember { mutableStateOf("main") } // "main", "penambahan", "pengurangan"
    
    // Form states for Penambahan/Pengurangan Kas
    var formTipeKas by remember { mutableStateOf("Tunai") } // "Tunai", "Non-Tunai"
    var formJumlah by remember { mutableStateOf("") }
    var formKeterangan by remember { mutableStateOf("") }
    var tipeDropdownExpanded by remember { mutableStateOf(false) }

    // Set default selected outlet if not defined
    LaunchedEffect(outlets) {
        if (outlets.isNotEmpty() && selectedOutlet.isBlank()) {
            viewModel.adminSelectedOutlet.value = "Semua Outlet"
        }
    }

    // Number format helper
    val formatRupiah: (Double) -> String = { amt ->
        "Rp " + String.format(java.util.Locale("id", "ID"), "%,.0f", amt)
    }

    // Filter calculations
    val ordersFiltered = if (selectedOutlet == "Semua Outlet") orders else orders.filter { it.outletName == selectedOutlet }
    val mutationsFiltered = if (selectedOutlet == "Semua Outlet") mutations else mutations.filter { it.notes.contains("[Outlet: $selectedOutlet]", true) }
    val expensesFiltered = if (selectedOutlet == "Semua Outlet") expenses else expenses.filter { it.outletName == selectedOutlet }

    // Tunai calculations
    val totalTunaiIncome = ordersFiltered.filter { it.status != "Batal" && it.paymentMethod == "Tunai" }.sumOf { it.finalTotal }
    val mutationsMasukTunai = mutationsFiltered.filter { (it.type == "Kas masuk" || it.type == "Kas Masuk") && !it.notes.contains("[Non-Tunai]", true) }.sumOf { it.amount }
    val mutationsKeluarTunai = mutationsFiltered.filter { (it.type == "Kas keluar" || it.type == "Kas Keluar" || it.type == "Refund" || it.type == "Pengeluaran operasional") && !it.notes.contains("[Non-Tunai]", true) }.sumOf { it.amount }
    val expensesTunai = expensesFiltered.filter { !it.notes.contains("[Non-Tunai]", true) }.sumOf { it.amount }
    val saldoTunai = (totalTunaiIncome + mutationsMasukTunai) - (mutationsKeluarTunai + expensesTunai)

    // Non-Tunai calculations
    val totalNonTunaiIncome = ordersFiltered.filter { it.status != "Batal" && (it.paymentMethod == "Transfer" || it.paymentMethod == "QRIS" || it.paymentMethod == "E-Wallet") }.sumOf { it.finalTotal }
    val mutationsMasukNonTunai = mutationsFiltered.filter { (it.type == "Kas masuk" || it.type == "Kas Masuk") && it.notes.contains("[Non-Tunai]", true) }.sumOf { it.amount }
    val mutationsKeluarNonTunai = mutationsFiltered.filter { (it.type == "Kas keluar" || it.type == "Kas Keluar" || it.type == "Refund" || it.type == "Pengeluaran operasional") && it.notes.contains("[Non-Tunai]", true) }.sumOf { it.amount }
    val expensesNonTunai = expensesFiltered.filter { it.notes.contains("[Non-Tunai]", true) }.sumOf { it.amount }
    val saldoNonTunai = (totalNonTunaiIncome + mutationsMasukNonTunai) - (mutationsKeluarNonTunai + expensesNonTunai)

    when (currentSubScreen) {
        "penambahan" -> {
            // PENAMBAHAN KAS SCREEN
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentSubScreen = "main" }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PENAMBAHAN KAS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Saldo Kas Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Saldo Kas (${selectedOutlet})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Saldo Tunai", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(formatRupiah(saldoTunai), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Saldo Non-Tunai", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(formatRupiah(saldoNonTunai), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Form fields
                Text(text = "Tipe Kas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = formTipeKas,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tipeDropdownExpanded = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { tipeDropdownExpanded = true })
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = tipeDropdownExpanded,
                        onDismissRequest = { tipeDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tunai") },
                            onClick = {
                                formTipeKas = "Tunai"
                                tipeDropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Non-Tunai") },
                            onClick = {
                                formTipeKas = "Non-Tunai"
                                tipeDropdownExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Jumlah (Rp)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formJumlah,
                    onValueChange = { if (it.all { char -> char.isDigit() }) formJumlah = it },
                    placeholder = { Text("Masukkan jumlah kas") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Keterangan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formKeterangan,
                    onValueChange = { formKeterangan = it },
                    placeholder = { Text("Keterangan penambahan kas") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val amount = formJumlah.toDoubleOrNull() ?: 0.0
                        if (amount <= 0) {
                            android.widget.Toast.makeText(context, "Jumlah harus diisi dengan valid!", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            val notesWithMeta = "[Outlet: $selectedOutlet] ${if (formTipeKas == "Non-Tunai") "[Non-Tunai] " else ""}$formKeterangan".trim()
                            viewModel.addCashMutation("Kas masuk", amount, notesWithMeta)
                            android.widget.Toast.makeText(context, "Berhasil menambahkan kas!", android.widget.Toast.LENGTH_SHORT).show()
                            // Clear and go back
                            formJumlah = ""
                            formKeterangan = ""
                            currentSubScreen = "main"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Tambahkan Kas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        "pengurangan" -> {
            // PENGURANGAN KAS SCREEN
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentSubScreen = "main" }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PENGURANGAN KAS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Saldo Kas Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Saldo Kas (${selectedOutlet})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Saldo Tunai", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Text(formatRupiah(saldoTunai), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Saldo Non-Tunai", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Text(formatRupiah(saldoNonTunai), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Form fields
                Text(text = "Tipe Kas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = formTipeKas,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tipeDropdownExpanded = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { tipeDropdownExpanded = true })
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = tipeDropdownExpanded,
                        onDismissRequest = { tipeDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tunai") },
                            onClick = {
                                formTipeKas = "Tunai"
                                tipeDropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Non-Tunai") },
                            onClick = {
                                formTipeKas = "Non-Tunai"
                                tipeDropdownExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Jumlah (Rp)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formJumlah,
                    onValueChange = { if (it.all { char -> char.isDigit() }) formJumlah = it },
                    placeholder = { Text("Masukkan jumlah pengurangan") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Keterangan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formKeterangan,
                    onValueChange = { formKeterangan = it },
                    placeholder = { Text("Keterangan pengurangan kas") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val amount = formJumlah.toDoubleOrNull() ?: 0.0
                        if (amount <= 0) {
                            android.widget.Toast.makeText(context, "Jumlah harus diisi dengan valid!", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            val notesWithMeta = "[Outlet: $selectedOutlet] ${if (formTipeKas == "Non-Tunai") "[Non-Tunai] " else ""}$formKeterangan".trim()
                            viewModel.addCashMutation("Kas keluar", amount, notesWithMeta)
                            android.widget.Toast.makeText(context, "Berhasil merekam pengurangan kas!", android.widget.Toast.LENGTH_SHORT).show()
                            // Clear and go back
                            formJumlah = ""
                            formKeterangan = ""
                            currentSubScreen = "main"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Kurangi Kas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        "pengeluaran" -> {
            AdminPengeluaranTab(viewModel, onBack = { currentSubScreen = "main" })
        }
        else -> {
            // MAIN LAPORAN SCREEN (Exactly matched to images provided, theme-compatible)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header (LAPORAN on left, Outlet selector on right)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LAPORAN",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Box {
                            Row(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(12.dp))
                                    .clickable { outletDropdownExpanded = true }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedOutlet,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = outletDropdownExpanded,
                                onDismissRequest = { outletDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Semua Outlet", fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        viewModel.adminSelectedOutlet.value = "Semua Outlet"
                                        outletDropdownExpanded = false
                                    }
                                )
                                outlets.forEach { outlet ->
                                    DropdownMenuItem(
                                        text = { Text(outlet.name) },
                                        onClick = {
                                            viewModel.adminSelectedOutlet.value = outlet.name
                                            outletDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Two Tabs/Buttons: Penambahan Kas & Pengurangan Kas
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { currentSubScreen = "penambahan" },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Penambahan Kas", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Button(
                            onClick = { currentSubScreen = "pengurangan" },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Text("Pengurangan Kas", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // SALDO KAS Section
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Saldo Kas",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Saldo Tunai Row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.MonetizationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Saldo Tunai",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = formatRupiah(saldoTunai),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Saldo Non-Tunai Row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Saldo Non-Tunai",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = formatRupiah(saldoNonTunai),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                // PESANAN HARI INI Section
                item {
                    val todayOrders = ordersFiltered.filter { viewModel.isToday(it.timestamp) }
                    val nilaiPesananToday = todayOrders.filter { it.status != "Batal" }.sumOf { it.finalTotal }
                    val jumlahPesananToday = todayOrders.filter { it.status != "Batal" }.size
                    val pesananBatalToday = todayOrders.filter { it.status == "Batal" }.size

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Pesanan Hari Ini",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Nilai Pesanan Row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.MonetizationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Nilai Pesanan",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = formatRupiah(nilaiPesananToday),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Jumlah Pesanan Row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.ReceiptLong,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Jumlah Pesanan",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$jumlahPesananToday Pesanan",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Pesanan Batal Row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Pesanan Batal",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$pesananBatalToday Pesanan",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Total Belum Bayar Row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Total Belum Bayar",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = formatRupiah(0.0),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                // LIHAT LAPORAN Section
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Lihat Laporan",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. Laporan Kas Card
                        Card(
                            onClick = { viewModel.navigateTo(Screen.LAPORAN_KAS) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Laporan Kas", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Laporan mutasi kas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // 2. Laporan Pesanan Card
                        Card(
                            onClick = { viewModel.navigateTo(Screen.LAPORAN_PEMESANAN) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Laporan Pesanan", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Laporan data pesanan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // 3. Analisa Pelanggan Card
                        Card(
                            onClick = { viewModel.navigateTo(Screen.ANALISA_PELANGGAN) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(MaterialTheme.colorScheme.tertiaryContainer, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Analisa Pelanggan", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Analisa data pelanggan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // 4. Analisa Layanan Card
                        Card(
                            onClick = { viewModel.navigateTo(Screen.ANALISA_LAYANAN) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Analisa Layanan", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Analisis perbandingan jenis layanan terlaris & profitnya", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // 5. Cari & Kelola Pelanggan Card
                        Card(
                            onClick = { viewModel.navigateTo(Screen.CARI_PELANGGAN) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Group, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Cari & Kelola Pelanggan", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Database seluruh pelanggan laundry, no WA & riwayat", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }


                    }
                }
            }
        }
    }
}

@Composable
fun AdminPengaturanTab(viewModel: LaundryViewModel) {
    var currentSubScreen by remember { mutableStateOf("main") }

    when (currentSubScreen) {
        "akun" -> {
            AdminPengaturanAkunSubScreen(viewModel, onBack = { currentSubScreen = "main" })
        }
        "layanan" -> {
            AdminPengaturanLayananSubScreen(viewModel, onBack = { currentSubScreen = "main" })
        }
        else -> {
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
                                Text("Atur kebijakan pembatalan, batasan diskon, dan notifikasi WA.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }

                item {
                    Card(
                        onClick = { currentSubScreen = "layanan" },
                        modifier = Modifier.fillMaxWidth().testTag("menu_kelola_layanan"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.ListAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Kelola Layanan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Atur jenis layanan (Kiloan, Satuan, dsb) dan paket harga masing-masing.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                item {
                    Card(
                        onClick = { currentSubScreen = "akun" },
                        modifier = Modifier.fillMaxWidth().testTag("menu_pengaturan_akun"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pengaturan Akun", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Perbarui username dan kata sandi keamanan akun Owner utama.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPengaturanAkunSubScreen(viewModel: LaundryViewModel, onBack: () -> Unit) {
    val usernameState by viewModel.adminUsernameState.collectAsState()
    val passwordState by viewModel.adminPasswordState.collectAsState()

    var usernameInput by remember { mutableStateOf(usernameState) }
    var passwordInput by remember { mutableStateOf(passwordState) }
    var confirmPasswordInput by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var showValidationDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogIsSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("akun_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PENGATURAN AKUN",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Akun Owner / Administrator",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Gunakan form di bawah ini untuk memperbarui kredensial masuk admin utama.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Username Admin",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    placeholder = { Text("Masukkan username admin") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("akun_username_input")
                )
            }

            item {
                Text(
                    text = "Kata Sandi Baru",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    placeholder = { Text("Masukkan kata sandi baru (min 5 karakter)") },
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("akun_password_input")
                )
            }

            item {
                Text(
                    text = "Konfirmasi Kata Sandi Baru",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = confirmPasswordInput,
                    onValueChange = { confirmPasswordInput = it },
                    placeholder = { Text("Ulangi kata sandi baru") },
                    visualTransformation = if (confirmPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle confirm password visibility")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("akun_confirm_password_input")
                )
            }

            item {
                Button(
                    onClick = {
                        if (usernameInput.trim().isEmpty()) {
                            dialogMessage = "Username tidak boleh kosong."
                            dialogIsSuccess = false
                            showValidationDialog = true
                        } else if (passwordInput.length < 5) {
                            dialogMessage = "Kata sandi baru minimal harus 5 karakter."
                            dialogIsSuccess = false
                            showValidationDialog = true
                        } else if (confirmPasswordInput.isNotEmpty() && passwordInput != confirmPasswordInput) {
                            dialogMessage = "Konfirmasi kata sandi tidak cocok."
                            dialogIsSuccess = false
                            showValidationDialog = true
                        } else {
                            viewModel.adminUsernameState.value = usernameInput.trim()
                            viewModel.adminPasswordState.value = passwordInput
                            dialogMessage = "Kredensial akun Owner berhasil diperbarui!"
                            dialogIsSuccess = true
                            showValidationDialog = true
                            confirmPasswordInput = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("akun_save_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        if (showValidationDialog) {
            AlertDialog(
                onDismissRequest = { showValidationDialog = false },
                title = { Text(if (dialogIsSuccess) "Berhasil" else "Peringatan", fontWeight = FontWeight.Bold) },
                text = { Text(dialogMessage) },
                confirmButton = {
                    TextButton(onClick = { showValidationDialog = false }) {
                        Text("Tutup", fontWeight = FontWeight.Bold)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (dialogIsSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                        contentDescription = null,
                        tint = if (dialogIsSuccess) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPengaturanLayananSubScreen(viewModel: LaundryViewModel, onBack: () -> Unit) {
    val services by viewModel.servicesState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    
    // For navigation within services (to pricing)
    var selectedServiceForPricing by remember { mutableStateOf<Service?>(null) }
    
    // For navigation to add service
    var isAddingService by remember { mutableStateOf(false) }

    // For navigation to edit/delete service
    var selectedServiceToEdit by remember { mutableStateOf<Service?>(null) }
    
    if (isAddingService) {
        AdminTambahLayananSubScreen(
            viewModel = viewModel,
            onBack = { isAddingService = false }
        )
        return
    }
    
    if (selectedServiceToEdit != null) {
        AdminUbahLayananSubScreen(
            viewModel = viewModel,
            service = selectedServiceToEdit!!,
            onBack = { selectedServiceToEdit = null }
        )
        return
    }
    
    if (selectedServiceForPricing != null) {
        AdminPengaturanHargaSubScreen(
            viewModel = viewModel, 
            service = selectedServiceForPricing!!, 
            onBack = { selectedServiceForPricing = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("layanan_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Layanan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari Nama Layanan") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Category Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Semua", "Satuan", "Kiloan", "Meteran").forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        
        // List
        val filteredServices = services.filter {
            (selectedCategory == "Semua" || it.category == selectedCategory) &&
            it.name.contains(searchQuery, ignoreCase = true)
        }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredServices) { service ->
                val prices by viewModel.getServicePricesFlow(service.id).collectAsState(initial = emptyList())
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE65100), shape = CircleShape), // Orange avatar
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = service.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = service.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = Color(0xFFE8F5E9),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = service.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${prices.size} paket harga",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(onClick = { /* More options */ }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { selectedServiceToEdit = service },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Text("Ubah")
                            }
                            Button(
                                onClick = { selectedServiceForPricing = service },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                            ) {
                                Text("Kelola Harga", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        
        // Add Button
        Button(
            onClick = { isAddingService = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("TAMBAH LAYANAN", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTambahLayananSubScreen(viewModel: LaundryViewModel, onBack: () -> Unit) {
    var nameInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Satuan") }
    
    val suggestions = listOf("Cuci + Setrika", "Setrika", "Bed Cover", "Selimut", "Karpet", "Sepatu")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("tambah_layanan_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tambah Layanan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Nama Layanan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("cth: Cuci + Setrika, Setrika, Bed Cover, Sepatu") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Saran cepat:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { suggestion ->
                    OutlinedButton(
                        onClick = { nameInput = suggestion },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                        Text(suggestion, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Tipe Layanan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text("Pilih satuan harga yang sesuai", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Options
            val types = listOf(
                Triple("Satuan", "Pcs", "Harga per pcs · cocok untuk pakaian, sepatu, tas"),
                Triple("Kiloan", "Kg", "Harga per kg · cocok untuk cucian umum"),
                Triple("Meteran", "m²", "Harga per m² · cocok untuk gorden, karpet")
            )
            
            types.forEach { (type, unit, desc) ->
                val isSelected = selectedCategory == type
                Card(
                    onClick = { selectedCategory = type },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp, 
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (type == "Satuan") Color(0xFFE3F2FD) // Light Blue
                                            else if (type == "Kiloan") Color(0xFFE8F5E9) // Light Green
                                            else Color(0xFFF3E5F5), // Light Purple
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unit,
                                color = if (type == "Satuan") Color(0xFF1976D2)
                                        else if (type == "Kiloan") Color(0xFF388E3C)
                                        else Color(0xFF7B1FA2),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = type, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Info Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8E1), shape = RoundedCornerShape(8.dp)) // Light Amber
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Setelah simpan, atur daftar harga lewat tombol Kelola Harga di card layanan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF57F17) // Dark Amber
                    )
                }
            }
        }
        
        Button(
            onClick = { 
                if (nameInput.isNotBlank()) {
                    viewModel.addService(nameInput, selectedCategory)
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Simpan Layanan", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUbahLayananSubScreen(viewModel: LaundryViewModel, service: Service, onBack: () -> Unit) {
    var nameInput by remember { mutableStateOf(service.name) }
    var selectedCategory by remember { mutableStateOf(service.category) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("ubah_layanan_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ubah Layanan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    viewModel.deleteService(service)
                    onBack()
                },
                modifier = Modifier.testTag("hapus_layanan_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Hapus Layanan",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Nama Layanan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("cth: Cuci + Setrika, Setrika, Bed Cover, Sepatu") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Tipe Layanan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text("Pilih satuan harga yang sesuai", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Options
            val types = listOf(
                Triple("Satuan", "Pcs", "Harga per pcs · cocok untuk pakaian, sepatu, tas"),
                Triple("Kiloan", "Kg", "Harga per kg · cocok untuk cucian umum"),
                Triple("Meteran", "m²", "Harga per m² · cocok untuk gorden, karpet")
            )
            
            types.forEach { (type, unit, desc) ->
                val isSelected = selectedCategory == type
                Card(
                    onClick = { selectedCategory = type },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp, 
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (type == "Satuan") Color(0xFFE3F2FD) // Light Blue
                                            else if (type == "Kiloan") Color(0xFFE8F5E9) // Light Green
                                            else Color(0xFFF3E5F5), // Light Purple
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unit,
                                color = if (type == "Satuan") Color(0xFF1976D2)
                                        else if (type == "Kiloan") Color(0xFF388E3C)
                                        else Color(0xFF7B1FA2),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = type, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        
        Button(
            onClick = { 
                if (nameInput.isNotBlank()) {
                    viewModel.updateService(service.copy(name = nameInput, category = selectedCategory))
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminPengaturanHargaSubScreen(viewModel: LaundryViewModel, service: Service, onBack: () -> Unit) {
    val prices by viewModel.getServicePricesFlow(service.id).collectAsState(initial = emptyList())
    
    // For navigation within prices (to edit price)
    var selectedPriceToEdit by remember { mutableStateOf<ServicePrice?>(null) }
    var isAddingPrice by remember { mutableStateOf(false) }
    
    if (selectedPriceToEdit != null) {
        AdminUbahHargaSubScreen(
            viewModel = viewModel, 
            service = service, 
            price = selectedPriceToEdit!!,
            onBack = { selectedPriceToEdit = null }
        )
        return
    }

    if (isAddingPrice) {
        AdminTambahHargaSubScreen(
            viewModel = viewModel,
            service = service,
            onBack = { isAddingPrice = false }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("harga_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Harga ${service.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        if (prices.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Belum ada harga untuk layanan ini",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Silakan klik tombol tambah di bawah",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(prices) { price ->
                    Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE65100), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = price.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = price.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Rp. ${String.format("%,.0f", price.price).replace(",", ".")}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            IconButton(onClick = { /* Options */ }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { selectedPriceToEdit = price },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ubah Harga", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        }
        
        // Add Button
        Button(
            onClick = { isAddingPrice = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("TAMBAH HARGA", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTambahHargaSubScreen(viewModel: LaundryViewModel, service: Service, onBack: () -> Unit) {
    var nameInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("tambah_harga_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tambah Harga",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(16.dp)
        ) {
            // Service Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE65100), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = service.name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Layanan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = service.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Nama Harga", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("Misal: Reguler, Kilat, dll") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Harga", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = priceInput,
                onValueChange = { 
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        priceInput = it 
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("Misal: 7000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Text("Rp", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )
            val parsedPrice = priceInput.toDoubleOrNull() ?: 0.0
            if (parsedPrice > 0) {
                Text(
                    text = "Terbaca: Rp ${String.format("%,.0f", parsedPrice).replace(",", ".")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
        
        Button(
            onClick = { 
                val parsedPrice = priceInput.toDoubleOrNull() ?: 0.0
                if (nameInput.isNotBlank() && parsedPrice >= 0) {
                    viewModel.addServicePrice(service.id, nameInput, parsedPrice)
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Simpan Harga", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUbahHargaSubScreen(viewModel: LaundryViewModel, service: Service, price: ServicePrice, onBack: () -> Unit) {
    var nameInput by remember { mutableStateOf(price.name) }
    var priceInput by remember { mutableStateOf(price.price.toLong().toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("ubah_harga_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ubah Harga",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    viewModel.deleteServicePrice(price)
                    onBack()
                },
                modifier = Modifier.testTag("hapus_harga_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Hapus Harga",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE65100), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = service.name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Layanan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = service.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Nama Harga", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Harga", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = priceInput,
                onValueChange = { 
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        priceInput = it 
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Text("Rp", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )
            val parsedPrice = priceInput.toDoubleOrNull() ?: 0.0
            if (parsedPrice > 0) {
                Text(
                    text = "Terbaca: Rp ${String.format("%,.0f", parsedPrice).replace(",", ".")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
        
        Button(
            onClick = { 
                val parsedPrice = priceInput.toDoubleOrNull() ?: 0.0
                if (nameInput.isNotBlank() && parsedPrice >= 0) {
                    viewModel.updateServicePrice(price.copy(name = nameInput, price = parsedPrice))
                    onBack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(iconContainerColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
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
    var activeQueueSearchQuery by remember { mutableStateOf("") }
    var siapAmbilSearchQuery by remember { mutableStateOf("") }
    var completedSearchQuery by remember { mutableStateOf("") }

    val antreanOrdersList = orders.filter { 
        (it.status == "Order Masuk" || it.status == "Dicuci" || it.status == "Disetrika" || it.status == "Packing") && 
        it.outletName == (activeOutlet?.name ?: "")
    }

    val siapAmbilOrdersList = orders.filter { 
        (it.status == "Siap Ambil" || it.status == "Selesai") && 
        it.outletName == (activeOutlet?.name ?: "")
    }

    val completedOrdersList = orders.filter { 
        (it.status == "Diambil" || it.status == "Batal") && 
        it.outletName == (activeOutlet?.name ?: "")
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWide = maxWidth > 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (isWide) {
                // Side Navigation Rail for Wide Screens (Tablet/Landscape)
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
                        .width(96.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                ) {
                    NavigationRail(
                        containerColor = Color.Transparent,
                        header = {
                            Icon(
                                imageVector = Icons.Filled.LocalLaundryService,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(vertical = 12.dp)
                            )
                        },
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        val navItems = listOf(
                            Triple(0, Icons.Filled.Home, "Pelayanan"),
                            Triple(1, Icons.Filled.LocalLaundryService, "Antrean"),
                            Triple(2, Icons.Filled.ShoppingBag, "Siap Ambil"),
                            Triple(3, Icons.Filled.DateRange, "Selesai")
                        )
                        navItems.forEach { (index, icon, label) ->
                            val isSelected = selectedTab == index
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { selectedTab = index },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (index == 1 && antreanOrdersList.isNotEmpty()) {
                                                Badge {
                                                    Text(
                                                        text = antreanOrdersList.size.toString(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White
                                                    )
                                                }
                                            } else if (index == 2 && siapAmbilOrdersList.isNotEmpty()) {
                                                Badge {
                                                    Text(
                                                        text = siapAmbilOrdersList.size.toString(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(icon, contentDescription = label)
                                    }
                                },
                                label = { 
                                    Text(
                                        text = label, 
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    ) 
                                },
                                alwaysShowLabel = true,
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp, 
                                    end = 16.dp, 
                                    top = 16.dp, 
                                    bottom = if (isWide) 16.dp else 100.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
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

                                item {
                                    Text(
                                        text = "Menu Pelayanan Kasir",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                item {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        }

                        1 -> {
                            val filteredAntreanOrders = antreanOrdersList.filter { order ->
                                activeQueueSearchQuery.isEmpty() ||
                                order.customerName.contains(activeQueueSearchQuery, ignoreCase = true) ||
                                order.customerPhone.contains(activeQueueSearchQuery, ignoreCase = true)
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp, 
                                    end = 16.dp, 
                                    top = 16.dp, 
                                    bottom = if (isWide) 16.dp else 100.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Antrean Cucian Masuk",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = "Proses Antrean",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = activeQueueSearchQuery,
                                        onValueChange = { activeQueueSearchQuery = it },
                                        placeholder = { Text("Cari nama pelanggan atau nomor...", fontSize = 13.sp) },
                                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                                        trailingIcon = {
                                            if (activeQueueSearchQuery.isNotEmpty()) {
                                                IconButton(onClick = { activeQueueSearchQuery = "" }) {
                                                    Icon(Icons.Filled.Clear, contentDescription = "Bersihkan", modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                }

                                if (filteredAntreanOrders.isEmpty()) {
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
                                                    text = if (activeQueueSearchQuery.isNotEmpty()) "Pencarian tidak ditemukan." else "Tidak ada cucian aktif di antrean.",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    items(filteredAntreanOrders) { order ->
                                        OrderQueueCard(order = order, viewModel = viewModel, settings = settings)
                                    }
                                }
                            }
                        }

                        2 -> {
                            val filteredSiapAmbilOrders = siapAmbilOrdersList.filter { order ->
                                siapAmbilSearchQuery.isEmpty() ||
                                order.customerName.contains(siapAmbilSearchQuery, ignoreCase = true) ||
                                order.customerPhone.contains(siapAmbilSearchQuery, ignoreCase = true)
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp, 
                                    end = 16.dp, 
                                    top = 16.dp, 
                                    bottom = if (isWide) 16.dp else 100.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Cucian Siap Diambil",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            color = Color(0xFFC8E6C9),
                                            shape = CircleShape
                                        ) {
                                            Text(
                                                text = "Menunggu Pelanggan",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1B5E20),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = siapAmbilSearchQuery,
                                        onValueChange = { siapAmbilSearchQuery = it },
                                        placeholder = { Text("Cari nama pelanggan atau nomor...", fontSize = 13.sp) },
                                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                                        trailingIcon = {
                                            if (siapAmbilSearchQuery.isNotEmpty()) {
                                                IconButton(onClick = { siapAmbilSearchQuery = "" }) {
                                                    Icon(Icons.Filled.Clear, contentDescription = "Bersihkan", modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                }

                                if (filteredSiapAmbilOrders.isEmpty()) {
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
                                                    imageVector = Icons.Filled.ShoppingBag,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                                    modifier = Modifier.size(64.dp)
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = if (siapAmbilSearchQuery.isNotEmpty()) "Pencarian tidak ditemukan." else "Tidak ada cucian yang siap diambil.",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    items(filteredSiapAmbilOrders) { order ->
                                        OrderQueueCard(order = order, viewModel = viewModel, settings = settings)
                                    }
                                }
                            }
                        }

                        3 -> {
                            var selectedDateMs by remember { mutableStateOf(System.currentTimeMillis()) }
                            val context = androidx.compose.ui.platform.LocalContext.current

                            val sdf = java.text.SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale("id", "ID"))
                            val dateStr = sdf.format(java.util.Date(selectedDateMs))

                            val calSelect = java.util.Calendar.getInstance().apply { timeInMillis = selectedDateMs }
                            val completedOrders = completedOrdersList.filter { order ->
                                val calOrder = java.util.Calendar.getInstance().apply { timeInMillis = order.timestamp }
                                calOrder.get(java.util.Calendar.YEAR) == calSelect.get(java.util.Calendar.YEAR) &&
                                calOrder.get(java.util.Calendar.DAY_OF_YEAR) == calSelect.get(java.util.Calendar.DAY_OF_YEAR)
                            }

                            val filteredCompletedOrders = completedOrders.filter { order ->
                                completedSearchQuery.isEmpty() ||
                                order.customerName.contains(completedSearchQuery, ignoreCase = true) ||
                                order.customerPhone.contains(completedSearchQuery, ignoreCase = true)
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp, 
                                    end = 16.dp, 
                                    top = 16.dp, 
                                    bottom = if (isWide) 16.dp else 100.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    Column {
                                        Text(
                                            text = "Riwayat Cucian Selesai",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Pilih tanggal untuk melihat laundry yang sudah selesai atau diambil.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                item {
                                    Card(
                                        onClick = {
                                            val calPick = java.util.Calendar.getInstance().apply { timeInMillis = selectedDateMs }
                                            android.app.DatePickerDialog(
                                                context,
                                                { _, year, month, dayOfMonth ->
                                                    val chosenCal = java.util.Calendar.getInstance().apply {
                                                        set(java.util.Calendar.YEAR, year)
                                                        set(java.util.Calendar.MONTH, month)
                                                        set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                                                    }
                                                    selectedDateMs = chosenCal.timeInMillis
                                                },
                                                calPick.get(java.util.Calendar.YEAR),
                                                calPick.get(java.util.Calendar.MONTH),
                                                calPick.get(java.util.Calendar.DAY_OF_MONTH)
                                            ).show()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.DateRange,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = "Tanggal Terpilih",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = dateStr,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.primary
                                            ) {
                                                Text(
                                                    text = "Pilih Tanggal",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = completedSearchQuery,
                                        onValueChange = { completedSearchQuery = it },
                                        placeholder = { Text("Cari nama pelanggan atau nomor...", fontSize = 13.sp) },
                                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                                        trailingIcon = {
                                            if (completedSearchQuery.isNotEmpty()) {
                                                IconButton(onClick = { completedSearchQuery = "" }) {
                                                    Icon(Icons.Filled.Clear, contentDescription = "Bersihkan", modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                }

                                if (filteredCompletedOrders.isEmpty()) {
                                    item {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 40.dp, horizontal = 16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.CheckCircle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                                    modifier = Modifier.size(48.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = if (completedSearchQuery.isNotEmpty()) "Pencarian tidak ditemukan." else "Tidak ada cucian selesai pada tanggal ini.",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    items(filteredCompletedOrders) { order ->
                                        OrderQueueCard(order = order, viewModel = viewModel, settings = settings)
                                    }
                                }
                            }
                        }
                    }
                }

                // Floating Mobile Bottom Navigation Bar (ONLY when screen is NOT wide)
                if (!isWide) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp,
                                modifier = Modifier.height(72.dp)
                            ) {
                                val navItems = listOf(
                                    Triple(0, Icons.Filled.Home, "Pelayanan"),
                                    Triple(1, Icons.Filled.LocalLaundryService, "Antrean"),
                                    Triple(2, Icons.Filled.ShoppingBag, "Siap Ambil"),
                                    Triple(3, Icons.Filled.DateRange, "Selesai")
                                )
                                navItems.forEach { (index, icon, label) ->
                                    val isSelected = selectedTab == index
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { selectedTab = index },
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if (index == 1 && antreanOrdersList.isNotEmpty()) {
                                                        Badge {
                                                            Text(
                                                                text = antreanOrdersList.size.toString(),
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = Color.White
                                                            )
                                                        }
                                                    } else if (index == 2 && siapAmbilOrdersList.isNotEmpty()) {
                                                        Badge {
                                                            Text(
                                                                text = siapAmbilOrdersList.size.toString(),
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = Color.White
                                                            )
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = label,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 10.sp
                                            )
                                        },
                                        alwaysShowLabel = true,
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        ),
                                        modifier = Modifier.testTag("cashier_nav_$index")
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

fun formatPhoneNumberForWhatsApp(phone: String): String {
    val clean = phone.replace(Regex("[^0-9]"), "")
    return if (clean.startsWith("0")) {
        "62" + clean.substring(1)
    } else if (clean.startsWith("8")) {
        "62" + clean
    } else {
        clean
    }
}

@Composable
fun OrderQueueCard(order: Order, viewModel: LaundryViewModel, settings: AppSettings?) {
    val currentDisplayStatus = when (order.status) {
        "Order Masuk", "Dicuci", "Disetrika", "Packing" -> "Order Masuk"
        "Siap Ambil", "Selesai" -> "Siap Ambil"
        "Diambil" -> "Diambil"
        "Batal" -> "Batal"
        else -> order.status
    }
    var showConfirmDialog by remember { mutableStateOf(false) }

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
                    color = when (currentDisplayStatus) {
                        "Order Masuk" -> MaterialTheme.colorScheme.primaryContainer
                        "Siap Ambil" -> Color(0xFFC8E6C9)
                        "Diambil" -> MaterialTheme.colorScheme.secondaryContainer
                        "Batal" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (currentDisplayStatus == "Order Masuk") "Antrean Baru" else currentDisplayStatus,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (currentDisplayStatus) {
                            "Order Masuk" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "Siap Ambil" -> Color(0xFF1B5E20)
                            "Diambil" -> MaterialTheme.colorScheme.onSecondaryContainer
                            "Batal" -> MaterialTheme.colorScheme.onErrorContainer
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
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val context = LocalContext.current
            OutlinedButton(
                onClick = {
                    val formattedPhone = formatPhoneNumberForWhatsApp(order.customerPhone)
                    val message = when (order.status) {
                        "Order Masuk" -> "Halo Kak ${order.customerName},\n\nPesanan laundry Anda dengan nomor struk *#ORD-${order.id}* telah diterima dan sedang kami proses.\n\nDetail Layanan:\n- Layanan: ${order.serviceType}\n- Jumlah: ${order.weightQty}\n- Total: Rp ${String.format("%,.0f", order.finalTotal).replace(",", ".")}\n\nTerima kasih telah mempercayakan laundry Anda kepada CucianKu! 🙏"
                        "Siap Ambil" -> "Halo Kak ${order.customerName},\n\nKabar baik! Cucian Anda dengan nomor struk *#ORD-${order.id}* sudah SELESAI dan SIAP DIAMBIL di outlet kami.\n\nDetail Layanan:\n- Layanan: ${order.serviceType}\n- Total: Rp ${String.format("%,.0f", order.finalTotal).replace(",", ".")}\n\nSilakan datang ke outlet untuk pengambilan. Terima kasih! 😊"
                        "Diambil" -> "Halo Kak ${order.customerName},\n\nPesanan laundry Anda dengan nomor struk *#ORD-${order.id}* telah selesai diambil.\n\nTerima kasih banyak atas kunjungannya. Semoga puas dengan layanan kami! Kami tunggu kedatangan Anda berikutnya. 🙏✨"
                        else -> "Halo Kak ${order.customerName},\n\nBerikut informasi pesanan laundry Anda dengan nomor struk *#ORD-${order.id}*.\nStatus terkini: *${order.status}*\n\nTerima kasih! 🙏"
                    }
                    val url = "https://api.whatsapp.com/send?phone=$formattedPhone&text=${java.net.URLEncoder.encode(message, "UTF-8")}"
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        viewModel.pendingWaNotification.value = "Gagal membuka WhatsApp. Pastikan aplikasi WhatsApp terinstal di ponsel Anda."
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF25D366)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF128C7E))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF128C7E)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Kirim Notifikasi WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Action row for moving statuses forward or cancellation
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cancel Button (checks permission)
                if (currentDisplayStatus != "Batal" && currentDisplayStatus != "Diambil") {
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
                        Text("Batal", fontSize = 11.sp)
                    }
                }

                // If currently "Order Masuk" -> action to "Siap Ambil"
                if (currentDisplayStatus == "Order Masuk") {
                    Button(
                        onClick = {
                            if (settings?.permissionBolehEdit == true) {
                                showConfirmDialog = true
                            } else {
                                viewModel.pendingWaNotification.value = "Akses ditolak: Anda tidak memiliki ijin (permission) dari owner untuk memperbarui status pesanan."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Siap Ambil", fontSize = 11.sp)
                    }
                } 
                // If currently "Siap Ambil" -> action to "Diambil" (Picked up by customer)
                else if (currentDisplayStatus == "Siap Ambil") {
                    Button(
                        onClick = {
                            if (settings?.permissionBolehEdit == true) {
                                viewModel.updateOrderStatus(order, "Diambil")
                            } else {
                                viewModel.pendingWaNotification.value = "Akses ditolak: Anda tidak memiliki ijin (permission) dari owner untuk mengambil pesanan."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Diambil", fontSize = 11.sp)
                    }
                }
            }
        }
    }

    // Safety Warning Confirmation Dialog for Siap Ambil
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Konfirmasi Siap Ambil", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Apakah cucian fisik atas nama \"${order.customerName}\" benar-benar sudah selesai diproses & siap diambil?\n\nCucian akan dipindahkan ke daftar siap ambil.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.updateOrderStatus(order, "Siap Ambil")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ya, Siap Ambil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
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
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = {
                                viewModel.deleteCashier(cashier)
                            }) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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
    var addressInput by remember { mutableStateOf("") }
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

                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("Alamat") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cust_address_input"),
                        shape = RoundedCornerShape(10.dp)
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
                                    addressInput = ""
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
                                        viewModel.addCustomer(nameInput, phoneInput, addressInput)
                                    } else {
                                        viewModel.updateCustomer(
                                            currentEditing.copy(
                                                name = nameInput,
                                                phone = phoneInput,
                                                address = addressInput,
                                                isInactive = isInactiveInput
                                            )
                                        )
                                        editingCustomer = null
                                    }
                                    nameInput = ""
                                    phoneInput = ""
                                    addressInput = ""
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PLG${cust.id.toString().padStart(4, '0')} - ${cust.name}",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "WA: ${cust.phone} • Status: ${if (cust.isInactive) "Inactive" else "Aktif"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (cust.address.isNotBlank()) {
                                Text(
                                    text = "Alamat: ${cust.address}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row {
                            IconButton(onClick = {
                                editingCustomer = cust
                                nameInput = cust.name
                                phoneInput = cust.phone
                                addressInput = cust.address
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
    var selectedTab by remember { mutableStateOf(0) } // 0 = Nilai Belanja, 1 = Frekuensi Order, 2 = Pelanggan Inactive

    // Filter orders by selected outlet
    val ordersFiltered = if (selectedOutlet == "Semua Outlet") {
        orders
    } else {
        orders.filter { it.outletName == selectedOutlet }
    }

    // Filter customers by selected outlet (registered there, or ordered there)
    val customersFiltered = if (selectedOutlet == "Semua Outlet") {
        customers
    } else {
        val phonesWithOrders = ordersFiltered.map { it.customerPhone }.toSet()
        customers.filter { it.registeredOutletName == selectedOutlet || it.phone in phonesWithOrders }
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

    // --- KPI Summary Metrics ---
    // 1. Total Customers
    val totalCustomersCount = customersFiltered.size

    // 2. New Customers count (registered in A vs B)
    val newCustomersA = when (selectedPeriod) {
        0 -> customersFiltered.filter { it.registrationTimestamp >= startOfToday }
        1 -> customersFiltered.filter { it.registrationTimestamp >= startOfSevenDaysAgo }
        else -> customersFiltered.filter { it.registrationTimestamp >= startOfThirtyDaysAgo }
    }
    val newCustomersB = when (selectedPeriod) {
        0 -> customersFiltered.filter { it.registrationTimestamp >= startOfYesterday && it.registrationTimestamp < startOfToday }
        1 -> customersFiltered.filter { it.registrationTimestamp >= startOfFourteenDaysAgo && it.registrationTimestamp < startOfSevenDaysAgo }
        else -> customersFiltered.filter { it.registrationTimestamp >= startOfSixtyDaysAgo && it.registrationTimestamp < startOfThirtyDaysAgo }
    }

    val newCustomersCountA = newCustomersA.size
    val newCustomersCountB = newCustomersB.size
    val newCustomersDiff = newCustomersCountA - newCustomersCountB

    // Spend mapping
    val customerSpendingMapA = ordersA.groupBy { it.customerPhone }.mapValues { it.value.sumOf { it.finalTotal } }
    val customerSpendingMapB = ordersB.groupBy { it.customerPhone }.mapValues { it.value.sumOf { it.finalTotal } }

    // Order Count mapping
    val customerOrderCountMapA = ordersA.groupBy { it.customerPhone }.mapValues { it.value.size }
    val customerOrderCountMapB = ordersB.groupBy { it.customerPhone }.mapValues { it.value.size }

    // Tab 0: Top customers by Spend Value (Nilai Belanja Banyak)
    val topSpendCustomers = customersFiltered.map { cust ->
        val totalSpent = customerSpendingMapA[cust.phone] ?: 0.0
        val spentPrev = customerSpendingMapB[cust.phone] ?: 0.0
        val diff = totalSpent - spentPrev
        Triple(cust, totalSpent, diff)
    }.filter { it.second > 0.0 || it.third != 0.0 }.sortedByDescending { it.second }

    // Tab 1: Top customers by Order Count (Jumlah Pesanan Banyak)
    val topCountCustomers = customersFiltered.map { cust ->
        val count = customerOrderCountMapA[cust.phone] ?: 0
        val countPrev = customerOrderCountMapB[cust.phone] ?: 0
        val diff = count - countPrev
        Triple(cust, count, diff)
    }.filter { it.second > 0 || it.third != 0 }.sortedByDescending { it.second }

    // Tab 2: Inactive customers (or 0 spending in A)
    val inactiveCustomers = customersFiltered.filter { cust ->
        cust.isInactive || (customerSpendingMapA[cust.phone] ?: 0.0) == 0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Gray curved header box for Outlet and Period filters
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Row 1: Outlet Selection
                var outletExpanded by remember { mutableStateOf(false) }
                val outlets by viewModel.outletsState.collectAsState()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { outletExpanded = true }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Outlet",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4B5563)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color(0xFFD1D5DB))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedOutlet,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
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

                // Row 2: Period Selection
                var periodExpanded by remember { mutableStateOf(false) }
                val periodOptions = listOf("Hari Ini", "Minggu Ini", "Bulan Ini")

                val formatEpochToDate: (Long) -> String = { epoch ->
                    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
                    sdf.format(java.util.Date(epoch))
                }

                val dateLabelStr = when (selectedPeriod) {
                    0 -> "${formatEpochToDate(startOfToday)} ~ ${formatEpochToDate(System.currentTimeMillis())}"
                    1 -> "${formatEpochToDate(startOfSevenDaysAgo)} ~ ${formatEpochToDate(System.currentTimeMillis())}"
                    else -> "${formatEpochToDate(startOfThirtyDaysAgo)} ~ ${formatEpochToDate(System.currentTimeMillis())}"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { periodExpanded = true }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Periode",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dateLabelStr,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
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

        Spacer(modifier = Modifier.height(4.dp))

        // Card 1: Pelanggan Baru
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFFFB300), shape = CircleShape)
                )
                Text(
                    text = "Pelanggan Baru",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$newCustomersCountA Orang",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Card 2: Total Pelanggan
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFFFB300), shape = CircleShape)
                )
                Text(
                    text = "Total Pelanggan",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$totalCustomersCount Orang",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Card 3: Pelanggan Dengan Jumlah Pesanan Terbanyak
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFFFB300), shape = CircleShape)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pelanggan Dengan",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Jumlah Pesanan Terbanyak",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val topCountCust = topCountCustomers.firstOrNull()
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = topCountCust?.first?.name ?: "Belum Ada Data",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "${topCountCust?.second ?: 0} Pesanan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        // Card 4: Pelanggan Dengan Nilai Pesanan Terbanyak
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFFFB300), shape = CircleShape)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pelanggan Dengan",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Nilai Pesanan Terbanyak",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val topSpendCust = topSpendCustomers.firstOrNull()
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = topSpendCust?.first?.name ?: "Belum Ada Data",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = if (topSpendCust != null && topSpendCust.second > 0.0) {
                            "Rp ${String.format("%,.0f", topSpendCust.second)}"
                        } else {
                            "Rp 0"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
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
    val services = listOf("Kiloan", "Satuan", "Meteran", "Express", "Cuci Kering", "Cuci Setrika")
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
    val context = LocalContext.current
    var exportMessage by remember { mutableStateOf<String?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var selectedStatusForExport by remember { mutableStateOf("Semua Status") }
    var exportFormat by remember { mutableStateOf("PDF") } // "PDF" or "CSV"

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

        Spacer(modifier = Modifier.height(12.dp))

        // CSV and PDF Download Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    exportFormat = "CSV"
                    showExportDialog = true
                },
                modifier = Modifier.weight(1f).testTag("btn_export_csv"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Filled.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ekspor CSV", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    exportFormat = "PDF"
                    showExportDialog = true
                },
                modifier = Modifier.weight(1f).testTag("btn_export_pdf"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Filled.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ekspor PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (exportFormat == "PDF") Icons.Filled.Description else Icons.Filled.TableChart,
                            contentDescription = null,
                            tint = if (exportFormat == "PDF") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ekspor Laporan ($exportFormat)", fontWeight = FontWeight.ExtraBold)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Silakan pilih status transaksi yang ingin diekspor. Laporan dan total pendapatan akan otomatis disesuaikan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "Pilih Status Transaksi:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        val statusOptions = listOf("Semua Status", "Order Masuk", "Diambil", "Selesai", "Siap Ambil", "Batal")
                        
                        // Vertical column of modern radio-like items
                        statusOptions.forEach { statusOption ->
                            val isSelected = selectedStatusForExport == statusOption
                            Card(
                                onClick = { selectedStatusForExport = statusOption },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedStatusForExport = statusOption }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = statusOption,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showExportDialog = false
                            
                            val periodTitle = when (selectedTab) {
                                0 -> "Hari Ini"
                                1 -> "7 Hari Terakhir"
                                2 -> "Bulan Ini"
                                else -> "Tahun Ini"
                            }
                            
                            val exportOrdersList = if (selectedStatusForExport == "Semua Status") {
                                filteredOrders
                            } else {
                                filteredOrders.filter { it.status == selectedStatusForExport }
                            }
                            
                            val exportTotal = exportOrdersList.filter { it.status != "Batal" }.sumOf { it.finalTotal }
                            
                            if (exportFormat == "CSV") {
                                val fileName = "Laporan_Keuangan_${selectedOutlet.replace(" ", "_")}_${periodTitle.replace(" ", "_")}_${selectedStatusForExport.replace(" ", "_")}.csv"
                                val csvContent = generateFinancialSummaryCsv(selectedOutlet, periodTitle, exportTotal, exportOrdersList)
                                val success = saveCsvToDownloads(context, csvContent, fileName)
                                if (success) {
                                    exportMessage = "Laporan CSV berhasil diunduh!\nNama file: $fileName"
                                } else {
                                    exportMessage = "Gagal mengunduh CSV. Pastikan ijin folder aktif."
                                }
                            } else {
                                val fileName = "Laporan_Keuangan_${selectedOutlet.replace(" ", "_")}_${periodTitle.replace(" ", "_")}_${selectedStatusForExport.replace(" ", "_")}.pdf"
                                val success = saveStylizedPdfToDownloads(
                                    context = context,
                                    outlet = selectedOutlet,
                                    period = periodTitle,
                                    statusFilter = selectedStatusForExport,
                                    orders = exportOrdersList,
                                    totalIncome = exportTotal,
                                    fileName = fileName
                                )
                                if (success) {
                                    exportMessage = "Laporan PDF berhasil diunduh!\nNama file: $fileName"
                                } else {
                                    exportMessage = "Gagal mengunduh PDF. Pastikan ijin folder aktif."
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Unduh Laporan", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        if (exportMessage != null) {
            AlertDialog(
                onDismissRequest = { exportMessage = null },
                confirmButton = {
                    Button(onClick = { exportMessage = null }) {
                        Text("OK")
                    }
                },
                title = { Text("Ekspor Laporan Sukses", fontWeight = FontWeight.Bold) },
                text = { Text(exportMessage ?: "") }
            )
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
    var priceCuciKeringInput by remember { mutableStateOf("") }
    var priceCuciSetrikaInput by remember { mutableStateOf("") }

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
            priceCuciKeringInput = s.priceCuciKering.toInt().toString()
            priceCuciSetrikaInput = s.priceCuciSetrika.toInt().toString()
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
            Text(text = "Pengaturan Transaksi", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        id = 1,
                        statistikHarianEnabled = statistikHarianEnabled,
                        permissionBolehBatal = permissionBolehBatal,
                        permissionBolehEdit = permissionBolehEdit,
                        permissionBolehHapusPelanggan = permissionBolehHapusPelanggan,
                        permissionBolehLihatLaporanKas = permissionBolehLihatLaporanKas,
                        permissionBolehLihatSaldo = permissionBolehLihatSaldo,
                        maksDiskonManual = maksDiskonInput.toDoubleOrNull() ?: 50000.0,
                        priceKiloan = priceKiloanInput.toDoubleOrNull() ?: 6000.0,
                        priceSatuan = priceSatuanInput.toDoubleOrNull() ?: 10000.0,
                        priceMeteran = priceMeteranInput.toDoubleOrNull() ?: 12000.0,
                        priceExpress = priceExpressInput.toDoubleOrNull() ?: 15000.0,
                        priceCuciKering = priceCuciKeringInput.toDoubleOrNull() ?: 7000.0,
                        priceCuciSetrika = priceCuciSetrikaInput.toDoubleOrNull() ?: 8500.0,
                        isWaOnOrderCreated = isWaOnOrderCreated,
                        isWaOnOrderFinished = isWaOnOrderFinished,
                        isWaOnOrderCancelled = isWaOnOrderCancelled
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
    val services by viewModel.servicesState.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    
    var selectedCategory by remember { mutableStateOf("Kiloan") }
    var selectedService by remember { mutableStateOf<Service?>(null) }
    var selectedPrice by remember { mutableStateOf<ServicePrice?>(null) }

    val filteredServices = services.filter { it.category == selectedCategory }

    LaunchedEffect(selectedCategory, services) {
        if (selectedService == null || selectedService?.category != selectedCategory) {
            selectedService = filteredServices.firstOrNull()
        }
    }

    val pricesFlow = selectedService?.let { viewModel.getServicePricesFlow(it.id) }
    val prices by (pricesFlow ?: kotlinx.coroutines.flow.flowOf(emptyList())).collectAsState(initial = emptyList())

    LaunchedEffect(prices) {
        if (selectedPrice == null || !prices.contains(selectedPrice)) {
            selectedPrice = prices.firstOrNull()
        }
    }
    
    val unitStr = when (selectedCategory) {
        "Kiloan" -> "Kg"
        "Meteran" -> "m²"
        else -> "Pcs"
    }
    val serviceType = if (selectedService != null && selectedPrice != null) {
        "${selectedCategory} - ${selectedService?.name} (${selectedPrice?.name})"
    } else {
        "Belum ada layanan dipilih"
    }
    
    var weightQty by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var discountInput by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Tunai") }

    var paymentExpanded by remember { mutableStateOf(false) }

    val basePrice = selectedPrice?.price ?: 0.0

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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    
                    // 1. Kategori Layanan
                    Column {
                        Text("1. Pilih Kategori Layanan", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Kiloan", "Satuan", "Meteran").forEach { tipe ->
                                FilterChip(
                                    selected = selectedCategory == tipe,
                                    onClick = { selectedCategory = tipe },
                                    label = { Text(tipe, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("chip_tipe_$tipe")
                                )
                            }
                        }
                    }

                    // 2. Layanan
                    Column {
                        Text("2. Pilih Layanan", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        if (filteredServices.isEmpty()) {
                            Text("Tidak ada layanan di kategori ini", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                filteredServices.forEach { service ->
                                    FilterChip(
                                        selected = selectedService == service,
                                        onClick = { selectedService = service },
                                        label = { Text(service.name, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                        modifier = Modifier.testTag("chip_service_${service.id}")
                                    )
                                }
                            }
                        }
                    }

                    // 3. Paket Harga
                    Column {
                        Text("3. Pilih Paket Harga", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        if (prices.isEmpty()) {
                            Text("Tidak ada paket harga untuk layanan ini", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                prices.forEach { price ->
                                    FilterChip(
                                        selected = selectedPrice == price,
                                        onClick = { selectedPrice = price },
                                        label = { Text(price.name, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                        modifier = Modifier.testTag("chip_price_${price.id}")
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Text display of calculated service name & price per unit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pilihan Terkini:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(
                                text = serviceType,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            val unitLabel = when (selectedCategory) {
                                "Kiloan" -> "/ kg"
                                "Meteran" -> "/ m²"
                                else -> "/ pcs"
                            }
                            Text(
                                text = "Rp ${String.format("%,.0f", basePrice)} $unitLabel",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    val qtyLabel = when (selectedCategory) {
                        "Kiloan" -> "Berat (Kg)"
                        "Meteran" -> "Luas (m²)"
                        else -> "Jumlah (Pcs)"
                    }
                    OutlinedTextField(
                        value = weightQty,
                        onValueChange = { weightQty = it },
                        label = { Text(qtyLabel) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("order_qty")
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan Tambahan (opsional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
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
                                customerName = customerName,
                                customerPhone = customerPhone,
                                serviceType = serviceType,
                                weightQty = qtyDouble,
                                notes = notes,
                                discountAmount = discDouble,
                                paymentMethod = paymentMethod,
                                pricePerUnit = basePrice
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

@Composable
fun ReceiptDialog(order: Order, viewModel: LaundryViewModel) {
    val clipboardManager = LocalClipboardManager.current
    var isPrinting by remember { mutableStateOf(false) }
    var printSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(isPrinting) {
        if (isPrinting) {
            delay(1500)
            isPrinting = false
            printSuccess = true
            delay(2000)
            printSuccess = false
        }
    }

    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val formattedDate = sdf.format(Date(order.timestamp))

    // Formatted Plain Text Receipt for clipboard / sharing
    val plainTextReceipt = """
========================================
           CUCIANKU LAUNDRY             
========================================
No. Struk : #ORD-${order.id}
Tanggal   : $formattedDate
Outlet    : ${order.outletName}
Kasir     : ${order.cashierName}
----------------------------------------
Pelanggan : ${order.customerName}
No. HP    : ${order.customerPhone}
----------------------------------------
Layanan   : ${order.serviceType}
Jumlah    : ${order.weightQty} Unit
Harga/Unit: Rp ${String.format("%,.0f", order.pricePerUnit)}
----------------------------------------
Subtotal  : Rp ${String.format("%,.0f", order.pricePerUnit * order.weightQty)}
Diskon    : Rp ${String.format("%,.0f", order.discountAmount)}
----------------------------------------
TOTAL     : Rp ${String.format("%,.0f", order.finalTotal)}
Bayar via : ${order.paymentMethod}
Status    : ${order.status}
========================================
     Terima Kasih Atas Kepercayaan Anda!
      Cucian Bersih, Wangi, & Rapi!   
========================================
""".trimIndent()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(enabled = true) { /* consume clicks */ },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Struk Pembayaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = { viewModel.selectedReceiptOrder.value = null }
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable content area representing the paper receipt
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF9F9F9),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Thermal printer paper style top scalloped border/spacer
                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - -",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Icon(
                            imageVector = Icons.Filled.LocalLaundryService,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )

                        Text(
                            text = "CUCIANKU LAUNDRY",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = order.outletName,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Kasir: ${order.cashierName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "==========================================",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )

                        // Meta details
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ReceiptRow(label = "No. Struk", value = "#ORD-${order.id}")
                            ReceiptRow(label = "Tanggal", value = formattedDate)
                            ReceiptRow(label = "Pelanggan", value = order.customerName)
                            ReceiptRow(label = "No. HP", value = order.customerPhone)
                        }

                        Text(
                            text = "------------------------------------------",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )

                        // Order details
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ReceiptRow(
                                label = "${order.serviceType} x ${order.weightQty}",
                                value = "Rp ${String.format("%,.0f", order.pricePerUnit * order.weightQty)}"
                            )
                            if (order.notes.isNotBlank()) {
                                Text(
                                    text = "   * Catatan: ${order.notes}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Text(
                            text = "------------------------------------------",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )

                        // Pricing calculation
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ReceiptRow(label = "Subtotal", value = "Rp ${String.format("%,.0f", order.pricePerUnit * order.weightQty)}")
                            if (order.discountAmount > 0.0) {
                                ReceiptRow(label = "Diskon", value = "- Rp ${String.format("%,.0f", order.discountAmount)}", valueColor = MaterialTheme.colorScheme.error)
                            }
                        }

                        Text(
                            text = "==========================================",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )

                        // Grand total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL AKHIR",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                            Text(
                                text = "Rp ${String.format("%,.0f", order.finalTotal)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        ReceiptRow(label = "Metode Bayar", value = order.paymentMethod)
                        ReceiptRow(label = "Status", value = if (order.status == "Order Masuk") "Baru Masuk" else order.status, valueColor = if (order.status == "Selesai" || order.status == "Diambil") Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Terima Kasih Atas Kunjungan Anda!",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Cucian Bersih, Rapi, & Harum",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "- - - - - - - - - - - - - - - - - - - - - -",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    // Loading printing overlays
                    if (isPrinting) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.9f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Sedang Mencetak...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mengirim data ke Printer Bluetooth...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (printSuccess) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.95f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Selesai Dicetak!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "Struk berhasil dicetak ke printer thermal.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy / share button
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(plainTextReceipt))
                            viewModel.pendingWaNotification.value = "Struk berhasil disalin ke clipboard! Anda bisa langsung membagikannya ke WhatsApp pelanggan."
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Salin Struk", fontSize = 12.sp)
                    }

                    // Print action button
                    Button(
                        onClick = { isPrinting = true },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cetak Struk", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val context = LocalContext.current
                Button(
                    onClick = {
                        val formattedPhone = formatPhoneNumberForWhatsApp(order.customerPhone)
                        val message = "Halo Kak ${order.customerName},\n\nBerikut adalah struk transaksi laundry Anda di CucianKu:\n\n$plainTextReceipt"
                        val url = "https://api.whatsapp.com/send?phone=$formattedPhone&text=${java.net.URLEncoder.encode(message, "UTF-8")}"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(url)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            viewModel.pendingWaNotification.value = "Gagal membuka WhatsApp. Pastikan aplikasi WhatsApp terinstal di ponsel Anda."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // WhatsApp green
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kirim Struk via WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}

fun generateFinancialSummaryCsv(outletName: String, periodTitle: String, totalIncome: Double, orders: List<Order>): String {
    val sb = java.lang.StringBuilder()
    sb.append("=== LAPORAN KEUANGAN CUCIANKU LAUNDRY ===\n")
    sb.append("Outlet;${outletName}\n")
    sb.append("Periode;${periodTitle}\n")
    sb.append("Total Pendapatan;Rp ${String.format("%.0f", totalIncome)}\n")
    sb.append("Total Transaksi;${orders.size}\n\n")
    
    sb.append("ID Transaksi;Tanggal;Pelanggan;No. HP;Layanan;Berat/Qty;Total Akhir;Metode Bayar;Status;Kasir\n")
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
    for (order in orders) {
        sb.append("#ORD-${order.id};")
        sb.append("${formatter.format(java.util.Date(order.timestamp))};")
        sb.append("${order.customerName.replace(";", " ")};")
        sb.append("${order.customerPhone};")
        sb.append("${order.serviceType.replace(";", " ")};")
        sb.append("${order.weightQty};")
        sb.append("${order.finalTotal};")
        sb.append("${order.paymentMethod};")
        sb.append("${order.status};")
        sb.append("${order.cashierName.replace(";", " ")}\n")
    }
    return sb.toString()
}

fun saveCsvToDownloads(context: android.content.Context, csvContent: String, fileName: String): Boolean {
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvContent.toByteArray())
                }
                true
            } else {
                false
            }
        } else {
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(downloadsDir, fileName)
            file.writeText(csvContent)
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun saveStylizedPdfToDownloads(
    context: android.content.Context,
    outlet: String,
    period: String,
    statusFilter: String,
    orders: List<com.example.data.model.Order>,
    totalIncome: Double,
    fileName: String
): Boolean {
    return try {
        val pdfDocument = android.graphics.pdf.PdfDocument()
        
        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(18, 140, 126) // CucianKu Teal
            textSize = 18f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            isAntiAlias = true
        }
        val subTitlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 10f
            isAntiAlias = true
        }
        val borderPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(220, 220, 220)
            strokeWidth = 1f
            style = android.graphics.Paint.Style.STROKE
        }
        val headerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 10f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            isAntiAlias = true
        }
        val headerBgPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(240, 245, 245)
            style = android.graphics.Paint.Style.FILL
        }
        val rowPaint = android.graphics.Paint().apply {
            textSize = 9f
            color = android.graphics.Color.rgb(60, 60, 60)
            isAntiAlias = true
        }
        val rowBoldPaint = android.graphics.Paint().apply {
            textSize = 9f
            color = android.graphics.Color.BLACK
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            isAntiAlias = true
        }
        
        val pageWidth = 595
        val pageHeight = 842
        
        var currentPageNumber = 1
        var pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        
        fun drawPageHeader(pageNum: Int) {
            val accentPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.rgb(18, 140, 126)
                style = android.graphics.Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 15f, accentPaint)
            
            if (pageNum == 1) {
                canvas.drawText("CUCIANKU LAUNDRY", 40f, 45f, titlePaint)
                canvas.drawText("Laporan Keuangan & Transaksi Pelanggan", 40f, 60f, subTitlePaint)
                canvas.drawLine(40f, 70f, (pageWidth - 40).toFloat(), 70f, borderPaint)
                
                val metaPaint = android.graphics.Paint().apply {
                    textSize = 9f
                    color = android.graphics.Color.DKGRAY
                    isAntiAlias = true
                }
                val metaBoldPaint = android.graphics.Paint().apply {
                    textSize = 9f
                    color = android.graphics.Color.BLACK
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    isAntiAlias = true
                }
                
                canvas.drawText("Outlet:", 40f, 90f, metaPaint)
                canvas.drawText(outlet, 110f, 90f, metaBoldPaint)
                
                canvas.drawText("Periode:", 40f, 105f, metaPaint)
                canvas.drawText(period, 110f, 105f, metaBoldPaint)
                
                canvas.drawText("Filter Status:", 40f, 120f, metaPaint)
                canvas.drawText(statusFilter, 110f, 120f, metaBoldPaint)
                
                val sdf = java.text.SimpleDateFormat("dd MMMM yyyy, HH:mm", java.util.Locale("id", "ID"))
                canvas.drawText("Tanggal Cetak:", 340f, 90f, metaPaint)
                canvas.drawText(sdf.format(java.util.Date()), 430f, 90f, metaBoldPaint)
                
                canvas.drawText("Transaksi:", 340f, 105f, metaPaint)
                canvas.drawText("${orders.size} Order", 430f, 105f, metaBoldPaint)
                
                canvas.drawText("Total Pendapatan:", 340f, 120f, metaPaint)
                canvas.drawText("Rp ${String.format("%,.0f", totalIncome).replace(",", ".")}", 430f, 120f, metaBoldPaint)
                
                canvas.drawLine(40f, 135f, (pageWidth - 40).toFloat(), 135f, borderPaint)
            } else {
                canvas.drawText("CUCIANKU LAUNDRY - Laporan Keuangan", 40f, 40f, headerPaint)
                canvas.drawText("Periode: $period • Outlet: $outlet", 40f, 53f, subTitlePaint)
                canvas.drawLine(40f, 62f, (pageWidth - 40).toFloat(), 62f, borderPaint)
            }
            
            val pageNumPaint = android.graphics.Paint().apply {
                textSize = 9f
                color = android.graphics.Color.GRAY
                isAntiAlias = true
            }
            canvas.drawText("Halaman $pageNum", (pageWidth - 90).toFloat(), 40f, pageNumPaint)
        }
        
        drawPageHeader(currentPageNumber)
        
        // Draw stats box on first page
        val boxPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(245, 248, 248)
            style = android.graphics.Paint.Style.FILL
            isAntiAlias = true
        }
        val boxBorderPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(18, 140, 126)
            strokeWidth = 1f
            style = android.graphics.Paint.Style.STROKE
            isAntiAlias = true
        }
        
        canvas.drawRoundRect(40f, 145f, (pageWidth - 40).toFloat(), 200f, 8f, 8f, boxPaint)
        canvas.drawRoundRect(40f, 145f, (pageWidth - 40).toFloat(), 200f, 8f, 8f, boxBorderPaint)
        
        val statsLabelPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(100, 100, 100)
            textSize = 9f
            isAntiAlias = true
        }
        val statsValPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(18, 140, 126)
            textSize = 13f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            isAntiAlias = true
        }
        
        canvas.drawText("TOTAL PENDAPATAN", 60f, 168f, statsLabelPaint)
        canvas.drawText("Rp ${String.format("%,.0f", totalIncome).replace(",", ".")}", 60f, 188f, statsValPaint)
        
        canvas.drawText("TOTAL TRANSAKSI", 250f, 168f, statsLabelPaint)
        canvas.drawText("${orders.size} Order", 250f, 188f, statsValPaint)
        
        canvas.drawText("STATUS FILTER", 410f, 168f, statsLabelPaint)
        canvas.drawText(statusFilter, 410f, 188f, statsValPaint)
        
        var y = 215f
        
        fun drawTableHeader() {
            canvas.drawRect(40f, y, (pageWidth - 40).toFloat(), y + 22f, headerBgPaint)
            canvas.drawRect(40f, y, (pageWidth - 40).toFloat(), y + 22f, borderPaint)
            
            val colY = y + 14f
            canvas.drawText("Struk", 48f, colY, headerPaint)
            canvas.drawText("Pelanggan", 125f, colY, headerPaint)
            canvas.drawText("Layanan", 255f, colY, headerPaint)
            canvas.drawText("Status", 375f, colY, headerPaint)
            canvas.drawText("Total", 480f, colY, headerPaint)
            
            y += 22f
        }
        
        drawTableHeader()
        
        val statusBgPaint = android.graphics.Paint().apply {
            style = android.graphics.Paint.Style.FILL
            isAntiAlias = true
        }
        val statusTextPaint = android.graphics.Paint().apply {
            textSize = 8f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            isAntiAlias = true
        }
        
        orders.forEachIndexed { index, order ->
            if (y > 770f) {
                pdfDocument.finishPage(page)
                currentPageNumber++
                pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                
                drawPageHeader(currentPageNumber)
                y = 75f
                drawTableHeader()
            }
            
            if (index % 2 == 1) {
                val rowBgPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.rgb(250, 252, 252)
                    style = android.graphics.Paint.Style.FILL
                }
                canvas.drawRect(40f, y, (pageWidth - 40).toFloat(), y + 20f, rowBgPaint)
            }
            canvas.drawLine(40f, y + 20f, (pageWidth - 40).toFloat(), y + 20f, borderPaint)
            
            val itemY = y + 13f
            canvas.drawText("#ORD-${order.id}", 48f, itemY, rowBoldPaint)
            
            var custName = order.customerName
            if (custName.length > 20) {
                custName = custName.take(18) + ".."
            }
            canvas.drawText(custName, 125f, itemY, rowPaint)
            
            var serviceInfo = "${order.serviceType} (${order.weightQty})"
            if (serviceInfo.length > 22) {
                serviceInfo = serviceInfo.take(20) + ".."
            }
            canvas.drawText(serviceInfo, 255f, itemY, rowPaint)
            
            // Status pill
            val status = order.status
            val (statusBg, statusFg) = when (status) {
                "Order Masuk" -> Pair(android.graphics.Color.rgb(224, 242, 241), android.graphics.Color.rgb(0, 121, 107))
                "Dicuci", "Disetrika", "Packing" -> Pair(android.graphics.Color.rgb(227, 242, 253), android.graphics.Color.rgb(21, 101, 192))
                "Selesai", "Siap Ambil" -> Pair(android.graphics.Color.rgb(232, 245, 233), android.graphics.Color.rgb(46, 125, 50))
                "Diambil" -> Pair(android.graphics.Color.rgb(243, 229, 245), android.graphics.Color.rgb(123, 31, 162))
                else -> Pair(android.graphics.Color.rgb(255, 235, 235), android.graphics.Color.rgb(198, 40, 40))
            }
            
            statusBgPaint.color = statusBg
            statusTextPaint.color = statusFg
            
            val pillLeft = 375f
            val pillTop = y + 3f
            val pillRight = 445f
            val pillBottom = y + 17f
            canvas.drawRoundRect(pillLeft, pillTop, pillRight, pillBottom, 4f, 4f, statusBgPaint)
            
            val statusTextWidth = statusTextPaint.measureText(status)
            val textX = pillLeft + (pillRight - pillLeft - statusTextWidth) / 2
            canvas.drawText(status, textX, y + 12f, statusTextPaint)
            
            val priceStr = "Rp ${String.format("%,.0f", order.finalTotal).replace(",", ".")}"
            val priceWidth = rowBoldPaint.measureText(priceStr)
            canvas.drawText(priceStr, (pageWidth - 48).toFloat() - priceWidth, itemY, rowBoldPaint)
            
            y += 20f
        }
        
        if (y > 700f) {
            pdfDocument.finishPage(page)
            currentPageNumber++
            pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            drawPageHeader(currentPageNumber)
            y = 75f
        }
        
        y += 15f
        canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, borderPaint)
        
        y += 15f
        val footerInfoPaint = android.graphics.Paint().apply {
            textSize = 8f
            color = android.graphics.Color.GRAY
            isAntiAlias = true
        }
        canvas.drawText("* Laporan ini digenerate secara otomatis oleh sistem CucianKu Laundry.", 40f, y, footerInfoPaint)
        canvas.drawText("Mengetahui,", (pageWidth - 160).toFloat(), y + 15f, rowBoldPaint)
        canvas.drawText("Manajer / Owner", (pageWidth - 160).toFloat(), y + 60f, rowPaint)
        canvas.drawLine((pageWidth - 160).toFloat(), y + 65f, (pageWidth - 40).toFloat(), y + 65f, borderPaint)
        
        pdfDocument.finishPage(page)
        
        val result = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                true
            } else {
                false
            }
        } else {
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(downloadsDir, fileName)
            java.io.FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            true
        }
        pdfDocument.close()
        result
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun savePdfToDownloads(context: android.content.Context, content: String, fileName: String): Boolean {
    return try {
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()
        paint.textSize = 12f
        paint.color = android.graphics.Color.BLACK
        
        var y = 40f
        content.split("\n").forEach { line ->
            if (y < 800f) {
                canvas.drawText(line, 40f, y, paint)
                y += 18f
            }
        }
        
        pdfDocument.finishPage(page)
        
        val result = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                true
            } else {
                false
            }
        } else {
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(downloadsDir, fileName)
            java.io.FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            true
        }
        pdfDocument.close()
        result
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

@Composable
fun OutletFinancialSummaryCard(
    outletName: String,
    orders: List<Order>
) {
    // Calculate daily and monthly revenue specifically for the currently selected outlet
    val calToday = Calendar.getInstance()
    val todayStart = calToday.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val calMonth = Calendar.getInstance()
    val monthStart = calMonth.apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    // Filter orders specifically for the selected outlet
    val outletOrders = if (outletName == "Semua Outlet") {
        orders
    } else {
        orders.filter { it.outletName == outletName }
    }

    // Daily Stats specifically for this outlet
    val ordersToday = outletOrders.filter { it.timestamp >= todayStart && it.status != "Batal" }
    val revenueToday = ordersToday.sumOf { it.finalTotal }
    val countToday = ordersToday.size

    // Monthly Stats specifically for this outlet
    val ordersMonth = outletOrders.filter { it.timestamp >= monthStart && it.status != "Batal" }
    val revenueMonth = ordersMonth.sumOf { it.finalTotal }
    val countMonth = ordersMonth.size

    // Selected period state (0 = Hari Ini, 1 = Bulan Ini)
    var selectedPeriod by remember { mutableStateOf(0) }

    val currentOrders = if (selectedPeriod == 0) ordersToday else ordersMonth
    val currentRevenue = if (selectedPeriod == 0) revenueToday else revenueMonth
    val currentCount = if (selectedPeriod == 0) countToday else countMonth
    val periodLabel = if (selectedPeriod == 0) "Hari Ini" else "Bulan Ini"

    // Payment methods breakdown for current selection specifically for this outlet
    val tunaiAmount = currentOrders.filter { it.paymentMethod == "Tunai" }.sumOf { it.finalTotal }
    val transferAmount = currentOrders.filter { it.paymentMethod == "Transfer" }.sumOf { it.finalTotal }
    val qrisAmount = currentOrders.filter { it.paymentMethod == "QRIS" }.sumOf { it.finalTotal }
    val ewalletAmount = currentOrders.filter { it.paymentMethod == "E-Wallet" }.sumOf { it.finalTotal }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("outlet_financial_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Ringkasan Finansial Outlet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (outletName == "Semua Outlet") "Gabungan Seluruh Outlet" else "Khusus Outlet: $outletName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily & Monthly Revenue KPI Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Daily KPI card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPeriod = 0 }
                        .testTag("kpi_daily_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPeriod == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selectedPeriod == 0) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Hari Ini",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedPeriod == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp ${String.format("%,.0f", revenueToday)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selectedPeriod == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$countToday Order",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selectedPeriod == 0) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Monthly KPI card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPeriod = 1 }
                        .testTag("kpi_monthly_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPeriod == 1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selectedPeriod == 1) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Bulan Ini",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedPeriod == 1) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp ${String.format("%,.0f", revenueMonth)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selectedPeriod == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$countMonth Order",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selectedPeriod == 1) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breakdown of payment methods for current selection
            Text(
                text = "Metode Pembayaran ($periodLabel)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PaymentBreakdownRow(label = "Tunai", amount = tunaiAmount, total = currentRevenue)
                    PaymentBreakdownRow(label = "Transfer", amount = transferAmount, total = currentRevenue)
                    PaymentBreakdownRow(label = "QRIS", amount = qrisAmount, total = currentRevenue)
                    PaymentBreakdownRow(label = "E-Wallet", amount = ewalletAmount, total = currentRevenue)
                }
            }

            // Monthly Daily Revenue Breakdown
            if (selectedPeriod == 1) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Riwayat Omset Harian Bulan Ini",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Group monthly orders by day
                val dailyRevenueMap = remember(ordersMonth) {
                    val map = mutableMapOf<String, Double>()
                    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                    ordersMonth.forEach { order ->
                        val dateStr = sdf.format(Date(order.timestamp))
                        map[dateStr] = (map[dateStr] ?: 0.0) + order.finalTotal
                    }
                    map.toList().sortedWith(compareByDescending { it.first })
                }

                if (dailyRevenueMap.isEmpty()) {
                    Text(
                        text = "Belum ada transaksi di bulan ini.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(dailyRevenueMap) { (dateStr, revenue) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Rp ${String.format("%,.0f", revenue)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
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

@Composable
fun PaymentBreakdownRow(label: String, amount: Double, total: Double) {
    val percentage = if (total > 0) amount / total else 0.0
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(70.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage.toFloat().coerceIn(0f, 1f))
                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp))
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(90.dp)) {
            Text(
                text = "Rp ${String.format("%,.0f", amount)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${String.format("%.1f", percentage * 100)}%",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AdminPengeluaranTab(viewModel: LaundryViewModel, onBack: (() -> Unit)? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val expenses by viewModel.expensesState.collectAsState()
    val outlets by viewModel.outletsState.collectAsState()

    var selectedTabState by remember { mutableStateOf(0) } // 0 = Tambah Pengeluaran, 1 = Riwayat

    // Form states
    var selectedOutletName by remember { mutableStateOf("CucianKu Pusat") }
    var selectedType by remember { mutableStateOf("Gaji") } // Gaji, Operasional, Sewa, Lainnya
    var amountInput by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var notesInput by remember { mutableStateOf("") }

    // Dropdown expanded states
    var outletDropdownExpanded by remember { mutableStateOf(false) }

    // Format utility
    val formatEpochToDate: (Long) -> String = { epoch ->
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
        sdf.format(java.util.Date(epoch))
    }

    // Set default outlet if not set and outlets list is populated
    LaunchedEffect(outlets) {
        if (outlets.isNotEmpty() && selectedOutletName.isBlank()) {
            selectedOutletName = outlets.first().name
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Upper back bar if back handler is provided
        if (onBack != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PENGELUARAN CABANG",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Upper banner/header displaying total expenditure for quick overview
        val totalSpending = expenses.sumOf { it.amount }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Pengeluaran Dicatat",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rp ${String.format("%,.0f", totalSpending)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Custom clean tabs selector
        TabRow(
            selectedTabIndex = selectedTabState,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Tab(
                selected = selectedTabState == 0,
                onClick = { selectedTabState = 0 }
            ) {
                Text(
                    text = "Tambah Pengeluaran",
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Tab(
                selected = selectedTabState == 1,
                onClick = { selectedTabState = 1 }
            ) {
                Text(
                    text = "Riwayat Catatan",
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTabState == 0) {
            // Screen 0: TAMBAH PENGELUARAN FORM
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Catat Pengeluaran Baru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Lengkapi form di bawah ini untuk mencatat pengeluaran operasional cabang laundry.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1. SELECT OUTLET
                Text(
                    text = "Outlet / Cabang",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { outletDropdownExpanded = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = selectedOutletName.ifBlank { "Pilih Outlet" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = outletDropdownExpanded,
                        onDismissRequest = { outletDropdownExpanded = false }
                    ) {
                        outlets.forEach { outlet ->
                            DropdownMenuItem(
                                text = { Text(outlet.name) },
                                onClick = {
                                    selectedOutletName = outlet.name
                                    outletDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // 2. SELECT TYPE (Gaji, Operasional, Sewa, Lainnya)
                Text(
                    text = "Tipe Pengeluaran",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val expenseTypes = listOf("Gaji", "Operasional", "Sewa", "Lainnya")
                    expenseTypes.forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. AMOUNT INPUT (Jumlah Pengeluaran)
                Text(
                    text = "Jumlah Pengeluaran (Rp)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            amountInput = input
                        }
                    },
                    placeholder = { Text("0") },
                    leadingIcon = {
                        Text(
                            text = "Rp",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                // 4. DATE PICKER
                Text(
                    text = "Tanggal Pengeluaran",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val calPick = java.util.Calendar.getInstance().apply { timeInMillis = dateMillis }
                            android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val chosenCal = java.util.Calendar.getInstance().apply {
                                        set(java.util.Calendar.YEAR, year)
                                        set(java.util.Calendar.MONTH, month)
                                        set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                                    }
                                    dateMillis = chosenCal.timeInMillis
                                },
                                calPick.get(java.util.Calendar.YEAR),
                                calPick.get(java.util.Calendar.MONTH),
                                calPick.get(java.util.Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = formatEpochToDate(dateMillis),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 5. NOTES (Keterangan)
                Text(
                    text = "Keterangan (Opsional)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    placeholder = { Text("Keterangan tambahan pengeluaran") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Notes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SAVE BUTTON
                Button(
                    onClick = {
                        val amountVal = amountInput.toDoubleOrNull() ?: 0.0
                        if (amountVal <= 0.0) {
                            android.widget.Toast.makeText(context, "Silakan masukkan jumlah pengeluaran yang valid!", android.widget.Toast.LENGTH_SHORT).show()
                        } else if (selectedOutletName.isBlank()) {
                            android.widget.Toast.makeText(context, "Silakan pilih outlet terlebih dahulu!", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addExpense(
                                outletName = selectedOutletName,
                                type = selectedType,
                                amount = amountVal,
                                dateMillis = dateMillis,
                                notes = notesInput
                            )
                            android.widget.Toast.makeText(context, "Pengeluaran berhasil disimpan!", android.widget.Toast.LENGTH_SHORT).show()
                            // Reset inputs
                            amountInput = ""
                            notesInput = ""
                            dateMillis = System.currentTimeMillis()
                            // Switch to history
                            selectedTabState = 1
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Simpan Catatan Pengeluaran",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // Screen 1: LIST / RIWAYAT PENGELUARAN
            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum Ada Catatan Pengeluaran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Semua pengeluaran yang disimpan akan tampil di sini.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(expenses) { exp ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Colored dot based on category
                                    val typeColor = when (exp.type) {
                                        "Gaji" -> Color(0xFFFFB300)
                                        "Operasional" -> Color(0xFF10B981)
                                        "Sewa" -> Color(0xFF3B82F6)
                                        else -> Color(0xFF8B5CF6)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(typeColor, shape = CircleShape)
                                    )
                                    Column {
                                        Text(
                                            text = exp.type,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = exp.outletName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (exp.notes.isNotBlank()) {
                                            Text(
                                                text = exp.notes,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                            )
                                        }
                                        Text(
                                            text = formatEpochToDate(exp.dateMillis),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Rp ${String.format("%,.0f", exp.amount)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteExpense(exp)
                                            android.widget.Toast.makeText(context, "Catatan pengeluaran dihapus!", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Hapus",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
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

