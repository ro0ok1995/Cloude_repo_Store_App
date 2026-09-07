package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.LanguageMode
import com.example.model.NavDestination
import com.example.model.StoreStrings
import com.example.ui.components.GlobalBottomBar
import com.example.ui.components.GlobalDrawerContent
import com.example.ui.components.GlobalTopBar
import com.example.ui.components.PlusActionSheet
import com.example.ui.components.QuickPaymentSheet
import com.example.ui.components.RecordTransactionSheet
import com.example.ui.components.UnifiedSettlementSheet
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.AnalysisCenterScreen
import com.example.ui.screens.AppSettingsScreen
import com.example.ui.screens.CustomerDetailsScreen
import com.example.ui.screens.DataCenterScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoreMenuItemId
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PurchasesScreen
import com.example.ui.screens.QuickPaymentScreen
import com.example.ui.screens.StoreInformationScreen
import com.example.ui.theme.SmallStoreTheme
import com.example.viewmodel.StoreViewModel
import kotlinx.coroutines.launch

/**
 * SmallStore Root Application Composable
 * Locks in all global rules:
 * - APP IDENTITY: SmallStore, Android mobile app, portrait, single-column layout
 * - LANGUAGE DIRECTION: Arabic-first (RTL) with full mirrored LTR (English) variant
 * - VISUAL TONE: Professional, clean, uncluttered. Default neutral light theme with optional Purple & Gold
 * - GLOBAL NAVIGATION SHELL:
 *   - Bottom bar with exactly 5 items (Home | Accounts | + | Analysis Center | More)
 *   - Central elevated '+' button opening action sheet with exactly 2 choices: 'Record Transaction' & 'Quick Payment'
 *   - Global drawer from top bar with full app navigation (Home, Accounts, Purchases, Analysis Center, Notifications, More/Settings)
 *   - Top bar: drawer icon on one side, title, bell icon with badge on the other side
 * - NOTIFICATION BADGE RULE: 0 = none, 1-9 = exact count, 10+ = '+9'
 * - EXACT TERMINOLOGY: "Record Transaction", "Quick Payment", "Purchases", "Account Statement", "Settlement", "Customer Details", "Analysis Center"
 * - COMPONENT STYLE: Rounded cards (14-16px), soft shadow, 1 primary filled button, sticky dialog actions, simple empty states
 */
