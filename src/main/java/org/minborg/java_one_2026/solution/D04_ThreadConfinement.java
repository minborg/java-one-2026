package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D04_ThreadConfinement {

    // struct Point2d {
    //   double x;
    //   double y;
    // } point = {3.0, 4.0};

    void main() throws InterruptedException {
        try (var arena = Arena.ofConfined()) {
            MemorySegment point = arena.allocateFrom(JAVA_DOUBLE, 3.0d, 4.0d);
            IO.println(Util.toPoint2dString(point));

            // Thread confinement (Optional)
            // Protects against subtle ordering and visibility bugs
            CompletableFuture.runAsync(() -> IO.println(Util.toPoint2dString(point)))
                    .join();

        }
    }

}
