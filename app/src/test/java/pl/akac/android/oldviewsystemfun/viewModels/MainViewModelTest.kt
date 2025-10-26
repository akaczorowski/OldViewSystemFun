package pl.akac.android.oldviewsystemfun.viewModels

import app.cash.turbine.test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {


    // beware of limitations https://developer.android.com/kotlin/coroutines/test#creating-dispatchers
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Test
    fun getSideEffect() = runTest{
        // must be created here, if created as a property of test class, then it will fail
        // because will be run before MainDispatcherRule replaces "Main" Dispatcher
        val sut = MainViewModel()
    }


    @Test
    fun `when init finished then list is set`() = runTest{ // uses the same scheduler as test rule, but differentDispatcher, default is StandardTestDispatcher for runTest
        val sut = MainViewModel()

        advanceUntilIdle()
        sut.state.test {
            assertEquals(10, awaitItem().list.size)
        }

    }




    // test examples

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkDispatchersUsesTheSameScheduler() = runTest(UnconfinedTestDispatcher()) {

        println(mainDispatcherRule.testDispatcher.scheduler)
        println(testScheduler)
        // the same :)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testEagerlyEnteringChildCoroutines() = runTest(UnconfinedTestDispatcher()) {
        var entered = false
        val deferred = CompletableDeferred<Unit>()
        var completed = false
        launch {
            entered = true
            deferred.await()
            completed = true
        }
        assertTrue(entered) // `entered = true` already executed.
        assertFalse(completed) // however, the child coroutine then suspended, so it is enqueued.
        deferred.complete(Unit) // resume the coroutine.
        assertTrue(completed) // now the child coroutine is immediately completed.
    }

    @Test
    fun testUnconfinedDispatcher() = runTest {
        val values = mutableListOf<Int>()
        val stateFlow = MutableStateFlow(0)
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            stateFlow.collect {
                values.add(it)
            }
        }
        stateFlow.value = 1
        stateFlow.value = 2
        stateFlow.value = 3
        job.cancel()
        // each assignment will immediately resume the collecting child coroutine,
        // so no values will be skipped.
        assertEquals(listOf(0, 1, 2, 3), values)
    }

}