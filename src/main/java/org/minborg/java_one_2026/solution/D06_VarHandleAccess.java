package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D06_VarHandleAccess {

    static final MemoryLayout POINT = MemoryLayout.structLayout(
            JAVA_DOUBLE.withName("x"),
            JAVA_DOUBLE.withName("y")
    );

    // VarHandle[varType=double, coord=[MemorySegment, long]]
    // Need to be static final in order to be performant
    static final VarHandle X_HANDLE = POINT.varHandle(PathElement.groupElement("x"));
    static final VarHandle Y_HANDLE = POINT.varHandle(PathElement.groupElement("y"));

    void main() {

        IO.println("X_HANDLE = " + X_HANDLE);
        IO.println("Y_HANDLE = " + Y_HANDLE);

        try (var arena = Arena.ofConfined()) {

            MemorySegment point = arena.allocate(POINT);
            X_HANDLE.set(point, 0L, 3.0d);
            Y_HANDLE.set(point, 0L, 4.0d); // No index/position for Y !!!
            IO.println(Util.toPoint2dString(point));

            // Var handle under the hood
            point.get(JAVA_DOUBLE, 0);

            // Method and VarHandle combinators
            VarHandle specializedXHandle = MethodHandles.insertCoordinates(X_HANDLE, 1, 0L);
            IO.println("specializedXHandle = " + specializedXHandle);
            specializedXHandle.set(point, 6.0);
            IO.println(Util.toPoint2dString(point));
            IO.println(specializedXHandle);

            // No non-aligned access !
            JAVA_DOUBLE.varHandle().compareAndSet(point, /* offset */ 1L, 0.0d, 3.0d);
        }
    }

}
