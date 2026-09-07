package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageMode
import com.example.model.StoreStrings
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.StatusRed

/**
 * Model representing an archived customer item for the example list.
 */
data class ArchivedCustomerItem(
    val id: String,
    val name: String,
    val archivedDateAr: String,
    val archivedDateEn: String,
    val initialBalance: String
)

/**
 * The DATA CENTER screen of SmallStore (reached from More -> Data Center).
 *
 * Requirements:
 * - TOP BAR: Back arrow + title "Data Center".
 * - SECTIONS (grouped list):
 *   1. "Archive / Trash":
 *      - Sub-rows: "Archived Customers", "Archived Products", "Deleted Items" - each a tappable row.
 *        "Archived Customers" is shown expanded with the example list.
 *   2. "Backup & Restore":
 *      - "Create Backup" and "Restore Backup" rows.
 * - EXAMPLE EXPANDED LIST - "Archived Customers":
 *   - Simple list of archived customer rows, each with name + "archived on [date]" +
 *     two inline actions: "Restore" (primary) and "Delete Permanently" (muted, secondary).
 * - RESTORE CONFLICT STATE:
 *   - Rendered as a secondary example dialog/card beneath the list, clearly labeled "Example: Restore Conflict",
 *     offering exactly three choices: "Replace", "Add", "Skip".
 * - CONSTRAINTS:
 *   - Philosophy: Active -> Archive/Trash -> Review -> Restore OR Permanent Cleanup.
 *   - "Restore" is visually the primary action; "Delete Permanently" is secondary/muted (outline/text, red).
 *   - Default simple light theme, RTL Arabic layout.
 */
