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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageMode
import com.example.model.StoreStrings
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary

/**
 * The ABOUT screen of SmallStore (reached from More -> About).
 *
 * Requirements:
 * - TOP BAR: Back arrow + title "About".
 * - CONTENT (simple centered/vertical layout):
 *   - App icon/logo placeholder at the top.
 *   - App name "SmallStore" and version number (e.g., "v1.0.0").
 *   - A short one-paragraph description of the app's purpose (shop-management: customers, balances, transactions, reports).
 *   - A simple list of links/rows: "Privacy Policy", "Terms of Use", "Contact Support" (plausible for an About screen; no social-media icons or marketing content).
 * - CONSTRAINTS:
 *   - Keep this screen short and static - no forms, no settings, no data entry of any kind.
 *   - Default simple light theme, RTL Arabic layout.
 */
@Composable
fun AboutScreen(
    languageMode: LanguageMode,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val context = LocalContext.current
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("about_screen"),
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
                                modifier = Modifier.testTag("about_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = if (isArabic) "رجوع" else "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = if (isArabic) StoreStrings.ABOUT_AR else StoreStrings.ABOUT_EN,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("about_screen_title")
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
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // App icon / logo placeholder at the top
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(GeoPrimary.copy(alpha = 0.12f))
                        .testTag("about_app_logo"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "SmallStore Logo",
                        tint = GeoPrimary,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Name
                Text(
                    text = if (isArabic) "SmallStore (${StoreStrings.ABOUT_APP_NAME_AR})" else StoreStrings.ABOUT_APP_NAME_EN,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("about_app_name")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Version Badge (e.g., "v1.0.0")
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = StoreStrings.ABOUT_VERSION_LABEL,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                            .testTag("about_version_label")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Short one-paragraph description of the app's purpose
                // (shop-management: customers, balances, transactions, reports)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, GeoOutlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isArabic) StoreStrings.ABOUT_PURPOSE_AR else StoreStrings.ABOUT_PURPOSE_EN,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.5.sp,
                            lineHeight = 22.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .testTag("about_purpose_description")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Simple list of links/rows: "Privacy Policy", "Terms of Use", "Contact Support"
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, GeoOutlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("about_links_card")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // 1. Privacy Policy
                        AboutLinkRow(
                            title = if (isArabic) StoreStrings.ABOUT_PRIVACY_POLICY_AR else StoreStrings.ABOUT_PRIVACY_POLICY_EN,
                            icon = Icons.Default.Policy,
                            testTag = "about_link_privacy_policy",
                            onClick = {
                                val msg = if (isArabic) "عرض سياسة الخصوصية" else "Viewing Privacy Policy"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )

                        HorizontalDivider(
                            color = GeoOutlineVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 2. Terms of Use
                        AboutLinkRow(
                            title = if (isArabic) StoreStrings.ABOUT_TERMS_OF_USE_AR else StoreStrings.ABOUT_TERMS_OF_USE_EN,
                            icon = Icons.Default.Description,
                            testTag = "about_link_terms_of_use",
                            onClick = {
                                val msg = if (isArabic) "عرض شروط الاستخدام" else "Viewing Terms of Use"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )

                        HorizontalDivider(
                            color = GeoOutlineVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 3. Contact Support
                        AboutLinkRow(
                            title = if (isArabic) StoreStrings.ABOUT_CONTACT_SUPPORT_AR else StoreStrings.ABOUT_CONTACT_SUPPORT_EN,
                            icon = Icons.Default.HeadsetMic,
                            testTag = "about_link_contact_support",
                            onClick = {
                                val msg = if (isArabic) "التواصل مع فريق الدعم الفني" else "Contacting Technical Support"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Subtle copyright / platform text
                Text(
                    text = if (isArabic) "© 2026 SmallStore - جميع الحقوق محفوظة" else "© 2026 SmallStore - All rights reserved",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Simple Link Row for the About screen
 */
@Composable
private fun AboutLinkRow(
    title: String,
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
                .size(36.dp)
                .clip(CircleShape)
                .background(GeoPrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GeoPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}
