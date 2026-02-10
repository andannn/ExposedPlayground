package me.andannn.test

import kotlinx.cinterop.ExperimentalForeignApi

class TestInterOp {
    @OptIn(ExperimentalForeignApi::class)
    fun foo(): Int =
        me.andannn.simplemath.add_integers(
            1,
            2,
        )
}
