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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccountFilter
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.StoreStrings
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import java.util.Locale

/**
 * SmallStore ACCOUNTS Screen
 *
 * Rules:
 * - TOP BAR: Title: "Accounts" (الحسابات), Drawer icon + notification bell as in Foundation.
 * - SEARCH AND FILTER:
 *   - Search field: "Search customer by name or phone" (البحث بالاسم أو رقم الهاتف).
 *   - Filter control: chip row for "All / Has Debt / Recently Active" (الكل / عليه ديون / نشط مؤخراً).
 * - CUSTOMER LIST:
 *   - Vertical scrollable list of customer cards/rows:
 *     customer name, phone number (muted/secondary), balance indicator (owed amount / "Paid up" / "خالص").
 *   - Tapping row navigates to Customer Details.
 * - ADD CUSTOMER:
 *   - Clearly visible single action ("+ Add Customer") near the top to create a new customer.
 * - EMPTY STATE:
 *   - Centered icon + "No customers yet" (لا يوجد عملاء حتى الآن) + add-customer action.
 * - CONSTRAINTS:
 *   - No sorting controls, tabs, or grouping headers beyond the single filter described above.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    accounts: List<CustomerAccount>,
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("accounts_screen")
    ) {
        // --- TOP ACTION BAR & SEARCH ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Row with "+ Add Customer" action button prominently placed at the top
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

                // Single visible action: "+ Add Customer"
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

            // Search field: "Search customer by name or phone"
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = if (isArabic) StoreStrings.SEARCH_CUSTOMER_ACCOUNTS_AR else StoreStrings.SEARCH_CUSTOMER_ACCOUNTS_EN,
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
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GeoPrimary,
                    unfocusedBorderColor = GeoOutline,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("accounts_search_field")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Simple Filter Control: "All / Has Debt / Recently Active"
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
        }

        HorizontalDivider(color = GeoOutline, thickness = 1.dp)

        // --- CUSTOMER LIST OR EMPTY STATE ---
        if (accounts.isEmpty()) {
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

                items(items = accounts, key = { it.id }) { customer ->
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

    // --- CUSTOMER DETAILS MODAL BOTTOM SHEET ---
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

    // --- ADD CUSTOMER DIALOG ---
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            isArabic = isArabic,
            onDismiss = onCloseAddCustomerDialog,
            onConfirm = onAddCustomer
        )
    }
}

/**
 * Customer Card / Row:
 * Shows:
 * - Customer name
 * - Phone number (muted/secondary text)
 * - Balance indicator (owed amount in red or "Paid up" / "خالص" in green)
 * - Trailing chevron to indicate navigability to Customer Details
 */
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
            // Customer Avatar / Initial
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

            // Name and Phone
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

            // Balance Indicator (Owed amount or Paid up)
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

            // Subtle chevron indicating row navigates to Customer Details
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Customer Details Bottom Sheet Content:
 * Displays: Customer name, phone, balance indicator, debt details,
 * and exact Foundation actions: "Account Statement" (كشف حساب) and "Settlement" (تسوية).
 */
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
        // Header
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

        // Customer Info Card
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

        // Required Foundation actions: "Account Statement" and "Settlement"
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

/**
 * Add Customer Dialog
 * A single simple creation flow for creating a new customer.
 */
@Composable
private fun AddCustomerDialog(
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, initialDebt: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var initialDebtStr by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                // Customer Name Field
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

                // Phone Number Field (Optional)
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

                // Initial Debt (Optional)
                OutlinedTextField(
                    value = initialDebtStr,
                    onValueChange = { initialDebtStr = it },
                    label = {
                        Text(if (isArabic) "الرصيد الأولي / الدين (اختياري)" else "Initial Debt / Balance (optional)")
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
                onClick = onDismiss,
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
