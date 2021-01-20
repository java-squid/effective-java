# 아이템29. 이왕이면 제네릭 타입으로 만들라

# 책정리

```
아이템68
아이템28
아이템27
아이템32
아이템61
```

# 제네릭 타입과 메서드

제네릭 타입과 메서드를 사용하는 일은 쉬운 편이지만 새로 만드는 일은 조금 더 어렵다.

일반 클래스를 제네릭 클래스로 만드는 첫 단계는 클래스 선언에 타입 매개변수를 추가하는 일이다. 이 때 타입 이름으로는 보통 E를 사용한다.

```java
public class Stack<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new E[DEFAULT_INITIAL_CAPACITY]; // 컴파일 에러
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        E result = elements[--size];
        elements[size] = null; // 다 쓴 객체 해제
        return result;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

E와 같은 실체화 불가 타입으로는 배열을 만들 수 없다. 배열을 사용하는 코드를 제네릭으로 만들려 할 때는 이 문제가 항상 발생한다.

## 해결책

### 1. 제네릭 배열 생성을 금지하는 제약을 대놓고 우회하는 방법

Object 배열을 생성한 다음 제네릭 배열로 형변환한다. 컴파일러는 오류 대신 경고를 내보낼 것이다.

```java
    @SuppressWarnings("unchecked")
		public Stack() {
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }
```

비검사 형변환이 안전함을 직접 증명했다면 범위를 최소로 좁혀 `@SuppressWarnings` 애너테이션으로 해당 경고를 숨긴다. 애너테이션을 달면 Stack은 깔끔히 컴파일되고, 명시적으로 형변환하지 않아도 ClassCastException 걱정없이 사용할 수 있게 된다.

### 2. elements 필드의 타입을 E[] 에서 Object[]로 바꾸는 것이다.

다른 오류가 발생한다. 캐스팅하여 사용한다면

```java
@SuppressWarnings("unchecked") E result = (E) elements[--size]; // unchecked cast
```

E는 실체화 불가 타입이므로 컴파일러는 런타임에 이뤄지는 형변환이 안전한지 증명할 방법이 없다. 여기서도 비검사 형변환을 수행하는 할당문에서만 숨기면 된다.

보통 현업에선 첫 번째 방식을 더 선호한다. 첫 번째는 형변환을 배열 생성시 단 한번만 해주면 되지만, 두 번째 방식에서는 배열에서 원소를 읽을 때마다 해줘야 한다.

하지만 E가 Object가 아닌 한 배열의 런타임 타입이 컴파일타임 타입과 달라 힙 오염을 일으킨다. 힙 오염이 걱정되면 두 번째 방식을 고수하기도 한다.

## 제네릭 Stack을 이용한 프로그램

```java
public class Main {
    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        for (String arg : args) {
            stack.push(arg);
        }
        while (!stack.isEmpty()) {
            System.out.println(stack.pop().toUpperCase());
        }
    }
}
```

사실 제네릭 타입 안에서 리스트를 사용하는게 항상 가능하지도, 꼭 더 좋은 것도 아니다. 자바가 리스트를 기본 타입으로 제공하지 않으므로 ArrayList 같은 제네릭 타입도 결국은 기본 타입인 배열을 사용해 구현해야 한다.

HashMap 같은 제네릭 타입은 성능을 높일 목적으로 배열을 사용하기도 한다.

Stack 예시처럼 대다수의 제네릭 타입은 타입 매개변수에 아무런 제약을 두지 않는다. Stack<Object>, Stack<int[]>, Stack<List<String>>, Stack 등 어떤 참주 타입으로도 Stack을 만들 수 있다.

단, 기본타입(primitive type)은 사용할 수 없다. Stack<int>를 만들려고 하면 컴파일 올가 난다. 자바 제네릭 타입 시스템의 근본적인 문제이며, 박싱된 기본 타입을 사용해 우회할 수 있다.

## 타입 매개변수에 제약을 두는 제네릭 타입

ex) java.util.concurrent.DelayQueue는 다음처럼 선언된다.

```java
class DelayQueue<E extends Delayed> implements BlockingQueue<E>
```

타입 매개변수 목록인 <E extends Delayed> 는 java.util.concurrent.Delayed의 하위 타입만 받는다는 뜻이다. 이렇게하여 DelayQueue 자신과 DelayQueue를 사용하는 클라이언트는 DelayQueue의 원소에서 형변환 없이 곧바로 Delayed 클래스의 메서드를 호출할 수 있다.

이러한 타입 매개변수 E를 한정적 타입 매개변수(bounded type parameter)라 한다.

모든 타입은 자기 자신의 하위 타입이므로 DelayQueue<Delayed>로도 사용할 수 있다.

## 결론

클라이언트에서 직접 형변환해야 하는 타입보다 제네릭 타입이 더 안전하고 쓰기 편하다.

→ 새로운 타입을 설계할 때는 형변환 없이도 사용할 수 있도록 할 것. 그렇게 하려면 제네릭 타입으로 만들어야 한다.

기존 타입 중 제네릭이 아니라면 제네릭 타입으로 변경하자. 기존 클라이언트에는 아무 영향을 주지 않으면서, 새로운 사용자를 훨씬 편하게 해주는 길이다.



### 스터디

`Stack<int>`, `Stack<double>` 왜 못하게 막아놨을까?

- Java 시스템의 기본적인 문제라 생각되지만....

1. int, double type을 구분할 수 없어서..?
2. Generic이라는 게 Object 하위만 판단..?

