# Item 74: Document all exceptions thrown by each method

## Key Points
- `throws` keyword vs `@throws` Javadoc tag
    - `throws`
        - Checked exceptions must be caught and handled or propagated with the
          `throws` keyword.
        - Do *not* use the `throws` keyword to document unchecked exceptions.
    - `@throws`
        - Document precisely the conditions under which each exception can be
          thrown, both checked and unchecked.

