package com.arunk.dashboardlogin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    var saldo = mutableStateOf(0L)
        private set

    val transaksiList: SnapshotStateList<Transaksi> = mutableStateListOf()

    private var nextId = 1L
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    fun tambahKas(amount: Long, note: String) {
        if (amount <= 0) return

        saldo.value += amount
        transaksiList.add(
            0,
            Transaksi(
                id = nextId++,
                type = TransaksiType.MASUK,
                amount = amount,
                note = note,
                date = dateFormatter.format(Date())
            )
        )
    }

    fun tarikKas(amount: Long, note: String): Boolean {
        if (amount <= 0 || amount > saldo.value) return false

        saldo.value -= amount
        transaksiList.add(
            0,
            Transaksi(
                id = nextId++,
                type = TransaksiType.KELUAR,
                amount = amount,
                note = note,
                date = dateFormatter.format(Date())
            )
        )
        return true
    }
}

fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(amount)}"
}
