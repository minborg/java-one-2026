package org.minborg.java_one_2026;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.HexFormat;

public final class Util {
    private Util() {
    }

    private static String toHex(MemorySegment segment) {
        final var hf = HexFormat.ofDelimiter(" ");
        return hf.formatHex(segment.toArray(ValueLayout.JAVA_BYTE));
    }

    private static String toHex(ByteBuffer buffer) {
        return toHex(MemorySegment.ofBuffer(buffer));
    }

    private static String toPoint2d(MemorySegment segment) {
        return "[" +
                segment.getAtIndex(ValueLayout.JAVA_DOUBLE, 0) + ", " +
                segment.getAtIndex(ValueLayout.JAVA_DOUBLE, 1) + "]";
    }

    private static String toPoint2d(ByteBuffer buffer) {
        final DoubleBuffer doubleBuffer = buffer.asDoubleBuffer();
        return "[" +
                doubleBuffer.get(0) + ", " +
                doubleBuffer.get(1) + "]";
    }

    public static String toPoint2dString(MemorySegment segment) {
        return toPoint2d(segment) + " - " + toHex(segment) + " " + (segment.isNative() ? "(N)" : "(H)");
    }

    public static String toPoint2dString(ByteBuffer buffer) {
        return toPoint2d(buffer) + " - " + toHex(buffer) + " " + (buffer.isDirect() ? "(N)" : "(H)");
    }


}
