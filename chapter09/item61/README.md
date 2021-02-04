# Item 61: Prefer privmite types to boxed primitives

## Key Points
- Applying the `==` operator to boxed primitives is almost always wrong,
  because the `==` operator is an identity comparison, i.e. for objects, the
  `==` operator compares the memory addresses of the objects.
    - e.g. [sample code](BoxedTypeIdentity.java)
- Avoid unnecessary autoboxing, i.e. unnecessary object creations.
    - e.g.  `Long sum = 0L; sum += 1;` will auto-unbox `sum`, then autobox the
      incremented value into a new `Long` object; instead, use `long sum` to
      avoid unnecessary autoboxing.

[Issue](https://github.com/java-squid/effective-java/issues/63)

