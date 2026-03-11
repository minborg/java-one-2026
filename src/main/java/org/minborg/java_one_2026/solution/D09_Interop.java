package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteOrder;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D09_Interop {

    // struct Point2d {
    //   double x;
    //   double y;
    // } point = {3.0, 4.0};

    void main() {
        // Arena -> Memory Segment Lifecycles
        try (var arena = Arena.ofConfined()) {
            MemorySegment point = arena.allocateFrom(JAVA_DOUBLE, 3.0, 4.0);
            IO.println(Util.toPoint2dString(point));

            // Interop with old constructs
            var asBuffer = point.asByteBuffer();
            IO.println(Util.toPoint2dString(asBuffer));

            byte[] toByteArray = point.toArray(JAVA_BYTE);
            var fromBuffer = MemorySegment.ofBuffer(asBuffer);

            IO.println(JAVA_DOUBLE);
            IO.println(JAVA_DOUBLE.withOrder(ByteOrder.BIG_ENDIAN));

            // Memory ordering
            // Memory alignment
            // No out of bounds addressing
            // No use-after-free access
            // Thread confinement (Optional)

        }
    }

}
