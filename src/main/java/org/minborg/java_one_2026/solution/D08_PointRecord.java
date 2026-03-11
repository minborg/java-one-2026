package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D08_PointRecord {

    private static final MemoryLayout POINT = MemoryLayout.structLayout(
            JAVA_DOUBLE.withName("x"),
            JAVA_DOUBLE.withName("y")
    );

    record Point(double x, double y) { }

    void main() {

        try (var arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocateFrom(JAVA_DOUBLE, 3.0d, 4.0d);
            IO.println(Util.toPoint2dString(segment));

            // Getter: Detached immutable Snapshot
            Point point = POINT_MAPPER.apply(segment);
            IO.println("point = " + point);

            Point newPoint = new Point(point.x() + 1, point.y() + 1);
            IO.println("newPoint = " + newPoint);
            // Setter
            POINT_REVERSED_MAPPER.accept(segment, newPoint);
            IO.println(Util.toPoint2dString(segment));
        }
    }

    private static final Function<MemorySegment, Point> POINT_MAPPER = new PointMapper();

    // Can be automatically generated given a record class and a memory layout
    static final class PointMapper implements Function<MemorySegment, Point> {

        static final VarHandle X = POINT.varHandle(PathElement.groupElement("x"));
        static final VarHandle Y = POINT.varHandle(PathElement.groupElement("y"));

        @Override
        public Point apply(MemorySegment segment) {
            return new Point((double) X.get(segment, 0), (double) Y.get(segment, 0));
        }
    }

    private static final BiConsumer<MemorySegment, Point> POINT_REVERSED_MAPPER = new ReversedPointMapper();

    // Can be automatically generated given a record class and a memory layout
    static final class ReversedPointMapper implements BiConsumer<MemorySegment, Point> {

        static final VarHandle X = POINT.varHandle(PathElement.groupElement("x"));
        static final VarHandle Y = POINT.varHandle(PathElement.groupElement("y"));

        @Override
        public void accept(MemorySegment segment, Point point) {
            X.set(segment, 0, point.x());
            Y.set(segment, 0, point.y());
        }
    }

    // AI GENERATE??

}
