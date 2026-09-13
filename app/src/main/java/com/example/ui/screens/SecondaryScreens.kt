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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppThemeMode
import com.example.model.LanguageMode
import com.example.model.NavDestination
import com.example.model.StoreInfo
import com.example.model.StoreStrings
import com.example.model.ThemeDisplayMode
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreInformationScreen(
    storeInfo: StoreInfo,
    languageMode: LanguageMode,
    onBackClick: () -> Unit,
    onSaveStoreInfo: (StoreInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val focusManager = LocalFocusManager.current
    var name by remember(storeInfo) { mutableStateOf(storeInfo.storeName) }
    var owner by remember(storeInfo) { mutableStateOf(storeInfo.ownerName) }
    var phone by remember(storeInfo) { mutableStateOf(storeInfo.phone) }
    var address by remember(storeInfo) { mutableStateOf(storeInfo.address) }
    var taxNumber by remember(storeInfo) { mutableStateOf(storeInfo.taxNumber) }
    var crNumber by remember(storeInfo) { mutableStateOf(storeInfo.crNumber) }
    var showSavedMessage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("store_information_screen")
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isArabic) StoreStrings.STORE_INFORMATION_AR else StoreStrings.STORE_INFORMATION_EN,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    focusManager.clearFocus()
                    onBackClick()
                }, modifier = Modifier.testTag("store_info_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (isArabic) "اسم المتجر" else "Store Name") },
                leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_store_name")
            )

            OutlinedTextField(
                value = owner,
                onValueChange = { owner = it },
                label = { Text(if (isArabic) "اسم المالك" else "Owner Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_owner_name")
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(if (isArabic) "رقم الهاتف" else "Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_store_phone")
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(if (isArabic) "العنوان / الموقع" else "Address / Location") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_store_address")
            )

            OutlinedTextField(
                value = taxNumber,
                onValueChange = { taxNumber = it },
                label = { Text(if (isArabic) "الرقم الضريبي (إن وجد)" else "Tax / VAT Number") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_tax_number")
            )

            OutlinedTextField(
                value = crNumber,
                onValueChange = { crNumber = it },
                label = { Text(if (isArabic) "السجل التجاري (إن وجد)" else "Commercial Register Number") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_cr_number")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSaveStoreInfo(
                        storeInfo.copy(
                            storeName = name,
                            ownerName = owner,
                            phone = phone,
                            address = address,
                            taxNumber = taxNumber,
                            crNumber = crNumber
                        )
                    )
                    showSavedMessage = true
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_store_info_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "حفظ البيانات" else "Save Information",
                    fontWeight = FontWeight.Bold
                )
            }

            if (showSavedMessage) {
                Text(
                    text = if (isArabic) "✓ تم حفظ البيانات بنجاح!" else "✓ Information saved successfully!",
                    color = StatusGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    languageMode: LanguageMode,
    themeMode: AppThemeMode,
    displayMode: ThemeDisplayMode,
    onBackClick: () -> Unit,
    onLanguageChange: (LanguageMode) -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    onDisplayModeChange: (ThemeDisplayMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("app_settings_screen")
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isArabic) StoreStrings.APP_SETTINGS_AR else StoreStrings.APP_SETTINGS_EN,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("settings_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Language Selection
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = GeoPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "لغة التطبيق" else "App Language",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = languageMode == LanguageMode.ARABIC,
                            onClick = { onLanguageChange(LanguageMode.ARABIC) },
                            label = { Text("العربية (RTL)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lang_arabic_chip")
                        )
                        FilterChip(
                            selected = languageMode == LanguageMode.ENGLISH,
                            onClick = { onLanguageChange(LanguageMode.ENGLISH) },
                            label = { Text("English (LTR)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("lang_english_chip")
                        )
                    }
                }
            }

            // Display Mode (Light / Dark / Auto)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "وضع المظهر" else "Display Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = displayMode == ThemeDisplayMode.LIGHT,
                            onClick = { onDisplayModeChange(ThemeDisplayMode.LIGHT) },
                            label = { Text(if (isArabic) "فاتح" else "Light") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("theme_light_chip")
                        )
                        FilterChip(
                            selected = displayMode == ThemeDisplayMode.DARK,
                            onClick = { onDisplayModeChange(ThemeDisplayMode.DARK) },
                            label = { Text(if (isArabic) "داكن" else "Dark") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("theme_dark_chip")
                        )
                        FilterChip(
                            selected = displayMode == ThemeDisplayMode.AUTO,
                            onClick = { onDisplayModeChange(ThemeDisplayMode.AUTO) },
                            label = { Text(if (isArabic) "تلقائي" else "System") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("theme_auto_chip")
                        )
                    }
                }
            }

            // Palette Theme
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = GeoPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "سمة الألوان" else "Color Palette",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = themeMode == AppThemeMode.NEUTRAL,
                            onClick = { onThemeChange(AppThemeMode.NEUTRAL) },
                            label = { Text(if (isArabic) "حيادي" else "Neutral") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("palette_neutral_chip")
                        )
                        FilterChip(
                            selected = themeMode == AppThemeMode.PURPLE,
                            onClick = { onThemeChange(AppThemeMode.PURPLE) },
                            label = { Text(if (isArabic) "بنفسجي" else "Purple") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("palette_purple_chip")
                        )
                        FilterChip(
                            selected = themeMode == AppThemeMode.GOLD,
                            onClick = { onThemeChange(AppThemeMode.GOLD) },
                            label = { Text(if (isArabic) "ذهبي" else "Gold") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("palette_gold_chip")
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataCenterScreen(
    languageMode: LanguageMode,
    onBackClick: () -> Unit,
    onResetData: () -> Unit,
    onExportBackup: ((android.net.Uri) -> Unit)? = null,
    onImportBackup: ((android.net.Uri) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    var showConfirmReset by remember { mutableStateOf(false) }

    val createDocLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/json")
    ) { uri: android.net.Uri? ->
        uri?.let { onExportBackup?.invoke(it) }
    }

    val openDocLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
    ) { uri: android.net.Uri? ->
        uri?.let { onImportBackup?.invoke(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("data_center_screen")
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isArabic) StoreStrings.DATA_CENTER_AR else StoreStrings.DATA_CENTER_EN,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("data_center_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Backup Option
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = GeoPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "نسخ احتياطي للبيانات" else "Backup Data",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "تصدير نسخة احتياطية من كافة الحسابات والعمليات" else "Export backup of all accounts and records",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {
                            val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(java.util.Date())
                            createDocLauncher.launch("SmallStore_Backup_$timestamp.json")
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        modifier = Modifier.testTag("backup_button")
                    ) {
                        Text(if (isArabic) "نسخ" else "Backup")
                    }
                }
            }

            // Restore Option
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = GeoPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "استعادة نسخة احتياطية" else "Restore Backup",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "استرجاع البيانات من ملف محفوظ سابقاً" else "Restore data from a previously saved file",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            openDocLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("restore_button")
                    ) {
                        Text(if (isArabic) "استعادة" else "Restore")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reset Data (Danger Zone)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StatusRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = StatusRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "إعادة ضبط المصنع للبيانات" else "Reset Application Data",
                            fontWeight = FontWeight.Bold,
                            color = StatusRed
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isArabic) "سيؤدي هذا إلى مسح كافة المعاملات والعملاء وإعادة تعيين البيانات الافتراضية." else "This will clear all transactions and customers and restore demo data.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showConfirmReset = true },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("reset_data_button")
                    ) {
                        Text(if (isArabic) "مسح وإعادة ضبط" else "Reset Data")
                    }
                }
            }
        }
    }

    if (showConfirmReset) {
        AlertDialog(
            onDismissRequest = { showConfirmReset = false },
            title = {
                Text(
                    text = if (isArabic) "تأكيد إعادة الضبط" else "Confirm Reset",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isArabic) "هل أنت متأكد من رغبتك في مسح كافة البيانات المسجلة؟" else "Are you sure you want to reset and restore sample data?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showConfirmReset = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Text(text = if (isArabic) "نعم، مسح" else "Yes, Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmReset = false }) {
                    Text(text = if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericContentScreen(
    title: String,
    content: String,
    onBackClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag(testTag)
    ) {
        TopAppBar(
            title = {
                Text(text = title, fontWeight = FontWeight.Bold)
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(14.dp))
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(
    languageMode: LanguageMode,
    onNavigate: (NavDestination) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("screen_about")
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isArabic) StoreStrings.ABOUT_SMALLSTORE_AR else StoreStrings.ABOUT_SMALLSTORE_EN,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("about_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Brand Header Card with summary
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(16.dp))
                    .testTag("about_app_header_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = GeoPrimary.copy(alpha = 0.12f),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = GeoPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isArabic) StoreStrings.APP_NAME_AR else StoreStrings.APP_NAME_EN,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) "الإصدار 1.0.0" else "Version 1.0.0",
                        style = MaterialTheme.typography.labelMedium,
                        color = GeoPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isArabic) StoreStrings.ABOUT_APP_SUMMARY_AR else StoreStrings.ABOUT_APP_SUMMARY_EN,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Three nested items: Privacy Policy, Terms of Use, Contact Support
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(16.dp))
                    .testTag("about_links_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(NavDestination.PRIVACY_POLICY) }
                            .padding(horizontal = 16.dp, vertical = 15.dp)
                            .testTag("about_item_privacy"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = GeoPrimary.copy(alpha = 0.08f),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = if (isArabic) StoreStrings.PRIVACY_POLICY_AR else StoreStrings.PRIVACY_POLICY_EN,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    HorizontalDivider(color = GeoOutlineVariant, thickness = 0.5.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(NavDestination.TERMS_OF_USE) }
                            .padding(horizontal = 16.dp, vertical = 15.dp)
                            .testTag("about_item_terms"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = GeoPrimary.copy(alpha = 0.08f),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = if (isArabic) StoreStrings.TERMS_OF_USE_AR else StoreStrings.TERMS_OF_USE_EN,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    HorizontalDivider(color = GeoOutlineVariant, thickness = 0.5.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(NavDestination.CONTACT_SUPPORT) }
                            .padding(horizontal = 16.dp, vertical = 15.dp)
                            .testTag("about_item_support"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = GeoPrimary.copy(alpha = 0.08f),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = if (isArabic) StoreStrings.CONTACT_SUPPORT_AR else StoreStrings.CONTACT_SUPPORT_EN,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(
    languageMode: LanguageMode,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("screen_contact_support")
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isArabic) StoreStrings.CONTACT_SUPPORT_TITLE_AR else StoreStrings.CONTACT_SUPPORT_TITLE_EN,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("support_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Studio & Description Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(16.dp))
                    .testTag("support_studio_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = GeoPrimary.copy(alpha = 0.10f),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isArabic) "الاستوديو والجهة المطورة" else "Studio & Developer",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = StoreStrings.CONTACT_SUPPORT_NAME,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = GeoOutlineVariant)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isArabic) StoreStrings.CONTACT_SUPPORT_DESC_AR else StoreStrings.CONTACT_SUPPORT_DESC_EN,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. WhatsApp Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(16.dp))
                    .testTag("support_whatsapp_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = Color(0xFF25D366).copy(alpha = 0.12f),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = Color(0xFF1EBE5D),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isArabic) StoreStrings.CONTACT_SUPPORT_PHONE_LABEL_AR else StoreStrings.CONTACT_SUPPORT_PHONE_LABEL_EN,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = StoreStrings.CONTACT_SUPPORT_PHONE,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.testTag("support_phone_text")
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Copy button
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("whatsapp", StoreStrings.CONTACT_SUPPORT_PHONE)
                                    clipboard.setPrimaryClip(clip)
                                    android.widget.Toast.makeText(
                                        context,
                                        if (isArabic) StoreStrings.CONTACT_SUPPORT_COPIED_AR else StoreStrings.CONTACT_SUPPORT_COPIED_EN,
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("support_copy_whatsapp_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "Copy WhatsApp",
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Call button
                            IconButton(
                                onClick = {
                                    try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                            data = android.net.Uri.parse("tel:${StoreStrings.CONTACT_SUPPORT_PHONE}")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("support_call_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call",
                                    tint = GeoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // WhatsApp Direct Chat Button
                    Button(
                        onClick = {
                            try {
                                val cleanPhone = StoreStrings.CONTACT_SUPPORT_PHONE.replace("+", "").replace(" ", "")
                                val url = "https://wa.me/$cleanPhone"
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("support_whatsapp_button")
                    ) {
                        Text(
                            text = if (isArabic) "مراسلة عبر واتساب (${StoreStrings.CONTACT_SUPPORT_PHONE})" else "Chat on WhatsApp (${StoreStrings.CONTACT_SUPPORT_PHONE})",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Email Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GeoOutlineVariant, RoundedCornerShape(16.dp))
                    .testTag("support_email_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = GeoPrimary.copy(alpha = 0.10f),
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = GeoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "البريد الإلكتروني" else "Email",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = StoreStrings.CONTACT_SUPPORT_EMAIL,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.testTag("support_email_text")
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("email", StoreStrings.CONTACT_SUPPORT_EMAIL)
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(
                                    context,
                                    if (isArabic) StoreStrings.CONTACT_SUPPORT_COPIED_AR else StoreStrings.CONTACT_SUPPORT_COPIED_EN,
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("support_copy_email_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Copy Email",
                                tint = GeoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email Send Button
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                    data = android.net.Uri.parse("mailto:${StoreStrings.CONTACT_SUPPORT_EMAIL}")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("support_email_button")
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "إرسال بريد إلكتروني" else "Send Email",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
