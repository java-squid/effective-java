# Item 63: Beware the performance of string concatenation

## Key Points
- For repeated string concatenation, use `StringBuilder` over the string
  concatenation opertator `+` to concatenate *n* strings of max length *k* in
  `O(n*k)` time as opposed to `O(n^2*k)` time.
- Sidenote: `StringBuilder` vs `StringBuffer`
    - `StringBuffer` is synchronized whereas `StringBuilder` is not, which
      means `StringBuilder` is faster, but not thread-safe.
    - Use `StringBuffer` for thread-safety and `StringBuilder` for performance.

[Issue](https://github.com/java-squid/effective-java/issues/65)

