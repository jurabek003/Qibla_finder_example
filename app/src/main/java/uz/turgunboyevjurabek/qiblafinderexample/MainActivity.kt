package uz.turgunboyevjurabek.qiblafinderexample

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import android.Manifest
import android.location.LocationManager
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import uz.turgunboyevjurabek.qiblafinderexample.service.CompassSensorManager
import uz.turgunboyevjurabek.qiblafinderexample.service.MyLocationManager
import uz.turgunboyevjurabek.qiblafinderexample.service.PermissionsManager
import uz.turgunboyevjurabek.qiblafinderexample.ui.theme.QiblaFinderExampleTheme
import uz.turgunboyevjurabek.qiblafinderexample.utils.calculateQiblaDirection
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var myLocationManager: MyLocationManager

    @Inject
    lateinit var compassSensorManager: CompassSensorManager

    private lateinit var permissionsManager: PermissionsManager
    private val viewModel by viewModels<MainViewModel>()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permissionsManager = PermissionsManager(this)
        permissionsManager.onPermissionGranted = {
            myLocationManager.getLastKnownLocation()
        }
        permissionsManager.checkAndRequestLocationPermission()

        myLocationManager.onLocationReceived = { location ->
            val qiblaDirection = calculateQiblaDirection(location.latitude, location.longitude).toFloat()
            Log.d("MainActivity Qibla", "New Qibla Direction: $qiblaDirection")
            viewModel.updateQiblaDirection(qiblaDirection)
        }

        compassSensorManager.onDirectionChanged = { direction ->
            Log.d("MainActivity Current", "New Current Direction: $direction")
            viewModel.updateCurrentDirection(direction)
        }


        setContent {
            QiblaFinderExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                       val state = viewModel.qiblaState.collectAsState()
                       QiblaScreen(
                           modifier = Modifier.padding(innerPadding),
                           qiblaDirection = state.value.qiblaDirection,
                           currentDirection = state.value.currentDirection
                       )
                }
            }
        }
        compassSensorManager.registerListeners()
    }
}