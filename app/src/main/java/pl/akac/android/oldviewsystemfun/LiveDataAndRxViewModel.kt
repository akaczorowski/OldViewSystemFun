package pl.akac.android.oldviewsystemfun

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class LiveDataAndRxViewModel: ViewModel() {


    // For state you could also use BehaviourSubject
    // OR BehaviourRelay If you don’t want to handle terminal events (like onError or onComplete)
    // which will make BehaviourSubject unusable
    // BehaviorRelay<Object> relay = BehaviorRelay.createDefault("default");
    // https://github.com/JakeWharton/RxRelay
    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    // OR PublishRelay If you don’t want to handle terminal events (like onError or onComplete)
    // which will make PublishSubject unusable
    private val _sideEffect = PublishSubject.create<SideEffect>()
    val sideEffect: Observable<SideEffect> = _sideEffect

    init {

        _state.value = UiState()
        viewModelScope.launch {

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
                    Item(id = 6, title = "item with data in it 6"),
                    Item(id = 7, title = "item with data in it 7"),
                    Item(id = 8, title = "item with data in it 8"),
                    Item(id = 9, title = "item with data in it 9"),
                    Item(id = 10, title = "item with data in it 10"),
                )
            )
        }
    }

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
                    _sideEffect.onNext(SideEffect.NotifyUserNewItemAdded)
                }
            }

            is Action.ItemClick -> viewModelScope.launch {
                _sideEffect.onNext(SideEffect.ItemClicked(action.data))
            }
        }
    }

}