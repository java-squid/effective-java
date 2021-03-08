# Item 75: Include failure-capture information in detail messages

## Key Points
- Detail message is an exception's string representation, i.e. the output of
  its `toString` method.
- Detail message of an exception should contain the values of all parameters
  and fields that contributed to the exception, excluding security-sensitive
  information like passwords or encryption keys.
    - e.g. `IndexOutOfBoundsException` with:
        1. the lower bound
        2. the upper bound
        3. the index value that failed to lie between the bounds.
        adequately captures the failure for debugging.
- Providing an appropriate *constructor* that requires failure-capture
  information is a good way to ensure informative detail messages.

