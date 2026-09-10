package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.AccountFilter
import com.example.model.AppThemeMode
import com.example.model.CartItem
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.NavDestination
import com.example.model.NotificationItem
import com.example.model.PaymentMethodOption
import com.example.model.PeriodFilter
import com.example.model.ProductItem
import com.example.model.SampleData
import com.example.model.SettlementType
import com.example.model.StoreInfo
import com.example.model.ThemeDisplayMode
import com.example.model.TransactionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class MainUiState(
    val currentDestination: NavDestination = NavDestination.HOME,
    val activeBottomNav: NavDestination = NavDestination.HOME,
    val isDrawerOpen: Boolean = false,
    val showActionSheet: Boolean = false,
    val showSettlementSheet: Boolean = false,
    val settlementTotal: Double = 0.0,

    val languageMode: LanguageMode = LanguageMode.ARABIC,
    val themeMode: AppThemeMode = AppThemeMode.PURPLE,
    val displayMode: ThemeDisplayMode = ThemeDisplayMode.LIGHT,
    val storeInfo: StoreInfo = StoreInfo(),

    val customers: List<CustomerAccount> = SampleData.sampleCustomers,
    val transactions: List<TransactionItem> = SampleData.sampleTransactions,
    val notifications: List<NotificationItem> = SampleData.sampleNotifications,
    val products: List<ProductItem> = SampleData.sampleProducts,

    // Home screen state
    val homeSearchQuery: String = "",
    val homeSelectedCustomer: CustomerAccount? = null,
    val homeSelectedPeriod: PeriodFilter = PeriodFilter.MONTH,

    // Accounts screen state
    val accountsSearchQuery: String = "",
    val accountsFilter: AccountFilter = AccountFilter.ALL,
    val accountsSelectedCustomerDetails: CustomerAccount? = null,
    val showAddCustomerDialog: Boolean = false,

    // Purchases screen state
    val purchasesCustomer: CustomerAccount? = null,
    val cart: List<CartItem> = emptyList(),
    val isCartExpanded: Boolean = false,
    val purchasesSearchQuery: String = "",

    // Quick Payment screen state
    val quickPaymentCustomer: CustomerAccount? = null,
    val quickPaymentTotal: Double = 150.0,
    val quickPaymentSettlementType: SettlementType = SettlementType.FULL,
    val quickPaymentMethod: PaymentMethodOption = PaymentMethodOption.CASH,
    val quickPaymentCashAmount: String = "150",
    val quickPaymentDebtAmount: String = "0",
    val quickPaymentNotes: String = ""
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // NAVIGATION
    fun navigateTo(destination: NavDestination) {
        _uiState.update { state ->
            val updatedBottomNav = when (destination) {
                NavDestination.HOME,
                NavDestination.ACCOUNTS,
                NavDestination.ANALYSIS_CENTER,
                NavDestination.MORE -> destination
                else -> state.activeBottomNav
            }
            state.copy(
                currentDestination = destination,
                activeBottomNav = updatedBottomNav,
                isDrawerOpen = false
            )
        }
    }

    fun openDrawer() {
        _uiState.update { it.copy(isDrawerOpen = true) }
    }

    fun closeDrawer() {
        _uiState.update { it.copy(isDrawerOpen = false) }
    }

    fun openActionSheet() {
        _uiState.update { it.copy(showActionSheet = true) }
    }

    fun closeActionSheet() {
        _uiState.update { it.copy(showActionSheet = false) }
    }

    fun setLanguageMode(mode: LanguageMode) {
        _uiState.update { it.copy(languageMode = mode) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setDisplayMode(mode: ThemeDisplayMode) {
        _uiState.update { it.copy(displayMode = mode) }
    }

    fun saveStoreInfo(info: StoreInfo) {
        _uiState.update { it.copy(storeInfo = info) }
    }

    // HOME SCREEN
    fun setHomeSearchQuery(query: String) {
        _uiState.update { current ->
            val updatedCustomer = if (current.homeSelectedCustomer != null && query != current.homeSelectedCustomer.customerName) {
                null
            } else {
                current.homeSelectedCustomer
            }
            current.copy(homeSearchQuery = query, homeSelectedCustomer = updatedCustomer)
        }
    }

    fun selectHomeCustomer(customer: CustomerAccount) {
        _uiState.update { it.copy(homeSelectedCustomer = customer, homeSearchQuery = customer.customerName) }
    }

    fun clearHomeSelectedCustomer() {
        _uiState.update { it.copy(homeSelectedCustomer = null, homeSearchQuery = "") }
    }

    fun setHomePeriod(period: PeriodFilter) {
        _uiState.update { it.copy(homeSelectedPeriod = period) }
    }

    // ACCOUNTS SCREEN
    fun setAccountsSearchQuery(query: String) {
        _uiState.update { it.copy(accountsSearchQuery = query) }
    }

    fun setAccountsFilter(filter: AccountFilter) {
        _uiState.update { it.copy(accountsFilter = filter) }
    }

    fun selectCustomerDetails(customer: CustomerAccount?) {
        _uiState.update { it.copy(accountsSelectedCustomerDetails = customer) }
    }

    fun openAddCustomerDialog() {
        _uiState.update { it.copy(showAddCustomerDialog = true) }
    }

    fun closeAddCustomerDialog() {
        _uiState.update { it.copy(showAddCustomerDialog = false) }
    }

    fun addCustomer(name: String, phone: String, initialDebt: Double) {
        val newCustomer = CustomerAccount(
            id = "c_${System.currentTimeMillis()}",
            customerName = name.trim(),
            phone = phone.trim(),
            balance = initialDebt,
            totalDebt = initialDebt,
            hasRecentActivity = true
        )
        _uiState.update {
            it.copy(
                customers = listOf(newCustomer) + it.customers,
                showAddCustomerDialog = false
            )
        }
    }

    // NOTIFICATIONS
    fun markNotificationsAsRead() {
        _uiState.update { state ->
            state.copy(notifications = state.notifications.map { it.copy(isRead = true) })
        }
    }

    // PURCHASES / CART
    fun setPurchasesCustomer(customer: CustomerAccount?) {
        _uiState.update { it.copy(purchasesCustomer = customer) }
    }

    fun setPurchasesSearchQuery(query: String) {
        _uiState.update { it.copy(purchasesSearchQuery = query) }
    }

    fun toggleCartExpanded() {
        _uiState.update { it.copy(isCartExpanded = !it.isCartExpanded) }
    }

    fun addToCart(product: ProductItem) {
        _uiState.update { state ->
            val existing = state.cart.find { it.product.id == product.id }
            val newCart = if (existing != null) {
                state.cart.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                state.cart + CartItem(product = product, quantity = 1)
            }
            state.copy(cart = newCart)
        }
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        _uiState.update { state ->
            val newCart = state.cart.mapNotNull { item ->
                if (item.product.id == productId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else {
                    item
                }
            }
            state.copy(cart = newCart)
        }
    }

    fun removeFromCart(productId: String) {
        _uiState.update { state ->
            state.copy(cart = state.cart.filter { it.product.id != productId })
        }
    }

    fun openPurchasesSettlement() {
        val total = _uiState.value.cart.sumOf { it.product.price * it.quantity }
        _uiState.update {
            it.copy(
                settlementTotal = total,
                showSettlementSheet = true
            )
        }
    }

    fun dismissSettlementSheet() {
        _uiState.update { it.copy(showSettlementSheet = false) }
    }

    fun completeSettlement(cashAmount: Double, debtAmount: Double, notes: String) {
        val state = _uiState.value
        val customer = state.purchasesCustomer ?: state.customers.firstOrNull() ?: return
        val total = state.settlementTotal
        val isFullCash = debtAmount <= 0.01

        val newTx = TransactionItem(
            id = "tx_${System.currentTimeMillis()}",
            customerName = customer.customerName,
            activityType = if (isFullCash) "شراء كاش" else "شراء آجل",
            amount = total,
            relativeTime = "الآن",
            date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            isCredit = !isFullCash,
            notes = notes
        )

        val updatedCustomers = state.customers.map {
            if (it.id == customer.id) {
                it.copy(
                    balance = it.balance + debtAmount,
                    totalDebt = it.totalDebt + debtAmount,
                    hasRecentActivity = true
                )
            } else it
        }

        _uiState.update {
            it.copy(
                customers = updatedCustomers,
                transactions = listOf(newTx) + it.transactions,
                cart = emptyList(),
                showSettlementSheet = false,
                currentDestination = NavDestination.HOME,
                activeBottomNav = NavDestination.HOME
            )
        }
    }

    // QUICK PAYMENT
    fun openQuickPayment(customer: CustomerAccount? = null) {
        val targetCustomer = customer ?: _uiState.value.customers.firstOrNull()
        val defaultDebt = targetCustomer?.balance?.coerceAtLeast(50.0) ?: 100.0
        _uiState.update {
            it.copy(
                quickPaymentCustomer = targetCustomer,
                quickPaymentTotal = defaultDebt,
                quickPaymentSettlementType = SettlementType.FULL,
                quickPaymentMethod = PaymentMethodOption.CASH,
                quickPaymentCashAmount = String.format(Locale.US, "%.0f", defaultDebt),
                quickPaymentDebtAmount = "0",
                quickPaymentNotes = "",
                currentDestination = NavDestination.QUICK_PAYMENT
            )
        }
    }

    fun setQuickPaymentCustomer(customer: CustomerAccount) {
        val defaultDebt = customer.balance.coerceAtLeast(50.0)
        _uiState.update {
            it.copy(
                quickPaymentCustomer = customer,
                quickPaymentTotal = defaultDebt,
                quickPaymentCashAmount = String.format(Locale.US, "%.0f", defaultDebt)
            )
        }
    }

    fun setQuickPaymentSettlementType(type: SettlementType) {
        _uiState.update { it.copy(quickPaymentSettlementType = type) }
    }

    fun setQuickPaymentMethod(method: PaymentMethodOption) {
        _uiState.update { state ->
            val total = state.quickPaymentTotal
            val cash = if (method == PaymentMethodOption.CASH) String.format(Locale.US, "%.0f", total) else "0"
            val debt = if (method == PaymentMethodOption.DEBT) String.format(Locale.US, "%.0f", total) else "0"
            state.copy(
                quickPaymentMethod = method,
                quickPaymentCashAmount = cash,
                quickPaymentDebtAmount = debt
            )
        }
    }

    fun setQuickPaymentCashAmount(amt: String) {
        _uiState.update { it.copy(quickPaymentCashAmount = amt) }
    }

    fun setQuickPaymentDebtAmount(amt: String) {
        _uiState.update { it.copy(quickPaymentDebtAmount = amt) }
    }

    fun setQuickPaymentNotes(notes: String) {
        _uiState.update { it.copy(quickPaymentNotes = notes) }
    }

    fun completeQuickPayment() {
        val state = _uiState.value
        val customer = state.quickPaymentCustomer ?: return
        val cash = state.quickPaymentCashAmount.toDoubleOrNull() ?: 0.0
        val debt = state.quickPaymentDebtAmount.toDoubleOrNull() ?: 0.0
        val totalPaid = cash + debt

        val newTx = TransactionItem(
            id = "tx_${System.currentTimeMillis()}",
            customerName = customer.customerName,
            activityType = "تسديد",
            amount = totalPaid,
            relativeTime = "الآن",
            date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            isCredit = false,
            notes = state.quickPaymentNotes.ifBlank { "تسديد دفعة سريعة" },
            settlementType = state.quickPaymentSettlementType
        )

        val updatedCustomers = state.customers.map {
            if (it.id == customer.id) {
                val newBal = (it.balance - totalPaid).coerceAtLeast(0.0)
                it.copy(balance = newBal, hasRecentActivity = true)
            } else it
        }

        _uiState.update {
            it.copy(
                customers = updatedCustomers,
                transactions = listOf(newTx) + it.transactions,
                currentDestination = NavDestination.HOME,
                activeBottomNav = NavDestination.HOME
            )
        }
    }

    fun resetData() {
        _uiState.update {
            it.copy(
                customers = SampleData.sampleCustomers,
                transactions = SampleData.sampleTransactions,
                notifications = SampleData.sampleNotifications,
                cart = emptyList(),
                homeSelectedCustomer = null,
                purchasesCustomer = null
            )
        }
    }
}
