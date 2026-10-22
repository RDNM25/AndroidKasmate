package com.arunk.dashboardlogin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.json.JSONArray
import org.json.JSONObject
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
    private const val STORAGE_KEY = "students"
    val students: SnapshotStateList<Siswa> = mutableStateListOf()

    private var nextId = 1L
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    fun initialize() {
        students.clear()
        val savedStudents = LocalDataStore.read(STORAGE_KEY)?.let(::JSONArray) ?: JSONArray()
        for (index in 0 until savedStudents.length()) {
            val savedStudent = savedStudents.optJSONObject(index) ?: continue
            val paymentsJson = savedStudent.optJSONArray("payments") ?: JSONArray()
            val payments = (0 until paymentsJson.length()).mapNotNull { paymentIndex ->
                paymentsJson.optJSONObject(paymentIndex)?.let { payment ->
                    PaymentRecord(payment.optLong("amount"), payment.optString("date"))
                }
            }
            students += Siswa(
                id = savedStudent.optLong("id"),
                nama = savedStudent.optString("nama"),
                owed = savedStudent.optLong("owed"),
                payments = payments
            )
        }
        nextId = (students.maxOfOrNull { it.id } ?: 0L) + 1L
    }

    fun addSiswa(nama: String, owed: Long): Boolean {
        val student = Siswa(id = nextId++, nama = nama, owed = owed)
        students.add(student)
        if (!persist()) {
            students.remove(student)
            nextId--
            return false
        }
        return true
    }

    fun getById(id: Long): Siswa? = students.firstOrNull { it.id == id }

    fun recordPayment(id: Long, amount: Long): Boolean {
        val index = students.indexOfFirst { it.id == id }
        if (index == -1 || amount <= 0) return false

        val current = students[index]
        if (amount > current.owed) return false

        val updatedPayments = current.payments + PaymentRecord(
            amount = amount,
            date = dateFormatter.format(Date())
        )
        students[index] = current.copy(owed = current.owed - amount, payments = updatedPayments)
        val studentState = serializeStudents()
        if (!KasRepository.tambahKas(
                amount,
                "Pembayaran dari ${current.nama}",
                mapOf(STORAGE_KEY to studentState)
            )
        ) {
            students[index] = current
            return false
        }
        return true
    }

    private fun persist(): Boolean = LocalDataStore.write(STORAGE_KEY, serializeStudents())

    private fun serializeStudents(): String {
        val savedStudents = JSONArray()
        students.forEach { student ->
            val payments = JSONArray()
            student.payments.forEach { payment ->
                payments.put(JSONObject().put("amount", payment.amount).put("date", payment.date))
            }
            savedStudents.put(
                JSONObject()
                    .put("id", student.id)
                    .put("nama", student.nama)
                    .put("owed", student.owed)
                    .put("payments", payments)
            )
        }
        return savedStudents.toString()
    }
}
