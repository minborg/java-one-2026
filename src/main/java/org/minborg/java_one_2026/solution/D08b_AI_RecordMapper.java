package org.minborg.java_one_2026.solution;

import org.minborg.java_one_2026.Util;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;

/**
 * Factory for creating getters (mappers) and setters between Java records and FFM memory segments.
 *
 * <p>Constraints:
 * <ul>
 *   <li>Record components must be primitive types only.</li>
 *   <li>StructLayout must consist only of named ValueLayout members.</li>
 *   <li>Names and primitive types must match one-to-one, otherwise IllegalArgumentException.</li>
 * </ul>
 */
public final class D08b_AI_RecordMapper {

    /**
     * A getter (mapper) that reads a record of type {@code R} from a segment starting at {@code offset}.
     *
     * @param <R> record type
     */
    @FunctionalInterface
    public interface RecordGetter<R extends Record> {
        /**
         * Materialize a new record instance by reading fields from {@code segment} starting at {@code offset}.
         *
         * @param segment segment to read from (must not be null)
         * @param offset  start offset in bytes (must be in range)
         * @return a new record instance
         * @throws NullPointerException     if segment is null
         * @throws IllegalArgumentException if offset is negative or out of range for the configured layout
         */
        R get(MemorySegment segment, long offset);
    }

    /**
     * A setter that writes a record of type {@code R} into a segment starting at {@code offset}.
     *
     * @param <R> record type
     */
    @FunctionalInterface
    public interface RecordSetter<R extends Record> {
        /**
         * Write the given record instance into {@code segment} starting at {@code offset}.
         *
         * @param segment segment to write to (must not be null)
         * @param offset  start offset in bytes (must be in range)
         * @param value   record instance to write (must not be null)
         * @throws NullPointerException     if segment or value is null
         * @throws IllegalArgumentException if offset is negative or out of range for the configured layout
         */
        void set(MemorySegment segment, long offset, R value);
    }

    public record Point(double x, double y) {}

    public static final StructLayout LAYOUT = MemoryLayout.structLayout(
            JAVA_DOUBLE.withName("x"),
            JAVA_DOUBLE.withName("y")
    );

    public static final RecordGetter<Point> POINT_GETTER = createGetter(Point.class, LAYOUT);
    public static final RecordSetter<Point> POINT_SETTER = createSetter(Point.class, LAYOUT);

    static void main() {

        try (var arena = Arena.ofConfined()) {
            var segment = arena.allocate(LAYOUT);
            IO.println("before setter: " + Util.toPoint2dString(segment));
            POINT_SETTER.set(segment, 0L, new Point(3.0d, 4.0d));
            IO.println("after setters: " + Util.toPoint2dString(segment));

            Point readPoint = POINT_GETTER.get(segment, 0L);
            IO.println("readPoint = " + readPoint);
        }

    }

    /**
     * Create a record getter (mapper) for the given record type and struct layout.
     *
     * @param recordClass record class (must be a user-defined record type; not Record.class)
     * @param layout      struct layout consisting only of named ValueLayout members
     * @param <R>         record type
     * @return a RecordGetter that reads {@code R} from a segment at an offset
     * @throws NullPointerException     if recordClass or layout is null
     * @throws IllegalArgumentException if the record/layout are incompatible
     */
    public static <R extends Record> RecordGetter<R> createGetter(Class<R> recordClass, StructLayout layout) {
        Objects.requireNonNull(recordClass, "recordClass");
        Objects.requireNonNull(layout, "layout");
        validateRecordClass(recordClass);

        var bindings = buildBindings(recordClass, layout);
        var ctor = findCanonicalConstructor(recordClass);

        // record implementation for the getter
        record GetterImpl<R extends Record>(StructLayout layout, long layoutSize, MethodHandle ctor, Binding[] bindings)
                implements RecordGetter<R> {

            @Override
            public R get(MemorySegment segment, long offset) {
                Objects.requireNonNull(segment, "segment");
                validateRange(segment, offset, layoutSize);

                Object[] args = new Object[bindings.length];
                for (int i = 0; i < bindings.length; i++) {
                    Binding b = bindings[i];
                    long addr = offset + b.fieldOffset;
                    // Each read returns boxed primitives; MethodHandle.invokeWithArguments accepts Objects.
                    args[i] = readPrimitive(segment, b.valueLayout, addr);
                }

                try {
                    @SuppressWarnings("unchecked")
                    R r = (R) ctor.invokeWithArguments(args);
                    return r;
                } catch (RuntimeException | Error e) {
                    throw e;
                } catch (Throwable t) {
                    throw new IllegalStateException("Failed to construct record " + GetterImpl.this.ctor, t);
                }
            }
        }

        return new GetterImpl<>(layout, layout.byteSize(), ctor, bindings);
    }

