package com.arunk.dashboardlogin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TransaksiType {
    MASUK,
    KELUAR
}

data class Transaksi(
    val id: Long,
    val type: TransaksiType,
    val amount: Long,
    val note: String,
    val date: String
)

object KasRepository {
    private const val STORAGE_KEY = "transactions"
    var saldo = mutableStateOf(0L)
        private set

    val transaksiList: SnapshotStateList<Transaksi> = mutableStateListOf()

    private var nextId = 1L
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    fun initialize() {
        transaksiList.clear()
        val savedTransactions = LocalDataStore.read(STORAGE_KEY)?.let(::JSONArray) ?: JSONArray()
        for (index in 0 until savedTransactions.length()) {
            val saved = savedTransactions.optJSONObject(index) ?: continue
            val type = runCatching { TransaksiType.valueOf(saved.optString("type")) }.getOrNull() ?: continue
            val amount = saved.optLong("amount")
            if (amount <= 0) continue
            transaksiList += Transaksi(
                id = saved.optLong("id"),
                type = type,
                amount = amount,
                note = saved.optString("note"),
                date = saved.optString("date")
            )
        }
        saldo.value = transaksiList.fold(0L) { total, transaction ->
            if (transaction.type == TransaksiType.MASUK) {
                Math.addExact(total, transaction.amount)
            } else {
                Math.subtractExact(total, transaction.amount)
            }
        }
        nextId = (transaksiList.maxOfOrNull { it.id } ?: 0L) + 1L
    }

    fun tambahKas(amount: Long, note: String): Boolean =
        tambahKas(amount, note, emptyMap())

    internal fun tambahKas(
        amount: Long,
        note: String,
        additionalValues: Map<String, String>
    ): Boolean {
        if (amount <= 0) return false
        val newBalance = runCatching { Math.addExact(saldo.value, amount) }.getOrNull() ?: return false

        val transaction = Transaksi(
            id = nextId++,
            type = TransaksiType.MASUK,
            amount = amount,
            note = note,
            date = dateFormatter.format(Date())
        )
        transaksiList.add(0, transaction)
        saldo.value = newBalance
        if (!persist(additionalValues)) {
            transaksiList.remove(transaction)
            saldo.value -= amount
            nextId--
            return false
        }
        return true
    }

    fun tarikKas(amount: Long, note: String): Boolean {
        if (amount <= 0 || amount > saldo.value) return false
        val newBalance = saldo.value - amount
        val transaction = Transaksi(
            id = nextId++,
            type = TransaksiType.KELUAR,
            amount = amount,
            note = note,
            date = dateFormatter.format(Date())
        )
        transaksiList.add(0, transaction)
        saldo.value = newBalance
        if (!persist()) {
            transaksiList.remove(transaction)
            saldo.value += amount
            nextId--
            return false
        }
        return true
    }

    private fun persist(additionalValues: Map<String, String> = emptyMap()): Boolean {
        val savedTransactions = JSONArray()
        transaksiList.forEach { transaction ->
            savedTransactions.put(
                JSONObject()
                    .put("id", transaction.id)
                    .put("type", transaction.type.name)
                    .put("amount", transaction.amount)
                    .put("note", transaction.note)
                    .put("date", transaction.date)
            )
        }
        return LocalDataStore.writeAll(additionalValues + (STORAGE_KEY to savedTransactions.toString()))
    }
}

fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(amount)}"
}
