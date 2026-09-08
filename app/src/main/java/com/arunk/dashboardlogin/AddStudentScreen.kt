package com.arunk.dashboardlogin

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
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
fun AddStudentScreen(onBack: () -> Unit) {
    var namaInput by remember { mutableStateOf("") }
    var owedInput by remember { mutableStateOf("") }
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
                text = "TAMBAH SISWA",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = namaInput,
            onValueChange = { namaInput = it },
            label = { Text("Nama Siswa") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandRed,
                focusedLabelColor = BrandRed,
                cursorColor = BrandRed
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = owedInput,
            onValueChange = { input -> owedInput = input.filter { it.isDigit() } },
            label = { Text("Jumlah Tunggakan (Rp)") },
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
                val owed = owedInput.toLongOrNull()
                when {
                    namaInput.isBlank() -> {
                        errorMessage = "Nama tidak boleh kosong"
                        successMessage = ""
                    }
                    owed == null || owed < 0 -> {
                        errorMessage = "Masukkan jumlah yang valid"
                        successMessage = ""
                    }
                    else -> {
                        StudentRepository.addSiswa(namaInput.trim(), owed)
                        namaInput = ""
                        owedInput = ""
                        errorMessage = ""
                        successMessage = "Siswa berhasil ditambahkan"
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
            Text("TAMBAH SISWA", fontWeight = FontWeight.Bold)
        }
    }
}