@Composable
fun SmallStoreApp(
    viewModel: StoreViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val isArabic = uiState.languageMode == LanguageMode.ARABIC
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    SmallStoreTheme(themeMode = uiState.themeMode) {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    GlobalDrawerContent(
                        currentDestination = uiState.currentDestination,
                        languageMode = uiState.languageMode,
                        unreadNotificationsCount = uiState.unreadNotificationsCount,
                        onSelectDestination = { destination ->
                            viewModel.navigateTo(destination)
                            coroutineScope.launch { drawerState.close() }
                        },
                        onToggleLanguage = { newLang ->
                            viewModel.setLanguage(newLang)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                }
            ) {
                val screenTitle = when (uiState.currentDestination) {
                    NavDestination.HOME -> if (isArabic) StoreStrings.HOME_AR else StoreStrings.HOME_EN
                    NavDestination.ACCOUNTS -> if (isArabic) StoreStrings.ACCOUNTS_AR else StoreStrings.ACCOUNTS_EN
                    NavDestination.PURCHASES -> if (isArabic) StoreStrings.PURCHASES_AR else StoreStrings.PURCHASES_EN
                    NavDestination.ANALYSIS_CENTER -> if (isArabic) StoreStrings.ANALYSIS_CENTER_AR else StoreStrings.ANALYSIS_CENTER_EN
                    NavDestination.NOTIFICATIONS -> if (isArabic) StoreStrings.NOTIFICATIONS_AR else StoreStrings.NOTIFICATIONS_EN
                    NavDestination.MORE_SETTINGS -> if (isArabic) StoreStrings.MORE_AR else StoreStrings.MORE_EN
                    NavDestination.STORE_INFORMATION -> if (isArabic) StoreStrings.STORE_INFO_AR else StoreStrings.STORE_INFO_EN
                    NavDestination.APP_SETTINGS -> if (isArabic) StoreStrings.APP_SETTINGS_AR else StoreStrings.APP_SETTINGS_EN
                    NavDestination.DATA_CENTER -> if (isArabic) StoreStrings.DATA_CENTER_AR else StoreStrings.DATA_CENTER_EN
                    NavDestination.ABOUT -> if (isArabic) StoreStrings.ABOUT_AR else StoreStrings.ABOUT_EN
                    NavDestination.CUSTOMER_DETAILS -> uiState.selectedCustomerDetails?.customerName ?: (if (isArabic) StoreStrings.CUSTOMER_DETAILS_AR else StoreStrings.CUSTOMER_DETAILS_EN)
                    NavDestination.QUICK_PAYMENT -> if (isArabic) StoreStrings.QUICK_PAYMENT_AR else StoreStrings.QUICK_PAYMENT_EN
                }

                Scaffold(
                    topBar = {
                        if (uiState.currentDestination != NavDestination.NOTIFICATIONS &&
                            uiState.currentDestination != NavDestination.CUSTOMER_DETAILS &&
                            uiState.currentDestination != NavDestination.PURCHASES &&
                            uiState.currentDestination != NavDestination.QUICK_PAYMENT &&
                            uiState.currentDestination != NavDestination.STORE_INFORMATION &&
                            uiState.currentDestination != NavDestination.APP_SETTINGS &&
                            uiState.currentDestination != NavDestination.DATA_CENTER &&
                            uiState.currentDestination != NavDestination.ABOUT
                        ) {
                            GlobalTopBar(
                                title = screenTitle,
                                unreadNotificationsCount = uiState.unreadNotificationsCount,
                                onDrawerClick = {
                                    coroutineScope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                },
                                onNotificationsClick = {
                                    viewModel.navigateTo(NavDestination.NOTIFICATIONS)
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (uiState.currentDestination != NavDestination.PURCHASES &&
                            uiState.currentDestination != NavDestination.QUICK_PAYMENT &&
                            uiState.currentDestination != NavDestination.STORE_INFORMATION &&
                            uiState.currentDestination != NavDestination.APP_SETTINGS &&
                            uiState.currentDestination != NavDestination.DATA_CENTER &&
                            uiState.currentDestination != NavDestination.ABOUT
                        ) {
                            GlobalBottomBar(
                                currentDestination = uiState.currentDestination,
                                languageMode = uiState.languageMode,
                                onNavigate = { destination ->
                                    viewModel.navigateTo(destination)
                                },
                                onPlusClick = {
                                    viewModel.openActionSheet()
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.currentDestination) {
                            NavDestination.HOME -> HomeScreen(
                                totalBalance = uiState.displayTotalBalance,
                                totalDebt = uiState.displayTotalDebt,
                                transactionsCount = uiState.displayTransactionsCount,
                                transactions = uiState.scopedTransactions,
                                matchingCustomers = uiState.matchingCustomers,
                                selectedCustomer = uiState.selectedCustomer,
                                searchQuery = uiState.searchQuery,
                                selectedPeriod = uiState.selectedPeriod,
                                languageMode = uiState.languageMode,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                onSelectCustomer = { viewModel.selectCustomer(it) },
                                onClearSelectedCustomer = { viewModel.clearSelectedCustomer() },
                                onSelectPeriod = { viewModel.setPeriod(it) }
                            )

                            NavDestination.ACCOUNTS -> AccountsScreen(
                                accounts = uiState.filteredAccounts,
                                searchQuery = uiState.accountsSearchQuery,
                                filter = uiState.accountsFilter,
                                selectedCustomerDetails = uiState.selectedCustomerDetails,
                                showAddCustomerDialog = uiState.showAddCustomerDialog,
                                languageMode = uiState.languageMode,
                                onSearchQueryChange = { viewModel.setAccountsSearchQuery(it) },
                                onFilterChange = { viewModel.setAccountsFilter(it) },
                                onCustomerClick = { viewModel.openCustomerDetails(it) },
                                onCloseCustomerDetails = { viewModel.closeCustomerDetails() },
                                onOpenAddCustomerDialog = { viewModel.openAddCustomerDialog() },
                                onCloseAddCustomerDialog = { viewModel.closeAddCustomerDialog() },
                                onAddCustomer = { name, phone, initialDebt ->
                                    viewModel.addCustomer(name, phone, initialDebt)
                                }
                            )

                            NavDestination.PURCHASES -> PurchasesScreen(
                                customer = uiState.purchasesCustomer,
                                allCustomers = uiState.accounts,
                                products = uiState.products,
                                cart = uiState.cart,
                                searchQuery = uiState.purchasesSearchQuery,
                                isCartExpanded = uiState.isCartExpanded,
                                languageMode = uiState.languageMode,
                                onBackClick = { viewModel.navigateBack() },
                                onSearchQueryChange = { viewModel.setPurchasesSearchQuery(it) },
                                onAddToCart = { viewModel.addToCart(it) },
                                onUpdateCartQuantity = { prodId, delta -> viewModel.updateCartQuantity(prodId, delta) },
                                onRemoveFromCart = { viewModel.removeFromCart(it) },
                                onToggleCartExpanded = { viewModel.toggleCartExpanded() },
                                onSelectCustomer = { viewModel.selectCustomerForPurchases(it) },
                                onClearCustomer = { viewModel.clearPurchasesCustomer() },
                                onCompleteTransaction = { viewModel.onCompletePurchaseTransaction() }
                            )

                            NavDestination.ANALYSIS_CENTER -> AnalysisCenterScreen(
                                languageMode = uiState.languageMode,
                                selectedCustomer = uiState.selectedCustomer,
                                totalBalance = uiState.totalBalance,
                                totalReceivables = uiState.totalReceivables,
                                totalPayables = uiState.totalPayables,
                                transactions = uiState.transactions,
                                accounts = uiState.accounts,
                                onClearCustomer = { viewModel.clearSelectedCustomer() }
                            )

                            NavDestination.NOTIFICATIONS -> NotificationsScreen(
                                notifications = uiState.notifications,
                                languageMode = uiState.languageMode,
                                onBackClick = { viewModel.navigateBack() },
                                onNotificationClick = { viewModel.onNotificationClick(it) },
                                onViewNotifications = { viewModel.markNotificationsAsRead() }
                            )

                            NavDestination.MORE_SETTINGS -> MoreScreen(
                                languageMode = uiState.languageMode,
                                onMenuItemClick = { itemId ->
                                    when (itemId) {
                                        MoreMenuItemId.STORE_INFO -> viewModel.navigateTo(NavDestination.STORE_INFORMATION)
                                        MoreMenuItemId.APP_SETTINGS -> viewModel.navigateTo(NavDestination.APP_SETTINGS)
                                        MoreMenuItemId.DATA_CENTER -> viewModel.navigateTo(NavDestination.DATA_CENTER)
                                        MoreMenuItemId.ABOUT -> viewModel.navigateTo(NavDestination.ABOUT)
                                    }
                                }
                            )

                            NavDestination.STORE_INFORMATION -> StoreInformationScreen(
                                languageMode = uiState.languageMode,
                                onBackClick = { viewModel.navigateBack() }
                            )

                            NavDestination.APP_SETTINGS -> AppSettingsScreen(
                                currentLanguage = uiState.languageMode,
                                currentTheme = uiState.themeMode,
                                onBackClick = { viewModel.navigateBack() },
                                onLanguageChange = { viewModel.setLanguage(it) },
                                onThemeChange = { viewModel.setTheme(it) }
                            )

                            NavDestination.DATA_CENTER -> DataCenterScreen(
                                languageMode = uiState.languageMode,
                                onBackClick = { viewModel.navigateBack() }
                            )

                            NavDestination.ABOUT -> AboutScreen(
                                languageMode = uiState.languageMode,
                                onBackClick = { viewModel.navigateBack() }
                            )

                            NavDestination.CUSTOMER_DETAILS -> CustomerDetailsScreen(
                                customer = uiState.selectedCustomerDetails,
                                transactions = uiState.transactions,
                                languageMode = uiState.languageMode,
                                onBackClick = { viewModel.closeCustomerDetails() },
                                onRecordTransaction = { viewModel.onRecordTransactionForCustomer(it) },
                                onAccountStatement = { viewModel.onAccountStatementForCustomer(it) },
                                onPayment = { viewModel.onPaymentForCustomer(it) },
                                onEditCustomer = { cust, name, phone -> viewModel.editCustomer(cust, name, phone) },
                                onArchiveCustomer = { viewModel.archiveCustomer(it) }
                            )

                            NavDestination.QUICK_PAYMENT -> QuickPaymentScreen(
                                customer = uiState.quickPaymentCustomer,
                                allCustomers = uiState.accounts,
                                transactionTotal = uiState.quickPaymentTransactionTotal,
                                settlementType = uiState.quickPaymentSettlementType,
                                paymentMethod = uiState.quickPaymentMethod,
                                cashAmount = uiState.quickPaymentCashAmount,
                                debtAmount = uiState.quickPaymentDebtAmount,
                                notes = uiState.quickPaymentNotes,
                                languageMode = uiState.languageMode,
                                onBackClick = { viewModel.navigateBack() },
                                onCustomerChange = { viewModel.setQuickPaymentCustomer(it) },
                                onSettlementTypeChange = { viewModel.setQuickPaymentSettlementType(it) },
                                onPaymentMethodChange = { viewModel.setQuickPaymentMethod(it) },
                                onCashAmountChange = { viewModel.setQuickPaymentCashAmount(it) },
                                onDebtAmountChange = { viewModel.setQuickPaymentDebtAmount(it) },
                                onNotesChange = { viewModel.setQuickPaymentNotes(it) },
                                onComplete = { viewModel.onCompleteQuickPayment() }
                            )
                        }
                    }
                }

                // Global Action Sheet for "+" Button:
                // Exactly two choices: "Record Transaction" and "Quick Payment".
                PlusActionSheet(
                    isOpen = uiState.showActionSheet,
                    languageMode = uiState.languageMode,
                    onDismiss = { viewModel.closeActionSheet() },
                    onRecordTransactionClick = { viewModel.openRecordTransaction() },
                    onQuickPaymentClick = { viewModel.openQuickPayment() }
                )

                // Record Transaction Form Bottom Sheet
                RecordTransactionSheet(
                    isOpen = uiState.showRecordTransactionSheet,
                    languageMode = uiState.languageMode,
                    onDismiss = { viewModel.closeRecordTransaction() },
                    onSubmit = { customer, amount, notes ->
                        viewModel.recordTransaction(customer, amount, notes)
                    }
                )

                // Quick Payment Form Bottom Sheet
                QuickPaymentSheet(
                    isOpen = uiState.showQuickPaymentSheet,
                    languageMode = uiState.languageMode,
                    onDismiss = { viewModel.closeQuickPayment() },
                    onSubmit = { customer, amount, notes ->
                        viewModel.quickPayment(customer, amount, notes)
                    }
                )

                // Unified Settlement Sheet (Generic, reusable dialog)
                val isSettlementOpen = uiState.showUnifiedSettlementSummary || uiState.showSettlementConfirmation
                val cartTotal = uiState.cart.sumOf { it.product.price * it.quantity }
                val settlementTotal = when {
                    uiState.showSettlementConfirmation && cartTotal > 0 -> cartTotal
                    uiState.showUnifiedSettlementSummary -> uiState.quickPaymentTransactionTotal
                    else -> 100.0 // Worked example default
                }
                val defaultCash = if (uiState.showUnifiedSettlementSummary && uiState.quickPaymentCashAmount.isNotBlank()) {
                    uiState.quickPaymentCashAmount
                } else "50"
                val defaultDebt = if (uiState.showUnifiedSettlementSummary && uiState.quickPaymentDebtAmount.isNotBlank()) {
                    uiState.quickPaymentDebtAmount
                } else "40"

                UnifiedSettlementSheet(
                    isOpen = isSettlementOpen,
                    languageMode = uiState.languageMode,
                    transactionTotal = settlementTotal,
                    initialCashAmount = defaultCash,
                    initialDebtAmount = defaultDebt,
                    initialNotes = uiState.quickPaymentNotes,
                    onDismiss = {
                        viewModel.dismissUnifiedSettlementSummary()
                        viewModel.dismissSettlementConfirmation()
                    },
                    onComplete = { cash, debt, notes ->
                        viewModel.confirmSettlement(cash, debt, notes)
                    }
                )
            }
        }
    }
}
