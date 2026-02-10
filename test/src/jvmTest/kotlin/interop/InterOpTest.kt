package interop

import me.andannn.simplemath.TestInterOp
import kotlin.test.Test
import kotlin.test.assertEquals

class InterOpTest {
    @Test
    fun `interop test`() {
        assertEquals(3, TestInterOp().foo())
    }
}
