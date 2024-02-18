package com.example.findfriendsapp

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class UserLocationDTO(
    var location: LatLngWrapper? = LatLngWrapper(),
    var name: String? = "User_bot_123",
    var deviceId: String? = "UnknownDevice-id"
) : Serializable

data class LatLngWrapper(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}


