package com.example.ui.customer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.ShopProfile
import com.example.ui.CartState
import com.example.ui.components.QrCodeView
import com.example.ui.theme.CardBackground
import com.example.ui.theme.SuccessGreen

@Composable
fun CustomerCartScreen(
    cartItems: List<CartItem>,
    cartState: CartState,
    shopProfile: ShopProfile,
    onAddToCart: (Long) -> Unit,
    onDecrementCart: (Long) -> Unit,
    onRemoveCartItem: (Long) -> Unit,
    onUpdateCustomerDetails: (name: String?, phone: String?, address: String?, notes: String?, paymentMethod: String?) -> Unit,
    onPlaceOrder: (onSuccess: (Order) -> Unit) -> Unit,
    onBrowseGroceries: () -> Unit,
    onViewOrderDetails: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val subtotal = cartItems.sumOf { it.totalPrice }
    val deliveryFee = if (subtotal >= shopProfile.freeDeliveryAbove) 0.0 else shopProfile.deliveryFee
    val total = subtotal + deliveryFee

    var showOrderSuccessDialog by remember { mutableStateOf(false) }
    var placedOrder by remember { mutableStateOf<Order?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isUpiPaymentDone by remember { mutableStateOf(false) }

    Surface(
        color = Color.White,
        modifier = modifier.fillMaxSize()
    ) {
        if (cartItems.isEmpty() && !showOrderSuccessDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CardBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Grocery Cart is Empty",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore our fresh produce and daily groceries to start shopping.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onBrowseGroceries,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.testTag("empty_cart_browse_button")
                    ) {
                        Text("Explore Groceries", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            return@Surface
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cart & Checkout",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = Color.Black
                        )
                        Text(
                            text = "${cartItems.sumOf { it.quantity }} items from ${shopProfile.shopName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6B7280)
                        )
                    }
                    OutlinedButton(
                        onClick = onBrowseGroceries,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color.Black),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("+ Add More", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                    }
                }
            }

            // Cart Items Card List
            items(cartItems, key = { it.product.id }) { item ->
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.product.iconEmoji, fontSize = 26.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black,
                                maxLines = 1
                            )
                            Text(
                                text = "${item.product.unit} • ₹${String.format("%.2f", item.product.price)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "₹${String.format("%.2f", item.totalPrice)}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = Color.Black
                            )
                        }

                        // Stepper (- Qty +)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(2.dp)
                        ) {
                            IconButton(
                                onClick = { onDecrementCart(item.product.id) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.Black, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = item.quantity.toString(),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { onAddToCart(item.product.id) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.Black, modifier = Modifier.size(14.dp))
                            }
                        }

                        IconButton(
                            onClick = { onRemoveCartItem(item.product.id) },
                            modifier = Modifier
                                .size(34.dp)
                                .padding(start = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Remove Item",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Delivery Details Section
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Delivery Details",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = cartState.customerName,
                            onValueChange = { onUpdateCustomerDetails(it, null, null, null, null) },
                            label = { Text("Your Full Name") },
                            placeholder = { Text("e.g. Alex Morgan") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = cartState.customerPhone,
                            onValueChange = { onUpdateCustomerDetails(null, it, null, null, null) },
                            label = { Text("Phone Number") },
                            placeholder = { Text("+1 555-019-2831") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_phone_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = cartState.deliveryAddress,
                            onValueChange = { onUpdateCustomerDetails(null, null, it, null, null) },
                            label = { Text("Delivery Address") },
                            placeholder = { Text("Flat / House No., Street, Landmark") },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_address_input")
                        )
                    }
                }
            }

            // Payment Options
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Payment Method",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val isUpi = cartState.paymentMethod.startsWith("UPI")
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = if (isUpi) BorderStroke(1.5.dp, Color.Black) else BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdateCustomerDetails(null, null, null, null, "UPI / QR Code") }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isUpi,
                                            onClick = { onUpdateCustomerDetails(null, null, null, null, "UPI / QR Code") },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text("UPI / Scan QR Code", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                                            Text("GPay, PhonePe, Paytm, BHIM", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                                        }
                                    }
                                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                                }

                                if (isUpi) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(CardBackground)
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Shop UPI: ${shopProfile.upiId}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("V-Mart UPI", shopProfile.upiId))
                                                Toast.makeText(context, "UPI ID Copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.Black, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // Owner UPI QR Code appears directly below UPI and above Cash on Delivery
                        if (isUpi) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.5.dp, Color.Black),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Scan Owner's QR Code to Pay",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Exact amount to pay: ₹${String.format("%.2f", total)}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = Color(0xFF4B5563)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Owner's dynamic QR code
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFFF9FAFB))
                                            .padding(12.dp)
                                    ) {
                                        QrCodeView(
                                            data = shopProfile.customQrPayload.ifBlank { "upi://pay?pa=${shopProfile.upiId}&pn=${shopProfile.shopName}&am=${String.format("%.2f", total)}&cu=INR" },
                                            size = 180.dp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Scan with any UPI app (GPay / PhonePe / Paytm)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF6B7280)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Payment success confirmation toggle
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isUpiPaymentDone) Color(0xFFECFDF5) else CardBackground,
                                        border = BorderStroke(1.dp, if (isUpiPaymentDone) SuccessGreen else Color(0xFFD1D5DB)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { isUpiPaymentDone = !isUpiPaymentDone }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isUpiPaymentDone) SuccessGreen else Color.White)
                                                    .then(if (!isUpiPaymentDone) Modifier.padding(1.dp) else Modifier),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isUpiPaymentDone) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = if (isUpiPaymentDone) "✓ UPI Payment Completed Successfully" else "Tap here after completing UPI payment",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = if (isUpiPaymentDone) Color(0xFF065F46) else Color.Black
                                                )
                                                Text(
                                                    text = if (isUpiPaymentDone) "Order will be placed and automatically confirmed" else "If payment fails or not completed, please choose Cash on Delivery",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isUpiPaymentDone) Color(0xFF047857) else Color(0xFF6B7280)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val isCod = cartState.paymentMethod.startsWith("Cash")
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = if (isCod) BorderStroke(1.5.dp, Color.Black) else BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdateCustomerDetails(null, null, null, null, "Cash on Delivery") }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                RadioButton(
                                    selected = isCod,
                                    onClick = { onUpdateCustomerDetails(null, null, null, null, "Cash on Delivery") },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text("Cash on Delivery (COD)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                                    Text("Pay cash when your groceries arrive", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
                                }
                            }
                        }
                    }
                }
            }

            // Bill Summary
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Bill Summary", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Item Total", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B7280))
                            Text("₹${String.format("%.2f", subtotal)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Fee", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B7280))
                            Text(
                                text = if (deliveryFee == 0.0) "FREE" else "₹${String.format("%.2f", deliveryFee)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (deliveryFee == 0.0) SuccessGreen else Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Grand Total", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                                Text("Inclusive of all taxes", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B7280))
                            }
                            Text(
                                text = "₹${String.format("%.2f", total)}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Place Order Action Button in bold black
            item {
                Button(
                    onClick = {
                        if (cartState.paymentMethod.startsWith("UPI") && !isUpiPaymentDone) {
                            validationError = "Please complete the UPI payment using the QR code above and check 'UPI Payment Completed Successfully' before placing order. Or choose Cash on Delivery."
                            return@Button
                        }
                        if (cartState.customerName.isBlank() || cartState.customerPhone.isBlank() || cartState.deliveryAddress.isBlank()) {
                            validationError = "Please fill in your name, contact phone number, and delivery address to place your order."
                            return@Button
                        }
                        onPlaceOrder { order ->
                            placedOrder = order
                            showOrderSuccessDialog = true
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("place_grocery_order_button")
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Place Order • ₹${String.format("%.2f", total)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }

    // Validation Error Dialog
    if (validationError != null) {
        AlertDialog(
            onDismissRequest = { validationError = null },
            containerColor = Color.White,
            title = {
                Text("Notice", fontWeight = FontWeight.Bold, color = Color.Black)
            },
            text = {
                Text(validationError ?: "", color = Color(0xFF374151))
            },
            confirmButton = {
                Button(
                    onClick = { validationError = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    // Success Dialog
    if (showOrderSuccessDialog && placedOrder != null) {
        val order = placedOrder!!
        AlertDialog(
            onDismissRequest = { showOrderSuccessDialog = false },
            containerColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Order Confirmed!", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Thank you, ${order.customerName}!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your order #${order.id} has been received by ${shopProfile.shopName}. Your groceries will be dispatched shortly to ${order.deliveryAddress}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4B5563)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOrderSuccessDialog = false
                        onViewOrderDetails(order)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Track Order", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOrderSuccessDialog = false }) {
                    Text("Close", color = Color(0xFF6B7280))
                }
            }
        )
    }
}
