package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.VMartDatabase
import com.example.data.model.CartItem
import com.example.data.model.CustomerUser
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ShopOwnerUser
import com.example.data.model.ShopProfile
import com.example.data.model.UserRole
import com.example.data.repository.VMartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppMode {
    CUSTOMER,
    SHOP_OWNER
}

enum class CustomerTab {
    SHOP,
    CART,
    MY_ORDERS
}

enum class ShopOwnerTab {
    ORDERS_DELIVERIES,
    INVENTORY,
    STORE_STATS
}

data class CartState(
    val items: Map<Long, Int> = emptyMap(), // productId -> quantity
    val customerName: String = "",
    val customerPhone: String = "",
    val deliveryAddress: String = "",
    val deliveryNotes: String = "",
    val paymentMethod: String = "UPI / QR Code" // "UPI / QR Code" or "Cash on Delivery"
)

class VMartViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VMartRepository

    init {
        val database = VMartDatabase.getDatabase(application)
        repository = VMartRepository(database.productDao(), database.orderDao())
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    // App Mode & Navigation States
    private val _userRole = MutableStateFlow(UserRole.LOGGED_OUT)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _currentCustomer = MutableStateFlow<CustomerUser?>(null)
    val currentCustomer: StateFlow<CustomerUser?> = _currentCustomer.asStateFlow()

    private val _currentOwner = MutableStateFlow<ShopOwnerUser?>(null)
    val currentOwner: StateFlow<ShopOwnerUser?> = _currentOwner.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _showOwnerLoginModal = MutableStateFlow(false)
    val showOwnerLoginModal: StateFlow<Boolean> = _showOwnerLoginModal.asStateFlow()

    private val _appMode = MutableStateFlow(AppMode.CUSTOMER)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    private val _customerTab = MutableStateFlow(CustomerTab.SHOP)
    val customerTab: StateFlow<CustomerTab> = _customerTab.asStateFlow()

    private val _shopOwnerTab = MutableStateFlow(ShopOwnerTab.ORDERS_DELIVERIES)
    val shopOwnerTab: StateFlow<ShopOwnerTab> = _shopOwnerTab.asStateFlow()

    // Search and Filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Cart State
    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    // Order feedback
    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    private val _showOrderSuccessDialog = MutableStateFlow(false)
    val showOrderSuccessDialog: StateFlow<Boolean> = _showOrderSuccessDialog.asStateFlow()

    // Shop Profile
    val shopProfile: StateFlow<ShopProfile> = repository.shopProfile

    // Products Flow
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Products Flow
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        searchQuery,
        selectedCategory
    ) { products, query, category ->
        products.filter { product ->
            val cleanCat = category.replace(Regex("[^A-Za-z0-9 ]"), "").trim()
            val cleanProdCat = product.category.replace(Regex("[^A-Za-z0-9 ]"), "").trim()
            val matchesCategory = (category == "All" ||
                    cleanProdCat.contains(cleanCat, ignoreCase = true) ||
                    cleanCat.contains(cleanProdCat, ignoreCase = true) ||
                    product.category.equals(category, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Orders Flow
    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Orders Flow
    val activeOrders: StateFlow<List<Order>> = repository.activeOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Delivered Orders Flow
    val deliveredOrders: StateFlow<List<Order>> = repository.deliveredOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart detailed items with product entities
    val cartItemsWithProducts: StateFlow<List<CartItem>> = combine(
        allProducts,
        _cartState
    ) { products, cart ->
        val productMap = products.associateBy { it.id }
        cart.items.mapNotNull { (prodId, qty) ->
            productMap[prodId]?.let { product ->
                CartItem(product = product, quantity = qty)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart calculations
    val cartSubtotal: StateFlow<Double> = cartItemsWithProducts.combine(allProducts) { items, _ ->
        items.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount: StateFlow<Int> = _cartState.combine(allProducts) { cart, _ ->
        cart.items.values.sum()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Actions
    fun setAppMode(mode: AppMode) {
        _appMode.value = mode
    }

    fun setCustomerTab(tab: CustomerTab) {
        _customerTab.value = tab
    }

    fun setShopOwnerTab(tab: ShopOwnerTab) {
        _shopOwnerTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addToCart(productId: Long) {
        val currentItems = _cartState.value.items.toMutableMap()
        val currentQty = currentItems[productId] ?: 0
        currentItems[productId] = currentQty + 1
        _cartState.value = _cartState.value.copy(items = currentItems)
    }

    fun decrementCartItem(productId: Long) {
        val currentItems = _cartState.value.items.toMutableMap()
        val currentQty = currentItems[productId] ?: 0
        if (currentQty > 1) {
            currentItems[productId] = currentQty - 1
        } else {
            currentItems.remove(productId)
        }
        _cartState.value = _cartState.value.copy(items = currentItems)
    }

    fun removeCartItem(productId: Long) {
        val currentItems = _cartState.value.items.toMutableMap()
        currentItems.remove(productId)
        _cartState.value = _cartState.value.copy(items = currentItems)
    }

    fun clearCart() {
        _cartState.value = _cartState.value.copy(items = emptyMap())
    }

    fun updateCustomerDetails(
        name: String? = null,
        phone: String? = null,
        address: String? = null,
        notes: String? = null,
        paymentMethod: String? = null
    ) {
        _cartState.value = _cartState.value.copy(
            customerName = name ?: _cartState.value.customerName,
            customerPhone = phone ?: _cartState.value.customerPhone,
            deliveryAddress = address ?: _cartState.value.deliveryAddress,
            deliveryNotes = notes ?: _cartState.value.deliveryNotes,
            paymentMethod = paymentMethod ?: _cartState.value.paymentMethod
        )
    }

    fun placeOrder(onSuccess: (Order) -> Unit) {
        viewModelScope.launch {
            val items = cartItemsWithProducts.value
            if (items.isEmpty()) return@launch

            val currentCart = _cartState.value
            val subtotal = items.sumOf { it.totalPrice }
            val profile = shopProfile.value
            val deliveryFee = if (subtotal >= profile.freeDeliveryAbove) 0.0 else profile.deliveryFee
            val total = subtotal + deliveryFee

            val itemsSummary = items.joinToString(", ") { "${it.product.name} x ${it.quantity}" }
            val itemsCount = items.sumOf { it.quantity }

            val isUpiPayment = currentCart.paymentMethod.contains("UPI", ignoreCase = true)
            val order = Order(
                customerName = currentCart.customerName.ifBlank { "Alex Morgan" },
                customerPhone = currentCart.customerPhone.ifBlank { "+91 98765 43210" },
                deliveryAddress = currentCart.deliveryAddress.ifBlank { "Flat 4B, 124 Park Avenue, Downtown" },
                deliveryNotes = currentCart.deliveryNotes,
                orderTimestamp = System.currentTimeMillis(),
                itemsSummary = itemsSummary,
                itemsCount = itemsCount,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                totalAmount = total,
                paymentMethod = currentCart.paymentMethod,
                paymentStatus = if (isUpiPayment) "Received (UPI Verified)" else "Pending (Cash on Delivery)",
                deliveryStatus = "New Order",
                deliveryPersonName = "V-Mart Express Delivery",
                paymentReference = if (isUpiPayment) "UPI-TXN-${System.currentTimeMillis() % 10000000}" else ""
            )

            val orderId = repository.placeOrder(order)
            val placedOrderWithId = order.copy(id = orderId)
            _lastPlacedOrder.value = placedOrderWithId
            _showOrderSuccessDialog.value = true

            // Clear cart
            clearCart()
            onSuccess(placedOrderWithId)
        }
    }

    fun updateCustomerOrder(order: Order) {
        viewModelScope.launch {
            repository.updateOrder(order)
        }
    }

    fun dismissOrderSuccessDialog() {
        _showOrderSuccessDialog.value = false
    }

    // Shop Owner Actions
    fun updateDeliveryStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateDeliveryStatus(orderId, newStatus)
        }
    }

    fun markPaymentReceived(orderId: Long) {
        viewModelScope.launch {
            repository.updatePaymentStatus(orderId, "Received")
        }
    }

    fun toggleProductStock(product: Product) {
        viewModelScope.launch {
            repository.toggleStock(product.id, product.inStock)
        }
    }

    fun addProduct(
        name: String,
        category: String,
        price: Double,
        originalPrice: Double?,
        unit: String,
        iconEmoji: String,
        description: String
    ) {
        viewModelScope.launch {
            val product = Product(
                name = name,
                category = category,
                price = price,
                originalPrice = originalPrice,
                unit = unit,
                iconEmoji = iconEmoji.ifBlank { "🛒" },
                inStock = true,
                stockCount = 50,
                description = description
            )
            repository.insertProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun updateShopProfile(profile: ShopProfile) {
        viewModelScope.launch {
            repository.updateShopProfile(profile)
        }
    }

    // Authentication Actions
    fun loginCustomer(
        name: String,
        phone: String,
        address: String,
        email: String = ""
    ) {
        val custName = name.ifBlank { "Alex Morgan" }
        val custPhone = phone.ifBlank { "+1 (555) 012-3456" }
        val custAddress = address.ifBlank { "Apartment 4B, 128 Green Valley Road" }
        val custEmail = email.ifBlank { "alex.morgan@email.com" }

        val customer = CustomerUser(
            name = custName,
            phone = custPhone,
            email = custEmail,
            defaultAddress = custAddress
        )
        _currentCustomer.value = customer
        _userRole.value = UserRole.CUSTOMER
        _appMode.value = AppMode.CUSTOMER
        _customerTab.value = CustomerTab.SHOP
        _authError.value = null
        _showOwnerLoginModal.value = false

        // Prepopulate cart with customer's address and details
        updateCustomerDetails(
            name = custName,
            phone = custPhone,
            address = custAddress
        )
    }

    fun loginShopOwner(email: String, pin: String): Boolean {
        val trimmedPin = pin.trim()
        if (trimmedPin == "1234" || trimmedPin.length >= 4) {
            val owner = ShopOwnerUser(
                name = "Vasant Mali",
                email = email.ifBlank { "owner@vmart.com" },
                storeName = "V-Mart Fresh Mart",
                storeCode = "VMART-101"
            )
            _currentOwner.value = owner
            _userRole.value = UserRole.SHOP_OWNER
            _appMode.value = AppMode.SHOP_OWNER
            _shopOwnerTab.value = ShopOwnerTab.ORDERS_DELIVERIES
            _authError.value = null
            _showOwnerLoginModal.value = false
            return true
        } else {
            _authError.value = "Incorrect PIN. Default demo PIN is 1234 (or any 4-digit code)."
            return false
        }
    }

    fun quickDemoCustomerLogin() {
        loginCustomer(
            name = "Alex Morgan",
            phone = "+1 (555) 012-3456",
            address = "Apt 4B, 128 Green Valley Road",
            email = "alex.morgan@email.com"
        )
    }

    fun quickDemoOwnerLogin() {
        loginShopOwner(email = "owner@vmart.com", pin = "1234")
    }

    fun logout() {
        _userRole.value = UserRole.LOGGED_OUT
        _authError.value = null
        _showOwnerLoginModal.value = false
    }

    fun openOwnerLoginModal() {
        _authError.value = null
        _showOwnerLoginModal.value = true
    }

    fun closeOwnerLoginModal() {
        _authError.value = null
        _showOwnerLoginModal.value = false
    }

    fun clearAuthError() {
        _authError.value = null
    }
}
