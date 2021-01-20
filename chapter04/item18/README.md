# Item 18: Favor composition over inheritance

## Problems of Using Inheritance

- **Inheritance violates encapsulation**, because a subclass depends on the implementation details of its super class.
- Implementation details of a super class:
  - can contain obscure, undocumented "self-use" e.g. `HashSet`'s `addAll()` method internally uses `add()`.
  - can contain optimizations that are hard to reimplement.
  - can rely on private members that are inaccessible to subclasses.
  - are subject to change in the future.

## Advantages of the *Wrapper Class* pattern

- A wrapper class has **no dependencies** on the implementation details of a composed class.
- In addition to being robust, it is also **flexible**, e.g. `ForwardingSet` can be used with any `Set` implementation.
- Also known as the *Decorator* pattern.

