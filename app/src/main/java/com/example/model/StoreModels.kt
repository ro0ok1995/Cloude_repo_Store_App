package com.example.model

enum class LanguageMode {
    ARABIC,
    ENGLISH
}

enum class NavDestination {
    HOME,
    ACCOUNTS,
    PURCHASES,
    ANALYSIS_CENTER,
    NOTIFICATIONS,
    MORE_SETTINGS,
    CUSTOMER_DETAILS,
    QUICK_PAYMENT,
    STORE_INFORMATION,
    APP_SETTINGS,
    DATA_CENTER,
    ABOUT
}

enum class PeriodFilter {
    TODAY,
    WEEK,
    MONTH,
    CUSTOM
}

enum class AccountFilter {
    ALL,
    HAS_DEBT,
    RECENTLY_ACTIVE
}

enum class SettlementType {
    FULL,
    PARTIAL
}

enum class PaymentMethodOption {
    CASH,
    DEBT
}

data class TransactionItem(
    val id: String,
    val title: String,
    val customerName: String,
    val activityType: String, // "Purchase" / "Payment" or "مشتريات" / "دفعة"
    val amount: Double,
    val isCredit: Boolean, // true = receivable/credit (+), false = payment/debit (-)
    val date: String,
    val relativeTime: String, // e.g., "منذ ساعتين", "منذ 4 ساعات", "أمس"
    val notes: String = ""
)

data class CustomerAccount(
    val id: String,
    val customerName: String,
    val balance: Double, // positive = customer owes store, negative = store owes customer
    val totalDebt: Double, // total outstanding debt
    val phone: String,
    val lastTransactionDate: String
)

data class NotificationItem(
    val id: String,
    val customerName: String,
    val transactionType: String, // "تم تسجيل معاملة" / "تم استلام دفعة" or "Record Transaction" / "Payment"
    val amount: Double,
    val timestamp: String, // e.g., "منذ 15 دقيقة", "منذ ساعتين", "منذ 4 ساعات", "أمس"
    val isPayment: Boolean, // true for Payment, false for Record Transaction
    val isRead: Boolean = false,
    val transactionId: String? = null
)

object StoreStrings {
    // Notifications specific
    const val NO_NOTIFICATIONS_EN = "No notifications yet"
    const val NO_NOTIFICATIONS_AR = "لا توجد إشعارات حتى الآن"

    const val NOTIF_RECORD_TRANSACTION_EN = "Record Transaction"
    const val NOTIF_RECORD_TRANSACTION_AR = "تم تسجيل معاملة"

    const val NOTIF_PAYMENT_EN = "Payment"
    const val NOTIF_PAYMENT_AR = "تم استلام دفعة"
    // Terminology (Exact matches required by spec)
    const val RECORD_TRANSACTION_EN = "Record Transaction"
    const val RECORD_TRANSACTION_AR = "تسجيل معاملة"

    const val QUICK_PAYMENT_EN = "Quick Payment"
    const val QUICK_PAYMENT_AR = "دفع سريع"

    const val PURCHASES_EN = "Purchases"
    const val PURCHASES_AR = "المشتريات"

    const val PAYMENT_EN = "Payment"
    const val PAYMENT_AR = "دفعة"

    const val ACCOUNT_STATEMENT_EN = "Account Statement"
    const val ACCOUNT_STATEMENT_AR = "كشف حساب"

    const val SETTLEMENT_EN = "Settlement"
    const val SETTLEMENT_AR = "تسوية"

    const val CUSTOMER_DETAILS_EN = "Customer Details"
    const val CUSTOMER_DETAILS_AR = "تفاصيل العميل"

    const val ANALYSIS_CENTER_EN = "Analysis Center"
    const val ANALYSIS_CENTER_AR = "مركز التحليلات"

    // Navigation & Shell
    const val HOME_EN = "Home"
    const val HOME_AR = "الرئيسية"

    const val ACCOUNTS_EN = "Accounts"
    const val ACCOUNTS_AR = "الحسابات"

    const val MORE_EN = "More"
    const val MORE_AR = "المزيد"

    // More Screen Menu Items
    const val STORE_INFO_EN = "Store Information"
    const val STORE_INFO_AR = "بيانات المتجر"
    const val STORE_INFO_DESC_EN = "Store-level identity and contact info"
    const val STORE_INFO_DESC_AR = "هوية المتجر، العنوان، ومعلومات التواصل"

