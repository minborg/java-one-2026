We are dealing with Java code using JDK 22 or newer and the FFM API (`java.lang.foreign`).
There is a user-defined `record` class of type `R extends java.lang.Record` (i.e., "the record").
The components of the record class can only be of primitive types (and not reference objects).
There is also a user-defined `java.lang.foreign.StructLayout` (i.e., "the layout").
The layout can only consist of `java.lang.foreign.ValueLayout` members.

The objective is, given the record class and the layout, to create
a record mapper (or record getter) (i.e., "the mapper") that can materialize records
from a `java.lang.foreign.MemorySegment` (i.e., "the segment").
When calling the mapper creation method, there should be a check that the record
class parameter actually extends `Record` and is not the `Record` class itself.

The mapper shall have the parameters `MemorySegment segment` and `long offset` as input parameters.
The offset specifies from where in the segment mapping shall start (in many cases this will be zero).
The mapper shall return a new record instance of type `R`.

The names and types of the record components shall match the names and types of the layout
or else `IllegalArgumentException` is thrown.
The record itself should be untouched.
The mapper should do input validations (i.e., null and range checks).
The implementing classes should be realized as `record` classes in order to improve performance.

We also want to generate the dual: a "record setter" given a record class and a layout
that takes a `MemorySegment segment`, a `long offset` and a record instance of type `R`
and sets values in the segment from the provided record.
The record setter should not return any value (i.e., `void`).

The mapper getter and setter method should be documented using JavaDocs.