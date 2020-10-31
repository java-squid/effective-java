# Always override `toString`

- The `toString` method is automatically invoked by `printf`, string concatenation, and debuggers, so it should be overriden even if *you* never call it explicitly.
- The general contract for `toString` - the returned string should be:
  1. concise
  2. informative
  3. easy to read
- A good `toString` implementation makes your class
  1. easier to use
  2. **easier to debug**
- Format specification?
  - Specifying the format:
    - Pros
      - The string can serve as  a "standard, unambiguous, human-readable representation of the object."
        - e.g. "111-222-3333" <-> `PhoneNumber` object with area code = 111, prefix = 222, line number = 3333
      - The availability of complementing static factories / constructors.
        - e.g. `Integer.valueOf(String s)`
      - Suit well with *value classes*, e.g. phone number, because of the reasons mentioned above.
    - Cons
      - Specifying the format and then changing it in the future will break code that depends on the format.
  - Not specifying the format:
    - Pros
      - Flexibility to add information or improve the format.
    - Cons
      - The advantages of specifying the format, static factories, are lost.
- Provide programmatic access to the information contained in the value returned by `toString`.
  - i.e. provide getters for such fields.
  - Why? else the information has to *parsed* instead of just accessed.

