package com.example

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable object Dashboard
@Serializable object AddExpense
@Serializable object Transactions
@Serializable object Calendar
@Serializable object Statistics
@Serializable object SettingsScreenDestination

@Composable
fun MainApp(viewModel: MainViewModel) {
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsStateWithLifecycle()
    val isAppUnlocked by viewModel.isAppUnlocked.collectAsStateWithLifecycle()
    
    if (isAppLockEnabled && !isAppUnlocked) {
        AppLockScreen(viewModel)
        return
    }

    val navController = rememberNavController()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (currentDestination?.route != AddExpense::class.qualifiedName) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Dashboard::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(Dashboard) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (currentDestination?.hierarchy?.any { it.route == Dashboard::class.qualifiedName } == true) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                    
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Transactions::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(Transactions) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (currentDestination?.hierarchy?.any { it.route == Transactions::class.qualifiedName } == true) Icons.AutoMirrored.Filled.List else Icons.AutoMirrored.Outlined.List,
                                contentDescription = "Transactions"
                            )
                        },
                        label = { Text("History") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                    
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Statistics::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(Statistics) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (currentDestination?.hierarchy?.any { it.route == Statistics::class.qualifiedName } == true) Icons.Filled.Analytics else Icons.Outlined.Analytics,
                                contentDescription = "Statistics"
                            )
                        },
                        label = { Text("Statistics") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                    
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == SettingsScreenDestination::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(SettingsScreenDestination) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (currentDestination?.hierarchy?.any { it.route == SettingsScreenDestination::class.qualifiedName } == true) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.route != AddExpense::class.qualifiedName) {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(targetValue = if (isPressed) 0.9f else 1f, animationSpec = tween(150))
                
                FloatingActionButton(
                    onClick = { navController.navigate(AddExpense) },
                    interactionSource = interactionSource,
                    modifier = Modifier.scale(scale),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Expense", modifier = Modifier.size(32.dp))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Dashboard,
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300))
            }
        ) {
            composable<Dashboard> { DashboardScreen(viewModel, onNavigateToCalendar = { navController.navigate(Calendar) }) }
            composable<Transactions> { TransactionsScreen(viewModel) }
            composable<Calendar> { CalendarScreen(viewModel) }
            composable<Statistics> { StatisticsScreen(viewModel) }
            composable<SettingsScreenDestination> { SettingsScreen(viewModel) }
            composable<AddExpense> { 
                AddExpenseScreen(viewModel, onBack = { navController.popBackStack() }) 
            }
        }
    }
}
