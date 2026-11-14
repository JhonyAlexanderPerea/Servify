package co.edu.uniquindio.servify.core.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.domain.model.Publication
import com.google.gson.JsonPrimitive
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions

private val ARMENIA: Point = Point.fromLngLat(-75.6811, 4.5339)

@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    center: Point = ARMENIA,
    zoom: Double = 12.0,
    publications: List<Publication> = emptyList(),
    interactive: Boolean = true,
    onMarkerClick: (String) -> Unit = {}
) {
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(zoom)
            center(center)
        }
    }

    val marker = rememberIconImage(
        key = R.drawable.red_marker,
        painter = painterResource(R.drawable.red_marker)
    )

    val publicationAnnotations = remember(publications) {
        publications.mapNotNull { pub ->
            pub.location?.let { loc ->
                PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(loc.longitude, loc.latitude))
                    .withData(JsonPrimitive(pub.id))
            }
        }
    }

    val clusterConfig = remember {
        AnnotationConfig(
            annotationSourceOptions = AnnotationSourceOptions(
                clusterOptions = ClusterOptions(clusterRadius = 60L)
            )
        )
    }

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = mapViewportState
        ) {
            if (publicationAnnotations.isNotEmpty()) {
                PointAnnotationGroup(
                    annotations = publicationAnnotations,
                    annotationConfig = clusterConfig
                ) {
                    iconImage = marker
                    interactionsState.onClicked { annotation ->
                        val pubId = annotation.getData()?.asString
                        if (pubId != null) {
                            onMarkerClick(pubId)
                        }
                        true
                    }
                }
            }
        }

        if (!interactive) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {}
            )
        }
    }
}
