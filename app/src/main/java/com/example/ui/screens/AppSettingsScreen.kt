package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageMode
import com.example.model.StoreStrings
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary

/**
 * Display modes for Appearance theme mode selector (Light / Dark / Auto)
 */
enum class ThemeDisplayMode {
    LIGHT,
    DARK,
    AUTO
}

/**
 * APP SETTINGS screen of SmallStore (reached from More -> App Settings).
 *
 * Requirements:
 * - TOP BAR: Back arrow + title "App Settings"
 * - SECTIONS (grouped list with section headers):
 *   1. "Appearance":
 *      - Theme mode selector: Light / Dark / Auto (segmented control)
 *      - Accent/theme selector: 3 options shown as small swatches/cards:
 *        "Simple (default)", "Purple", "Gold" (Simple selected by default)
 *   2. "Language":
 *      - Toggle/selector between "العربية" and "English" (العربية selected by default)
 *   3. "Backup & Restore":
 *      - Two rows: "Backup Now" and "Restore from Backup", each with a short one-line description and a chevron
 *   4. "Preferences" (small section):
 *      - Simple toggles plausible for this app (e.g., "Enable notifications", "Transaction sound effects")
 * - BOTTOM ACTION: No bottom Save button (settings save immediately on change)
 * - CONSTRAINTS: No unrelated settings (no account management, multi-currency, tax settings)
 * - RTL Arabic layout default, simple light theme
 */
