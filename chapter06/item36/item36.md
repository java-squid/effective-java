# 열거 타입과 애너테이션 (6장)

## #36 : 비트 필드 대신 EnumSet을 사용하라

> 열거할 수 있는 타입을 한데 모아 집합 형태로 사용한다고 해도 비트 필드보다는 EnumSet을 사용하는 것이 여러 장점이 있다.

### 요약

- 비트별 OR를 사용해 여러 상수를 하나의 집합으로 모은 것을 비트 필드(bit filed)라고 한다.
  - `BOLD=0000 0001, ITALIC=0000 0010, UNDER_LINE=0000 0100, ...`
  - 비트 필드를 사용하면 비트별 연산을 사용해 합집합과 교집합 같은 집합 연산을 효율적으로 할 수 있다.
  - 비트 필드는 정수 열거 상수 단점을 그대로 지니며, 비트 필드값이 그대로 출력되면 해석하기가 어려운 단점도 있다.
- EnumSet 클래스를 이용하면 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현해준다.
- Set 인터페이스를 완벽히 구현하며, 타입 세이프하고 다른 어떤 Set 구현체와도 함께 사용할 수 있다.
- EnumSet의 유일한 단점이라면(Java 9 까지는) 불변 EnumSet을 만들 수 없다는 것이다.



## References

- Effective Java 3/E - Joshua Bloch
- [JavaSquid Issue-36](https://github.com/java-squid/effective-java/issues/36)



## 질문

- [JavaSquid Issue-36](https://github.com/java-squid/effective-java/issues/36)

  ![111](https://user-images.githubusercontent.com/58318041/101474529-a5849180-398e-11eb-8d4d-cf7405376559.png)

  ![222](https://user-images.githubusercontent.com/58318041/101474560-b03f2680-398e-11eb-95a2-5be6f168bda8.png)