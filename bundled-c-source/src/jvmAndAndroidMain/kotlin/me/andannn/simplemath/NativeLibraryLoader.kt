package me.andannn.simplemath

internal expect object NativeLibraryLoader {
    fun loadLibrary(name: String)
}
