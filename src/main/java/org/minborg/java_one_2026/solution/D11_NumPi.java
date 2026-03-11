package org.minborg.java_one_2026.solution;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.SequenceLayout;
import java.lang.invoke.VarHandle;
import java.util.concurrent.CompletableFuture;

import static java.lang.foreign.MemoryLayout.sequenceLayout;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_FLOAT;

public final class D11_NumPi {

    private static final MemoryLayout MATRIX = sequenceLayout(2,
            MemoryLayout.sequenceLayout(3, JAVA_FLOAT));

    private static final VarHandle MATRIX_HANDLE = MATRIX.varHandle(
            MemoryLayout.PathElement.sequenceElement(),
            MemoryLayout.PathElement.sequenceElement()
    );

    void main() {

        IO.println("MATRIX = " + MATRIX);
        IO.println("MATRIX_HANDLE = " + MATRIX_HANDLE);
        // Why can't we see the names?

        try (var arena = Arena.ofConfined()) {
            var segment = arena.allocateFrom(JAVA_FLOAT, 1f, 2f, 3f, 4f, 5f, 6f);
            IO.println(segment);
            // Either `int` or `long` variable _and_ predicate. No mix.
            for (long r = 0; r < 2L; r++) {
                for (long c = 0; c < 3L; c++) {
                    IO.println("(" + r + "," + c + ") = " + (double) MATRIX_HANDLE.get(segment, 0L, r, c));
                }
            }
            // Checks indices individually
            float _ = (float) MATRIX_HANDLE.get(segment, 0L, 3L, 0L);
        }
    }

}
