package org.chornobyl.hamdash.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.chornobyl.hamdash.AppContainer
import org.chornobyl.hamdash.ui.about.AboutScreen
import org.chornobyl.hamdash.ui.bands.BandsScreen
import org.chornobyl.hamdash.ui.bands.BandsViewModel
import org.chornobyl.hamdash.ui.common.HamDashViewModelFactory
import org.chornobyl.hamdash.ui.dashboard.DashboardScreen
import org.chornobyl.hamdash.ui.dashboard.DashboardViewModel
import org.chornobyl.hamdash.ui.digital.DigitalScreen
import org.chornobyl.hamdash.ui.digital.DigitalViewModel
import org.chornobyl.hamdash.ui.dmr.DmrScreen
import org.chornobyl.hamdash.ui.dmr.DmrViewModel
import org.chornobyl.hamdash.ui.map.MapScreen
import org.chornobyl.hamdash.ui.map.MapViewModel
import org.chornobyl.hamdash.ui.more.MoreScreen
import org.chornobyl.hamdash.ui.nav.Destination
import org.chornobyl.hamdash.ui.nav.ROUTE_ABOUT
import org.chornobyl.hamdash.ui.nav.ROUTE_BANDS
import org.chornobyl.hamdash.ui.nav.ROUTE_DIGITAL
import org.chornobyl.hamdash.ui.nav.ROUTE_PROPAGATION
import org.chornobyl.hamdash.ui.nav.ROUTE_SETTINGS
import org.chornobyl.hamdash.ui.nav.bottomNavDestinations
import org.chornobyl.hamdash.ui.nav.repeaterDetailRoute
import org.chornobyl.hamdash.ui.propagation.PropagationScreen
import org.chornobyl.hamdash.ui.propagation.PropagationViewModel
import org.chornobyl.hamdash.ui.repeaters.RepeaterDetailScreen
import org.chornobyl.hamdash.ui.repeaters.RepeatersScreen
import org.chornobyl.hamdash.ui.repeaters.RepeatersViewModel
import org.chornobyl.hamdash.ui.settings.SettingsScreen
import org.chornobyl.hamdash.ui.settings.SettingsViewModel

@Composable
fun HamDashApp(container: AppContainer) {
    val navController = rememberNavController()
    val factory = HamDashViewModelFactory(container)

    Scaffold(
        bottomBar = { HamDashBottomBar(navController) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Dashboard.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Dashboard.route) {
                val vm: DashboardViewModel = viewModel(factory = factory)
                DashboardScreen(
                    viewModel = vm,
                    onOpenMap = { navController.navigate(Destination.Map.route) },
                    onOpenRepeaters = { navController.navigate(Destination.Repeaters.route) },
                    onOpenDmr = { navController.navigate(Destination.Dmr.route) },
                    onOpenPropagation = { navController.navigate(ROUTE_PROPAGATION) },
                )
            }
            composable(Destination.Map.route) {
                val vm: MapViewModel = viewModel(factory = factory)
                MapScreen(viewModel = vm)
            }
            composable(Destination.Repeaters.route) {
                val vm: RepeatersViewModel = viewModel(factory = factory)
                RepeatersScreen(
                    viewModel = vm,
                    onOpenDetail = { id -> navController.navigate(repeaterDetailRoute(id)) },
                )
            }
            composable(
                route = "repeater_detail/{repeaterId}",
                arguments = listOf(navArgument("repeaterId") {}),
            ) { backStackEntry ->
                val vm: RepeatersViewModel = viewModel(factory = factory)
                val id = backStackEntry.arguments?.getString("repeaterId").orEmpty()
                RepeaterDetailScreen(viewModel = vm, repeaterId = id)
            }
            composable(Destination.Dmr.route) {
                val vm: DmrViewModel = viewModel(factory = factory)
                DmrScreen(viewModel = vm)
            }
            composable(Destination.More.route) {
                MoreScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(ROUTE_BANDS) {
                val vm: BandsViewModel = viewModel(factory = factory)
                BandsScreen(viewModel = vm)
            }
            composable(ROUTE_DIGITAL) {
                val vm: DigitalViewModel = viewModel(factory = factory)
                DigitalScreen(viewModel = vm)
            }
            composable(ROUTE_PROPAGATION) {
                val vm: PropagationViewModel = viewModel(factory = factory)
                PropagationScreen(viewModel = vm)
            }
            composable(ROUTE_SETTINGS) {
                val vm: SettingsViewModel = viewModel(factory = factory)
                SettingsScreen(viewModel = vm)
            }
            composable(ROUTE_ABOUT) {
                AboutScreen()
            }
        }
    }
}

@Composable
private fun HamDashBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        bottomNavDestinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
            )
        }
    }
}
