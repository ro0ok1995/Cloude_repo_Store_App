package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.ui.SmallStoreApp
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      SmallStoreApp()
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun notifications_screen_screenshot() {
    val viewModel = com.example.viewmodel.StoreViewModel()
    viewModel.navigateTo(com.example.model.NavDestination.NOTIFICATIONS)

    composeTestRule.setContent {
      SmallStoreApp(viewModel = viewModel)
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/notifications.png")
  }

  @Test
  fun accounts_screen_screenshot() {
    val viewModel = com.example.viewmodel.StoreViewModel()
    viewModel.navigateTo(com.example.model.NavDestination.ACCOUNTS)

    composeTestRule.setContent {
      SmallStoreApp(viewModel = viewModel)
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/accounts.png")
  }

  @Test
  fun customer_details_screen_screenshot() {
    val viewModel = com.example.viewmodel.StoreViewModel()
    val customer = viewModel.uiState.value.accounts[1] // خالد بن عبدالعزيز
    viewModel.openCustomerDetails(customer)

    composeTestRule.setContent {
      SmallStoreApp(viewModel = viewModel)
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/customer_details.png")
  }

  @Test
  fun purchases_screen_entry_b_screenshot() {
    val viewModel = com.example.viewmodel.StoreViewModel()
    viewModel.navigateTo(com.example.model.NavDestination.PURCHASES)

    composeTestRule.setContent {
      SmallStoreApp(viewModel = viewModel)
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/purchases_entry_b.png")
  }

  @Test
  fun purchases_screen_with_customer_and_cart_screenshot() {
    val viewModel = com.example.viewmodel.StoreViewModel()
    viewModel.navigateTo(com.example.model.NavDestination.PURCHASES)
    viewModel.selectCustomerForPurchases(viewModel.uiState.value.accounts[1])
    viewModel.addToCart(viewModel.uiState.value.products[0])
    viewModel.addToCart(viewModel.uiState.value.products[1])

    composeTestRule.setContent {
      SmallStoreApp(viewModel = viewModel)
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/purchases_with_cart.png")
  }

  @Test
  fun quick_payment_full_mode_screenshot() {
    val viewModel = com.example.viewmodel.StoreViewModel()
    viewModel.openQuickPayment()

    composeTestRule.setContent {
      SmallStoreApp(viewModel = viewModel)
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/quick_payment_full.png")
  }

  @Test
  fun quick_payment_partial_mode_screenshot() {
    val viewModel = com.example.viewmodel.StoreViewModel()
    viewModel.openQuickPayment()
    viewModel.setQuickPaymentSettlementType(com.example.model.SettlementType.PARTIAL)

    composeTestRule.setContent {
      SmallStoreApp(viewModel = viewModel)
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/quick_payment_partial.png")
  }

  @Test
  fun unified_settlement_sheet_worked_example_screenshot() {
    composeTestRule.setContent {
      com.example.ui.theme.SmallStoreTheme {
        androidx.compose.runtime.CompositionLocalProvider(
          androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
        ) {
          androidx.compose.material3.Surface(
            color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
          ) {
            com.example.ui.components.UnifiedSettlementSheetContent(
              languageMode = com.example.model.LanguageMode.ARABIC,
              transactionTotal = 100.0,
              initialCashAmount = "50",
              initialDebtAmount = "40",
              initialNotes = ""
            )
          }
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/unified_settlement_sheet.png")
  }
}
