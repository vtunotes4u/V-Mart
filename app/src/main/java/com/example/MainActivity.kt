package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.AppMode
import com.example.ui.CustomerTab
import com.example.ui.ShopOwnerTab
import com.example.ui.VMartViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.OwnerLoginDialog
import com.example.ui.components.VMartBottomBar
import com.example.ui.components.VMartTopBar
import com.example.ui.customer.CustomerCartScreen
import com.example.ui.customer.CustomerOrdersScreen
import com.example.ui.customer.CustomerShopScreen
import com.example.ui.owner.OwnerDeliveriesScreen
import com.example.ui.owner.OwnerInventoryScreen
import com.example.ui.owner.OwnerRevenueScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        VMartApp()
      }
    }
  }
}

@Composable
fun VMartApp(
  viewModel: VMartViewModel = viewModel()
) {
  val userRole by viewModel.userRole.collectAsStateWithLifecycle()
  val currentCustomer by viewModel.currentCustomer.collectAsStateWithLifecycle()
  val currentOwner by viewModel.currentOwner.collectAsStateWithLifecycle()
  val authError by viewModel.authError.collectAsStateWithLifecycle()
  val showOwnerLoginModal by viewModel.showOwnerLoginModal.collectAsStateWithLifecycle()

  val appMode by viewModel.appMode.collectAsStateWithLifecycle()
  val customerTab by viewModel.customerTab.collectAsStateWithLifecycle()
  val shopOwnerTab by viewModel.shopOwnerTab.collectAsStateWithLifecycle()

  val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
  val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
  val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
  val activeOrders by viewModel.activeOrders.collectAsStateWithLifecycle()

  val cartState by viewModel.cartState.collectAsStateWithLifecycle()
  val cartItemsWithProducts by viewModel.cartItemsWithProducts.collectAsStateWithLifecycle()
  val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
  val cartSubtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()

  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val shopProfile by viewModel.shopProfile.collectAsStateWithLifecycle()

  // If not logged in, present the separate Customer & Shop Owner Login portals
  if (userRole == UserRole.LOGGED_OUT) {
    AuthScreen(
      onCustomerLogin = { name, phone, address ->
        viewModel.loginCustomer(name, phone, address)
      },
      onOwnerLogin = { email, pin ->
        viewModel.loginShopOwner(email, pin)
      },
      onQuickCustomerLogin = {
        viewModel.quickDemoCustomerLogin()
      },
      onQuickOwnerLogin = {
        viewModel.quickDemoOwnerLogin()
      },
      errorMessage = authError,
      onClearError = { viewModel.clearAuthError() }
    )
    return
  }

  // Owner PIN Dialog when switching from customer mode or accessing owner features
  if (showOwnerLoginModal) {
    OwnerLoginDialog(
      onDismiss = { viewModel.closeOwnerLoginModal() },
      onLogin = { email, pin -> viewModel.loginShopOwner(email, pin) },
      onQuickOwnerLogin = { viewModel.quickDemoOwnerLogin() },
      errorMessage = authError
    )
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      VMartTopBar(
        currentMode = appMode,
        userRole = userRole,
        customerName = currentCustomer?.name,
        cartItemCount = cartItemCount,
        onCartClick = {
          viewModel.setAppMode(AppMode.CUSTOMER)
          viewModel.setCustomerTab(CustomerTab.CART)
        },
        onRequestOwnerAccess = {
          viewModel.openOwnerLoginModal()
        },
        onSwitchToCustomer = {
          viewModel.setAppMode(AppMode.CUSTOMER)
        },
        onLogout = {
          viewModel.logout()
        },
        onQuickAddItem = if (appMode == AppMode.SHOP_OWNER) {
          {
            viewModel.setShopOwnerTab(ShopOwnerTab.INVENTORY)
          }
        } else null
      )
    },
    bottomBar = {
      VMartBottomBar(
        appMode = appMode,
        customerTab = customerTab,
        shopOwnerTab = shopOwnerTab,
        cartCount = cartItemCount,
        activeOrdersCount = activeOrders.size,
        onSelectCustomerTab = { viewModel.setCustomerTab(it) },
        onSelectOwnerTab = { viewModel.setShopOwnerTab(it) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (appMode == AppMode.CUSTOMER) {
        when (customerTab) {
          CustomerTab.SHOP -> {
            CustomerShopScreen(
              products = filteredProducts,
              cartItemsMap = cartState.items,
              searchQuery = searchQuery,
              selectedCategory = selectedCategory,
              cartItemCount = cartItemCount,
              cartTotal = cartSubtotal,
              onSearchChange = { viewModel.setSearchQuery(it) },
              onCategorySelect = { viewModel.setSelectedCategory(it) },
              onAddToCart = { viewModel.addToCart(it) },
              onDecrementCart = { viewModel.decrementCartItem(it) },
              onViewCart = { viewModel.setCustomerTab(CustomerTab.CART) }
            )
          }
          CustomerTab.CART -> {
            CustomerCartScreen(
              cartItems = cartItemsWithProducts,
              cartState = cartState,
              shopProfile = shopProfile,
              onAddToCart = { viewModel.addToCart(it) },
              onDecrementCart = { viewModel.decrementCartItem(it) },
              onRemoveCartItem = { viewModel.removeCartItem(it) },
              onUpdateCustomerDetails = { name, phone, address, notes, paymentMethod ->
                viewModel.updateCustomerDetails(name, phone, address, notes, paymentMethod)
              },
              onPlaceOrder = { onSuccess ->
                viewModel.placeOrder(onSuccess)
              },
              onBrowseGroceries = { viewModel.setCustomerTab(CustomerTab.SHOP) },
              onViewOrderDetails = {
                viewModel.setCustomerTab(CustomerTab.MY_ORDERS)
              }
            )
          }
          CustomerTab.MY_ORDERS -> {
            CustomerOrdersScreen(
              orders = allOrders,
              shopProfile = shopProfile,
              onStartShopping = { viewModel.setCustomerTab(CustomerTab.SHOP) },
              onUpdateOrder = { viewModel.updateCustomerOrder(it) }
            )
          }
        }
      } else {
        when (shopOwnerTab) {
          ShopOwnerTab.ORDERS_DELIVERIES -> {
            OwnerDeliveriesScreen(
              orders = allOrders,
              onUpdateDeliveryStatus = { orderId, status ->
                viewModel.updateDeliveryStatus(orderId, status)
              },
              onMarkPaymentReceived = { orderId ->
                viewModel.markPaymentReceived(orderId)
              }
            )
          }
          ShopOwnerTab.INVENTORY -> {
            OwnerInventoryScreen(
              products = allProducts,
              onToggleStock = { viewModel.toggleProductStock(it) },
              onAddProduct = { name, category, price, originalPrice, unit, iconEmoji, description ->
                viewModel.addProduct(name, category, price, originalPrice, unit, iconEmoji, description)
              },
              onDeleteProduct = { viewModel.deleteProduct(it) }
            )
          }
          ShopOwnerTab.STORE_STATS -> {
            OwnerRevenueScreen(
              orders = allOrders,
              shopProfile = shopProfile,
              onUpdateShopProfile = { viewModel.updateShopProfile(it) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
