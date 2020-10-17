# Prefer dependency injection to hardwiring resources

## Key points

- When / Where
  - For objects whose behavior is parameterized by underlying resources, i.e. dependencies.

- Why
  - Reusability - we can create a single class with different dependencies instead of creating multiple classes for each dependency.
  - Flexibility - it is easy to change a dependency to another.
  - Testability - it is easy to inject a mock dependency for testing, e.g. random generator with seed.

- How
  1. Injecting dependencies through constructors, static factories, or builders.
  2. Injecting a dependency *factory*.
  3. Using a *dependency injection framework*, e.g. Spring, to handle large dependency graphs.

## Follow-up Questions

- "Static utility classes and singletons are inappropriate for classes whose behavior is parameterized by an underlying resource."
  - Both static utility classes and singletons have limited instantiation, which means that if their behavior is parameterized by underlying resources, their dependencies must be changed. 
  - However, changing dependencies (e.g. with a setter) of an object during its life cycle is "error-prone and unworkable in a concurrent setting." Instead, new objects with different dependencies should be created. 
  - Using dependency injection for static classes and singletons that do not require changing dependencies is completely fine.

- Java Functional Interface
  - Supplier interface is part of the [java.util.function package](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/function/package-summary.html) that was introduced in Java 8 to provide target types for lambda expressions and method references, since functions are not first-class citizen in Java.
  - Injecting a dependency factory can be done easily by using the Supplier interface to *supply* a dependency into the target object.
- Why is Spring deprecating field injection?
  - Out of scope. Related to reflections, immutability, etc. Refer to this [Stackoverflow post](https://stackoverflow.com/a/39892204/10709152) if interested.

