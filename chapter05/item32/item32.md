# Item 32: Combine generics and varargs judiciously

## Key Points

- When defining a method, prefer a `List` parameter over generic varargs parameter.
- When using a library, only use generic varargs methods with `@SafeVarargs` annotation.

------

## Heap Pollution

- What is it?
  - when a variable of a parameterized type refers to an object that is not of that type.
    - e.g. a variable of type `Generic<A>` refers to an object of type `Generic<B>`
- When can it happen?
  1. Unsafe use of generic varargs.
  2. Unsafe use of generics with arrays.
  3. Use of raw types.

## @SafeVarargs

- A generic varargs methods is safe if:
  1. it doesn't **store** anything in the varargs parameter array.
  2. it doesn't make the array (or a clone) **visible** to untrusted code.
- Use `@SafeVarargs` annotation on **every** method with a varargs parameter of a generic or parameterized type, i.e. **never write unsafe varargs methods**. 