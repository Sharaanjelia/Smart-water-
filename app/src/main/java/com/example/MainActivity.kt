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
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.BillDetailScreen
import com.example.ui.screens.BillHistoryScreen
import com.example.ui.screens.BillScreen
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
import com.example.ui.screens.ScanningMeterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
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
  billViewModel: com.example.ui.viewmodel.BillViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
  var currentScreen by remember { mutableStateOf("splash") }
  var selectedPaymentMethod by remember { mutableStateOf("QRIS") }

  val currentMeterValue by meterViewModel.currentMeterValue.androidx.lifecycle.compose.collectAsStateWithLifecycle()
  val scannedMeterValue by meterViewModel.scannedMeterValue.androidx.lifecycle.compose.collectAsStateWithLifecycle()
  val activeBill by billViewModel.activeBill.androidx.lifecycle.compose.collectAsStateWithLifecycle()
  val currentUser by authViewModel.currentUser.androidx.lifecycle.compose.collectAsStateWithLifecycle()

  Crossfade(targetState = currentScreen, label = "screenTransition", modifier = modifier) { screen ->
    when (screen) {
      "splash" -> SplashScreen(
        onNavigateToHome = { currentScreen = "onboarding" }
      )
      "onboarding" -> OnboardingScreen(
        onFinish = { currentScreen = "login" }
      )
      "login" -> LoginScreen(
        onLoginSuccess = { currentScreen = "home" },
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
        onScanComplete = {
          meterViewModel.performScan()
          currentScreen = "meter_result"
        }
      )
      "meter_result" -> MeterResultScreen(
        onNavigateBack = { currentScreen = "report_meter" },
        onRetake = { currentScreen = "scanning_meter" },
        onContinue = { currentScreen = "meter_confirmation" },
        detectedValue = scannedMeterValue
      )
      "meter_confirmation" -> MeterConfirmationScreen(
        onNavigateBack = { currentScreen = "meter_result" },
        detectedValue = scannedMeterValue,
        previousValue = currentMeterValue,
        onSubmitReport = { newMeter ->
          meterViewModel.submitReport(newMeter) {
            currentScreen = "home"
          }
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
        onLogout = {
          authViewModel.logout {
            currentScreen = "login"
          }
        }
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
        onProceedToSuccess = { method ->
          selectedPaymentMethod = method
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
        onPaymentCompleted = {
          val billId = activeBill?.billId ?: "BILL-202504-001"
          billViewModel.payBill(billId, selectedPaymentMethod) {
            currentScreen = "payment_success"
          }
        }
      )
      "payment_success" -> PaymentSuccessScreen(
        onNavigateToHome = { currentScreen = "home" },
        paymentMethod = selectedPaymentMethod
      )
      "admin_dashboard" -> AdminDashboardScreen(
        onNavigateBack = { currentScreen = "login" }
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
      onScanComplete = {}
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



