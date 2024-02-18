package com.example.findfriendsapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.findfriendsapp.ui.components.MapsScreen
import com.example.findfriendsapp.ui.components.RegistrationScreen
import com.example.findfriendsapp.ui.theme.FindFriendsAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    // FusedLocationProvider for getting user's location data
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Later initialize firebase database reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize firebase database reference
        database = Firebase.database.reference

        // Create path to store all users' locations on the database
        val usersLocations = database.child("usersLocations")

        // Get device unique id
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // Check location permissions
        val locationPermissionsAlreadyGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Create location requesting launcher
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {}

        // Request location permissions
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            FindFriendsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    // Initialize fusedLocationProvider
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                    var isRegistered by remember {
                        mutableStateOf(false)
                    }

                    var name: String by rememberSaveable {
                        mutableStateOf("")
                    }
                    // Create observable for composables
                    var userLocation = UserLocationDTO(
                        deviceId = deviceId, location = LatLngWrapper(0.00, 0.00), name = name
                    )

                    var usersLocationsData =
                        remember { mutableStateListOf<UserLocationDTO>(userLocation) }

                    // Setup for getting continuous user's location data
                    val locationRequest =
                        LocationRequest.create().setInterval(1000).setFastestInterval(500)
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            super.onLocationResult(result)
                            for (location in result.locations) {
                                userLocation = UserLocationDTO(
                                    location = LatLngWrapper(location.latitude, location.longitude),
                                    name = name,
                                    deviceId = deviceId
                                )
                                usersLocations.child(deviceId).setValue(userLocation)
                            }
                        }
                    }

                    // Get user's location and assign it to userLocation
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest, locationCallback, Looper.getMainLooper()
                    )

                    val valueEventListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            usersLocationsData.clear()
                            for (childSnapshot in snapshot.children) {
                                val data = childSnapshot.getValue(UserLocationDTO::class.java)
                                data?.let { usersLocationsData.add(it) }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    }

                    // Listen for changes in the usersLocations path
                    usersLocations.addValueEventListener(valueEventListener)

                    AppNavigation(
                        isGranted = locationPermissionsAlreadyGranted,
                        usersLocations = usersLocationsData,
                        name = name,
                        onNameChange = { newName -> name = newName },
                        isRegistered = isRegistered
                    )
                }
            }
        }
    }
}


fun NavHostController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.currentDestination?.id
                ?: this@navigateSingleTopTo.graph.startDestinationId
        ) {
            saveState = true
            inclusive = true
        }
        restoreState = true
        launchSingleTop = true
    }
}


@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    isGranted: Boolean,
    usersLocations: MutableList<UserLocationDTO>,
    name: String,
    onNameChange: (String) -> Unit,
    isRegistered: Boolean
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = if (isRegistered) Maps.routeWithArgs else Registration.route
    ) {
        composable(Registration.route) {
            RegistrationScreen(name = name,
                onNameChange = onNameChange,
                onClickNext = { username -> navController.navigateSingleTopTo("${Maps.route}/${username}") })
        }

        composable(
            Maps.routeWithArgs, arguments = Maps.navArgs
        ) {
            if (isGranted) {
                val username = it.arguments?.getString(Maps.args)!!
                MapsScreen(usersLocations = usersLocations)
            } else {
                navController.navigateSingleTopTo(Registration.route)
            }
        }
    }
}
