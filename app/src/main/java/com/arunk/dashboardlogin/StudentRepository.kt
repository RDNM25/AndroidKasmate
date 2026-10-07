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

object StudentRepository {
    val students: SnapshotStateList<Siswa> = mutableStateListOf()

    private var nextId = 1L
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    fun addSiswa(nama: String, owed: Long) {
        students.add(Siswa(id = nextId++, nama = nama, owed = owed))
    }

    fun getById(id: Long): Siswa? = students.firstOrNull { it.id == id }

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
