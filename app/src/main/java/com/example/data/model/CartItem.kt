package com.example.data.model

data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}

data class ShopProfile(
    val shopName: String = "V-Mart Supermarket",
    val ownerName: String = "Store Manager",
    val phone: String = "+91 98765 43210",
    val upiId: String = "vmart.fresh@upi",
    val address: String = "Shop #4, Market Square, Central Bazaar",
    val deliveryFee: Double = 25.0,
    val freeDeliveryAbove: Double = 300.0,
    val customQrPayload: String = "upi://pay?pa=vmart.fresh@upi&pn=VMartFresh&cu=INR"
)