    /**
     * Create a record setter for the given record type and struct layout.
     *
     * @param recordClass record class (must be a user-defined record type; not Record.class)
     * @param layout      struct layout consisting only of named ValueLayout members
     * @param <R>         record type
     * @return a RecordSetter that writes {@code R} to a segment at an offset
     * @throws NullPointerException     if recordClass or layout is null
     * @throws IllegalArgumentException if the record/layout are incompatible
     */
    public static <R extends Record> RecordSetter<R> createSetter(Class<R> recordClass, StructLayout layout) {
        Objects.requireNonNull(recordClass, "recordClass");
        Objects.requireNonNull(layout, "layout");
        validateRecordClass(recordClass);

        var bindings = buildBindings(recordClass, layout);
        var accessors = findComponentAccessors(recordClass);

        // record implementation for the setter
        record SetterImpl<R extends Record>(StructLayout layout, long layoutSize, Binding[] bindings, MethodHandle[] accessors)
                implements RecordSetter<R> {

            @Override
            public void set(MemorySegment segment, long offset, R value) {
                Objects.requireNonNull(segment, "segment");
                Objects.requireNonNull(value, "value");
                validateRange(segment, offset, layoutSize);

                for (int i = 0; i < bindings.length; i++) {
                    Binding b = bindings[i];
                    long addr = offset + b.fieldOffset;

                    Object fieldValue;
                    try {
                        fieldValue = accessors[i].invoke(value); // boxed primitive
                    } catch (RuntimeException | Error e) {
                        throw e;
                    } catch (Throwable t) {
                        throw new IllegalStateException("Failed to read record component via accessor", t);
                    }

                    writePrimitive(segment, b.valueLayout, addr, fieldValue);
                }
            }
        }

        return new SetterImpl<>(layout, layout.byteSize(), bindings, accessors);
    }

    // ===== Implementation details =====

    private static void validateRecordClass(Class<?> recordClass) {
        // requirement: extends Record and is not Record itself; also ensure it's actually a record
        if (recordClass == Record.class) {
            throw new IllegalArgumentException("recordClass must not be java.lang.Record itself");
        }
        if (!Record.class.isAssignableFrom(recordClass)) {
            throw new IllegalArgumentException("recordClass must extend java.lang.Record");
        }
        if (!recordClass.isRecord()) {
            throw new IllegalArgumentException("recordClass must be a record type (Class::isRecord is false)");
        }
    }

    /**
     * One binding per record component: which ValueLayout to use and its byte offset in the struct.
     */
    private record Binding(String name, Class<?> carrier, ValueLayout valueLayout, long fieldOffset) {}

    private static <R extends Record> Binding[] buildBindings(Class<R> recordClass, StructLayout layout) {
        RecordComponent[] components = recordClass.getRecordComponents();
        if (components == null) {
            throw new IllegalArgumentException("Not a record: " + recordClass.getName());
        }

        // Build a lookup table from layout member name -> (ValueLayout, offset)
        Map<String, LayoutMember> members = layoutMembers(layout);

        // Ensure no extra layout members and no missing members:
        if (members.size() != components.length) {
            throw new IllegalArgumentException(
                    "Layout member count (" + members.size() + ") does not match record component count (" + components.length + ")");
        }

        Binding[] bindings = new Binding[components.length];
        for (int i = 0; i < components.length; i++) {
            RecordComponent rc = components[i];
            String name = rc.getName();
            Class<?> type = rc.getType();

            if (!type.isPrimitive()) {
                throw new IllegalArgumentException("Record component '" + name + "' must be primitive, found: " + type.getTypeName());
            }

            LayoutMember lm = members.get(name);
            if (lm == null) {
                throw new IllegalArgumentException("No layout member named '" + name + "' for record component '" + name + "'");
            }

            // ValueLayout carrier must match the primitive type
            Class<?> carrier = lm.valueLayout.carrier();
            if (carrier != type) {
                throw new IllegalArgumentException(
                        "Type mismatch for '" + name + "': record has " + type.getTypeName()
                                + " but layout carrier is " + carrier.getTypeName());
            }

            bindings[i] = new Binding(name, carrier, lm.valueLayout, lm.offset);
        }
        return bindings;
    }

    private record LayoutMember(ValueLayout valueLayout, long offset) {}

    private static Map<String, LayoutMember> layoutMembers(StructLayout layout) {
        List<MemoryLayout> memberLayouts = layout.memberLayouts();
        Map<String, LayoutMember> map = new HashMap<>(memberLayouts.size() * 2);

        for (MemoryLayout ml : memberLayouts) {
            if (!(ml instanceof ValueLayout vl)) {
                throw new IllegalArgumentException(
                        "StructLayout must contain only ValueLayout members, found: " + ml.getClass().getName());
            }
            String name = ml.name().orElseThrow(() ->
                    new IllegalArgumentException("All ValueLayout members must be named (missing name in layout member)"));

            if (map.containsKey(name)) {
                throw new IllegalArgumentException("Duplicate layout member name: '" + name + "'");
            }

            long off = layout.byteOffset(MemoryLayout.PathElement.groupElement(name));
            map.put(name, new LayoutMember(vl, off));
        }
        return map;
    }

