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
    val expensesFlow: Flow<List<Expense>> = laundryDao.getExpensesFlow()
    val servicesFlow: Flow<List<Service>> = laundryDao.getServicesFlow()

    fun getServicePricesFlow(serviceId: Int): Flow<List<ServicePrice>> = laundryDao.getServicePricesFlow(serviceId)

    suspend fun getSettingsDirect(): AppSettings? = laundryDao.getSettingsDirect()

    suspend fun saveSettings(settings: AppSettings) {
        laundryDao.insertSettings(settings)
    }

    suspend fun insertService(service: Service): Long {
        return laundryDao.insertService(service)
    }

    suspend fun updateService(service: Service) {
        laundryDao.updateService(service)
    }

    suspend fun deleteService(service: Service) {
        laundryDao.deleteService(service)
    }

    suspend fun insertServicePrice(servicePrice: ServicePrice) {
        laundryDao.insertServicePrice(servicePrice)
    }

    suspend fun updateServicePrice(servicePrice: ServicePrice) {
        laundryDao.updateServicePrice(servicePrice)
    }

    suspend fun deleteServicePrice(servicePrice: ServicePrice) {
        laundryDao.deleteServicePrice(servicePrice)
    }

    suspend fun insertCashier(cashier: Cashier) {
        laundryDao.insertCashier(cashier)
    }

    suspend fun updateCashier(cashier: Cashier) {
        laundryDao.updateCashier(cashier)
    }

    suspend fun deleteCashier(cashier: Cashier) {
        laundryDao.deleteCashier(cashier)
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

    suspend fun insertExpense(expense: Expense) {
        laundryDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        laundryDao.deleteExpense(expense)
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
            val now = System.currentTimeMillis()
            laundryDao.insertCustomer(Customer(0, "Rian Prasetia", "081234567890", "Jl. Merdeka No. 5", false, now - 2 * 24 * 60 * 60 * 1000L)) // 2 days ago
            laundryDao.insertCustomer(Customer(0, "Dewi Lestari", "085712345678", "Jl. Mawar No. 12", false, now - 15 * 24 * 60 * 60 * 1000L)) // 15 days ago
            laundryDao.insertCustomer(Customer(0, "Andi Wijaya", "081987654321", "Jl. Melati No. 8", false, now - 40 * 24 * 60 * 60 * 1000L)) // 40 days ago
            laundryDao.insertCustomer(Customer(0, "Lina Marlina", "082111223344", "Jl. Flamboyan No. 3", true, now - 50 * 24 * 60 * 60 * 1000L)) // 50 days ago
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

        // Check expenses
        val currentExpenses = expensesFlow.firstOrNull() ?: emptyList()
        if (currentExpenses.isEmpty()) {
            val cal = Calendar.getInstance()
            laundryDao.insertExpense(Expense(0, "CucianKu Pusat", "Gaji", 2500000.0, cal.timeInMillis - 5 * 24 * 60 * 60 * 1000L, "Gaji Bulanan Staff"))
            laundryDao.insertExpense(Expense(0, "CucianKu Pusat", "Operasional", 75000.0, cal.timeInMillis - 2 * 24 * 60 * 60 * 1000L, "Beli Deterjen & Pewangi"))
            laundryDao.insertExpense(Expense(0, "CucianKu Sudirman", "Sewa", 1200000.0, cal.timeInMillis - 10 * 24 * 60 * 60 * 1000L, "Sewa Toko Bulanan"))
            laundryDao.insertExpense(Expense(0, "CucianKu Pusat", "Lainnya", 50000.0, cal.timeInMillis, "Servis Setrika Uap"))
        }

        // Check services
        val currentServices = servicesFlow.firstOrNull() ?: emptyList()
        if (currentServices.isEmpty()) {
            val serviceId1 = laundryDao.insertService(Service(0, "Cuci + Setrika", "Kiloan"))
            laundryDao.insertServicePrice(ServicePrice(0, serviceId1.toInt(), "Reguler", 7000.0))
            laundryDao.insertServicePrice(ServicePrice(0, serviceId1.toInt(), "Express", 10000.0))

            val serviceId2 = laundryDao.insertService(Service(0, "Setrika", "Satuan"))
            // Service 2 has 0 price packages just to match the screenshot for example.
        }
    }
}
