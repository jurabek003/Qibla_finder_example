package uz.turgunboyevjurabek.qiblafinderexample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import uz.turgunboyevjurabek.qiblafinderexample.service.CompassSensorManager
import uz.turgunboyevjurabek.qiblafinderexample.service.MyLocationManager
import uz.turgunboyevjurabek.qiblafinderexample.service.PermissionsManager
import uz.turgunboyevjurabek.qiblafinderexample.ui.theme.QiblaFinderExampleTheme
import uz.turgunboyevjurabek.qiblafinderexample.utils.calculateQiblaDirection
import uz.turgunboyevjurabek.qiblafinderexample.vm.MainViewModel
import javax.inject.Inject

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