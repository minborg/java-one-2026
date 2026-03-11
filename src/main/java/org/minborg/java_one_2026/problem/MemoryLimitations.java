package org.minborg.java_one_2026.problem;

import java.nio.ByteBuffer;

import org.minborg.java_one_2026.Util;

public final class MemoryLimitations {

    // struct Point2d {
    //   double x;
    //   double y;
    // } point = {3.0, 4.0};

    void main() {
        // Heap buffer backed by a byte array on heap
        // The address can't be retained by native code
        ByteBuffer heapPoint = ByteBuffer.allocate(2 * 8);
        heapPoint.putDouble(0, 3.0);
        heapPoint.putDouble(8, 4.0);

        IO.println(Util.toPoint2dString(heapPoint));

        // Native buffer backed by malloc:ed memory
        ByteBuffer nativePoint = ByteBuffer.allocateDirect(2 * 8);
        nativePoint.putDouble(0, 3.0);
        nativePoint.putDouble(8, 4.0);

        IO.println(Util.toPoint2dString(nativePoint));

        // Unsafe ...

        // Mixed relative (cursor)/absolut addressing
        // A Buffer is not immutable
        // How do we deterministically free the native memory?
        // What if we have billions of points?
        // What if the point layout changes?
        // How do we perform atomic operations like CAS and CAE
        // What if the native memory is related to other native memory? (UAF, leaks)
        // What if something holds a reference to our native memory (liveness)?
    }

}
