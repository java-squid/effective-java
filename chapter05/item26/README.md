# 아이템26. 로 타입은 사용하지 말라

# 책정리

```
아이템27
아이템28
아이템30
아이템31
```

# 제네릭 클래스, 인터페이스

클래스와 인터페이스 선언에 타입 매개변수가 쓰이면 제네릭 클래스 혹은 제네릭 인터페이스라 한다.

각각의 제네릭 타입은 일련의 매개변수화 타입(parameterized type)을 정의한다.

## 로 타입(Raw Type)

제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 때를 말한다. 로 타입은 타입 선언에서 제네릭 타입 정보가 전부 지워진 것처럼 동작하는데, 제네릭이 도래하기 전 코드와 호환되도록 하기 위한 궁여지책이다.

```java
private final Collection stamps = ...;

stamps.add(new Coin(...)); // "unchecked call" 경고를 내뱉는다.
```

오류는 가능한 한 발생 즉시,  이상적으로는 컴파일할 때 발견하는 것이 좋다. 런타임 때 발견한다면 런타임에 문제를 겪는 코드와 원인을 제공한 코드가 물리적으로 상당히 떨어져 있을 가능성이 커진다.

제네릭을 활용하면 이 정보가 주석이 아닌 타입 선언 자체에 녹아든다.

```java
private final Collection<Stamp> stamps = ...;
```

컴파일러는 컬렉션에서 원소를 꺼내는 모든 곳에 보이지 않는 형변환을 추가하여 절대 실패하지 않음을 보장한다.

### 로 타입은 왜 쓰는가?

로 타입을 쓰는 걸 언어 차원에서 막아 놓지는 않았지만 절대로 써서는 안 된다.

로 타입을 쓰면 제네릭이 안겨주는 안전성과 표현력을 모두 잃게 된다. 그렇다면 왜 만들었을까?

> 호환성 때문이다.

기존 코드를 모두 수용하면서 제네릭을 사용하는 새로운 코드와도 맞물려 돌아가게 하기때문에 동작이 가능하다.

마이그레이션 호환성을 위해 로 타입을 지원하고 제네릭 구현에는 소거 방식을 사용하기로 했다.

## 로 타입과 Object (매개변수화)

List같은 로 타입은 사용해서는 안 되나, List<Object> 처럼 임의 객체를 허용하는 매개변수화 타입은 괜찮다.

List는 제네릭 타입에서 완전히 발을 뺀 것이고, List<Object>는 모든 타입을 허용한다는 의사를 컴파일러에 명확히 전달한 것이다.

List<String>을 넘길 수 있지만, List<Object> 를 받는 메서드에는 넘길 수 없다.

→ 제네릭의 하위 타입 규칙때문이다.

예시)

```java
public class Main {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        unsafeAdd(strings, Integer.valueOf(42));
        String s = strings.get(0);
    }

    private static void unsafeAdd(List list, Object o) {
        list.add(o);
    }
}
// Exception in thread "main" java.lang.ClassCastException: 
// java.lang.Integer cannot be cast to java.lang.String
// at chapter05.item26.Main.main(Main.java:10)
```

컴파일은 되지만 strings.get(0)을 실행한다면 ClassCastException 을 던진다.

List를 매개변수화 타입인 List<Object>로 바꾼 다음 다시 컴파일 해보면

```java
				List<String> strings = new ArrayList<>();
        unsafeAdd(strings, Integer.valueOf(42));
        String s = strings.get(0);
    }
```

unsafeAdd()에서 컴파일이 안된다.

### 원소 타입을 몰라도 되는 방식

```java
static int numElementsInCommon(Set s1, Set s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1)) {
                result++;
            }
        }
        return result;
    }
```

이 메서드 방식은 동작하지만 로 타입을 사용하여 안전하지 않다. 따라서 비한정적 와일드카드 타입(unbounded wildcard type)을 대신 사용하는게 좋다.

제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않다면 물음표(?)를 사용할 것.

어떤 타입이라도 담을 수 있는 가장 범용적인 매개변수화 Set타입은 `Set<?>` 이다.

## 와일드카드 타입과 로 타입

와일드카드 타입은 안전하고, 로 타입은 안전하지 않다. 로 타입 컬렉션에는 아무 원소나 넣을 수 있으니 타입 불변식을 훼손하기 쉽다.

반면, Collection<?> 에는 (null 외에는) 어떤 원소도 넣을 수 없다. 다른 원소를 넣으려 하면 컴파일할 때 오류메시지를 발견하게 될 것이다.

