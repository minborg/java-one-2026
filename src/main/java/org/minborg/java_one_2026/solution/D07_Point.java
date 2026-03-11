package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.util.Objects;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

public final class D07_Point {

    interface HasSegment {
        MemorySegment segment();
    }

    interface Point extends HasSegment {

        MemoryLayout LAYOUT = MemoryLayout.structLayout(
                JAVA_DOUBLE.withName("x"),
                JAVA_DOUBLE.withName("y")
        );

        double x();

        double y();

        void x(double x);

        void y(double y);

        static Point of(MemorySegment segment) {
            return new PointImpl(segment);
        }

        static Point of(Arena arena) {
            return of(arena.allocate(LAYOUT));
        }
    }

    // Can be automatically generated via classfile or method combinators
    record PointImpl(@Override MemorySegment segment) implements Point {

        static final VarHandle X = LAYOUT.varHandle(PathElement.groupElement("x"));
        static final VarHandle Y = LAYOUT.varHandle(PathElement.groupElement("y"));

        @Override public double x() { return (double) X.get(segment, 0L);}
        @Override public double y() { return (double) Y.get(segment, 0L); }
        @Override public void x(double x) { X.set(segment, 0L, x);}
        @Override public void y(double y) { Y.set(segment, 0L, y);}

        @Override public String toString() { return "[" + x() + ", " + y() + "]"; }
        @Override public int hashCode() { return Objects.hash(x(), y()); }
        @Override public boolean equals(Object o) {
            return o instanceof Point other && x() == other.x() && y() == other.y();
        }
    }

    void main() {

        try (var arena = Arena.ofConfined()) {
            Point point = Point.of(arena);
            point.x(3.0d);
            point.y(4.0d);
            IO.println(point.toString()); // [3.0, 4.0]
            IO.println(Util.toPoint2dString(point.segment()));
        }

        Point point;
        try (var arena = Arena.ofConfined()) {
            point = Point.of(arena);
        }
        // `point` is closed!
        IO.println(Util.toPoint2dString(point.segment()));

    }

}
