package org.minborg.java_one_2026.solution;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D12_Misc {

    static final MemoryLayout POINT = MemoryLayout.structLayout(
            JAVA_DOUBLE.withName("x"),
            JAVA_DOUBLE.withName("y")
    );


    void main() {
        try (var arena = Arena.ofConfined()) {
            var points = arena.allocateFrom(JAVA_DOUBLE, 3, 4, 6, 8);
            IO.println(points);

            var secondPoint = points.asSlice(POINT.byteSize(), POINT);
            IO.println(secondPoint);

            IO.println("address = 0x" + Long.toHexString(points.address()));
            IO.println("byteSize = 0x" + Long.toHexString(points.byteSize()));
            IO.println("maxByteAlignment = 0x" + Long.toHexString(points.maxByteAlignment()));

            IO.println("isNative = " + points.isNative());
            IO.println("isMapped = " + points.isMapped());
            IO.println("isAccessibleBy = " + points.isAccessibleBy(Thread.currentThread()));
            IO.println("isReadOnly = " + points.isReadOnly());

            IO.println("A stream of slices:");
            points.elements(POINT)
                    .forEachOrdered(IO::println);

            IO.println("double[3] = " + points.getAtIndex(JAVA_DOUBLE, 3));

            points.fill((byte) 0);

            IO.println("NULL = " + MemorySegment.NULL);

            var asReadOnly = points.asReadOnly();
            IO.println("asReadOnly.isReadOnly() = " + asReadOnly.isReadOnly());
            asReadOnly.set(JAVA_DOUBLE, 0, -1.0);

        }
    }

}