    // Store Information Form Strings
    const val STORE_NAME_LABEL_EN = "Store Name"
    const val STORE_NAME_LABEL_AR = "اسم المتجر"
    const val OWNER_NAME_LABEL_EN = "Owner Name"
    const val OWNER_NAME_LABEL_AR = "اسم المالك"
    const val PHONE_LABEL_EN = "Phone"
    const val PHONE_LABEL_AR = "رقم الهاتف"
    const val ADDRESS_LABEL_EN = "Address"
    const val ADDRESS_LABEL_AR = "العنوان"
    const val CHANGE_LOGO_EN = "Change"
    const val CHANGE_LOGO_AR = "تغيير"
    const val SAVE_EN = "Save"
    const val SAVE_AR = "حفظ"
    const val STORE_INFO_SAVED_EN = "Store information saved successfully"
    const val STORE_INFO_SAVED_AR = "تم حفظ بيانات المتجر بنجاح"

    // Plausible Arabic Sample Values
    const val SAMPLE_STORE_NAME_AR = "تموينات الأمل"
    const val SAMPLE_STORE_NAME_EN = "Al-Amal Store"
    const val SAMPLE_OWNER_NAME_AR = "عبدالله بن فهد المنصور"
    const val SAMPLE_OWNER_NAME_EN = "Abdullah Fahad Al-Mansoor"
    const val SAMPLE_PHONE = "0551234567"
    const val SAMPLE_ADDRESS_AR = "الرياض، حي السليمانية، شارع الملك عبدالعزيز، مبنى 24"
    const val SAMPLE_ADDRESS_EN = "Riyadh, As Sulimaniyah, King Abdulaziz Road, Building 24"

    const val APP_SETTINGS_EN = "App Settings"
    const val APP_SETTINGS_AR = "إعدادات التطبيق"
    const val APP_SETTINGS_DESC_EN = "Appearance, language, backup/restore, preferences"
    const val APP_SETTINGS_DESC_AR = "المظهر، اللغة، النسخ الاحتياطي، والتفضيلات"

    // App Settings Screen Strings
    const val SECTION_APPEARANCE_EN = "Appearance"
    const val SECTION_APPEARANCE_AR = "المظهر"
    const val THEME_MODE_LABEL_EN = "Theme Mode"
    const val THEME_MODE_LABEL_AR = "وضع المظهر"
    const val THEME_MODE_LIGHT_EN = "Light"
    const val THEME_MODE_LIGHT_AR = "فاتح"
    const val THEME_MODE_DARK_EN = "Dark"
    const val THEME_MODE_DARK_AR = "داكن"
    const val THEME_MODE_AUTO_EN = "Auto"
    const val THEME_MODE_AUTO_AR = "تلقائي"

    const val THEME_ACCENT_LABEL_EN = "Accent Theme"
    const val THEME_ACCENT_LABEL_AR = "لون السمة"
    const val ACCENT_SIMPLE_EN = "Simple (default)"
    const val ACCENT_SIMPLE_AR = "بسيط (الافتراضي)"
    const val ACCENT_PURPLE_EN = "Purple"
    const val ACCENT_PURPLE_AR = "بنفسجي"
    const val ACCENT_GOLD_EN = "Gold"
    const val ACCENT_GOLD_AR = "ذهبي"

    const val SECTION_LANGUAGE_EN = "Language"
    const val SECTION_LANGUAGE_AR = "اللغة"
    const val LANG_ARABIC = "العربية"
    const val LANG_ENGLISH = "English"

    const val SECTION_BACKUP_EN = "Backup & Restore"
    const val SECTION_BACKUP_AR = "النسخ الاحتياطي والاستعادة"
    const val BACKUP_NOW_EN = "Backup Now"
    const val BACKUP_NOW_AR = "نسخ احتياطي الآن"
    const val BACKUP_NOW_DESC_EN = "Save a local backup of store data and transactions"
    const val BACKUP_NOW_DESC_AR = "حفظ نسخة من بيانات المتجر والمعاملات محلياً"
    const val RESTORE_FROM_BACKUP_EN = "Restore from Backup"
    const val RESTORE_FROM_BACKUP_AR = "استعادة من نسخة احتياطية"
    const val RESTORE_FROM_BACKUP_DESC_EN = "Restore previously saved store data"
    const val RESTORE_FROM_BACKUP_DESC_AR = "استرجاع بيانات المتجر المحفوظة مسبقاً"