    private static <R extends Record> MethodHandle findCanonicalConstructor(Class<R> recordClass) {
        RecordComponent[] components = recordClass.getRecordComponents();
        Class<?>[] ptypes = new Class<?>[components.length];
        for (int i = 0; i < components.length; i++) {
            ptypes[i] = components[i].getType();
        }

        try {
            var lookup = MethodHandles.publicLookup();
            return lookup.findConstructor(recordClass, MethodType.methodType(void.class, ptypes));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot access canonical constructor for " + recordClass.getName(), e);
        }
    }

    private static <R extends Record> MethodHandle[] findComponentAccessors(Class<R> recordClass) {
        RecordComponent[] components = recordClass.getRecordComponents();
        MethodHandle[] mhs = new MethodHandle[components.length];

        try {
            var lookup = MethodHandles.publicLookup();
            for (int i = 0; i < components.length; i++) {
                // Record component accessor is public instance method with same name, return primitive type.
                var rc = components[i];
                mhs[i] = lookup.findVirtual(recordClass, rc.getName(), MethodType.methodType(rc.getType()));
            }
            return mhs;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot access one or more record component accessors for " + recordClass.getName(), e);
        }
    }

    private static void validateRange(MemorySegment segment, long offset, long layoutSize) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0, was: " + offset);
        }
        long segSize = segment.byteSize();
        // avoid overflow: check as (offset > segSize - layoutSize) when possible
        if (layoutSize < 0) {
            throw new IllegalArgumentException("layoutSize must be >= 0, was: " + layoutSize);
        }
        if (layoutSize > segSize) {
            throw new IllegalArgumentException("Layout size (" + layoutSize + ") exceeds segment size (" + segSize + ")");
        }
        if (offset > segSize - layoutSize) {
            throw new IllegalArgumentException(
                    "offset (" + offset + ") out of range: need offset+layoutSize <= segmentSize; "
                            + "layoutSize=" + layoutSize + ", segmentSize=" + segSize);
        }
    }

    private static Object readPrimitive(MemorySegment segment, ValueLayout layout, long offset) {
        Class<?> c = layout.carrier();
        // Use strongly-typed reads to avoid ambiguity and keep behavior explicit.
        if (c == byte.class)   return segment.get((ValueLayout.OfByte) layout, offset);
        if (c == boolean.class) {
            // No standard ValueLayout for boolean; treat as byte 0/1 if user provided such a layout.
            // If they used a custom ValueLayout with carrier boolean.class, handle it here.
            return segment.get((ValueLayout.OfBoolean) layout, offset);
        }
        if (c == short.class)  return segment.get((ValueLayout.OfShort) layout, offset);
        if (c == char.class)   return segment.get((ValueLayout.OfChar) layout, offset);
        if (c == int.class)    return segment.get((ValueLayout.OfInt) layout, offset);
        if (c == float.class)  return segment.get((ValueLayout.OfFloat) layout, offset);
        if (c == long.class)   return segment.get((ValueLayout.OfLong) layout, offset);
        if (c == double.class) return segment.get((ValueLayout.OfDouble) layout, offset);

        throw new IllegalArgumentException("Unsupported primitive carrier: " + c.getTypeName());
    }

    private static void writePrimitive(MemorySegment segment, ValueLayout layout, long offset, Object value) {
        Class<?> c = layout.carrier();
        if (c == byte.class)   { segment.set((ValueLayout.OfByte) layout, offset, (byte) value); return; }
        if (c == boolean.class){ segment.set((ValueLayout.OfBoolean) layout, offset, (boolean) value); return; }
        if (c == short.class)  { segment.set((ValueLayout.OfShort) layout, offset, (short) value); return; }
        if (c == char.class)   { segment.set((ValueLayout.OfChar) layout, offset, (char) value); return; }
        if (c == int.class)    { segment.set((ValueLayout.OfInt) layout, offset, (int) value); return; }
        if (c == float.class)  { segment.set((ValueLayout.OfFloat) layout, offset, (float) value); return; }
        if (c == long.class)   { segment.set((ValueLayout.OfLong) layout, offset, (long) value); return; }
        if (c == double.class) { segment.set((ValueLayout.OfDouble) layout, offset, (double) value); return; }

        throw new IllegalArgumentException("Unsupported primitive carrier: " + c.getTypeName());
    }
}
