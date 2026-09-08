package com.example.data.repository

import android.content.Context
import com.example.data.dao.OrderDao
import com.example.data.dao.ProductDao
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ShopProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class VMartRepository(
    private val productDao: ProductDao,
    private val orderDao: OrderDao
) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()
    val activeOrders: Flow<List<Order>> = orderDao.getActiveOrders()
    val deliveredOrders: Flow<List<Order>> = orderDao.getDeliveredOrders()

    private val _shopProfile = MutableStateFlow(
        ShopProfile(
            shopName = "V-Mart Fresh Supermarket",
            ownerName = "Rajesh Sharma (Owner)",
            phone = "+1 (800) 555-8627",
            upiId = "vmart.groceries@okaxis",
            address = "Shop 12, Main Central Market, Green Avenue",
            deliveryFee = 1.99,
            freeDeliveryAbove = 30.00
        )
    )
    val shopProfile: StateFlow<ShopProfile> = _shopProfile.asStateFlow()

    suspend fun updateShopProfile(profile: ShopProfile) {
        _shopProfile.value = profile
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val count = productDao.getProductCount()
        if (count == 0) {
            val initialProducts = listOf(
                // Fresh Veggies
                Product(
                    name = "Farm Fresh Tomatoes",
                    category = "Fresh Veggies",
                    price = 1.49,
                    originalPrice = 1.99,
                    unit = "500 g",
                    iconEmoji = "🍅",
                    inStock = true,
                    stockCount = 45,
                    description = "Ripe, juicy red tomatoes sourced directly from local organic farms."
                ),
                Product(
                    name = "Crisp Green Broccoli",
                    category = "Fresh Veggies",
                    price = 1.99,
                    originalPrice = 2.49,
                    unit = "1 pc (approx 400g)",
                    iconEmoji = "🥦",
                    inStock = true,
                    stockCount = 28,
                    description = "Freshly harvested nutrient-rich green broccoli crowns."
                ),
                Product(
                    name = "Baby Spinach Bunch",
                    category = "Fresh Veggies",
                    price = 1.29,
                    originalPrice = null,
                    unit = "250 g bunch",
                    iconEmoji = "🥬",
                    inStock = true,
                    stockCount = 35,
                    description = "Tender, washed organic baby spinach leaves, ideal for salads."
                ),
                Product(
                    name = "Red Organic Onions",
                    category = "Fresh Veggies",
                    price = 1.89,
                    originalPrice = 2.29,
                    unit = "1 kg bag",
                    iconEmoji = "🧅",
                    inStock = true,
                    stockCount = 60,
                    description = "Crisp, flavorful red onions essential for daily cooking."
                ),
                Product(
                    name = "Idaho Golden Potatoes",
                    category = "Fresh Veggies",
                    price = 1.69,
                    originalPrice = null,
                    unit = "1 kg",
                    iconEmoji = "🥔",
                    inStock = true,
                    stockCount = 80,
                    description = "Freshly cleaned all-purpose potatoes perfect for baking or mashing."
                ),
                Product(
                    name = "Sweet Bell Peppers",
                    category = "Fresh Veggies",
                    price = 2.49,
                    originalPrice = 2.99,
                    unit = "Pack of 3 (Mixed)",
                    iconEmoji = "🫑",
                    inStock = true,
                    stockCount = 20,
                    description = "Vibrant trio of red, yellow, and green sweet bell peppers."
                ),

                // Juicy Fruits
                Product(
                    name = "Royal Gala Apples",
                    category = "Juicy Fruits",
                    price = 3.49,
                    originalPrice = 4.29,
                    unit = "1 kg (approx 5-6 pcs)",
                    iconEmoji = "🍎",
                    inStock = true,
                    stockCount = 50,
                    description = "Crisp, sweet, and aromatic dessert apples."
                ),
                Product(
                    name = "Fresh Cavendish Bananas",
                    category = "Juicy Fruits",
                    price = 1.79,
                    originalPrice = null,
                    unit = "1 dozen (12 pcs)",
                    iconEmoji = "🍌",
                    inStock = true,
                    stockCount = 40,
                    description = "Naturally ripened, energy-rich potassium source."
                ),
                Product(
                    name = "Sweet Valencia Oranges",
                    category = "Juicy Fruits",
                    price = 2.99,
                    originalPrice = 3.50,
                    unit = "1 kg pack",
                    iconEmoji = "🍊",
                    inStock = true,
                    stockCount = 30,
                    description = "Juicy citrus oranges rich in Vitamin C."
                ),
                Product(
                    name = "Organic Strawberries",
                    category = "Juicy Fruits",
                    price = 3.99,
                    originalPrice = 4.99,
                    unit = "400 g box",
                    iconEmoji = "🍓",
                    inStock = true,
                    stockCount = 15,
                    description = "Sweet handpicked red berries, fragrant and delicious."
                ),
                Product(
                    name = "Ripe Hass Avocados",
                    category = "Juicy Fruits",
                    price = 2.79,
                    originalPrice = 3.29,
                    unit = "2 pcs",
                    iconEmoji = "🥑",
                    inStock = true,
                    stockCount = 25,
                    description = "Creamy, nutrient-packed avocados ready to eat."
                ),

                // Dairy & Bakery
                Product(
                    name = "Farm Fresh Whole Milk",
                    category = "Dairy & Bakery",
                    price = 3.89,
                    originalPrice = null,
                    unit = "1 Gallon (3.78 L)",
                    iconEmoji = "🥛",
                    inStock = true,
                    stockCount = 40,
                    description = "Pure pasteurized whole vitamin D cow's milk."
                ),
                Product(
                    name = "100% Whole Wheat Bread",
                    category = "Dairy & Bakery",
                    price = 2.19,
                    originalPrice = 2.69,
                    unit = "400 g loaf",
                    iconEmoji = "🍞",
                    inStock = true,
                    stockCount = 25,
                    description = "Freshly baked artisan whole grain bread slices."
                ),
                Product(
                    name = "Pure Grass-Fed Salted Butter",
                    category = "Dairy & Bakery",
                    price = 2.99,
                    originalPrice = 3.49,
                    unit = "250 g block",
                    iconEmoji = "🧈",
                    inStock = true,
                    stockCount = 30,
                    description = "Rich, golden churned butter with a hint of sea salt."
                ),
                Product(
                    name = "Farm Fresh Brown Eggs",
                    category = "Dairy & Bakery",
                    price = 3.49,
                    originalPrice = 3.99,
                    unit = "Pack of 12",
                    iconEmoji = "🥚",
                    inStock = true,
                    stockCount = 50,
                    description = "Grade-A free-range brown eggs full of natural protein."
                ),
                Product(
                    name = "Sharp Cheddar Cheese",
                    category = "Dairy & Bakery",
                    price = 3.79,
                    originalPrice = null,
                    unit = "200 g block",
                    iconEmoji = "🧀",
                    inStock = true,
                    stockCount = 22,
                    description = "Aged dairy cheese with rich creamy texture."
                ),

                // Staples & Grains
                Product(
                    name = "Premium Aged Basmati Rice",
                    category = "Staples & Grains",
                    price = 6.49,
                    originalPrice = 7.99,
                    unit = "2 kg bag",
                    iconEmoji = "🍚",
                    inStock = true,
                    stockCount = 35,
                    description = "Long grain aromatic Basmati rice, aged for 2 years."
                ),
                Product(
                    name = "Extra Virgin Olive Oil",
                    category = "Staples & Grains",
                    price = 7.99,
                    originalPrice = 9.49,
                    unit = "500 ml bottle",
                    iconEmoji = "🫒",
                    inStock = true,
                    stockCount = 20,
                    description = "First cold-pressed Mediterranean extra virgin olive oil."
                ),
                Product(
                    name = "Organic Whole Wheat Flour",
                    category = "Staples & Grains",
                    price = 3.49,
                    originalPrice = null,
                    unit = "2 kg pack",
                    iconEmoji = "🌾",
                    inStock = true,
                    stockCount = 45,
                    description = "Stone-ground pure chakki atta for soft and healthy chapatis."
                ),
                Product(
                    name = "Red Kidney Beans (Rajma)",
                    category = "Staples & Grains",
                    price = 2.49,
                    originalPrice = 2.99,
                    unit = "1 kg bag",
                    iconEmoji = "🫘",
                    inStock = true,
                    stockCount = 30,
                    description = "Hand-sorted, high-protein red kidney beans."
                ),

                // Snacks & Drinks
                Product(
                    name = "100% Pure Orange Juice",
                    category = "Snacks & Drinks",
                    price = 3.19,
                    originalPrice = null,
                    unit = "1 Liter bottle",
                    iconEmoji = "🧃",
                    inStock = true,
                    stockCount = 25,
                    description = "No added sugar, freshly squeezed orange juice with pulp."
                ),
                Product(
                    name = "Roasted & Salted California Almonds",
                    category = "Snacks & Drinks",
                    price = 4.99,
                    originalPrice = 5.99,
                    unit = "250 g pouch",
                    iconEmoji = "🥜",
                    inStock = true,
                    stockCount = 30,
                    description = "Crunchy, oven-roasted California almonds lightly sea-salted."
                ),
                Product(
                    name = "Belgian Choc-Chip Cookies",
                    category = "Snacks & Drinks",
                    price = 2.29,
                    originalPrice = 2.79,
                    unit = "200 g pack",
                    iconEmoji = "🍪",
                    inStock = true,
                    stockCount = 40,
                    description = "Buttery cookies loaded with gourmet Belgian chocolate chips."
                ),
                Product(
                    name = "Organic Green Tea Bags",
                    category = "Snacks & Drinks",
                    price = 3.49,
                    originalPrice = null,
                    unit = "25 tea bags",
                    iconEmoji = "🍵",
                    inStock = true,
                    stockCount = 20,
                    description = "Pure Himalayan whole leaf antioxidant green tea."
                )
            )
            productDao.insertAll(initialProducts)
        }

        // Also pre-seed a sample active order and a delivered order if empty
        val orderCount = orderDao.getOrderCount()
        if (orderCount == 0) {
            val sampleOrders = listOf(
                Order(
                    customerName = "Priya Sharma",
                    customerPhone = "+1 (555) 234-5678",
                    deliveryAddress = "Apt 4B, Sunflower Residency, 3rd Cross",
                    deliveryNotes = "Please ring bell and leave near doorstep",
                    orderTimestamp = System.currentTimeMillis() - (25 * 60 * 1000), // 25 mins ago
                    itemsSummary = "Farm Fresh Tomatoes x 1, Royal Gala Apples x 1, Whole Milk x 1",
                    itemsCount = 3,
                    subtotal = 8.87,
                    deliveryFee = 1.99,
                    totalAmount = 10.86,
                    paymentMethod = "UPI / QR Code",
                    paymentStatus = "Pending", // Needs shop owner confirmation
                    deliveryStatus = "Out for Delivery",
                    deliveryPersonName = "Vikram (V-Mart Rider)",
                    paymentReference = "UPI-TXN-88231"
                ),
                Order(
                    customerName = "Amit Patel",
                    customerPhone = "+1 (555) 987-6543",
                    deliveryAddress = "House 12, Green Park Avenue",
                    deliveryNotes = "Call before arrival",
                    orderTimestamp = System.currentTimeMillis() - (5 * 60 * 1000), // 5 mins ago
                    itemsSummary = "Premium Basmati Rice x 1, Farm Fresh Brown Eggs x 1, Sweet Bell Peppers x 1",
                    itemsCount = 3,
                    subtotal = 12.47,
                    deliveryFee = 1.99,
                    totalAmount = 14.46,
                    paymentMethod = "Cash on Delivery",
                    paymentStatus = "Pending",
                    deliveryStatus = "New Order",
                    deliveryPersonName = "V-Mart Express",
                    paymentReference = ""
                ),
                Order(
                    customerName = "Sarah Jenkins",
                    customerPhone = "+1 (555) 456-7890",
                    deliveryAddress = "Flat 102, Lakeview Apartments",
                    deliveryNotes = "Delivered to security desk",
                    orderTimestamp = System.currentTimeMillis() - (3 * 3600 * 1000), // 3 hours ago
                    itemsSummary = "Belgian Choc-Chip Cookies x 2, 100% Pure Orange Juice x 1",
                    itemsCount = 3,
                    subtotal = 7.77,
                    deliveryFee = 1.99,
                    totalAmount = 9.76,
                    paymentMethod = "UPI / QR Code",
                    paymentStatus = "Received",
                    deliveryStatus = "Delivered",
                    deliveryPersonName = "Vikram (V-Mart Rider)",
                    paymentReference = "UPI-TXN-77319"
                )
            )
            orderDao.insertAll(sampleOrders)
        }
    }

    // Product actions
    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun toggleStock(id: Long, currentStock: Boolean) = productDao.updateStockStatus(id, !currentStock)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    // Order actions
    suspend fun placeOrder(order: Order): Long = orderDao.insertOrder(order)
    suspend fun updateOrder(order: Order) = orderDao.updateOrder(order)
    suspend fun updateDeliveryStatus(orderId: Long, status: String) = orderDao.updateDeliveryStatus(orderId, status)
    suspend fun updatePaymentStatus(orderId: Long, status: String) = orderDao.updatePaymentStatus(orderId, status)
    suspend fun deleteOrder(order: Order) = orderDao.deleteOrder(order)
}