    const val SECTION_PREFERENCES_EN = "Preferences"
    const val SECTION_PREFERENCES_AR = "التفضيلات"
    const val PREF_ENABLE_NOTIFICATIONS_EN = "Enable notifications"
    const val PREF_ENABLE_NOTIFICATIONS_AR = "تفعيل الإشعارات"
    const val PREF_ENABLE_NOTIFICATIONS_DESC_EN = "Alerts for payments and due debts"
    const val PREF_ENABLE_NOTIFICATIONS_DESC_AR = "تنبيهات المدفوعات والديون المستحقة"
    const val PREF_TRANSACTION_SOUNDS_EN = "Transaction sound effects"
    const val PREF_TRANSACTION_SOUNDS_AR = "أصوات المعاملات"
    const val PREF_TRANSACTION_SOUNDS_DESC_EN = "Play subtle chime when recording transactions"
    const val PREF_TRANSACTION_SOUNDS_DESC_AR = "نغمة هادئة عند تسجيل المعاملات والدفع السريع"

    const val BACKUP_SUCCESS_EN = "Local backup created successfully"
    const val BACKUP_SUCCESS_AR = "تم إنشاء النسخة الاحتياطية بنجاح"
    const val RESTORE_SUCCESS_EN = "Latest backup restored successfully"
    const val RESTORE_SUCCESS_AR = "تم استرجاع النسخة الاحتياطية بنجاح"

    const val DATA_CENTER_EN = "Data Center"
    const val DATA_CENTER_AR = "مركز البيانات"
    const val DATA_CENTER_DESC_EN = "Customer, product, and archive/data-management functions"
    const val DATA_CENTER_DESC_AR = "إدارة العملاء، المنتجات، والأرشفة وقواعد البيانات"

    // Data Center Screen Strings
    const val SECTION_ARCHIVE_TRASH_EN = "Archive / Trash"
    const val SECTION_ARCHIVE_TRASH_AR = "الأرشيف / سلة المحذوفات"
    const val ARCHIVED_CUSTOMERS_EN = "Archived Customers"
    const val ARCHIVED_CUSTOMERS_AR = "العملاء المؤرشفون"
    const val ARCHIVED_CUSTOMERS_DESC_EN = "Customers moved out of active balances"
    const val ARCHIVED_CUSTOMERS_DESC_AR = "العملاء المستبعدون من الأرصدة النشطة"
    const val ARCHIVED_PRODUCTS_EN = "Archived Products"
    const val ARCHIVED_PRODUCTS_AR = "المنتجات المؤرشفة"
    const val ARCHIVED_PRODUCTS_DESC_EN = "Inactive inventory items kept for history"
    const val ARCHIVED_PRODUCTS_DESC_AR = "منتجات غير نشطة محفوظة للسجلات السابقة"
    const val DELETED_ITEMS_EN = "Deleted Items"
    const val DELETED_ITEMS_AR = "العناصر المحذوفة"
    const val DELETED_ITEMS_DESC_EN = "Items waiting for 30-day permanent cleanup"
    const val DELETED_ITEMS_DESC_AR = "عناصر تنتظر الحذف النهائي التلقائي"

    const val CREATE_BACKUP_EN = "Create Backup"
    const val CREATE_BACKUP_AR = "إنشاء نسخة احتياطية"
    const val CREATE_BACKUP_DESC_EN = "Save full database snapshot to local storage"
    const val CREATE_BACKUP_DESC_AR = "حفظ لقطة كاملة لقاعدة البيانات محلياً"
    const val RESTORE_BACKUP_EN = "Restore Backup"
    const val RESTORE_BACKUP_AR = "استعادة نسخة احتياطية"
    const val RESTORE_BACKUP_DESC_EN = "Restore database from previously created snapshot"
    const val RESTORE_BACKUP_DESC_AR = "استرجاع قاعدة البيانات من لقطة سابقة"

    const val RESTORE_ACTION_EN = "Restore"
    const val RESTORE_ACTION_AR = "استعادة"
    const val DELETE_PERMANENTLY_EN = "Delete Permanently"
    const val DELETE_PERMANENTLY_AR = "حذف نهائي"

    const val ARCHIVED_ON_EN = "Archived on"
    const val ARCHIVED_ON_AR = "تمت الأرشفة في"

    const val EXAMPLE_EXPANDED_TITLE_EN = "Example: Archived Customers"
    const val EXAMPLE_EXPANDED_TITLE_AR = "مثال: قائمة العملاء المؤرشفين"

