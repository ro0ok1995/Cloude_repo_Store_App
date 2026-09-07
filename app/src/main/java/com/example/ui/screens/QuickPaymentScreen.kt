package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.PaymentMethodOption
import com.example.model.SettlementType
import com.example.model.StoreStrings
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
 * Quick Payment Screen of SmallStore
 *
 * SPECIFICATION RULES:
 * - ENTRY: Reached via "+ -> Quick Payment".
 *
 * STEP 1 - CUSTOMER SELECTION:
 * - Top of screen: a required "Select Customer" search/picker field.
 * - Shows it already displaying a selected customer's name with a small "change" link,
 *   since customer selection happens first in this flow.
 *
 * STEP 2 - SETTLEMENT TYPE:
 * - A clear two-option toggle/segmented control: "Full" (كامل) and "Partial" (جزئي).
 *   Only one can be active at a time.
 *
 * FULL MODE (default selected state for this render):
 * - Below the toggle, choice between "Cash" (نقداً) and "Debt" (آجل) chips/cards.
 * - Only the input field for the selected method (Cash or Debt) is active/editable;
 *   the other is hidden or disabled/greyed out - do not show both active in Full mode.
 *
 * PARTIAL MODE (alternate state supported by the layout):
 * - Both "Cash amount" field and "Debt amount" field are shown and editable simultaneously.
 *
 * NOTES:
 * - Optional multi-line "Notes" field near bottom, clearly labeled "(optional)".
 *
 * VALIDATION RULE:
 * - Amounts entered can never exceed transaction total (helper/error text below amount field).
 *
 * BOTTOM ACTION:
 * - Single primary button "Complete" (إتمام) anchored at bottom, opening Unified Settlement summary.
 *
 * CONSTRAINTS:
 * - Only Cash and Debt payment methods. No receipt-printing option.
 * - Rendered in Full mode with plausible Arabic sample data.
 */
