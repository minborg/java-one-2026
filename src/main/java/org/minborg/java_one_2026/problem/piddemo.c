#include <jni.h>

#if defined(_WIN32)
  #include <windows.h>
#else
  #include <unistd.h>
#endif

// Signature must match: (no args) -> long
JNIEXPORT jlong JNICALL Java_org_minborg_java_one_2026_problem_getPid(JNIEnv *env, jclass clazz) {
#if defined(_WIN32)
    return (jlong) GetCurrentProcessId();
#else
    return (jlong) getpid();
#endif
}