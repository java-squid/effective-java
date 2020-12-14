# 표준 함수형 인터페이스를 사용하라



## 질문

- 표준 함수형 인터페이스란?

  ```java
  @FunctionalInterface interface EldestEntryRemovalFunction<K,V> {
      ...
  }
  ```

  - 위와 같이 `@Functional..` 로 정의된 인터페이스를 말하는 듯

## 정리

- 6개 기본 인터페이스
  1. Operator
     - 인수 1개 --> `UnaryOperator<T>`
     - 인수 2개 --> `BinaryOperator<T>` 
     - 반환 값과 인수값이 같은 함수
     - 예시
       - `String::toLowerCase`
  2. Predicate
     - 인수 1개
     - 반환값이 boolean 타입
     - 예시
       - `Collections::isEmpty`
  3. Function
     - T를 받아 R을 리턴함.
     - 예시
       - `Arrays::asList`
  4. Supplier
     - 공급자
     - T를 받아서 뭔가하고 리턴하는 방식
  5. Consumer
     - 소비자
     - 뭔가 받길 하는데, 리턴하는 건 없고
- 표준 함수형 인터페이스는 **기본 타입**만 지원
  - 박싱된 기본 타입을 사용할 수 도있지만, 성능상 문제가 있을 수도 있기에 사용하지말자
- 함수형 인터페이스를 사용해야할 때, 만들지 말고 **이미 있는 것을 사용**하자
- 표준 함수형 인터페이스를 사용하는 이유는 람다에서 사용하기 위해서 (java 8과 함께 등장...)
  - 또한, 내부 추상 메서드는 하나로 제한 --> 컴파일 시에 오류를 잡아줄 수 있음.

## QnA
![image](https://user-images.githubusercontent.com/22140570/102083782-728f4180-3e57-11eb-9097-cc6a24f6156b.png)
- https://codechacha.com/ko/java8-functional-interface/
