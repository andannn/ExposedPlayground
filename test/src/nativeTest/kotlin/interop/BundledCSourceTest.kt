package interop

import me.andannn.test.TestInterOp
import kotlin.test.Test
import kotlin.test.assertEquals

class BundledCSourceTest {
    @Test
    fun `foo`() {
        assertEquals(3, TestInterOp().foo())
    }
}
