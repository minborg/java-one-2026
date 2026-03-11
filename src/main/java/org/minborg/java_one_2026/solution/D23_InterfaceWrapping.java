package org.minborg.java_one_2026.solution;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandleProxies;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static java.lang.foreign.ValueLayout.*;

public final class D23_InterfaceWrapping {

    @FunctionalInterface
    public interface StrLen {
        long applyAsLong(MemorySegment charPtr);
    }

    private static final StrLen STRLEN = link(
            StrLen.class, "strlen", FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));

    void main() throws Throwable {
        var text = "JavaOne \0 2026";
        try (var arena = Arena.ofConfined()) {
            MemorySegment charPtr = arena.allocateFrom(text);

            long len = STRLEN.applyAsLong(charPtr);
            IO.println("len from native = " + len);
            IO.println("len from Java   = " + text.length());
        }
    }

    public static <T> T link(Class<T> type,
                             String name,
                             // Can automatically be derived from the type!
                             FunctionDescriptor fd) {

        Linker linker = Linker.nativeLinker();
        MemorySegment symbol = linker.defaultLookup().findOrThrow(name);

        try {
            var mh = linker.downcallHandle(symbol, fd);
            return MethodHandleProxies.asInterfaceInstance(type, mh);
        } catch (NoSuchMethodError e) {
            throw new InternalError(e);
        }

    }

    public static <T> T link(Class<T> type, String name) {
        return link(type, name, functionDescriptorFromFunctionalInterface(type));
    }


    // AI Generated

    /**
     * Derive a FunctionDescriptor from a functional interface by analyzing its single abstract method (SAM).
     *
     * <p>Supported mappings:
     * <ul>
     *   <li>void -> FunctionDescriptor.ofVoid(...)</li>
     *   <li>primitive -> ValueLayout.JAVA_*</li>
     *   <li>MemorySegment -> ValueLayout.ADDRESS (native pointer)</li>
     * </ul>
     *
     * @throws IllegalArgumentException if {@code type} is not a functional interface or uses unsupported types
     */
    public static FunctionDescriptor functionDescriptorFromFunctionalInterface(Class<?> type) {
        if (type == null) throw new NullPointerException("type");
        if (!type.isInterface()) {
            throw new IllegalArgumentException("type must be an interface: " + type.getName());
        }

        Method sam = findSam(type);

        ValueLayout[] argLayouts = Arrays.stream(sam.getParameterTypes())
                .map(D23_InterfaceWrapping::toLayout)
                .toArray(ValueLayout[]::new);

        Class<?> rt = sam.getReturnType();
        if (rt == void.class) {
            return FunctionDescriptor.ofVoid(argLayouts);
        }
        return FunctionDescriptor.of(toLayout(rt), argLayouts);
    }

    private static Method findSam(Class<?> type) {
        Method[] methods = type.getMethods(); // includes inherited public methods
        Method sam = null;

        for (Method m : methods) {
            if (m.isDefault()) continue;
            if (Modifier.isStatic(m.getModifiers())) continue;
            if (m.getDeclaringClass() == Object.class) continue;

            // abstract instance method?
            if (!Modifier.isAbstract(m.getModifiers())) continue;

            if (sam != null) {
                throw new IllegalArgumentException("Not a functional interface (multiple abstract methods): "
                        + type.getName() + " has at least " + sam + " and " + m);
            }
            sam = m;
        }

        if (sam == null) {
            throw new IllegalArgumentException("Not a functional interface (no abstract method found): " + type.getName());
        }
        return sam;
    }

    private static ValueLayout toLayout(Class<?> carrier) {
        if (carrier == boolean.class) return JAVA_BOOLEAN;
        if (carrier == byte.class)    return JAVA_BYTE;
        if (carrier == short.class)   return JAVA_SHORT;
        if (carrier == char.class)    return JAVA_CHAR;
        if (carrier == int.class)     return JAVA_INT;
        if (carrier == long.class)    return JAVA_LONG;
        if (carrier == float.class)   return JAVA_FLOAT;
        if (carrier == double.class)  return JAVA_DOUBLE;

        if (carrier == MemorySegment.class) return ADDRESS;

        throw new IllegalArgumentException("Unsupported parameter/return type for FFM downcall: " + carrier.getTypeName()
                + " (use primitives or MemorySegment, or provide an explicit FunctionDescriptor)");
    }

}
