package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Waves
import com.example.data.repository.WaterMonitoringRepository
import com.example.ui.components.ReservoirTankVisual
import com.example.ui.components.WaterFlowPipelineVisual
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Bill
import com.example.data.model.MeterReading
import com.example.data.repository.BillRepository
import com.example.data.repository.MeterRepository
import com.example.ui.theme.SmartWaterPrimary

/**
 * Layar Dashboard Petugas / Admin PDAM (Mobile Officer Mode)
 * Digunakan oleh staf/petugas PDAM untuk:
 * 1. Verifikasi laporan pembacaan meter mandiri pelanggan
 * 2. Monitoring tagihan & pembayaran pelanggan
 * 3. Manajemen data pelanggan terdaftar
 */
@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReservoirDetail: () -> Unit = {},
    onNavigateToDistributionNetwork: () -> Unit = {},
    onNavigateToDistributionDetail: (String) -> Unit = {},
    authRepository: com.example.data.repository.AuthRepository = com.example.data.repository.AuthRepository.getInstance(),
    meterRepository: MeterRepository = MeterRepository.getInstance(),
    billRepository: BillRepository = BillRepository.getInstance(),
    walletRepository: com.example.data.repository.WalletRepository = com.example.data.repository.WalletRepository.getInstance(),
    waterRepository: WaterMonitoringRepository = WaterMonitoringRepository.getInstance(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }

    // Mulai listener Firestore real-time saat Admin Dashboard dibuka
    androidx.compose.runtime.LaunchedEffect(Unit) {
        authRepository.observeAllCustomers()
        meterRepository.observeAllMeterRecords()
        billRepository.observeAllBills()
        walletRepository.loadAllWithdrawalRequests()
    }

    val meterRecords by meterRepository.allMeterRecords.collectAsStateWithLifecycle()
    val billsList by billRepository.allBills.collectAsStateWithLifecycle()
    val customersList by authRepository.allCustomers.collectAsStateWithLifecycle()
    val withdrawalsList by walletRepository.allWithdrawalRequests.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("admin_dashboard_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Portal Petugas PDAM",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Petugas: Ahmad Pratama (ID #04)",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Cloud Online Status Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF16A34A).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Cloud Live",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4ADE80)
                        )
                    }
                }
            }

            // Tab Navigation (4 Tabs)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = SmartWaterPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SmartWaterPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Laporan Meter", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Tagihan", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Pelanggan", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Monitoring Air", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                    icon = { Icon(Icons.Default.Opacity, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    0 -> VerificationTabContent(
                        readings = meterRecords,
                        onApprove = { id ->
                            coroutineScope.launch {
                                meterRepository.updateRecordStatus(id, "TERVERIFIKASI")
                            }
                            Toast.makeText(context, "Laporan meter disetujui & diverifikasi!", Toast.LENGTH_SHORT).show()
                        },
                        onReject = { id ->
                            coroutineScope.launch {
                                meterRepository.updateRecordStatus(id, "DITOLAK")
                            }
                            Toast.makeText(context, "Laporan meter ditolak", Toast.LENGTH_SHORT).show()
                        }
                    )
                    1 -> BillingTabContent(
                        bills = billsList,
                        withdrawals = withdrawalsList,
                        onApproveWithdrawal = { id ->
                            coroutineScope.launch {
                                walletRepository.updateWithdrawalStatus(id, "approved", "Disetujui petugas PDAM")
                            }
                            Toast.makeText(context, "Penarikan disetujui", Toast.LENGTH_SHORT).show()
                        },
                        onRejectWithdrawal = { id ->
                            coroutineScope.launch {
                                walletRepository.updateWithdrawalStatus(id, "rejected", "Ditolak petugas PDAM")
                            }
                            Toast.makeText(context, "Penarikan ditolak", Toast.LENGTH_SHORT).show()
                        }
                    )
                    2 -> CustomerTabContent(customers = customersList)
                    3 -> MonitoringTabContent(
                        waterRepository = waterRepository,
                        onNavigateToReservoirDetail = onNavigateToReservoirDetail,
                        onNavigateToDistributionNetwork = onNavigateToDistributionNetwork,
                        onNavigateToDistributionDetail = onNavigateToDistributionDetail
                    )
                }
            }
        }
    }
}

