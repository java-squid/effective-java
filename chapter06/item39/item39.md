# 열거 타입과 애너테이션 (6장)

## #39 : 명명 패턴보다 애너테이션을 사용하라

> 애너테이션으로 할 수 있는 일을 명명 패턴으로 처리할 이유는 없다. 일반 프로그래머가 애너테이션 타입을 직접 정의할 일은 거의 없지만, 자바가 제공하는 애너테이션 타입들을 적절히 활용해야 한다.

### 요약

- 변수, 메서드의 이름을 일관된 방식으로 작성하는 패턴을 **명명 패턴**이라고 한다.

- 명명 패턴을 사용하면 아래의 단점들이 있으므로 애너테이션 사용을 권장한다.

  - 오타 발생 시 문제가 발생한다.
  - 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다. (특정 예외를 던져야 성공하는 테스트 등)

- **마커 애너테이션**

  - 아무 매개변수 없이 단순히 대상에 마킹(marking)한다.

  - 예시 : `@Test`

    ```java
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Test{}
    ```

  - @Retention : 보존정책, 해당 애너테이션이 런타임에도 유지되어야 한다는 표시이다.

  - @Target : 적용대상, 해당 애너테이션의 적용대상을 제한한다.(여기서는 메서드에서만 사용하도록 제한) 

- 위처럼 애너테이션 선언에 다는 애너테이션을 **메타애너테이션**(meta-annotation)이라고 한다.

- 애너테이션은 자바의 리플렉션을 사용하여 구현한다.

- `@Repeatable` : 반복, 여러 개의 값을 받을 때 배열 대신 사용한다.



## References

- Effective Java 3/E - Joshua Bloch
- [JavaSquid Issue-39](https://github.com/java-squid/effective-java/issues/39)
- [Javabom](https://javabom.tistory.com/78)



## 질문

- [JavaSquid Issue-39](https://github.com/java-squid/effective-java/issues/39)

  ![111](https://user-images.githubusercontent.com/58318041/101873935-3e9fec00-3bcb-11eb-8c2c-441dee985865.png)