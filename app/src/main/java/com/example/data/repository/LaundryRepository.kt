package com.example.data.repository

import com.example.data.db.LaundryDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class LaundryRepository(private val laundryDao: LaundryDao) {

    val settingsFlow: Flow<AppSettings?> = laundryDao.getSettingsFlow()
    val cashiersFlow: Flow<List<Cashier>> = laundryDao.getCashiersFlow()
    val outletsFlow: Flow<List<Outlet>> = laundryDao.getOutletsFlow()
    val customersFlow: Flow<List<Customer>> = laundryDao.getCustomersFlow()
    val ordersFlow: Flow<List<Order>> = laundryDao.getOrdersFlow()
    val cashMutationsFlow: Flow<List<CashMutation>> = laundryDao.getCashMutationsFlow()

    suspend fun getSettingsDirect(): AppSettings? = laundryDao.getSettingsDirect()

    suspend fun saveSettings(settings: AppSettings) {
        laundryDao.insertSettings(settings)
    }

    suspend fun insertCashier(cashier: Cashier) {
        laundryDao.insertCashier(cashier)
    }

    suspend fun updateCashier(cashier: Cashier) {
        laundryDao.updateCashier(cashier)
    }

    suspend fun insertOutlet(outlet: Outlet) {
        laundryDao.insertOutlet(outlet)
    }

    suspend fun updateOutlet(outlet: Outlet) {
        laundryDao.updateOutlet(outlet)
    }

    suspend fun deleteOutlet(outlet: Outlet) {
        laundryDao.deleteOutlet(outlet)
    }

    suspend fun insertCustomer(customer: Customer) {
        laundryDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) {
        laundryDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        laundryDao.deleteCustomer(customer)
    }

    suspend fun insertOrder(order: Order): Long {
        return laundryDao.insertOrder(order)
    }

    suspend fun updateOrder(order: Order) {
        laundryDao.updateOrder(order)
    }

    suspend fun insertCashMutation(mutation: CashMutation) {
        laundryDao.insertCashMutation(mutation)
    }

    // Generate automatically: KSR001, KSR002 etc.
    suspend fun generateNextCashierId(): String {
        val list = cashiersFlow.firstOrNull() ?: emptyList()
        val nextNum = if (list.isEmpty()) {
            1
        } else {
            val maxNum = list.mapNotNull { cashier ->
                val digits = cashier.cashierId.replace("KSR", "")
                digits.toIntOrNull()
            }.maxOrNull() ?: 0
            maxNum + 1
        }
        return "KSR${nextNum.toString().padStart(3, '0')}"
    }

    suspend fun prepopulateIfEmpty() {
        // Check settings
        val currentSettings = laundryDao.getSettingsDirect()
        if (currentSettings == null) {
            laundryDao.insertSettings(AppSettings())
        }

        // Check cashiers
        val currentCashiers = cashiersFlow.firstOrNull() ?: emptyList()
        if (currentCashiers.isEmpty()) {
            laundryDao.insertCashier(Cashier("KSR001", "Budi Santoso", "Pagi", true))
            laundryDao.insertCashier(Cashier("KSR002", "Siti Aminah", "Siang", true))
            laundryDao.insertCashier(Cashier("KSR003", "Ahmad Fauzi", "Malam", true))
        }

        // Check outlets
        val currentOutlets = outletsFlow.firstOrNull() ?: emptyList()
        if (currentOutlets.isEmpty()) {
            laundryDao.insertOutlet(Outlet(0, "CucianKu Pusat", "Jl. Jenderal Sudirman No. 12"))
            laundryDao.insertOutlet(Outlet(0, "CucianKu Sudirman", "Jl. Ahmad Yani No. 45"))
        }

        // Check customers
        val currentCustomers = customersFlow.firstOrNull() ?: emptyList()
        if (currentCustomers.isEmpty()) {
            laundryDao.insertCustomer(Customer(0, "Rian Prasetia", "081234567890", false))
            laundryDao.insertCustomer(Customer(0, "Dewi Lestari", "085712345678", false))
            laundryDao.insertCustomer(Customer(0, "Andi Wijaya", "081987654321", false))
            laundryDao.insertCustomer(Customer(0, "Lina Marlina", "082111223344", true)) // Inactive customer
        }

        // Check orders
        val currentOrders = ordersFlow.firstOrNull() ?: emptyList()
        if (currentOrders.isEmpty()) {
            val cal = Calendar.getInstance()
            
            // Order 1: Selesai (Today)
            laundryDao.insertOrder(
                Order(
                    0,
                    "Rian Prasetia",
                    "081234567890",
                    "CucianKu Pusat",
                    "Budi Santoso",
                    "Kiloan",
                    3.5,
                    "Wangi lavender, setrika rapi",
                    6000.0,
                    0.0,
                    21000.0,
                    "Tunai",
                    "Selesai",
                    cal.timeInMillis
                )
            )

            // Order 2: Dicuci (Today)
            laundryDao.insertOrder(
                Order(
                    0,
                    "Dewi Lestari",
                    "085712345678",
                    "CucianKu Pusat",
                    "Budi Santoso",
                    "Express",
                    2.0,
                    "Jangan pakai pemutih",
                    15000.0,
                    5000.0,
                    25000.0,
                    "QRIS",
                    "Dicuci",
                    cal.timeInMillis
                )
            )

            // Order Today - Kiloan
            laundryDao.insertOrder(
                Order(
                    0,
                    "Rian Prasetia",
                    "081234567890",
                    "CucianKu Pusat",
                    "Budi Santoso",
                    "Kiloan",
                    5.0,
                    "Cuci wangi, setrika licin",
                    6000.0,
                    0.0,
                    30000.0,
                    "Tunai",
                    "Selesai",
                    cal.timeInMillis
                )
            )

            // Order Today - Satuan
            laundryDao.insertOrder(
                Order(
                    0,
                    "Andi Wijaya",
                    "081987654321",
                    "CucianKu Pusat",
                    "Budi Santoso",
                    "Satuan",
                    3.0,
                    "Bedcover sedang",
                    12000.0,
                    5000.0,
                    41000.0,
                    "Tunai",
                    "Selesai",
                    cal.timeInMillis
                )
            )

            // Order Today - Meteran
            laundryDao.insertOrder(
                Order(
                    0,
                    "Lina Marlina",
                    "082111223344",
                    "CucianKu Pusat",
                    "Budi Santoso",
                    "Meteran",
                    8.0,
                    "Karpet ruang tamu",
                    10000.0,
                    0.0,
                    80000.0,
                    "QRIS",
                    "Selesai",
                    cal.timeInMillis
                )
            )

            // Order 3: Diambil (Yesterday)
            cal.add(Calendar.DAY_OF_YEAR, -1)
            laundryDao.insertOrder(
                Order(
                    0,
                    "Andi Wijaya",
                    "081987654321",
                    "CucianKu Sudirman",
                    "Siti Aminah",
                    "Satuan",
                    1.0,
                    "Jas hitam pesta",
                    10000.0,
                    0.0,
                    10000.0,
                    "Transfer",
                    "Diambil",
                    cal.timeInMillis
                )
            )
        }

        // Check cash mutations
        val currentMutations = cashMutationsFlow.firstOrNull() ?: emptyList()
        if (currentMutations.isEmpty()) {
            val cal = Calendar.getInstance()
            laundryDao.insertCashMutation(
                CashMutation(0, "Budi Santoso", "Kas masuk", 100000.0, "Saldo Awal Kasir Budi", cal.timeInMillis)
            )
            laundryDao.insertCashMutation(
                CashMutation(0, "Budi Santoso", "Pengeluaran operasional", 15000.0, "Beli Deterjen Tambahan", cal.timeInMillis)
            )
        }
    }
}
