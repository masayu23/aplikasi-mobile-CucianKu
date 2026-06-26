package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LaundryDao {
    // App Settings
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettings)

    // Cashiers
    @Query("SELECT * FROM cashiers ORDER BY cashierId ASC")
    fun getCashiersFlow(): Flow<List<Cashier>>

    @Query("SELECT * FROM cashiers WHERE cashierId = :id LIMIT 1")
    suspend fun getCashierById(id: String): Cashier?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashier(cashier: Cashier)

    @Update
    suspend fun updateCashier(cashier: Cashier)

    // Outlets
    @Query("SELECT * FROM outlets ORDER BY name ASC")
    fun getOutletsFlow(): Flow<List<Outlet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutlet(outlet: Outlet)

    @Update
    suspend fun updateOutlet(outlet: Outlet)

    @Delete
    suspend fun deleteOutlet(outlet: Outlet)

    // Customers
    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun getCustomersFlow(): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    // Orders
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getOrdersFlow(): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)

    // Cash Mutations
    @Query("SELECT * FROM cash_mutations ORDER BY timestamp DESC")
    fun getCashMutationsFlow(): Flow<List<CashMutation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashMutation(mutation: CashMutation)
}
