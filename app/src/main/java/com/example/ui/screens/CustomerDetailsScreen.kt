package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.StoreStrings
import com.example.model.TransactionItem
import com.example.ui.components.SimpleEmptyState
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * SmallStore CUSTOMER DETAILS Screen
 *
 * Rules:
 * - TOP BAR: Back arrow + customer's name as the title.
 * - CUSTOMER SUMMARY HEADER:
 *   - A header card with: customer name, phone number, and current balance
 *     (clearly shown, e.g., "Owes 150" or "Settled").
 * - UNIFIED ACTIONS:
 *   - Exactly three prominent action buttons/tiles in a row (or stacked on very small screens):
 *     "Record Transaction", "Account Statement", "Payment".
 *     These are the ONLY actions on this screen besides basic edit/delete for the customer.
 *   - Tapping "Record Transaction" leads into the Purchases flow with this customer already pre-selected.
 *   - Tapping "Account Statement" leads into Analysis Center -> Account Statement with this customer pre-selected.
 *   - Tapping "Payment" leads into the Quick Payment flow with this customer pre-selected.
 * - RECENT HISTORY:
 *   - Below the actions, a short "Recent Activity" list scoped only to this customer
 *     (same row style as Home's Latest Activities), newest first.
 * - SECONDARY OPTIONS:
 *   - A small overflow menu (three-dot icon) in the top bar for "Edit customer" and "Archive customer" only.
 *     No "Delete permanently" here.
 * - CONSTRAINTS:
 *   - No tabs, charts, or extra statistics on this screen.
 * - Default: simple light theme, RTL Arabic layout with plausible Arabic customer name ("خالد بن عبدالعزيز").
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailsScreen(
    customer: CustomerAccount? = null,
    transactions: List<TransactionItem> = emptyList(),
    languageMode: LanguageMode = LanguageMode.ARABIC,
    onBackClick: () -> Unit = {},
    onRecordTransaction: (CustomerAccount) -> Unit = {},
    onAccountStatement: (CustomerAccount) -> Unit = {},
    onPayment: (CustomerAccount) -> Unit = {},
    onEditCustomer: (CustomerAccount, String, String) -> Unit = { _, _, _ -> },
    onArchiveCustomer: (CustomerAccount) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val currency = if (isArabic) "ر.س" else "SAR"

    // Plausible Arabic customer default for standalone rendering / fallback
    val defaultCustomer = remember {
        CustomerAccount(
            id = "2",
            customerName = "خالد بن عبدالعزيز",
            balance = 1850.00,
            totalDebt = 1850.00,
            phone = "+966 55 987 6543",
            lastTransactionDate = "2026-09-04"
        )
    }

    // Default sample transactions for this customer if none passed
    val defaultCustomerTransactions = remember {
        listOf(
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
                id = "t8",
                title = "تسجيل مشتريات",
                customerName = "خالد بن عبدالعزيز",
                activityType = "مشتريات",
                amount = 620.00,
                isCredit = false,
                date = "2026-08-30",
                relativeTime = "منذ 5 أيام",
                notes = "بضاعة آجلة"
            ),
            TransactionItem(
                id = "t9",
                title = "دفعة نقدية",
                customerName = "خالد بن عبدالعزيز",
                activityType = "دفعة",
                amount = 400.00,
                isCredit = true,
                date = "2026-08-20",
                relativeTime = "منذ أسبوعين",
                notes = "سداد مستحقات"
            )
        )
    }

    val activeCustomer = customer ?: defaultCustomer
    val scopedTransactions = if (customer != null && transactions.isNotEmpty()) {
        transactions.filter { it.customerName == activeCustomer.customerName }
    } else if (customer != null) {
        emptyList()
    } else {
        defaultCustomerTransactions
    }

    var showOverflowMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("customer_details_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ==========================================
            // 1. TOP BAR
            // Back arrow + customer's name as title + three-dot overflow menu
            // ==========================================
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 0.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                        .testTag("customer_details_top_bar"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Arrow
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("customer_details_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isArabic) "رجوع" else "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Customer Name as the Title
                    Text(
                        text = activeCustomer.customerName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("customer_details_title")
                    )

                    // Secondary Options: Overflow Menu (three-dot icon)
                    Box {
                        IconButton(
                            onClick = { showOverflowMenu = true },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("customer_overflow_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = if (isArabic) "خيارات إضافية" else "More options",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                            modifier = Modifier.testTag("customer_overflow_dropdown")
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isArabic) StoreStrings.EDIT_CUSTOMER_AR else StoreStrings.EDIT_CUSTOMER_EN,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    showEditDialog = true
                                },
                                modifier = Modifier.testTag("menu_edit_customer")
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isArabic) StoreStrings.ARCHIVE_CUSTOMER_AR else StoreStrings.ARCHIVE_CUSTOMER_EN,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Archive,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    showArchiveDialog = true
                                },
                                modifier = Modifier.testTag("menu_archive_customer")
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = GeoOutline, thickness = 1.dp)

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ==========================================
                // 2. CUSTOMER SUMMARY HEADER CARD
                // Customer name, phone number, and current balance (e.g., "Owes 150" or "Settled")
                // ==========================================
                item {
                    CustomerSummaryHeaderCard(
                        customer = activeCustomer,
                        currency = currency,
                        isArabic = isArabic
                    )
                }

                // ==========================================
                // 3. UNIFIED ACTIONS
                // Exactly three prominent action buttons/tiles in a row:
                // "Record Transaction", "Account Statement", "Payment"
                // ==========================================
                item {
                    UnifiedActionsRow(
                        isArabic = isArabic,
                        onRecordTransaction = { onRecordTransaction(activeCustomer) },
                        onAccountStatement = { onAccountStatement(activeCustomer) },
                        onPayment = { onPayment(activeCustomer) }
                    )
                }

                // ==========================================
                // 4. RECENT HISTORY (scoped only to this customer)
                // Short "Recent Activity" list, newest first
                // ==========================================
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) StoreStrings.RECENT_ACTIVITY_AR else StoreStrings.RECENT_ACTIVITY_EN,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = if (isArabic) "أحدث العمليات" else "Latest records",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                if (scopedTransactions.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
                        ) {
                            SimpleEmptyState(
                                message = if (isArabic) "لا توجد نشاطات مسجلة لهذا العميل." else "No activities recorded for this customer.",
                                icon = Icons.Default.History,
                                testTag = "customer_recent_empty_state"
                            )
                        }
                    }
                } else {
                    items(items = scopedTransactions, key = { it.id }) { tx ->
                        CustomerActivityRowCard(
                            transaction = tx,
                            currency = currency,
                            isArabic = isArabic
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // ==========================================
    // 5. DIALOGS (Secondary Options)
    // Edit Customer & Archive Customer
    // ==========================================
    if (showEditDialog) {
        EditCustomerDialog(
            customer = activeCustomer,
            isArabic = isArabic,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newPhone ->
                onEditCustomer(activeCustomer, newName, newPhone)
                showEditDialog = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        if (isArabic) "تم تحديث بيانات العميل بنجاح" else "Customer details updated successfully"
                    )
                }
            }
        )
    }

    if (showArchiveDialog) {
        ArchiveCustomerDialog(
            customerName = activeCustomer.customerName,
            isArabic = isArabic,
            onDismiss = { showArchiveDialog = false },
            onConfirmArchive = {
                showArchiveDialog = false
                onArchiveCustomer(activeCustomer)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        if (isArabic) "تمت أرشفة العميل" else "Customer archived"
                    )
                }
            }
        )
    }
}

