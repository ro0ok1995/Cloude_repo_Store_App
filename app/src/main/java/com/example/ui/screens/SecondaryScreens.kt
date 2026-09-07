package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerAccount
import com.example.model.LanguageMode
import com.example.model.NotificationItem
import com.example.model.StoreStrings
import com.example.ui.components.SimpleEmptyState
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.util.Locale

// SecondaryScreens.kt: Contains MoreSettingsScreen and shared secondary components

/**
 * More/Settings Screen
 * Exact term: "More/Settings" (المزيد / الإعدادات)
 * Controls for Language (Arabic RTL / English LTR) and Themes.
 */
@Composable
fun MoreSettingsScreen(
    currentLanguage: LanguageMode,
    currentTheme: AppThemeMode,
    onLanguageChange: (LanguageMode) -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = currentLanguage == LanguageMode.ARABIC

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("more_settings_screen")
    ) {
        Text(
            text = if (isArabic) StoreStrings.MORE_SETTINGS_AR else StoreStrings.MORE_SETTINGS_EN,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 1. Language Card (RTL / LTR)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = GeoOutlineVariant, shape = RoundedCornerShape(16.dp))
                .padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "اتجاه الواجهة واللغة" else "Language & Direction",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isArabic) "تطبيق عربي أولاً مع دعم RTL كامل وخيار LTR معكوس" else "Arabic-first with full RTL and mirrored LTR support",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SelectablePill(
                        label = "العربية (RTL)",
                        isSelected = currentLanguage == LanguageMode.ARABIC,
                        testTag = "settings_lang_ar",
                        onClick = { onLanguageChange(LanguageMode.ARABIC) },
                        modifier = Modifier.weight(1f)
                    )
                    SelectablePill(
                        label = "English (LTR)",
                        isSelected = currentLanguage == LanguageMode.ENGLISH,
                        testTag = "settings_lang_en",
                        onClick = { onLanguageChange(LanguageMode.ENGLISH) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Theme Selection Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = GeoOutlineVariant, shape = RoundedCornerShape(16.dp))
                .padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "المظهر الهندسي والمتوازن" else "Theme & Appearance",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isArabic) "مظهر التوازن الهندسي (Geometric Balance) مع دعم كامل للسمات" else "Geometric Balance theme with clean layout and purple accent",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SelectablePill(
                        label = if (isArabic) "التوازن الهندسي" else "Geometric Balance",
                        isSelected = currentTheme == AppThemeMode.NEUTRAL,
                        testTag = "settings_theme_neutral",
                        onClick = { onThemeChange(AppThemeMode.NEUTRAL) },
                        modifier = Modifier.weight(1f)
                    )
                    SelectablePill(
                        label = if (isArabic) "بنفسجي" else "Purple",
                        isSelected = currentTheme == AppThemeMode.PURPLE,
                        testTag = "settings_theme_purple",
                        onClick = { onThemeChange(AppThemeMode.PURPLE) },
                        modifier = Modifier.weight(1f)
                    )
                    SelectablePill(
                        label = if (isArabic) "ذهبي" else "Gold",
                        isSelected = currentTheme == AppThemeMode.GOLD,
                        testTag = "settings_theme_gold",
                        onClick = { onThemeChange(AppThemeMode.GOLD) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. App Identity Info Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = GeoOutlineVariant, shape = RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = StoreStrings.APP_NAME,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isArabic) "تطبيق إدارة الحسابات، الأرصدة، والمشتريات" else "Shop-management for customers, balances & transactions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SelectablePill(
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) GeoPrimary else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
