# Item 23: Prefer Class Hierarchies to Tagged Classes

## Key Points

- Tagged classes are verbose, error-prone, and inefficient.
- Prefer class hierarchies to tagged classes.

## Tagged Class

- Tagged class is a class whose instances come in multiple flavors and contain a *tag* field indicating the flavor of the instance.
- The data and operations, i.e. the type, of a tagged class is dependent on the tag instead of dynamic subtyping; this results in many shortcomings:
  - As opposed to defining a new subclass, adding a new flavor is an extreme nuisance, because the tagged class has to be directly modified to make every switch statement handle another flavor.
  - Implementation details of each flavor are jumbled together into a single class, not only cluttering the source code, but also increasing memory footprint with unused members.
- Replace them with class hierarchies and subtyping as we know them.

