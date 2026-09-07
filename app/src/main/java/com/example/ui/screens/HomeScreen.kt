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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.PeriodFilter
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
import java.util.Locale

/**
 * SmallStore HOME Screen (RTL Arabic First)
 * Strictly conforms to:
 * - Default Scope: Whole shop view by default.
 * - Customer Search: "Search customer" field that filters/selects a customer.
 * - Selected Customer Chip: Removable chip with 'x' icon returning to shop-wide scope.
 * - Fixed Top Area: Top bar, customer search, period selector, and compact stats stay fixed.
 * - Compact Statistics Block: 3 small stat cards (Total Balance, Total Debt, Today's Transactions).
 * - Period Selector: (Today / Week / Month / Custom) stays visible at all times.
 * - Latest Activities List: Scrollable vertical list showing customer name, activity type, amount, relative time.
 * - Scoped upon customer selection to only that customer's history.
 */
@Composable
fun HomeScreen(
    totalBalance: Double,
    totalDebt: Double,
    transactionsCount: Int,
    transactions: List<TransactionItem>,
    matchingCustomers: List<CustomerAccount>,
    selectedCustomer: CustomerAccount?,
    searchQuery: String,
    selectedPeriod: PeriodFilter,
    languageMode: LanguageMode,
    onSearchQueryChange: (String) -> Unit,
    onSelectCustomer: (CustomerAccount) -> Unit,
    onClearSelectedCustomer: () -> Unit,
    onSelectPeriod: (PeriodFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val currency = if (isArabic) "ر.س" else "SAR"
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_container")
    ) {
        // ==========================================
        // FIXED TOP SECTION (Stays fixed while list scrolls)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("home_fixed_top_section")
        ) {
            // 1. CUSTOMER SEARCH FIELD
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_search_input"),
                placeholder = {
                    Text(
                        text = if (isArabic) StoreStrings.SEARCH_CUSTOMER_AR else StoreStrings.SEARCH_CUSTOMER_EN,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = GeoPrimary,
                    unfocusedBorderColor = GeoOutline
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )

            // Search Results Dropdown Suggestions (if typing)
            if (searchQuery.isNotBlank() && matchingCustomers.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .border(1.dp, GeoOutline, RoundedCornerShape(12.dp))
                        .testTag("customer_search_suggestions")
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        matchingCustomers.take(4).forEach { customer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectCustomer(customer)
                                        focusManager.clearFocus()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("customer_suggestion_${customer.id}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = GeoPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = customer.customerName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = String.format(Locale.US, "%,.2f %s", customer.balance, currency),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (customer.balance >= 0) StatusGreen else StatusRed
                                )
                            }
                        }
                    }
                }
            }

            // 2. SELECTED CUSTOMER CHIP (appears when a customer is picked, with 'x' to clear)
            if (selectedCustomer != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("selected_customer_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedCustomer.customerName,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = onClearSelectedCustomer,
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("clear_selected_customer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear customer filter",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = if (isArabic) "نطاق خاص بالعميل" else "Customer Scoped",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. PERIOD SELECTOR (Today / Week / Month / Custom) - stays visible at all times
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("period_selector_row"),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PeriodChip(
                    label = if (isArabic) StoreStrings.PERIOD_TODAY_AR else StoreStrings.PERIOD_TODAY_EN,
                    isSelected = selectedPeriod == PeriodFilter.TODAY,
                    testTag = "period_today",
                    onClick = { onSelectPeriod(PeriodFilter.TODAY) },
                    modifier = Modifier.weight(1f)
                )
                PeriodChip(
                    label = if (isArabic) StoreStrings.PERIOD_WEEK_AR else StoreStrings.PERIOD_WEEK_EN,
                    isSelected = selectedPeriod == PeriodFilter.WEEK,
                    testTag = "period_week",
                    onClick = { onSelectPeriod(PeriodFilter.WEEK) },
                    modifier = Modifier.weight(1f)
                )
                PeriodChip(
                    label = if (isArabic) StoreStrings.PERIOD_MONTH_AR else StoreStrings.PERIOD_MONTH_EN,
                    isSelected = selectedPeriod == PeriodFilter.MONTH,
                    testTag = "period_month",
                    onClick = { onSelectPeriod(PeriodFilter.MONTH) },
                    modifier = Modifier.weight(1f)
                )
                PeriodChip(
                    label = if (isArabic) StoreStrings.PERIOD_CUSTOM_AR else StoreStrings.PERIOD_CUSTOM_EN,
                    isSelected = selectedPeriod == PeriodFilter.CUSTOM,
                    testTag = "period_custom",
                    onClick = { onSelectPeriod(PeriodFilter.CUSTOM) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. COMPACT STATISTICS BLOCK (3 stat cards)
            // Displays shop-wide metrics by default, or scoped customer metrics when a customer is selected.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compact_stats_block"),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stat 1: Total Balance (إجمالي الرصيد)
                CompactStatCard(
                    title = if (selectedCustomer != null) {
                        if (isArabic) "رصيد العميل" else "Customer Balance"
                    } else {
                        if (isArabic) StoreStrings.TOTAL_BALANCE_AR else StoreStrings.TOTAL_BALANCE_EN
                    },
                    value = String.format(Locale.US, "%,.1f %s", totalBalance, currency),
                    valueColor = if (totalBalance >= 0) StatusGreen else StatusRed,
                    testTag = "stat_total_balance",
                    modifier = Modifier.weight(1f)
                )

                // Stat 2: Total Debt (إجمالي الديون)
                CompactStatCard(
                    title = if (selectedCustomer != null) {
                        if (isArabic) "مستحقات العميل" else "Customer Due"
                    } else {
                        if (isArabic) StoreStrings.TOTAL_DEBT_AR else StoreStrings.TOTAL_DEBT_EN
                    },
                    value = String.format(Locale.US, "%,.1f %s", totalDebt, currency),
                    valueColor = MaterialTheme.colorScheme.onSurface,
                    testTag = "stat_total_debt",
                    modifier = Modifier.weight(1f)
                )

                // Stat 3: Today's Transactions / Activities count
                CompactStatCard(
                    title = if (selectedCustomer != null) {
                        if (isArabic) "عمليات العميل" else "Customer Txs"
                    } else {
                        if (isArabic) StoreStrings.TODAY_TRANSACTIONS_AR else StoreStrings.TODAY_TRANSACTIONS_EN
                    },
                    value = "$transactionsCount",
                    valueColor = GeoPrimary,
                    testTag = "stat_today_txs",
                    modifier = Modifier.weight(0.9f)
                )
            }
        }

        Divider(color = GeoOutline, thickness = 1.dp)

        // ==========================================
        // SCROLLABLE LATEST ACTIVITIES LIST
        // (Scrolls independently below fixed top section)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .testTag("latest_activities_section")
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Section Header: "Latest Activities" (أحدث النشاطات)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) StoreStrings.LATEST_ACTIVITIES_AR else StoreStrings.LATEST_ACTIVITIES_EN,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (selectedCustomer != null) {
                    Text(
                        text = if (isArabic) "سجل العميل بالكامل" else "Complete History",
                        style = MaterialTheme.typography.bodySmall,
                        color = GeoPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (transactions.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GeoOutlineVariant, RoundedCornerShape(16.dp))
                ) {
                    SimpleEmptyState(
                        message = if (isArabic) "لا توجد نشاطات مسجلة." else "No activities recorded.",
                        icon = Icons.Default.ReceiptLong,
                        testTag = "activities_empty_state"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("latest_activities_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = transactions, key = { it.id }) { tx ->
                        ActivityRowCard(
                            transaction = tx,
                            currency = currency,
                            isArabic = isArabic
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * Period Selector Pill Button
 */
@Composable
private fun PeriodChip(
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, GeoOutline),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Compact Stat Card Component
 */
@Composable
private fun CompactStatCard(
    title: String,
    value: String,
    valueColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Latest Activity Row Card:
 * Each row showing: customer name, activity type (e.g. "Purchase" / "Payment"), amount, and relative time (e.g. "2h ago").
 */
@Composable
private fun ActivityRowCard(
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
            .testTag("activity_row_${transaction.id}")
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
                    text = transaction.customerName,
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
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Amount and Relative Time
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(
                        Locale.US,
                        "%s%,.2f %s",
                        if (isPayment || isCredit) "+" else "-",
                        transaction.amount,
                        currency
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = if (isPayment || isCredit) StatusGreen else StatusRed
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.relativeTime,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
