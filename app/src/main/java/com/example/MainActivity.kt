package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.BillDetailScreen
import com.example.ui.screens.CustomerWaterConditionScreen
import com.example.ui.screens.BillHistoryScreen
import com.example.ui.screens.BillScreen
import com.example.ui.screens.DistributionDetailScreen
import com.example.ui.screens.DistributionNetworkScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MeterConfirmationScreen
import com.example.ui.screens.MeterResultScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PaymentInstructionScreen
import com.example.ui.screens.PaymentMethodScreen
import com.example.ui.screens.PaymentSuccessScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.ReportMeterScreen
import com.example.ui.screens.ReservoirDetailScreen
import com.example.ui.screens.ScanningMeterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WaterMonitoringDashboardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        SmartWaterApp()
      }
    }
  }
}

@Composable
fun SmartWaterApp(
  modifier: Modifier = Modifier,
  authViewModel: com.example.ui.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
  homeViewModel: com.example.ui.viewmodel.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
  meterViewModel: com.example.ui.viewmodel.MeterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
  billViewModel: com.example.ui.viewmodel.BillViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
  walletViewModel: com.example.ui.viewmodel.WalletViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
  var currentScreen by remember { mutableStateOf("splash") }
  var selectedPaymentMethod by remember { mutableStateOf("QRIS") }
  var selectedDistributionPointId by remember { mutableStateOf("DIST-A") }

  val currentMeterValue by meterViewModel.currentMeterValue.collectAsStateWithLifecycle()
  val scannedMeterValue by meterViewModel.scannedMeterValue.collectAsStateWithLifecycle()
  val activeBill by billViewModel.activeBill.collectAsStateWithLifecycle()
  val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
  val wallet by walletViewModel.wallet.collectAsStateWithLifecycle()
  val xenditState by billViewModel.xenditState.collectAsStateWithLifecycle()
  val isXenditLoading = xenditState is com.example.ui.viewmodel.XenditState.Loading

  // Muat data spesifik user saat user login
  androidx.compose.runtime.LaunchedEffect(currentUser) {
    currentUser?.let { user ->
      com.example.data.repository.MeterRepository.getInstance().loadHistoryForUser(user.uid)
      com.example.data.repository.BillRepository.getInstance().loadBillsForUser(user.uid)
      walletViewModel.createWalletIfNeeded(user.uid, "Operator ${user.fullName.ifBlank { "Smart Water" }}")
      walletViewModel.loadWallet(user.uid)
    }
  }

  val context = androidx.compose.ui.platform.LocalContext.current
  val prefs = remember { context.getSharedPreferences("smart_water_prefs", android.content.Context.MODE_PRIVATE) }

  // Fungsi helper untuk membuka Xendit Checkout via Custom Tab atau Browser
  fun openXenditCheckout(url: String) {
    if (url.isBlank()) return
    try {
      val customTabsIntent = androidx.browser.customtabs.CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
      customTabsIntent.launchUrl(context, android.net.Uri.parse(url))
    } catch (e: Exception) {
      try {
        val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        context.startActivity(browserIntent)
      } catch (err: Exception) {
        android.util.Log.e("MainActivity", "Gagal membuka checkout browser: ${err.message}")
      }
    }
  }

  // Auto-navigate ke payment_success saat pembayaran lunas dari Firestore / Webhook
  androidx.compose.runtime.LaunchedEffect(activeBill?.isPaid) {
    if (activeBill?.isPaid == true && (currentScreen == "payment_instruction" || currentScreen == "payment_method")) {
      currentScreen = "payment_success"
    }
  }

  Crossfade(targetState = currentScreen, label = "screenTransition", modifier = modifier) { screen ->
    when (screen) {
      "splash" -> SplashScreen(
        onNavigateToHome = {
          val fbAuthUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
          val currentAppUser = authViewModel.currentUser.value
          if (fbAuthUser != null || currentAppUser != null) {
            val role = currentAppUser?.role ?: if (fbAuthUser?.email == "admin@smartwater.local") "admin" else "customer"
            currentScreen = if (role == "admin") "admin_dashboard" else "home"
          } else {
            val isFirstTime = !prefs.getBoolean("onboarding_completed", false)
            currentScreen = if (isFirstTime) "onboarding" else "login"
          }
        }
      )
      "onboarding" -> OnboardingScreen(
        onFinish = {
          prefs.edit().putBoolean("onboarding_completed", true).apply()
          currentScreen = "login"
        }
      )
      "login" -> LoginScreen(
        onLoginSuccess = {
          if (authViewModel.currentUser.value?.role == "admin") {
            currentScreen = "admin_dashboard"
          } else {
            currentScreen = "home"
          }
        },
        onNavigateToRegister = { currentScreen = "register" },
        onDemoLogin = { currentScreen = "home" },
        onNavigateToForgotPassword = { currentScreen = "forgot_password" },
        onNavigateToAdmin = { currentScreen = "admin_dashboard" },
        authViewModel = authViewModel
      )
      "forgot_password" -> ForgotPasswordScreen(
        onNavigateBackToLogin = { currentScreen = "login" }
      )
      "register" -> RegisterScreen(
        onRegisterSuccess = { currentScreen = "home" },
        onNavigateToLogin = { currentScreen = "login" },
        authViewModel = authViewModel
      )
      "home" -> HomeScreen(
        onBackToSplash = { currentScreen = "splash" },
        onLogout = {
          authViewModel.logout {
            currentScreen = "login"
          }
        },
        onNavigateToReportMeter = { currentScreen = "report_meter" },
        onNavigateToTagihan = { currentScreen = "bill" },
        onNavigateToNotification = { currentScreen = "notification" },
        onNavigateToBillHistory = { currentScreen = "bill_history" },
        onNavigateToProfile = { currentScreen = "profile" },
        onNavigateToWaterCondition = { currentScreen = "water_condition" },
        meterValue = currentMeterValue
      )
      "report_meter" -> ReportMeterScreen(
        onNavigateBack = { currentScreen = "home" },
        onStartScan = { currentScreen = "scanning_meter" },
        onNavigateToHome = { currentScreen = "home" },
        onNavigateToTagihan = { currentScreen = "bill" },
        onNavigateToNotification = { currentScreen = "notification" },
        onNavigateToProfile = { currentScreen = "profile" },
        currentMeterValue = currentMeterValue
      )
      "scanning_meter" -> ScanningMeterScreen(
        onNavigateBack = { currentScreen = "report_meter" },
        onScanComplete = { photoPath, detectedDigits ->
          meterViewModel.setCapturedPhotoPath(photoPath)
          meterViewModel.updateScannedValue(detectedDigits)
          currentScreen = "meter_result"
        }
      )
      "meter_result" -> MeterResultScreen(
        onNavigateBack = { currentScreen = "report_meter" },
        onRetake = { currentScreen = "scanning_meter" },
        onContinue = { currentScreen = "meter_confirmation" },
        detectedValue = scannedMeterValue,
        previousValue = currentMeterValue
      )
      "meter_confirmation" -> MeterConfirmationScreen(
        onNavigateBack = { currentScreen = "meter_result" },
        detectedValue = scannedMeterValue,
        previousValue = currentMeterValue,
        onSubmitReport = { newMeter ->
          meterViewModel.submitReport(
            meterValue = newMeter,
            previousValue = currentMeterValue,
            onSuccess = {
              currentScreen = "home"
            }
          )
        },
        onPayNow = {
          currentScreen = "bill_detail"
        }
      )
      "bill" -> BillScreen(
        onNavigateToDetail = { currentScreen = "bill_detail" },
        onNavigateToHome = { currentScreen = "home" },
        onNavigateToReport = { currentScreen = "report_meter" },
        onNavigateToNotification = { currentScreen = "notification" },
        onNavigateToHistory = { currentScreen = "bill_history" },
        onNavigateToProfile = { currentScreen = "profile" }
      )
      "bill_history" -> BillHistoryScreen(
        onNavigateToHome = { currentScreen = "home" },
        onNavigateToReport = { currentScreen = "report_meter" },
        onNavigateToNotification = { currentScreen = "notification" },
        onNavigateToBill = { currentScreen = "bill" },
        onNavigateToProfile = { currentScreen = "profile" }
      )
      "notification" -> NotificationScreen(
        onNavigateToHome = { currentScreen = "home" },
        onNavigateToBill = { currentScreen = "bill" },
        onNavigateToReport = { currentScreen = "report_meter" },
        onNavigateToProfile = { currentScreen = "profile" }
      )
      "profile" -> ProfileScreen(
        onNavigateToHome = { currentScreen = "home" },
        onNavigateToBill = { currentScreen = "bill" },
        onNavigateToReport = { currentScreen = "report_meter" },
        onNavigateToNotification = { currentScreen = "notification" },
        onNavigateToSettings = { currentScreen = "settings" },
        onNavigateToWallet = { currentScreen = "wallet" },
        onLogout = {
          authViewModel.logout {
            currentScreen = "login"
          }
        }
      )
      "wallet" -> com.example.ui.screens.WalletScreen(
        currentUser = currentUser,
        wallet = wallet,
        onNavigateBack = { currentScreen = "profile" },
        walletViewModel = walletViewModel,
        onWithdrawalSuccess = { currentScreen = "profile" }
      )
      "settings" -> SettingsScreen(
        onNavigateBack = { currentScreen = "profile" },
        onNavigateToHome = { currentScreen = "home" },
        onNavigateToBill = { currentScreen = "bill" },
        onNavigateToReport = { currentScreen = "report_meter" },
        onNavigateToNotification = { currentScreen = "notification" }
      )
      "bill_detail" -> BillDetailScreen(
        onNavigateBack = { currentScreen = "bill" },
        onProceedToPayment = { currentScreen = "payment_method" }
      )
      "payment_method" -> PaymentMethodScreen(
        onNavigateBack = { currentScreen = "bill_detail" },
        totalAmount = "Rp %,d".format(activeBill?.totalAmount ?: 150800L).replace(',', '.'),
        isLoading = isXenditLoading,
        onProceedToSuccess = { method ->
          selectedPaymentMethod = method
          val billId = activeBill?.billId ?: "BILL-202504-001"
          val custName = currentUser?.fullName?.ifEmpty { "Budi Santoso" } ?: "Budi Santoso"
          val custEmail = currentUser?.email?.ifEmpty { "pelanggan@smartwater.id" } ?: "pelanggan@smartwater.id"
          val custPhone = currentUser?.phoneNumber?.ifEmpty { "+6281234567890" } ?: "+6281234567890"

          if (method.contains("Xendit", ignoreCase = true) || method == "QRIS" || method.contains("Virtual", ignoreCase = true) || method.contains("E-Wallet", ignoreCase = true)) {
            billViewModel.createXenditInvoice(
              billId = billId,
              customerName = custName,
              customerEmail = custEmail,
              customerPhone = custPhone,
              onInvoiceReady = { invoiceUrl ->
                openXenditCheckout(invoiceUrl)
              }
            )
          }
          currentScreen = "payment_instruction"
        }
      )
      "payment_instruction" -> PaymentInstructionScreen(
        onNavigateBack = { currentScreen = "payment_method" },
        paymentMethod = selectedPaymentMethod,
        totalAmount = activeBill?.totalAmount ?: 150800L,
        billId = activeBill?.billId ?: "BILL-202504-001",
        customerName = currentUser?.fullName?.ifEmpty { "Budi Santoso" } ?: "Budi Santoso",
        customerId = currentUser?.customerId?.ifEmpty { "1234567890" } ?: "1234567890",
        invoiceUrl = activeBill?.invoiceUrl.orEmpty(),
        xenditInvoiceId = activeBill?.xenditInvoiceId.orEmpty(),
        isXenditLoading = isXenditLoading,
        onOpenXenditCheckout = {
          val url = activeBill?.invoiceUrl.orEmpty()
          if (url.isNotBlank()) {
            openXenditCheckout(url)
          }
        },
        onCheckPaymentStatus = {
          val billId = activeBill?.billId ?: "BILL-202504-001"
          val invoiceId = activeBill?.xenditInvoiceId.orEmpty()
          if (invoiceId.isNotBlank()) {
            billViewModel.checkXenditInvoiceStatus(billId, invoiceId) {
              currentScreen = "payment_success"
            }
          } else {
            val fixedAmount = activeBill?.totalAmount ?: 150800L
            billViewModel.payBill(billId, selectedPaymentMethod, fixedAmount) {
              currentScreen = "payment_success"
            }
          }
        },
        onSimulateSandbox = {
          val billId = activeBill?.billId ?: "BILL-202504-001"
          val invoiceId = activeBill?.xenditInvoiceId.orEmpty()
          if (invoiceId.isNotBlank()) {
            billViewModel.simulateSandboxPayment(billId, invoiceId) {
              currentScreen = "payment_success"
            }
          } else {
            val fixedAmount = activeBill?.totalAmount ?: 150800L
            billViewModel.payBill(billId, "Xendit Sandbox", fixedAmount) {
              currentScreen = "payment_success"
            }
          }
        },
        onPaymentCompleted = {
          val billId = activeBill?.billId ?: "BILL-202504-001"
          val fixedAmount = activeBill?.totalAmount ?: 150800L
          billViewModel.payBill(billId, selectedPaymentMethod, fixedAmount) {
            currentScreen = "payment_success"
          }
        }
      )
      "payment_success" -> PaymentSuccessScreen(
        onNavigateToHome = { currentScreen = "home" },
        paymentMethod = selectedPaymentMethod
      )
      "admin_dashboard" -> AdminDashboardScreen(
        onNavigateBack = {
          authViewModel.logout {
            currentScreen = "login"
          }
        },
        onNavigateToReservoirDetail = { currentScreen = "reservoir_detail" },
        onNavigateToDistributionNetwork = { currentScreen = "distribution_network" },
        onNavigateToDistributionDetail = { pointId ->
          selectedDistributionPointId = pointId
          currentScreen = "distribution_detail"
        }
      )
      "water_monitoring" -> WaterMonitoringDashboardScreen(
        onNavigateBack = { currentScreen = "admin_dashboard" },
        onNavigateToReservoirDetail = { currentScreen = "reservoir_detail" },
        onNavigateToDistributionNetwork = { currentScreen = "distribution_network" },
        onNavigateToDistributionDetail = { pointId ->
          selectedDistributionPointId = pointId
          currentScreen = "distribution_detail"
        }
      )
      "reservoir_detail" -> ReservoirDetailScreen(
        onNavigateBack = { currentScreen = "admin_dashboard" }
      )
      "distribution_network" -> DistributionNetworkScreen(
        onNavigateBack = { currentScreen = "admin_dashboard" },
        onSelectDistributionPoint = { pointId ->
          selectedDistributionPointId = pointId
          currentScreen = "distribution_detail"
        }
      )
      "distribution_detail" -> DistributionDetailScreen(
        pointId = selectedDistributionPointId,
        onNavigateBack = { currentScreen = "distribution_network" }
      )
      "water_condition" -> CustomerWaterConditionScreen(
        onNavigateBack = { currentScreen = "home" },
        customerZone = currentUser?.zone ?: "DIST-A"
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
  MyApplicationTheme {
    SplashScreen(onNavigateToHome = {})
  }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
  MyApplicationTheme {
    LoginScreen(
      onLoginSuccess = {},
      onNavigateToRegister = {},
      onDemoLogin = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
  MyApplicationTheme {
    RegisterScreen(
      onRegisterSuccess = {},
      onNavigateToLogin = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
  MyApplicationTheme {
    ForgotPasswordScreen(
      onNavigateBackToLogin = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
  MyApplicationTheme {
    HomeScreen(
      onBackToSplash = {},
      onLogout = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun ReportMeterScreenPreview() {
  MyApplicationTheme {
    ReportMeterScreen(
      onNavigateBack = {},
      onStartScan = {},
      onNavigateToHome = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun ScanningMeterScreenPreview() {
  MyApplicationTheme {
    ScanningMeterScreen(
      onNavigateBack = {},
      onScanComplete = { _, _ -> }
    )
  }
}

@Preview(showBackground = true)
@Composable
fun MeterResultScreenPreview() {
  MyApplicationTheme {
    MeterResultScreen(
      onNavigateBack = {},
      onRetake = {},
      onContinue = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun MeterConfirmationScreenPreview() {
  MyApplicationTheme {
    MeterConfirmationScreen(
      onNavigateBack = {},
      onSubmitReport = {},
      onPayNow = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun BillScreenPreview() {
  MyApplicationTheme {
    BillScreen(
      onNavigateToDetail = {},
      onNavigateToHome = {},
      onNavigateToReport = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun BillDetailScreenPreview() {
  MyApplicationTheme {
    BillDetailScreen(
      onNavigateBack = {},
      onProceedToPayment = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PaymentMethodScreenPreview() {
  MyApplicationTheme {
    PaymentMethodScreen(
      onNavigateBack = {},
      onProceedToSuccess = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PaymentSuccessScreenPreview() {
  MyApplicationTheme {
    PaymentSuccessScreen(
      onNavigateToHome = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun BillHistoryScreenPreview() {
  MyApplicationTheme {
    BillHistoryScreen(
      onNavigateToHome = {},
      onNavigateToReport = {},
      onNavigateToNotification = {},
      onNavigateToBill = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
  MyApplicationTheme {
    NotificationScreen(
      onNavigateToHome = {},
      onNavigateToBill = {},
      onNavigateToReport = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
  MyApplicationTheme {
    ProfileScreen(
      onNavigateToHome = {},
      onNavigateToBill = {},
      onNavigateToReport = {},
      onNavigateToNotification = {},
      onNavigateToSettings = {},
      onNavigateToWallet = {},
      onLogout = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
  MyApplicationTheme {
    SettingsScreen(
      onNavigateBack = {},
      onNavigateToHome = {},
      onNavigateToBill = {},
      onNavigateToReport = {},
      onNavigateToNotification = {}
    )
  }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
  MyApplicationTheme {
    OnboardingScreen(
      onFinish = {}
    )
  }
}



