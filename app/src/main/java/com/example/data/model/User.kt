package com.example.data.model

enum class UserRole {
    LOGGED_OUT,
    CUSTOMER,
    SHOP_OWNER
}

data class CustomerUser(
    val id: String = "cust_1",
    val name: String = "Alex Morgan",
    val phone: String = "+1 (555) 012-3456",
    val email: String = "alex.morgan@email.com",
    val defaultAddress: String = "Apartment 4B, 128 Green Valley Road"
)

data class ShopOwnerUser(
    val id: String = "owner_1",
    val name: String = "Vasant Mali",
    val email: String = "owner@vmart.com",
    val storeName: String = "V-Mart Fresh Mart",
    val storeCode: String = "VMART-101",
    val roleTitle: String = "Store Manager & Owner"
)
