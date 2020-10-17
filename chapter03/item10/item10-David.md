# Obey the General Contract when Overriding Equals

## Properties of Equivalence Relations

- Reflexive - `x.equals(x)` must return true.

- Symmetric - if `x.equals(y)`, then `y.equals(x)`

  - e.g. implementing the `equals()` method of `CaseInsensitiveString` to interoperate with `String` is futile, because ((String) y).equals((CaseInsensitiveString) x) will **not work**.

- Transitive - if `x.equals(y)` and `y.equals(z)`, then `x.equals(z)`.

  - example that violates transitivity

    ```java
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false
        if (!(o instanceof ColorPoint))
            return o.equals(this)
        return super.equals(o) && ((ColorPoint) o).color == color;
    }
    
    ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
    Point p2 = new Point(1, 2);
    ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);
    
    p1.equals(p2); // true
    p2.equals(p3); // true
    p1.equals(p3); // false
    ```

  - In general, "[T]here is no way to extend an instantiable class and add a value component while preserving the equals contract."

  - "Favor composition over inheritance." (item 18)

  - It is possible to add a value component to a subclass of an **abstract** class without violating the equals contract. 

- Consistent - if `x.equals(y)` now, then `x.equals(y)` later if x and y are not modified.

  - e.g. if URL's equality depends on IP provided by DNS, update in DNS might violate consistency.

- Non-nullity - `x.equals(null)` must return false.

  - Using `instanceof` operator implicitly takes care of null, i.e. `o instanceof MyType` returns if `o` is null.

## Key Points

- "No class is an island." - John Donne - 
- "There is no way to extend an instantiable class and add a value component while preserving the equals contract."
- Just use Lombok (or AutoValue)...

## 