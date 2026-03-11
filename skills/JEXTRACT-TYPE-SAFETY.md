We are dealing with Java code using JDK 22 or newer and the FFM API.
Given a run with jextract on a C ".h" file, we want to encapsulate general `MemorySegment` parameters
with type-safe parameters.
The type-safe parameter classes should hold the underlying segment.
The type-safe parameter classes shall implement this interface:
```java
interface HasSegment {
    segment();
}
```
For performance reasons, a type-safe parameter class shall be implemented using a record.
Here is an example of a type-safe parameter class:
```c 
size_t strlen(char* ptr);
```

Type-safe parameter
```java
public record Ptr(MemorySegment ptr) implements HasSegment {
    public static Ptr of(MemorySegment ptr) {
        Objects.requireNonNull(ptr);
        return new Ptr(ptr);
    }
}
```
Wrapper method
```java
long strlen(Ptr ptr) {
    Objects.requireNonNull(ptr);
    return jextract.pkt.strlen(ptr.segment()); 
}
```
Do not modify the generated code
Put the type-safe parameters and the wrapper methods in a separate package.
Carry over the documentation from the methods extracted by jextract to the type-safe methods.

Here is how the method in the example above can be used:
```java
try (var arena = Arena.ofConfined()) {
    long len = strlen(Ptr.of(arena.allocateFrom("test")));
}
```