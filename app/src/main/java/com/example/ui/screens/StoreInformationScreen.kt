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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageMode
import com.example.model.StoreStrings
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary

/**
 * STORE INFORMATION screen of SmallStore (reached from More -> Store Information).
 *
 * Requirements:
 * - TOP BAR: Back arrow + title "Store Information"
 * - FORM FIELDS: Simple vertical form, one field per row, labels above inputs:
 *   1. Store Name (text)
 *   2. Owner Name (text)
 *   3. Phone (text, numeric keyboard style)
 *   4. Address (multi-line text)
 *   - Optional: Small store logo/image placeholder at top with a "Change" action
 * - BOTTOM ACTION: One primary "Save" button anchored at the bottom
 * - CONSTRAINTS: No tax/legal/business-registration fields, multiple branches, or map picker.
 * - RTL Arabic layout default with plausible Arabic sample values.
 */
@Composable
fun StoreInformationScreen(
    languageMode: LanguageMode,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSaveClick: (storeName: String, ownerName: String, phone: String, address: String) -> Unit = { _, _, _, _ -> }
) {
    val isArabic = languageMode == LanguageMode.ARABIC
    val context = LocalContext.current
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // State initialized with plausible sample values
    var storeName by remember {
        mutableStateOf(if (isArabic) StoreStrings.SAMPLE_STORE_NAME_AR else StoreStrings.SAMPLE_STORE_NAME_EN)
    }
    var ownerName by remember {
        mutableStateOf(if (isArabic) StoreStrings.SAMPLE_OWNER_NAME_AR else StoreStrings.SAMPLE_OWNER_NAME_EN)
    }
    var phone by remember {
        mutableStateOf(StoreStrings.SAMPLE_PHONE)
    }
    var address by remember {
        mutableStateOf(if (isArabic) StoreStrings.SAMPLE_ADDRESS_AR else StoreStrings.SAMPLE_ADDRESS_EN)
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .testTag("store_info_screen"),
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
                                modifier = Modifier.testTag("store_info_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = if (isArabic) "رجوع" else "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = if (isArabic) StoreStrings.STORE_INFO_AR else StoreStrings.STORE_INFO_EN,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("store_info_title")
                            )
                        }

                        HorizontalDivider(color = GeoOutlineVariant)
                    }
                }
            },
            bottomBar = {
                // Primary "Save" button anchored at bottom
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        HorizontalDivider(color = GeoOutlineVariant)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Button(
                                onClick = {
                                    onSaveClick(storeName, ownerName, phone, address)
                                    val message = if (isArabic) {
                                        StoreStrings.STORE_INFO_SAVED_AR
                                    } else {
                                        StoreStrings.STORE_INFO_SAVED_EN
                                    }
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    onBackClick()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("save_store_info_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GeoPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) StoreStrings.SAVE_AR else StoreStrings.SAVE_EN,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Store Logo/Image placeholder at the top with "Change" action
                StoreLogoSection(
                    isArabic = isArabic,
                    onChangeClick = {
                        val msg = if (isArabic) "اختر صورة لشعار المتجر" else "Select store logo image"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Form Field 1: Store Name
                StoreInputField(
                    label = if (isArabic) StoreStrings.STORE_NAME_LABEL_AR else StoreStrings.STORE_NAME_LABEL_EN,
                    value = storeName,
                    onValueChange = { storeName = it },
                    leadingIcon = Icons.Default.Storefront,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    testTag = "store_name_input"
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Form Field 2: Owner Name
                StoreInputField(
                    label = if (isArabic) StoreStrings.OWNER_NAME_LABEL_AR else StoreStrings.OWNER_NAME_LABEL_EN,
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    leadingIcon = Icons.Default.Person,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    testTag = "owner_name_input"
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Form Field 3: Phone (numeric keyboard style)
                StoreInputField(
                    label = if (isArabic) StoreStrings.PHONE_LABEL_AR else StoreStrings.PHONE_LABEL_EN,
                    value = phone,
                    onValueChange = { phone = it },
                    leadingIcon = Icons.Default.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    testTag = "phone_input"
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Form Field 4: Address (multi-line text)
                StoreInputField(
                    label = if (isArabic) StoreStrings.ADDRESS_LABEL_AR else StoreStrings.ADDRESS_LABEL_EN,
                    value = address,
                    onValueChange = { address = it },
                    leadingIcon = Icons.Default.Place,
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    testTag = "address_input"
                )

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

/**
 * Small store logo/image placeholder at the top with a "Change" action
 */
@Composable
private fun StoreLogoSection(
    isArabic: Boolean,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(88.dp)
        ) {
            // Main logo container
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(GeoPrimary.copy(alpha = 0.08f))
                    .clickable(onClick = onChangeClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = if (isArabic) "شعار المتجر" else "Store Logo",
                    tint = GeoPrimary,
                    modifier = Modifier.size(46.dp)
                )
            }

            // Edit badge icon overlay
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(GeoPrimary)
                    .clickable(onClick = onChangeClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = if (isArabic) "تغيير الشعار" else "Change Logo",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // "Change" Action Button
        OutlinedButton(
            onClick = onChangeClick,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, GeoOutlineVariant),
            modifier = Modifier
                .height(36.dp)
                .testTag("change_logo_button")
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isArabic) StoreStrings.CHANGE_LOGO_AR else StoreStrings.CHANGE_LOGO_EN,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Reusable vertical form field with label strictly ABOVE input
 */
@Composable
private fun StoreInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    testTag: String = ""
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Label above input
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Text input
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = GeoPrimary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoOutlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}