@Composable
fun QuickPaymentScreen(
    customer: CustomerAccount?,
    allCustomers: List<CustomerAccount>,
    transactionTotal: Double,
    settlementType: SettlementType,
    paymentMethod: PaymentMethodOption,
    cashAmount: String,
    debtAmount: String,
    notes: String,
    languageMode: LanguageMode = LanguageMode.ARABIC,
    onBackClick: () -> Unit = {},
    onCustomerChange: (CustomerAccount) -> Unit = {},
    onSettlementTypeChange: (SettlementType) -> Unit = {},
    onPaymentMethodChange: (PaymentMethodOption) -> Unit = {},
    onCashAmountChange: (String) -> Unit = {},
    onDebtAmountChange: (String) -> Unit = {},
    onNotesChange: (String) -> Unit = {},
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val currency = if (isArabic) "ر.س" else "SAR"
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showCustomerPicker by remember { mutableStateOf(false) }
    var showSettlementDialog by remember { mutableStateOf(false) }

    // Amount Calculations & Validation
    val parsedCash = cashAmount.toDoubleOrNull() ?: 0.0
    val parsedDebt = debtAmount.toDoubleOrNull() ?: 0.0
    val currentEnteredTotal = when (settlementType) {
        SettlementType.FULL -> when (paymentMethod) {
            PaymentMethodOption.CASH -> parsedCash
            PaymentMethodOption.DEBT -> parsedDebt
        }
        SettlementType.PARTIAL -> parsedCash + parsedDebt
    }
    val isExceeded = currentEnteredTotal > (transactionTotal + 0.01)
    val isValid = customer != null && currentEnteredTotal > 0.0 && !isExceeded

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("quick_payment_screen"),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("quick_payment_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isArabic) "رجوع" else "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (isArabic) StoreStrings.QUICK_PAYMENT_AR else StoreStrings.QUICK_PAYMENT_EN,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("quick_payment_title")
                    )
                }
            }
        },
        bottomBar = {
            // Sticky Bottom Primary Action: "Complete"
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = GeoOutlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            if (isValid) {
                                showSettlementDialog = true
                                onComplete()
                            }
                        },
                        enabled = isValid,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GeoPrimary,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("quick_payment_complete_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) StoreStrings.COMPLETE_ACTION_AR else StoreStrings.COMPLETE_ACTION_EN,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // STEP 1 — CUSTOMER SELECTION
            // ==========================================
            Text(
                text = if (isArabic) "الخطوة 1: تحديد العميل *" else "Step 1: Customer Selection *",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Required Customer Card: Pre-filled with selected customer and small "change" link
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = GeoOutlineVariant, shape = RoundedCornerShape(14.dp))
                    .testTag("quick_payment_customer_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GeoPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = customer?.customerName?.take(1) ?: "ع",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = customer?.customerName ?: (if (isArabic) "خالد بن عبدالعزيز" else "Khalid Bin Abdulaziz"),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("selected_customer_name")
                            )
                            Text(
                                text = customer?.phone ?: "+966 55 987 6543",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Small "change" link
                    TextButton(
                        onClick = { showCustomerPicker = true },
                        modifier = Modifier.testTag("quick_payment_change_customer_button")
                    ) {
                        Text(
                            text = if (isArabic) StoreStrings.CHANGE_CUSTOMER_AR else StoreStrings.CHANGE_CUSTOMER_EN,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = GeoPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transaction Total Banner
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = GeoOutlineVariant, shape = RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) StoreStrings.TRANSACTION_TOTAL_AR else StoreStrings.TRANSACTION_TOTAL_EN,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = String.format(Locale.US, "%.2f %s", transactionTotal, currency),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = GeoPrimary
                        ),
                        modifier = Modifier.testTag("quick_payment_transaction_total")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // STEP 2 — SETTLEMENT TYPE
            // ==========================================
            Text(
                text = if (isArabic) "الخطوة 2: نوع التسوية" else "Step 2: Settlement Type",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Two-option toggle/segmented control: "Full" and "Partial"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp)
                    .testTag("settlement_type_segmented_control")
            ) {
                val fullSelected = settlementType == SettlementType.FULL
                val partialSelected = settlementType == SettlementType.PARTIAL

                // "Full" Option (default)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (fullSelected) MaterialTheme.colorScheme.surface
                            else Color.Transparent
                        )
                        .clickable { onSettlementTypeChange(SettlementType.FULL) }
                        .padding(vertical = 10.dp)
                        .testTag("settlement_type_full"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) StoreStrings.SETTLEMENT_TYPE_FULL_AR else StoreStrings.SETTLEMENT_TYPE_FULL_EN,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (fullSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (fullSelected) GeoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // "Partial" Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (partialSelected) MaterialTheme.colorScheme.surface
                            else Color.Transparent
                        )
                        .clickable { onSettlementTypeChange(SettlementType.PARTIAL) }
                        .padding(vertical = 10.dp)
                        .testTag("settlement_type_partial"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) StoreStrings.SETTLEMENT_TYPE_PARTIAL_AR else StoreStrings.SETTLEMENT_TYPE_PARTIAL_EN,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (partialSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (partialSelected) GeoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Rendering: FULL MODE vs PARTIAL MODE
            if (settlementType == SettlementType.FULL) {
                // ==========================================
                // FULL MODE (Default Selected State)
                // ==========================================
                Text(
                    text = if (isArabic) "طريقة السداد بالكامل:" else "Full Payment Method:",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Selectable Chips/Radio Cards: Cash vs Debt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isCash = paymentMethod == PaymentMethodOption.CASH
                    val isDebt = paymentMethod == PaymentMethodOption.DEBT

                    // Cash Chip Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCash) GeoPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isCash) 1.5.dp else 1.dp,
                                color = if (isCash) GeoPrimary else GeoOutlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onPaymentMethodChange(PaymentMethodOption.CASH) }
                            .testTag("payment_method_cash_chip")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = if (isCash) GeoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) StoreStrings.METHOD_CASH_AR else StoreStrings.METHOD_CASH_EN,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isCash) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCash) GeoPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    // Debt Chip Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDebt) GeoPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isDebt) 1.5.dp else 1.dp,
                                color = if (isDebt) GeoPrimary else GeoOutlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onPaymentMethodChange(PaymentMethodOption.DEBT) }
                            .testTag("payment_method_debt_chip")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = if (isDebt) GeoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) StoreStrings.METHOD_DEBT_AR else StoreStrings.METHOD_DEBT_EN,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isDebt) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isDebt) GeoPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // In Full mode: Only the input field for the selected method is active/editable!
                if (paymentMethod == PaymentMethodOption.CASH) {
                    Text(
                        text = if (isArabic) "المبلغ النقدي المسدد" else "Cash Amount",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = cashAmount,
                        onValueChange = onCashAmountChange,
                        trailingIcon = {
                            Text(
                                text = currency,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = isExceeded,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isExceeded) StatusRed else GeoPrimary,
                            unfocusedBorderColor = if (isExceeded) StatusRed else GeoOutlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_payment_cash_input")
                    )
                } else {
                    Text(
                        text = if (isArabic) "المبلغ الآجل المسجل" else "Debt Amount",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = debtAmount,
                        onValueChange = onDebtAmountChange,
                        trailingIcon = {
                            Text(
                                text = currency,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = isExceeded,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isExceeded) StatusRed else GeoPrimary,
                            unfocusedBorderColor = if (isExceeded) StatusRed else GeoOutlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_payment_debt_input")
                    )
                }
            } else {
                // ==========================================
                // PARTIAL MODE (Alternate state supported by layout)
                // Both Cash amount and Debt amount are shown & editable simultaneously
                // ==========================================
                Text(
                    text = if (isArabic) "المبلغ النقدي:" else "Cash Amount:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = cashAmount,
                    onValueChange = onCashAmountChange,
                    trailingIcon = {
                        Text(
                            text = currency,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = isExceeded,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_payment_cash_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isArabic) "المبلغ الآجل:" else "Debt Amount:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = debtAmount,
                    onValueChange = onDebtAmountChange,
                    trailingIcon = {
                        Text(
                            text = currency,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = isExceeded,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_payment_debt_input")
                )
            }

            // ==========================================
            // VALIDATION RULE HELPER / ERROR TEXT
            // Amounts entered can never exceed the transaction total
            // ==========================================
            Spacer(modifier = Modifier.height(6.dp))
            if (isExceeded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusRedBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = StatusRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) {
                            "تنبيه: المبلغ المدخل (${String.format(Locale.US, "%.2f", currentEnteredTotal)} $currency) يتجاوز إجمالي المعاملة (${String.format(Locale.US, "%.2f", transactionTotal)} $currency)!"
                        } else {
                            "Error: Entered amount exceeds transaction total!"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = StatusRed,
                        modifier = Modifier.testTag("quick_payment_validation_error")
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArabic) {
                            "ملاحظة: ${StoreStrings.AMOUNT_LIMIT_HELPER_AR} (الحد الأقصى: ${String.format(Locale.US, "%.2f %s", transactionTotal, currency)})"
                        } else {
                            "Note: ${StoreStrings.AMOUNT_LIMIT_HELPER_EN} (Max: ${String.format(Locale.US, "%.2f %s", transactionTotal, currency)})"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("quick_payment_validation_helper")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // NOTES FIELD (Multi-line, clearly labeled "(optional)")
            // ==========================================
            Text(
                text = if (isArabic) StoreStrings.NOTES_LABEL_AR else StoreStrings.NOTES_LABEL_EN,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                placeholder = {
                    Text(
                        text = if (isArabic) "اكتب أي تفاصيل إضافية عن الدفعة..." else "Enter any payment notes...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                minLines = 3,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GeoPrimary,
                    unfocusedBorderColor = GeoOutlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_payment_notes_input")
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Customer Picker Dialog
    if (showCustomerPicker) {
        CustomerSelectionDialog(
            customers = allCustomers,
            selectedCustomerId = customer?.id,
            isArabic = isArabic,
            onDismiss = { showCustomerPicker = false },
            onSelectCustomer = {
                onCustomerChange(it)
                showCustomerPicker = false
            }
        )
    }

    // Unified Settlement Summary Confirmation Dialog
    if (showSettlementDialog) {
        AlertDialog(
            onDismissRequest = { showSettlementDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusGreen,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (isArabic) "ملخص التسوية الموحدة" else "Unified Settlement Summary",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isArabic) "تم تأكيد عملية الدفع السريع بنجاح وفق التفاصيل التالية:" else "Quick payment confirmed successfully with the following details:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isArabic) "العميل: ${customer?.customerName}" else "Customer: ${customer?.customerName}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (isArabic) "نوع التسوية: ${if (settlementType == SettlementType.FULL) "كامل" else "جزئي"}" else "Settlement Type: ${settlementType.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = if (isArabic) "المبلغ: ${String.format(Locale.US, "%.2f %s", currentEnteredTotal, currency)}" else "Amount: ${String.format(Locale.US, "%.2f %s", currentEnteredTotal, currency)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = GeoPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettlementDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                ) {
                    Text(text = if (isArabic) "حسناً" else "OK")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

/**
 * Customer Selection Dialog for Quick Payment
 */
@Composable
private fun CustomerSelectionDialog(
    customers: List<CustomerAccount>,
    selectedCustomerId: String?,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSelectCustomer: (CustomerAccount) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isArabic) StoreStrings.SELECT_CUSTOMER_AR else StoreStrings.SELECT_CUSTOMER_EN,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isArabic) "اختر عميلاً لتطبيق الدفع السريع:" else "Select customer for quick payment:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                ) {
                    items(customers, key = { it.id }) { cust ->
                        val isSelected = cust.id == selectedCustomerId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) GeoPrimary.copy(alpha = 0.12f)
                                    else Color.Transparent
                                )
                                .clickable { onSelectCustomer(cust) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cust.customerName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = cust.phone,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        HorizontalDivider(
                            color = GeoOutlineVariant.copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (isArabic) "إلغاء" else "Cancel", color = GeoPrimary)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
