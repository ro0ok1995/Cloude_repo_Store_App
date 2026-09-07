package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageMode
import com.example.model.StoreStrings
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary

/**
 * Unique identifiers for the exactly four More menu items
 */
enum class MoreMenuItemId {
    STORE_INFO,
    APP_SETTINGS,
    DATA_CENTER,
    ABOUT
}

/**
 * Data model for a menu row/card on the More screen
 */
data class MoreMenuItem(
    val id: MoreMenuItemId,
    val titleAr: String,
    val titleEn: String,
    val descAr: String,
    val descEn: String,
    val icon: ImageVector,
    val testTag: String
)

/**
 * The MORE screen of SmallStore.
 *
 * Requirements:
 * - Simple vertical list of exactly four large tappable menu rows/cards:
 *   1. "Store Information" — store-level identity/contact info.
 *   2. "App Settings" — appearance, language, backup/restore, preferences.
 *   3. "Data Center" — customer, product, and archive/data-management functions.
 *   4. "About" — application information.
 * - Minimal: top bar (rendered by GlobalTopBar) + exactly these 4 rows + global bottom navigation.
 * - No multi-user login, account/profile section, or logout button.
 * - RTL Arabic layout default with natural translations.
 */
@Composable
fun MoreScreen(
    languageMode: LanguageMode,
    modifier: Modifier = Modifier,
    onMenuItemClick: (MoreMenuItemId) -> Unit = {}
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val context = LocalContext.current

    val menuItems = listOf(
        MoreMenuItem(
            id = MoreMenuItemId.STORE_INFO,
            titleAr = StoreStrings.STORE_INFO_AR,
            titleEn = StoreStrings.STORE_INFO_EN,
            descAr = StoreStrings.STORE_INFO_DESC_AR,
            descEn = StoreStrings.STORE_INFO_DESC_EN,
            icon = Icons.Default.Storefront,
            testTag = "more_menu_row_store_info"
        ),
        MoreMenuItem(
            id = MoreMenuItemId.APP_SETTINGS,
            titleAr = StoreStrings.APP_SETTINGS_AR,
            titleEn = StoreStrings.APP_SETTINGS_EN,
            descAr = StoreStrings.APP_SETTINGS_DESC_AR,
            descEn = StoreStrings.APP_SETTINGS_DESC_EN,
            icon = Icons.Default.Settings,
            testTag = "more_menu_row_app_settings"
        ),
        MoreMenuItem(
            id = MoreMenuItemId.DATA_CENTER,
            titleAr = StoreStrings.DATA_CENTER_AR,
            titleEn = StoreStrings.DATA_CENTER_EN,
            descAr = StoreStrings.DATA_CENTER_DESC_AR,
            descEn = StoreStrings.DATA_CENTER_DESC_EN,
            icon = Icons.Default.Storage,
            testTag = "more_menu_row_data_center"
        ),
        MoreMenuItem(
            id = MoreMenuItemId.ABOUT,
            titleAr = StoreStrings.ABOUT_AR,
            titleEn = StoreStrings.ABOUT_EN,
            descAr = StoreStrings.ABOUT_DESC_AR,
            descEn = StoreStrings.ABOUT_DESC_EN,
            icon = Icons.Default.Info,
            testTag = "more_menu_row_about"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("more_screen")
    ) {
        menuItems.forEachIndexed { index, item ->
            MoreMenuCard(
                item = item,
                isArabic = isArabic,
                onClick = {
                    onMenuItemClick(item.id)
                    val toastMessage = if (isArabic) {
                        "الانتقال إلى: ${item.titleAr}"
                    } else {
                        "Navigating to: ${item.titleEn}"
                    }
                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                }
            )

            if (index < menuItems.lastIndex) {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

/**
 * Large, accessible, tappable menu row card adhering to Foundation rules
 */
@Composable
private fun MoreMenuCard(
    item: MoreMenuItem,
    isArabic: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.5.dp,
            pressedElevation = 2.dp
        ),
        border = BorderStroke(1.dp, GeoOutlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(item.testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container with tinted brand background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = GeoPrimary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title and short one-line description
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isArabic) item.titleAr else item.titleEn,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic) item.descAr else item.descEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Trailing Chevron auto-mirrored for RTL/LTR
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
