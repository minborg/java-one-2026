package org.minborg.java_one_2026.solution;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

public final class D20_Linker {

    void main() throws Throwable {
        // Get a linker to link with native functions
        Linker linker = Linker.nativeLinker();

        // Returns a symbol lookup for symbols in a set of commonly used libraries.
        SymbolLookup stdlib = linker.defaultLookup();

        MemorySegment getPidSymbol = stdlib.findOrThrow("getpid");

        FunctionDescriptor getPidDesc = FunctionDescriptor.of(JAVA_INT);
        MethodHandle getPid = linker.downcallHandle(getPidSymbol, getPidDesc);

        int pid = (int)getPid.invokeExact();

        IO.println("pid = " + pid);

        IO.println("size_t = " + linker.canonicalLayouts().get("size_t"));
        IO.println("JAVA_LONG = " + JAVA_LONG);

    }

}