@Composable
private fun VerificationTabContent(
    readings: List<MeterReading>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Laporan Masuk Hari Ini",
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF)
                        )
                        Text(
                            text = "${readings.size} Laporan",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8)
                        )
                    }
                    Text(
                        text = "Periode Aktif: April 2025",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3B82F6)
                    )
                }
            }
        }

        items(readings) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pelanggan ID: ${item.customerId}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "No. Meter: ${item.effectiveMeterId} • Zona: ${item.zone}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Lalu: ${item.previousReading.toInt()} m³ ➔ Kini: ${item.currentReading.toInt()} m³",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SmartWaterPrimary
                            )
                        }

                        // Badge Status
                        val (bgCol, txtCol) = when (item.status) {
                            "TERVERIFIKASI" -> Color(0xFFDCFCE7) to Color(0xFF16A34A)
                            "DITOLAK" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
                            else -> Color(0xFFFEF9C3) to Color(0xFFCA8A04)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgCol)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = item.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = txtCol
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reading Value Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Angka Dilaporkan", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = "${item.meterValue} m³",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Volume Pemakaian", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = "+${item.meterDeltaM3} m³",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons (Setujui / Tolak)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onReject(item.readingId) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tolak", fontSize = 13.sp)
                        }

                        Button(
                            onClick = { onApprove(item.readingId) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Setujui", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BillingTabContent(
    bills: List<Bill>,
    withdrawals: List<com.example.data.model.WithdrawalRequest> = emptyList(),
    onApproveWithdrawal: (String) -> Unit = {},
    onRejectWithdrawal: (String) -> Unit = {}
) {
    val totalPaid = bills.filter { it.status == "LUNAS" }.sumOf { it.totalAmount }
    val totalUnpaid = bills.filter { it.status != "LUNAS" }.sumOf { it.totalAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Dual Finance Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Terbayar
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Pendapatan Lunas", fontSize = 11.sp, color = Color(0xFF166534))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp %,d".format(totalPaid).replace(',', '.'),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                // Menunggak
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Belum Terbayar", fontSize = 11.sp, color = Color(0xFF991B1B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp %,d".format(totalUnpaid).replace(',', '.'),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }

        item {
            if (withdrawals.isNotEmpty()) {
                Text(
                    text = "Permintaan Penarikan Saldo",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(withdrawals.filter { it.status == "pending" }) { withdrawal ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Penarikan ke ${withdrawal.bankName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "${withdrawal.accountHolder} • ${withdrawal.accountNumber}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Nominal: Rp %,d".format(withdrawal.amount).replace(',', '.'),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onRejectWithdrawal(withdrawal.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                        ) {
                            Text("Tolak")
                        }
                        Button(
                            onClick = { onApproveWithdrawal(withdrawal.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                        ) {
                            Text("Setujui")
                        }
                    }
                }
            }
        }

        items(bills) { bill ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tagihan ${bill.period}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "ID Pelanggan: ${bill.customerId} • ${bill.usageM3} m³",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        if (bill.paymentMethod.isNotBlank()) {
                            Text(
                                text = "Via: ${bill.paymentMethod}",
                                fontSize = 10.sp,
                                color = Color(0xFF0284C7)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Rp %,d".format(bill.totalAmount).replace(',', '.'),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val isLunas = bill.status == "LUNAS"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isLunas) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isLunas) "LUNAS" else "BELUM BAYAR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLunas) Color(0xFF16A34A) else Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerTabContent(customers: List<com.example.data.model.User>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Pelanggan Terdaftar: ${customers.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
                Text(
                    text = "Cloud Live Sync",
                    fontSize = 10.sp,
                    color = Color(0xFF16A34A),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (customers.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada pelanggan terdaftar di Firestore.",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        items(customers) { user ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.fullName.ifEmpty { "Pelanggan Smart Water" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ID: ${user.customerId} • No. Meter: ${user.meterNumber.ifEmpty { user.customerId }}",
                            fontSize = 11.sp,
                            color = SmartWaterPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Zona: ${user.zone} • ${user.phoneNumber.ifEmpty { user.email }}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        if (user.address.isNotBlank()) {
                            Text(
                                text = user.address,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Aktif", fontSize = 10.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MonitoringTabContent(
    waterRepository: WaterMonitoringRepository,
    onNavigateToReservoirDetail: () -> Unit,
    onNavigateToDistributionNetwork: () -> Unit,
    onNavigateToDistributionDetail: (String) -> Unit
) {
    val reservoir by waterRepository.reservoir.collectAsStateWithLifecycle()
    val distributionPoints by waterRepository.distributionPoints.collectAsStateWithLifecycle()
    val alerts by waterRepository.alerts.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Notice Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF6FF))
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = SmartWaterPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Data Monitoring • Data Simulasi",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = "Monitoring kondisi level reservoir dan jaringan distribusi air PDAM.",
                        fontSize = 10.5.sp,
                        color = Color(0xFF3B82F6)
                    )
                }
            }
        }

        // 4 KPI Summary Cards (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminKpiCard(
                        title = "Level Reservoir",
                        value = "${reservoir.percentage}%",
                        unit = "Kapasitas",
                        info = "7.800 / 10.000 m³",
                        icon = Icons.Default.Opacity,
                        iconBg = Color(0xFFE0F2FE),
                        iconTint = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReservoirDetail
                    )
                    val firstFlow = distributionPoints.firstOrNull()?.flowRateLps ?: 12.5
                    AdminKpiCard(
                        title = "Debit Air",
                        value = "$firstFlow",
                        unit = "L/s",
                        info = "Distribusi A (Utama)",
                        icon = Icons.Default.Waves,
                        iconBg = Color(0xFFDCFCE7),
                        iconTint = Color(0xFF16A34A),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToDistributionDetail("DIST-A") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val firstPressure = distributionPoints.firstOrNull()?.pressureBar ?: 2.8
                    AdminKpiCard(
                        title = "Tekanan Air",
                        value = "$firstPressure",
                        unit = "bar",
                        info = "Jaringan Distribusi A",
                        icon = Icons.Default.Speed,
                        iconBg = Color(0xFFF3E8FF),
                        iconTint = Color(0xFF9333EA),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDistributionNetwork
                    )
                    AdminKpiCard(
                        title = "Status Reservoir",
                        value = reservoir.status,
                        unit = "",
                        info = "Inflow ${reservoir.inflowRateLps} L/s",
                        icon = Icons.Default.Factory,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReservoirDetail
                    )
                }
            }
        }

        // Reservoir Tank Visual Card
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Visualisasi Reservoir Air",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Detail Reservoir ➔",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SmartWaterPrimary,
                        modifier = Modifier.clickable { onNavigateToReservoirDetail() }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ReservoirTankVisual(reservoir = reservoir)
            }
        }

        // Water Flow Pipeline Visual
        item {
            WaterFlowPipelineVisual(
                reservoir = reservoir,
                distributionPoints = distributionPoints,
                onSelectDistributionPoint = onNavigateToDistributionDetail
            )
        }

        // Monitoring Alert Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CrisisAlert,
                                contentDescription = null,
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Monitoring Alert",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Text(
                            text = "${alerts.size} Peringatan",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        alerts.forEach { alert ->
                            val isWarn = alert.type == "WARNING"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isWarn) Color(0xFFFFFBEB) else Color(0xFFF0FDF4))
                                    .border(
                                        1.dp,
                                        if (isWarn) Color(0xFFFDE68A) else Color(0xFFBBF7D0),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = if (isWarn) Icons.Default.Warning else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isWarn) Color(0xFFD97706) else Color(0xFF16A34A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = alert.title,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isWarn) Color(0xFF92400E) else Color(0xFF166534)
                                    )
                                    Text(
                                        text = alert.message,
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Link to Distribution Network
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDistributionNetwork() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Jaringan Distribusi Air",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Tekanan & debit 3 titik (Distribusi A, B, C)",
                            fontSize = 10.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminKpiCard(
    title: String,
    value: String,
    unit: String,
    info: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .shadow(1.dp, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 10.5.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = unit, fontSize = 10.sp, color = Color(0xFF64748B))
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = info, fontSize = 9.5.sp, color = Color(0xFF94A3B8))
        }
    }
}
