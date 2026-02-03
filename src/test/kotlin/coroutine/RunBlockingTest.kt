package coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RunBlockingTest {
    @Test
    fun `run blocking test`() {
        println(
            Thread.currentThread().threadId()
        )
        runBlocking {
            val currentInterceptor = coroutineContext[ContinuationInterceptor]

            launch {
                assertEquals(currentInterceptor, coroutineContext[ContinuationInterceptor])
            }

            launch(Dispatchers.IO) {
                assertNotEquals(currentInterceptor, coroutineContext[ContinuationInterceptor])
            }
        }
    }
}