@Composable
fun AppSettingsScreen(
    currentLanguage: LanguageMode,
    currentTheme: AppThemeMode,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLanguageChange: (LanguageMode) -> Unit = {},
    onThemeChange: (AppThemeMode) -> Unit = {}
) {
    val isArabic = currentLanguage == LanguageMode.ARABIC
    val context = LocalContext.current
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    var themeDisplayMode by remember { mutableStateOf(ThemeDisplayMode.LIGHT) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundsEnabled by remember { mutableStateOf(true) }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("app_settings_screen"),
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
                                modifier = Modifier.testTag("app_settings_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = if (isArabic) "رجوع" else "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = if (isArabic) StoreStrings.APP_SETTINGS_AR else StoreStrings.APP_SETTINGS_EN,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("app_settings_title")
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
                // SECTION 1: APPEARANCE (المظهر)
                // ==========================================
                SectionHeader(
                    title = if (isArabic) StoreStrings.SECTION_APPEARANCE_AR else StoreStrings.SECTION_APPEARANCE_EN,
                    modifier = Modifier.testTag("section_appearance_header")
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 1. Theme Mode: Light / Dark / Auto (Segmented Control)
                        Text(
                            text = if (isArabic) StoreStrings.THEME_MODE_LABEL_AR else StoreStrings.THEME_MODE_LABEL_EN,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        ThemeModeSegmentedControl(
                            selectedMode = themeDisplayMode,
                            isArabic = isArabic,
                            onModeSelected = { mode ->
                                themeDisplayMode = mode
                                val toastText = when (mode) {
                                    ThemeDisplayMode.LIGHT -> if (isArabic) "تم اختيار الوضع الفاتح" else "Light mode selected"
                                    ThemeDisplayMode.DARK -> if (isArabic) "الوضع الداكن (قيد التجربة)" else "Dark mode (preview)"
                                    ThemeDisplayMode.AUTO -> if (isArabic) "تم اختيار الوضع التلقائي للنظام" else "Auto system mode selected"
                                }
                                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                            }
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = GeoOutlineVariant.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Accent/Theme Selector: Simple (default), Purple, Gold
                        Text(
                            text = if (isArabic) StoreStrings.THEME_ACCENT_LABEL_AR else StoreStrings.THEME_ACCENT_LABEL_EN,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Option 1: Simple (default)
                            AccentSwatchCard(
                                title = if (isArabic) StoreStrings.ACCENT_SIMPLE_AR else StoreStrings.ACCENT_SIMPLE_EN,
                                swatchColor = Color(0xFF15803D), // Forest green / neutral balance
                                isSelected = currentTheme == AppThemeMode.NEUTRAL,
                                isArabic = isArabic,
                                onClick = { onThemeChange(AppThemeMode.NEUTRAL) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("accent_swatch_simple")
                            )

                            // Option 2: Purple
                            AccentSwatchCard(
                                title = if (isArabic) StoreStrings.ACCENT_PURPLE_AR else StoreStrings.ACCENT_PURPLE_EN,
                                swatchColor = Color(0xFF7C3AED), // Rich purple
                                isSelected = currentTheme == AppThemeMode.PURPLE,
                                isArabic = isArabic,
                                onClick = { onThemeChange(AppThemeMode.PURPLE) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("accent_swatch_purple")
                            )

                            // Option 3: Gold
                            AccentSwatchCard(
                                title = if (isArabic) StoreStrings.ACCENT_GOLD_AR else StoreStrings.ACCENT_GOLD_EN,
                                swatchColor = Color(0xFFD97706), // Warm gold
                                isSelected = currentTheme == AppThemeMode.GOLD,
                                isArabic = isArabic,
                                onClick = { onThemeChange(AppThemeMode.GOLD) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("accent_swatch_gold")
                            )
                        }
                    }
                }

                // ==========================================
                // SECTION 2: LANGUAGE (اللغة)
                // ==========================================
                SectionHeader(
                    title = if (isArabic) StoreStrings.SECTION_LANGUAGE_AR else StoreStrings.SECTION_LANGUAGE_EN,
                    modifier = Modifier.testTag("section_language_header")
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Arabic Selector (Default)
                        LanguageOptionItem(
                            label = StoreStrings.LANG_ARABIC,
                            subLabel = "يمين إلى يسار (RTL)",
                            isSelected = isArabic,
                            onClick = { onLanguageChange(LanguageMode.ARABIC) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("language_option_arabic")
                        )

                        // English Selector
                        LanguageOptionItem(
                            label = StoreStrings.LANG_ENGLISH,
                            subLabel = "Left-to-Right (LTR)",
                            isSelected = !isArabic,
                            onClick = { onLanguageChange(LanguageMode.ENGLISH) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("language_option_english")
                        )
                    }
                }

                // ==========================================
                // SECTION 3: BACKUP & RESTORE (النسخ الاحتياطي والاستعادة)
                // ==========================================
                SectionHeader(
                    title = if (isArabic) StoreStrings.SECTION_BACKUP_AR else StoreStrings.SECTION_BACKUP_EN,
                    modifier = Modifier.testTag("section_backup_header")
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
                        // Row 1: Backup Now
                        SettingActionRow(
                            title = if (isArabic) StoreStrings.BACKUP_NOW_AR else StoreStrings.BACKUP_NOW_EN,
                            description = if (isArabic) StoreStrings.BACKUP_NOW_DESC_AR else StoreStrings.BACKUP_NOW_DESC_EN,
                            icon = Icons.Default.Download,
                            testTag = "backup_now_row",
                            onClick = {
                                val msg = if (isArabic) StoreStrings.BACKUP_SUCCESS_AR else StoreStrings.BACKUP_SUCCESS_EN
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )

                        HorizontalDivider(
                            color = GeoOutlineVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Row 2: Restore from Backup
                        SettingActionRow(
                            title = if (isArabic) StoreStrings.RESTORE_FROM_BACKUP_AR else StoreStrings.RESTORE_FROM_BACKUP_EN,
                            description = if (isArabic) StoreStrings.RESTORE_FROM_BACKUP_DESC_AR else StoreStrings.RESTORE_FROM_BACKUP_DESC_EN,
                            icon = Icons.Default.Refresh,
                            testTag = "restore_from_backup_row",
                            onClick = {
                                val msg = if (isArabic) StoreStrings.RESTORE_SUCCESS_AR else StoreStrings.RESTORE_SUCCESS_EN
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // ==========================================
                // SECTION 4: PREFERENCES (التفضيلات)
                // ==========================================
                SectionHeader(
                    title = if (isArabic) StoreStrings.SECTION_PREFERENCES_AR else StoreStrings.SECTION_PREFERENCES_EN,
                    modifier = Modifier.testTag("section_preferences_header")
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
                        // Toggle 1: Enable Notifications
                        SettingToggleRow(
                            title = if (isArabic) StoreStrings.PREF_ENABLE_NOTIFICATIONS_AR else StoreStrings.PREF_ENABLE_NOTIFICATIONS_EN,
                            description = if (isArabic) StoreStrings.PREF_ENABLE_NOTIFICATIONS_DESC_AR else StoreStrings.PREF_ENABLE_NOTIFICATIONS_DESC_EN,
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            testTag = "pref_notifications_switch"
                        )

                        HorizontalDivider(
                            color = GeoOutlineVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Toggle 2: Transaction Sound Effects
                        SettingToggleRow(
                            title = if (isArabic) StoreStrings.PREF_TRANSACTION_SOUNDS_AR else StoreStrings.PREF_TRANSACTION_SOUNDS_EN,
                            description = if (isArabic) StoreStrings.PREF_TRANSACTION_SOUNDS_DESC_AR else StoreStrings.PREF_TRANSACTION_SOUNDS_DESC_EN,
                            checked = soundsEnabled,
                            onCheckedChange = { soundsEnabled = it },
                            testTag = "pref_sounds_switch"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Clean Section Header
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
 * Segmented Control for Theme Display Mode (Light / Dark / Auto)
 */
@Composable
private fun ThemeModeSegmentedControl(
    selectedMode: ThemeDisplayMode,
    isArabic: Boolean,
    onModeSelected: (ThemeDisplayMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val modes = listOf(
            ThemeDisplayMode.LIGHT to (if (isArabic) StoreStrings.THEME_MODE_LIGHT_AR else StoreStrings.THEME_MODE_LIGHT_EN),
            ThemeDisplayMode.DARK to (if (isArabic) StoreStrings.THEME_MODE_DARK_AR else StoreStrings.THEME_MODE_DARK_EN),
            ThemeDisplayMode.AUTO to (if (isArabic) StoreStrings.THEME_MODE_AUTO_AR else StoreStrings.THEME_MODE_AUTO_EN)
        )

        modes.forEach { (mode, label) ->
            val isSelected = selectedMode == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                    )
                    .clickable { onModeSelected(mode) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    ),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Accent Color Swatch Card
 */
@Composable
private fun AccentSwatchCard(
    title: String,
    swatchColor: Color,
    isSelected: Boolean,
    isArabic: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GeoPrimary.copy(alpha = 0.07f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) GeoPrimary else GeoOutlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Swatch circular dot with optional checkmark
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(swatchColor),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Language Option Card
 */
@Composable
private fun LanguageOptionItem(
    label: String,
    subLabel: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GeoPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) GeoPrimary else GeoOutlineVariant
        ),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp
                    ),
                    color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
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
    }
}

/**
 * Action Row with leading icon, text, and trailing chevron
 */
@Composable
private fun SettingActionRow(
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

/**
 * Toggle Row with Title, Description, and Switch
 */
@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GeoPrimary
            )
        )
    }
}
