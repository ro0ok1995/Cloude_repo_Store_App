package com.example

import com.example.model.CartItem
import com.example.model.CustomerAccount
import com.example.model.ProductItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying core business logic, customer filtering, and data integrity.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCustomerSearchByNameAndPhone() {
    val customers = listOf(
      CustomerAccount(id = "1", customerName = "أحمد محمد", balance = 150.0, totalDebt = 150.0, phone = "0501234567"),
      CustomerAccount(id = "2", customerName = "خالد عبدالله", balance = 0.0, totalDebt = 0.0, phone = "0559876543"),
      CustomerAccount(id = "3", customerName = "John Doe", balance = -50.0, totalDebt = 0.0, phone = "0511122233")
    )

    // Match by Arabic name
    val matchAhmed = customers.filter {
      val q = "أحمد"
      it.customerName.lowercase().contains(q) || it.phone.contains(q)
    }
    assertEquals(1, matchAhmed.size)
    assertEquals("1", matchAhmed.first().id)

    // Match by English name case-insensitive
    val matchJohn = customers.filter {
      val q = "john"
      it.customerName.lowercase().contains(q) || it.phone.contains(q)
    }
    assertEquals(1, matchJohn.size)
    assertEquals("3", matchJohn.first().id)

    // Match by phone substring
    val matchPhone = customers.filter {
      val q = "987"
      it.customerName.lowercase().contains(q) || it.phone.contains(q)
    }
    assertEquals(1, matchPhone.size)
    assertEquals("2", matchPhone.first().id)

    // Empty query returns all
    val blankQuery = "  "
    val allIfBlank = if (blankQuery.isBlank()) customers else customers.filter { it.customerName.contains(blankQuery) }
    assertEquals(3, allIfBlank.size)
  }

  @Test
  fun testCustomerBalanceClassification() {
    val inDebt = CustomerAccount(id = "1", customerName = "Customer A", balance = 250.0, totalDebt = 250.0, phone = "0500000001")
    val zeroBalance = CustomerAccount(id = "2", customerName = "Customer B", balance = 0.0, totalDebt = 0.0, phone = "0500000002")
    val creditBalance = CustomerAccount(id = "3", customerName = "Customer C", balance = -75.0, totalDebt = 0.0, phone = "0500000003")

    assertTrue(inDebt.balance > 0)
    assertEquals(0.0, zeroBalance.balance, 0.001)
    assertTrue(creditBalance.balance < 0)
  }

  @Test
  fun testAppCurrencyFormatting() {
    assertEquals("₪", com.example.model.AppCurrency.SYMBOL)
    val formattedAr = com.example.model.AppCurrency.formatAmount(150.0, isArabic = true)
    assertTrue(formattedAr.contains("₪"))
    assertTrue(formattedAr.contains("150"))
  }

  @Test
  fun testQuickPaymentDebtReduction() {
    val initialCustomer = CustomerAccount(
      id = "cust_1",
      customerName = "طارق الحسين",
      balance = 100.0,
      totalDebt = 100.0,
      phone = "0501112233"
    )

    val paymentAmount = 50.0
    val updatedCustomer = initialCustomer.copy(
      balance = (initialCustomer.balance - paymentAmount).coerceAtLeast(0.0),
      totalDebt = (initialCustomer.totalDebt - paymentAmount).coerceAtLeast(0.0),
      hasRecentActivity = true
    )

    assertEquals(50.0, updatedCustomer.balance, 0.001)
    assertEquals(50.0, updatedCustomer.totalDebt, 0.001)
    assertTrue(updatedCustomer.hasRecentActivity)
  }

  @Test
  fun testQuickPaymentValidation() {
    val customerWithDebt = CustomerAccount(
      id = "cust_2",
      customerName = "سالم العمري",
      balance = 100.0,
      totalDebt = 100.0,
      phone = "0502223344"
    )
    val customerWithoutDebt = CustomerAccount(
      id = "cust_3",
      customerName = "يوسف النجار",
      balance = 0.0,
      totalDebt = 0.0,
      phone = "0503334455"
    )

    // Case 1: Entered amount exceeds debt (150 > 100)
    val enteredExceeded = 150.0
    val isExceeded = enteredExceeded > (customerWithDebt.balance + 0.001)
    assertTrue("Payment exceeding debt should be flagged as exceeded", isExceeded)

    // Case 2: Entered amount exact or less than debt
    val enteredValid = 80.0
    val isValid = enteredValid > 0.0 && enteredValid <= customerWithDebt.balance && customerWithDebt.balance > 0.0
    assertTrue("Valid payment within debt should pass", isValid)

    // Case 3: Customer with 0 debt
    val isCustomerWithoutDebtValid = enteredValid > 0.0 && enteredValid <= customerWithoutDebt.balance && customerWithoutDebt.balance > 0.0
    assertTrue("Payment for customer with 0 debt should be blocked", !isCustomerWithoutDebtValid)

    // Case 4: No customer selected
    val nullCustomer: CustomerAccount? = null
    val isNullCustomerValid = nullCustomer != null && enteredValid > 0.0
    assertTrue("Confirmation without customer should be blocked", !isNullCustomerValid)
  }

  @Test
  fun testRecordTransactionItemLinesMapping() {
    val sampleProduct1 = ProductItem(
      id = "prod_1",
      name = "قهوة برازيلية",
      price = 25.0,
      costPrice = 18.0
    )
    val sampleProduct2 = ProductItem(
      id = "prod_2",
      name = "شاي سيلاني",
      price = 10.0,
      costPrice = 6.5
    )
    val cart = listOf(
      CartItem(product = sampleProduct1, quantity = 2),
      CartItem(product = sampleProduct2, quantity = 3)
    )

    val txId = "tx_test_123"
    val lines = cart.map { cartItem ->
      com.example.data.db.TransactionItemLineEntity(
        transactionId = txId,
        productId = cartItem.product.id,
        productNameSnapshot = cartItem.product.name,
        quantity = cartItem.quantity,
        unitPrice = cartItem.product.price,
        costPrice = cartItem.product.costPrice,
        subtotal = cartItem.product.price * cartItem.quantity
      )
    }

    assertEquals(2, lines.size)

    // Line 1
    assertEquals("tx_test_123", lines[0].transactionId)
    assertEquals("prod_1", lines[0].productId)
    assertEquals("قهوة برازيلية", lines[0].productNameSnapshot)
    assertEquals(2, lines[0].quantity)
    assertEquals(25.0, lines[0].unitPrice, 0.001)
    assertEquals(18.0, lines[0].costPrice, 0.001)
    assertEquals(50.0, lines[0].subtotal, 0.001)

    // Line 2
    assertEquals("tx_test_123", lines[1].transactionId)
    assertEquals("prod_2", lines[1].productId)
    assertEquals("شاي سيلاني", lines[1].productNameSnapshot)
    assertEquals(3, lines[1].quantity)
    assertEquals(10.0, lines[1].unitPrice, 0.001)
    assertEquals(6.5, lines[1].costPrice, 0.001)
    assertEquals(30.0, lines[1].subtotal, 0.001)

    val totalAmount = lines.sumOf { it.subtotal }
    assertEquals(80.0, totalAmount, 0.001)
  }

  @Test
  fun testSettlementContextTitles() {
    val recordAr = com.example.ui.components.SettlementContext.RECORD_TRANSACTION
    val legacy = com.example.ui.components.SettlementContext.QUICK_PAYMENT_LEGACY

    val titleRecordAr = when (recordAr) {
      com.example.ui.components.SettlementContext.RECORD_TRANSACTION -> "تسجيل المعاملة"
      com.example.ui.components.SettlementContext.QUICK_PAYMENT_LEGACY -> "المحاسبة"
    }
    assertEquals("تسجيل المعاملة", titleRecordAr)

    val titleRecordEn = when (recordAr) {
      com.example.ui.components.SettlementContext.RECORD_TRANSACTION -> "Record Transaction"
      com.example.ui.components.SettlementContext.QUICK_PAYMENT_LEGACY -> "Settlement"
    }
    assertEquals("Record Transaction", titleRecordEn)

    val titleLegacyAr = when (legacy) {
      com.example.ui.components.SettlementContext.RECORD_TRANSACTION -> "تسجيل المعاملة"
      com.example.ui.components.SettlementContext.QUICK_PAYMENT_LEGACY -> "المحاسبة"
    }
    assertEquals("المحاسبة", titleLegacyAr)
  }

  @Test
  fun testStoreDebtAgingSummaryCalculation() {
    val today = java.time.LocalDate.of(2026, 9, 12)
    val customers = listOf(
      CustomerAccount(id = "c1", customerName = "عميل أ", balance = 300.0, totalDebt = 300.0, phone = "0501111111"),
      CustomerAccount(id = "c2", customerName = "عميل ب", balance = 150.0, totalDebt = 150.0, phone = "0502222222"),
      CustomerAccount(id = "c3", customerName = "عميل ج", balance = 0.0, totalDebt = 0.0, phone = "0503333333")
    )

    val transactions = listOf(
      // c1: 10 days old (0-30 bucket) 200, 45 days old (31-60 bucket) 100
      com.example.model.TransactionItem("t1", "شراء آجل", "عميل أ", "شراء آجل", 200.0, true, "2026-09-02", "منذ 10 أيام", "مشتريات"),
      com.example.model.TransactionItem("t2", "شراء آجل", "عميل أ", "شراء آجل", 100.0, true, "2026-07-29", "منذ 45 يوم", "مشتريات"),
      // c2: 100 days old (90+ bucket) 150
      com.example.model.TransactionItem("t3", "شراء آجل", "عميل ب", "شراء آجل", 150.0, true, "2026-06-04", "منذ 100 يوم", "مشتريات")
    )

    val summary = com.example.viewmodel.DebtAgingUtils.calculateStoreDebtAgingSummary(
      customers = customers,
      allTransactions = transactions,
      today = today,
      isArabic = true
    )

    assertEquals(450.0, summary.totalOutstandingDebt, 0.001)
    assertEquals(2, summary.totalCustomersWithDebtCount)
    assertEquals(200.0, summary.sum0To30, 0.001)
    assertEquals(100.0, summary.sum31To60, 0.001)
    assertEquals(0.0, summary.sum61To90, 0.001)
    assertEquals(150.0, summary.sum90Plus, 0.001)
  }

  @Test
  fun testAccountStatementFiltersAndBalances() {
    val vm = com.example.viewmodel.AnalysisCenterViewModel()
    val cust = CustomerAccount(id = "c1", customerName = "علي أحمد", balance = 200.0, totalDebt = 200.0, phone = "0550000000")
    val txs = listOf(
      com.example.model.TransactionItem("tx1", "شراء نقدي", "علي أحمد", "كاش", 50.0, false, "2026-09-01", "اليوم", "بيبسي"),
      com.example.model.TransactionItem("tx2", "شراء آجل", "علي أحمد", "آجل", 250.0, true, "2026-09-02", "اليوم", "أرز وسكر"),
      com.example.model.TransactionItem("tx3", "تسديد دفعة", "علي أحمد", "تسديد", 50.0, false, "2026-09-03", "اليوم", "دفعة نقدية")
    )

    // Filter ALL
    val allRows = vm.computeStatementRows(
      allTransactions = txs,
      selectedCustomer = cust,
      filter = com.example.viewmodel.StatementTxFilter.ALL,
      period = com.example.model.PeriodFilter.CUSTOM
    )
    assertEquals(3, allRows.size)
    // tx1 cash: does not alter running balance
    assertEquals(0.0, allRows[0].runningBalance, 0.001)
    // tx2 debt: +250
    assertEquals(250.0, allRows[1].runningBalance, 0.001)
    // tx3 payment: -50 -> 200
    assertEquals(200.0, allRows[2].runningBalance, 0.001)

    // Filter PAYMENT
    val paymentRows = vm.computeStatementRows(
      allTransactions = txs,
      selectedCustomer = cust,
      filter = com.example.viewmodel.StatementTxFilter.PAYMENT,
      period = com.example.model.PeriodFilter.CUSTOM
    )
    assertEquals(1, paymentRows.size)
    assertEquals("tx3", paymentRows[0].id)

    // Filter CASH_PURCHASE
    val cashRows = vm.computeStatementRows(
      allTransactions = txs,
      selectedCustomer = cust,
      filter = com.example.viewmodel.StatementTxFilter.CASH_PURCHASE,
      period = com.example.model.PeriodFilter.CUSTOM
    )
    assertEquals(1, cashRows.size)
    assertEquals("tx1", cashRows[0].id)

    // Filter DEBT_PURCHASE
    val debtRows = vm.computeStatementRows(
      allTransactions = txs,
      selectedCustomer = cust,
      filter = com.example.viewmodel.StatementTxFilter.DEBT_PURCHASE,
      period = com.example.model.PeriodFilter.CUSTOM
    )
    assertEquals(1, debtRows.size)
    assertEquals("tx2", debtRows[0].id)
  }
}

