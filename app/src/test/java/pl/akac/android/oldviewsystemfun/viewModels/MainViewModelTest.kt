package pl.akac.android.oldviewsystemfun.viewModels

import app.cash.turbine.test
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


//GUIDES:
// https://developer.android.com/kotlin/coroutines/test#inject-scope
// https://developer.android.com/kotlin/flow/test
class MainViewModelTest {


    // beware of limitations https://developer.android.com/kotlin/coroutines/test#creating-dispatchers
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Test
    fun getSideEffect() = runTest {
        // must be created here, if created as a property of test class, then it will fail
        // because will be run before MainDispatcherRule replaces "Main" Dispatcher
        val sut = MainViewModel()
    }


    @Test
    fun `when init finished then list is set`() =
        runTest { // uses the same scheduler as test rule, but differentDispatcher, default is StandardTestDispatcher for runTest
            val sut = MainViewModel()

            advanceUntilIdle() // StandardTestDispatcher is used so we need this to run scheduled coroutines in MainViewModel
            sut.state.test {
                assertEquals(10, awaitItem().list.size)
            }

        }

    @Test
    fun `when init finished then list is set V2`() =
        runTest(UnconfinedTestDispatcher()) { // uses the same scheduler as test rule

            val sut = MainViewModel()

            sut.state.test {
                assertEquals(10, awaitItem().list.size)
            }

        }


}