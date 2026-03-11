package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D03_UseAfterFree {

    // struct Point2d {
    //   double x;
    //   double y;
    // } point = {3.0, 4.0};

    void main() {
        MemorySegment point;
        try (var arena = Arena.ofConfined()) {
            point = arena.allocateFrom(JAVA_DOUBLE, 3.0d, 4.0d);
            IO.println(Util.toPoint2dString(point));
        } // The arena is implicitly closed (AutoClosable)

        // No use-after-free access
        point.get(JAVA_DOUBLE, 0L);

    }

}
