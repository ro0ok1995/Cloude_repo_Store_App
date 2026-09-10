package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.PeriodFilter
import com.example.model.SettlementType
import com.example.model.StoreStrings
import com.example.model.TransactionItem
import com.example.ui.components.CustomerSearchField
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
import java.util.Locale
import kotlin.math.roundToInt

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
    modifier: Modifier = Modifier,
    allCustomers: List<CustomerAccount> = matchingCustomers
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val currency = if (isArabic) "ر.س" else "SAR"
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_container")
    ) {
        // FIXED TOP SECTION
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("home_fixed_top_section")
        ) {
            // Reusable Customer Search Field with Dropdown
            CustomerSearchField(
                customers = allCustomers.ifEmpty { matchingCustomers },
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onCustomerSelected = onSelectCustomer,
                onClearSelection = onClearSelectedCustomer,
                selectedCustomerId = selectedCustomer?.id,
                currency = currency,
                isArabic = isArabic,
                inputTestTag = "customer_search_input",
                dropdownTestTag = "customer_search_suggestions",
                itemTagPrefix = "customer_suggestion_"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. PERIOD SELECTOR (Today / Week / Month / Custom)
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

            // 4. METRICS CARD
            val periodTransactions = remember(transactions, selectedPeriod, selectedCustomer) {
                val base = if (selectedCustomer != null) {
                    transactions.filter { it.customerName == selectedCustomer.customerName }
                } else {
                    transactions
                }
                base.filter { tx ->
                    when (selectedPeriod) {
                        PeriodFilter.TODAY -> tx.date == "2026-09-05" || tx.relativeTime.contains("دقيقة") || tx.relativeTime.contains("ساعة") || tx.relativeTime.contains("الآن")
                        PeriodFilter.WEEK -> tx.date >= "2026-08-30" || tx.date.startsWith("2026-09")
                        PeriodFilter.MONTH -> tx.date.startsWith("2026-09")
                        PeriodFilter.CUSTOM -> true
                    }
                }
            }

            val cashSalesAmount = remember(periodTransactions) {
                periodTransactions
                    .filter { !it.isCredit && (it.activityType.contains("شراء كاش") || it.activityType.contains("Cash") || (!it.activityType.contains("تسديد") && !it.activityType.contains("Payment"))) }
                    .sumOf { it.amount }
            }
            // ASSUMPTION: If settlementType is null on historical settlements, default to SettlementType.FULL.
            val fullSettlementAmount = remember(periodTransactions) {
                periodTransactions
                    .filter { (it.activityType.contains("تسديد") || it.activityType.contains("Payment")) && (it.settlementType == SettlementType.FULL || it.settlementType == null) }
                    .sumOf { it.amount }
            }
            val partialSettlementAmount = remember(periodTransactions) {
                periodTransactions
                    .filter { (it.activityType.contains("تسديد") || it.activityType.contains("Payment")) && it.settlementType == SettlementType.PARTIAL }
                    .sumOf { it.amount }
            }
            val debtAmount = if (selectedCustomer != null) selectedCustomer.balance else totalDebt
            val totalActivity = debtAmount + cashSalesAmount + fullSettlementAmount + partialSettlementAmount
            val debtPercent = if (totalActivity > 0) ((debtAmount / totalActivity) * 100).roundToInt() else 0
            val cashPercent = if (totalActivity > 0) ((cashSalesAmount / totalActivity) * 100).roundToInt() else 0
            val fullPercent = if (totalActivity > 0) ((fullSettlementAmount / totalActivity) * 100).roundToInt() else 0
            val partialPercent = if (totalActivity > 0) (100 - debtPercent - cashPercent - fullPercent).coerceAtLeast(0) else 0

            val ringSegments = remember(debtPercent, cashPercent, fullPercent, partialPercent) {
                listOf(
                    DonutSegment(debtPercent.toFloat(), StatusRed),
                    DonutSegment(cashPercent.toFloat(), StatusBlue),
                    DonutSegment(fullPercent.toFloat(), StatusGreen),
                    DonutSegment(partialPercent.toFloat(), StatusAmber)
                )
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GeoOutlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_debt_ratio_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = if (selectedCustomer != null) {
                                if (isArabic) "ديون العميل المستحقة" else "Customer Outstanding Debt"
                            } else {
                                if (isArabic) StoreStrings.TOTAL_OUTSTANDING_DEBT_AR else StoreStrings.TOTAL_OUTSTANDING_DEBT_EN
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format(Locale.US, "%,.1f %s", debtAmount, currency),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            ),
                            color = StatusRed,
                            modifier = Modifier.testTag("stat_total_debt")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DebtRatioRingChart(
                                segments = ringSegments,
                                centerPercentage = debtPercent,
                                subtitle = if (isArabic) StoreStrings.STAT_DEBT_CREDIT_AR else StoreStrings.STAT_DEBT_CREDIT_EN,
                                modifier = Modifier
                                    .size(130.dp)
                                    .testTag("home_debt_ring_chart")
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            CompositionLocalProvider(LocalLayoutDirection provides (if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr)) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("home_stats_legend"),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    HomeStatLegendItem(
                                        dotColor = StatusRed,
                                        label = if (isArabic) StoreStrings.STAT_DEBT_CREDIT_AR else StoreStrings.STAT_DEBT_CREDIT_EN,
                                        amount = String.format(Locale.US, "%,.1f %s", debtAmount, currency),
                                        percentage = "$debtPercent%",
                                        testTag = "legend_debt_credit"
                                    )
                                    HomeStatLegendItem(
                                        dotColor = StatusBlue,
                                        label = if (isArabic) StoreStrings.STAT_CASH_SALES_AR else StoreStrings.STAT_CASH_SALES_EN,
                                        amount = String.format(Locale.US, "%,.1f %s", cashSalesAmount, currency),
                                        percentage = "$cashPercent%",
                                        testTag = "legend_cash_sales"
                                    )
                                    HomeStatLegendItem(
                                        dotColor = StatusGreen,
                                        label = if (isArabic) StoreStrings.STAT_PAYMENTS_RECEIVED_AR else StoreStrings.STAT_PAYMENTS_RECEIVED_EN,
                                        amount = String.format(Locale.US, "%,.1f %s", fullSettlementAmount, currency),
                                        percentage = "$fullPercent%",
                                        testTag = "legend_payment_full"
                                    )
                                    HomeStatLegendItem(
                                        dotColor = StatusAmber,
                                        label = if (isArabic) StoreStrings.STAT_INSTALLMENTS_AR else StoreStrings.STAT_INSTALLMENTS_EN,
                                        amount = String.format(Locale.US, "%,.1f %s", partialSettlementAmount, currency),
                                        percentage = "$partialPercent%",
                                        testTag = "legend_installment_partial"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = GeoOutline, thickness = 1.dp)

        // SCROLLABLE LATEST ACTIVITIES LIST
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .testTag("latest_activities_section")
        ) {
            Spacer(modifier = Modifier.height(14.dp))
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
                        text = if (isArabic) "السجل الكامل" else "Complete History",
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
                        message = if (isArabic) "لا توجد معاملات مسجلة." else "No activities recorded.",
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

data class DonutSegment(
    val percentage: Float,
    val color: Color
)

@Composable
fun DebtRatioRingChart(
    segments: List<DonutSegment>,
    centerPercentage: Int,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            val strokeWidth = 12.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeftOffset = Offset(
                x = (size.width - diameter) / 2f,
                y = (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            // Draw circular background track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            // Draw segments sequentially starting at -90deg (top)
            var currentAngle = -90f
            for (segment in segments) {
                val sweep = (segment.percentage / 100f) * 360f
                if (sweep > 0f) {
                    drawArc(
                        color = segment.color,
                        startAngle = currentAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeftOffset,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    currentAngle += sweep
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                text = "$centerPercentage%",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HomeStatLegendItem(
    dotColor: Color,
    label: String,
    amount: String,
    percentage: String,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = percentage,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = dotColor
                )
            }
            Text(
                text = amount,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActivityRowCard(
    transaction: TransactionItem,
    currency: String,
    isArabic: Boolean
) {
    val isCredit = transaction.isCredit
    val isPayment = transaction.activityType == "تسديد" || transaction.activityType == "Payment"
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
