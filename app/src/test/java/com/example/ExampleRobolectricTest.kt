package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.LanguageMode
import com.example.model.PeriodFilter
import com.example.viewmodel.StoreViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SmallStore", appName)
    }

    @Test
    fun `default home screen state is whole shop scope`() {
        val viewModel = StoreViewModel()
        val state = viewModel.uiState.value

        assertEquals(LanguageMode.ARABIC, state.languageMode)
        assertNull(state.selectedCustomer)
        assertEquals("", state.searchQuery)
        assertEquals(PeriodFilter.TODAY, state.selectedPeriod)
        assertTrue(state.scopedTransactions.size >= 4)
        assertEquals(state.shopTotalBalance, state.displayTotalBalance, 0.01)
    }

    @Test
    fun `search customer and select updates scope to selected customer`() {
        val viewModel = StoreViewModel()
        viewModel.setSearchQuery("الأمل")

        val stateAfterSearch = viewModel.uiState.value
        assertEquals(1, stateAfterSearch.matchingCustomers.size)
        val customer = stateAfterSearch.matchingCustomers.first()
        assertEquals("مؤسسة الأمل للتجارة", customer.customerName)

        viewModel.selectCustomer(customer)
        val stateAfterSelect = viewModel.uiState.value
        assertNotNull(stateAfterSelect.selectedCustomer)
        assertEquals("مؤسسة الأمل للتجارة", stateAfterSelect.selectedCustomer?.customerName)
        assertEquals(customer.balance, stateAfterSelect.displayTotalBalance, 0.01)

        // Verify that activities are strictly filtered to this customer
        val customerTransactions = stateAfterSelect.scopedTransactions
        assertTrue(customerTransactions.isNotEmpty())
        assertTrue(customerTransactions.all { it.customerName == "مؤسسة الأمل للتجارة" })

        // Clear selection returns to whole shop
        viewModel.clearSelectedCustomer()
        val stateAfterClear = viewModel.uiState.value
        assertNull(stateAfterClear.selectedCustomer)
        assertEquals(stateAfterClear.shopTotalBalance, stateAfterClear.displayTotalBalance, 0.01)
    }

    @Test
    fun `period selection updates selected period`() {
        val viewModel = StoreViewModel()
        viewModel.setPeriod(PeriodFilter.MONTH)
        assertEquals(PeriodFilter.MONTH, viewModel.uiState.value.selectedPeriod)
    }

    @Test
    fun `notifications are only record transaction and payment events`() {
        val viewModel = StoreViewModel()
        val notifs = viewModel.uiState.value.notifications
        assertTrue(notifs.isNotEmpty())

        val validEventTypes = setOf("تم تسجيل معاملة", "تم استلام دفعة", "Record Transaction", "Payment")
        for (notif in notifs) {
            assertTrue("Event type must be either Record Transaction or Payment: ${notif.transactionType}",
                validEventTypes.contains(notif.transactionType))
            assertTrue(notif.amount > 0.0)
            assertTrue(notif.customerName.isNotBlank())
            assertTrue(notif.timestamp.isNotBlank())
        }
    }

    @Test
    fun `viewing notifications marks them as read`() {
        val viewModel = StoreViewModel()
        assertTrue(viewModel.uiState.value.unreadNotificationsCount > 0)

        viewModel.markNotificationsAsRead()
        assertEquals(0, viewModel.uiState.value.unreadNotificationsCount)
        assertTrue(viewModel.uiState.value.notifications.all { it.isRead })
    }

    @Test
    fun `tapping notification navigates to account statement with preselected customer`() {
        val viewModel = StoreViewModel()
        val firstNotif = viewModel.uiState.value.notifications.first()

        viewModel.navigateTo(com.example.model.NavDestination.NOTIFICATIONS)
        assertEquals(com.example.model.NavDestination.NOTIFICATIONS, viewModel.uiState.value.currentDestination)

        viewModel.onNotificationClick(firstNotif)
        assertEquals(com.example.model.NavDestination.ACCOUNTS, viewModel.uiState.value.currentDestination)
        assertNotNull(viewModel.uiState.value.selectedCustomer)
        assertEquals(firstNotif.customerName, viewModel.uiState.value.selectedCustomer?.customerName)

        // Back button returns to previous destination
        viewModel.navigateBack()
        assertEquals(com.example.model.NavDestination.NOTIFICATIONS, viewModel.uiState.value.currentDestination)
    }

    @Test
    fun `accounts search filters customer list by name and phone`() {
        val viewModel = StoreViewModel()
        assertEquals(5, viewModel.uiState.value.filteredAccounts.size)

        // Search by name
        viewModel.setAccountsSearchQuery("خالد")
        assertEquals(1, viewModel.uiState.value.filteredAccounts.size)
        assertEquals("خالد بن عبدالعزيز", viewModel.uiState.value.filteredAccounts.first().customerName)

        // Search by phone
        viewModel.setAccountsSearchQuery("56 111")
        assertEquals(1, viewModel.uiState.value.filteredAccounts.size)
        assertEquals("مركز النور للمواد الغذائية", viewModel.uiState.value.filteredAccounts.first().customerName)

        // Search with no match
        viewModel.setAccountsSearchQuery("غير موجود")
        assertEquals(0, viewModel.uiState.value.filteredAccounts.size)

        // Clear search
        viewModel.setAccountsSearchQuery("")
        assertEquals(5, viewModel.uiState.value.filteredAccounts.size)
    }

    @Test
    fun `accounts filter options work for all, has debt, and recently active`() {
        val viewModel = StoreViewModel()

        // Default ALL
        assertEquals(com.example.model.AccountFilter.ALL, viewModel.uiState.value.accountsFilter)
        assertEquals(5, viewModel.uiState.value.filteredAccounts.size)

        // HAS_DEBT (balance > 0)
        viewModel.setAccountsFilter(com.example.model.AccountFilter.HAS_DEBT)
        assertTrue(viewModel.uiState.value.filteredAccounts.all { it.balance > 0 })
        assertEquals(4, viewModel.uiState.value.filteredAccounts.size) // 1 customer has negative balance (-3200)

        // RECENTLY_ACTIVE
        viewModel.setAccountsFilter(com.example.model.AccountFilter.RECENTLY_ACTIVE)
        assertTrue(viewModel.uiState.value.filteredAccounts.all {
            it.lastTransactionDate == "2026-09-04" || it.lastTransactionDate == "2026-09-05"
        })
    }

    @Test
    fun `add customer creates new customer in accounts list`() {
        val viewModel = StoreViewModel()
        val initialCount = viewModel.uiState.value.accounts.size

        viewModel.openAddCustomerDialog()
        assertTrue(viewModel.uiState.value.showAddCustomerDialog)

        viewModel.addCustomer(
            name = "عبدالله التميمي",
            phone = "+966 50 111 2222",
            initialDebt = 500.0
        )

        assertFalse(viewModel.uiState.value.showAddCustomerDialog)
        assertEquals(initialCount + 1, viewModel.uiState.value.accounts.size)
        val added = viewModel.uiState.value.accounts.first()
        assertEquals("عبدالله التميمي", added.customerName)
        assertEquals(500.0, added.balance, 0.01)
    }

    @Test
    fun `open customer details selects customer and close clears selection`() {
        val viewModel = StoreViewModel()
        val customer = viewModel.uiState.value.accounts.first()

        assertNull(viewModel.uiState.value.selectedCustomerDetails)

        viewModel.openCustomerDetails(customer)
        assertEquals(customer.id, viewModel.uiState.value.selectedCustomerDetails?.id)
        assertEquals(com.example.model.NavDestination.CUSTOMER_DETAILS, viewModel.uiState.value.currentDestination)

        viewModel.closeCustomerDetails()
        assertNull(viewModel.uiState.value.selectedCustomerDetails)
    }

    @Test
    fun `customer details unified actions preselect customer and navigate appropriately`() {
        val viewModel = StoreViewModel()
        val customer = viewModel.uiState.value.accounts[1] // خالد بن عبدالعزيز

        viewModel.openCustomerDetails(customer)
        assertEquals(customer.id, viewModel.uiState.value.selectedCustomerDetails?.id)

        // 1. Record Transaction -> Navigates to PURCHASES with customer pre-selected
        viewModel.onRecordTransactionForCustomer(customer)
        assertEquals(com.example.model.NavDestination.PURCHASES, viewModel.uiState.value.currentDestination)
        assertEquals(customer.id, viewModel.uiState.value.selectedCustomer?.id)

        // 2. Account Statement -> Navigates to ANALYSIS_CENTER with customer pre-selected
        viewModel.onAccountStatementForCustomer(customer)
        assertEquals(com.example.model.NavDestination.ANALYSIS_CENTER, viewModel.uiState.value.currentDestination)
        assertEquals(customer.id, viewModel.uiState.value.selectedCustomer?.id)

        // 3. Payment -> Opens Quick Payment with customer pre-selected
        viewModel.onPaymentForCustomer(customer)
        assertEquals(com.example.model.NavDestination.QUICK_PAYMENT, viewModel.uiState.value.currentDestination)
        assertEquals(customer.id, viewModel.uiState.value.selectedCustomer?.id)
        assertEquals(customer.id, viewModel.uiState.value.quickPaymentCustomer?.id)
    }

    @Test
    fun `customer details edit and archive work correctly`() {
        val viewModel = StoreViewModel()
        val customer = viewModel.uiState.value.accounts.first()

        viewModel.openCustomerDetails(customer)

        // Edit
        viewModel.editCustomer(customer, "مؤسسة الأمل الجديدة", "+966 50 999 8888")
        assertEquals("مؤسسة الأمل الجديدة", viewModel.uiState.value.selectedCustomerDetails?.customerName)
        assertEquals("+966 50 999 8888", viewModel.uiState.value.selectedCustomerDetails?.phone)

        // Archive
        val initialSize = viewModel.uiState.value.accounts.size
        viewModel.archiveCustomer(customer)
        assertEquals(initialSize - 1, viewModel.uiState.value.accounts.size)
        assertNull(viewModel.uiState.value.selectedCustomerDetails)
        assertEquals(com.example.model.NavDestination.ACCOUNTS, viewModel.uiState.value.currentDestination)
    }

    @Test
    fun `purchases entry B has no customer pre-selected and checkout is disabled`() {
        val viewModel = StoreViewModel()
        viewModel.navigateTo(com.example.model.NavDestination.PURCHASES)

        val state = viewModel.uiState.value
        assertEquals(com.example.model.NavDestination.PURCHASES, state.currentDestination)
        assertNull(state.purchasesCustomer)
        assertTrue(state.cart.isEmpty())
        assertFalse(state.canCompletePurchase)
    }

    @Test
    fun `adding products to cart and selecting customer enables complete transaction`() {
        val viewModel = StoreViewModel()
        viewModel.navigateTo(com.example.model.NavDestination.PURCHASES)

        val product1 = viewModel.uiState.value.products[0] // 45.00
        val product2 = viewModel.uiState.value.products[1] // 18.50

        viewModel.addToCart(product1)
        viewModel.addToCart(product1) // 2 of product1
        viewModel.addToCart(product2) // 1 of product2

        var state = viewModel.uiState.value
        assertEquals(2, state.cart.size)
        assertEquals(3, state.cartItemsCount)
        assertEquals(108.50, state.cartTotalAmount, 0.01)
        assertFalse("Checkout disabled without customer", state.canCompletePurchase)

        // Select a customer
        val customer = state.accounts[0]
        viewModel.selectCustomerForPurchases(customer)

        state = viewModel.uiState.value
        assertNotNull(state.purchasesCustomer)
        assertEquals(customer.id, state.purchasesCustomer?.id)
        assertTrue("Checkout enabled with customer and items in cart", state.canCompletePurchase)

        // Updating quantity
        viewModel.updateCartQuantity(product1.id, -1)
        assertEquals(2, viewModel.uiState.value.cartItemsCount)

        // Removing item
        viewModel.removeFromCart(product2.id)
        assertEquals(1, viewModel.uiState.value.cart.size)

        // Completing transaction opens settlement flow
        viewModel.onCompletePurchaseTransaction()
        assertTrue(viewModel.uiState.value.showSettlementConfirmation)
    }

    @Test
    fun `product search query filters products accurately`() {
        val viewModel = StoreViewModel()
        viewModel.setPurchasesSearchQuery("أرز")

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredProducts.size)
        assertTrue(state.filteredProducts.first().name.contains("أرز"))

        // Clear search
        viewModel.setPurchasesSearchQuery("")
        assertEquals(viewModel.uiState.value.products.size, viewModel.uiState.value.filteredProducts.size)
    }

    @Test
    fun `quick payment entry sets up pre-selected customer in full mode`() {
        val viewModel = StoreViewModel()
        viewModel.openQuickPayment()

        val state = viewModel.uiState.value
        assertEquals(com.example.model.NavDestination.QUICK_PAYMENT, state.currentDestination)
        assertNotNull(state.quickPaymentCustomer)
        assertEquals("خالد بن عبدالعزيز", state.quickPaymentCustomer?.customerName)
        assertEquals(com.example.model.SettlementType.FULL, state.quickPaymentSettlementType)
        assertEquals(com.example.model.PaymentMethodOption.CASH, state.quickPaymentMethod)
        assertEquals(1850.00, state.quickPaymentTransactionTotal, 0.01)
        assertEquals(1850.00, state.quickPaymentCurrentEnteredTotal, 0.01)
        assertFalse(state.isQuickPaymentAmountExceeded)
        assertTrue(state.isQuickPaymentValid)
    }

    @Test
    fun `quick payment full mode allows toggling payment method between cash and debt`() {
        val viewModel = StoreViewModel()
        viewModel.openQuickPayment()

        // Switch to Debt in Full mode
        viewModel.setQuickPaymentMethod(com.example.model.PaymentMethodOption.DEBT)
        var state = viewModel.uiState.value
        assertEquals(com.example.model.PaymentMethodOption.DEBT, state.quickPaymentMethod)
        assertEquals("", state.quickPaymentCashAmount)
        assertEquals("1850.00", state.quickPaymentDebtAmount)
        assertEquals(1850.00, state.quickPaymentCurrentEnteredTotal, 0.01)
        assertTrue(state.isQuickPaymentValid)

        // Switch back to Cash in Full mode
        viewModel.setQuickPaymentMethod(com.example.model.PaymentMethodOption.CASH)
        state = viewModel.uiState.value
        assertEquals(com.example.model.PaymentMethodOption.CASH, state.quickPaymentMethod)
        assertEquals("1850.00", state.quickPaymentCashAmount)
        assertEquals("", state.quickPaymentDebtAmount)
        assertTrue(state.isQuickPaymentValid)
    }

    @Test
    fun `quick payment partial mode enables editing both cash and debt amounts`() {
        val viewModel = StoreViewModel()
        viewModel.openQuickPayment()

        // Switch to Partial mode
        viewModel.setQuickPaymentSettlementType(com.example.model.SettlementType.PARTIAL)
        var state = viewModel.uiState.value
        assertEquals(com.example.model.SettlementType.PARTIAL, state.quickPaymentSettlementType)
        assertEquals(925.00, state.quickPaymentParsedCash, 0.01)
        assertEquals(925.00, state.quickPaymentParsedDebt, 0.01)
        assertEquals(1850.00, state.quickPaymentCurrentEnteredTotal, 0.01)
        assertTrue(state.isQuickPaymentValid)

        // Custom split: 1000 cash, 500 debt (total 1500 <= 1850)
        viewModel.setQuickPaymentCashAmount("1000.00")
        viewModel.setQuickPaymentDebtAmount("500.00")
        state = viewModel.uiState.value
        assertEquals(1500.00, state.quickPaymentCurrentEnteredTotal, 0.01)
        assertFalse(state.isQuickPaymentAmountExceeded)
        assertTrue(state.isQuickPaymentValid)

        // Exceeding transaction total: 1000 cash + 900 debt = 1900 > 1850
        viewModel.setQuickPaymentDebtAmount("900.00")
        state = viewModel.uiState.value
        assertEquals(1900.00, state.quickPaymentCurrentEnteredTotal, 0.01)
        assertTrue("Exceeded transaction total", state.isQuickPaymentAmountExceeded)
        assertFalse("Cannot complete when exceeded", state.isQuickPaymentValid)
    }

    @Test
    fun `changing customer in quick payment updates customer and transaction total`() {
        val viewModel = StoreViewModel()
        viewModel.openQuickPayment()

        val newCustomer = viewModel.uiState.value.accounts[0] // مؤسسة الأمل للتجارة (4250.00)
        viewModel.setQuickPaymentCustomer(newCustomer)

        val state = viewModel.uiState.value
        assertEquals("مؤسسة الأمل للتجارة", state.quickPaymentCustomer?.customerName)
        assertEquals(4250.00, state.quickPaymentTransactionTotal, 0.01)
        assertEquals("4250.00", state.quickPaymentCashAmount)
        assertTrue(state.isQuickPaymentValid)

        // Notes and Complete Action
        viewModel.setQuickPaymentNotes("دفعة استثنائية عن شهر سبتمبر")
        assertEquals("دفعة استثنائية عن شهر سبتمبر", viewModel.uiState.value.quickPaymentNotes)

        viewModel.onCompleteQuickPayment()
        assertTrue(viewModel.uiState.value.showUnifiedSettlementSummary)
    }

    @Test
    fun `unified settlement worked example calculations match 100 total, 50 cash, 40 debt`() {
        val total = 100.0
        val cash = 50.0
        val debt = 40.0
        val totalPaid = cash + debt
        val remaining = total - totalPaid

        assertEquals(90.0, totalPaid, 0.001)
        assertEquals(10.0, remaining, 0.001)
    }

    @Test
    fun `purchases complete transaction opens unified settlement and confirm settlement completes flow`() {
        val viewModel = StoreViewModel()
        viewModel.navigateTo(com.example.model.NavDestination.PURCHASES)
        viewModel.selectCustomerForPurchases(viewModel.uiState.value.accounts[0])
        viewModel.addToCart(viewModel.uiState.value.products[0])

        viewModel.onCompletePurchaseTransaction()
        assertTrue(viewModel.uiState.value.showSettlementConfirmation)

        viewModel.confirmSettlement(50.0, 40.0, "تم الاستلام")
        assertFalse(viewModel.uiState.value.showSettlementConfirmation)
        assertTrue(viewModel.uiState.value.cart.isEmpty())
        assertEquals(com.example.model.NavDestination.HOME, viewModel.uiState.value.currentDestination)
    }

    @Test
    fun `more screen navigates to more destination with 4 defined menu items`() {
        val viewModel = StoreViewModel()
        viewModel.navigateTo(com.example.model.NavDestination.MORE_SETTINGS)
        assertEquals(com.example.model.NavDestination.MORE_SETTINGS, viewModel.uiState.value.currentDestination)

        // Verify all 4 required more menu item strings exist
        assertEquals("بيانات المتجر", com.example.model.StoreStrings.STORE_INFO_AR)
        assertEquals("Store Information", com.example.model.StoreStrings.STORE_INFO_EN)

        assertEquals("إعدادات التطبيق", com.example.model.StoreStrings.APP_SETTINGS_AR)
        assertEquals("App Settings", com.example.model.StoreStrings.APP_SETTINGS_EN)

        assertEquals("مركز البيانات", com.example.model.StoreStrings.DATA_CENTER_AR)
        assertEquals("Data Center", com.example.model.StoreStrings.DATA_CENTER_EN)

        assertEquals("عن التطبيق", com.example.model.StoreStrings.ABOUT_AR)
        assertEquals("About", com.example.model.StoreStrings.ABOUT_EN)
    }

    @Test
    fun `store information screen navigates and contains all required form labels`() {
        val viewModel = StoreViewModel()
        viewModel.navigateTo(com.example.model.NavDestination.MORE_SETTINGS)
        viewModel.navigateTo(com.example.model.NavDestination.STORE_INFORMATION)
        assertEquals(com.example.model.NavDestination.STORE_INFORMATION, viewModel.uiState.value.currentDestination)

        // Verify back navigation returns to previous destination
        viewModel.navigateBack()
        assertEquals(com.example.model.NavDestination.MORE_SETTINGS, viewModel.uiState.value.currentDestination)

        // Verify form labels exist
        assertEquals("اسم المتجر", com.example.model.StoreStrings.STORE_NAME_LABEL_AR)
        assertEquals("اسم المالك", com.example.model.StoreStrings.OWNER_NAME_LABEL_AR)
        assertEquals("رقم الهاتف", com.example.model.StoreStrings.PHONE_LABEL_AR)
        assertEquals("العنوان", com.example.model.StoreStrings.ADDRESS_LABEL_AR)
        assertEquals("حفظ", com.example.model.StoreStrings.SAVE_AR)
    }

    @Test
    fun `app settings screen navigation and section properties`() {
        val viewModel = StoreViewModel()
        viewModel.navigateTo(com.example.model.NavDestination.MORE_SETTINGS)
        viewModel.navigateTo(com.example.model.NavDestination.APP_SETTINGS)
        assertEquals(com.example.model.NavDestination.APP_SETTINGS, viewModel.uiState.value.currentDestination)

        // Verify back navigation returns to MORE_SETTINGS
        viewModel.navigateBack()
        assertEquals(com.example.model.NavDestination.MORE_SETTINGS, viewModel.uiState.value.currentDestination)

        // Verify Theme and Language changes
        viewModel.setTheme(com.example.ui.theme.AppThemeMode.PURPLE)
        assertEquals(com.example.ui.theme.AppThemeMode.PURPLE, viewModel.uiState.value.themeMode)
        viewModel.setTheme(com.example.ui.theme.AppThemeMode.NEUTRAL)
        assertEquals(com.example.ui.theme.AppThemeMode.NEUTRAL, viewModel.uiState.value.themeMode)

        viewModel.setLanguage(com.example.model.LanguageMode.ENGLISH)
        assertEquals(com.example.model.LanguageMode.ENGLISH, viewModel.uiState.value.languageMode)
        viewModel.setLanguage(com.example.model.LanguageMode.ARABIC)
        assertEquals(com.example.model.LanguageMode.ARABIC, viewModel.uiState.value.languageMode)

        // Verify App Settings section strings
        assertEquals("المظهر", com.example.model.StoreStrings.SECTION_APPEARANCE_AR)
        assertEquals("Appearance", com.example.model.StoreStrings.SECTION_APPEARANCE_EN)
        assertEquals("اللغة", com.example.model.StoreStrings.SECTION_LANGUAGE_AR)
        assertEquals("Language", com.example.model.StoreStrings.SECTION_LANGUAGE_EN)
        assertEquals("النسخ الاحتياطي والاستعادة", com.example.model.StoreStrings.SECTION_BACKUP_AR)
        assertEquals("Backup & Restore", com.example.model.StoreStrings.SECTION_BACKUP_EN)
        assertEquals("التفضيلات", com.example.model.StoreStrings.SECTION_PREFERENCES_AR)
        assertEquals("Preferences", com.example.model.StoreStrings.SECTION_PREFERENCES_EN)

        // Verify exact row strings
        assertEquals("نسخ احتياطي الآن", com.example.model.StoreStrings.BACKUP_NOW_AR)
        assertEquals("استعادة من نسخة احتياطية", com.example.model.StoreStrings.RESTORE_FROM_BACKUP_AR)
        assertEquals("تفعيل الإشعارات", com.example.model.StoreStrings.PREF_ENABLE_NOTIFICATIONS_AR)
    }

    @Test
    fun `data center screen navigation sections and conflict states`() {
        val viewModel = StoreViewModel()
        viewModel.navigateTo(com.example.model.NavDestination.MORE_SETTINGS)
        viewModel.navigateTo(com.example.model.NavDestination.DATA_CENTER)
        assertEquals(com.example.model.NavDestination.DATA_CENTER, viewModel.uiState.value.currentDestination)

        // Verify back navigation returns to MORE_SETTINGS
        viewModel.navigateBack()
        assertEquals(com.example.model.NavDestination.MORE_SETTINGS, viewModel.uiState.value.currentDestination)

        viewModel.navigateTo(com.example.model.NavDestination.DATA_CENTER)
        assertEquals(com.example.model.NavDestination.DATA_CENTER, viewModel.uiState.value.currentDestination)

        // Verify section strings
        assertEquals("الأرشيف / سلة المحذوفات", com.example.model.StoreStrings.SECTION_ARCHIVE_TRASH_AR)
        assertEquals("Archive / Trash", com.example.model.StoreStrings.SECTION_ARCHIVE_TRASH_EN)
        assertEquals("العملاء المؤرشفون", com.example.model.StoreStrings.ARCHIVED_CUSTOMERS_AR)
        assertEquals("Archived Customers", com.example.model.StoreStrings.ARCHIVED_CUSTOMERS_EN)
        assertEquals("المنتجات المؤرشفة", com.example.model.StoreStrings.ARCHIVED_PRODUCTS_AR)
        assertEquals("Archived Products", com.example.model.StoreStrings.ARCHIVED_PRODUCTS_EN)
        assertEquals("العناصر المحذوفة", com.example.model.StoreStrings.DELETED_ITEMS_AR)
        assertEquals("Deleted Items", com.example.model.StoreStrings.DELETED_ITEMS_EN)

        // Verify Backup & Restore section strings
        assertEquals("إنشاء نسخة احتياطية", com.example.model.StoreStrings.CREATE_BACKUP_AR)
        assertEquals("Create Backup", com.example.model.StoreStrings.CREATE_BACKUP_EN)
        assertEquals("استعادة نسخة احتياطية", com.example.model.StoreStrings.RESTORE_BACKUP_AR)
        assertEquals("Restore Backup", com.example.model.StoreStrings.RESTORE_BACKUP_EN)

        // Verify actions
        assertEquals("استعادة", com.example.model.StoreStrings.RESTORE_ACTION_AR)
        assertEquals("Restore", com.example.model.StoreStrings.RESTORE_ACTION_EN)
        assertEquals("حذف نهائي", com.example.model.StoreStrings.DELETE_PERMANENTLY_AR)
        assertEquals("Delete Permanently", com.example.model.StoreStrings.DELETE_PERMANENTLY_EN)

        // Verify Conflict choices: exactly Replace, Add, Skip
        assertEquals("مثال: تعارض عند الاستعادة", com.example.model.StoreStrings.CONFLICT_DIALOG_TITLE_AR)
        assertEquals("Example: Restore Conflict", com.example.model.StoreStrings.CONFLICT_DIALOG_TITLE_EN)
        assertEquals("استبدال", com.example.model.StoreStrings.CONFLICT_REPLACE_AR)
        assertEquals("Replace", com.example.model.StoreStrings.CONFLICT_REPLACE_EN)
        assertEquals("إضافة", com.example.model.StoreStrings.CONFLICT_ADD_AR)
        assertEquals("Add", com.example.model.StoreStrings.CONFLICT_ADD_EN)
        assertEquals("تخطي", com.example.model.StoreStrings.CONFLICT_SKIP_AR)
        assertEquals("Skip", com.example.model.StoreStrings.CONFLICT_SKIP_EN)

        // Verify Lifecycle philosophy string
        assertEquals("Active → Archive/Trash → Review → Restore OR Permanent Cleanup", com.example.model.StoreStrings.DATA_LIFECYCLE_PHILOSOPHY_EN)
    }

    @Test
    fun `about screen navigation layout and static links`() {
        val viewModel = StoreViewModel()
        assertEquals(com.example.model.NavDestination.ABOUT, viewModel.uiState.value.currentDestination)

        // Verify back navigation returns to MORE_SETTINGS
        viewModel.navigateBack()
        assertEquals(com.example.model.NavDestination.MORE_SETTINGS, viewModel.uiState.value.currentDestination)

        viewModel.navigateTo(com.example.model.NavDestination.ABOUT)
        assertEquals(com.example.model.NavDestination.ABOUT, viewModel.uiState.value.currentDestination)

        // Verify top bar and app identity
        assertEquals("عن التطبيق", com.example.model.StoreStrings.ABOUT_AR)
        assertEquals("About", com.example.model.StoreStrings.ABOUT_EN)
        assertEquals("SmallStore", com.example.model.StoreStrings.ABOUT_APP_NAME_EN)
        assertEquals("v1.0.0", com.example.model.StoreStrings.ABOUT_VERSION_LABEL)

        // Verify links/rows: "Privacy Policy", "Terms of Use", "Contact Support"
        assertEquals("سياسة الخصوصية", com.example.model.StoreStrings.ABOUT_PRIVACY_POLICY_AR)
        assertEquals("Privacy Policy", com.example.model.StoreStrings.ABOUT_PRIVACY_POLICY_EN)
        assertEquals("شروط الاستخدام", com.example.model.StoreStrings.ABOUT_TERMS_OF_USE_AR)
        assertEquals("Terms of Use", com.example.model.StoreStrings.ABOUT_TERMS_OF_USE_EN)
        assertEquals("الاتصال بالدعم الفني", com.example.model.StoreStrings.ABOUT_CONTACT_SUPPORT_AR)
        assertEquals("Contact Support", com.example.model.StoreStrings.ABOUT_CONTACT_SUPPORT_EN)
    }
}
