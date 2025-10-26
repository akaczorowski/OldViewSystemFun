package pl.akac.android.oldviewsystemfun.viewModels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Uses UnconfinedTestDispatcher fot setting "Main" Dispatcher
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

// #################################### for junit5
/**
 * Usage
 * @ExtendWith(TestCoroutineExtension::class)
 * class SomeViewModelTest { ...
 */
//@OptIn(ExperimentalCoroutinesApi::class)
//open class TestCoroutineExtension(
//    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
//) : BeforeEachCallback, AfterEachCallback {
//    override fun beforeEach(context: ExtensionContext?) {
//        Dispatchers.setMain(dispatcher)
//    }
//
//    override fun afterEach(context: ExtensionContext?) {
//        Dispatchers.resetMain()
//    }
//}
//
//class StandardTestCoroutineExtension : TestCoroutineExtension(dispatcher = StandardTestDispatcher())
