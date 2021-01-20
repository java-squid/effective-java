# Item 47: Prefer Collection to Stream as a return type

## Key Points
- Prefer `Collection<E>` to `Stream<T>` as a return type because:
    - although both `Collection<E>` and `Stream<T>` interfaces have the
      `iterator()` method, only `Collection<E>` extends `Iterable<T>`,
      resulting in `Stream<T>` needing a cast or an adapter to be usable in
      enhanced for loops.
    - `Collection<E>` offers both `iterator()` and `stream()` methods.

## Why Does `Stream<T>` Not Extend `Iterable<T>`?
- `Stream<T>` can only be operated on once, whereas `Iterable<T>` in general
  can be iterated over multiple times.
- Reference: [Stack Overflow](https://stackoverflow.com/a/20130131)

## Method Reference and Type Inference
- Although the `Iterable<T>` interface is not annotated with
  `@FunctionalInterface`, the interface has a single abstract method
  `iterator()`, so a zero-parameter lambda that returns an iterator fulfills
  the interface contract.
- Method reference `stream::iterator` is equivalent to `() -> stream.iterator()`
- With Java's type inference, the `iterableOf()` method below properly
  functions as an adapter without the need for a cast, and can be used to
  gracefully supply streams to enhanced for loops.
  ```java
  public static <E> Iterable<E> iterableOf(Stream<E> stream) {
      return stream::iterator;
  }

  for (int n : iterableOf(IntStream.range(0, 5)) {
      // process n
  }
  ```

## Returning a Very Large or Infinite Sequence
- Standard collection implementations store all of their elements on memory and
  therefore cannot handle large sequences, e.g. a power set.
- Consider implementing a special-purpose collection that does not store the
  elements of a sequence but rather relies on an iterator to generates the next
  element.
- The `AbstractList<E>` abstract skeletal implementation class has a concrete
  inner `Iterator()` class that relies on the abstract `get(int index)` method,
  so providing a concrete `get(int index)` method suffices to create an
  `Iterable<T>` instance.
- Also, it is possible to manually implement a concrete iterator atop an
  `AbstractCollection<E>`.
    - [example PowerSet implementation](https://github.com/java-squid/effective-java/commit/16ed3082c86eda2993b18edc3c3323dcd3ff529b)

[GitHub Issue #47](https://github.com/java-squid/effective-java/issues/47)