## 로타입의 소소한 예외

### 1. class 리터럴에는 로 타입을 써야 한다.

자바 명세는 class 리터럴에 매개변수화 타입을 사용하지 못하게 했다.(배열과 기본타입은 허용) List.class, String[].class, int.class는 허용하고 List<String>.class와 List<?>.class는 허용하지 않는다.

### 2. instanceof 연산자와 관련이 있다.

런타임에는 제네릭 타입정보가 지워지므로 instanceof 연산자는 비한정적 와일드카드 타입 이외의 매개변수화 타입에는 적용할 수 없다. 로 타입이든 비한정적 와일드카드 타입이든 instanceof는 완전히 똑같이 동작한다.

예시)

```java
if (o instanceof Set) {    // 로 타입
		Set<?> s = (Set<?>) o; // 와일드카드 타입
}
```

→ o의 타입이 Set임을 확인한 다음 와일드카드 타입인 Set<?>로 형변환해야 한다.

## 결론

로 타입을 사용하면 런타임에 예외가 일어날 수 있으니 사용하면 안 된다.

Set<Object>는 어떤 타입의 객체도 저장할 수 있는 매개변수화 타입이다.

Set<?> 는 모종의 타입 객체만 저장할 수 있는 와일드카드 타입이다.

로 타입인 Set은 제네릭 타입 시스템에 속하지 않는다.





# Q&A

### Q. 런타임에는 제네릭 정보가 지워지는 걸까

### A.

- Why?

  - 파고 들어가자면 밑도 끝도 없이 어려운 주제...
  - Joshua Bloch이 언급한 이유는 Java 5 이전에 존재하던 raw type에 대한 하위 호완성을 유지하기 위함.
    - e.g. `List l = new ArrayList();`를 컴파일 에러로 취급하지 않고 경고만 하고 넘어가기.

- What / How?

  - Type Erasure?

    - 컴파일 전

      ```
        public class Node<T> {
      
            private T data;
            private Node<T> next;
      
            public Node(T data, Node<T> next) {
                this.data = data;
                this.next = next;
            }
      
            public T getData() { return data; }
            // ...
        }
      ```

    - Type erasure 후 (컴파일 중)

      ```
        public class Node {
      
           private Object data;
           private Node next;
      
            public Node(Object data, Node next) {
                this.data = data;
                this.next = next;
            }
      
            public Object getData() { return data; }
            // ...
        }
      ```

    - type paramter `T`가 `Object`로 바뀌어있는 것을 확인할 수 있다.

    - 더 자세한 내용은 [Erasure of Generic Types](https://docs.oracle.com/javase/tutorial/java/generics/genTypes.html)

  - ```
    Collection<Supertype>
    ```

    은

     

    ```
    Collection<Subtype>
    ```

    의 상위 클래스가 아니다!

    - 왜? `Collection<Animal>`이 `Collection<Dog>`의 상위 클래스면 `Collection<Dog>`에 `Cat`을 추가할 수 있게 된다...

    ```
    // Illegal code - because otherwise life would be Bad
    List<Dog> dogs = new ArrayList<Dog>(); // ArrayList implements List
    List<Animal> animals = dogs; // Awooga awooga
    animals.add(new Cat());
    Dog dog = dogs.get(0); // This should be safe, right?
    ```

    - 출처: [StackOverflow](https://stackoverflow.com/a/2745301/10709152)

  - 에러 예시

    ```
    class A { /* ... */ }
    class B extends A { /* ... */ }
    B b = new B();
    A a = b;
    List<B> lb = new ArrayList<>();
    List<A> la = lb;   // compile-time error
    ```

    - 출처: [Wildcards and Subtyping](https://docs.oracle.com/javase/tutorial/java/generics/subtyping.html)

  - 그렇기 때문에 와일드카드를 써야한다.

    ```
    void f(Set<?> s) {}
    void g(Set<Object> s) {}
    Set<String> s = new HashSet<>();
    f(s);
    g(s); // compile-time error
    ```

  - 더 자세한 내용은: [Wildcards and Subtyping](https://docs.oracle.com/javase/tutorial/java/generics/subtyping.html)

### 스터디

- 다양한 소스를 참고하자. (자바봄 뿐만 아니라, 다른 소스들도 확인하자)
- 런타임에 제네릭 정보가 지워지는 것은 Java 5 이전에, 하위 호완성을 유지하기 위함.
- Void 객체