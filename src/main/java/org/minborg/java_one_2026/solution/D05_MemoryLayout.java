package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D05_MemoryLayout {

    // struct Point2d {
    //   double x;
    //   double y;
    // } point = {3.0, 4.0};

    static final MemoryLayout POINT = MemoryLayout.structLayout(
            JAVA_DOUBLE.withName("x"),
            JAVA_DOUBLE.withName("y")
    );

    // JAVA_DOUBLE and the other ValueLayout types have:
    //   Carrier type (e.g. `double`)
    //   Endianness (e.g. little endian)
    //   Alignment
    //   Optional Name


    // MemoryLayout (sealed interface)
    //   SequenceLayout (array)
    //   GroupLayout
    //     StructLayout
    //     UnionLayout
    //   PaddingLayout
    //   ValueLayout
    //       OfBoolean
    //       OfByte
    //       OfChar
    //       OfShort
    //       OfInt
    //       OfLong
    //       OfFloat
    //       OfDouble
    //       Address (with an additional target layout)

    void main() {

        IO.println("POINT = " + POINT);

        try (var arena = Arena.ofConfined()) {
            // Layout size and layout alignment are considered
            MemorySegment point = arena.allocate(POINT);
            IO.println(Util.toPoint2dString(point));
        }
    }

}
