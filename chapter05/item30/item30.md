# 이왕이면 제네릭 메서드로 만들라

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
  Set<E> result = new HashSet<>(s1);
  result.addAll(s2);
  return result;
}
```

- `<E>` 는 타입 매개변수 목록 : 이 메서드에서 어떤 타입의 매개변수를 사용할 꺼나?
- `Set<E>` 는 반환 타입 : 이 메서드의 반환타입은 무엇이니?

- 이러한 제네릭 설정이 없었을 경우 unchecked 오류가 발생하는 이유?
  - 컴파일은 된다. 즉 코드 상으로 문제는 없지만..
  - 런타임에 ClassCastException 이 발생할 수 있다.
    - 고로 이러한 오류를 사전에 예방하기 위해 컴파일 시에 빡세게 검사하는 거라 보면 될듯.

- 제네릭 싱글턴 패턴
  - `Collections.reverseOrder`
  - `Collections.emptySet`

- 재귀적 타입 한정

  - 자기 자신이 들어간 표현식을 사용하여, 타입 매개변수의 허용범위를 한정시키는 것.

  ```java
  public static <E extends Comparable<E>> E max(Collection<E> c);
  ```

  - Element이 매개변수로 들어오는 데, 그 E는 `Comparable<E>`의 확장, 그러니까 정렬 검색등을 수행할 수 있다!



## 정리

- 제네릭을 왜 쓸까?
  - 입력 매개변수, 반환값에 제네릭을 쓴다.
  - 왜? 
  - 쓰지 않으면, 컴파일 시에 오류를 잡아내지 못한다 --> 런타임에 오류 발생.
  - 즉 안전성을 위해서 사용
- 타입이든, 메서드든 형변환이 필요한 경우에는 제네릭을 사용하자
