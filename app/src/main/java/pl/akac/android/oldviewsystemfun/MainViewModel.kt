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
import kotlin.random.Random

class MainViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Main.immediate) {

            }
            delay(2000)
            _state.value = _state.value.copy(
                list = listOf(
                    Item(id = 1, title = "item with data in it 1"),
                    Item(id = 2, title = "item with data in it 2"),
                    Item(id = 3, title = "item with data in it 3"),
                    Item(id = 4, title = "item with data in it 4"),
                    Item(id = 5, title = "item with data in it 5"),
                    Item(id = 6, title = "item with data in it 6"),
                )
            )

            delay(2000)

            _state.value = _state.value.copy(
                list = listOf(
                    Item(id = 5, title = "item with data in it 5"),
                    Item(id = 1, title = "item with data in it 1"),
                    Item(id = 2, title = "item with data in it 2"),
                    Item(id = 4, title = "item with data in it 4"),
                    Item(id = 3, title = "item with data in it 3"),
                    Item(id = 6, title = "item with data in it 6"),
                )
            )

            delay(2000)
            _state.value = _state.value.copy(
                list = listOf(
                    Item(id = 1, title = "item with data in it 1"),
                    Item(id = 2, title = "item with data in it 2"),
                    Item(id = 3, title = "item with data in it 3"),
                    Item(id = 4, title = "item with data in it 4"),
                    Item(id = 5, title = "item with data in it 5"),
                    Item(id = 6, title = "item with data in it 7"),
                    Item(id = 7, title = "item with data in it 8"),
                    Item(id = 8, title = "item with data in it 9"),
                    Item(id = 9, title = "item with data in it 10"),
                    Item(id = 10, title = "item with data in it 11"),
                )
            )
        }
    }

    private val _sideEffect = Channel<SideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private val _state = MutableStateFlow<UiState>(UiState())
    val state = _state.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            Action.AddMoreItems -> {
                val id = Random.nextInt(1000)
                _state.value = _state.value.copy(
                    list = listOf(
                        Item(id = id, title = "new item id: $id"),
                    ) + _state.value.list
                )

                viewModelScope.launch {
                    _sideEffect.send(SideEffect.NotifyUserNewItemAdded)
                }
            }

            is Action.ItemClick -> viewModelScope.launch {
                _sideEffect.send(SideEffect.ItemClicked(action.data))
            }
        }
    }

}

data class UiState(
    val list: List<Item> = emptyList()
)

data class Item(
    val id: Int,
    val title: String,
)

sealed interface SideEffect {
    data class ItemClicked(val data: Item): SideEffect

    data object NotifyUserNewItemAdded : SideEffect
}

sealed interface Action {
    data object AddMoreItems : Action
    data class ItemClick(val data: Item) : Action
}
