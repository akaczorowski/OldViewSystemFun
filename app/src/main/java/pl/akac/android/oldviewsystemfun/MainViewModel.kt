package pl.akac.android.oldviewsystemfun

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Main.immediate){

            }
            delay(2000)
            _state.value = _state.value.copy(
                list = listOf(
                    Item(id = 1, title = "item with data in it 1"),
                    Item(id = 1, title = "item with data in it 2"),
                    Item(id = 1, title = "item with data in it 3"),
                    Item(id = 1, title = "item with data in it 4"),
                    Item(id = 1, title = "item with data in it 5"),
                    Item(id = 1, title = "item with data in it 6"),
                )
            )
        }
    }

    private val _sideEffect = Channel<SideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private val _state = MutableStateFlow<UiState>(UiState())
    val state = _state.asStateFlow()

}

data class UiState(
    val list: List<Item> = emptyList()
)

data class Item(
    val id: Int,
    val title: String,
)

sealed interface SideEffect{
    data object ButtonClick
}
