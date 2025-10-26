package pl.akac.android.oldviewsystemfun.viewModels

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// GUIDES:
// https://developer.android.com/kotlin/flow/test
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesTests {

    // beware of limitations https://developer.android.com/kotlin/coroutines/test#creating-dispatchers
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkDispatchersUsesTheSameScheduler() = runTest(UnconfinedTestDispatcher()) {

        println(mainDispatcherRule.testDispatcher.scheduler)
        println(testScheduler)
        // the same :)
    }


    @Test
    fun testExampleBackgroundJob() = runTest {
        val channel = Channel<Int>()
        backgroundScope.launch {
            var i = 0
            while (true) {
                channel.send(i++)
            }
        }
        repeat(100) {
            assertEquals(it, channel.receive())
        }
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

    @Test
    fun continuouslyCollect() = runTest {
        val dataSource = FakeDataSource()
        val repository = Repository(dataSource)

        val values = mutableListOf<Int>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { // even when testScheduler is not passed it will use the same on as runTest
            repository.scores().toList(values)
        }

        dataSource.emit(1)
        assertEquals(10, values[0]) // Assert on the list contents

        dataSource.emit(2)
        dataSource.emit(3)
        assertEquals(30, values[2])

        assertEquals(3, values.size) // Assert the number of items collected
    }

}





class Repository(private val dataSource: DataSource) {
    fun scores(): Flow<Int> {
        return dataSource.counts().map { it * 10 }
    }
}

class FakeDataSource : DataSource {
    private val flow = MutableSharedFlow<Int>()
    suspend fun emit(value: Int) = flow.emit(value)
    override fun counts(): Flow<Int> = flow
}

interface DataSource{
    fun counts(): Flow<Int>
}