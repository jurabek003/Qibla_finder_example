package uz.turgunboyevjurabek.qiblafinderexample

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    private val _qiblaState = MutableStateFlow(QiblaState())
    val qiblaState = _qiblaState.asStateFlow()

    // Other properties and methods...

    fun updateQiblaDirection(newDirection: Float) {
        _qiblaState.update {
            it.copy(
                qiblaDirection = newDirection
            )
        }
        Log.d("ViewModel direction", "Updating Qibla Direction to $newDirection")
    }

    fun updateCurrentDirection(newDirection: Float) {
        _qiblaState.update {
            it.copy(
                currentDirection = newDirection
            )
        }
        Log.d("ViewModel direction", "Updating currentDirection to $newDirection")
    }

}