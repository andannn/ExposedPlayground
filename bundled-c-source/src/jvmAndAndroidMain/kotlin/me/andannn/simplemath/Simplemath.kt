package me.andannn.simplemath

class TestInterOp {
    fun foo(): Int {
        LibraryLoader
        return nativeAddIntegers(
            1,
            2,
        )
    }

    private object LibraryLoader {
        init {
            println("init jni....")
            NativeLibraryLoader.loadLibrary("abcdJni")
        }
    }
}

// me.andannn.simplemath.SimplemathKt
private external fun nativeAddIntegers(
    a: Int,
    b: Int,
): Int
