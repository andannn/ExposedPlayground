@file:OptIn(ExperimentalForeignApi::class)

package com.libuv.cinterop

import kotlinx.cinterop.*
import libuv.*
import kotlin.test.*
import kotlinx.cinterop.ptr as cinteropPtr

class LibuvTest {
    @Test
    fun testLibuvBuild() {
        println("uv_version() ${uv_version()}")
        println("uv_version_string() ${uv_version_string()?.toKString()}")
    }

    @Test
    fun event_loop(): Unit = memScoped {
        val loopPointer = alloc<uv_loop_t>().cinteropPtr
        uv_loop_init(loopPointer)

        println("Now quitting.\n")

        uv_run(loopPointer, UV_RUN_DEFAULT);
        uv_loop_close(loopPointer)
    }

    @Test
    fun uv_default_loop_test(): Unit = memScoped {
        val loop = uv_default_loop()
        println("Default loop.")

        val timerPointer = alloc<uv_timer_s>().cinteropPtr
        uv_timer_init(loop, timerPointer).also {
            println("Timer initialized $it")
        }

        uv_timer_start(
            handle = timerPointer,
            cb = staticCFunction { timer ->
                println("Timer callback")
            },
            timeout = 3000u,
            repeat = 1000u,
        )

        uv_run(loop, UV_RUN_DEFAULT)
    }

    @Test
    fun uv_idle_handler_test(): Unit = memScoped {
        val idleHandlerPtr = alloc<uv_idle_t>().cinteropPtr
        uv_idle_init(uv_default_loop(), idleHandlerPtr)
        uv_idle_start(idleHandlerPtr, cb = staticCFunction { idlePtr ->
            uv_idle_handler_test_counter++
            println("Idle handler callback")
            if (uv_idle_handler_test_counter >= 100)
                uv_idle_stop(idlePtr)
        })
        println("Idling...")

        uv_run(uv_default_loop(), UV_RUN_DEFAULT)
        uv_loop_close(uv_default_loop())
    }

    companion object {
        var uv_idle_handler_test_counter = 1
    }
}