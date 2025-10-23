package pl.akac.android.oldviewsystemfun

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class MainViewModel : ViewModel() {

    private val _sideEffect = Channel<SideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private val _state = MutableStateFlow<UiState>(UiState())
    val state = _state.asStateFlow()

}

data class UiState(
    val list: List<String> = emptyList()
)

sealed interface SideEffect{
    data object ButtonClick
}
