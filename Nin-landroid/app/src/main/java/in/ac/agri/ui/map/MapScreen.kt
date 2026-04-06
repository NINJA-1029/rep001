package in.ac.agri.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import in.ac.agri.network.ParcelDetails
import org.maplibre.gl.maps.MapView
import org.maplibre.gl.maps.Style
import org.maplibre.gl.style.layers.FillLayer
import org.maplibre.gl.style.layers.PropertyFactory
import org.maplibre.gl.style.layers.RasterLayer
import org.maplibre.gl.style.sources.GeoJsonSource
import org.maplibre.gl.style.sources.RasterSource
import org.maplibre.gl.style.sources.TileSet

@Composable
fun MapScreen(viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val parcelDetails by viewModel.parcelDetails.collectAsState()
    
    var showNdvi by remember { mutableStateOf(false) }
    var showOrtho by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            if (parcelDetails is ParcelDetailsState.Success) {
                ParcelDetailsSheet((parcelDetails as ParcelDetailsState.Success).details)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        getMapAsync { map ->
                            map.setStyle(Style.SATELLITE)
                        }
                    }
                },
                update = { mapView ->
                    mapView.getMapAsync { map ->
                        val state = parcelDetails
                        if (state is ParcelDetailsState.Success) {
                            val details = state.details
                            map.getStyle { style ->
                                // Boundary
                                updateGeoJsonLayer(style, "parcel-boundary", details.boundary)
                                
                                // Orthomosaic
                                updateRasterLayer(style, "ortho", details.orthomosaicUrl, showOrtho)
                                
                                // NDVI
                                updateRasterLayer(style, "ndvi", details.ndviUrl, showNdvi, 0.6f)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Layer Controls
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = showOrtho, onCheckedChange = { showOrtho = it })
                    Text("Orthomosaic", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = showNdvi, onCheckedChange = { showNdvi = it })
                    Text("NDVI", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private fun updateGeoJsonLayer(style: Style, id: String, geoJson: String) {
    val sourceId = "$id-source"
    val layerId = "$id-layer"
    
    if (style.getSource(sourceId) == null) {
        style.addSource(GeoJsonSource(sourceId, geoJson))
        style.addLayer(FillLayer(layerId, sourceId).apply {
            setProperties(
                PropertyFactory.fillColor("rgba(0, 255, 0, 0.3)"),
                PropertyFactory.fillOutlineColor("green")
            )
        })
    } else {
        (style.getSource(sourceId) as? GeoJsonSource)?.setGeoJson(geoJson)
    }
}

private fun updateRasterLayer(style: Style, id: String, url: String, visible: Boolean, opacity: Float = 1.0f) {
    val sourceId = "$id-source"
    val layerId = "$id-layer"
    
    if (visible) {
        if (style.getSource(sourceId) == null) {
            val tileSet = TileSet("2.1.0", url)
            style.addSource(RasterSource(sourceId, tileSet, 256))
            style.addLayer(RasterLayer(layerId, sourceId).apply {
                setProperties(PropertyFactory.rasterOpacity(opacity))
            })
        }
    } else {
        style.removeLayer(layerId)
        style.removeSource(sourceId)
    }
}

@Composable
fun ParcelDetailsSheet(details: ParcelDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Health Score: ${details.healthScore.score.toInt()}% (${details.healthScore.status})",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Confidence: ${(details.healthScore.confidence * 100).toInt()}%",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Canopy Count", style = MaterialTheme.typography.labelMedium)
                    Text(text = "${details.canopyData.count}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "${(details.canopyData.confidence * 100).toInt()}% conf", style = MaterialTheme.typography.bodySmall)
                }
                Column {
                    Text(text = "Valuation", style = MaterialTheme.typography.labelMedium)
                    Text(text = "$${details.valuation.mid.toInt()}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Range: $${details.valuation.low.toInt()} - $${details.valuation.high.toInt()}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