@Composable
fun DataCenterScreen(
    languageMode: LanguageMode,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val context = LocalContext.current
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Expanded state for Archived Customers (expanded by default to show example list)
    var isArchivedCustomersExpanded by remember { mutableStateOf(true) }
    var showConflictCard by remember { mutableStateOf(true) }

    // Plausible sample archived customers
    val archivedCustomers = remember {
        mutableStateListOf(
            ArchivedCustomerItem(
                id = "arch_1",
                name = "محمد علي الغامدي",
                archivedDateAr = "14 أغسطس 2026",
                archivedDateEn = "Aug 14, 2026",
                initialBalance = "450.00 ر.س"
            ),
            ArchivedCustomerItem(
                id = "arch_2",
                name = "خالد بن صالح العمري",
                archivedDateAr = "28 أغسطس 2026",
                archivedDateEn = "Aug 28, 2026",
                initialBalance = "0.00 ر.س"
            ),
            ArchivedCustomerItem(
                id = "arch_3",
                name = "مؤسسة الوفاء للتجارة",
                archivedDateAr = "02 سبتمبر 2026",
                archivedDateEn = "Sep 02, 2026",
                initialBalance = "1,200.00 ر.س"
            )
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("data_center_screen"),
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 0.5.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.testTag("data_center_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = if (isArabic) "رجوع" else "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = if (isArabic) StoreStrings.DATA_CENTER_AR else StoreStrings.DATA_CENTER_EN,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("data_center_title")
                            )
                        }

                        HorizontalDivider(color = GeoOutlineVariant)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 18.dp)
                    .navigationBarsPadding()
            ) {
                // ==========================================
                // LIFECYCLE PHILOSOPHY STRIP
                // Active -> Archive/Trash -> Review -> Restore OR Permanent Cleanup
                // ==========================================
                PhilosophyBanner(isArabic = isArabic)

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // SECTION 1: ARCHIVE / TRASH (الأرشيف / سلة المحذوفات)
                // ==========================================
                SectionHeader(
                    title = if (isArabic) StoreStrings.SECTION_ARCHIVE_TRASH_AR else StoreStrings.SECTION_ARCHIVE_TRASH_EN,
                    modifier = Modifier.testTag("section_archive_trash_header")
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, GeoOutlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 22.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Sub-row 1: Archived Customers (Tappable / Expandable)
                        ArchiveSubRow(
                            title = if (isArabic) StoreStrings.ARCHIVED_CUSTOMERS_AR else StoreStrings.ARCHIVED_CUSTOMERS_EN,
                            description = if (isArabic) StoreStrings.ARCHIVED_CUSTOMERS_DESC_AR else StoreStrings.ARCHIVED_CUSTOMERS_DESC_EN,
                            badgeCount = archivedCustomers.size.toString(),
                            icon = Icons.Default.People,
                            isExpanded = isArchivedCustomersExpanded,
                            testTag = "subrow_archived_customers",
                            onClick = {
                                isArchivedCustomersExpanded = !isArchivedCustomersExpanded
                            }
                        )

                        // EXPANDED LIST: Archived Customers Example
                        AnimatedVisibility(
                            visible = isArchivedCustomersExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("archived_customers_expanded_list")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isArabic) StoreStrings.EXAMPLE_EXPANDED_TITLE_AR else StoreStrings.EXAMPLE_EXPANDED_TITLE_EN,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Surface(
                                        color = GeoPrimary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (isArabic) "${archivedCustomers.size} سجلات" else "${archivedCustomers.size} records",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp
                                            ),
                                            color = GeoPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                archivedCustomers.forEachIndexed { index, item ->
                                    ArchivedCustomerCard(
                                        customer = item,
                                        isArabic = isArabic,
                                        onRestore = {
                                            // Trigger either conflict dialog or toast
                                            if (item.id == "arch_1") {
                                                showConflictCard = true
                                                val msg = if (isArabic) "تعارض محتمل: تم فتح نموذج حل التعارض أدناه" else "Potential conflict: See resolve conflict card below"
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            } else {
                                                archivedCustomers.remove(item)
                                                val msg = if (isArabic) "تمت استعادة العميل ${item.name} بنجاح" else "Customer ${item.name} restored successfully"
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onDeletePermanently = {
                                            archivedCustomers.remove(item)
                                            val msg = if (isArabic) "تم حذف ${item.name} نهائياً" else "Customer ${item.name} deleted permanently"
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        },
                                        testTag = "archived_customer_item_${item.id}"
                                    )
                                    if (index < archivedCustomers.size - 1) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }

                        HorizontalDivider(
                            color = GeoOutlineVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Sub-row 2: Archived Products
                        ArchiveSubRow(
                            title = if (isArabic) StoreStrings.ARCHIVED_PRODUCTS_AR else StoreStrings.ARCHIVED_PRODUCTS_EN,
                            description = if (isArabic) StoreStrings.ARCHIVED_PRODUCTS_DESC_AR else StoreStrings.ARCHIVED_PRODUCTS_DESC_EN,
                            badgeCount = "7",
                            icon = Icons.Default.Inventory2,
                            isExpanded = false,
                            testTag = "subrow_archived_products",
                            onClick = {
                                val msg = if (isArabic) "المنتجات المؤرشفة (7 عناصر)" else "Archived Products (7 items)"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )

                        HorizontalDivider(
                            color = GeoOutlineVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Sub-row 3: Deleted Items
                        ArchiveSubRow(
                            title = if (isArabic) StoreStrings.DELETED_ITEMS_AR else StoreStrings.DELETED_ITEMS_EN,
                            description = if (isArabic) StoreStrings.DELETED_ITEMS_DESC_AR else StoreStrings.DELETED_ITEMS_DESC_EN,
                            badgeCount = "2",
                            icon = Icons.Default.DeleteOutline,
                            isExpanded = false,
                            testTag = "subrow_deleted_items",
                            onClick = {
                                val msg = if (isArabic) "العناصر المحذوفة بانتظار التنظيف (عنصران)" else "Deleted items pending cleanup (2 items)"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // ==========================================
                // RESTORE CONFLICT STATE (Example dialog/card)
                // clearly labeled "Example: Restore Conflict"
                // ==========================================
                if (showConflictCard) {
                    RestoreConflictCard(
                        isArabic = isArabic,
                        conflictName = "محمد علي الغامدي",
                        onReplace = {
                            showConflictCard = false
                            val msg = if (isArabic) "تم استبدال السجل الحالي بالسجل المستعاد بنجاح" else "Existing record replaced with restored item"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onAdd = {
                            showConflictCard = false
                            val msg = if (isArabic) "تمت إضافة السجل كعميل جديد (نسخة 2)" else "Added as a new customer record (Copy 2)"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onSkip = {
                            showConflictCard = false
                            val msg = if (isArabic) "تم تخطي عملية الاستعادة" else "Restoration skipped"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.padding(bottom = 22.dp)
                    )
                }

                // ==========================================
                // SECTION 2: BACKUP & RESTORE (النسخ الاحتياطي والاستعادة)
                // ==========================================
                SectionHeader(
                    title = if (isArabic) StoreStrings.SECTION_BACKUP_AR else StoreStrings.SECTION_BACKUP_EN,
                    modifier = Modifier.testTag("section_datacenter_backup_header")
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, GeoOutlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1: Create Backup
                        DataCenterActionRow(
                            title = if (isArabic) StoreStrings.CREATE_BACKUP_AR else StoreStrings.CREATE_BACKUP_EN,
                            description = if (isArabic) StoreStrings.CREATE_BACKUP_DESC_AR else StoreStrings.CREATE_BACKUP_DESC_EN,
                            icon = Icons.Default.Download,
                            testTag = "datacenter_create_backup_row",
                            onClick = {
                                val msg = if (isArabic) StoreStrings.BACKUP_SUCCESS_AR else StoreStrings.BACKUP_SUCCESS_EN
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )

                        HorizontalDivider(
                            color = GeoOutlineVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Row 2: Restore Backup
                        DataCenterActionRow(
                            title = if (isArabic) StoreStrings.RESTORE_BACKUP_AR else StoreStrings.RESTORE_BACKUP_EN,
                            description = if (isArabic) StoreStrings.RESTORE_BACKUP_DESC_AR else StoreStrings.RESTORE_BACKUP_DESC_EN,
                            icon = Icons.Default.Refresh,
                            testTag = "datacenter_restore_backup_row",
                            onClick = {
                                val msg = if (isArabic) StoreStrings.RESTORE_SUCCESS_AR else StoreStrings.RESTORE_SUCCESS_EN
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Philosophy Banner:
 * Active -> Archive/Trash -> Review -> Restore OR Permanent Cleanup
 */
@Composable
private fun PhilosophyBanner(isArabic: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GeoPrimary.copy(alpha = 0.06f)),
        border = BorderStroke(1.dp, GeoPrimary.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("philosophy_lifecycle_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(GeoPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Archive,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isArabic) "فلسفة إدارة البيانات وحمايتها" else "Data Management Philosophy",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = GeoPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isArabic) StoreStrings.DATA_LIFECYCLE_PHILOSOPHY_AR else StoreStrings.DATA_LIFECYCLE_PHILOSOPHY_EN,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Section Header
 */
@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

/**
 * Archive Sub-row with count badge and chevron / toggle
 */
@Composable
private fun ArchiveSubRow(
    title: String,
    description: String,
    badgeCount: String,
    icon: ImageVector,
    isExpanded: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GeoPrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GeoPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = badgeCount,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Single Card for an Archived Customer in the expanded list.
 * Inline actions:
 * - "Restore" (primary/expected action: filled button)
 * - "Delete Permanently" (smaller, secondary, muted action: red text/outline button)
 */
@Composable
private fun ArchivedCustomerCard(
    customer: ArchivedCustomerItem,
    isArabic: Boolean,
    onRestore: () -> Unit,
    onDeletePermanently: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, GeoOutlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = customer.initialBalance,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "${if (isArabic) StoreStrings.ARCHIVED_ON_AR else StoreStrings.ARCHIVED_ON_EN} ${if (isArabic) customer.archivedDateAr else customer.archivedDateEn}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Inline Actions Row:
            // "Restore" = Primary filled button
            // "Delete Permanently" = Smaller, muted outlined/text button (red tone)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary / muted: Delete Permanently
                OutlinedButton(
                    onClick = onDeletePermanently,
                    border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.45f)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = StatusRed
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_delete_permanently_${customer.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = StatusRed
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isArabic) StoreStrings.DELETE_PERMANENTLY_AR else StoreStrings.DELETE_PERMANENTLY_EN,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Primary / expected: Restore
                Button(
                    onClick = onRestore,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GeoPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_restore_${customer.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isArabic) StoreStrings.RESTORE_ACTION_AR else StoreStrings.RESTORE_ACTION_EN,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * RESTORE CONFLICT STATE CARD
 * Clearly labeled "Example: Restore Conflict"
 * Offers exactly three choices: "Replace", "Add", "Skip".
 */
@Composable
private fun RestoreConflictCard(
    isArabic: Boolean,
    conflictName: String,
    onReplace: () -> Unit,
    onAdd: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, Color(0xFFD97706).copy(alpha = 0.4f)), // Amber/warning border
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("restore_conflict_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: "Example: Restore Conflict"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (isArabic) StoreStrings.CONFLICT_DIALOG_TITLE_AR else StoreStrings.CONFLICT_DIALOG_TITLE_EN,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Conflict description with item name
            Text(
                text = if (isArabic) {
                    "يوجد عميل حالي بنفس الاسم \"$conflictName\" في الحسابات النشطة برصيد 450.00 ر.س. كيف ترغب في معالجة هذا التعارض؟"
                } else {
                    "An active customer named \"$conflictName\" already exists with balance 450.00 SAR. How would you like to handle this conflict?"
                },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Exactly three choices: "Replace", "Add", "Skip"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Choice 1: Skip (Neutral / text)
                OutlinedButton(
                    onClick = onSkip,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GeoOutlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("conflict_btn_skip")
                ) {
                    Text(
                        text = if (isArabic) StoreStrings.CONFLICT_SKIP_AR else StoreStrings.CONFLICT_SKIP_EN,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }

                // Choice 2: Add (Secondary action)
                OutlinedButton(
                    onClick = onAdd,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GeoPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GeoPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("conflict_btn_add")
                ) {
                    Text(
                        text = if (isArabic) StoreStrings.CONFLICT_ADD_AR else StoreStrings.CONFLICT_ADD_EN,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }

                // Choice 3: Replace (Primary action for resolution)
                Button(
                    onClick = onReplace,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GeoPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("conflict_btn_replace")
                ) {
                    Text(
                        text = if (isArabic) StoreStrings.CONFLICT_REPLACE_AR else StoreStrings.CONFLICT_REPLACE_EN,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Data Center Action Row for Backup & Restore
 */
@Composable
private fun DataCenterActionRow(
    title: String,
    description: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GeoPrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GeoPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
