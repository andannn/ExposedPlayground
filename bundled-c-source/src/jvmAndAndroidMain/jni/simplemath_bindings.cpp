#include <jni.h>
#include <simplemath.h>

JNIEXPORT jint

JNICALL nativeAddIntegers(
        JNIEnv *env,
        jclass clazz,
        jint a,
        jint b
) {
    return add_integers(a, b);
}

static const JNINativeMethod gMethods[] = {
        {"nativeAddIntegers", "(II)I", (void *) nativeAddIntegers}
};

static int register_methods(JNIEnv *env, const char *className,
                            const JNINativeMethod *methods,
                            int methodCount) {
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return JNI_ERR;
    }
    int result = env->RegisterNatives(clazz, methods, methodCount);
    env->DeleteLocalRef(clazz);
    if (result != 0) {
        return JNI_ERR;
    }
    return JNI_OK;
}

jint JNI_OnLoad(JavaVM *vm, void * /* reserved */) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    if (register_methods(env, "me/andannn/simplemath/SimplemathKt", gMethods, sizeof(gMethods) / sizeof(gMethods[0])) != JNI_OK) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}
