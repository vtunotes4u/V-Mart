package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.AppMode
import com.example.ui.theme.AvatarDarkGreen

@Composable
fun VMartTopBar(
    currentMode: AppMode,
    userRole: UserRole,
    customerName: String?,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    onRequestOwnerAccess: () -> Unit,
    onSwitchToCustomer: () -> Unit,
    onLogout: () -> Unit,
    onQuickAddItem: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showProfileMenu by remember { mutableStateOf(false) }

    Surface(
        color = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Brand Title like VYRO BETA in reference
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "V-MART",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontSize = 22.sp
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Small black pill badge next to brand just like BETA badge in reference
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (currentMode == AppMode.CUSTOMER) "GROCERY" else "STORE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
            }

            // Right: Notification Bell & Circular Avatar like reference
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cart or Notification icon
                if (currentMode == AppMode.CUSTOMER) {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("top_bar_cart_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge(
                                        containerColor = Color.Black,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = cartItemCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Shopping Cart",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = { /* Notification view */ },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("top_bar_notification_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Profile Avatar (Dark Green circle with White initial like the 'S' in screenshot)
                val avatarLetter = if (currentMode == AppMode.CUSTOMER) {
                    (customerName?.firstOrNull() ?: 'C').uppercaseChar()
                } else {
                    'S' // Store owner
                }

                Box {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AvatarDarkGreen)
                            .clickable { showProfileMenu = true }
                            .testTag("top_bar_avatar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatarLetter.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                    }

                    // Clean Dropdown menu for role switching and logout
                    DropdownMenu(
                        expanded = showProfileMenu,
                        onDismissRequest = { showProfileMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text(
                                text = if (currentMode == AppMode.CUSTOMER) (customerName ?: "Customer") else "Shop Owner (V-Mart)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                            Text(
                                text = if (currentMode == AppMode.CUSTOMER) "Customer Account" else "Store Manager Hub",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                        HorizontalDivider()

                        if (currentMode == AppMode.CUSTOMER) {
                            DropdownMenuItem(
                                text = { Text("Switch to Shop Owner Hub", color = Color.Black) },
                                leadingIcon = {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color.Black)
                                },
                                onClick = {
                                    showProfileMenu = false
                                    onRequestOwnerAccess()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Switch to Customer View", color = Color.Black) },
                                leadingIcon = {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color.Black)
                                },
                                onClick = {
                                    showProfileMenu = false
                                    onSwitchToCustomer()
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("Log Out", color = Color.Red) },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.Red)
                            },
                            onClick = {
                                showProfileMenu = false
                                onLogout()
                            }
                        )
                    }
                }
            }
        }
    }
}
