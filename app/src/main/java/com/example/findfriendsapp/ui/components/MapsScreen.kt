package com.example.findfriendsapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.findfriendsapp.UserLocationDTO
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapsScreen(
    modifier: Modifier = Modifier, usersLocations: MutableList<UserLocationDTO>
) {
    val cameraPositionPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(usersLocations[0].location?.toLatLng()!!, 10f)
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = modifier, cameraPositionState = cameraPositionPosition
        ) {
            for (userLocation in usersLocations) {
                Marker(
                    state = MarkerState(position = userLocation.location?.toLatLng()!!),
                    title = userLocation.name,
                    snippet = "Marker in Baku"
                )
            }
        }
    }
}
