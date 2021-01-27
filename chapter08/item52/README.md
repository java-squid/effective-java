# Item 52: Use overloading judiciously

## Key Points
- Selection among overloaded methods is static, i.e. the choice which
  overloading to invoke is made at compile time.
- Avoid exporting two overloadings with the same number of parameters.
    - e.g. `writeInt(int)` and `writeDouble(double)` better than `write(int)`
      and `write(double)`
- Do not export two overloadings with parameters that are not "radically
  different", i.e. parameters that can be cast to each other, in the same
  position.
    - e.g. the overloadings `List.remove(Object)` and `List.remove(int)` are
      problematic because for `List<Integer> l` and `int i`,
      `l.remove(Integer.valueOf(i))` invokes the former whereas `l.remove(i)`
      invokes the latter.
- Do not overload methods to take different functional interfaces in the same
  argument position.
    - e.g. For `ExecutorService exec`, `exec.submit(System.out::println)` won't
      compile because there are two overloadings `submit(Runnable)` and
      `submit(Callable)`, and `System.out.println` is also overloaded.

- [Issue](https://github.com/java-squid/effective-java/issues/53)

