package org.chornobyl.hamdash.ui.map

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.chornobyl.hamdash.domain.RepeaterMode
import org.chornobyl.hamdash.location.LOCATION_PERMISSIONS
import org.chornobyl.hamdash.ui.repeaters.RepeaterDetailContent
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private const val CHORNOBYL_LAT = 51.389
private const val CHORNOBYL_LON = 30.099

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE),
        )
        Configuration.getInstance().userAgentValue = context.packageName
        if (viewModel.hasLocationPermission()) viewModel.refreshMyLocation()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        if (results.values.any { it }) {
            viewModel.refreshMyLocation()
        }
    }

    val mapView = remember { MapView(context) }
    DisposableEffect(Unit) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(9.0)
        mapView.controller.setCenter(GeoPoint(CHORNOBYL_LAT, CHORNOBYL_LON))
        onDispose { mapView.onDetach() }
    }

    LaunchedEffect(state.repeaters, state.myLocation) {
        mapView.overlays.clear()
        state.repeaters.forEach { repeater ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(repeater.latitude, repeater.longitude)
            marker.title = "${repeater.callsign} — ${RepeaterMode.fromRaw(repeater.mode).label}"
            marker.snippet = "${repeater.rxFrequencyMhz} MHz"
            marker.setOnMarkerClickListener { _, _ ->
                viewModel.selectRepeater(repeater.id)
                true
            }
            mapView.overlays.add(marker)
        }
        state.myLocation?.let { loc ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(loc.latitude, loc.longitude)
            marker.title = "My location"
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = state.selectedMode == null,
                    onClick = { viewModel.selectMode(null) },
                    label = { Text("All") },
                )
            }
            items(RepeaterMode.entries) { mode ->
                FilterChip(
                    selected = state.selectedMode == mode,
                    onClick = { viewModel.selectMode(mode) },
                    label = { Text(mode.label) },
                )
            }
        }

        FloatingActionButton(
            onClick = {
                if (viewModel.hasLocationPermission()) {
                    viewModel.refreshMyLocation()
                    state.myLocation?.let { mapView.controller.animateTo(GeoPoint(it.latitude, it.longitude)) }
                } else {
                    permissionLauncher.launch(LOCATION_PERMISSIONS)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = "My location")
        }
    }

    val selected = state.repeaters.firstOrNull { it.id == state.selectedRepeaterId }
    if (selected != null) {
        ModalBottomSheet(onDismissRequest = { viewModel.selectRepeater(null) }) {
            RepeaterDetailContent(repeater = selected)
        }
    }
}
