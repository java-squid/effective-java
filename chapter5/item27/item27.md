# 비검사 경고를 제거하라

## 비검사 경고란?

- 비검사 경고에는
  - 형변환 경고, 메서드 호출 경고, 타입 경고, 변환 경고등이 있음.
- 제네릭과 관련된 경고, 비검사 경고를 제거하면 해당 코드는 타입 안정성이 보장된다고 볼 수 있음.
  - runtime에 ClassCastException이 발생하지 않는다!
- 비검사 경고를 제거할 수 없지만, 안전하다고 확신되면 `@Suppress Warning("unchecked")`  애노테이션을  달아서 경고를 숨기자.
  - 해당 애노테이션은 항상 좁은 범위에 적용시키자.
  - 절대로 클래스 전체에 적용시키지 말자



## 정리

- 비검사 경고는 ClassCastException 같이, 그대로 실행해도 문제는 없지만 런타임시 잠재적으로 에러가 발생할 수 있는 부분을 말한다.

- 제거할 수 없지만, 코드 상 안전하다는 확신이 있으면 `@Suppress Warning("unchecked")` 어노테이션을 붙이자.
  - 이 어노테이션은 가능한 좁은 범위에 붙이자 (e.g 매개변수, 함수 선언등등..)
  - 또한, 왜 사용하였는 지 주석을 남겨놓자
  
## QnA

### Q1 제거할 수 없는 경고는 왜 생길까?
제거할 수 없는 경고는 왜 생길까에 대해서 고민을 하면서 예시를 짜봤습니다.
```java
class MyStack<E> {
    private static final int DEFAULT_SIZE = 16;

    private Object[] elements;
    private int i;

    public MyStack() {
        elements = new Object[DEFAULT_SIZE];
        i = 0;
    }

    public void push(E element) {
        elements[i++] = element;
    }

    public E pop() {
        @SuppressWarnings("unchecked")
        E element = (E) elements[--i];
        return element;
    }

    private void resize(double factor) {
        int newSize = (int) (elements.length * factor);
        elements = new Object[newSize];
        // ... copying logic omitted
    }
}
```
- ArrayList를 비롯한 많은 Collection 인터페이스 구현체들은 기본적으로 내부적으로 배열을 사용하고 있습니다. 
- 위 예시를 보면 `elements`가 `Object[]`로 선언이 되어 사용되기 때문에 `pop()` 메소드에서 `@SuppressedWarnings("unchecked")` 어노테이션이 사용이 됐습니다.
- 그러면 처음부터 `elements`를 `Object[]`가 아닌 `E[]`로 선언하여 사용할 수는 없었을까? 라는 궁금증이 들어서 (자바봄 예시에서도 그렇게 선언이 되어있던데...) 여러가지 시도를 해보았습니다.

```java
private E[] elements;

public MyStack() {
    elements = new E[DEFAULT_SIZE]; // generic array creation error
    // ...
}
```
- 일단 이 방법은 제너릭 배열 생성 **에러**가 발생하기 때문에 사용할 수 없습니다.

```java
private E[] elements;

public MyStack() {
    @SuppressWarnings("unchecked")
    elements = (E[]) new Object[DEFAULT_SIZE];
    // ...
}
``` 
- 이것도 **에러**입니다. `@SuppressWarnings` 어노테이션은 변수, 함수, 혹은 클래스를 *선언*할 때만 사용할 수 있습니다. 
- 위 예시는 변수 선언이 아닌 대입*문*입니다 (if 문, while 문의 그 문입니다).

```java
private E[] elements;

@SuppressWarnings("unchecked")
public MyStack() {
    elements = (E[]) new Object[DEFAULT_SIZE];
    // ...
}
``` 
- 차선책으로 경고 억제를 함수 선언 위에 붙이는 방법이 있는데 가장 좁은 스코프에서 경고 억제를 하라는 원칙을 지키지 못 하게 됩니다.

```java
@SuppressWarnings("unchecked")
private E[] elements = (E[]) new Object[DEFAULT_SIZE];

public MyStack() {
    // ...
}
```
- 이번에는 변수 선언에 어노테이션을 붙여서 에러가 발생하지 않습니다 (킹치웠나?)
- 하지만 `MyStack<E>` 객체는 `resize()`라는 메소드를 구현해야 합니다.

```java
@SuppressWarnings("unchecked")
private void resize(double factor) {
    // ...
    elements = (E[]) new Object[newSize];
    // ...
}
```
- 제너릭 배열은 생성이 불가능하기 때문에 이번에도 캐스팅을 해줘야 하는데 마찬가지로 선언이 아닌 대입문이기 때문에 경고 억제를 가장 좁은 스코프에서 하지 못하고 메소드 선언에서 해야만 합니다. 이상적이지는 않죠.

> 결론
- 제너릭과 배열을 같이 사용하는 경우에는 비검사 경고가 불가피하게 발생하게 된다.
- 내부적으로 사용하는 배열이 `final`한 경우를 제외하고는 그 배열의 정적 타입을 `Object[]`로 선언하는 것이 가장 좁은 스코프에서 비검사 경고 억제를 하라는 원칙을 지킬 수 있는 방법이다.

### Etc..
![image](https://user-images.githubusercontent.com/22140570/99970092-b6ee6b00-2dde-11eb-84b2-ecac5b5578bc.png)
