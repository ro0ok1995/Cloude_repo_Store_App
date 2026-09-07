package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.PeriodFilter
import com.example.model.StoreStrings
import com.example.model.TransactionItem
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import java.util.Locale

enum class AnalysisTab {
    STATISTICS,
    ACCOUNT_STATEMENT,
    REPORTS
}

/**
 * Filter option for Transaction Type in Account Statement
 */
enum class StatementTxFilter {
    ALL,
    PURCHASE,
    PAYMENT
}

/**
 * Itemized Statement Row Representation
 */
data class StatementRow(
    val id: String,
    val date: String,
    val customerName: String,
    val description: String,
    val type: String,
    val isPayment: Boolean,
    val amount: Double,
    val runningBalance: Double
)

/**
 * Report Types available in the guided Reports builder
 */
enum class ReportType {
    SALES,
    CUSTOMER_BALANCES,
    PAYMENTS,
    INVENTORY_MOVEMENT
}

/**
 * Data Grouping options for reports
 */
enum class ReportGrouping {
    DAILY,
    WEEKLY,
    MONTHLY
}

/**
 * Detail level options for reports
 */
enum class ReportDetailLevel {
    SUMMARY,
    DETAILED
}

/**
 * Report Type metadata item for Step 1 grid/list
 */
data class ReportTypeOption(
    val type: ReportType,
    val titleAr: String,
    val titleEn: String,
    val descAr: String,
    val descEn: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

/**
 * Sample row inside the Report Preview
 */
data class ReportPreviewRow(
    val col1: String,
    val col2: String,
    val col3: String,
    val col4: String
)

/**
 * ANALYSIS CENTER SCREEN of SmallStore.
 *
 * TOP-LEVEL STRUCTURE:
 * - Top bar title: "Analysis Center" ("مركز التحليلات").
 * - Directly below the top bar, three tabs in a row:
 *     "Statistics" ("الإحصائيات") | "Account Statement" ("كشف حساب") | "Reports" ("التقارير").
 *   Render "Statistics" as the active/selected tab.
 *
 * CONTEXT RULE (apply visually if a customer context exists):
 * - If arrived from a customer-driven flow (selectedCustomer != null), show a small chip
 *   with the customer's name and a clear/remove option.
 * - If arriving directly (Drawer -> Analysis Center), no customer chip is shown and the scope is shop-wide.
 *   Render the shop-wide (no chip) version for this prompt by default (selectedCustomer = null).
 *
 * STATISTICS TAB CONTENT:
 * - An independent period selector for this tab only (Today / Week / Month / Custom) — changing it
 *   must not be implied to affect the other two tabs.
 * - A simple search/filter row if relevant to statistics (kept minimal).
 * - A set of statistic cards/widgets arranged in a responsive grid appropriate for mobile (1–2 columns):
 *     1. Total Sales ("إجمالي المبيعات")
 *     2. Total Payments ("إجمالي المقبوضات")
 *     3. Total Outstanding Debt ("إجمالي الديون القائمة")
 *     4. Number of Transactions ("عدد المعاملات")
 * - One simple chart (flat bar chart) summarizing activity over the selected period (clean, flat, no 3D).
 *
 * CONSTRAINTS:
 * - Do not merge Account Statement or Reports content into this tab — this tab shows only statistics/summary
 *   widgets and one chart.
 * - Default simple light theme, RTL Arabic layout with plausible Arabic labels and numbers.
 */
@Composable
fun AnalysisCenterScreen(
    languageMode: LanguageMode = LanguageMode.ARABIC,
    selectedCustomer: CustomerAccount? = null,
    accounts: List<CustomerAccount> = emptyList(),
    totalBalance: Double = 10520.00,
    totalReceivables: Double = 13720.00,
    totalPayables: Double = 3200.00,
    transactions: List<TransactionItem> = emptyList(),
    onClearCustomer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Top three tabs state: "Reports" active as requested
    var selectedTab by remember { mutableStateOf(AnalysisTab.REPORTS) }

    // Independent period selector for the Statistics tab only (Today / Week / Month / Custom)
    var statsPeriod by remember { mutableStateOf(PeriodFilter.MONTH) }

    // Minimal search/filter query for Statistics
    var statsSearchQuery by remember { mutableStateOf("") }
    var showFilterActive by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("analysis_center_screen")
        ) {
            // ==============================================================
            // 1. TOP TABS ROW (Directly below the top bar)
            // Three tabs: "Statistics" | "Account Statement" | "Reports"
            // Render "Statistics" as the active/selected tab.
            // ==============================================================
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = GeoPrimary,
                    indicator = { tabPositions ->
                        if (selectedTab.ordinal < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                                color = GeoPrimary,
                                height = 3.dp
                            )
                        }
                    },
                    divider = {
                        HorizontalDivider(color = GeoOutlineVariant, thickness = 1.dp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analysis_tabs_row")
                ) {
                    // Tab 1: Statistics (Active)
                    Tab(
                        selected = selectedTab == AnalysisTab.STATISTICS,
                        onClick = { selectedTab = AnalysisTab.STATISTICS },
                        text = {
                            Text(
                                text = if (isArabic) StoreStrings.TAB_STATISTICS_AR else StoreStrings.TAB_STATISTICS_EN,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (selectedTab == AnalysisTab.STATISTICS) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        },
                        selectedContentColor = GeoPrimary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("tab_statistics")
                    )

                    // Tab 2: Account Statement
                    Tab(
                        selected = selectedTab == AnalysisTab.ACCOUNT_STATEMENT,
                        onClick = { selectedTab = AnalysisTab.ACCOUNT_STATEMENT },
                        text = {
                            Text(
                                text = if (isArabic) StoreStrings.TAB_ACCOUNT_STATEMENT_AR else StoreStrings.TAB_ACCOUNT_STATEMENT_EN,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (selectedTab == AnalysisTab.ACCOUNT_STATEMENT) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        },
                        selectedContentColor = GeoPrimary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("tab_account_statement")
                    )

                    // Tab 3: Reports
                    Tab(
                        selected = selectedTab == AnalysisTab.REPORTS,
                        onClick = { selectedTab = AnalysisTab.REPORTS },
                        text = {
                            Text(
                                text = if (isArabic) StoreStrings.TAB_REPORTS_AR else StoreStrings.TAB_REPORTS_EN,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (selectedTab == AnalysisTab.REPORTS) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        },
                        selectedContentColor = GeoPrimary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("tab_reports")
                    )
                }
            }

            // ==============================================================
            // 2. CONTEXT RULE: Customer Chip or Shop-wide Scope
            // If arriving from customer flow, show chip with remove option.
            // When direct (shop-wide), no chip is shown.
            // ==============================================================
            if (selectedCustomer != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("analysis_customer_chip")
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
                                    onClick = onClearCustomer,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("clear_analysis_customer_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = if (isArabic) "إزالة تصفية العميل" else "Clear customer filter",
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
            }

            // ==============================================================
            // 3. TAB CONTENT
            // Constraints: Statistics tab active, showing only statistics/summary widgets and one chart.
            // ==============================================================
            when (selectedTab) {
                AnalysisTab.STATISTICS -> {
                    StatisticsTabContent(
                        isArabic = isArabic,
                        selectedPeriod = statsPeriod,
                        onSelectPeriod = { statsPeriod = it },
                        searchQuery = statsSearchQuery,
                        onSearchChange = { statsSearchQuery = it },
                        showFilterActive = showFilterActive,
                        onToggleFilter = { showFilterActive = !showFilterActive },
                        totalSales = when (statsPeriod) {
                            PeriodFilter.TODAY -> 1850.00
                            PeriodFilter.WEEK -> 9450.00
                            PeriodFilter.MONTH -> 24800.00
                            PeriodFilter.CUSTOM -> 15200.00
                        },
                        totalPayments = when (statsPeriod) {
                            PeriodFilter.TODAY -> 1200.00
                            PeriodFilter.WEEK -> 6700.00
                            PeriodFilter.MONTH -> 18950.00
                            PeriodFilter.CUSTOM -> 11400.00
                        },
                        totalOutstandingDebt = if (selectedCustomer != null) {
                            selectedCustomer.totalDebt
                        } else {
                            totalReceivables
                        },
                        transactionCount = when (statsPeriod) {
                            PeriodFilter.TODAY -> 8
                            PeriodFilter.WEEK -> 34
                            PeriodFilter.MONTH -> 118
                            PeriodFilter.CUSTOM -> 56
                        }
                    )
                }

                AnalysisTab.ACCOUNT_STATEMENT -> {
                    AccountStatementTabContent(
                        isArabic = isArabic,
                        accounts = accounts,
                        selectedCustomerContext = selectedCustomer,
                        transactions = transactions
                    )
                }

                AnalysisTab.REPORTS -> {
                    ReportsTabContent(
                        isArabic = isArabic,
                        accounts = accounts,
                        transactions = transactions
                    )
                }
            }
        }
    }
}

/**
 * The inner content of the Statistics Tab.
 * Formatted with independent period selector, minimal search/filter row,
 * responsive 2-column statistic cards, and one clean, flat activity chart.
 */
@Composable
private fun StatisticsTabContent(
    isArabic: Boolean,
    selectedPeriod: PeriodFilter,
    onSelectPeriod: (PeriodFilter) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    showFilterActive: Boolean,
    onToggleFilter: () -> Unit,
    totalSales: Double,
    totalPayments: Double,
    totalOutstandingDebt: Double,
    transactionCount: Int
) {
    val currency = if (isArabic) "ر.س" else "SAR"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("statistics_tab_content")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // -------------------------------------------------------------
        // A. INDEPENDENT PERIOD SELECTOR (Today / Week / Month / Custom)
        // Strictly scoped to this tab only.
        // -------------------------------------------------------------
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stats_period_selector_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .testTag("stats_period_selector_row"),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatsPeriodChip(
                        label = if (isArabic) StoreStrings.PERIOD_TODAY_AR else StoreStrings.PERIOD_TODAY_EN,
                        isSelected = selectedPeriod == PeriodFilter.TODAY,
                        testTag = "stats_period_today",
                        onClick = { onSelectPeriod(PeriodFilter.TODAY) },
                        modifier = Modifier.weight(1f)
                    )
                    StatsPeriodChip(
                        label = if (isArabic) StoreStrings.PERIOD_WEEK_AR else StoreStrings.PERIOD_WEEK_EN,
                        isSelected = selectedPeriod == PeriodFilter.WEEK,
                        testTag = "stats_period_week",
                        onClick = { onSelectPeriod(PeriodFilter.WEEK) },
                        modifier = Modifier.weight(1f)
                    )
                    StatsPeriodChip(
                        label = if (isArabic) StoreStrings.PERIOD_MONTH_AR else StoreStrings.PERIOD_MONTH_EN,
                        isSelected = selectedPeriod == PeriodFilter.MONTH,
                        testTag = "stats_period_month",
                        onClick = { onSelectPeriod(PeriodFilter.MONTH) },
                        modifier = Modifier.weight(1f)
                    )
                    StatsPeriodChip(
                        label = if (isArabic) StoreStrings.PERIOD_CUSTOM_AR else StoreStrings.PERIOD_CUSTOM_EN,
                        isSelected = selectedPeriod == PeriodFilter.CUSTOM,
                        testTag = "stats_period_custom",
                        onClick = { onSelectPeriod(PeriodFilter.CUSTOM) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // B. SIMPLE SEARCH / FILTER ROW (Minimal — not overbuilt)
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stats_filter_row"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = if (isArabic) "البحث في العمليات والإحصائيات..." else "Search transactions & stats...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchChange("") },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoOutline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("stats_search_input")
                )

                // Filter toggle icon button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (showFilterActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (showFilterActive) GeoPrimary else GeoOutline
                    ),
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleFilter() }
                        .testTag("stats_filter_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = if (isArabic) "تصفية" else "Filter",
                            tint = if (showFilterActive) GeoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // C. STATISTIC CARDS / WIDGETS (Arranged in a responsive 2-column grid)
        // 1. Total Sales ("إجمالي المبيعات")
        // 2. Total Payments ("إجمالي المقبوضات")
        // 3. Total Outstanding Debt ("إجمالي الديون القائمة")
        // 4. Number of Transactions ("عدد المعاملات")
        // -------------------------------------------------------------
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stats_cards_grid"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1: Total Sales & Total Payments
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatisticCard(
                        title = if (isArabic) StoreStrings.TOTAL_SALES_AR else StoreStrings.TOTAL_SALES_EN,
                        value = String.format(Locale.US, "%,.2f %s", totalSales, currency),
                        subtitle = if (isArabic) "+12% عن الفترة السابقة" else "+12% vs last period",
                        isPositiveSubtitle = true,
                        valueColor = GeoPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_total_sales")
                    )

                    StatisticCard(
                        title = if (isArabic) StoreStrings.TOTAL_PAYMENTS_AR else StoreStrings.TOTAL_PAYMENTS_EN,
                        value = String.format(Locale.US, "%,.2f %s", totalPayments, currency),
                        subtitle = if (isArabic) "مقبوضات نقدية وبنكية" else "Cash & bank collected",
                        isPositiveSubtitle = null,
                        valueColor = StatusGreen,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_total_payments")
                    )
                }

                // Row 2: Total Outstanding Debt & Number of Transactions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatisticCard(
                        title = if (isArabic) StoreStrings.TOTAL_OUTSTANDING_DEBT_AR else StoreStrings.TOTAL_OUTSTANDING_DEBT_EN,
                        value = String.format(Locale.US, "%,.2f %s", totalOutstandingDebt, currency),
                        subtitle = if (isArabic) "مستحقات للتحصيل" else "Due for collection",
                        isPositiveSubtitle = false,
                        valueColor = StatusRed,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_outstanding_debt")
                    )

                    StatisticCard(
                        title = if (isArabic) StoreStrings.NUMBER_OF_TRANSACTIONS_AR else StoreStrings.NUMBER_OF_TRANSACTIONS_EN,
                        value = String.format(Locale.US, "%d", transactionCount),
                        subtitle = if (isArabic) "عملية مسجلة" else "recorded operations",
                        isPositiveSubtitle = null,
                        valueColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_card_transaction_count")
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // D. ONE SIMPLE FLAT CHART
        // Summarizing activity over the selected period. Kept clean, flat, no 3D.
        // -------------------------------------------------------------
        item {
            ActivityBarChartCard(
                isArabic = isArabic,
                selectedPeriod = selectedPeriod,
                currency = currency
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Responsive Statistic Widget Card.
 */
@Composable
private fun StatisticCard(
    title: String,
    value: String,
    subtitle: String,
    isPositiveSubtitle: Boolean?,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = when (isPositiveSubtitle) {
                    true -> StatusGreen
                    false -> StatusRed
                    null -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Clean, flat activity bar chart card.
 * Summarizes activity (Sales vs Payments) over discrete intervals.
 */
@Composable
private fun ActivityBarChartCard(
    isArabic: Boolean,
    selectedPeriod: PeriodFilter,
    currency: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("activity_chart_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Title & Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) StoreStrings.ACTIVITY_SUMMARY_AR else StoreStrings.ACTIVITY_SUMMARY_EN,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(
                        color = GeoPrimary,
                        label = if (isArabic) "مبيعات" else "Sales"
                    )
                    LegendItem(
                        color = StatusGreen,
                        label = if (isArabic) "مقبوضات" else "Payments"
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Chart bars data based on period
            val chartPoints = when (selectedPeriod) {
                PeriodFilter.TODAY -> listOf(
                    ChartBarPoint(label = if (isArabic) "صباحاً" else "Morning", salesRatio = 0.45f, paymentsRatio = 0.35f, salesVal = "450", payVal = "350"),
                    ChartBarPoint(label = if (isArabic) "ظهراً" else "Noon", salesRatio = 0.70f, paymentsRatio = 0.50f, salesVal = "700", payVal = "500"),
                    ChartBarPoint(label = if (isArabic) "مساءً" else "Evening", salesRatio = 0.85f, paymentsRatio = 0.65f, salesVal = "850", payVal = "650")
                )
                PeriodFilter.WEEK -> listOf(
                    ChartBarPoint(label = if (isArabic) "السبت" else "Sat", salesRatio = 0.55f, paymentsRatio = 0.40f, salesVal = "1.2k", payVal = "900"),
                    ChartBarPoint(label = if (isArabic) "الأحد" else "Sun", salesRatio = 0.75f, paymentsRatio = 0.60f, salesVal = "1.8k", payVal = "1.4k"),
                    ChartBarPoint(label = if (isArabic) "الإثنين" else "Mon", salesRatio = 0.40f, paymentsRatio = 0.30f, salesVal = "950", payVal = "700"),
                    ChartBarPoint(label = if (isArabic) "الثلاثاء" else "Tue", salesRatio = 0.90f, paymentsRatio = 0.80f, salesVal = "2.1k", payVal = "1.9k"),
                    ChartBarPoint(label = if (isArabic) "الأربعاء" else "Wed", salesRatio = 0.65f, paymentsRatio = 0.50f, salesVal = "1.5k", payVal = "1.2k"),
                    ChartBarPoint(label = if (isArabic) "الخميس" else "Thu", salesRatio = 0.85f, paymentsRatio = 0.70f, salesVal = "1.9k", payVal = "1.6k")
                )
                PeriodFilter.MONTH, PeriodFilter.CUSTOM -> listOf(
                    ChartBarPoint(label = if (isArabic) "أسبوع 1" else "Wk 1", salesRatio = 0.60f, paymentsRatio = 0.45f, salesVal = "5.8k", payVal = "4.2k"),
                    ChartBarPoint(label = if (isArabic) "أسبوع 2" else "Wk 2", salesRatio = 0.75f, paymentsRatio = 0.65f, salesVal = "6.9k", payVal = "5.5k"),
                    ChartBarPoint(label = if (isArabic) "أسبوع 3" else "Wk 3", salesRatio = 0.50f, paymentsRatio = 0.40f, salesVal = "4.6k", payVal = "3.8k"),
                    ChartBarPoint(label = if (isArabic) "أسبوع 4" else "Wk 4", salesRatio = 0.90f, paymentsRatio = 0.80f, salesVal = "7.5k", payVal = "6.4k")
                )
            }

            // Flat Chart Canvas / Bars Layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .testTag("flat_bar_chart"),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                chartPoints.forEach { point ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Bars Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            // Sales Bar
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(point.salesRatio.coerceIn(0.1f, 1f))
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(GeoPrimary)
                            )
                            // Payments Bar
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(point.paymentsRatio.coerceIn(0.1f, 1f))
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(StatusGreen)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Label underneath
                        Text(
                            text = point.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            HorizontalDivider(
                color = GeoOutlineVariant,
                modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)
            )

            // Bottom Chart Footnote
            Text(
                text = if (isArabic) "الرسم البياني يلخص مؤشرات الأداء الحالية للفترة المحددة بدقة." else "Chart summarizes performance indicators for the selected interval.",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class ChartBarPoint(
    val label: String,
    val salesRatio: Float,
    val paymentsRatio: Float,
    val salesVal: String,
    val payVal: String
)

/**
 * Custom Tab-scoped Period Chip
 */
@Composable
private fun StatsPeriodChip(
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.surface,
        label = "chip_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "chip_text"
    )

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant) else null,
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * The inner content of the Account Statement Tab.
 *
 * Requirements:
 * - Direct entry starts shop-wide (no customer selected) -> renders "Select Customer (optional)" field at the top.
 * - Filter row/panel with: Customer (optional), Product (optional dropdown), Transaction Type, and Date Range.
 * - Independent period/date control separate from the other two tabs.
 * - Summary bar above or below the list with totals: Total In, Total Out, and Net.
 * - Scrollable, itemized statement list with: date, description/type, amount, running balance.
 * - Simple "Export" or "Share" icon/button at the top/action bar.
 * - Constraints: No report-template selection or print-preview flow. Simple light theme, RTL Arabic layout.
 */
@Composable
fun AccountStatementTabContent(
    isArabic: Boolean,
    accounts: List<CustomerAccount> = emptyList(),
    selectedCustomerContext: CustomerAccount? = null,
    transactions: List<TransactionItem> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Customer selection (starts null unless arrived with customer context)
    var selectedCustomer by remember { mutableStateOf<CustomerAccount?>(selectedCustomerContext) }
    var customerDropdownExpanded by remember { mutableStateOf(false) }

    // Product selection (optional dropdown)
    val productOptions = if (isArabic) {
        listOf("جميع المنتجات", "سكر الأسرة 10 كجم", "أرز الشعلان 5 كجم", "زيت عافية ذرة 1.8 لتر", "حليب نيدو مجفف 2.5 كجم", "شاي ربيع 100 كيس")
    } else {
        listOf("All Products", "Al Osra Sugar 10kg", "Al Shalan Rice 5kg", "Afia Corn Oil 1.8L", "Nido Milk Powder 2.5kg", "Rabea Tea 100 Bags")
    }
    var selectedProductIndex by remember { mutableStateOf(0) }
    var productDropdownExpanded by remember { mutableStateOf(false) }

    // Transaction Type Filter (All / Purchases / Payments)
    var selectedTxType by remember { mutableStateOf(StatementTxFilter.ALL) }

    // Independent period / date range filter for this tab
    var statementPeriod by remember { mutableStateOf(PeriodFilter.MONTH) }

    // Sample/source statement rows
    val baseRows = remember(selectedCustomer, accounts) {
        listOf(
            StatementRow(
                id = "TX-101",
                date = "2026/04/01",
                customerName = if (isArabic) "مؤسسة الأمل للتجارة" else "Al-Amal Trading Est.",
                description = if (isArabic) "فاتورة مشتريات بضاعة متنوعة" else "Purchases invoice diverse goods",
                type = if (isArabic) "مشتريات" else "Purchase",
                isPayment = false,
                amount = 2450.00,
                runningBalance = 2450.00
            ),
            StatementRow(
                id = "TX-102",
                date = "2026/04/03",
                customerName = if (isArabic) "مؤسسة الأمل للتجارة" else "Al-Amal Trading Est.",
                description = if (isArabic) "سند قبض نقدي" else "Cash receipt voucher",
                type = if (isArabic) "دفعة نقدية" else "Payment",
                isPayment = true,
                amount = 1500.00,
                runningBalance = 950.00
            ),
            StatementRow(
                id = "TX-103",
                date = "2026/04/05",
                customerName = if (isArabic) "شركة النور للمواد الغذائية" else "Al-Noor Food Co.",
                description = if (isArabic) "فاتورة مشتريات زيت وأرز" else "Purchase invoice oil & rice",
                type = if (isArabic) "مشتريات" else "Purchase",
                isPayment = false,
                amount = 1800.00,
                runningBalance = 2750.00
            ),
            StatementRow(
                id = "TX-104",
                date = "2026/04/08",
                customerName = if (isArabic) "شركة النور للمواد الغذائية" else "Al-Noor Food Co.",
                description = if (isArabic) "تحويل بنكي دفعة حساب" else "Bank transfer account payment",
                type = if (isArabic) "دفعة بنكية" else "Payment",
                isPayment = true,
                amount = 1000.00,
                runningBalance = 1750.00
            ),
            StatementRow(
                id = "TX-105",
                date = "2026/04/10",
                customerName = if (isArabic) "سوبرماركت البركة" else "Al-Baraka Supermarket",
                description = if (isArabic) "فاتورة مشتريات معلبات وسكر" else "Purchase invoice canned goods",
                type = if (isArabic) "مشتريات" else "Purchase",
                isPayment = false,
                amount = 3200.00,
                runningBalance = 4950.00
            ),
            StatementRow(
                id = "TX-106",
                date = "2026/04/12",
                customerName = if (isArabic) "سوبرماركت البركة" else "Al-Baraka Supermarket",
                description = if (isArabic) "سند قبض نقدي مجزأ" else "Partial cash payment",
                type = if (isArabic) "دفعة نقدية" else "Payment",
                isPayment = true,
                amount = 2000.00,
                runningBalance = 2950.00
            ),
            StatementRow(
                id = "TX-107",
                date = "2026/04/14",
                customerName = if (isArabic) "تموينات الفجر" else "Al-Fajr Groceries",
                description = if (isArabic) "فاتورة مشتريات شاي وحليب" else "Purchase invoice tea & milk",
                type = if (isArabic) "مشتريات" else "Purchase",
                isPayment = false,
                amount = 1420.00,
                runningBalance = 4370.00
            ),
            StatementRow(
                id = "TX-108",
                date = "2026/04/15",
                customerName = if (isArabic) "تموينات الفجر" else "Al-Fajr Groceries",
                description = if (isArabic) "سداد دفعة سريعة" else "Quick payment settlement",
                type = if (isArabic) "دفعة نقدية" else "Payment",
                isPayment = true,
                amount = 800.00,
                runningBalance = 3570.00
            )
        )
    }

    // Filter rows based on selectedCustomer and selectedTxType
    val filteredRows = remember(baseRows, selectedCustomer, selectedTxType, selectedProductIndex) {
        baseRows.filter { row ->
            val matchCustomer = selectedCustomer == null || row.customerName.contains(selectedCustomer!!.customerName) || selectedCustomer!!.customerName.contains(row.customerName)
            val matchType = when (selectedTxType) {
                StatementTxFilter.ALL -> true
                StatementTxFilter.PURCHASE -> !row.isPayment
                StatementTxFilter.PAYMENT -> row.isPayment
            }
            matchCustomer && matchType
        }
    }

    // Dynamic totals calculation
    val totalIn = filteredRows.filter { it.isPayment }.sumOf { it.amount }
    val totalOut = filteredRows.filter { !it.isPayment }.sumOf { it.amount }
    val netBalance = totalOut - totalIn

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("account_statement_tab_content")
    ) {
        // -------------------------------------------------------------
        // Header & Quick Action Bar (Export & Share)
        // -------------------------------------------------------------
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isArabic) StoreStrings.ACCOUNT_STATEMENT_AR else StoreStrings.ACCOUNT_STATEMENT_EN,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (selectedCustomer != null) {
                                selectedCustomer!!.customerName
                            } else {
                                if (isArabic) StoreStrings.ALL_CUSTOMERS_SHOP_WIDE_AR else StoreStrings.ALL_CUSTOMERS_SHOP_WIDE_EN
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = GeoPrimary
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Export Action Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        if (isArabic) "تم تصدير كشف الحساب بنجاح" else "Statement exported successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .testTag("statement_export_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) StoreStrings.EXPORT_STATEMENT_AR else StoreStrings.EXPORT_STATEMENT_EN,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Share Action Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        if (isArabic) "مشاركة كشف الحساب" else "Share Statement",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .testTag("statement_share_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) StoreStrings.SHARE_STATEMENT_AR else StoreStrings.SHARE_STATEMENT_EN,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = GeoOutlineVariant, thickness = 1.dp)
        }

        // -------------------------------------------------------------
        // FILTERS PANEL
        // Customer, Product, Transaction Type, and Date Range
        // -------------------------------------------------------------
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("statement_filters_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Filter row title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isArabic) "خيارات التصفية والبحث" else "Filter & Query Options",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // 1. Customer Selection Field (Optional, direct entry starts shop-wide)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { customerDropdownExpanded = true }
                                .testTag("select_customer_filter_field")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (selectedCustomer != null) GeoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedCustomer?.customerName
                                            ?: if (isArabic) StoreStrings.SELECT_CUSTOMER_OPTIONAL_AR else StoreStrings.SELECT_CUSTOMER_OPTIONAL_EN,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (selectedCustomer != null) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (selectedCustomer != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (selectedCustomer != null) {
                                    IconButton(
                                        onClick = { selectedCustomer = null },
                                        modifier = Modifier.size(24.dp).testTag("clear_customer_filter_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = if (isArabic) "إلغاء التحديد" else "Clear selection",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Dropdown for customer selection
                        DropdownMenu(
                            expanded = customerDropdownExpanded,
                            onDismissRequest = { customerDropdownExpanded = false },
                            modifier = Modifier.testTag("customer_dropdown_menu")
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isArabic) "الكل (جميع العملاء - على مستوى المحل)" else "All (Shop-wide Scope)",
                                        fontWeight = if (selectedCustomer == null) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedCustomer == null) GeoPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    selectedCustomer = null
                                    customerDropdownExpanded = false
                                }
                            )
                            val customersList = if (accounts.isNotEmpty()) {
                                accounts
                            } else {
                                listOf(
                                    CustomerAccount("1", if (isArabic) "مؤسسة الأمل للتجارة" else "Al-Amal Trading Est.", 950.0, 950.0, "0501234567", "2026/04/10"),
                                    CustomerAccount("2", if (isArabic) "شركة النور للمواد الغذائية" else "Al-Noor Food Co.", 1750.0, 1750.0, "0559876543", "2026/04/12"),
                                    CustomerAccount("3", if (isArabic) "سوبرماركت البركة" else "Al-Baraka Supermarket", 2950.0, 2950.0, "0541122334", "2026/04/14"),
                                    CustomerAccount("4", if (isArabic) "تموينات الفجر" else "Al-Fajr Groceries", 3570.0, 3570.0, "0563344556", "2026/04/15")
                                )
                            }
                            customersList.forEach { account ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = account.customerName,
                                            fontWeight = if (selectedCustomer?.id == account.id) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedCustomer?.id == account.id) GeoPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        selectedCustomer = account
                                        customerDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 2. Product Selection (Optional dropdown) & Date Range row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Product Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { productDropdownExpanded = true }
                                    .testTag("select_product_filter_field")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = productOptions[selectedProductIndex],
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = productDropdownExpanded,
                                onDismissRequest = { productDropdownExpanded = false }
                            ) {
                                productOptions.forEachIndexed { idx, item ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = item,
                                                fontWeight = if (selectedProductIndex == idx) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selectedProductIndex == idx) GeoPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            selectedProductIndex = idx
                                            productDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Date Range Button (Independent date control)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    // Cycles through statement periods
                                    statementPeriod = when (statementPeriod) {
                                        PeriodFilter.MONTH -> PeriodFilter.TODAY
                                        PeriodFilter.TODAY -> PeriodFilter.WEEK
                                        PeriodFilter.WEEK -> PeriodFilter.CUSTOM
                                        PeriodFilter.CUSTOM -> PeriodFilter.MONTH
                                    }
                                }
                                .testTag("statement_date_range_picker")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = GeoPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (statementPeriod) {
                                            PeriodFilter.TODAY -> if (isArabic) StoreStrings.PERIOD_TODAY_AR else StoreStrings.PERIOD_TODAY_EN
                                            PeriodFilter.WEEK -> if (isArabic) StoreStrings.PERIOD_WEEK_AR else StoreStrings.PERIOD_WEEK_EN
                                            PeriodFilter.MONTH -> if (isArabic) StoreStrings.PERIOD_MONTH_AR else StoreStrings.PERIOD_MONTH_EN
                                            PeriodFilter.CUSTOM -> if (isArabic) StoreStrings.PERIOD_CUSTOM_AR else StoreStrings.PERIOD_CUSTOM_EN
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // 3. Transaction Type Chips (All / Purchases / Payments)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // All
                        FilterTypeChip(
                            label = if (isArabic) StoreStrings.ALL_TYPES_AR else StoreStrings.ALL_TYPES_EN,
                            isSelected = selectedTxType == StatementTxFilter.ALL,
                            testTag = "filter_tx_all",
                            onClick = { selectedTxType = StatementTxFilter.ALL },
                            modifier = Modifier.weight(1f)
                        )
                        // Purchases (المشتريات)
                        FilterTypeChip(
                            label = if (isArabic) StoreStrings.TX_FILTER_PURCHASE_AR else StoreStrings.TX_FILTER_PURCHASE_EN,
                            isSelected = selectedTxType == StatementTxFilter.PURCHASE,
                            testTag = "filter_tx_purchase",
                            onClick = { selectedTxType = StatementTxFilter.PURCHASE },
                            modifier = Modifier.weight(1f)
                        )
                        // Payments (المقبوضات / الدفعات)
                        FilterTypeChip(
                            label = if (isArabic) StoreStrings.TX_FILTER_PAYMENT_AR else StoreStrings.TX_FILTER_PAYMENT_EN,
                            isSelected = selectedTxType == StatementTxFilter.PAYMENT,
                            testTag = "filter_tx_payment",
                            onClick = { selectedTxType = StatementTxFilter.PAYMENT },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // SUMMARY BAR
        // Totals: Total In, Total Out, and Net Balance
        // -------------------------------------------------------------
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("statement_summary_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Total Out (المشتريات / المدين)
                    SummaryColumnItem(
                        label = if (isArabic) StoreStrings.TOTAL_OUT_AR else StoreStrings.TOTAL_OUT_EN,
                        amount = totalOut,
                        color = StatusRed,
                        isArabic = isArabic,
                        testTag = "summary_total_out"
                    )

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(GeoOutlineVariant)
                    )

                    // Total In (المقبوضات / الدائن)
                    SummaryColumnItem(
                        label = if (isArabic) StoreStrings.TOTAL_IN_AR else StoreStrings.TOTAL_IN_EN,
                        amount = totalIn,
                        color = StatusGreen,
                        isArabic = isArabic,
                        testTag = "summary_total_in"
                    )

                    // Vertical Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(GeoOutlineVariant)
                    )

                    // Net Balance (الرصيد الصافي)
                    SummaryColumnItem(
                        label = if (isArabic) StoreStrings.NET_BALANCE_AR else StoreStrings.NET_BALANCE_EN,
                        amount = netBalance,
                        color = GeoPrimary,
                        isArabic = isArabic,
                        testTag = "summary_net_balance"
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // Statement Table Header
        // -------------------------------------------------------------
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "التاريخ" else "Date",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(76.dp)
                    )
                    Text(
                        text = if (isArabic) "البيان / النوع" else "Description / Type",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (isArabic) "المبلغ" else "Amount",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(76.dp)
                    )
                    Text(
                        text = if (isArabic) StoreStrings.RUNNING_BALANCE_AR else StoreStrings.RUNNING_BALANCE_EN,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(84.dp)
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // Itemized Statement Rows (Scrollable List)
        // -------------------------------------------------------------
        if (filteredRows.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(36.dp)
                        .testTag("empty_statement_indicator"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) "لا توجد معاملات مطابقة لمعايير البحث" else "No transactions match the selected filters",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(filteredRows, key = { it.id }) { rowItem ->
                StatementItemRow(
                    row = rowItem,
                    isArabic = isArabic
                )
                HorizontalDivider(
                    color = GeoOutlineVariant.copy(alpha = 0.6f),
                    thickness = 0.8.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Filter Chip for Transaction Types
 */
@Composable
private fun FilterTypeChip(
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.surface,
        label = "filter_chip_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "filter_chip_text"
    )

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant) else null,
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * Summary Metric Column in Summary Bar
 */
@Composable
private fun SummaryColumnItem(
    label: String,
    amount: Double,
    color: Color,
    isArabic: Boolean,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = String.format(Locale.US, "%.2f", amount) + " " + if (isArabic) StoreStrings.CURRENCY_SAR_AR else StoreStrings.CURRENCY_SAR_EN,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

/**
 * Single Row in Itemized Statement List
 * Showing: Date, Description/Type, Amount, Running Balance
 */
@Composable
private fun StatementItemRow(
    row: StatementRow,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .testTag("statement_row_${row.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date column
            Text(
                text = row.date,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(76.dp)
            )

            // Description & Type column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (row.isPayment) StatusGreenBg else StatusRedBg
                    ) {
                        Text(
                            text = row.type,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (row.isPayment) StatusGreen else StatusRed,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = row.customerName,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Amount column
            Text(
                text = (if (row.isPayment) "-" else "+") + String.format(Locale.US, "%.2f", row.amount),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                color = if (row.isPayment) StatusGreen else StatusRed,
                textAlign = TextAlign.End,
                modifier = Modifier.width(76.dp)
            )

            // Running Balance column
            Text(
                text = String.format(Locale.US, "%.2f", row.runningBalance),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                modifier = Modifier.width(84.dp)
            )
        }
    }
}

/**
 * The inner content of the Reports Tab.
 * Formatted as a guided step-by-step vertical flow on one screen:
 * - Step 1: "Report Type" (selectable report type cards)
 * - Step 2: "Options" (archived accounts, grouping, detail level)
 * - Step 3: "Period" (independent period selector)
 * - Step 4: "Preview" (mocked-up document summary, KPIs, and sample rows)
 * - Step 5: Bottom action row with three buttons: "Generate" (primary filled), "Share", "Print" (secondary outlined).
 */
@Composable
fun ReportsTabContent(
    isArabic: Boolean,
    accounts: List<CustomerAccount> = emptyList(),
    transactions: List<TransactionItem> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Step 1: Report Type state (Customer Balances selected by default for rich display)
    var selectedReportType by remember { mutableStateOf(ReportType.CUSTOMER_BALANCES) }

    // Step 2: Options state
    var includeArchivedAccounts by remember { mutableStateOf(false) }
    var selectedGrouping by remember { mutableStateOf(ReportGrouping.WEEKLY) }
    var selectedDetailLevel by remember { mutableStateOf(ReportDetailLevel.DETAILED) }

    // Step 3: Period state (Independent of Statistics and Account Statement tabs)
    var reportPeriod by remember { mutableStateOf(PeriodFilter.MONTH) }

    val reportTypes = remember {
        listOf(
            ReportTypeOption(
                type = ReportType.CUSTOMER_BALANCES,
                titleAr = "تقرير أرصدة العملاء والديون",
                titleEn = "Customer Balances & Debts",
                descAr = "كشف تفصيلي بالذمم المدينة القائمة لكل عميل، ومطابقة الحسابات وأعمار الديون",
                descEn = "Detailed breakdown of receivables, debts per customer and aging analysis",
                icon = Icons.Default.AccountBalanceWallet
            ),
            ReportTypeOption(
                type = ReportType.SALES,
                titleAr = "تقرير المبيعات والفواتير",
                titleEn = "Sales & Invoices Report",
                descAr = "إجمالي فواتير المبيعات النقدية والآجلة، وتحليل الأصناف الأكثر طلباً وهوامش الربح",
                descEn = "Total cash & credit sales invoices, top selling products and gross margins",
                icon = Icons.Default.ShoppingCart
            ),
            ReportTypeOption(
                type = ReportType.PAYMENTS,
                titleAr = "تقرير المقبوضات والتحصيلات",
                titleEn = "Payments & Collections",
                descAr = "سجل عمليات السداد النقدية والتحويلات البنكية وإجمالي التدفقات المالية الواردة",
                descEn = "Log of cash receipts, bank transfers and reconciled collections",
                icon = Icons.Default.Payments
            ),
            ReportTypeOption(
                type = ReportType.INVENTORY_MOVEMENT,
                titleAr = "تقرير حركة الأصناف والمخزون",
                titleEn = "Inventory & Sales Movement",
                descAr = "كميات السحب والمبيعات وتكلفة البضاعة المباعة وتنبيهات نواقص الأصناف",
                descEn = "Product movement, sold quantities, cost of goods and low-stock alerts",
                icon = Icons.Default.Inventory
            )
        )
    }

    // Dynamic Preview Rows based on selected Report Type
    val previewRows = remember(selectedReportType) {
        when (selectedReportType) {
            ReportType.CUSTOMER_BALANCES -> listOf(
                ReportPreviewRow("مؤسسة الأمل", "0501234567", "2,450.00 ر.س", "نشط"),
                ReportPreviewRow("شركة النور", "0559876543", "1,750.00 ر.س", "منتظم"),
                ReportPreviewRow("سوبرماركت البركة", "0541122334", "4,950.00 ر.س", "متأخر"),
                ReportPreviewRow("تموينات الفجر", "0563344556", "4,570.00 ر.س", "متأخر")
            )
            ReportType.SALES -> listOf(
                ReportPreviewRow("INV-8821", "سوبرماركت البركة", "أرز وسكر وزيت", "3,200.00 ر.س"),
                ReportPreviewRow("INV-8822", "مؤسسة الأمل", "بضاعة تموينية", "2,450.00 ر.س"),
                ReportPreviewRow("INV-8823", "شركة النور", "زيوت ومشروبات", "1,800.00 ر.س"),
                ReportPreviewRow("INV-8824", "تموينات الفجر", "معلبات وشاي", "1,420.00 ر.س")
            )
            ReportType.PAYMENTS -> listOf(
                ReportPreviewRow("RCT-401", "سوبرماركت البركة", "سند قبض نقدي", "2,000.00 ر.س"),
                ReportPreviewRow("RCT-402", "مؤسسة الأمل", "سند قبض نقدي", "1,500.00 ر.س"),
                ReportPreviewRow("RCT-403", "شركة النور", "تحويل بنكي", "1,000.00 ر.س"),
                ReportPreviewRow("RCT-404", "تموينات الفجر", "سند قبض نقدي", "800.00 ر.س")
            )
            ReportType.INVENTORY_MOVEMENT -> listOf(
                ReportPreviewRow("سكر الأسرة 10 كجم", "120 كيس", "42.00 ر.س", "5,040.00 ر.س"),
                ReportPreviewRow("أرز الشعلان 5 كجم", "85 كيس", "38.50 ر.س", "3,272.50 ر.س"),
                ReportPreviewRow("زيت عافية 1.8 لتر", "140 حبة", "21.00 ر.س", "2,940.00 ر.س"),
                ReportPreviewRow("حليب نيدو 2.5 كجم", "65 علبة", "88.00 ر.س", "5,720.00 ر.س")
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_tab_content")
    ) {
        // Guided Report Builder Header Banner
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GeoPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "منشئ التقارير المالية" else "Financial Report Builder",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic)
                                "اتبع الخطوات المتتابعة لتخصيص ومعاينة وإصدار التقرير المطلوب ومشاركته أو طباعته"
                            else
                                "Follow the guided steps below to configure, preview and export your report",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ==========================================
        // STEP 1: REPORT TYPE ("نوع التقرير")
        // ==========================================
        item {
            ReportStepSectionHeader(
                stepNumber = 1,
                title = if (isArabic) "نوع التقرير" else "Report Type",
                subtitle = if (isArabic) "اختر نوع التقرير المطلوب إنشاؤه" else "Select the required report type",
                modifier = Modifier.testTag("step_report_type_header")
            )
        }

        items(reportTypes) { reportOption ->
            val isSelected = selectedReportType == reportOption.type
            ReportTypeSelectableCard(
                reportOption = reportOption,
                isSelected = isSelected,
                isArabic = isArabic,
                onClick = { selectedReportType = reportOption.type }
            )
        }

        // ==========================================
        // STEP 2: OPTIONS PANEL ("خيارات التقرير")
        // ==========================================
        item {
            Spacer(modifier = Modifier.height(12.dp))
            ReportStepSectionHeader(
                stepNumber = 2,
                title = if (isArabic) "خيارات التقرير" else "Report Options",
                subtitle = if (isArabic) "تخصيص معايير وعرض بيانات التقرير" else "Configure filtering & grouping parameters",
                modifier = Modifier.testTag("step_options_header")
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("step_options_section")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Option 1: Include / Exclude archived customers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includeArchivedAccounts = !includeArchivedAccounts },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "تضمين العملاء والحسابات المؤرشفة" else "Include archived customers",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isArabic)
                                    "إظهار الحسابات غير النشطة ضمن نتائج وأرصدة التقرير"
                                else
                                    "Include inactive accounts in calculations and rows",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = includeArchivedAccounts,
                            onCheckedChange = { includeArchivedAccounts = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GeoPrimary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.testTag("option_archived_accounts_switch")
                        )
                    }

                    HorizontalDivider(color = GeoOutlineVariant)

                    // Option 2: Group by Day / Week / Month
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isArabic) "طريقة تجميع البيانات:" else "Group by:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReportGroupingChip(
                                title = if (isArabic) "يومي" else "Daily",
                                isSelected = selectedGrouping == ReportGrouping.DAILY,
                                onClick = { selectedGrouping = ReportGrouping.DAILY },
                                modifier = Modifier.weight(1f)
                            )
                            ReportGroupingChip(
                                title = if (isArabic) "أسبوعي" else "Weekly",
                                isSelected = selectedGrouping == ReportGrouping.WEEKLY,
                                onClick = { selectedGrouping = ReportGrouping.WEEKLY },
                                modifier = Modifier.weight(1f)
                            )
                            ReportGroupingChip(
                                title = if (isArabic) "شهري" else "Monthly",
                                isSelected = selectedGrouping == ReportGrouping.MONTHLY,
                                onClick = { selectedGrouping = ReportGrouping.MONTHLY },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    HorizontalDivider(color = GeoOutlineVariant)

                    // Option 3: Detail Level (Summary vs Detailed)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isArabic) "مستوى تفاصيل التقرير:" else "Detail Level:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReportGroupingChip(
                                title = if (isArabic) "ملخص إجمالي" else "Summary",
                                isSelected = selectedDetailLevel == ReportDetailLevel.SUMMARY,
                                onClick = { selectedDetailLevel = ReportDetailLevel.SUMMARY },
                                modifier = Modifier.weight(1f)
                            )
                            ReportGroupingChip(
                                title = if (isArabic) "تفصيلي شامل" else "Detailed",
                                isSelected = selectedDetailLevel == ReportDetailLevel.DETAILED,
                                onClick = { selectedDetailLevel = ReportDetailLevel.DETAILED },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // STEP 3: PERIOD SELECTOR ("فترة التقرير")
        // ==========================================
        item {
            Spacer(modifier = Modifier.height(12.dp))
            ReportStepSectionHeader(
                stepNumber = 3,
                title = if (isArabic) "فترة التقرير" else "Report Period",
                subtitle = if (isArabic) "تحديد النطاق الزمني المستهدف للتقرير (مستقل)" else "Select specific date range for this report",
                modifier = Modifier.testTag("step_period_header")
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("step_period_section")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Segmented period filter chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PeriodFilter.values().forEach { period ->
                            val isSelected = reportPeriod == period
                            val label = when (period) {
                                PeriodFilter.TODAY -> if (isArabic) "اليوم" else "Today"
                                PeriodFilter.WEEK -> if (isArabic) "هذا الأسبوع" else "Week"
                                PeriodFilter.MONTH -> if (isArabic) "هذا الشهر" else "Month"
                                PeriodFilter.CUSTOM -> if (isArabic) "مخصص" else "Custom"
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { reportPeriod = period }
                                    .testTag("report_period_chip_${period.name}")
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Active range banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GeoPrimary.copy(alpha = 0.05f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val periodRangeText = when (reportPeriod) {
                                PeriodFilter.TODAY -> if (isArabic) "تاريخ اليوم: 2026/04/15" else "Today: 2026/04/15"
                                PeriodFilter.WEEK -> if (isArabic) "من 2026/04/09 إلى 2026/04/15 (آخر 7 أيام)" else "From 2026/04/09 to 2026/04/15 (Last 7 Days)"
                                PeriodFilter.MONTH -> if (isArabic) "من 2026/04/01 إلى 2026/04/30 (شهر كامل)" else "From 2026/04/01 to 2026/04/30 (Full Month)"
                                PeriodFilter.CUSTOM -> if (isArabic) "نطاق زمني مخصص محدد من المستخدم" else "Custom defined date range"
                            }
                            Text(
                                text = periodRangeText,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = GeoPrimary
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // STEP 4: PREVIEW AREA ("معاينة التقرير")
        // ==========================================
        item {
            Spacer(modifier = Modifier.height(12.dp))
            ReportStepSectionHeader(
                stepNumber = 4,
                title = if (isArabic) "معاينة التقرير" else "Report Preview",
                subtitle = if (isArabic) "نموذج حي لمحتوى التقرير النهائي بناءً على المعايير" else "Live document preview with sample records",
                modifier = Modifier.testTag("step_preview_header")
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GeoOutline),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("report_preview_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Document Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "تموينات الأمل المركزية" else "SmallStore Al-Amal",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isArabic) "سجل تجاري: 1010893421" else "CR: 1010893421",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusGreenBg
                        ) {
                            Text(
                                text = if (isArabic) "تقرير رسمي معتمد" else "Official Report",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                color = StatusGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Report Title Banner inside Document
                    val reportTitleText = when (selectedReportType) {
                        ReportType.CUSTOMER_BALANCES -> if (isArabic) "تقرير أرصدة العملاء والذمم المدينة" else "Customer Balances & Receivables Report"
                        ReportType.SALES -> if (isArabic) "تقرير المبيعات والفواتير الإجمالي" else "Comprehensive Sales & Invoices Report"
                        ReportType.PAYMENTS -> if (isArabic) "تقرير المقبوضات والتحصيلات المالية" else "Cash & Collections Receipt Report"
                        ReportType.INVENTORY_MOVEMENT -> if (isArabic) "تقرير حركة الأصناف والمخزون" else "Inventory & Sales Movement Report"
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = reportTitleText,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val groupingText = when (selectedGrouping) {
                                ReportGrouping.DAILY -> if (isArabic) "يومي" else "Daily"
                                ReportGrouping.WEEKLY -> if (isArabic) "أسبوعي" else "Weekly"
                                ReportGrouping.MONTHLY -> if (isArabic) "شهري" else "Monthly"
                            }
                            val detailText = when (selectedDetailLevel) {
                                ReportDetailLevel.SUMMARY -> if (isArabic) "ملخص" else "Summary"
                                ReportDetailLevel.DETAILED -> if (isArabic) "تفصيلي" else "Detailed"
                            }
                            Text(
                                text = if (isArabic)
                                    "الفترة: أبريل 2026 • التجميع: $groupingText • المستوى: $detailText • 2026/04/15"
                                else
                                    "Period: April 2026 • Group: $groupingText • Level: $detailText • 2026/04/15",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(color = GeoOutlineVariant)

                    // 3 KPI Summary Metrics inside Document Preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (selectedReportType) {
                            ReportType.CUSTOMER_BALANCES -> {
                                PreviewStatPill(
                                    title = if (isArabic) "إجمالي الذمم" else "Total Debt",
                                    value = "13,720 ر.س",
                                    valueColor = StatusRed,
                                    modifier = Modifier.weight(1f)
                                )
                                PreviewStatPill(
                                    title = if (isArabic) "عدد المدينين" else "Debtors",
                                    value = "4 عملاء",
                                    valueColor = GeoPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                PreviewStatPill(
                                    title = if (isArabic) "ديون متأخرة" else "Overdue",
                                    value = "3,570 ر.س",
                                    valueColor = StatusRed,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            ReportType.SALES -> {
                                PreviewStatPill(
                                    title = if (isArabic) "إجمالي المبيعات" else "Total Sales",
                                    value = "24,580 ر.س",
                                    valueColor = GeoPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                PreviewStatPill(
                                    title = if (isArabic) "عدد الفواتير" else "Invoices",
                                    value = "86 فاتورة",
                                    valueColor = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                PreviewStatPill(
                                    title = if (isArabic) "هامش الربح" else "Margin",
                                    value = "18.5%",
                                    valueColor = StatusGreen,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            ReportType.PAYMENTS -> {
                                PreviewStatPill(
                                    title = if (isArabic) "المتحصلات" else "Collected",
                                    value = "18,400 ر.س",
                                    valueColor = StatusGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                PreviewStatPill(
                                    title = if (isArabic) "سداد نقدي" else "Cash",
                                    value = "12,100 ر.س",
                                    valueColor = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                PreviewStatPill(
                                    title = if (isArabic) "تحويلات" else "Bank",
                                    value = "6,300 ر.س",
                                    valueColor = GeoPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            ReportType.INVENTORY_MOVEMENT -> {
                                PreviewStatPill(
                                    title = if (isArabic) "قيمة المخزون" else "Stock Value",
                                    value = "38,900 ر.س",
                                    valueColor = GeoPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                PreviewStatPill(
                                    title = if (isArabic) "أصناف نشطة" else "Active Items",
                                    value = "48 صنف",
                                    valueColor = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                PreviewStatPill(
                                    title = if (isArabic) "نواقص المخزون" else "Low Stock",
                                    value = "3 أصناف",
                                    valueColor = StatusRed,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = GeoOutlineVariant)

                    // Preview Table Header
                    val colHeaders = when (selectedReportType) {
                        ReportType.CUSTOMER_BALANCES -> listOf(
                            if (isArabic) "العميل" else "Customer",
                            if (isArabic) "الهاتف" else "Phone",
                            if (isArabic) "الرصيد القائم" else "Balance",
                            if (isArabic) "الحالة" else "Status"
                        )
                        ReportType.SALES -> listOf(
                            if (isArabic) "الفاتورة" else "Invoice",
                            if (isArabic) "العميل" else "Customer",
                            if (isArabic) "البيان" else "Items",
                            if (isArabic) "المبلغ" else "Amount"
                        )
                        ReportType.PAYMENTS -> listOf(
                            if (isArabic) "السند" else "Receipt",
                            if (isArabic) "العميل" else "Customer",
                            if (isArabic) "النوع" else "Method",
                            if (isArabic) "المبلغ" else "Amount"
                        )
                        ReportType.INVENTORY_MOVEMENT -> listOf(
                            if (isArabic) "الصنف" else "Product",
                            if (isArabic) "الكمية" else "Qty",
                            if (isArabic) "السعر" else "Price",
                            if (isArabic) "الإجمالي" else "Total"
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = colHeaders[0],
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                modifier = Modifier.weight(1.3f)
                            )
                            Text(
                                text = colHeaders[1],
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                modifier = Modifier.weight(1.1f)
                            )
                            Text(
                                text = colHeaders[2],
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                modifier = Modifier.weight(1.1f),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = colHeaders[3],
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                modifier = Modifier.weight(0.8f),
                                textAlign = TextAlign.End
                            )
                        }
                    }

                    // Sample Table Rows
                    previewRows.forEachIndexed { index, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.col1,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1.3f)
                            )
                            Text(
                                text = row.col2,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1.1f)
                            )
                            Text(
                                text = row.col3,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                color = if (selectedReportType == ReportType.CUSTOMER_BALANCES) StatusRed else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1.1f)
                            )
                            Text(
                                text = row.col4,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (row.col4 == "متأخر") StatusRed else StatusGreen,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(0.8f)
                            )
                        }
                        if (index < previewRows.size - 1) {
                            HorizontalDivider(color = GeoOutlineVariant.copy(alpha = 0.5f))
                        }
                    }

                    // Document Footer watermark
                    HorizontalDivider(color = GeoOutlineVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "صفحة 1 من 1 • تم الإنشاء بواسطة نظام SmallStore" else "Page 1 of 1 • Generated via SmallStore",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isArabic) "ختم إلكتروني آلي" else "E-Signed",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = GeoPrimary
                        )
                    }
                }
            }
        }

        // ==========================================
        // STEP 5: BOTTOM ACTIONS ROW ("توليد / مشاركة / طباعة")
        // ==========================================
        item {
            Spacer(modifier = Modifier.height(12.dp))
            ReportStepSectionHeader(
                stepNumber = 5,
                title = if (isArabic) "إصدار وتصدير التقرير" else "Generate & Export",
                subtitle = if (isArabic) "توليد الملف النهائي أو مشاركته أو طباعته مباشرة" else "Issue, share or print final document",
                modifier = Modifier.testTag("step_actions_header")
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("step_actions_section")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Generate Button (Primary filled)
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                if (isArabic) "تم توليد وحفظ التقرير بنجاح بصيغة PDF" else "Report generated and saved as PDF successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("report_action_generate")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "توليد التقرير" else "Generate",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Share Button (Secondary outlined)
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                if (isArabic) "مشاركة ملف التقرير عبر التطبيقات المتاحة" else "Sharing report...",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutline),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("report_action_share")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "مشاركة" else "Share",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Print Button (Secondary outlined)
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                if (isArabic) "إرسال التقرير إلى أمر الطباعة..." else "Sending report to printer...",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GeoOutline),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("report_action_print")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "طباعة" else "Print",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Step Section Header component
 */
@Composable
private fun ReportStepSectionHeader(
    stepNumber: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = GeoPrimary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Selectable card for Step 1: Report Type
 */
@Composable
private fun ReportTypeSelectableCard(
    reportOption: ReportTypeOption,
    isSelected: Boolean,
    isArabic: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GeoPrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) GeoPrimary else GeoOutlineVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick)
            .testTag("report_type_card_${reportOption.type.name}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) GeoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = reportOption.icon,
                        contentDescription = null,
                        tint = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isArabic) reportOption.titleAr else reportOption.titleEn,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isArabic) reportOption.descAr else reportOption.descEn,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Filter Chip for Report Options
 */
@Composable
private fun ReportGroupingChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, GeoOutlineVariant),
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Stat summary box inside document preview
 */
@Composable
private fun PreviewStatPill(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


