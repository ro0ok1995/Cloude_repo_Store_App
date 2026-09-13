package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.backup.BackupManager
import com.example.data.backup.BackupPayload
import com.example.data.db.TransactionItemLineEntity
import com.example.data.repository.StoreRepository
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
import com.example.ui.components.SettlementContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MainUiState(
    val currentDestination: NavDestination = NavDestination.HOME,
    val activeBottomNav: NavDestination = NavDestination.HOME,
    val isDrawerOpen: Boolean = false,
    val showActionSheet: Boolean = false,
    val showSettlementSheet: Boolean = false,
    val settlementTotal: Double = 0.0,
    val settlementContext: SettlementContext = SettlementContext.RECORD_TRANSACTION,

    val languageMode: LanguageMode = LanguageMode.ARABIC,
    val themeMode: AppThemeMode = AppThemeMode.PURPLE,
    val displayMode: ThemeDisplayMode = ThemeDisplayMode.LIGHT,
    val storeInfo: StoreInfo = StoreInfo(),

    val customers: List<CustomerAccount> = emptyList(),
    val transactions: List<TransactionItem> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val products: List<ProductItem> = emptyList(),
    val transactionLines: List<com.example.data.db.TransactionItemLineEntity> = emptyList(),

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
    val quickPaymentAmount: String = "",
    val quickPaymentNotes: String = ""
)

class MainViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: StoreRepository = StoreRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // Backup & Restore conflict handling
    val pendingRestorePayload = MutableStateFlow<BackupPayload?>(null)
    val showRestoreConflictSheet = MutableStateFlow(false)

    // StoreInfo status
    val isStoreInfoSaved = MutableStateFlow<Boolean?>(null)
    private var hasCheckedFirstLaunch = false

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
            val saved = repository.isStoreInfoSaved()
            isStoreInfoSaved.value = saved
            if (!saved && !hasCheckedFirstLaunch) {
                hasCheckedFirstLaunch = true
                _uiState.update { it.copy(currentDestination = NavDestination.STORE_INFORMATION) }
            }
        }

        viewModelScope.launch {
            repository.customers.collect { list ->
                _uiState.update { it.copy(customers = list) }
            }
        }

        viewModelScope.launch {
            repository.transactions.collect { list ->
                _uiState.update { it.copy(transactions = list) }
            }
        }

        viewModelScope.launch {
            repository.products.collect { list ->
                _uiState.update { it.copy(products = list) }
            }
        }

        viewModelScope.launch {
            repository.notifications.collect { list ->
                _uiState.update { it.copy(notifications = list) }
            }
        }

        viewModelScope.launch {
            repository.transactionLines.collect { list ->
                _uiState.update { it.copy(transactionLines = list) }
            }
        }

        viewModelScope.launch {
            repository.storeInfo.collect { info ->
                _uiState.update { it.copy(storeInfo = info) }
            }
        }
    }

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
        viewModelScope.launch {
            val wasSaved = repository.isStoreInfoSaved()
            repository.saveStoreInfo(info, markAsSaved = true)
            isStoreInfoSaved.value = true
            _uiState.update { current ->
                val nextDest = if (!wasSaved && current.currentDestination == NavDestination.STORE_INFORMATION) {
                    NavDestination.HOME
                } else {
                    current.currentDestination
                }
                current.copy(
                    storeInfo = info,
                    currentDestination = nextDest,
                    activeBottomNav = if (nextDest == NavDestination.HOME) NavDestination.HOME else current.activeBottomNav
                )
            }
        }
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
        _uiState.update { it.copy(showAddCustomerDialog = false) }
        viewModelScope.launch {
            repository.addCustomer(newCustomer)
        }
    }

    // NOTIFICATIONS
    fun markNotificationsAsRead() {
        viewModelScope.launch {
            repository.markNotificationsAsRead()
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

    fun openPurchasesSettlement(cartItems: List<CartItem> = _uiState.value.cart) {
        val total = cartItems.sumOf { it.product.price * it.quantity }
        _uiState.update {
            it.copy(
                settlementTotal = total,
                settlementContext = SettlementContext.RECORD_TRANSACTION,
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
        val txId = "tx_${System.currentTimeMillis()}"

        val newTx = TransactionItem(
            id = txId,
            customerName = customer.customerName,
            activityType = if (isFullCash) "شراء كاش" else "شراء آجل",
            amount = total,
            relativeTime = "الآن",
            date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            isCredit = !isFullCash,
            notes = notes,
            settlementType = if (isFullCash) SettlementType.FULL else SettlementType.PARTIAL
        )

        val lines = state.cart.map { cartItem ->
            TransactionItemLineEntity(
                transactionId = txId,
                productId = cartItem.product.id,
                productNameSnapshot = cartItem.product.name,
                quantity = cartItem.quantity,
                unitPrice = cartItem.product.price,
                costPrice = cartItem.product.costPrice,
                subtotal = cartItem.product.price * cartItem.quantity
            )
        }

        val updatedCustomer = customer.copy(
            balance = customer.balance + debtAmount,
            totalDebt = customer.totalDebt + debtAmount,
            hasRecentActivity = true
        )

        val notif = NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            customerName = customer.customerName,
            transactionType = if (isFullCash) "شراء كاش" else "شراء آجل",
            amount = total,
            timestamp = "الآن",
            isPayment = false,
            isRead = false,
            transactionId = txId
        )

        _uiState.update {
            it.copy(
                cart = emptyList(),
                showSettlementSheet = false,
                currentDestination = NavDestination.HOME,
                activeBottomNav = NavDestination.HOME
            )
        }

        viewModelScope.launch {
            repository.addTransaction(newTx, lines)
            repository.updateCustomer(updatedCustomer)
            repository.addNotification(notif)
        }
    }

    // QUICK PAYMENT
    fun openQuickPayment(customer: CustomerAccount? = null) {
        _uiState.update {
            it.copy(
                quickPaymentCustomer = customer,
                quickPaymentAmount = "",
                quickPaymentNotes = "",
                currentDestination = NavDestination.QUICK_PAYMENT
            )
        }
    }

    fun setQuickPaymentCustomer(customer: CustomerAccount?) {
        _uiState.update {
            it.copy(quickPaymentCustomer = customer)
        }
    }

    fun setQuickPaymentAmount(amt: String) {
        _uiState.update { it.copy(quickPaymentAmount = amt) }
    }

    fun setQuickPaymentNotes(notes: String) {
        _uiState.update { it.copy(quickPaymentNotes = notes) }
    }

    fun completeQuickPayment() {
        val state = _uiState.value
        val customer = state.quickPaymentCustomer ?: return
        val amount = state.quickPaymentAmount.toDoubleOrNull() ?: return
        if (amount <= 0.0 || amount > (customer.balance + 0.001)) return

        val txId = "tx_${System.currentTimeMillis()}"
        val newTx = TransactionItem(
            id = txId,
            customerName = customer.customerName,
            activityType = "تسديد",
            amount = amount,
            relativeTime = "الآن",
            date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            isCredit = false,
            notes = state.quickPaymentNotes.ifBlank { "تسديد دفعة سريعة" },
            settlementType = null
        )

        val updatedCustomer = customer.copy(
            balance = (customer.balance - amount).coerceAtLeast(0.0),
            totalDebt = (customer.totalDebt - amount).coerceAtLeast(0.0),
            hasRecentActivity = true
        )

        val newNotif = NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            customerName = customer.customerName,
            transactionType = "تسديد",
            amount = amount,
            timestamp = "الآن",
            isPayment = true,
            isRead = false,
            transactionId = txId
        )

        _uiState.update {
            it.copy(
                quickPaymentCustomer = null,
                quickPaymentAmount = "",
                quickPaymentNotes = "",
                currentDestination = NavDestination.HOME,
                activeBottomNav = NavDestination.HOME
            )
        }

        viewModelScope.launch {
            repository.addTransaction(newTx)
            repository.updateCustomer(updatedCustomer)
            repository.addNotification(newNotif)
        }
    }

    fun resetData() {
        _uiState.update {
            it.copy(
                cart = emptyList(),
                homeSelectedCustomer = null,
                purchasesCustomer = null
            )
        }
        viewModelScope.launch {
            repository.resetDatabaseToSampleData()
        }
    }

    // BACKUP & RESTORE
    fun exportBackup(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val payload = repository.getAllDataForBackup()
                val jsonString = BackupManager.serialize(payload)
                val success = BackupManager.writeToUri(context.contentResolver, uri, jsonString)
                onResult(success)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun prepareRestore(context: Context, uri: Uri, onError: () -> Unit, onSuccessSilent: () -> Unit) {
        viewModelScope.launch {
            try {
                val jsonString = BackupManager.readFromUri(context.contentResolver, uri)
                if (jsonString.isNullOrBlank()) {
                    onError()
                    return@launch
                }
                val payload = BackupManager.deserialize(jsonString)
                val currentInfo = repository.getStoreInfoSnapshot()

                val differs = isStoreInfoDifferent(payload.storeInfoAtBackupTime, currentInfo)
                if (differs) {
                    pendingRestorePayload.value = payload
                    showRestoreConflictSheet.value = true
                } else {
                    repository.restoreDataFromBackup(payload, replaceStoreInfo = false)
                    pendingRestorePayload.value = null
                    showRestoreConflictSheet.value = false
                    onSuccessSilent()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
    }

    fun confirmRestore(replaceStoreInfo: Boolean, onComplete: () -> Unit) {
        val payload = pendingRestorePayload.value ?: return
        viewModelScope.launch {
            try {
                repository.restoreDataFromBackup(payload, replaceStoreInfo = replaceStoreInfo)
                onComplete()
            } finally {
                pendingRestorePayload.value = null
                showRestoreConflictSheet.value = false
            }
        }
    }

    fun cancelRestore() {
        pendingRestorePayload.value = null
        showRestoreConflictSheet.value = false
    }

    private fun isStoreInfoDifferent(backup: StoreInfo, current: StoreInfo): Boolean {
        return backup.storeName.trim() != current.storeName.trim() ||
                backup.ownerName.trim() != current.ownerName.trim() ||
                backup.phone.trim() != current.phone.trim() ||
                backup.address.trim() != current.address.trim() ||
                backup.taxNumber.trim() != current.taxNumber.trim() ||
                backup.crNumber.trim() != current.crNumber.trim()
    }
}

