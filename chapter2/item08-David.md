# Avoid finalizers and cleaners

## Finalizer

- `Object.finalize()` method is called the finalizer.
- Finalizers had been used to release resources such as files or threads that require termination.
- `Object.finalize()` method is executed by the garbage collector when the object becomes unreachable. However, a garbage collector's behavior is implementation-dependent and unpredictable.
- Also, uncaught exceptions thrown during finalization is ignored, resulting in corrupted states and security vulnerabilities.
- Has been **deprecated** since Java 9.

## Cleaner

- `java.lang.ref.Cleaner` has been introduced in Java 9 to replace finalizers.
-  The execution of the cleaning action is performed by a separate cleaner thread, as opposed to a garbage collector. However, the execution of the cleaner thread is still dependent on the garbage collection algorithm.
- Cleaners are tricky to use, where the key point is to **not refer** to the object being cleaned up; such reference creates circularity that prevents the object from becoming eligible for garbage collection.
- Should only be used as a **safety net** in addition to implementing Autocloseable interface.

## AutoCloseable interface

- `java.lang.AutoCloseable` has been introduced in Java 7, along with `try-with-resources` (item 9).
- Implementing AutoCloseable interface and `close()` method is the best practice for releasing resources.
- More on `try-with-resources` in item 9.