package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.OurDreamApp
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoveScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SavingsScreen
import com.example.ui.theme.MyanmarRedAccent
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.LoveViewModel
import com.example.ui.viewmodel.ProfileViewModel
import com.example.ui.viewmodel.SettingsViewModel
import com.example.ui.viewmodel.SavingsViewModel
import com.example.ui.viewmodel.ViewModelFactory

sealed class Screen(
  val route: String,
  val title: String,
  val burmeseLabel: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
) {
  object Home : Screen(
    route = "home",
    title = "Home",
    burmeseLabel = "ပင်မ",
    selectedIcon = Icons.Filled.Home,
    unselectedIcon = Icons.Outlined.Home,
    testTag = "nav_item_home"
  )

  object Finance : Screen(
    route = "finance",
    title = "Finance",
    burmeseLabel = "ငွေစာရင်း",
    selectedIcon = Icons.Filled.AccountBalanceWallet,
    unselectedIcon = Icons.Outlined.AccountBalanceWallet,
    testTag = "nav_item_finance"
  )

  object Savings : Screen(
    route = "savings",
    title = "Savings",
    burmeseLabel = "စုငွေ",
    selectedIcon = Icons.Filled.Savings,
    unselectedIcon = Icons.Outlined.Savings,
    testTag = "nav_item_savings"
  )

  object Love : Screen(
    route = "love",
    title = "Our Love",
    burmeseLabel = "အချစ်",
    selectedIcon = Icons.Filled.Favorite,
    unselectedIcon = Icons.Outlined.FavoriteBorder,
    testTag = "nav_item_love"
  )

  object Profile : Screen(
    route = "profile",
    title = "Profile",
    burmeseLabel = "ကိုယ်ရေး",
    selectedIcon = Icons.Filled.Person,
    unselectedIcon = Icons.Outlined.Person,
    testTag = "nav_item_profile"
  )

  object Settings : Screen(
    route = "settings",
    title = "Settings",
    burmeseLabel = "ဆက်တင်",
    selectedIcon = Icons.Filled.Home,
    unselectedIcon = Icons.Outlined.Home,
    testTag = "nav_item_settings"
  )
}

val bottomNavScreens = listOf(
  Screen.Home,
  Screen.Finance,
  Screen.Savings,
  Screen.Love,
  Screen.Profile
)

@Composable
fun OurDreamMainApp(app: OurDreamApp) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val showBottomBar = bottomNavScreens.any { it.route == currentRoute }

  val factory = ViewModelFactory(app)
  val homeViewModel: HomeViewModel = viewModel(factory = factory)
  val financeViewModel: FinanceViewModel = viewModel(factory = factory)
  val savingsViewModel: SavingsViewModel = viewModel(factory = factory)
  val loveViewModel: LoveViewModel = viewModel(factory = factory)
  val profileViewModel: ProfileViewModel = viewModel(factory = factory)
  val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

  Scaffold(
    bottomBar = {
      if (showBottomBar) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 8.dp
        ) {
          bottomNavScreens.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
              selected = isSelected,
              onClick = {
                if (currentRoute != screen.route) {
                  navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              },
              icon = {
                Icon(
                  imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                  contentDescription = screen.title
                )
              },
              label = {
                Text(
                  text = screen.title,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MyanmarRedAccent,
                selectedTextColor = MyanmarRedAccent,
                indicatorColor = MyanmarRedAccent.copy(alpha = 0.12f)
              ),
              modifier = Modifier.testTag(screen.testTag)
            )
          }
        }
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { paddingValues ->
    NavHost(
      navController = navController,
      startDestination = Screen.Home.route,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      composable(Screen.Home.route) {
        HomeScreen(
          viewModel = homeViewModel,
          onNavigateToFinance = { navController.navigate(Screen.Finance.route) },
          onNavigateToSavings = { navController.navigate(Screen.Savings.route) },
          onNavigateToLove = { navController.navigate(Screen.Love.route) },
          onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
        )
      }

      composable(Screen.Finance.route) {
        FinanceScreen(
          viewModel = financeViewModel
        )
      }

      composable(Screen.Savings.route) {
        SavingsScreen(
          viewModel = savingsViewModel
        )
      }

      composable(Screen.Love.route) {
        LoveScreen(
          viewModel = loveViewModel
        )
      }

      composable(Screen.Profile.route) {
        ProfileScreen(
          viewModel = profileViewModel,
          onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
        )
      }

      composable(Screen.Settings.route) {
        SettingsScreen(
          viewModel = settingsViewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }
    }
  }
}