    const val CONFLICT_DIALOG_TITLE_EN = "Example: Restore Conflict"
    const val CONFLICT_DIALOG_TITLE_AR = "مثال: تعارض عند الاستعادة"
    const val CONFLICT_DESC_EN = "An existing customer with the same name already exists in active accounts. How would you like to handle this conflict?"
    const val CONFLICT_DESC_AR = "تم العثور على عميل حالي بنفس الاسم في الحسابات النشطة. كيف ترغب في التعامل مع التعارض؟"
    const val CONFLICT_REPLACE_EN = "Replace"
    const val CONFLICT_REPLACE_AR = "استبدال"
    const val CONFLICT_ADD_EN = "Add"
    const val CONFLICT_ADD_AR = "إضافة"
    const val CONFLICT_SKIP_EN = "Skip"
    const val CONFLICT_SKIP_AR = "تخطي"

    const val DATA_LIFECYCLE_PHILOSOPHY_EN = "Active → Archive/Trash → Review → Restore OR Permanent Cleanup"
    const val DATA_LIFECYCLE_PHILOSOPHY_AR = "نشط ← الأرشيف/المهملات ← مراجعة ← استعادة أو حذف نهائي"

    const val ABOUT_EN = "About"
    const val ABOUT_AR = "عن التطبيق"
    const val ABOUT_DESC_EN = "Application information and version details"
    const val ABOUT_DESC_AR = "معلومات التطبيق، الإصدار، وتفاصيل النظام"

    const val ABOUT_APP_NAME_EN = "SmallStore"
    const val ABOUT_APP_NAME_AR = "سمول ستور"
    const val ABOUT_VERSION_LABEL = "v1.0.0"
    const val ABOUT_PURPOSE_EN = "SmallStore is an all-in-one store management application designed to organize customers, track balances, manage transactions, and deliver actionable sales and debt reports for retail and wholesale businesses."
    const val ABOUT_PURPOSE_AR = "سمول ستور هو تطبيق متكامل لإدارة المتاجر، صُمم لتنظيم حسابات العملاء، ومتابعة الأرصدة، وتسجيل المعاملات المالية، وتقديم تقارير دورية دقيقة لحركة المبيعات والديون لمحلات التجزئة والجملة."

    const val ABOUT_PRIVACY_POLICY_EN = "Privacy Policy"
    const val ABOUT_PRIVACY_POLICY_AR = "سياسة الخصوصية"
    const val ABOUT_TERMS_OF_USE_EN = "Terms of Use"
    const val ABOUT_TERMS_OF_USE_AR = "شروط الاستخدام"
    const val ABOUT_CONTACT_SUPPORT_EN = "Contact Support"
    const val ABOUT_CONTACT_SUPPORT_AR = "الاتصال بالدعم الفني"

    const val NOTIFICATIONS_EN = "Notifications"
    const val NOTIFICATIONS_AR = "الإشعارات"

    const val MORE_SETTINGS_EN = "More/Settings"
    const val MORE_SETTINGS_AR = "المزيد / الإعدادات"

    const val APP_NAME = "SmallStore"
    const val OPTIONAL_EN = "(optional)"
    const val OPTIONAL_AR = "(اختياري)"

    // Home screen specific
    const val SEARCH_CUSTOMER_EN = "Search customer"
    const val SEARCH_CUSTOMER_AR = "البحث عن عميل"

    const val LATEST_ACTIVITIES_EN = "Latest Activities"
    const val LATEST_ACTIVITIES_AR = "أحدث النشاطات"

    const val TOTAL_BALANCE_EN = "Total Balance"
    const val TOTAL_BALANCE_AR = "إجمالي الرصيد"

    const val TOTAL_DEBT_EN = "Total Debt"
    const val TOTAL_DEBT_AR = "إجمالي الديون"

    const val TODAY_TRANSACTIONS_EN = "Today's Transactions"
    const val TODAY_TRANSACTIONS_AR = "معاملات اليوم"

    const val PERIOD_TODAY_EN = "Today"
    const val PERIOD_TODAY_AR = "اليوم"

    const val PERIOD_WEEK_EN = "Week"
    const val PERIOD_WEEK_AR = "الأسبوع"

    const val PERIOD_MONTH_EN = "Month"
    const val PERIOD_MONTH_AR = "الشهر"

    const val PERIOD_CUSTOM_EN = "Custom"
    const val PERIOD_CUSTOM_AR = "مخصص"

    // Accounts screen specific
    const val SEARCH_CUSTOMER_ACCOUNTS_EN = "Search customer by name or phone"
    const val SEARCH_CUSTOMER_ACCOUNTS_AR = "البحث بالاسم أو رقم الهاتف"

