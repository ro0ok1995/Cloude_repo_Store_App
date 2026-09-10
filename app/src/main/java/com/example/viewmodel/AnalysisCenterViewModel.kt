package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CustomerAccount
import com.example.model.PeriodFilter
import com.example.model.TransactionItem
import com.example.util.ReportPreviewRow
import com.example.util.StatementRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

enum class AnalysisTab {
    STATISTICS,
    ACCOUNT_STATEMENT,
    REPORTS
}

enum class StatementTxFilter {
    ALL,
    PAYMENT,
    CASH_PURCHASE,
    DEBT_PURCHASE
}

enum class ReportType {
    COMPREHENSIVE_CUSTOMER,
    SALES_SUMMARY,
    DEBT_BALANCES,
    TAX_SUMMARY
}

data class AnalysisCenterUiState(
    val currentTab: AnalysisTab = AnalysisTab.STATISTICS,
    // Shared lockable period
    val isPeriodLocked: Boolean = true,
    val sharedPeriod: PeriodFilter = PeriodFilter.MONTH,
    val statisticsPeriod: PeriodFilter = PeriodFilter.MONTH,
    val statementPeriod: PeriodFilter = PeriodFilter.MONTH,
    val reportsPeriod: PeriodFilter = PeriodFilter.MONTH,

    // Shared Customer Context across Statistics and Statement, and conditional in Reports
    val selectedCustomer: CustomerAccount? = null, // null means "All customers"

    // Statement tab filters
    val statementFilter: StatementTxFilter = StatementTxFilter.ALL,
    val statementSearchQuery: String = "",

    // Reports tab state
    val selectedReportType: ReportType = ReportType.COMPREHENSIVE_CUSTOMER,
    val isExporting: Boolean = false,
    val exportSuccessMessage: String? = null
)

class AnalysisCenterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisCenterUiState())
    val uiState: StateFlow<AnalysisCenterUiState> = _uiState.asStateFlow()

    fun selectTab(tab: AnalysisTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun togglePeriodLock() {
        _uiState.update { state ->
            val newLocked = !state.isPeriodLocked
            if (newLocked) {
                // When re-locking, synchronize current tab's period to sharedPeriod
                val activePeriod = when (state.currentTab) {
                    AnalysisTab.STATISTICS -> state.statisticsPeriod
                    AnalysisTab.ACCOUNT_STATEMENT -> state.statementPeriod
                    AnalysisTab.REPORTS -> state.reportsPeriod
                }
                state.copy(
                    isPeriodLocked = true,
                    sharedPeriod = activePeriod,
                    statisticsPeriod = activePeriod,
                    statementPeriod = activePeriod,
                    reportsPeriod = activePeriod
                )
            } else {
                state.copy(isPeriodLocked = false)
            }
        }
    }

    fun selectPeriod(period: PeriodFilter) {
        _uiState.update { state ->
            if (state.isPeriodLocked) {
                // Update all tabs synchronously
                state.copy(
                    sharedPeriod = period,
                    statisticsPeriod = period,
                    statementPeriod = period,
                    reportsPeriod = period
                )
            } else {
                // Update only current tab's period
                when (state.currentTab) {
                    AnalysisTab.STATISTICS -> state.copy(statisticsPeriod = period)
                    AnalysisTab.ACCOUNT_STATEMENT -> state.copy(statementPeriod = period)
                    AnalysisTab.REPORTS -> state.copy(reportsPeriod = period)
                }
            }
        }
    }

    fun selectCustomer(customer: CustomerAccount?) {
        _uiState.update { it.copy(selectedCustomer = customer) }
    }

    fun clearCustomer() {
        _uiState.update { it.copy(selectedCustomer = null) }
    }

    fun setStatementFilter(filter: StatementTxFilter) {
        _uiState.update { it.copy(statementFilter = filter) }
    }

    fun setStatementSearchQuery(query: String) {
        _uiState.update { it.copy(statementSearchQuery = query) }
    }

    fun selectReportType(type: ReportType) {
        _uiState.update { it.copy(selectedReportType = type) }
    }

    fun setExportSuccessMessage(msg: String?) {
        _uiState.update { it.copy(exportSuccessMessage = msg) }
    }

    /**
     * Active period for whichever tab is currently displayed
     */
    fun getActivePeriod(): PeriodFilter {
        val state = _uiState.value
        return if (state.isPeriodLocked) {
            state.sharedPeriod
        } else {
            when (state.currentTab) {
                AnalysisTab.STATISTICS -> state.statisticsPeriod
                AnalysisTab.ACCOUNT_STATEMENT -> state.statementPeriod
                AnalysisTab.REPORTS -> state.reportsPeriod
            }
        }
    }

    /**
     * Helper to compute filtered statement rows with running balances.
     */
    fun computeStatementRows(
        allTransactions: List<TransactionItem>,
        selectedCustomer: CustomerAccount?,
        filter: StatementTxFilter,
        searchQuery: String,
        period: PeriodFilter
    ): List<StatementRow> {
        // 1. Filter by customer
        var txList = if (selectedCustomer != null) {
            allTransactions.filter { it.customerName.equals(selectedCustomer.customerName, ignoreCase = true) }
        } else {
            allTransactions
        }

        // 2. Filter by period
        txList = txList.filter { tx ->
            when (period) {
                PeriodFilter.TODAY -> tx.date == "2026-09-05" || tx.relativeTime.contains("دقيقة") || tx.relativeTime.contains("ساعة") || tx.relativeTime.contains("الآن")
                PeriodFilter.WEEK -> tx.date >= "2026-08-30" || tx.date.startsWith("2026-09")
                PeriodFilter.MONTH -> tx.date.startsWith("2026-09")
                PeriodFilter.CUSTOM -> true
            }
        }

        // 3. Filter by transaction type
        txList = when (filter) {
            StatementTxFilter.ALL -> txList
            StatementTxFilter.PAYMENT -> txList.filter { it.activityType.contains("تسديد") || it.activityType.contains("Payment") }
            StatementTxFilter.CASH_PURCHASE -> txList.filter { !it.isCredit && (it.activityType.contains("كاش") || it.activityType.contains("Cash")) }
            StatementTxFilter.DEBT_PURCHASE -> txList.filter { it.isCredit || it.activityType.contains("آجل") || it.activityType.contains("دين") || it.activityType.contains("Debt") }
        }

        // 4. Search query
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            txList = txList.filter {
                it.customerName.lowercase().contains(q) ||
                it.notes.lowercase().contains(q) ||
                it.activityType.lowercase().contains(q)
            }
        }

        // 5. Build statement rows with running balance calculation
        var running = 0.0
        return txList.map { tx ->
            val isPayment = tx.activityType.contains("تسديد") || tx.activityType.contains("Payment")
            val isDebtPurchase = tx.isCredit || tx.activityType.contains("آجل") || tx.activityType.contains("دين")
            if (isPayment) {
                running -= tx.amount
            } else if (isDebtPurchase) {
                running += tx.amount
            }
            StatementRow(
                id = tx.id,
                date = tx.date,
                customerName = tx.customerName,
                description = if (tx.notes.isNotBlank()) tx.notes else tx.activityType,
                type = tx.activityType,
                isPayment = isPayment,
                isCreditDebt = isDebtPurchase,
                amount = tx.amount,
                runningBalance = running
            )
        }
    }
}
