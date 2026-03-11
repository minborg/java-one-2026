package org.minborg.java_one_2026.solution;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public final class D21_Linker {

    // LINT WARNING ?
    private static final MethodHandle PID_MH;

    static {
        Linker linker = Linker.nativeLinker();
        MemorySegment getPidSymbol = linker.defaultLookup().findOrThrow("getpid");
        FunctionDescriptor getPidDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT);
        try {
            PID_MH = linker.downcallHandle(getPidSymbol, getPidDesc);;
        } catch (NoSuchMethodError e) {
            throw new InternalError(e);
        }
    }

    void main() throws Throwable {

        int pid = (int)PID_MH.invokeExact();
        IO.println("pid = " + pid);

    }

}