    const val FILTER_ALL_EN = "All"
    const val FILTER_ALL_AR = "الكل"

    const val FILTER_HAS_DEBT_EN = "Has Debt"
    const val FILTER_HAS_DEBT_AR = "عليه ديون"

    const val FILTER_RECENTLY_ACTIVE_EN = "Recently Active"
    const val FILTER_RECENTLY_ACTIVE_AR = "نشط مؤخراً"

    const val ADD_CUSTOMER_EN = "+ Add Customer"
    const val ADD_CUSTOMER_AR = "+ إضافة عميل"

    const val NO_CUSTOMERS_YET_EN = "No customers yet"
    const val NO_CUSTOMERS_YET_AR = "لا يوجد عملاء حتى الآن"

    const val PAID_UP_EN = "Paid up"
    const val PAID_UP_AR = "خالص"

    // Customer Details screen specific
    const val EDIT_CUSTOMER_EN = "Edit customer"
    const val EDIT_CUSTOMER_AR = "تعديل العميل"

    const val ARCHIVE_CUSTOMER_EN = "Archive customer"
    const val ARCHIVE_CUSTOMER_AR = "أرشفة العميل"

    const val OWES_EN = "Owes"
    const val OWES_AR = "عليه"

    const val SETTLED_EN = "Settled"
    const val SETTLED_AR = "تمت التسوية"

    const val RECENT_ACTIVITY_EN = "Recent Activity"
    const val RECENT_ACTIVITY_AR = "أحدث النشاطات"

    // Purchases screen specific
    const val SEARCH_PRODUCTS_EN = "Search products"
    const val SEARCH_PRODUCTS_AR = "البحث في المنتجات"

    const val COMPLETE_TRANSACTION_EN = "Complete Transaction"
    const val COMPLETE_TRANSACTION_AR = "إتمام المعاملة"

    const val SELECT_CUSTOMER_EN = "Select Customer"
    const val SELECT_CUSTOMER_AR = "تحديد العميل"

    const val SELECT_CUSTOMER_REQUIRED_EN = "Required: Select a customer to enable checkout"
    const val SELECT_CUSTOMER_REQUIRED_AR = "مطلوب: يرجى تحديد عميل لتمكين إتمام المعاملة"

    const val CHANGE_CUSTOMER_EN = "Change"
    const val CHANGE_CUSTOMER_AR = "تغيير"

    const val VIEW_CART_EN = "View Cart"
    const val VIEW_CART_AR = "عرض السلة"

    const val HIDE_CART_EN = "Hide Cart"
    const val HIDE_CART_AR = "إخفاء السلة"

    const val CART_ITEMS_EN = "items"
    const val CART_ITEMS_AR = "أصناف"

    const val TOTAL_EN = "Total"
    const val TOTAL_AR = "الإجمالي"

    const val NO_PRODUCTS_FOUND_EN = "No products found"
    const val NO_PRODUCTS_FOUND_AR = "لم يتم العثور على منتجات"

    // Quick Payment screen specific
    const val SETTLEMENT_TYPE_FULL_EN = "Full"
    const val SETTLEMENT_TYPE_FULL_AR = "كامل"

    const val SETTLEMENT_TYPE_PARTIAL_EN = "Partial"
    const val SETTLEMENT_TYPE_PARTIAL_AR = "جزئي"

    const val METHOD_CASH_EN = "Cash"
    const val METHOD_CASH_AR = "نقداً"

    const val METHOD_DEBT_EN = "Debt"
    const val METHOD_DEBT_AR = "آجل"

    const val COMPLETE_ACTION_EN = "Complete"
    const val COMPLETE_ACTION_AR = "إتمام"

    const val NOTES_LABEL_EN = "Notes (optional)"
    const val NOTES_LABEL_AR = "ملاحظات (اختياري)"

    const val TRANSACTION_TOTAL_EN = "Transaction Total"
    const val TRANSACTION_TOTAL_AR = "إجمالي المعاملة"

    const val AMOUNT_LIMIT_HELPER_EN = "The amount entered cannot exceed transaction total"
    const val AMOUNT_LIMIT_HELPER_AR = "لا يمكن أن يتجاوز المبلغ المدخل إجمالي المعاملة"

    // Settlement Bottom Sheet specific
    const val CASH_AMOUNT_LABEL_EN = "Cash Amount"
    const val CASH_AMOUNT_LABEL_AR = "المبلغ النقدي"

