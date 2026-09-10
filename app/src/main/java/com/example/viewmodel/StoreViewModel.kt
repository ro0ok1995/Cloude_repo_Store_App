package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.AccountFilter
import com.example.model.CartItem
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.NavDestination
import com.example.model.NotificationItem
import com.example.model.PaymentMethodOption
import com.example.model.PeriodFilter
import com.example.model.ProductItem
import com.example.model.SettlementType
import com.example.model.StoreStrings
import com.example.model.TransactionItem
import com.example.model.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StoreUiState(
    val languageMode: LanguageMode = LanguageMode.ARABIC, // Arabic-first default
    val themeMode: AppThemeMode = AppThemeMode.NEUTRAL,   // Neutral light default
    val currentDestination: NavDestination = NavDestination.ABOUT,
    val previousDestination: NavDestination = NavDestination.MORE_SETTINGS,
    val showActionSheet: Boolean = false,
    val showRecordTransactionSheet: Boolean = false,
    val showQuickPaymentSheet: Boolean = false,
    val showEmptyStateDemo: Boolean = false,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val selectedCustomer: CustomerAccount? = null,
    val selectedPeriod: PeriodFilter = PeriodFilter.TODAY,
    val accountsSearchQuery: String = "",
    val accountsFilter: AccountFilter = AccountFilter.ALL,
    val showAddCustomerDialog: Boolean = false,
    val selectedCustomerDetails: CustomerAccount? = null,
    val purchasesCustomer: CustomerAccount? = null,
    val purchasesSearchQuery: String = "",
    val cart: List<CartItem> = emptyList(),
    val isCartExpanded: Boolean = false,
    val showCustomerPickerForPurchases: Boolean = false,
    val showSettlementConfirmation: Boolean = false,
    val products: List<ProductItem> = listOf(
        ProductItem("p1", "أرز بسمتي هندي فاخر (5 كجم)", 45.00, "مواد غذائية", "كيس"),
        ProductItem("p2", "زيت دوار الشمس نقي (1.5 لتر)", 18.50, "زيوت", "عبوة"),
        ProductItem("p3", "سكر أبيض ناعم الأسرة (5 كجم)", 22.00, "مواد غذائية", "كيس"),
        ProductItem("p4", "حليب طويل الأجل كامل الدسم (12 عبوة)", 54.00, "ألبان", "كرتون"),
        ProductItem("p5", "شاي أسود فرط فاخر (400 جم)", 16.00, "مشروبات", "عبوة"),
        ProductItem("p6", "معجون طماطم لونا (8 حبات)", 14.00, "معلبات", "شدّة"),
        ProductItem("p7", "جبنة كريمية بيضاء (500 جم)", 15.50, "ألبان", "حبة"),
        ProductItem("p8", "قهوة عربية هرري ممتازة (500 جم)", 38.00, "مشروبات", "كيس"),
        ProductItem("p9", "دقيق فاخر متعدد الاستخدام (10 كجم)", 28.00, "دقيق ومخبوزات", "كيس"),
        ProductItem("p10", "مياه نبع شرب نقية (كرتون 40 عبوة)", 17.00, "مشروبات", "كرتون"),
        ProductItem("p11", "طبق بيض مزارع طازج (30 بيضة)", 21.00, "طازج", "طبق"),
        ProductItem("p12", "تونة خفيفة في زيت الزيتون (185 جم)", 7.50, "معلبات", "حبة")
    ),
    val quickPaymentCustomer: CustomerAccount? = null,
    val quickPaymentTransactionTotal: Double = 1850.00,
    val quickPaymentSettlementType: SettlementType = SettlementType.FULL,
    val quickPaymentMethod: PaymentMethodOption = PaymentMethodOption.CASH,
    val quickPaymentCashAmount: String = "1850.00",
    val quickPaymentDebtAmount: String = "",
    val quickPaymentNotes: String = "",
    val showUnifiedSettlementSummary: Boolean = false,
    val accounts: List<CustomerAccount> = listOf(
        CustomerAccount("1", "مؤسسة الأمل للتجارة", 4250.00, 4250.00, "+966 50 123 4567", "2026-09-03"),
        CustomerAccount("2", "خالد بن عبدالعزيز", 1850.00, 1850.00, "+966 55 987 6543", "2026-09-04"),
        CustomerAccount("3", "سوبرماركت الرياض", -3200.00, 0.00, "+966 54 444 3322", "2026-09-02"),
        CustomerAccount("4", "مركز النور للمواد الغذائية", 6700.00, 6700.00, "+966 56 111 2233", "2026-09-05"),
        CustomerAccount("5", "أحمد الشمري", 920.00, 920.00, "+966 50 777 8899", "2026-09-05")
    ),
    val transactions: List<TransactionItem> = listOf(
        TransactionItem(
            id = "t1",
            title = "دفعة نقدية",
            customerName = "مؤسسة الأمل للتجارة",
            activityType = "دفعة",
            amount = 450.00,
            isCredit = true,
            date = "2026-09-05",
            relativeTime = "منذ ساعتين",
            notes = "سند قبض نقدي"
        ),
        TransactionItem(
            id = "t2",
            title = "فاتورة مشتريات",
            customerName = "سوبرماركت الرياض",
            activityType = "مشتريات",
            amount = 1200.00,
            isCredit = false,
            date = "2026-09-05",
            relativeTime = "منذ 3 ساعات",
            notes = "معدات ومواد تموينية"
        ),
        TransactionItem(
            id = "t3",
            title = "تسجيل معاملة",
            customerName = "مركز النور للمواد الغذائية",
            activityType = "مشتريات",
            amount = 200.00,
            isCredit = true,
            date = "2026-09-05",
            relativeTime = "منذ 4 ساعات",
            notes = "كشف الحساب - عميل رقم 4"
        ),
        TransactionItem(
            id = "t4",
            title = "دفعة بنكية",
            customerName = "خالد بن عبدالعزيز",
            activityType = "دفعة",
            amount = 500.00,
            isCredit = true,
            date = "2026-09-04",
            relativeTime = "أمس",
            notes = "تحويل سريع"
        ),
        TransactionItem(
            id = "t5",
            title = "مشتريات إضافية",
            customerName = "مؤسسة الأمل للتجارة",
            activityType = "مشتريات",
            amount = 850.00,
            isCredit = false,
            date = "2026-09-03",
            relativeTime = "منذ يومين",
            notes = "بضاعة آجلة"
        ),
        TransactionItem(
            id = "t6",
            title = "دفعة ختامية",
            customerName = "أحمد الشمري",
            activityType = "دفعة",
            amount = 300.00,
            isCredit = true,
            date = "2026-09-02",
            relativeTime = "منذ 3 أيام",
            notes = "سداد مستحقات"
        )
    ),
    // Financial Activity Feed: Only ever shows two kinds of events: "Record Transaction" & "Payment"
    val notifications: List<NotificationItem> = listOf(
        NotificationItem(
            id = "n1",
            customerName = "مؤسسة الأمل للتجارة",
            transactionType = "تم استلام دفعة",
            amount = 450.00,
            timestamp = "منذ 15 دقيقة",
            isPayment = true,
            isRead = false,
            transactionId = "t1"
        ),
        NotificationItem(
            id = "n2",
            customerName = "مركز النور للمواد الغذائية",
            transactionType = "تم تسجيل معاملة",
            amount = 200.00,
            timestamp = "منذ ساعتين",
            isPayment = false,
            isRead = false,
            transactionId = "t3"
        ),
        NotificationItem(
            id = "n3",
            customerName = "سوبرماركت الرياض",
            transactionType = "تم تسجيل معاملة",
            amount = 1200.00,
            timestamp = "منذ 3 ساعات",
            isPayment = false,
            isRead = false,
            transactionId = "t2"
        ),
        NotificationItem(
            id = "n4",
            customerName = "خالد بن عبدالعزيز",
            transactionType = "تم استلام دفعة",
            amount = 500.00,
            timestamp = "أمس 05:40 م",
            isPayment = true,
            isRead = true,
            transactionId = "t4"
        ),
        NotificationItem(
            id = "n5",
            customerName = "مؤسسة الأمل للتجارة",
            transactionType = "تم تسجيل معاملة",
            amount = 850.00,
            timestamp = "منذ يومين",
            isPayment = false,
            isRead = true,
            transactionId = "t5"
        ),
        NotificationItem(
            id = "n6",
            customerName = "أحمد الشمري",
            transactionType = "تم استلام دفعة",
            amount = 300.00,
            timestamp = "منذ 3 أيام",
            isPayment = true,
            isRead = true,
            transactionId = "t6"
        ),
        NotificationItem(
            id = "n7",
            customerName = "مركز النور للمواد الغذائية",
            transactionType = "تم استلام دفعة",
            amount = 1500.00,
            timestamp = "منذ 4 أيام",
            isPayment = true,
            isRead = true
        ),
        NotificationItem(
            id = "n8",
            customerName = "خالد بن عبدالعزيز",
            transactionType = "تم تسجيل معاملة",
            amount = 620.00,
            timestamp = "منذ 5 أيام",
            isPayment = false,
            isRead = true
        )
    )
) {
    val unreadNotificationsCount: Int
        get() = notifications.count { !it.isRead }

    val totalBalance: Double
        get() = accounts.sumOf { it.balance }

    val totalReceivables: Double
        get() = accounts.filter { it.balance > 0 }.sumOf { it.balance }

    val totalPayables: Double
        get() = accounts.filter { it.balance < 0 }.sumOf { -it.balance }

    // Shop-wide metrics
    val shopTotalBalance: Double
        get() = totalBalance

    val shopTotalDebt: Double
        get() = totalReceivables

    val shopTodayTransactionsCount: Int
        get() = transactions.count { it.relativeTime.contains("ساع") || it.date == "2026-09-05" }

    // Effective metrics depending on whether a customer is selected
    val displayTotalBalance: Double
        get() = selectedCustomer?.balance ?: shopTotalBalance

    val displayTotalDebt: Double
        get() = selectedCustomer?.totalDebt ?: shopTotalDebt

    val displayTransactionsCount: Int
        get() = if (selectedCustomer != null) {
            scopedTransactions.size
        } else {
            shopTodayTransactionsCount
        }

    // Scoped activities: if customer selected, show only that customer's history from the beginning
    val scopedTransactions: List<TransactionItem>
        get() {
            val list = if (selectedCustomer != null) {
                transactions.filter { it.customerName == selectedCustomer.customerName }
            } else {
                transactions
            }
            return list
        }

    // Filtered customers matching search query (Home screen)
    val matchingCustomers: List<CustomerAccount>
        get() {
            if (searchQuery.isBlank()) return emptyList()
            return accounts.filter {
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery)
            }
        }

    // Filtered customers for Accounts screen based on accountsSearchQuery & accountsFilter
    val filteredAccounts: List<CustomerAccount>
        get() {
            val query = accountsSearchQuery.trim().lowercase()
            val baseList = when (accountsFilter) {
                AccountFilter.ALL -> accounts
                AccountFilter.HAS_DEBT -> accounts.filter { it.balance > 0 }
                AccountFilter.RECENTLY_ACTIVE -> accounts.filter {
                    it.lastTransactionDate == "2026-09-04" || it.lastTransactionDate == "2026-09-05"
                }
            }
            return if (query.isEmpty()) {
                baseList
            } else {
                baseList.filter {
                    it.customerName.lowercase().contains(query) || it.phone.lowercase().contains(query)
                }
            }
        }

    val filteredProducts: List<ProductItem>
        get() {
            val query = purchasesSearchQuery.trim().lowercase()
            return if (query.isEmpty()) {
                products
            } else {
                products.filter {
                    it.name.lowercase().contains(query) || it.category.lowercase().contains(query)
                }
            }
        }

    val cartItemsCount: Int
        get() = cart.sumOf { it.quantity }

    val cartTotalAmount: Double
        get() = cart.sumOf { it.product.price * it.quantity }

    val canCompletePurchase: Boolean
        get() = purchasesCustomer != null && cart.isNotEmpty()

    val quickPaymentParsedCash: Double
        get() = quickPaymentCashAmount.toDoubleOrNull() ?: 0.0

    val quickPaymentParsedDebt: Double
        get() = quickPaymentDebtAmount.toDoubleOrNull() ?: 0.0

    val quickPaymentCurrentEnteredTotal: Double
        get() = when (quickPaymentSettlementType) {
            SettlementType.FULL -> when (quickPaymentMethod) {
                PaymentMethodOption.CASH -> quickPaymentParsedCash
                PaymentMethodOption.DEBT -> quickPaymentParsedDebt
            }
            SettlementType.PARTIAL -> quickPaymentParsedCash + quickPaymentParsedDebt
        }

    val isQuickPaymentAmountExceeded: Boolean
        get() = quickPaymentCurrentEnteredTotal > (quickPaymentTransactionTotal + 0.001)

    val isQuickPaymentValid: Boolean
        get() = quickPaymentCustomer != null &&
                quickPaymentCurrentEnteredTotal > 0.0 &&
                !isQuickPaymentAmountExceeded
}

class StoreViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    fun setLanguage(language: LanguageMode) {
        _uiState.update { it.copy(languageMode = language) }
    }

    fun setTheme(theme: AppThemeMode) {
        _uiState.update { it.copy(themeMode = theme) }
    }

    fun navigateTo(destination: NavDestination) {
        _uiState.update {
            it.copy(
                previousDestination = if (it.currentDestination != destination && it.currentDestination != NavDestination.NOTIFICATIONS) {
                    it.currentDestination
                } else it.previousDestination,
                currentDestination = destination
            )
        }
    }

    fun navigateBack() {
        _uiState.update {
            it.copy(currentDestination = it.previousDestination)
        }
    }

    fun markNotificationsAsRead() {
        _uiState.update { current ->
            current.copy(notifications = current.notifications.map { it.copy(isRead = true) })
        }
    }

    fun onNotificationClick(notification: NotificationItem) {
        val matchedCustomer = _uiState.value.accounts.find { it.customerName == notification.customerName }
        _uiState.update { current ->
            current.copy(
                selectedCustomer = matchedCustomer,
                previousDestination = NavDestination.NOTIFICATIONS,
                currentDestination = NavDestination.ACCOUNTS // Navigates to Account Statement with customer pre-selected
            )
        }
    }

    fun openActionSheet() {
        _uiState.update { it.copy(showActionSheet = true) }
    }

    fun closeActionSheet() {
        _uiState.update { it.copy(showActionSheet = false) }
    }

    fun openRecordTransaction() {
        _uiState.update { it.copy(showActionSheet = false, showRecordTransactionSheet = true) }
    }

    fun closeRecordTransaction() {
        _uiState.update { it.copy(showRecordTransactionSheet = false) }
    }

    fun openQuickPayment() {
        _uiState.update { current ->
            val customer = current.selectedCustomer ?: current.accounts.getOrNull(1) ?: current.accounts.firstOrNull()
            val total = if ((customer?.balance ?: 0.0) > 0) (customer?.balance ?: 1850.00) else 1850.00
            current.copy(
                showActionSheet = false,
                previousDestination = current.currentDestination,
                currentDestination = NavDestination.QUICK_PAYMENT,
                quickPaymentCustomer = customer,
                quickPaymentTransactionTotal = total,
                quickPaymentSettlementType = SettlementType.FULL,
                quickPaymentMethod = PaymentMethodOption.CASH,
                quickPaymentCashAmount = String.format(java.util.Locale.US, "%.2f", total),
                quickPaymentDebtAmount = "",
                quickPaymentNotes = ""
            )
        }
    }

    fun closeQuickPayment() {
        _uiState.update { it.copy(showQuickPaymentSheet = false) }
    }

    fun toggleEmptyStateDemo() {
        _uiState.update { it.copy(showEmptyStateDemo = !it.showEmptyStateDemo) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearching = query.isNotBlank()) }
    }

    fun selectCustomer(customer: CustomerAccount) {
        _uiState.update {
            it.copy(
                selectedCustomer = customer,
                searchQuery = "",
                isSearching = false
            )
        }
    }

    fun clearSelectedCustomer() {
        _uiState.update {
            it.copy(
                selectedCustomer = null,
                searchQuery = "",
                isSearching = false
            )
        }
    }

    fun setPeriod(period: PeriodFilter) {
        _uiState.update { it.copy(selectedPeriod = period) }
    }

    fun recordTransaction(customerName: String, amount: Double, notes: String) {
        val txId = "t${System.currentTimeMillis()}"
        val isAr = _uiState.value.languageMode == LanguageMode.ARABIC
        val newTx = TransactionItem(
            id = txId,
            title = if (isAr) "معاملة جديدة" else "New Transaction",
            customerName = customerName,
            activityType = if (isAr) "مشتريات" else "Purchase",
            amount = amount,
            isCredit = true,
            date = "2026-09-05",
            relativeTime = if (isAr) "الآن" else "Just now",
            notes = notes
        )
        val newNotif = NotificationItem(
            id = "n${System.currentTimeMillis()}",
            customerName = customerName,
            transactionType = if (isAr) StoreStrings.NOTIF_RECORD_TRANSACTION_AR else StoreStrings.NOTIF_RECORD_TRANSACTION_EN,
            amount = amount,
            timestamp = if (isAr) "الآن" else "Just now",
            isPayment = false,
            isRead = false,
            transactionId = txId
        )
        _uiState.update { current ->
            current.copy(
                transactions = listOf(newTx) + current.transactions,
                notifications = listOf(newNotif) + current.notifications,
                showRecordTransactionSheet = false
            )
        }
    }

    fun quickPayment(customerName: String, amount: Double, notes: String) {
        val txId = "t${System.currentTimeMillis()}"
        val isAr = _uiState.value.languageMode == LanguageMode.ARABIC
        val newTx = TransactionItem(
            id = txId,
            title = if (isAr) "دفع سريع" else "Quick Payment",
            customerName = customerName,
            activityType = if (isAr) "دفعة" else "Payment",
            amount = amount,
            isCredit = false,
            date = "2026-09-05",
            relativeTime = if (isAr) "الآن" else "Just now",
            notes = notes
        )
        val newNotif = NotificationItem(
            id = "n${System.currentTimeMillis()}",
            customerName = customerName,
            transactionType = if (isAr) StoreStrings.NOTIF_PAYMENT_AR else StoreStrings.NOTIF_PAYMENT_EN,
            amount = amount,
            timestamp = if (isAr) "الآن" else "Just now",
            isPayment = true,
            isRead = false,
            transactionId = txId
        )
        _uiState.update { current ->
            current.copy(
                transactions = listOf(newTx) + current.transactions,
                notifications = listOf(newNotif) + current.notifications,
                showQuickPaymentSheet = false
            )
        }
    }

    fun setSampleNotificationCount(count: Int) {
        val isAr = _uiState.value.languageMode == LanguageMode.ARABIC
        val items = (1..count).map { idx ->
            val isEven = idx % 2 == 0
            NotificationItem(
                id = "n$idx",
                customerName = if (isEven) "مؤسسة الأمل للتجارة" else "مركز النور للمواد الغذائية",
                transactionType = if (isEven) {
                    if (isAr) StoreStrings.NOTIF_PAYMENT_AR else StoreStrings.NOTIF_PAYMENT_EN
                } else {
                    if (isAr) StoreStrings.NOTIF_RECORD_TRANSACTION_AR else StoreStrings.NOTIF_RECORD_TRANSACTION_EN
                },
                amount = 250.0 * idx,
                timestamp = "منذ ${idx * 10} دقيقة",
                isPayment = isEven,
                isRead = false
            )
        }
        _uiState.update { it.copy(notifications = items) }
    }

    fun setAccountsSearchQuery(query: String) {
        _uiState.update { it.copy(accountsSearchQuery = query) }
    }

    fun setAccountsFilter(filter: AccountFilter) {
        _uiState.update { it.copy(accountsFilter = filter) }
    }

    fun openAddCustomerDialog() {
        _uiState.update { it.copy(showAddCustomerDialog = true) }
    }

    fun closeAddCustomerDialog() {
        _uiState.update { it.copy(showAddCustomerDialog = false) }
    }

    fun addCustomer(name: String, phone: String, initialDebt: Double) {
        val newId = System.currentTimeMillis().toString()
        val newCustomer = CustomerAccount(
            id = newId,
            customerName = name.trim(),
            balance = initialDebt,
            totalDebt = if (initialDebt > 0) initialDebt else 0.0,
            phone = phone.trim().ifEmpty { "+966 50 000 0000" },
            lastTransactionDate = "2026-09-05"
        )
        _uiState.update { current ->
            current.copy(
                accounts = listOf(newCustomer) + current.accounts,
                showAddCustomerDialog = false
            )
        }
    }

    fun openCustomerDetails(customer: CustomerAccount) {
        _uiState.update {
            it.copy(
                selectedCustomerDetails = customer,
                selectedCustomer = customer,
                previousDestination = it.currentDestination,
                currentDestination = NavDestination.CUSTOMER_DETAILS
            )
        }
    }

    fun closeCustomerDetails() {
        _uiState.update {
            it.copy(
                selectedCustomerDetails = null,
                currentDestination = if (it.previousDestination != NavDestination.CUSTOMER_DETAILS) it.previousDestination else NavDestination.ACCOUNTS
            )
        }
    }

    fun onRecordTransactionForCustomer(customer: CustomerAccount) {
        _uiState.update {
            it.copy(
                selectedCustomer = customer,
                purchasesCustomer = customer,
                previousDestination = it.currentDestination,
                currentDestination = NavDestination.PURCHASES
            )
        }
    }

    fun onAccountStatementForCustomer(customer: CustomerAccount) {
        _uiState.update {
            it.copy(
                selectedCustomer = customer,
                previousDestination = it.currentDestination,
                currentDestination = NavDestination.ANALYSIS_CENTER
            )
        }
    }

    fun onPaymentForCustomer(customer: CustomerAccount) {
        _uiState.update { current ->
            val total = if (customer.balance > 0) customer.balance else 1850.00
            current.copy(
                selectedCustomer = customer,
                quickPaymentCustomer = customer,
                quickPaymentTransactionTotal = total,
                quickPaymentSettlementType = SettlementType.FULL,
                quickPaymentMethod = PaymentMethodOption.CASH,
                quickPaymentCashAmount = String.format(java.util.Locale.US, "%.2f", total),
                quickPaymentDebtAmount = "",
                quickPaymentNotes = "",
                showQuickPaymentSheet = true,
                previousDestination = current.currentDestination,
                currentDestination = NavDestination.QUICK_PAYMENT
            )
        }
    }

    fun editCustomer(customer: CustomerAccount, newName: String, newPhone: String) {
        _uiState.update { current ->
            val updatedAccounts = current.accounts.map {
                if (it.id == customer.id) {
                    it.copy(customerName = newName, phone = newPhone)
                } else it
            }
            val updatedDetails = if (current.selectedCustomerDetails?.id == customer.id) {
                current.selectedCustomerDetails.copy(customerName = newName, phone = newPhone)
            } else current.selectedCustomerDetails

            current.copy(
                accounts = updatedAccounts,
                selectedCustomerDetails = updatedDetails
            )
        }
    }

    fun archiveCustomer(customer: CustomerAccount) {
        _uiState.update { current ->
            current.copy(
                accounts = current.accounts.filter { it.id != customer.id },
                selectedCustomerDetails = null,
                currentDestination = NavDestination.ACCOUNTS
            )
        }
    }

    // Purchases screen actions
    fun setPurchasesSearchQuery(query: String) {
        _uiState.update { it.copy(purchasesSearchQuery = query) }
    }

    fun addToCart(product: ProductItem) {
        _uiState.update { current ->
            val existingIndex = current.cart.indexOfFirst { it.product.id == product.id }
            val updatedCart = if (existingIndex >= 0) {
                current.cart.mapIndexed { index, cartItem ->
                    if (index == existingIndex) cartItem.copy(quantity = cartItem.quantity + 1) else cartItem
                }
            } else {
                current.cart + CartItem(product = product, quantity = 1)
            }
            current.copy(cart = updatedCart)
        }
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        _uiState.update { current ->
            val updatedCart = current.cart.mapNotNull { item ->
                if (item.product.id == productId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else {
                    item
                }
            }
            current.copy(cart = updatedCart)
        }
    }

    fun removeFromCart(productId: String) {
        _uiState.update { current ->
            current.copy(cart = current.cart.filter { it.product.id != productId })
        }
    }

    fun clearCart() {
        _uiState.update { it.copy(cart = emptyList(), isCartExpanded = false) }
    }

    fun toggleCartExpanded() {
        _uiState.update { it.copy(isCartExpanded = !it.isCartExpanded) }
    }

    fun setCartExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isCartExpanded = expanded) }
    }

    fun setPurchasesCustomer(customer: CustomerAccount?) {
        _uiState.update { it.copy(purchasesCustomer = customer) }
    }

    fun openCustomerPickerForPurchases() {
        _uiState.update { it.copy(showCustomerPickerForPurchases = true) }
    }

    fun closeCustomerPickerForPurchases() {
        _uiState.update { it.copy(showCustomerPickerForPurchases = false) }
    }

    fun selectCustomerForPurchases(customer: CustomerAccount) {
        _uiState.update {
            it.copy(
                purchasesCustomer = customer,
                showCustomerPickerForPurchases = false
            )
        }
    }

    fun clearPurchasesCustomer() {
        _uiState.update { it.copy(purchasesCustomer = null) }
    }

    fun onCompletePurchaseTransaction() {
        _uiState.update { it.copy(showSettlementConfirmation = true) }
    }

    fun dismissSettlementConfirmation() {
        _uiState.update { it.copy(showSettlementConfirmation = false) }
    }

    // Quick Payment screen methods
    fun setQuickPaymentCustomer(customer: CustomerAccount) {
        _uiState.update { current ->
            val total = if (customer.balance > 0) customer.balance else 1850.00
            val cash = if (current.quickPaymentSettlementType == SettlementType.FULL && current.quickPaymentMethod == PaymentMethodOption.CASH) {
                String.format(java.util.Locale.US, "%.2f", total)
            } else current.quickPaymentCashAmount
            val debt = if (current.quickPaymentSettlementType == SettlementType.FULL && current.quickPaymentMethod == PaymentMethodOption.DEBT) {
                String.format(java.util.Locale.US, "%.2f", total)
            } else current.quickPaymentDebtAmount

            current.copy(
                quickPaymentCustomer = customer,
                quickPaymentTransactionTotal = total,
                quickPaymentCashAmount = cash,
                quickPaymentDebtAmount = debt
            )
        }
    }

    fun setQuickPaymentSettlementType(type: SettlementType) {
        _uiState.update { current ->
            val total = current.quickPaymentTransactionTotal
            if (type == SettlementType.FULL) {
                val cash = if (current.quickPaymentMethod == PaymentMethodOption.CASH) String.format(java.util.Locale.US, "%.2f", total) else ""
                val debt = if (current.quickPaymentMethod == PaymentMethodOption.DEBT) String.format(java.util.Locale.US, "%.2f", total) else ""
                current.copy(
                    quickPaymentSettlementType = type,
                    quickPaymentCashAmount = cash,
                    quickPaymentDebtAmount = debt
                )
            } else {
                val half = total / 2.0
                current.copy(
                    quickPaymentSettlementType = type,
                    quickPaymentCashAmount = String.format(java.util.Locale.US, "%.2f", half),
                    quickPaymentDebtAmount = String.format(java.util.Locale.US, "%.2f", total - half)
                )
            }
        }
    }

    fun setQuickPaymentMethod(method: PaymentMethodOption) {
        _uiState.update { current ->
            val total = current.quickPaymentTransactionTotal
            current.copy(
                quickPaymentMethod = method,
                quickPaymentCashAmount = if (method == PaymentMethodOption.CASH) String.format(java.util.Locale.US, "%.2f", total) else "",
                quickPaymentDebtAmount = if (method == PaymentMethodOption.DEBT) String.format(java.util.Locale.US, "%.2f", total) else ""
            )
        }
    }

    fun setQuickPaymentCashAmount(amount: String) {
        _uiState.update { it.copy(quickPaymentCashAmount = amount) }
    }

    fun setQuickPaymentDebtAmount(amount: String) {
        _uiState.update { it.copy(quickPaymentDebtAmount = amount) }
    }

    fun setQuickPaymentNotes(notes: String) {
        _uiState.update { it.copy(quickPaymentNotes = notes) }
    }

    fun onCompleteQuickPayment() {
        _uiState.update { it.copy(showUnifiedSettlementSummary = true) }
    }

    fun dismissUnifiedSettlementSummary() {
        _uiState.update { it.copy(showUnifiedSettlementSummary = false) }
    }

    fun confirmSettlement(cash: Double, debt: Double, notes: String) {
        _uiState.update { current ->
            current.copy(
                showUnifiedSettlementSummary = false,
                showSettlementConfirmation = false,
                cart = emptyList(),
                currentDestination = NavDestination.HOME
            )
        }
    }
}
