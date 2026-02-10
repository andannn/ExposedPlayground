package me.andannn.simplemath

/** Helper class to load native libraries based on the host platform. */
internal actual object NativeLibraryLoader {
    actual fun loadLibrary(name: String): Unit =
        synchronized(this) {
            // Load from APK natives
            System.loadLibrary(name)
        }
}