    const val DEBT_AMOUNT_LABEL_EN = "Debt Amount"
    const val DEBT_AMOUNT_LABEL_AR = "المبلغ الآجل"

    const val TOTAL_PAID_FORMULA_EN = "Total Paid = Cash Amount + Debt Amount"
    const val TOTAL_PAID_FORMULA_AR = "إجمالي المدفوع = المبلغ النقدي + المبلغ الآجل"

    const val REMAINING_BALANCE_FORMULA_EN = "Remaining Balance = Transaction Total − Total Paid"
    const val REMAINING_BALANCE_FORMULA_AR = "المبلغ المتبقي = إجمالي المعاملة − إجمالي المدفوع"

    const val TOTAL_PAID_EN = "Total Paid"
    const val TOTAL_PAID_AR = "إجمالي المدفوع"

    const val REMAINING_BALANCE_EN = "Remaining Balance"
    const val REMAINING_BALANCE_AR = "المبلغ المتبقي"

    // Analysis Center specific
    const val TAB_STATISTICS_EN = "Statistics"
    const val TAB_STATISTICS_AR = "الإحصائيات"

    const val TAB_ACCOUNT_STATEMENT_EN = "Account Statement"
    const val TAB_ACCOUNT_STATEMENT_AR = "كشف حساب"

    const val TAB_REPORTS_EN = "Reports"
    const val TAB_REPORTS_AR = "التقارير"

    const val TOTAL_SALES_EN = "Total Sales"
    const val TOTAL_SALES_AR = "إجمالي المبيعات"

    const val TOTAL_PAYMENTS_EN = "Total Payments"
    const val TOTAL_PAYMENTS_AR = "إجمالي المقبوضات"

    const val TOTAL_OUTSTANDING_DEBT_EN = "Total Outstanding Debt"
    const val TOTAL_OUTSTANDING_DEBT_AR = "إجمالي الديون القائمة"

    const val NUMBER_OF_TRANSACTIONS_EN = "Number of Transactions"
    const val NUMBER_OF_TRANSACTIONS_AR = "عدد المعاملات"

    const val ACTIVITY_SUMMARY_EN = "Activity Summary"
    const val ACTIVITY_SUMMARY_AR = "ملخص النشاط"

    const val SELECT_CUSTOMER_OPTIONAL_EN = "Select Customer (optional)"
    const val SELECT_CUSTOMER_OPTIONAL_AR = "تحديد العميل (اختياري)"

    const val ALL_CUSTOMERS_SHOP_WIDE_EN = "All Customers (Shop-wide)"
    const val ALL_CUSTOMERS_SHOP_WIDE_AR = "جميع العملاء (على مستوى المتجر)"

    const val TRANSACTION_TYPE_FILTER_EN = "Transaction Type"
    const val TRANSACTION_TYPE_FILTER_AR = "نوع المعاملة"

    const val ALL_TYPES_EN = "All"
    const val ALL_TYPES_AR = "الكل"

    const val TX_FILTER_PURCHASE_EN = "Purchase"
    const val TX_FILTER_PURCHASE_AR = "مشتريات"

    const val TX_FILTER_PAYMENT_EN = "Payment"
    const val TX_FILTER_PAYMENT_AR = "دفعة"

    const val SELECT_PRODUCT_OPTIONAL_EN = "Product (optional)"
    const val SELECT_PRODUCT_OPTIONAL_AR = "المنتج (اختياري)"

    const val ALL_PRODUCTS_EN = "All Products"
    const val ALL_PRODUCTS_AR = "جميع المنتجات"

    const val TOTAL_IN_EN = "Total In"
    const val TOTAL_IN_AR = "إجمالي الوارد"

    const val TOTAL_OUT_EN = "Total Out"
    const val TOTAL_OUT_AR = "إجمالي الصادر"

    const val NET_BALANCE_EN = "Net"
    const val NET_BALANCE_AR = "الصافي"

    const val RUNNING_BALANCE_EN = "Balance"
    const val RUNNING_BALANCE_AR = "الرصيد التراكمي"

    const val EXPORT_STATEMENT_EN = "Export"
    const val EXPORT_STATEMENT_AR = "تصدير"

    const val SHARE_STATEMENT_EN = "Share"
    const val SHARE_STATEMENT_AR = "مشاركة"

    const val CURRENCY_SAR_EN = "SAR"
    const val CURRENCY_SAR_AR = "ر.س"
}

data class ProductItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String = "مواد غذائية",
    val unit: String = "حبة"
)

data class CartItem(
    val product: ProductItem,
    val quantity: Int
)
