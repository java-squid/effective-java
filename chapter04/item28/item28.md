# Item 28: Prefer lists to arrays

## Key Points

- Arrays and generics have fundamental differences that make them hard to be mixed together.
- When encountered with generic array creation error or an unchecked cast warnings on a cast to an array type, the best solution is often to use `List<E>` over `E[]`, which might sacrifice some conciseness or performance, but provides better type safety and interoperability.

## Arrays

- Covariant

  - i.e. `Sub[]` is a subtype of `Super[]`, given `Sub` is a subtype of `Super`.

- Reification

  - i.e. element type is enforced at runtime.

    ```java
    Object[] a = new String[1];
    a[0] = 1; // throws ArrayStoreException
    ```

- Provides runtime type safety but not compile-time type safety.

## Generics

- Invariant

  - i.e. `List<Sub>` is **not** a subtype of `List<Super>`, even if `Sub` is a subtype of `Super`.

- Type Erasure

  - i.e. type constraints are enforced only at compile time and then *erased* at runtime.

  - Erasure is the reason why generics is backward-compatible with legacy code.

    ```java
    List<Object> l = new ArrayList<String>(); // incompatible type error
    l.add(1); // won't get here
    ```

  - Provides compile-time type safety but not runtime safety.

## Arrays and Generics

- Generic array creation is prohibited.

  - Why?

    ```java
    // Why generic array creation is illegal - won't compile
    List<String>[] a = new List<String>[1]; // should be a generic array creation error
    List<Integer> l = List.of(42);
    Object[] oa = a; // is allowed because arrays are covariant
    oa[0] = l; // is allowed because generic types are erased at runtime
    String s = s[0].get(0); // now we're in trouble
    ```

    - If generic array creation error is allowed, as opposed to getting a *compile error* in line 1, we encounter a `ClassCastException` at **runtime**, which is objectively worse.