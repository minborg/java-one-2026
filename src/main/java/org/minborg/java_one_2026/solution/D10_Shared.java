package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.Arena;
import java.util.concurrent.CompletableFuture;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;

public final class D10_Shared {

    void main() {
        try (var arena = Arena.ofShared()) {
            var segment = arena.allocate(16L);
            CompletableFuture.runAsync(() -> IO.println(segment.get(JAVA_BYTE, 0L)))
                    .join();
        } // Performs a local handshake
    }

}
