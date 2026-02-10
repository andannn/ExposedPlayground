package interop

import androidx.test.ext.junit.runners.AndroidJUnit4
import me.andannn.simplemath.TestInterOp
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class InterOpTest {
    @Test
    fun interop_test() {
        assertEquals(3, TestInterOp().foo())
    }
}
