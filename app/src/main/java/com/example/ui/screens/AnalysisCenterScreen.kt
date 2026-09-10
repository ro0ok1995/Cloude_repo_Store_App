package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.PeriodFilter
import com.example.model.SettlementType
import com.example.model.StoreInfo
import com.example.model.StoreStrings
import com.example.model.TransactionItem
import com.example.ui.components.SimpleEmptyState
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import com.example.util.ReportExporter
import com.example.util.ReportPreviewRow
import com.example.util.StatementRow
import com.example.viewmodel.AnalysisCenterUiState
import com.example.viewmodel.AnalysisCenterViewModel
import com.example.viewmodel.AnalysisTab
import com.example.viewmodel.ReportType
import com.example.viewmodel.StatementTxFilter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun AnalysisCenterScreen(
    viewModel: AnalysisCenterViewModel,
    customers: List<CustomerAccount>,
    transactions: List<TransactionItem>,
    storeInfo: StoreInfo,
    languageMode: LanguageMode,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isArabic = languageMode == LanguageMode.ARABIC
    val currency = if (isArabic) "ر.س" else "SAR"
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var showCustomerDialog by remember { mutableStateOf(false) }

    // Calculate active period based on lock state
    val activePeriod = viewModel.getActivePeriod()

    // Progressive disclosure checks:
    // Period selector: shown for Statistics and Account Statement, and for Reports ONLY when Comprehensive Customer report is active.
    val shouldShowPeriodSelector = when (uiState.currentTab) {
        AnalysisTab.STATISTICS, AnalysisTab.ACCOUNT_STATEMENT -> true
        AnalysisTab.REPORTS -> uiState.selectedReportType == ReportType.COMPREHENSIVE_CUSTOMER
    }

    // Customer selector: shown on Statistics and Account Statement, and conditionally on Reports for Comprehensive Customer report.
    val shouldShowCustomerSelector = when (uiState.currentTab) {
        AnalysisTab.STATISTICS, AnalysisTab.ACCOUNT_STATEMENT -> true
        AnalysisTab.REPORTS -> uiState.selectedReportType == ReportType.COMPREHENSIVE_CUSTOMER
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("analysis_center_screen")
    ) {
        // 1. TOP TAB ROW (Statistics | Account Statement | Reports)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = uiState.currentTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = GeoPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.currentTab.ordinal]),
                            height = 3.dp,
                            color = GeoPrimary
                        )
                    },
                    divider = {
                        HorizontalDivider(color = GeoOutlineVariant)
                    },
                    modifier = Modifier.testTag("analysis_tab_row")
                ) {
                    Tab(
                        selected = uiState.currentTab == AnalysisTab.STATISTICS,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.selectTab(AnalysisTab.STATISTICS)
                        },
                        text = {
                            Text(
                                text = if (isArabic) StoreStrings.TAB_STATISTICS_AR else StoreStrings.TAB_STATISTICS_EN,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (uiState.currentTab == AnalysisTab.STATISTICS) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        },
                        modifier = Modifier.testTag("tab_statistics")
                    )
                    Tab(
                        selected = uiState.currentTab == AnalysisTab.ACCOUNT_STATEMENT,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.selectTab(AnalysisTab.ACCOUNT_STATEMENT)
                        },
                        text = {
                            Text(
                                text = if (isArabic) StoreStrings.TAB_STATEMENT_AR else StoreStrings.TAB_STATEMENT_EN,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (uiState.currentTab == AnalysisTab.ACCOUNT_STATEMENT) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        },
                        modifier = Modifier.testTag("tab_statement")
                    )
                    Tab(
                        selected = uiState.currentTab == AnalysisTab.REPORTS,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.selectTab(AnalysisTab.REPORTS)
                        },
                        text = {
                            Text(
                                text = if (isArabic) StoreStrings.TAB_REPORTS_AR else StoreStrings.TAB_REPORTS_EN,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (uiState.currentTab == AnalysisTab.REPORTS) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        },
                        modifier = Modifier.testTag("tab_reports")
                    )
                }

                // 2. SHARED, LOCKABLE PERIOD SELECTOR (Progressive disclosure)
                if (shouldShowPeriodSelector) {
                    PeriodSelectorLockableRow(
                        selectedPeriod = activePeriod,
                        isLocked = uiState.isPeriodLocked,
                        isArabic = isArabic,
                        onSelectPeriod = { viewModel.selectPeriod(it) },
                        onToggleLock = { viewModel.togglePeriodLock() }
                    )
                }

                // 3. SHARED CUSTOMER CONTEXT SELECTOR (Progressive disclosure)
                if (shouldShowCustomerSelector) {
                    CustomerContextBar(
                        selectedCustomer = uiState.selectedCustomer,
                        isArabic = isArabic,
                        isRequired = uiState.currentTab == AnalysisTab.REPORTS && uiState.selectedReportType == ReportType.COMPREHENSIVE_CUSTOMER,
                        onClickSelect = {
                            focusManager.clearFocus()
                            showCustomerDialog = true
                        },
                        onClear = {
                            focusManager.clearFocus()
                            viewModel.clearCustomer()
                        }
                    )
                }
            }
        }

        // 4. ACTIVE TAB CONTENT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (uiState.currentTab) {
                AnalysisTab.STATISTICS -> {
                    StatisticsTabContent(
                        transactions = transactions,
                        selectedCustomer = uiState.selectedCustomer,
                        activePeriod = activePeriod,
                        currency = currency,
                        isArabic = isArabic,
                        context = context,
                        storeInfo = storeInfo
                    )
                }
                AnalysisTab.ACCOUNT_STATEMENT -> {
                    AccountStatementTabContent(
                        viewModel = viewModel,
                        uiState = uiState,
                        allTransactions = transactions,
                        activePeriod = activePeriod,
                        currency = currency,
                        isArabic = isArabic,
                        context = context,
                        storeInfo = storeInfo
                    )
                }
                AnalysisTab.REPORTS -> {
                    ReportsTabContent(
                        viewModel = viewModel,
                        uiState = uiState,
                        customers = customers,
                        allTransactions = transactions,
                        activePeriod = activePeriod,
                        currency = currency,
                        isArabic = isArabic,
                        context = context,
                        storeInfo = storeInfo,
                        onSelectCustomerRequest = { showCustomerDialog = true }
                    )
                }
            }
        }
    }

    // Customer Selection Dialog
    if (showCustomerDialog) {
        CustomerPickerModal(
            customers = customers,
            selectedCustomerId = uiState.selectedCustomer?.id,
            isArabic = isArabic,
            onDismiss = { showCustomerDialog = false },
            onSelect = {
                viewModel.selectCustomer(it)
                showCustomerDialog = false
            },
            onSelectAll = {
                viewModel.clearCustomer()
                showCustomerDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// PERIOD SELECTOR WITH LOCK/UNLOCK TOGGLE
// -------------------------------------------------------------
@Composable
private fun PeriodSelectorLockableRow(
    selectedPeriod: PeriodFilter,
    isLocked: Boolean,
    isArabic: Boolean,
    onSelectPeriod: (PeriodFilter) -> Unit,
    onToggleLock: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("period_selector_container")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Period Chips
            PeriodSelectorChip(
                label = if (isArabic) StoreStrings.PERIOD_TODAY_AR else StoreStrings.PERIOD_TODAY_EN,
                isSelected = selectedPeriod == PeriodFilter.TODAY,
                testTag = "period_chip_today",
                onClick = { onSelectPeriod(PeriodFilter.TODAY) },
                modifier = Modifier.weight(1f)
            )
            PeriodSelectorChip(
                label = if (isArabic) StoreStrings.PERIOD_WEEK_AR else StoreStrings.PERIOD_WEEK_EN,
                isSelected = selectedPeriod == PeriodFilter.WEEK,
                testTag = "period_chip_week",
                onClick = { onSelectPeriod(PeriodFilter.WEEK) },
                modifier = Modifier.weight(1f)
            )
            PeriodSelectorChip(
                label = if (isArabic) StoreStrings.PERIOD_MONTH_AR else StoreStrings.PERIOD_MONTH_EN,
                isSelected = selectedPeriod == PeriodFilter.MONTH,
                testTag = "period_chip_month",
                onClick = { onSelectPeriod(PeriodFilter.MONTH) },
                modifier = Modifier.weight(1f)
            )

            // Lock / Unlock Toggle Button
            IconButton(
                onClick = onToggleLock,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isLocked) GeoPrimary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .testTag("period_lock_toggle")
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = if (isLocked) {
                        if (isArabic) "الفترة مقفلة وموحدة عبر التبويبات" else "Locked: Period is shared across tabs"
                    } else {
                        if (isArabic) "الفترة غير مقفلة ومستقلة لكل تبويب" else "Unlocked: Period is independent per tab"
                    },
                    tint = if (isLocked) GeoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun PeriodSelectorChip(
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) null else BorderStroke(1.dp, GeoOutlineVariant),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 6.dp),
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

// -------------------------------------------------------------
// SHARED CUSTOMER CONTEXT BAR
// -------------------------------------------------------------
@Composable
private fun CustomerContextBar(
    selectedCustomer: CustomerAccount?,
    isArabic: Boolean,
    isRequired: Boolean = false,
    onClickSelect: () -> Unit,
    onClear: () -> Unit
) {
    Surface(
        color = if (isRequired && selectedCustomer == null) StatusRedBg.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(0.5.dp, if (isRequired && selectedCustomer == null) StatusRed.copy(alpha = 0.4f) else GeoOutlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_context_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onClickSelect() }
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (selectedCustomer != null) GeoPrimary else MaterialTheme.colorScheme.outlineVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (selectedCustomer != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isArabic) StoreStrings.CUSTOMER_CONTEXT_LABEL_AR else StoreStrings.CUSTOMER_CONTEXT_LABEL_EN,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedCustomer?.customerName ?: (if (isArabic) StoreStrings.ALL_CUSTOMERS_AR else StoreStrings.ALL_CUSTOMERS_EN),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedCustomer != null) GeoPrimary else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("customer_context_value")
                    )
                }
                if (isRequired && selectedCustomer == null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "(مطلوب)" else "(Required)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusRed
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedCustomer != null) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("clear_customer_context_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear customer filter",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                TextButton(
                    onClick = onClickSelect,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("change_customer_context_button")
                ) {
                    Text(
                        text = if (selectedCustomer == null) {
                            if (isArabic) "تحديد عميل" else "Select"
                        } else {
                            if (isArabic) "تغيير" else "Change"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = GeoPrimary
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: STATISTICS
// -------------------------------------------------------------
@Composable
private fun StatisticsTabContent(
    transactions: List<TransactionItem>,
    selectedCustomer: CustomerAccount?,
    activePeriod: PeriodFilter,
    currency: String,
    isArabic: Boolean,
    context: Context,
    storeInfo: StoreInfo
) {
    val filteredTransactions = remember(transactions, selectedCustomer, activePeriod) {
        var list = if (selectedCustomer != null) {
            transactions.filter { it.customerName.equals(selectedCustomer.customerName, ignoreCase = true) }
        } else {
            transactions
        }
        list.filter { tx ->
            when (activePeriod) {
                PeriodFilter.TODAY -> tx.date == "2026-09-05" || tx.relativeTime.contains("دقيقة") || tx.relativeTime.contains("ساعة") || tx.relativeTime.contains("الآن")
                PeriodFilter.WEEK -> tx.date >= "2026-08-30" || tx.date.startsWith("2026-09")
                PeriodFilter.MONTH -> tx.date.startsWith("2026-09")
                PeriodFilter.CUSTOM -> true
            }
        }
    }

    val totalCashSales = remember(filteredTransactions) {
        filteredTransactions
            .filter { !it.isCredit && (it.activityType.contains("كاش") || it.activityType.contains("Cash") || (!it.activityType.contains("تسديد") && !it.activityType.contains("Payment"))) }
            .sumOf { it.amount }
    }
    val totalDebtSales = remember(filteredTransactions) {
        filteredTransactions
            .filter { it.isCredit || it.activityType.contains("آجل") || it.activityType.contains("دين") }
            .sumOf { it.amount }
    }
    val fullSettlementAmount = remember(filteredTransactions) {
        filteredTransactions
            .filter { (it.activityType.contains("تسديد") || it.activityType.contains("Payment")) && (it.settlementType == SettlementType.FULL || it.settlementType == null) }
            .sumOf { it.amount }
    }
    val partialSettlementAmount = remember(filteredTransactions) {
        filteredTransactions
            .filter { (it.activityType.contains("تسديد") || it.activityType.contains("Payment") || it.activityType.contains("قسط")) && it.settlementType == SettlementType.PARTIAL }
            .sumOf { it.amount }
    }
    val totalPaymentsReceived = fullSettlementAmount + partialSettlementAmount
    val totalSales = totalCashSales + totalDebtSales
    val netBalance = totalDebtSales - totalPaymentsReceived

    // Donut percentages calculation matching HomeScreen.kt
    val totalVolume = totalDebtSales + totalCashSales + fullSettlementAmount + partialSettlementAmount
    val debtPercent = if (totalVolume > 0) ((totalDebtSales / totalVolume) * 100).roundToInt() else 0
    val cashPercent = if (totalVolume > 0) ((totalCashSales / totalVolume) * 100).roundToInt() else 0
    val fullPercent = if (totalVolume > 0) ((fullSettlementAmount / totalVolume) * 100).roundToInt() else 0
    val partialPercent = if (totalVolume > 0) (100 - debtPercent - cashPercent - fullPercent).coerceAtLeast(0) else 0

    val ringSegments = remember(debtPercent, cashPercent, fullPercent, partialPercent) {
        listOf(
            DonutSegment(debtPercent.toFloat(), StatusRed),
            DonutSegment(cashPercent.toFloat(), StatusBlue),
            DonutSegment(fullPercent.toFloat(), StatusGreen),
            DonutSegment(partialPercent.toFloat(), StatusAmber)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("statistics_tab_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High-level KPI grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiMetricCard(
                title = if (isArabic) StoreStrings.STAT_TOTAL_SALES_AR else StoreStrings.STAT_TOTAL_SALES_EN,
                value = String.format(Locale.US, "%,.2f %s", totalSales, currency),
                color = GeoPrimary,
                testTag = "kpi_total_sales",
                modifier = Modifier.weight(1f)
            )
            KpiMetricCard(
                title = if (isArabic) StoreStrings.STAT_DEBT_CREDIT_AR else StoreStrings.STAT_DEBT_CREDIT_EN,
                value = String.format(Locale.US, "%,.2f %s", totalDebtSales, currency),
                color = StatusRed,
                testTag = "kpi_debt_sales",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiMetricCard(
                title = if (isArabic) StoreStrings.STAT_PAYMENTS_RECEIVED_AR else StoreStrings.STAT_PAYMENTS_RECEIVED_EN,
                value = String.format(Locale.US, "%,.2f %s", totalPaymentsReceived, currency),
                color = StatusGreen,
                testTag = "kpi_payments_received",
                modifier = Modifier.weight(1f)
            )
            KpiMetricCard(
                title = if (isArabic) StoreStrings.STAT_CASH_SALES_AR else StoreStrings.STAT_CASH_SALES_EN,
                value = String.format(Locale.US, "%,.2f %s", totalCashSales, currency),
                color = StatusBlue,
                testTag = "kpi_cash_sales",
                modifier = Modifier.weight(1f)
            )
        }

        // Net Balance Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, GeoOutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("kpi_net_balance_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "صافي مستحقات الديون خلال الفترة" else "Net Outstanding Debt for Period",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isArabic) "(مبيعات الآجل - السدادات المستلمة)" else "(Debt Sales - Payments)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = String.format(Locale.US, "%,.2f %s", netBalance, currency),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    ),
                    color = if (netBalance > 0) StatusRed else StatusGreen,
                    modifier = Modifier.testTag("kpi_net_balance_value")
                )
            }
        }

        // DONUT CHART + LEGEND CARD (Referencing DebtRatioRingChart)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, GeoOutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("statistics_chart_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "توزيع حجم العمليات والديون" else "Volume & Debt Breakdown",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Donut Chart
                    Box(
                        modifier = Modifier.size(136.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        DebtRatioRingChart(
                            segments = ringSegments,
                            centerPercentage = debtPercent,
                            subtitle = if (isArabic) "نسبة الديون" else "Debt Ratio",
                            modifier = Modifier
                                .size(136.dp)
                                .testTag("statistics_donut_chart")
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 4-item Legend (Debt, Cash, Full Payment, Installments)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatisticsLegendRow(
                            dotColor = StatusRed,
                            label = if (isArabic) "آجل / ديون" else "Debt",
                            amount = totalDebtSales,
                            percentage = debtPercent,
                            currency = currency,
                            testTag = "stat_legend_debt"
                        )
                        StatisticsLegendRow(
                            dotColor = StatusBlue,
                            label = if (isArabic) "كاش / نقدي" else "Cash",
                            amount = totalCashSales,
                            percentage = cashPercent,
                            currency = currency,
                            testTag = "stat_legend_cash"
                        )
                        StatisticsLegendRow(
                            dotColor = StatusGreen,
                            label = if (isArabic) "تسديد كامل" else "Full Payment",
                            amount = fullSettlementAmount,
                            percentage = fullPercent,
                            currency = currency,
                            testTag = "stat_legend_payment_full"
                        )
                        StatisticsLegendRow(
                            dotColor = StatusAmber,
                            label = if (isArabic) "أقساط / جزئي" else "Installments",
                            amount = partialSettlementAmount,
                            percentage = partialPercent,
                            currency = currency,
                            testTag = "stat_legend_installment"
                        )
                    }
                }
            }
        }

        // STANDARDIZED EXPORT ACTIONS (PDF, CSV, Share)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, GeoOutlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isArabic) "تصدير الإحصائيات والمشاركة" else "Export & Share Statistics",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Export PDF Button
                    Button(
                        onClick = {
                            val headers = if (isArabic) listOf("البند", "القيمة", "النسبة", "العملة") else listOf("Metric", "Amount", "Percentage", "Currency")
                            val rows = listOf(
                                ReportPreviewRow(if (isArabic) "مبيعات الآجل" else "Debt Sales", String.format(Locale.US, "%.2f", totalDebtSales), "$debtPercent%", currency),
                                ReportPreviewRow(if (isArabic) "المبيعات النقدية" else "Cash Sales", String.format(Locale.US, "%.2f", totalCashSales), "$cashPercent%", currency),
                                ReportPreviewRow(if (isArabic) "تسديد كامل" else "Full Payment", String.format(Locale.US, "%.2f", fullSettlementAmount), "$fullPercent%", currency),
                                ReportPreviewRow(if (isArabic) "أقساط" else "Installments", String.format(Locale.US, "%.2f", partialSettlementAmount), "$partialPercent%", currency)
                            )
                            val kpis = listOf(
                                Pair(if (isArabic) "إجمالي المبيعات" else "Total Sales", String.format(Locale.US, "%.2f %s", totalSales, currency)),
                                Pair(if (isArabic) "المتحصلات" else "Payments", String.format(Locale.US, "%.2f %s", totalPaymentsReceived, currency)),
                                Pair(if (isArabic) "الصافي" else "Net Balance", String.format(Locale.US, "%.2f %s", netBalance, currency))
                            )
                            val title = if (isArabic) "تقرير إحصائيات المبيعات والديون" else "Sales & Debt Statistics Report"
                            val subtitle = selectedCustomer?.customerName ?: (if (isArabic) "كافة العملاء" else "All Customers")
                            val file = ReportExporter.createCachedPdf(
                                context = context,
                                fileName = "Stats_${System.currentTimeMillis()}.pdf",
                                title = title,
                                storeName = storeInfo.storeName,
                                subtitle = subtitle,
                                kpis = kpis,
                                headers = headers,
                                rows = rows
                            )
                            ReportExporter.shareFile(context, file, "application/pdf", title)
                            Toast.makeText(context, if (isArabic) "تم تجهيز تقرير PDF للمشاركة" else "PDF statistics ready for export", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("statistics_export_pdf_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isArabic) "PDF" else "PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Export CSV Button
                    OutlinedButton(
                        onClick = {
                            val headers = if (isArabic) listOf("البند", "القيمة", "النسبة", "العملة") else listOf("Metric", "Amount", "Percentage", "Currency")
                            val rows = listOf(
                                ReportPreviewRow(if (isArabic) "مبيعات الآجل" else "Debt Sales", String.format(Locale.US, "%.2f", totalDebtSales), "$debtPercent%", currency),
                                ReportPreviewRow(if (isArabic) "المبيعات النقدية" else "Cash Sales", String.format(Locale.US, "%.2f", totalCashSales), "$cashPercent%", currency),
                                ReportPreviewRow(if (isArabic) "تسديد كامل" else "Full Payment", String.format(Locale.US, "%.2f", fullSettlementAmount), "$fullPercent%", currency),
                                ReportPreviewRow(if (isArabic) "أقساط" else "Installments", String.format(Locale.US, "%.2f", partialSettlementAmount), "$partialPercent%", currency)
                            )
                            val csv = ReportExporter.generateReportCsv(headers, rows)
                            val file = ReportExporter.createCachedCsv(context, "Stats_${System.currentTimeMillis()}.csv", csv)
                            ReportExporter.shareFile(context, file, "text/csv", "Statistics CSV")
                            Toast.makeText(context, if (isArabic) "تم تجهيز ملف CSV للمشاركة" else "CSV statistics ready for export", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("statistics_export_csv_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isArabic) "CSV" else "CSV", color = GeoPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Share Text Button
                    OutlinedButton(
                        onClick = {
                            val scope = selectedCustomer?.customerName ?: (if (isArabic) "كافة العملاء" else "All Customers")
                            val shareText = buildString {
                                appendLine("SmallStore - ${storeInfo.storeName}")
                                appendLine(if (isArabic) "تقرير إحصائيات: $scope" else "Statistics Summary: $scope")
                                appendLine(if (isArabic) "إجمالي المبيعات: %,.2f %s".format(Locale.US, totalSales, currency) else "Total Sales: %,.2f %s".format(Locale.US, totalSales, currency))
                                appendLine(if (isArabic) "مبيعات الآجل: %,.2f %s (%d%%)".format(Locale.US, totalDebtSales, currency, debtPercent) else "Debt Sales: %,.2f %s (%d%%)".format(Locale.US, totalDebtSales, currency, debtPercent))
                                appendLine(if (isArabic) "مبيعات كاش: %,.2f %s (%d%%)".format(Locale.US, totalCashSales, currency, cashPercent) else "Cash Sales: %,.2f %s (%d%%)".format(Locale.US, totalCashSales, currency, cashPercent))
                                appendLine(if (isArabic) "تسديد كامل: %,.2f %s (%d%%)".format(Locale.US, fullSettlementAmount, currency, fullPercent) else "Full Payments: %,.2f %s (%d%%)".format(Locale.US, fullSettlementAmount, currency, fullPercent))
                                appendLine(if (isArabic) "أقساط: %,.2f %s (%d%%)".format(Locale.US, partialSettlementAmount, currency, partialPercent) else "Installments: %,.2f %s (%d%%)".format(Locale.US, partialSettlementAmount, currency, partialPercent))
                                appendLine(if (isArabic) "صافي الديون: %,.2f %s".format(Locale.US, netBalance, currency) else "Net Outstanding: %,.2f %s".format(Locale.US, netBalance, currency))
                            }
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Share Statistics"))
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("statistics_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isArabic) "مشاركة" else "Share", color = GeoPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun StatisticsLegendRow(
    dotColor: Color,
    label: String,
    amount: Double,
    percentage: Int,
    currency: String,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = String.format(Locale.US, "%,.0f %s", amount, currency),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "($percentage%)",
                style = MaterialTheme.typography.labelSmall,
                color = dotColor
            )
        }
    }
}

@Composable
private fun StatLegendIndicator(
    dotColor: Color,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    color: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, GeoOutlineVariant),
        modifier = modifier.testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                ),
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 2: ACCOUNT STATEMENT
// -------------------------------------------------------------
@Composable
private fun AccountStatementTabContent(
    viewModel: AnalysisCenterViewModel,
    uiState: AnalysisCenterUiState,
    allTransactions: List<TransactionItem>,
    activePeriod: PeriodFilter,
    currency: String,
    isArabic: Boolean,
    context: Context,
    storeInfo: StoreInfo
) {
    val focusManager = LocalFocusManager.current
    val rows = remember(
        allTransactions,
        uiState.selectedCustomer,
        uiState.statementFilter,
        uiState.statementSearchQuery,
        activePeriod
    ) {
        viewModel.computeStatementRows(
            allTransactions = allTransactions,
            selectedCustomer = uiState.selectedCustomer,
            filter = uiState.statementFilter,
            searchQuery = uiState.statementSearchQuery,
            period = activePeriod
        )
    }

    val totalOut = rows.filter { !it.isPayment }.sumOf { it.amount }
    val totalIn = rows.filter { it.isPayment }.sumOf { it.amount }
    val netBalance = totalOut - totalIn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("account_statement_tab_content")
    ) {
        // FILTERS HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Transaction type filter chips: All / Payment / Cash purchase / Debt purchase
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("statement_filter_chips_row"),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatementFilterChip(
                    label = if (isArabic) StoreStrings.TX_FILTER_ALL_AR else StoreStrings.TX_FILTER_ALL_EN,
                    isSelected = uiState.statementFilter == StatementTxFilter.ALL,
                    testTag = "filter_chip_all",
                    onClick = { viewModel.setStatementFilter(StatementTxFilter.ALL) },
                    modifier = Modifier.weight(1f)
                )
                StatementFilterChip(
                    label = if (isArabic) StoreStrings.TX_FILTER_PAYMENT_AR else StoreStrings.TX_FILTER_PAYMENT_EN,
                    isSelected = uiState.statementFilter == StatementTxFilter.PAYMENT,
                    testTag = "filter_chip_payment",
                    onClick = { viewModel.setStatementFilter(StatementTxFilter.PAYMENT) },
                    modifier = Modifier.weight(1f)
                )
                StatementFilterChip(
                    label = if (isArabic) StoreStrings.TX_FILTER_CASH_PURCHASE_AR else StoreStrings.TX_FILTER_CASH_PURCHASE_EN,
                    isSelected = uiState.statementFilter == StatementTxFilter.CASH_PURCHASE,
                    testTag = "filter_chip_cash_purchase",
                    onClick = { viewModel.setStatementFilter(StatementTxFilter.CASH_PURCHASE) },
                    modifier = Modifier.weight(1.2f)
                )
                StatementFilterChip(
                    label = if (isArabic) StoreStrings.TX_FILTER_DEBT_PURCHASE_AR else StoreStrings.TX_FILTER_DEBT_PURCHASE_EN,
                    isSelected = uiState.statementFilter == StatementTxFilter.DEBT_PURCHASE,
                    testTag = "filter_chip_debt_purchase",
                    onClick = { viewModel.setStatementFilter(StatementTxFilter.DEBT_PURCHASE) },
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.statementSearchQuery,
                    onValueChange = { viewModel.setStatementSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = if (isArabic) "بحث في العمليات..." else "Search transactions...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (uiState.statementSearchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.setStatementSearchQuery("")
                                focusManager.clearFocus()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoOutlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("statement_search_field")
                )

                // Quick Export PDF Button
                IconButton(
                    onClick = {
                        val previewRows = rows.map { row ->
                            ReportPreviewRow(
                                col1 = row.date,
                                col2 = row.customerName,
                                col3 = row.type,
                                col4 = String.format(Locale.US, "%.2f", if (row.isPayment) -row.amount else row.amount)
                            )
                        }
                        val headers = if (isArabic) listOf("التاريخ", "العميل", "النوع", "المبلغ") else listOf("Date", "Customer", "Type", "Amount")
                        val kpis = listOf(
                            Pair(if (isArabic) "إجمالي الوارد" else "Total In", String.format(Locale.US, "%.2f %s", totalIn, currency)),
                            Pair(if (isArabic) "إجمالي المنصرف" else "Total Out", String.format(Locale.US, "%.2f %s", totalOut, currency)),
                            Pair(if (isArabic) "الرصيد الصافي" else "Net Balance", String.format(Locale.US, "%.2f %s", netBalance, currency))
                        )
                        val title = if (isArabic) StoreStrings.TAB_STATEMENT_AR else StoreStrings.TAB_STATEMENT_EN
                        val subtitle = uiState.selectedCustomer?.customerName ?: (if (isArabic) "كافة العملاء" else "All Customers")
                        val file = ReportExporter.createCachedPdf(
                            context = context,
                            fileName = "Statement_${System.currentTimeMillis()}.pdf",
                            title = title,
                            storeName = storeInfo.storeName,
                            subtitle = subtitle,
                            kpis = kpis,
                            headers = headers,
                            rows = previewRows
                        )
                        ReportExporter.shareFile(context, file, "application/pdf", "Account Statement PDF")
                        Toast.makeText(context, if (isArabic) "تم تجهيز ملف PDF للمشاركة" else "PDF ready for export", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GeoPrimary.copy(alpha = 0.10f))
                        .testTag("statement_export_pdf_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "Export PDF",
                        tint = GeoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Quick Export CSV Button
                IconButton(
                    onClick = {
                        val csv = ReportExporter.generateStatementCsv(rows, isArabic)
                        val file = ReportExporter.createCachedCsv(context, "Statement_${System.currentTimeMillis()}.csv", csv)
                        ReportExporter.shareFile(context, file, "text/csv", "Account Statement CSV")
                        Toast.makeText(context, if (isArabic) "تم تجهيز ملف CSV للمشاركة" else "CSV ready for export", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GeoPrimary.copy(alpha = 0.10f))
                        .testTag("statement_export_csv_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Export CSV",
                        tint = GeoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Share Text Summary Button
                IconButton(
                    onClick = {
                        val text = ReportExporter.generateStatementShareText(
                            customerName = uiState.selectedCustomer?.customerName ?: (if (isArabic) "كافة العملاء" else "All Customers"),
                            rows = rows,
                            totalIn = totalIn,
                            totalOut = totalOut,
                            netBalance = netBalance,
                            isArabic = isArabic
                        )
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, "Share Statement"))
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GeoPrimary.copy(alpha = 0.10f))
                        .testTag("statement_share_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Text",
                        tint = GeoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = GeoOutlineVariant)

        // 3 DEDICATED SUMMARY CARDS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Summary card: Total In
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .weight(1f)
                    .testTag("statement_summary_in")
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (isArabic) "إجمالي الوارد" else "Total In",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format(Locale.US, "%,.2f", totalIn),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = StatusGreen
                    )
                }
            }

            // Summary card: Total Out
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .weight(1f)
                    .testTag("statement_summary_out")
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (isArabic) "إجمالي المنصرف" else "Total Out",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format(Locale.US, "%,.2f", totalOut),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = StatusRed
                    )
                }
            }

            // Summary card: Net Balance
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .weight(1f)
                    .testTag("statement_summary_net")
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (isArabic) "الرصيد الصافي" else "Net Balance",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format(Locale.US, "%,.2f", netBalance),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (netBalance > 0) StatusRed else StatusGreen,
                        modifier = Modifier.testTag("statement_net_balance_value")
                    )
                }
            }
        }

        HorizontalDivider(color = GeoOutlineVariant)

        // ROWS LIST
        if (rows.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                SimpleEmptyState(
                    message = if (isArabic) "لا توجد معاملات مطابقة للفلتر المحدد" else "No transactions found matching criteria",
                    icon = Icons.Default.ReceiptLong,
                    testTag = "statement_empty_state"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
                    .testTag("statement_rows_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                }
                items(rows, key = { it.id }) { row ->
                    StatementRowCard(row = row, currency = currency, isArabic = isArabic)
                }
                item {
                    // Totals Row at the bottom of the list
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, GeoOutlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("statement_totals_row")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isArabic) "الإجمالي (${rows.size} معاملة)" else "Total (${rows.size} txs)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isArabic) "الوارد: %,.2f | المنصرف: %,.2f".format(Locale.US, totalIn, totalOut)
                                    else "In: %,.2f | Out: %,.2f".format(Locale.US, totalIn, totalOut),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isArabic) "صافي الحساب" else "Net Total",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format(Locale.US, "%,.2f %s", netBalance, currency),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = if (netBalance > 0) StatusRed else StatusGreen
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StatementFilterChip(
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) null else BorderStroke(1.dp, GeoOutlineVariant),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StatementRowCard(
    row: StatementRow,
    currency: String,
    isArabic: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, GeoOutlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("statement_row_${row.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = row.customerName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${row.date} • ${row.description}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    color = if (row.isPayment) StatusGreenBg else if (row.isCreditDebt) StatusRedBg else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = row.type,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (row.isPayment) StatusGreen else if (row.isCreditDebt) StatusRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(
                color = GeoOutlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "المبلغ: " else "Amount: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format(Locale.US, "%s%,.2f %s", if (row.isPayment) "-" else "+", row.amount, currency),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (row.isPayment) StatusGreen else StatusRed
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "الرصيد التراكمي: " else "Running: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format(Locale.US, "%,.2f %s", row.runningBalance, currency),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (row.runningBalance > 0) StatusRed else StatusGreen
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: REPORTS
// -------------------------------------------------------------
@Composable
private fun ReportsTabContent(
    viewModel: AnalysisCenterViewModel,
    uiState: AnalysisCenterUiState,
    customers: List<CustomerAccount>,
    allTransactions: List<TransactionItem>,
    activePeriod: PeriodFilter,
    currency: String,
    isArabic: Boolean,
    context: Context,
    storeInfo: StoreInfo,
    onSelectCustomerRequest: () -> Unit
) {
    val reportTypes = listOf(
        ReportType.COMPREHENSIVE_CUSTOMER to (if (isArabic) StoreStrings.REPORT_COMPREHENSIVE_CUSTOMER_AR else StoreStrings.REPORT_COMPREHENSIVE_CUSTOMER_EN),
        ReportType.SALES_SUMMARY to (if (isArabic) StoreStrings.REPORT_SALES_SUMMARY_AR else StoreStrings.REPORT_SALES_SUMMARY_EN),
        ReportType.DEBT_BALANCES to (if (isArabic) StoreStrings.REPORT_DEBT_BALANCES_AR else StoreStrings.REPORT_DEBT_BALANCES_EN),
        ReportType.TAX_SUMMARY to (if (isArabic) StoreStrings.REPORT_TAX_SUMMARY_AR else StoreStrings.REPORT_TAX_SUMMARY_EN)
    )

    // Build data based on selected report type
    val isComprehensiveCustomer = uiState.selectedReportType == ReportType.COMPREHENSIVE_CUSTOMER
    val customerIsSelected = uiState.selectedCustomer != null

    // Generate table preview rows
    val previewRows = remember(uiState.selectedReportType, uiState.selectedCustomer, activePeriod, allTransactions, customers) {
        when (uiState.selectedReportType) {
            ReportType.COMPREHENSIVE_CUSTOMER -> {
                if (uiState.selectedCustomer == null) {
                    emptyList()
                } else {
                    val custTxs = allTransactions.filter { it.customerName.equals(uiState.selectedCustomer.customerName, ignoreCase = true) }
                    custTxs.map { tx ->
                        ReportPreviewRow(
                            col1 = tx.date,
                            col2 = if (tx.notes.isNotBlank()) tx.notes else tx.activityType,
                            col3 = tx.activityType,
                            col4 = String.format(Locale.US, "%.2f", tx.amount)
                        )
                    }
                }
            }
            ReportType.SALES_SUMMARY -> {
                allTransactions.take(15).map { tx ->
                    ReportPreviewRow(
                        col1 = tx.date,
                        col2 = tx.customerName,
                        col3 = tx.activityType,
                        col4 = String.format(Locale.US, "%.2f", tx.amount)
                    )
                }
            }
            ReportType.DEBT_BALANCES -> {
                customers.filter { it.balance > 0 }.map { cust ->
                    ReportPreviewRow(
                        col1 = cust.customerName,
                        col2 = cust.phone,
                        col3 = if (isArabic) "مدين" else "Debit",
                        col4 = String.format(Locale.US, "%.2f", cust.balance)
                    )
                }
            }
            ReportType.TAX_SUMMARY -> {
                allTransactions.take(10).map { tx ->
                    val tax = tx.amount * 0.15
                    ReportPreviewRow(
                        col1 = tx.date,
                        col2 = tx.activityType,
                        col3 = String.format(Locale.US, "%.2f", tx.amount),
                        col4 = String.format(Locale.US, "%.2f", tax)
                    )
                }
            }
        }
    }

    val tableHeaders = when (uiState.selectedReportType) {
        ReportType.COMPREHENSIVE_CUSTOMER -> listOf(
            if (isArabic) "التاريخ" else "Date",
            if (isArabic) "الوصف" else "Description",
            if (isArabic) "النوع" else "Type",
            if (isArabic) "المبلغ" else "Amount"
        )
        ReportType.SALES_SUMMARY -> listOf(
            if (isArabic) "التاريخ" else "Date",
            if (isArabic) "العميل" else "Customer",
            if (isArabic) "النوع" else "Type",
            if (isArabic) "المبلغ" else "Amount"
        )
        ReportType.DEBT_BALANCES -> listOf(
            if (isArabic) "العميل" else "Customer",
            if (isArabic) "الهاتف" else "Phone",
            if (isArabic) "الحالة" else "Status",
            if (isArabic) "الرصيد" else "Balance"
        )
        ReportType.TAX_SUMMARY -> listOf(
            if (isArabic) "التاريخ" else "Date",
            if (isArabic) "البيان" else "Detail",
            if (isArabic) "الإجمالي" else "Total",
            if (isArabic) "الضريبة (15%)" else "VAT (15%)"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("reports_tab_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Report Type Selector Card (2x2 Grid)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, GeoOutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("report_type_selector_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isArabic) StoreStrings.SELECT_REPORT_TYPE_AR else StoreStrings.SELECT_REPORT_TYPE_EN,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 2x2 Grid
                val chunkedReports = reportTypes.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    chunkedReports.forEach { rowPair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowPair.forEach { (type, label) ->
                                val isSelected = uiState.selectedReportType == type
                                val icon = when (type) {
                                    ReportType.COMPREHENSIVE_CUSTOMER -> Icons.Default.Person
                                    ReportType.SALES_SUMMARY -> Icons.Default.ReceiptLong
                                    ReportType.DEBT_BALANCES -> Icons.Default.Assessment
                                    ReportType.TAX_SUMMARY -> Icons.Default.TableChart
                                }
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) GeoPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) GeoPrimary else GeoOutlineVariant
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.selectReportType(type) }
                                        .testTag("report_option_${type.name}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) GeoPrimary else MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(GeoPrimary),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            if (rowPair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // 2. Warning if Comprehensive Customer Report selected but no customer picked
        if (isComprehensiveCustomer && !customerIsSelected) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = StatusRedBg),
                border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("comprehensive_customer_warning_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = StatusRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) StoreStrings.CUSTOMER_REQUIRED_FOR_REPORT_AR else StoreStrings.CUSTOMER_REQUIRED_FOR_REPORT_EN,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = StatusRed
                        )
                        Text(
                            text = if (isArabic) "يرجى تحديد العميل من شريط التحديد أعلاه أو بالضغط على الزر أدناه" else "Please select a customer to display statement and export",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusRed
                        )
                    }
                    Button(
                        onClick = onSelectCustomerRequest,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                        modifier = Modifier.testTag("report_pick_customer_button")
                    ) {
                        Text(if (isArabic) "اختيار" else "Select")
                    }
                }
            }
        }

        // 3. KPI / Summary Cards for Comprehensive Report
        if (isComprehensiveCustomer && customerIsSelected) {
            val cust = uiState.selectedCustomer!!
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                border = BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_customer_summary_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${if (isArabic) "بيانات العميل: " else "Customer: "} ${cust.customerName}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = cust.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "إجمالي المديونية" else "Total Debt",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format(Locale.US, "%,.2f %s", cust.totalDebt, currency),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = StatusRed
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isArabic) "الرصيد الصافي" else "Current Balance",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format(Locale.US, "%,.2f %s", cust.balance, currency),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (cust.balance > 0) StatusRed else StatusGreen
                            )
                        }
                    }
                }
            }
        }

        // 4. Report Preview Table
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, GeoOutlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("report_preview_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TableChart, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) StoreStrings.REPORT_PREVIEW_TITLE_AR else StoreStrings.REPORT_PREVIEW_TITLE_EN,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (isArabic) "${previewRows.size} صفوف" else "${previewRows.size} rows",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Table Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    tableHeaders.forEachIndexed { idx, title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(if (idx == 1) 1.5f else 1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (previewRows.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "لا توجد بيانات متاحة للمعاينة" else "No data available for preview",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    previewRows.take(12).forEachIndexed { idx, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (idx % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f) else Color.Transparent)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = row.col1, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                            Text(text = row.col2, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = row.col3, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                            Text(text = row.col4, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 5. Action Buttons (Export PDF, CSV, Print, Share)
        val canExport = !isComprehensiveCustomer || customerIsSelected

        val reportTitle = if (isArabic) {
            when (uiState.selectedReportType) {
                ReportType.COMPREHENSIVE_CUSTOMER -> "تقرير العميل الشامل - ${uiState.selectedCustomer?.customerName ?: ""}"
                ReportType.SALES_SUMMARY -> "تقرير ملخص المبيعات"
                ReportType.DEBT_BALANCES -> "تقرير أرصدة الديون"
                ReportType.TAX_SUMMARY -> "تقرير ضريبة القيمة المضافة"
            }
        } else {
            uiState.selectedReportType.name.replace("_", " ")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // PDF Export
            Button(
                onClick = {
                    if (canExport) {
                        val file = ReportExporter.createCachedPdf(
                            context = context,
                            fileName = "Report_${System.currentTimeMillis()}.pdf",
                            title = reportTitle,
                            storeName = storeInfo.storeName,
                            subtitle = if (isArabic) "تم التصدير من سمول ستور" else "Exported from SmallStore",
                            kpis = listOf("Total Rows" to "${previewRows.size}"),
                            headers = tableHeaders,
                            rows = previewRows
                        )
                        ReportExporter.shareFile(context, file, "application/pdf", reportTitle)
                    }
                },
                enabled = canExport,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("report_export_pdf_button")
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isArabic) StoreStrings.EXPORT_PDF_AR else StoreStrings.EXPORT_PDF_EN, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // CSV Export
            OutlinedButton(
                onClick = {
                    if (canExport) {
                        val csv = ReportExporter.generateReportCsv(tableHeaders, previewRows)
                        val file = ReportExporter.createCachedCsv(context, "Report_${System.currentTimeMillis()}.csv", csv)
                        ReportExporter.shareFile(context, file, "text/csv", reportTitle)
                        Toast.makeText(context, if (isArabic) "تم تجهيز ملف CSV للمشاركة" else "CSV report ready for export", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = canExport,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("report_export_csv_button")
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isArabic) "CSV" else "CSV", color = GeoPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Print HTML
            OutlinedButton(
                onClick = {
                    if (canExport) {
                        val html = ReportExporter.generateReportHtml(
                            title = reportTitle,
                            storeName = storeInfo.storeName,
                            subtitle = if (isArabic) "سمول ستور للتقارير" else "SmallStore Reports",
                            kpis = listOf((if (isArabic) "عدد السجلات" else "Records") to "${previewRows.size}"),
                            headers = tableHeaders,
                            rows = previewRows,
                            isArabic = isArabic
                        )
                        ReportExporter.printHtml(context, reportTitle, html, isArabic)
                    }
                },
                enabled = canExport,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("report_print_button")
            ) {
                Icon(Icons.Default.Print, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isArabic) StoreStrings.PRINT_REPORT_AR else StoreStrings.PRINT_REPORT_EN, color = GeoPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Share Text
            OutlinedButton(
                onClick = {
                    if (canExport) {
                        val shareText = buildString {
                            appendLine("SmallStore - ${storeInfo.storeName}")
                            appendLine(reportTitle)
                            appendLine(tableHeaders.joinToString(" | "))
                            previewRows.take(10).forEach { r ->
                                appendLine("${r.col1} | ${r.col2} | ${r.col3} | ${r.col4}")
                            }
                            if (previewRows.size > 10) {
                                appendLine("... (+${previewRows.size - 10} more)")
                            }
                        }
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, "Share Report"))
                    }
                },
                enabled = canExport,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("report_share_button")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isArabic) "مشاركة" else "Share", color = GeoPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------
// CUSTOMER PICKER MODAL
// -------------------------------------------------------------
@Composable
private fun CustomerPickerModal(
    customers: List<CustomerAccount>,
    selectedCustomerId: String?,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSelect: (CustomerAccount) -> Unit,
    onSelectAll: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var search by remember { mutableStateOf("") }
    val filtered = remember(customers, search) {
        if (search.isBlank()) customers
        else customers.filter { it.customerName.contains(search, ignoreCase = true) || it.phone.contains(search) }
    }

    AlertDialog(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismiss()
        },
        title = {
            Text(
                text = if (isArabic) "اختيار العميل" else "Select Customer",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text(if (isArabic) "بحث بالاسم أو الهاتف..." else "Search name or phone...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("customer_picker_search")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // "All Customers" option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedCustomerId == null) GeoPrimary.copy(alpha = 0.12f) else Color.Transparent)
                        .clickable {
                            focusManager.clearFocus()
                            onSelectAll()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("customer_picker_all_option"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isArabic) StoreStrings.ALL_CUSTOMERS_AR else StoreStrings.ALL_CUSTOMERS_EN,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (selectedCustomerId == null) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedCustomerId == null) GeoPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (selectedCustomerId == null) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = GeoOutlineVariant, modifier = Modifier.padding(vertical = 4.dp))

                if (filtered.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isArabic) "لا يوجد عملاء مطابقون" else "No matching customers found",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    ) {
                        items(filtered, key = { it.id }) { customer ->
                            val isSelected = customer.id == selectedCustomerId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GeoPrimary.copy(alpha = 0.12f) else Color.Transparent)
                                    .clickable {
                                        focusManager.clearFocus()
                                        onSelect(customer)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 9.dp)
                                    .testTag("customer_picker_item_${customer.id}"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = customer.customerName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = customer.phone,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = {
                focusManager.clearFocus()
                onDismiss()
            }) {
                Text(if (isArabic) "إغلاق" else "Close", color = GeoPrimary)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
