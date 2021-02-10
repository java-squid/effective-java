# Item 66: Use native methods judiciously

## Key Points
- It is rarely advisable to use native methods.
    - Use the [Java process API](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/Process.html)
      to access OS processes and platform-specific facilities.
    - Native methods:
        - are not *safe*.
        - are less portable.
        - are harder to debug.
        - can *decrease* performance if not used correctly.
        - requires tedious "glue code".
- But correct usage *can* provide improved performance, e.g. using native
  libraries like GMP.

