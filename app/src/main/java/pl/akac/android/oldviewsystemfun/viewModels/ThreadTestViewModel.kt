package pl.akac.android.oldviewsystemfun.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ThreadTestViewModel : ViewModel() {

    private val counter = Counter5()

    init {

        viewModelScope.launch {
            Log.d("DDD", "working")

            repeat(10) {
                val deferred1 = async(Dispatchers.Default) {
                    repeat(1000) {
                        counter.increment()
                    }
                }

                val deferred2 = async(Dispatchers.Default) {
                    repeat(1000) {
                        counter.increment()
                    }
                }

                deferred1.await()
                deferred2.await()

                Log.d("DDD", "counter = ${counter.getCount()}")
                counter.reset()
            }

        }
    }
}

class Counter {

    @Volatile
    private var count = 0

    fun increment() {
        synchronized(this) {
            count++
        }
    }

    fun reset() {
        count = 0
    }

    fun getCount(): Int = count
}

class Counter2 {

    @Volatile
    private var count = 0

    @Synchronized
    fun increment() {
        count++
    }

    fun reset() {
        count = 0
    }

    fun getCount(): Int = count
}

// will not work correctly because increment operation is not atomic, and takes a while,
// so the second thread can read the same value as the thread that is about to increment it
class Counter3 {

    @Volatile
    private var count = 0

    fun increment() {
        count++
    }

    fun reset() {
        count = 0
    }

    fun getCount(): Int = count
}

class Counter4 {

    private val lock = ReentrantLock()

    @Volatile
    private var count = 0

    fun increment() {
        lock.withLock {
            count++
        }
    }

    fun reset() {
        count = 0
    }

    fun getCount(): Int = count
}

class Counter5 {

    private val lock = Semaphore(1)

    @Volatile
    private var count = 0

    suspend fun increment() {
        lock.withPermit {
            count++
        }
    }

    fun reset() {
        count = 0
    }

    fun getCount(): Int = count
}

//class Counter6 {
//
//    private val lock = CountDownLatch(1)
//
//    @Volatile
//    private var count = 0
//
//    suspend fun increment() {
//        lock.await()
//            count++
//        lock.countDown()
//    }
//
//    fun reset() {
//        count = 0
//    }
//
//    fun getCount(): Int = count
//}