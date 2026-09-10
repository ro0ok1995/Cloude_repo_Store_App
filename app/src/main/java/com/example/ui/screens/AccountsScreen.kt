package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountFilter
import com.example.model.AccountSortOption
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.SettlementType
import com.example.model.StoreStrings
import com.example.model.TransactionItem
import com.example.ui.components.CustomerSearchField
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    accounts: List<CustomerAccount>,
    allCustomers: List<CustomerAccount> = emptyList(),
    transactions: List<TransactionItem> = emptyList(),
    searchQuery: String,
    filter: AccountFilter,
    selectedCustomerDetails: CustomerAccount?,
    showAddCustomerDialog: Boolean,
    languageMode: LanguageMode,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (AccountFilter) -> Unit,
    onCustomerClick: (CustomerAccount) -> Unit,
    onCloseCustomerDetails: () -> Unit,
    onOpenAddCustomerDialog: () -> Unit,
    onCloseAddCustomerDialog: () -> Unit,
    onAddCustomer: (name: String, phone: String, initialDebt: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val currency = if (isArabic) "ر.س" else "SAR"
    val focusManager = LocalFocusManager.current

    var sortOption by remember { mutableStateOf(AccountSortOption.DEFAULT) }

    val matchingCustomers = remember(searchQuery, allCustomers, accounts) {
        val pool = if (allCustomers.isNotEmpty()) allCustomers else accounts
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) emptyList()
        else pool.filter { it.customerName.lowercase().contains(q) || it.phone.contains(q) }
    }

    val sortedAccounts = remember(accounts, sortOption, transactions) {
        when (sortOption) {
            AccountSortOption.DEFAULT -> accounts
            AccountSortOption.HIGHEST_DEBT -> accounts.sortedByDescending { it.balance }
            AccountSortOption.HIGHEST_CASH -> accounts.sortedByDescending { customer ->
                transactions.filter {
                    it.customerName == customer.customerName && !it.isCredit &&
                    (it.activityType.contains("شراء كاش") || it.activityType.contains("Cash") || (!it.activityType.contains("تسديد") && !it.activityType.contains("Payment")))
                }.sumOf { it.amount }
            }
            AccountSortOption.HIGHEST_INSTALLMENTS -> accounts.sortedByDescending { customer ->
                transactions.filter {
                    it.customerName == customer.customerName &&
                    (it.activityType.contains("تسديد") || it.activityType.contains("Payment")) &&
                    it.settlementType == SettlementType.PARTIAL
                }.sumOf { it.amount }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("accounts_screen")
    ) {
        // TOP ACTION BAR & SEARCH
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) StoreStrings.ACCOUNTS_AR else StoreStrings.ACCOUNTS_EN,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = onOpenAddCustomerDialog,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    contentPadding = ButtonDefaults.ContentPadding,
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("add_customer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArabic) StoreStrings.ADD_CUSTOMER_AR else StoreStrings.ADD_CUSTOMER_EN,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reusable Customer Search Field with Dropdown
            CustomerSearchField(
                customers = if (allCustomers.isNotEmpty()) allCustomers else accounts,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onCustomerSelected = { customer ->
                    onSearchQueryChange(customer.customerName)
                    onCustomerClick(customer)
                },
                onClearSelection = {
                    onSearchQueryChange("")
                },
                selectedCustomerId = selectedCustomerDetails?.id,
                placeholderText = if (isArabic) StoreStrings.SEARCH_CUSTOMER_ACCOUNTS_AR else StoreStrings.SEARCH_CUSTOMER_ACCOUNTS_EN,
                currency = currency,
                isArabic = isArabic,
                inputTestTag = "accounts_search_input",
                dropdownTestTag = "accounts_search_results_overlay",
                itemTagPrefix = "search_result_item_"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Control: "All / Has Debt / Recently Active"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("accounts_filter_row"),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == AccountFilter.ALL,
                    onClick = { onFilterChange(AccountFilter.ALL) },
                    label = {
                        Text(
                            text = if (isArabic) StoreStrings.FILTER_ALL_AR else StoreStrings.FILTER_ALL_EN,
                            fontSize = 12.sp,
                            fontWeight = if (filter == AccountFilter.ALL) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GeoPrimary.copy(alpha = 0.12f),
                        selectedLabelColor = GeoPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filter == AccountFilter.ALL,
                        borderColor = if (filter == AccountFilter.ALL) GeoPrimary else GeoOutline
                    ),
                    modifier = Modifier.testTag("filter_all")
                )

                FilterChip(
                    selected = filter == AccountFilter.HAS_DEBT,
                    onClick = { onFilterChange(AccountFilter.HAS_DEBT) },
                    label = {
                        Text(
                            text = if (isArabic) StoreStrings.FILTER_HAS_DEBT_AR else StoreStrings.FILTER_HAS_DEBT_EN,
                            fontSize = 12.sp,
                            fontWeight = if (filter == AccountFilter.HAS_DEBT) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GeoPrimary.copy(alpha = 0.12f),
                        selectedLabelColor = GeoPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filter == AccountFilter.HAS_DEBT,
                        borderColor = if (filter == AccountFilter.HAS_DEBT) GeoPrimary else GeoOutline
                    ),
                    modifier = Modifier.testTag("filter_has_debt")
                )

                FilterChip(
                    selected = filter == AccountFilter.RECENTLY_ACTIVE,
                    onClick = { onFilterChange(AccountFilter.RECENTLY_ACTIVE) },
                    label = {
                        Text(
                            text = if (isArabic) StoreStrings.FILTER_RECENTLY_ACTIVE_AR else StoreStrings.FILTER_RECENTLY_ACTIVE_EN,
                            fontSize = 12.sp,
                            fontWeight = if (filter == AccountFilter.RECENTLY_ACTIVE) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GeoPrimary.copy(alpha = 0.12f),
                        selectedLabelColor = GeoPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = filter == AccountFilter.RECENTLY_ACTIVE,
                        borderColor = if (filter == AccountFilter.RECENTLY_ACTIVE) GeoPrimary else GeoOutline
                    ),
                    modifier = Modifier.testTag("filter_recently_active")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sort by control (independent of filter chips)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("accounts_sort_row")
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort by",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isArabic) "ترتيب:" else "Sort:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FilterChip(
                    selected = sortOption == AccountSortOption.DEFAULT,
                    onClick = { sortOption = AccountSortOption.DEFAULT },
                    label = {
                        Text(
                            text = if (isArabic) StoreStrings.SORT_DEFAULT_AR else StoreStrings.SORT_DEFAULT_EN,
                            fontSize = 11.sp,
                            fontWeight = if (sortOption == AccountSortOption.DEFAULT) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("sort_default")
                )
                FilterChip(
                    selected = sortOption == AccountSortOption.HIGHEST_DEBT,
                    onClick = { sortOption = AccountSortOption.HIGHEST_DEBT },
                    label = {
                        Text(
                            text = if (isArabic) StoreStrings.SORT_HIGHEST_DEBT_AR else StoreStrings.SORT_HIGHEST_DEBT_EN,
                            fontSize = 11.sp,
                            fontWeight = if (sortOption == AccountSortOption.HIGHEST_DEBT) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("sort_highest_debt")
                )
                FilterChip(
                    selected = sortOption == AccountSortOption.HIGHEST_CASH,
                    onClick = { sortOption = AccountSortOption.HIGHEST_CASH },
                    label = {
                        Text(
                            text = if (isArabic) StoreStrings.SORT_HIGHEST_CASH_AR else StoreStrings.SORT_HIGHEST_CASH_EN,
                            fontSize = 11.sp,
                            fontWeight = if (sortOption == AccountSortOption.HIGHEST_CASH) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("sort_highest_cash")
                )
                FilterChip(
                    selected = sortOption == AccountSortOption.HIGHEST_INSTALLMENTS,
                    onClick = { sortOption = AccountSortOption.HIGHEST_INSTALLMENTS },
                    label = {
                        Text(
                            text = if (isArabic) StoreStrings.SORT_HIGHEST_INSTALLMENTS_AR else StoreStrings.SORT_HIGHEST_INSTALLMENTS_EN,
                            fontSize = 11.sp,
                            fontWeight = if (sortOption == AccountSortOption.HIGHEST_INSTALLMENTS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("sort_highest_installments")
                )
            }
        }

        HorizontalDivider(color = GeoOutline, thickness = 1.dp)

        // CUSTOMER LIST OR EMPTY STATE
        if (sortedAccounts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.testTag("accounts_empty_state")
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isArabic) StoreStrings.NO_CUSTOMERS_YET_AR else StoreStrings.NO_CUSTOMERS_YET_EN,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onOpenAddCustomerDialog,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        modifier = Modifier.testTag("empty_state_add_customer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) StoreStrings.ADD_CUSTOMER_AR else StoreStrings.ADD_CUSTOMER_EN,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag("accounts_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(items = sortedAccounts, key = { it.id }) { customer ->
                    CustomerCardItem(
                        customer = customer,
                        currency = currency,
                        isArabic = isArabic,
                        onClick = { onCustomerClick(customer) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (selectedCustomerDetails != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = onCloseCustomerDetails,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier.testTag("customer_details_sheet")
        ) {
            CustomerDetailsContent(
                customer = selectedCustomerDetails,
                currency = currency,
                isArabic = isArabic,
                onClose = onCloseCustomerDetails
            )
        }
    }

    if (showAddCustomerDialog) {
        AddCustomerDialog(
            isArabic = isArabic,
            onDismiss = onCloseAddCustomerDialog,
            onConfirm = onAddCustomer
        )
    }
}

@Composable
private fun CustomerCardItem(
    customer: CustomerAccount,
    currency: String,
    isArabic: Boolean = true,
    onClick: () -> Unit
) {
    val hasDebt = customer.balance > 0
    val isPaidUp = customer.balance == 0.0

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = GeoOutlineVariant, shape = RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("customer_card_${customer.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = GeoPrimary.copy(alpha = 0.08f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = customer.customerName.take(1),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GeoPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.customerName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (customer.phone.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = customer.phone,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (isPaidUp) {
                Surface(
                    color = StatusGreenBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isArabic) StoreStrings.PAID_UP_AR else StoreStrings.PAID_UP_EN,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = if (hasDebt) StatusRedBg else StatusGreenBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = String.format(
                                Locale.US,
                                "%s%,.2f %s",
                                if (hasDebt) "" else "-",
                                customer.balance,
                                currency
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasDebt) StatusRed else StatusGreen,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun CustomerDetailsContent(
    customer: CustomerAccount,
    currency: String,
    isArabic: Boolean,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) StoreStrings.CUSTOMER_DETAILS_AR else StoreStrings.CUSTOMER_DETAILS_EN,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = GeoPrimary.copy(alpha = 0.12f),
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = customer.customerName.take(1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = GeoPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = customer.customerName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (customer.phone.isNotBlank()) {
                            Text(
                                text = customer.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isArabic) StoreStrings.TOTAL_BALANCE_AR else StoreStrings.TOTAL_BALANCE_EN,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(Locale.US, "%,.2f %s", customer.balance, currency),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (customer.balance > 0) StatusRed else StatusGreen
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isArabic) StoreStrings.TOTAL_DEBT_AR else StoreStrings.TOTAL_DEBT_EN,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(Locale.US, "%,.2f %s", customer.totalDebt, currency),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = StatusRed
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onClose,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("details_account_statement_button")
            ) {
                Text(
                    text = if (isArabic) StoreStrings.ACCOUNT_STATEMENT_AR else StoreStrings.ACCOUNT_STATEMENT_EN,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onClose,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("details_settlement_button")
            ) {
                Text(
                    text = if (isArabic) StoreStrings.SETTLEMENT_AR else StoreStrings.SETTLEMENT_EN,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun AddCustomerDialog(
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, initialDebt: Double) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var initialDebtStr by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismiss()
        },
        title = {
            Text(
                text = if (isArabic) StoreStrings.ADD_CUSTOMER_AR else StoreStrings.ADD_CUSTOMER_EN,
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
                    label = {
                        Text(if (isArabic) "اسم العميل *" else "Customer Name *")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    isError = nameError,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_customer_name_field")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = {
                        Text(if (isArabic) "رقم الهاتف (اختياري)" else "Phone number (optional)")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_customer_phone_field")
                )

                OutlinedTextField(
                    value = initialDebtStr,
                    onValueChange = { initialDebtStr = it },
                    label = {
                        Text(if (isArabic) "الرصيد الافتتاحي / دين سابق (اختياري)" else "Initial Debt / Balance (optional)")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_customer_debt_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        focusManager.clearFocus()
                        val debt = initialDebtStr.toDoubleOrNull() ?: 0.0
                        onConfirm(name, phone, debt)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                modifier = Modifier.testTag("confirm_add_customer_button")
            ) {
                Text(
                    text = if (isArabic) "حفظ" else "Save",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("cancel_add_customer_button")
            ) {
                Text(text = if (isArabic) "إلغاء" else "Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
