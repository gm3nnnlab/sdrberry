package org.chornobyl.hamdash.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard : Destination("dashboard", "Dashboard", Icons.Filled.Dashboard)
    data object Map : Destination("map", "Map", Icons.Filled.Map)
    data object Repeaters : Destination("repeaters", "Repeaters", Icons.Filled.Router)
    data object Dmr : Destination("dmr", "DMR", Icons.Filled.Radio)
    data object More : Destination("more", "More", Icons.Filled.MoreHoriz)
}

val bottomNavDestinations = listOf(
    Destination.Dashboard,
    Destination.Map,
    Destination.Repeaters,
    Destination.Dmr,
    Destination.More,
)

const val ROUTE_BANDS = "bands"
const val ROUTE_DIGITAL = "digital"
const val ROUTE_PROPAGATION = "propagation"
const val ROUTE_SETTINGS = "settings"
const val ROUTE_ABOUT = "about"
const val ROUTE_REPEATER_DETAIL = "repeater_detail/{repeaterId}"

fun repeaterDetailRoute(repeaterId: String) = "repeater_detail/$repeaterId"