/**
 * Customer Summary Header Card:
 * Displays customer name, phone number, and clearly shown current balance
 * (e.g. "عليه 1,850.00 ر.س" or "تمت التسوية (خالص)").
 */
@Composable
private fun CustomerSummaryHeaderCard(
    customer: CustomerAccount,
    currency: String,
    isArabic: Boolean
) {
    val hasDebt = customer.balance > 0
    val isSettled = customer.balance == 0.0
    val isStoreDue = customer.balance < 0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = GeoOutlineVariant, shape = RoundedCornerShape(16.dp))
            .testTag("customer_summary_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Customer Avatar Circle
                Surface(
                    color = GeoPrimary.copy(alpha = 0.12f),
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = customer.customerName.take(1),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = GeoPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Name & Phone
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = customer.customerName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("header_customer_name")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = customer.phone.ifBlank { if (isArabic) "بدون رقم هاتف" else "No phone" },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("header_customer_phone")
                        )
                    }
                }
            }

            HorizontalDivider(
                color = GeoOutline.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 14.dp)
            )

            // Current Balance Clearly Displayed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_balance_indicator"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "الرصيد الحالي" else "Current Balance",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isSettled) {
                    Surface(
                        color = StatusGreenBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("balance_settled_badge")
                    ) {
                        Text(
                            text = if (isArabic) "تمت التسوية (خالص)" else "Settled",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    val balanceLabel = if (hasDebt) {
                        if (isArabic) {
                            String.format(Locale.US, "عليه %,.2f %s", customer.balance, currency)
                        } else {
                            String.format(Locale.US, "Owes %,.2f %s", customer.balance, currency)
                        }
                    } else {
                        if (isArabic) {
                            String.format(Locale.US, "له %,.2f %s", -customer.balance, currency)
                        } else {
                            String.format(Locale.US, "Credit %,.2f %s", -customer.balance, currency)
                        }
                    }

                    val badgeColor = if (hasDebt) StatusRed else StatusGreen
                    val badgeBg = if (hasDebt) StatusRedBg else StatusGreenBg

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("balance_amount_badge")
                    ) {
                        Text(
                            text = balanceLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * UNIFIED ACTIONS:
 * Exactly three prominent action buttons/tiles in a row:
 * 1. "Record Transaction" (تسجيل معاملة)
 * 2. "Account Statement" (كشف حساب)
 * 3. "Payment" (دفعة)
 */
@Composable
private fun UnifiedActionsRow(
    isArabic: Boolean,
    onRecordTransaction: () -> Unit,
    onAccountStatement: () -> Unit,
    onPayment: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("unified_actions_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Tile 1: "Record Transaction" -> leads into Purchases flow with pre-selected customer
        ActionTile(
            title = if (isArabic) StoreStrings.RECORD_TRANSACTION_AR else StoreStrings.RECORD_TRANSACTION_EN,
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            accentColor = GeoPrimary,
            testTag = "action_record_transaction",
            onClick = onRecordTransaction,
            modifier = Modifier.weight(1f)
        )

        // Tile 2: "Account Statement" -> leads into Analysis Center -> Account Statement with pre-selected customer
        ActionTile(
            title = if (isArabic) StoreStrings.ACCOUNT_STATEMENT_AR else StoreStrings.ACCOUNT_STATEMENT_EN,
            icon = Icons.Default.Assessment,
            accentColor = MaterialTheme.colorScheme.onSurface,
            testTag = "action_account_statement",
            onClick = onAccountStatement,
            modifier = Modifier.weight(1f)
        )

        // Tile 3: "Payment" -> leads into Quick Payment flow with pre-selected customer
        ActionTile(
            title = if (isArabic) StoreStrings.PAYMENT_AR else StoreStrings.PAYMENT_EN,
            icon = Icons.Default.AccountBalanceWallet,
            accentColor = StatusGreen,
            testTag = "action_payment",
            onClick = onPayment,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Single Action Tile Component
 */
@Composable
private fun ActionTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .border(width = 1.dp, color = GeoOutlineVariant, shape = RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.10f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Activity Row Card for Customer Details
 * Mirrors Home Screen's latest activities row format:
 * customer name, activity type badge ("مشتريات" / "دفعة"), notes, amount, and relative time.
 */
@Composable
private fun CustomerActivityRowCard(
    transaction: TransactionItem,
    currency: String,
    isArabic: Boolean
) {
    val isCredit = transaction.isCredit
    val isPayment = transaction.activityType == "دفعة" || transaction.activityType == "Payment"

    val (badgeBg, badgeTint, iconVector) = if (isPayment || isCredit) {
        Triple(StatusGreenBg, StatusGreen, Icons.Default.Add)
    } else {
        Triple(StatusRedBg, StatusRed, Icons.Default.Remove)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
            .testTag("customer_activity_row_${transaction.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Pill
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = badgeTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Customer Name & Activity Type + Notes
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title.ifBlank { transaction.activityType },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = transaction.activityType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (transaction.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = transaction.notes,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Amount and relative time
            Column(horizontalAlignment = Alignment.End) {
                val prefix = if (isPayment || isCredit) "+" else "-"
                val amountColor = if (isPayment || isCredit) StatusGreen else StatusRed
                Text(
                    text = String.format(Locale.US, "%s%,.2f %s", prefix, transaction.amount, currency),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = amountColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.relativeTime,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Edit Customer Dialog
 */
@Composable
private fun EditCustomerDialog(
    customer: CustomerAccount,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSave: (newName: String, newPhone: String) -> Unit
) {
    var name by remember { mutableStateOf(customer.customerName) }
    var phone by remember { mutableStateOf(customer.phone) }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isArabic) StoreStrings.EDIT_CUSTOMER_AR else StoreStrings.EDIT_CUSTOMER_EN,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text(if (isArabic) "اسم العميل" else "Customer Name") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    isError = nameError,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_customer_name_field")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isArabic) "رقم الهاتف" else "Phone Number") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_customer_phone_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        onSave(name.trim(), phone.trim())
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                modifier = Modifier.testTag("confirm_edit_customer_button")
            ) {
                Text(text = if (isArabic) "حفظ التعديلات" else "Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("cancel_edit_customer_button")
            ) {
                Text(text = if (isArabic) "إلغاء" else "Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

/**
 * Archive Customer Confirmation Dialog
 * Note: Permanent delete does not exist on this screen. Archiving is the only destructive-style action.
 */
@Composable
private fun ArchiveCustomerDialog(
    customerName: String,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onConfirmArchive: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isArabic) StoreStrings.ARCHIVE_CUSTOMER_AR else StoreStrings.ARCHIVE_CUSTOMER_EN,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(
                text = if (isArabic) {
                    "هل أنت متأكد من أرشفة العميل \"$customerName\"؟ سيتم نقل الحساب إلى الأرشيف."
                } else {
                    "Are you sure you want to archive customer \"$customerName\"? The account will be moved to archives."
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmArchive,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.testTag("confirm_archive_customer_button")
            ) {
                Text(
                    text = if (isArabic) "أرشفة" else "Archive",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("cancel_archive_customer_button")
            ) {
                Text(text = if (isArabic) "إلغاء" else "Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
