package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.Wallet
import com.example.data.model.WithdrawalRequest
import com.example.ui.viewmodel.WalletViewModel

@Composable
fun WalletScreen(
    currentUser: User?,
    wallet: Wallet?,
    onNavigateBack: () -> Unit,
    walletViewModel: WalletViewModel,
    onWithdrawalSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var amountText by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("BCA") }
    var accountNumber by remember { mutableStateOf("") }
    var accountHolder by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var isSubmitting by remember { mutableStateOf(false) }

    val balance = wallet?.balance ?: 0L
    val formattedBalance = "Rp %,d".format(balance).replace(',', '.')

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Wallet Operator",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = wallet?.merchantName ?: "Operator Smart Water",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Saldo Tersedia",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )
                Text(
                    text = formattedBalance,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Tarik Saldo ke Bank", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nominal (Rp)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Nama Bank") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Nomor Rekening") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = accountHolder,
                    onValueChange = { accountHolder = it },
                    label = { Text("Nama Pemilik Rekening") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val userId = currentUser?.uid ?: return@Button
                        val amount = amountText.toLongOrNull() ?: 0L
                        if (amount <= 0L) {
                            Toast.makeText(context, "Masukkan nominal penarikan", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (amount > (wallet?.balance ?: 0L)) {
                            Toast.makeText(context, "Saldo tidak mencukupi", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSubmitting = true
                        walletViewModel.requestWithdrawal(
                            userId = userId,
                            amount = amount,
                            bankName = bankName,
                            accountNumber = accountNumber,
                            accountHolder = accountHolder,
                            onSuccess = {
                                isSubmitting = false
                                amountText = ""
                                Toast.makeText(context, "Permintaan tarik saldo terkirim", Toast.LENGTH_SHORT).show()
                                onWithdrawalSuccess()
                            }
                        )
                    },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text("Ajukan Penarikan")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Riwayat Penarikan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        val requests by walletViewModel.withdrawalRequests.collectAsStateWithLifecycle()
        if (requests.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Belum ada riwayat penarikan",
                    modifier = Modifier.padding(18.dp),
                    color = Color(0xFF64748B)
                )
            }
        } else {
            requests.forEach { request ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Rp ${request.amount}", fontWeight = FontWeight.Bold)
                            Text("${request.bankName} • ${request.accountNumber}", color = Color(0xFF64748B))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (request.status == "pending") Icons.Default.ArrowUpward else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (request.status == "pending") Color(0xFFF59E0B) else Color(0xFF16A34A)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(request.status.uppercase(), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
