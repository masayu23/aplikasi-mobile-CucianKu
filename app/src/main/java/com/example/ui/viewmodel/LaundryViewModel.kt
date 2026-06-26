package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.LaundryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

enum class Screen {
    SPLASH,
    ROLE_SELECTION,
    CASHIER_LOGIN,
    ADMIN_DASHBOARD,
    CASHIER_DASHBOARD,
    KELOLA_KASIR,
    KELOLA_OUTLET,
    CARI_PELANGGAN,
    ANALISA_PELANGGAN,
    ANALISA_LAYANAN,
    LAPORAN_PEMESANAN,
    LAPORAN_KAS,
    SETTING_KASIR,
    ORDER_BARU,
    CRUD_PELANGGAN,
    MUTASI_KAS
}

class LaundryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LaundryRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LaundryRepository(database.laundryDao())
        
        // Populate default data if empty
        viewModelScope.launch(Dispatchers.IO) {
            repository.prepopulateIfEmpty()
        }
    }

    // Navigation and Session State
    var currentScreen = MutableStateFlow(Screen.SPLASH)
    val navigationStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        navigationStack.add(currentScreen.value)
        currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (navigationStack.isNotEmpty()) {
            currentScreen.value = navigationStack.removeAt(navigationStack.size - 1)
            return true
        }
        return false
    }

    var selectedRole = MutableStateFlow<String?>(null) // "Admin" or "Kasir"
    var activeCashier = MutableStateFlow<Cashier?>(null)
    var activeOutlet = MutableStateFlow<Outlet?>(null)
    var activeShift = MutableStateFlow<String>("Pagi")

    // Admin state
    var adminSelectedTab = MutableStateFlow(0) // 0 = Beranda, 1 = Laporan, 2 = Pengaturan
    var adminSelectedOutlet = MutableStateFlow("Semua Outlet")

    // Database flows
    val settingsState = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    val cashiersState = repository.cashiersFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val outletsState = repository.outletsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val customersState = repository.customersFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val ordersState = repository.ordersFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cashMutationsState = repository.cashMutationsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Temp WA notification message to trigger UI Toast / Overlay
    var pendingWaNotification = MutableStateFlow<String?>(null)

    // Admin Settings actions
    fun updateSettings(settings: AppSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSettings(settings)
        }
    }

    // Cashier Actions
    fun addCashier(name: String, shift: String, assignedOutlet: String, isActive: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            val autoId = repository.generateNextCashierId()
            val newCashier = Cashier(autoId, name, shift, isActive, assignedOutlet)
            repository.insertCashier(newCashier)
        }
    }

    fun updateCashier(cashier: Cashier) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCashier(cashier)
        }
    }

    // Outlet Actions
    fun addOutlet(name: String, address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertOutlet(Outlet(0, name, address))
        }
    }

    fun updateOutlet(outlet: Outlet) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateOutlet(outlet)
        }
    }

    fun deleteOutlet(outlet: Outlet) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteOutlet(outlet)
        }
    }

    // Customer Actions
    fun addCustomer(name: String, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCustomer(Customer(0, name, phone, false))
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCustomer(customer)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCustomer(customer)
        }
    }

    // Order Actions
    fun createOrder(
        customerName: String,
        customerPhone: String,
        serviceType: String,
        weightQty: Double,
        notes: String,
        discountAmount: Double,
        paymentMethod: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val settings = settingsState.value ?: AppSettings()
            
            // Auto add or activate customer
            val existingCustomers = customersState.value
            val match = existingCustomers.find { it.phone == customerPhone || it.name.equals(customerName, true) }
            if (match == null) {
                repository.insertCustomer(Customer(0, customerName, customerPhone, false))
            } else if (match.isInactive) {
                repository.updateCustomer(match.copy(isInactive = false))
            }

            val pricePerUnit = when (serviceType) {
                "Kiloan" -> settings.priceKiloan
                "Satuan" -> settings.priceSatuan
                "Meteran" -> settings.priceMeteran
                "Express" -> settings.priceExpress
                else -> settings.priceKiloan
            }

            val totalRaw = pricePerUnit * weightQty
            val allowedDiscount = if (discountAmount > settings.maksDiskonManual) settings.maksDiskonManual else discountAmount
            val finalTotal = (totalRaw - allowedDiscount).coerceAtLeast(0.0)

            val newOrder = Order(
                0,
                customerName,
                customerPhone,
                activeOutlet.value?.name ?: "CucianKu Pusat",
                activeCashier.value?.name ?: "Kasir Utama",
                serviceType,
                weightQty,
                notes,
                pricePerUnit,
                allowedDiscount,
                finalTotal,
                paymentMethod,
                "Order Masuk",
                System.currentTimeMillis()
            )

            repository.insertOrder(newOrder)

            // Auto WA Notification Simulation
            if (settings.isWaOnOrderCreated) {
                pendingWaNotification.value = "Kirim WA ke $customerName ($customerPhone):\n\"Halo $customerName, order laundry CucianKu ($serviceType) Anda telah dibuat dengan total Rp ${String.format("%,.0f", finalTotal)}. Terima kasih!\""
            }
        }
    }

    fun addDummyTodayOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            val cal = Calendar.getInstance()
            val todayMs = cal.timeInMillis
            
            // Generate distinct mock customers
            val mockCustomers = listOf(
                Customer(0, "Pratama Yoga", "081122233344", false),
                Customer(0, "Amalia Putri", "081223344556", false),
                Customer(0, "Suryadi Wibowo", "081334455667", false),
                Customer(0, "Diana Lestari", "081445566778", false),
                Customer(0, "Kevin Sanjaya", "081556677889", false)
            )
            mockCustomers.forEach { repository.insertCustomer(it) }

            val d1 = Order(0, "Pratama Yoga", "081122233344", "CucianKu Pusat", "Budi Santoso", "Kiloan", 4.5, "Cuci wangi, setrika licin", 6000.0, 0.0, 27000.0, "Tunai", "Selesai", todayMs)
            val d2 = Order(0, "Amalia Putri", "081223344556", "CucianKu Pusat", "Budi Santoso", "Express", 3.0, "Tanpa pemutih, pewangi ekstra", 15000.0, 0.0, 45000.0, "QRIS", "Dicuci", todayMs)
            val d3 = Order(0, "Suryadi Wibowo", "081334455667", "CucianKu Pusat", "Siti Aminah", "Satuan", 2.0, "Bedcover besar & selimut", 12000.0, 4000.0, 20000.0, "Tunai", "Selesai", todayMs)
            val d4 = Order(0, "Diana Lestari", "081445566778", "CucianKu Sudirman", "Ahmad Fauzi", "Meteran", 6.0, "Karpet wol tebal", 10000.0, 0.0, 60000.0, "QRIS", "Selesai", todayMs)
            val d5 = Order(0, "Kevin Sanjaya", "081556677889", "CucianKu Sudirman", "Siti Aminah", "Kiloan", 5.5, "Pakaian olahraga", 6000.0, 0.0, 33000.0, "Tunai", "Order Masuk", todayMs)
            val d6 = Order(0, "Andi Wijaya", "081987654321", "CucianKu Pusat", "Budi Santoso", "Meteran", 5.0, "Gordyn rumah tangga", 10000.0, 5000.0, 45000.0, "Transfer", "Selesai", todayMs)
            
            repository.insertOrder(d1)
            repository.insertOrder(d2)
            repository.insertOrder(d3)
            repository.insertOrder(d4)
            repository.insertOrder(d5)
            repository.insertOrder(d6)
        }
    }

    fun updateOrderStatus(order: Order, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = order.copy(status = newStatus)
            repository.updateOrder(updated)

            val settings = settingsState.value ?: AppSettings()
            if (newStatus == "Selesai" && settings.isWaOnOrderFinished) {
                pendingWaNotification.value = "Kirim WA ke ${order.customerName} (${order.customerPhone}):\n\"Halo ${order.customerName}, cucian Anda sudah SELESAI dan siap diambil di ${order.outletName}. Silakan datang ya!\""
            } else if (newStatus == "Batal" && settings.isWaOnOrderCancelled) {
                pendingWaNotification.value = "Kirim WA ke ${order.customerName} (${order.customerPhone}):\n\"Halo ${order.customerName}, order laundry Anda telah dibatalkan oleh kasir.\""
            }
        }
    }

    // Cash Mutation Actions
    fun addCashMutation(type: String, amount: Double, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val mutation = CashMutation(
                0,
                activeCashier.value?.name ?: "Sistem",
                type,
                amount,
                notes,
                System.currentTimeMillis()
            )
            repository.insertCashMutation(mutation)
        }
    }

    // Clear WA notification after displaying
    fun clearPendingWaNotification() {
        pendingWaNotification.value = null
    }

    // ==========================================
    // ADVANCED GEMINI AI ADVISOR FOR LAUNDRY
    // ==========================================
    var aiState = MutableStateFlow<String?>(null)
    var isAiLoading = MutableStateFlow(false)

    fun getAiGrowthConsultation() {
        viewModelScope.launch(Dispatchers.IO) {
            isAiLoading.value = true
            aiState.value = null

            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                aiState.value = "Kunci API Gemini tidak diatur. Silakan atur GEMINI_API_KEY Anda di AI Studio Secrets Panel."
                isAiLoading.value = false
                return@launch
            }

            // Prepare local summary parameters for Gemini
            val totalOrders = ordersState.value.size
            val ordersToday = ordersState.value.filter { isToday(it.timestamp) }
            val incomeToday = ordersToday.sumOf { it.finalTotal }
            val customersCount = customersState.value.size
            val outletsCount = outletsState.value.size

            // Group service type breakdown
            val kiloanCount = ordersState.value.count { it.serviceType == "Kiloan" }
            val satuanCount = ordersState.value.count { it.serviceType == "Satuan" }
            val meteranCount = ordersState.value.count { it.serviceType == "Meteran" }
            val expressCount = ordersState.value.count { it.serviceType == "Express" }

            val prompt = """
                Anda adalah Konsultan Bisnis Smart Laundry AI asisten untuk aplikasi CucianKu.
                Berikut adalah ringkasan performa laundry hari ini:
                - Jumlah Total Pesanan: $totalOrders
                - Jumlah Pesanan Hari Ini: ${ordersToday.size}
                - Pendapatan Hari Ini: Rp ${String.format("%,.0f", incomeToday)}
                - Total Pelanggan Terdaftar: $customersCount
                - Jumlah Cabang/Outlet: $outletsCount
                - Rincian Layanan yang Digunakan Pelanggan:
                  * Kiloan: $kiloanCount kali
                  * Satuan: $satuanCount kali
                  * Meteran: $meteranCount kali
                  * Express: $expressCount kali

                Berikan analisis singkat (maksimal 150 kata) dalam Bahasa Indonesia yang sangat informatif, profesional, dan memberikan 3 strategi taktis untuk meningkatkan profitabilitas laundry atau mengaktifkan kembali pelanggan pasif! Gunakan bullet point yang bersih dan gaya bahasa yang ramah tapi berwibawa. Jangan sebutkan detail teknis kode.
            """.trimIndent()

            try {
                val client = OkHttpClient()
                val requestBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        })
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                    .post(requestBody.toString().toRequestBody(mediaType))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: ""
                        val json = JSONObject(responseBody)
                        val text = json.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                        aiState.value = text
                    } else {
                        aiState.value = "Gagal memanggil AI Advisor: Kode Respon ${response.code}. Pastikan kuota dan kredensial API Key valid."
                    }
                }
            } catch (e: Exception) {
                aiState.value = "Terjadi kesalahan koneksi AI: ${e.localizedMessage}"
            } finally {
                isAiLoading.value = false
            }
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }
}
