package com.example.ui.owner

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.ShopProfile
import com.example.ui.components.QrCodeView
import com.example.ui.theme.CardBackground
import com.example.ui.theme.SuccessGreen

@Composable
fun OwnerRevenueScreen(
    orders: List<Order>,
    shopProfile: ShopProfile,
    onUpdateShopProfile: (ShopProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val totalOrders = orders.size
    val paidOrders = orders.filter { it.paymentStatus.contains("Received", ignoreCase = true) }
    val totalRevenue = paidOrders.sumOf { it.totalAmount }

    val pendingOrders = orders.filter { !it.paymentStatus.contains("Received", ignoreCase = true) }
    val pendingRevenue = pendingOrders.sumOf { it.totalAmount }

    val upiOrders = orders.filter { it.paymentMethod.contains("UPI", ignoreCase = true) }
    val upiRevenue = upiOrders.filter { it.paymentStatus.contains("Received", ignoreCase = true) }.sumOf { it.totalAmount }

    val codOrders = orders.filter { it.paymentMethod.contains("Cash", ignoreCase = true) }
    val codRevenue = codOrders.filter { it.paymentStatus.contains("Received", ignoreCase = true) }.sumOf { it.totalAmount }

    val avgOrderValue = if (totalOrders > 0) orders.sumOf { it.totalAmount } / totalOrders else 0.0

    var isEditingSettings by remember { mutableStateOf(false) }
    var shopName by remember { mutableStateOf(shopProfile.shopName) }
    var ownerName by remember { mutableStateOf(shopProfile.ownerName) }
    var phone by remember { mutableStateOf(shopProfile.phone) }
    var upiId by remember { mutableStateOf(shopProfile.upiId) }
    var address by remember { mutableStateOf(shopProfile.address) }
    var deliveryFeeStr by remember { mutableStateOf(shopProfile.deliveryFee.toString()) }
    var freeDeliveryAboveStr by remember { mutableStateOf(shopProfile.freeDeliveryAbove.toString()) }
    var customQrPayload by remember { mutableStateOf(shopProfile.customQrPayload) }

    Surface(
        color = Color.White,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header matching reference screenshot
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My stats",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = Color.Black
                        )
                        Text(
                            text = "Track earnings and watch your revenue grow",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6B7280)
                        )
                    }

                    OutlinedButton(
                        onClick = { isEditingSettings = !isEditingSettings },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.Black),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isEditingSettings) "Cancel" else "Edit ⚙",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
                }
            }

            // Stats Row 1: 2 Cards (Profiles 2, Posts 14 in reference)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCardHorizontal(
                        label = "Revenue",
                        value = "₹${String.format("%.0f", totalRevenue)}",
                        modifier = Modifier.weight(1f)
                    )
                    StatCardHorizontal(
                        label = "Orders",
                        value = totalOrders.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stats Row 2: 3 Cards (Followers 1, Views 1.6k, Avg views 113 in reference)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCardVertical(
                        label = "Paid",
                        value = paidOrders.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCardVertical(
                        label = "Pending",
                        value = "₹${String.format("%.0f", pendingRevenue)}",
                        modifier = Modifier.weight(1f)
                    )
                    StatCardVertical(
                        label = "Avg Order",
                        value = "₹${String.format("%.0f", avgOrderValue)}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Section 2: Payment Methods
            item {
                Column {
                    Text(
                        text = "Payments received",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Color.Black
                    )
                    Text(
                        text = "Verification by UPI QR & Cash on Delivery",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            // Payment Breakdown Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // UPI Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("UPI QR", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                            Text("${upiOrders.size} orders", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${String.format("%.2f", upiRevenue)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.Black
                            )
                        }
                    }

                    // COD Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Cash on Delivery", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                            Text("${codOrders.size} orders", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${String.format("%.2f", codRevenue)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Section 3: Owner QR Code & Payment QR Display
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Owner QR Code (UPI)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                                Text(
                                    text = "Displayed to customers at checkout",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // QR Preview Box
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .padding(12.dp)
                        ) {
                            QrCodeView(
                                data = (if (isEditingSettings) customQrPayload else shopProfile.customQrPayload).ifBlank {
                                    "upi://pay?pa=${shopProfile.upiId}&pn=${shopProfile.shopName}&cu=INR"
                                },
                                size = 160.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Linked to UPI: ${if (isEditingSettings) upiId else shopProfile.upiId}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.Black
                        )
                        Text(
                            text = if (isEditingSettings) "You can update the UPI ID or QR payload string below if customers report issues." else "Tap 'Edit ⚙' above to change your QR code or UPI link anytime.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Section 4: Store & QR Settings
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Store & Delivery Profile",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )

                            if (isEditingSettings) {
                                Button(
                                    onClick = {
                                        val fee = deliveryFeeStr.toDoubleOrNull() ?: shopProfile.deliveryFee
                                        val freeAbove = freeDeliveryAboveStr.toDoubleOrNull() ?: shopProfile.freeDeliveryAbove
                                        onUpdateShopProfile(
                                            ShopProfile(
                                                shopName = shopName.ifBlank { shopProfile.shopName },
                                                ownerName = ownerName.ifBlank { shopProfile.ownerName },
                                                phone = phone.ifBlank { shopProfile.phone },
                                                upiId = upiId.ifBlank { shopProfile.upiId },
                                                address = address.ifBlank { shopProfile.address },
                                                deliveryFee = fee,
                                                freeDeliveryAbove = freeAbove,
                                                customQrPayload = customQrPayload.ifBlank { "upi://pay?pa=${upiId.ifBlank { shopProfile.upiId }}&pn=${shopName.ifBlank { shopProfile.shopName }}&cu=INR" }
                                            )
                                        )
                                        Toast.makeText(context, "Store & QR settings saved!", Toast.LENGTH_SHORT).show()
                                        isEditingSettings = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Save", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isEditingSettings) {
                            OutlinedTextField(
                                value = shopName,
                                onValueChange = { shopName = it },
                                label = { Text("Store Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = upiId,
                                onValueChange = { upiId = it },
                                label = { Text("Store UPI ID (For Customer Payments)") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = customQrPayload,
                                onValueChange = { customQrPayload = it },
                                label = { Text("Custom QR Code Payload (UPI URL or text)") },
                                placeholder = { Text("e.g. upi://pay?pa=yourname@upi&pn=StoreName&cu=INR") },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = deliveryFeeStr,
                                    onValueChange = { deliveryFeeStr = it },
                                    label = { Text("Delivery Fee (₹)") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = Color.Black,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = freeDeliveryAboveStr,
                                    onValueChange = { freeDeliveryAboveStr = it },
                                    label = { Text("Free Above (₹)") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = Color.Black,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ProfileInfoRow(label = "Store Name", value = shopProfile.shopName)
                                ProfileInfoRow(label = "Owner / Contact", value = "${shopProfile.ownerName} (${shopProfile.phone})")
                                ProfileInfoRow(label = "UPI Payment ID", value = shopProfile.upiId)
                                ProfileInfoRow(label = "QR Code Payload", value = shopProfile.customQrPayload.ifBlank { "Default (upi://pay?pa=${shopProfile.upiId}...)" })
                                ProfileInfoRow(label = "Delivery Fee", value = "₹${String.format("%.2f", shopProfile.deliveryFee)} (Free above ₹${String.format("%.0f", shopProfile.freeDeliveryAbove)})")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.Black)
    }
}
