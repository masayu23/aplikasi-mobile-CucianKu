package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val statistikHarianEnabled: Boolean = true,
    val permissionBolehBatal: Boolean = true,
    val permissionBolehEdit: Boolean = true,
    val permissionBolehHapusPelanggan: Boolean = true,
    val permissionBolehLihatLaporanKas: Boolean = true,
    val permissionBolehLihatSaldo: Boolean = true,
    val maksDiskonManual: Double = 50000.0,
    val priceKiloan: Double = 6000.0,
    val priceSatuan: Double = 10000.0,
    val priceMeteran: Double = 12000.0,
    val priceExpress: Double = 15000.0,
    val isWaOnOrderCreated: Boolean = true,
    val isWaOnOrderFinished: Boolean = true,
    val isWaOnOrderCancelled: Boolean = true
)

@Entity(tableName = "cashiers")
data class Cashier(
    @PrimaryKey val cashierId: String, // KSR001, KSR002...
    val name: String,
    val shift: String, // Pagi, Siang, Malam
    val isActive: Boolean = true,
    val assignedOutlet: String = "CucianKu Pusat"
)

@Entity(tableName = "outlets")
data class Outlet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String, // No WA
    val isInactive: Boolean = false
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val customerPhone: String,
    val outletName: String,
    val cashierName: String,
    val serviceType: String, // Kiloan, Satuan, Meteran, Express
    val weightQty: Double,
    val notes: String,
    val pricePerUnit: Double,
    val discountAmount: Double,
    val finalTotal: Double,
    val paymentMethod: String, // Tunai, Transfer, QRIS, E-Wallet
    val status: String, // Order Masuk, Dicuci, Disetrika, Packing, Selesai, Diambil, Batal
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cash_mutations")
data class CashMutation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cashierName: String,
    val type: String, // Kas Masuk, Kas Keluar, Refund, Pengeluaran operasional
    val amount: Double,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)
