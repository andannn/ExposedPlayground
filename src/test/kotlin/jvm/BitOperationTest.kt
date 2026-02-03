package jvm

import kotlin.test.Test
import kotlin.test.assertEquals

class BitOperationTest {
    @Test
    fun `shift left operation`() {
        assertEquals(
            "00000000000000000000000000000100",
            (1 shl 2).toUInt().toString(2).padStart(32, '0')
        )
        assertEquals(
            "11111111111111111111111111111100",
            (-1 shl 2).toUInt().toString(2).padStart(32, '0')
        )
    }

    @Test
    fun `OR operation`() {
        assertEquals(
            "00000000000000000000000000010100",
            ((1 shl 2) or (1 shl 4)).toString(2).padStart(32, '0')
        )
    }
}