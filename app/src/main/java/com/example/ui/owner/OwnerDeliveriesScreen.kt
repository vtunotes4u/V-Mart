package com.example.ui.owner

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.CardBackground
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OwnerDeliveriesScreen(
    orders: List<Order>,
    onUpdateDeliveryStatus: (orderId: Long, newStatus: String) -> Unit,
    onMarkPaymentReceived: (orderId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Active Orders") }

    val activeCount = orders.count { !it.deliveryStatus.equals("Delivered", ignoreCase = true) }
    val deliveredCount = orders.count { it.deliveryStatus.equals("Delivered", ignoreCase = true) }
    val newCount = orders.count { it.deliveryStatus.equals("New Order", ignoreCase = true) }
    val pendingPaymentsCount = orders.count { !it.paymentStatus.contains("Received", ignoreCase = true) }
    val totalRevenue = orders
        .filter { it.paymentStatus.contains("Received", ignoreCase = true) }
        .sumOf { it.totalAmount }

    val filteredOrders = when (selectedFilter) {
        "Active Orders" -> orders.filter { !it.deliveryStatus.equals("Delivered", ignoreCase = true) }
        "New Orders" -> orders.filter { it.deliveryStatus.equals("New Order", ignoreCase = true) }
        "Out for Delivery" -> orders.filter { it.deliveryStatus.equals("Out for Delivery", ignoreCase = true) }
        "Delivered" -> orders.filter { it.deliveryStatus.equals("Delivered", ignoreCase = true) }
        else -> orders
    }

    Surface(
        color = Color.White,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Header matching reference screenshot
            item {
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
                        text = "Dispatch grocery orders and collect payments",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            // Stats Row 1: 2 Cards (Profiles 2, Posts 14 in reference)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCardHorizontal(
                        label = "Active Orders",
                        value = activeCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCardHorizontal(
                        label = "Delivered",
                        value = deliveredCount.toString(),
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
                        label = "New Orders",
                        value = newCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCardVertical(
                        label = "Pending Pay",
                        value = pendingPaymentsCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCardVertical(
                        label = "Collected",
                        value = "₹${String.format("%.0f", totalRevenue)}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Filter Chips
            item {
                val filters = listOf("Active Orders", "New Orders", "Out for Delivery", "Delivered", "All Orders")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    filter,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White,
                                containerColor = CardBackground,
                                labelColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = null,
                            modifier = Modifier.testTag("filter_order_$filter")
                        )
                    }
                }
            }

            // Orders List
            if (filteredOrders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📦", fontSize = 44.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No orders in $selectedFilter",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            items(filteredOrders, key = { it.id }) { order ->
                OwnerOrderDeliveryCard(
                    order = order,
                    onUpdateDeliveryStatus = { newStatus -> onUpdateDeliveryStatus(order.id, newStatus) },
                    onMarkPaymentReceived = { onMarkPaymentReceived(order.id) }
                )
            }
        }
    }
}

@Composable
fun OwnerOrderDeliveryCard(
    order: Order,
    onUpdateDeliveryStatus: (String) -> Unit,
    onMarkPaymentReceived: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(order.orderTimestamp))

    val isDelivered = order.deliveryStatus.equals("Delivered", ignoreCase = true)
    val isPaymentReceived = order.paymentStatus.contains("Received", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .fillMaxWidth()
            .testTag("owner_order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Order # + Date + Current Delivery Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.id}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280)
                    )
                }

                // Delivery Status Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDelivered) Color.White else Color.Black)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.deliveryStatus,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDelivered) SuccessGreen else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFE5E7EB))
            Spacer(modifier = Modifier.height(10.dp))

            // Customer Details with Call Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = order.customerName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
                    Text(
                        text = order.customerPhone,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${order.customerPhone}")
                        }
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call Customer", tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delivery Address
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = order.deliveryAddress,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.Black
                    )
                    if (order.deliveryNotes.isNotBlank()) {
                        Text(
                            text = "Note: ${order.deliveryNotes}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grocery Items Summary
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Ordered Items (${order.itemsCount}):",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Receipt Status & Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = if (isPaymentReceived) SuccessGreen else Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.paymentMethod,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.Black
                        )
                    }
                    Text(
                        text = if (isPaymentReceived) "✓ Payment Received" else "Status: ${order.paymentStatus}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isPaymentReceived) SuccessGreen else Color(0xFFDC2626)
                    )
                }

                Text(
                    text = "₹${String.format("%.2f", order.totalAmount)}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Delivery & Payment Action Buttons for Shop Owner
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isPaymentReceived) {
                    Button(
                        onClick = onMarkPaymentReceived,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("mark_payment_received_${order.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Confirm Payment Received (₹${String.format("%.2f", order.totalAmount)})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                when (order.deliveryStatus) {
                    "New Order" -> {
                        Button(
                            onClick = { onUpdateDeliveryStatus("Packed") },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("action_pack_${order.id}")
                        ) {
                            Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark as Packed & Ready", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    "Packed" -> {
                        Button(
                            onClick = { onUpdateDeliveryStatus("Out for Delivery") },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("action_dispatch_${order.id}")
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dispatch / Out for Delivery", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    "Out for Delivery" -> {
                        Button(
                            onClick = { onUpdateDeliveryStatus("Delivered") },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("action_deliver_${order.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark Delivered to Customer", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    "Delivered" -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Order Successfully Delivered",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
