package com.arunk.dashboardlogin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arunk.dashboardlogin.ui.theme.BrandRed

@Composable
fun StudentDetailScreen(
    studentId: Long,
    onBack: () -> Unit
) {
    val siswa = StudentRepository.getById(studentId)

    var paymentInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = siswa?.nama ?: "Siswa",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (siswa == null) {
            Text(
                text = "Data siswa tidak ditemukan",
                style = MaterialTheme.typography.bodyLarge
            )
            return@Column
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BrandRed)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sisa Tunggakan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (siswa.owed > 0) formatRupiah(siswa.owed) else "Lunas",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = paymentInput,
            onValueChange = { input -> paymentInput = input.filter { it.isDigit() } },
            label = { Text("Jumlah Bayar (Rp)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandRed,
                focusedLabelColor = BrandRed,
                cursorColor = BrandRed
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (successMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = successMessage,
                color = BrandRed,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val amount = paymentInput.toLongOrNull()
                when {
                    amount == null || amount <= 0 -> {
                        errorMessage = "Masukkan jumlah yang valid"
                        successMessage = ""
                    }
                    else -> {
                        StudentRepository.recordPayment(siswa.id, amount)
                        errorMessage = ""
                        successMessage = "Pembayaran berhasil dicatat"
                        paymentInput = ""
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandRed,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("BAYAR", fontWeight = FontWeight.Bold)
        }

        if (siswa.payments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Riwayat Pembayaran",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            siswa.payments.reversed().forEach { payment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = payment.date,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = formatRupiah(payment.amount),
                            fontWeight = FontWeight.Bold,
                            color = BrandRed
                        )
                    }
                }
            }
        }
    }
}
