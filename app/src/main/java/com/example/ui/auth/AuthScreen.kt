package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AvatarDarkGreen
import com.example.ui.theme.CardBackground

enum class AuthTab {
    CUSTOMER_LOGIN,
    OWNER_LOGIN
}

@Composable
fun AuthScreen(
    onCustomerLogin: (name: String, phone: String, address: String) -> Unit,
    onOwnerLogin: (email: String, pin: String) -> Boolean,
    onQuickCustomerLogin: () -> Unit,
    onQuickOwnerLogin: () -> Unit,
    errorMessage: String? = null,
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AuthTab.CUSTOMER_LOGIN) }

    // Customer form state
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }

    // Owner form state
    var ownerEmail by remember { mutableStateOf("owner@vmart.com") }
    var ownerPin by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Minimal Header like VYRO BETA in reference screenshot
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "V-MART",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontSize = 24.sp
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SIGN IN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to V-Mart",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = Color.Black
            )
            Text(
                text = "Select your account type to proceed",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Two clean selection cards side-by-side (matching reference layout for Profiles 2 / Posts 14)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Customer Card
                RoleCard(
                    title = "Customer",
                    subtitle = "Order Groceries",
                    icon = Icons.Default.ShoppingBag,
                    isSelected = selectedTab == AuthTab.CUSTOMER_LOGIN,
                    onClick = {
                        selectedTab = AuthTab.CUSTOMER_LOGIN
                        onClearError()
                    },
                    modifier = Modifier.weight(1f).testTag("tab_customer_login")
                )

                // Owner Card
                RoleCard(
                    title = "Shop Owner",
                    subtitle = "Manage Inventory",
                    icon = Icons.Default.Storefront,
                    isSelected = selectedTab == AuthTab.OWNER_LOGIN,
                    onClick = {
                        selectedTab = AuthTab.OWNER_LOGIN
                        onClearError()
                    },
                    modifier = Modifier.weight(1f).testTag("tab_owner_login")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error banner if any
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFFEF2F2))
                            .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFFDC2626)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Clean Form Container
            if (selectedTab == AuthTab.CUSTOMER_LOGIN) {
                // Customer Input Fields
                Text(
                    text = "Customer Sign In",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Text(
                    text = "Order fresh groceries for doorstep delivery",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = {
                        customerName = it
                        onClearError()
                    },
                    label = { Text("Your Full Name") },
                    placeholder = { Text("e.g. Alex Morgan") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_name")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = {
                        customerPhone = it
                        onClearError()
                    },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+1 (555) 019-2834") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_phone")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customerAddress,
                    onValueChange = {
                        customerAddress = it
                        onClearError()
                    },
                    label = { Text("Delivery Address") },
                    placeholder = { Text("e.g. Apt 4B, 124 Park Ave") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.Black) },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_address")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Black Button
                Button(
                    onClick = {
                        onCustomerLogin(
                            customerName.ifBlank { "Alex Morgan" },
                            customerPhone.ifBlank { "+91 98765 43210" },
                            customerAddress.ifBlank { "Flat 4B, 124 Park Avenue, Downtown" }
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("customer_submit_login_button")
                ) {
                    Text(
                        text = "Sign In & Start Shopping",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

            } else {
                // Owner Form
                Text(
                    text = "Store Owner Login",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Text(
                    text = "Manage inventory, orders, and receive payments",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = ownerEmail,
                    onValueChange = {
                        ownerEmail = it
                        onClearError()
                    },
                    label = { Text("Store Email or Admin ID") },
                    placeholder = { Text("owner@vmart.com") },
                    leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.Black) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_owner_email")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = ownerPin,
                    onValueChange = {
                        if (it.length <= 8) {
                            ownerPin = it
                            onClearError()
                        }
                    },
                    label = { Text("Store Passcode (PIN)") },
                    placeholder = { Text("Enter 4-digit PIN") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black) },
                    trailingIcon = {
                        IconButton(onClick = { isPinVisible = !isPinVisible }) {
                            Icon(
                                imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPinVisible) "Hide PIN" else "Show PIN",
                                tint = Color.Black
                            )
                        }
                    },
                    visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_owner_pin")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onOwnerLogin(ownerEmail.ifBlank { "owner@vmart.com" }, ownerPin.ifBlank { "1234" })
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("owner_submit_login_button")
                ) {
                    Text(
                        text = "Access Shop Owner Hub",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF4F5F7) else Color(0xFFFAFAFC)
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) Color.Black else Color(0xFFE5E7EB)
        ),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.Black else Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7280)
            )
        }
    }
}
