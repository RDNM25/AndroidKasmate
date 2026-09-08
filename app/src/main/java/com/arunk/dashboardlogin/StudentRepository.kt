package com.arunk.dashboardlogin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PaymentRecord(
    val amount: Long,
    val date: String
)

data class Siswa(
    val id: Long,
    val nama: String,
    val owed: Long,
    val payments: List<PaymentRecord> = emptyList()
)

/**
 * Simple in-memory store for per-student dues (uang kas per siswa).
 * Note: resets when the app process is killed, same limitation the
 * rest of the app's in-memory data has (no Room/DataStore wired up yet).
 */
object StudentRepository {
    val students: SnapshotStateList<Siswa> = mutableStateListOf()

    private var nextId = 1L
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    fun addSiswa(nama: String, owed: Long) {
        students.add(Siswa(id = nextId++, nama = nama, owed = owed))
    }

    fun getById(id: Long): Siswa? = students.firstOrNull { it.id == id }

    /**
     * Records a payment from a student, reducing what they owe and
     * adding the payment to the shared class fund (KasRepository).
     */
    fun recordPayment(id: Long, amount: Long): Boolean {
        val index = students.indexOfFirst { it.id == id }
        if (index == -1 || amount <= 0) return false

        val current = students[index]
        val newOwed = (current.owed - amount).coerceAtLeast(0)
        val updatedPayments = current.payments + PaymentRecord(
            amount = amount,
            date = dateFormatter.format(Date())
        )
        students[index] = current.copy(owed = newOwed, payments = updatedPayments)

        KasRepository.tambahKas(amount, "Pembayaran dari ${current.nama}")

        return true
    }
}
