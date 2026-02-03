package coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.InvocationInterceptor
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test

class TestScope {
    @Test
    fun `test scope test`() = runTest {
        println(
            coroutineContext[ContinuationInterceptor]
        )
        // StandardTestDispatcher can skip delay
        delay(10000000000000)
    }
}