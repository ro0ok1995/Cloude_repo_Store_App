package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageMode
import com.example.model.NavDestination
import com.example.model.StoreStrings

/**
 * GLOBAL DRAWER (HAMBURGER MENU):
 * Accessible from the top bar on every screen, containing full app navigation:
 * (Home, Accounts, Purchases, Analysis Center, Notifications, More/Settings).
 * The drawer opens from the right in RTL and from the left in LTR.
 */
@Composable
fun GlobalDrawerContent(
    currentDestination: NavDestination,
    languageMode: LanguageMode,
    unreadNotificationsCount: Int,
    onSelectDestination: (NavDestination) -> Unit,
    onToggleLanguage: (LanguageMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = languageMode == LanguageMode.ARABIC

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = StoreStrings.APP_NAME,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = StoreStrings.APP_NAME,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isArabic) "إدارة المتاجر والحسابات" else "Shop & Balance Management",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Navigation List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Home
                DrawerMenuItem(
                    label = if (isArabic) StoreStrings.HOME_AR else StoreStrings.HOME_EN,
                    icon = Icons.Default.Home,
                    isSelected = currentDestination == NavDestination.HOME,
                    testTag = "drawer_item_home",
                    onClick = { onSelectDestination(NavDestination.HOME) }
                )

                // 2. Accounts
                DrawerMenuItem(
                    label = if (isArabic) StoreStrings.ACCOUNTS_AR else StoreStrings.ACCOUNTS_EN,
                    icon = Icons.Default.AccountBalanceWallet,
                    isSelected = currentDestination == NavDestination.ACCOUNTS,
                    testTag = "drawer_item_accounts",
                    onClick = { onSelectDestination(NavDestination.ACCOUNTS) }
                )

                // 3. Purchases
                DrawerMenuItem(
                    label = if (isArabic) StoreStrings.PURCHASES_AR else StoreStrings.PURCHASES_EN,
                    icon = Icons.Default.ShoppingBag,
                    isSelected = currentDestination == NavDestination.PURCHASES,
                    testTag = "drawer_item_purchases",
                    onClick = { onSelectDestination(NavDestination.PURCHASES) }
                )

                // 4. Analysis Center
                DrawerMenuItem(
                    label = if (isArabic) StoreStrings.ANALYSIS_CENTER_AR else StoreStrings.ANALYSIS_CENTER_EN,
                    icon = Icons.Default.BarChart,
                    isSelected = currentDestination == NavDestination.ANALYSIS_CENTER,
                    testTag = "drawer_item_analysis_center",
                    onClick = { onSelectDestination(NavDestination.ANALYSIS_CENTER) }
                )

                // 5. Notifications
                DrawerMenuItem(
                    label = if (isArabic) StoreStrings.NOTIFICATIONS_AR else StoreStrings.NOTIFICATIONS_EN,
                    icon = Icons.Default.Notifications,
                    isSelected = currentDestination == NavDestination.NOTIFICATIONS,
                    badgeCount = unreadNotificationsCount,
                    testTag = "drawer_item_notifications",
                    onClick = { onSelectDestination(NavDestination.NOTIFICATIONS) }
                )

                // 6. More
                DrawerMenuItem(
                    label = if (isArabic) StoreStrings.MORE_AR else StoreStrings.MORE_EN,
                    icon = Icons.Default.MoreHoriz,
                    isSelected = currentDestination == NavDestination.MORE_SETTINGS,
                    testTag = "drawer_item_more",
                    onClick = { onSelectDestination(NavDestination.MORE_SETTINGS) }
                )
            }

            Divider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Quick Language Switcher in Drawer Footer (Arabic RTL <-> English LTR)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "اللغة / Language" else "Language / اللغة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LanguageBadge(
                        label = "العربية (RTL)",
                        isSelected = languageMode == LanguageMode.ARABIC,
                        testTag = "lang_toggle_ar",
                        onClick = { onToggleLanguage(LanguageMode.ARABIC) }
                    )
                    LanguageBadge(
                        label = "English (LTR)",
                        isSelected = languageMode == LanguageMode.ENGLISH,
                        testTag = "lang_toggle_en",
                        onClick = { onToggleLanguage(LanguageMode.ENGLISH) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    badgeCount: Int = 0
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 15.sp
            ),
            color = contentColor,
            modifier = Modifier.weight(1f)
        )
        if (badgeCount > 0) {
            NotificationBadge(unreadCount = badgeCount)
        }
    }
}

@Composable
private fun LanguageBadge(
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
