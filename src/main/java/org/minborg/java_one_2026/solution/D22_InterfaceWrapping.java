package org.minborg.java_one_2026.solution;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.util.function.IntSupplier;

public final class D22_InterfaceWrapping {

    private static final IntSupplier PID = link(IntSupplier.class,
            "getpid",
            FunctionDescriptor.of(ValueLayout.JAVA_INT));

    void main() throws Throwable {
        int pid = PID.getAsInt();
        IO.println("pid = " + pid);
    }


    public static <T> T link(Class<T> type,
                             String name,
                             // Can automatically be derived from the type!
                             FunctionDescriptor fd) {

        Linker linker = Linker.nativeLinker();
        MemorySegment symbol = linker.defaultLookup().findOrThrow(name);

        try {
            var mh = linker.downcallHandle(symbol, fd);
            // Hidden gem!
            return MethodHandleProxies.asInterfaceInstance(type, mh);
        } catch (NoSuchMethodError e) {
            throw new InternalError(e);
        }

    }

}
