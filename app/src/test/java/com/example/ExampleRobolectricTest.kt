package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.BillDetailScreen
import com.example.ui.screens.BillScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MeterConfirmationScreen
import com.example.ui.screens.MeterResultScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PaymentMethodScreen
import com.example.ui.screens.PaymentSuccessScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.ReportMeterScreen
import com.example.ui.screens.ScanningMeterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Smart Water", appName)
  }

  @Test
  fun `login screen renders essential elements`() {
    var demoClicked = false
    composeTestRule.setContent {
      MyApplicationTheme {
        LoginScreen(
          onLoginSuccess = {},
          onNavigateToRegister = {},
          onDemoLogin = { demoClicked = true }
        )
      }
    }

    composeTestRule.onNodeWithTag("login_screen_container").assertExists()
    composeTestRule.onNodeWithTag("login_title").assertExists()
    composeTestRule.onNodeWithTag("login_input_customer_id").assertExists()
    composeTestRule.onNodeWithTag("login_input_pin").assertExists()
    composeTestRule.onNodeWithTag("login_button").assertExists()
    composeTestRule.onNodeWithTag("demo_login_button").assertExists()

    composeTestRule.onNodeWithTag("demo_login_button").performScrollTo().performClick()
    assertTrue(demoClicked)
  }

  @Test
  fun `register screen renders required fields`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        RegisterScreen(
          onRegisterSuccess = {},
          onNavigateToLogin = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("register_screen_container").assertExists()
    composeTestRule.onNodeWithTag("register_title").assertExists()
    composeTestRule.onNodeWithTag("register_input_name").assertExists()
    composeTestRule.onNodeWithTag("register_input_meter").assertExists()
    composeTestRule.onNodeWithTag("register_input_phone").assertExists()
    composeTestRule.onNodeWithTag("register_input_address").assertExists()
    composeTestRule.onNodeWithTag("register_input_pin").assertExists()
    composeTestRule.onNodeWithTag("register_submit_button").assertExists()
  }

  @Test
  fun `forgot password screen renders steps and phone input`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        ForgotPasswordScreen(
          onNavigateBackToLogin = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("forgot_password_screen_container").assertExists()
    composeTestRule.onNodeWithTag("forgot_password_title").assertExists()
    composeTestRule.onNodeWithTag("forgot_password_input_phone").assertExists()
    composeTestRule.onNodeWithTag("forgot_password_submit_button").assertExists()
    composeTestRule.onNodeWithTag("ulang_survey_link").assertExists()
  }

  @Test
  fun `dashboard renders user info and quick actions`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        HomeScreen(
          onBackToSplash = {},
          onLogout = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("dashboard_screen_container").assertExists()
    composeTestRule.onNodeWithTag("user_name_text").assertExists()
    composeTestRule.onNodeWithTag("customer_id_text").assertExists()
    composeTestRule.onNodeWithTag("meter_floating_card").assertExists()
    composeTestRule.onNodeWithTag("meter_counter_text").assertExists()
    composeTestRule.onNodeWithTag("tagihan_aktif_text").assertExists()
    composeTestRule.onNodeWithTag("quick_actions_title").assertExists()
    composeTestRule.onNodeWithTag("action_laporkan_meter").assertExists()
    composeTestRule.onNodeWithTag("action_tagihan").assertExists()
    composeTestRule.onNodeWithTag("action_bayar").assertExists()
    composeTestRule.onNodeWithTag("action_riwayat").assertExists()
    composeTestRule.onNodeWithTag("nav_beranda").assertExists()
    composeTestRule.onNodeWithTag("nav_tagihan").assertExists()
    composeTestRule.onNodeWithTag("nav_laporkan").assertExists()
    composeTestRule.onNodeWithTag("nav_notifikasi").assertExists()
    composeTestRule.onNodeWithTag("nav_profil").assertExists()
  }

  @Test
  fun `report meter screen renders viewfinder and meter info`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        ReportMeterScreen(
          onNavigateBack = {},
          onStartScan = {},
          onNavigateToHome = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("report_meter_screen_container").assertExists()
    composeTestRule.onNodeWithTag("report_meter_title").assertExists()
    composeTestRule.onNodeWithTag("report_meter_back_button").assertExists()
    composeTestRule.onNodeWithTag("flash_toggle_button").assertExists()
    composeTestRule.onNodeWithTag("camera_viewfinder_box").assertExists()
    composeTestRule.onNodeWithTag("meter_info_card").assertExists()
    composeTestRule.onNodeWithTag("meter_number_digits").assertExists()
    composeTestRule.onNodeWithTag("report_meter_tips_card").assertExists()
    composeTestRule.onNodeWithTag("laporkan_sekarang_button").assertExists()
  }

  @Test
  fun `scanning meter screen renders laser viewfinder and guide`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        ScanningMeterScreen(
          onNavigateBack = {},
          onScanComplete = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("scanning_meter_screen_container").assertExists()
    composeTestRule.onNodeWithTag("scanning_title").assertExists()
    composeTestRule.onNodeWithTag("scanning_viewfinder_box").assertExists()
    composeTestRule.onNodeWithTag("menganalisis_text", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("memproses_gambar_text").assertExists()
    composeTestRule.onNodeWithTag("panduan_card").assertExists()
  }

  @Test
  fun `meter result screen renders detected value and tarif info`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        MeterResultScreen(
          onNavigateBack = {},
          onRetake = {},
          onContinue = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("meter_result_screen_container").assertExists()
    composeTestRule.onNodeWithTag("result_title").assertExists()
    composeTestRule.onNodeWithTag("angka_terdeteksi_card").assertExists()
    composeTestRule.onNodeWithTag("detected_value_text").assertExists()
    composeTestRule.onNodeWithTag("accuracy_badge").assertExists()
    composeTestRule.onNodeWithTag("informasi_tarif_card").assertExists()
    composeTestRule.onNodeWithTag("ambil_ulang_button").assertExists()
    composeTestRule.onNodeWithTag("lanjutkan_button").assertExists()
  }

  @Test
  fun `meter confirmation screen renders usage details and submit action`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        MeterConfirmationScreen(
          onNavigateBack = {},
          onSubmitReport = {},
          onPayNow = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("meter_confirmation_screen_container").assertExists()
    composeTestRule.onNodeWithTag("confirmation_title").assertExists()
    composeTestRule.onNodeWithTag("rincian_pemakaian_card").assertExists()
    composeTestRule.onNodeWithTag("digital_counter_01358").assertExists()
    composeTestRule.onNodeWithTag("pemakaian_highlight_text").assertExists()
    composeTestRule.onNodeWithTag("estimasi_tagihan_card").assertExists()
    composeTestRule.onNodeWithTag("kirim_laporan_button").assertExists()
    composeTestRule.onNodeWithTag("bayar_sekarang_button").assertExists()
  }

  @Test
  fun `bill screen renders active bill and allows navigation to detail`() {
    var navigatedToDetail = false
    composeTestRule.setContent {
      MyApplicationTheme {
        BillScreen(
          onNavigateToDetail = { navigatedToDetail = true },
          onNavigateToHome = {},
          onNavigateToReport = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("bill_screen_container").assertExists()
    composeTestRule.onNodeWithTag("bill_header_title").assertExists()
    composeTestRule.onNodeWithTag("tab_tagihan_aktif").assertExists()
    composeTestRule.onNodeWithTag("tab_riwayat").assertExists()
    composeTestRule.onNodeWithTag("active_bill_card").assertExists()
    composeTestRule.onNodeWithTag("bill_amount_text").assertExists()
    composeTestRule.onNodeWithTag("unpaid_badge").assertExists()
    composeTestRule.onNodeWithTag("bill_total_highlight").assertExists()
    composeTestRule.onNodeWithTag("bayar_sekarang_main_button").assertExists()

    composeTestRule.onNodeWithTag("bayar_sekarang_main_button").performClick()
    assertTrue(navigatedToDetail)
  }

  @Test
  fun `bill detail screen renders breakdown and usage info`() {
    var proceedToPayment = false
    composeTestRule.setContent {
      MyApplicationTheme {
        BillDetailScreen(
          onNavigateBack = {},
          onProceedToPayment = { proceedToPayment = true }
        )
      }
    }

    composeTestRule.onNodeWithTag("bill_detail_screen_container").assertExists()
    composeTestRule.onNodeWithTag("bill_detail_title").assertExists()
    composeTestRule.onNodeWithTag("detail_total_amount").assertExists()
    composeTestRule.onNodeWithTag("detail_unpaid_badge").assertExists()
    composeTestRule.onNodeWithTag("rincian_tagihan_card").assertExists()
    composeTestRule.onNodeWithTag("detail_rincian_total").assertExists()
    composeTestRule.onNodeWithTag("info_pemakaian_card").assertExists()
    composeTestRule.onNodeWithTag("detail_pemakaian_value").assertExists()
    composeTestRule.onNodeWithTag("detail_bayar_sekarang_button").assertExists()
    composeTestRule.onNodeWithTag("detail_lanjutkan_button").assertExists()

    composeTestRule.onNodeWithTag("detail_lanjutkan_button").performClick()
    assertTrue(proceedToPayment)
  }

  @Test
  fun `payment method screen allows selection and continuation`() {
    var selectedResult = ""
    composeTestRule.setContent {
      MyApplicationTheme {
        PaymentMethodScreen(
          onNavigateBack = {},
          onProceedToSuccess = { method -> selectedResult = method }
        )
      }
    }

    composeTestRule.onNodeWithTag("payment_method_screen_container").assertExists()
    composeTestRule.onNodeWithTag("payment_method_title").assertExists()
    composeTestRule.onNodeWithTag("total_tagihan_blue_card").assertExists()
    composeTestRule.onNodeWithTag("blue_card_amount").assertExists()
    composeTestRule.onNodeWithTag("payment_option_qris").assertExists()
    composeTestRule.onNodeWithTag("payment_option_transfer_bank").assertExists()
    composeTestRule.onNodeWithTag("payment_option_e-wallet").assertExists()
    composeTestRule.onNodeWithTag("payment_option_virtual_account").assertExists()
    composeTestRule.onNodeWithTag("payment_lanjutkan_button").assertExists()

    composeTestRule.onNodeWithTag("payment_lanjutkan_button").performClick()
    assertEquals("QRIS", selectedResult)
  }

  @Test
  fun `payment success screen renders confirmation and buttons`() {
    var homeClicked = false
    composeTestRule.setContent {
      MyApplicationTheme {
        PaymentSuccessScreen(
          onNavigateToHome = { homeClicked = true },
          paymentMethod = "QRIS"
        )
      }
    }

    composeTestRule.onNodeWithTag("payment_success_screen_container").assertExists()
    composeTestRule.onNodeWithTag("payment_success_icon").assertExists()
    composeTestRule.onNodeWithTag("payment_success_title").assertExists()
    composeTestRule.onNodeWithTag("payment_success_amount").assertExists()
    composeTestRule.onNodeWithTag("payment_receipt_card").assertExists()
    composeTestRule.onNodeWithTag("lihat_resi_button").assertExists()
    composeTestRule.onNodeWithTag("kembali_ke_beranda_button").assertExists()

    composeTestRule.onNodeWithTag("kembali_ke_beranda_button").performClick()
    assertTrue(homeClicked)
  }

  @Test
  fun `profile screen renders info and actions`() {
    var settingsClicked = false
    composeTestRule.setContent {
      MyApplicationTheme {
        ProfileScreen(
          onNavigateToHome = {},
          onNavigateToBill = {},
          onNavigateToReport = {},
          onNavigateToNotification = {},
          onNavigateToSettings = { settingsClicked = true },
          onLogout = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("profile_item_phone").assertExists()
    composeTestRule.onNodeWithTag("profile_item_meter").assertExists()
    composeTestRule.onNodeWithTag("profile_item_tariff").assertExists()
    composeTestRule.onNodeWithTag("profile_item_address").assertExists()
    composeTestRule.onNodeWithTag("profile_item_email").assertExists()
    composeTestRule.onNodeWithTag("profile_item_settings").assertExists()
    composeTestRule.onNodeWithTag("profile_item_settings").performClick()
    assertTrue(settingsClicked)
  }

  @Test
  fun `settings screen renders all sections`() {
    var backClicked = false
    composeTestRule.setContent {
      MyApplicationTheme {
        SettingsScreen(
          onNavigateBack = { backClicked = true },
          onNavigateToHome = {},
          onNavigateToBill = {},
          onNavigateToReport = {},
          onNavigateToNotification = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("settings_back_button").assertExists()
    composeTestRule.onNodeWithTag("settings_item_notif").assertExists()
    composeTestRule.onNodeWithTag("settings_item_security").assertExists()
    composeTestRule.onNodeWithTag("settings_item_meter").assertExists()
    composeTestRule.onNodeWithTag("settings_item_tariff").assertExists()
    composeTestRule.onNodeWithTag("settings_item_faq").assertExists()
    composeTestRule.onNodeWithTag("settings_item_about").assertExists()

    composeTestRule.onNodeWithTag("settings_back_button").performClick()
    assertTrue(backClicked)
  }

  @Test
  fun `splash screen renders brand and logo`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        SplashScreen(onNavigateToHome = {})
      }
    }

    composeTestRule.onNodeWithTag("splash_screen_container").assertExists()
    composeTestRule.onNodeWithTag("splash_water_logo", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("smart_text_brand", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("water_text_brand", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("smart_water_subtitle", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("splash_tagline", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("splash_wave_bottom", useUnmergedTree = true).assertExists()
  }

  @Test
  fun `onboarding screen renders pages and skip button`() {
    var finished = false
    composeTestRule.setContent {
      MyApplicationTheme {
        OnboardingScreen(onFinish = { finished = true })
      }
    }

    composeTestRule.onNodeWithTag("onboarding_screen_container").assertExists()
    composeTestRule.onNodeWithTag("onboarding_skip_button").assertExists()
    composeTestRule.onNodeWithTag("onboarding_action_button").assertExists()
    composeTestRule.onNodeWithTag("onboarding_skip_button").performClick()
    assertTrue(finished)
  }
}

