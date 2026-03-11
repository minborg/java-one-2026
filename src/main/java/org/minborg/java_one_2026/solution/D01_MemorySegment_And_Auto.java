package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D01_MemorySegment_And_Auto {

    // struct Point2d {
    //   double x;
    //   double y;
    // } point = {3.0, 4.0};

    void main() {
        // A MemorySegment models a 64-bit continuous region of memory (heap or native): "flat memory"
        // View, automatic scope, backing array kept alive
        MemorySegment heapPoint = MemorySegment.ofArray(new double[]{3.0d, 4.0d});
        IO.println("heapPoint = " + heapPoint);
        IO.println(Util.toPoint2dString(heapPoint));

        MemorySegment nativePoint = Arena.ofAuto().allocate(8L * 2L);
        // No liveness check needed
        nativePoint.set(JAVA_DOUBLE, 0L, 3.0d);
        nativePoint.set(JAVA_DOUBLE, 8L, 4.0d);
        IO.println("nativePoint = " + nativePoint);
        IO.println(Util.toPoint2dString(nativePoint));

        // No out-of-bounds access !
        nativePoint.set(JAVA_DOUBLE, 16L, 5.0d);
    }

}
