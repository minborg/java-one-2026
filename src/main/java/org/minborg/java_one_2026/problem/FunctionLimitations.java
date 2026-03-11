package org.minborg.java_one_2026.problem;

public final class FunctionLimitations {

    /*
    #include <stdio.h>
    #include <unistd.h>
    #include <sys/types.h>

    // A sad story ...
    int main() {
       pid_t pid = getpid();
       printf("Current Process ID: %d\n", (int)pid); // Casting to int for printing
       return 0;
    }
     */

    private static /**/ native /**/ long getPid();
    // A .h file is generated
    // Java_org_minborg_java_one_2026_problem_getPid()


    /*
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

    */


    /*
    javac FunctionLimitations.java
    javac -h . FunctionLimitations.java

    # make the 'piddemo' lib If Mac
    gcc -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/darwin" \
    -shared -o libpiddemo.dylib piddemo.c
     */

    static {
        // Loads libpiddemo.so (Linux/macOS) or piddemo.dll (Windows)
        System.loadLibrary("piddemo");
    }

    void main() {
        long pid = getPid();
        IO.println("Current Process ID from JNI: " + pid);
        IO.println("Current Process ID from Java (ProcessHandle): " + ProcessHandle.current().pid());

        // The C function name must match the JNI-mangled name
        // The shim lib has to be shipped along with your application
        // Ensure your native library is found via -Djava.library.path=. or placed in a standard library path.
        // Imagine having 1,000 native methods in 100 classes ...

    }

}
