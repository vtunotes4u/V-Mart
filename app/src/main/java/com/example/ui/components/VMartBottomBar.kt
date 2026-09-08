package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppMode
import com.example.ui.CustomerTab
import com.example.ui.ShopOwnerTab

@Composable
fun VMartBottomBar(
    appMode: AppMode,
    customerTab: CustomerTab,
    shopOwnerTab: ShopOwnerTab,
    cartCount: Int,
    activeOrdersCount: Int,
    onSelectCustomerTab: (CustomerTab) -> Unit,
    onSelectOwnerTab: (ShopOwnerTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(color = Color(0xFFF0F1F3), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (appMode == AppMode.CUSTOMER) {
                    BottomNavItem(
                        label = "Shop",
                        selected = customerTab == CustomerTab.SHOP,
                        selectedIcon = Icons.Filled.Store,
                        unselectedIcon = Icons.Outlined.Store,
                        onClick = { onSelectCustomerTab(CustomerTab.SHOP) },
                        testTag = "nav_tab_shop"
                    )

                    BottomNavItem(
                        label = "Cart",
                        selected = customerTab == CustomerTab.CART,
                        selectedIcon = Icons.Filled.ShoppingCart,
                        unselectedIcon = Icons.Outlined.ShoppingCart,
                        onClick = { onSelectCustomerTab(CustomerTab.CART) },
                        badgeCount = cartCount,
                        testTag = "nav_tab_cart"
                    )

                    BottomNavItem(
                        label = "Orders",
                        selected = customerTab == CustomerTab.MY_ORDERS,
                        selectedIcon = Icons.Filled.ReceiptLong,
                        unselectedIcon = Icons.Outlined.ReceiptLong,
                        onClick = { onSelectCustomerTab(CustomerTab.MY_ORDERS) },
                        testTag = "nav_tab_customer_orders"
                    )
                } else {
                    BottomNavItem(
                        label = "Deliveries",
                        selected = shopOwnerTab == ShopOwnerTab.ORDERS_DELIVERIES,
                        selectedIcon = Icons.Filled.LocalShipping,
                        unselectedIcon = Icons.Outlined.LocalShipping,
                        onClick = { onSelectOwnerTab(ShopOwnerTab.ORDERS_DELIVERIES) },
                        badgeCount = activeOrdersCount,
                        testTag = "nav_tab_owner_deliveries"
                    )

                    BottomNavItem(
                        label = "Inventory",
                        selected = shopOwnerTab == ShopOwnerTab.INVENTORY,
                        selectedIcon = Icons.Filled.Inventory,
                        unselectedIcon = Icons.Outlined.Inventory2,
                        onClick = { onSelectOwnerTab(ShopOwnerTab.INVENTORY) },
                        testTag = "nav_tab_owner_inventory"
                    )

                    BottomNavItem(
                        label = "Revenue",
                        selected = shopOwnerTab == ShopOwnerTab.STORE_STATS,
                        selectedIcon = Icons.Filled.Assessment,
                        unselectedIcon = Icons.Outlined.Assessment,
                        onClick = { onSelectOwnerTab(ShopOwnerTab.STORE_STATS) },
                        testTag = "nav_tab_owner_stats"
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit,
    badgeCount: Int = 0,
    testTag: String = ""
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(42.dp, 28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (selected) Color(0xFFF4F5F7) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = badgeCount.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    tint = if (selected) Color.Black else Color(0xFF9CA3AF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = if (selected) Color.Black else Color(0xFF9CA3AF)
        )
    }
}
