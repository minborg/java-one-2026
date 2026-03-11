package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D09_MMap {

    void main() throws IOException {

        // Contents of the file: "ABCDEFGH"
        Path path = Path.of("data", "file.txt");
        IO.println("path = " + path.toAbsolutePath());

        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
             var arena = Arena.ofConfined()) {
            // Map the file into a memory segment
            MemorySegment mappedSegment = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, fileChannel.size(), arena);

            // Read data directly from memory
            byte firstByte = mappedSegment.get(ValueLayout.JAVA_BYTE, 0L);
            IO.println("firstByte = " + (char) firstByte);

        } // Segment automatically unmapped here
    }

}
