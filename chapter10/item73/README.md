# 아이템73. 추상화 수준에 맞는 예외를 던져라

# 책정리

```java
아이템20
```

# 추상화와 예외

메서드가 저수준 예외를 처리하지 않고 바깥으로 전파해버릴 때 당황스러울 것이다. 이는 내부 구현 방식을 드러내어 윗 레벨 API를 오염시킨다.

다음 릴리스에서 구현 방식을 바꾸면 다른 예외가 튀어나와 기존 클라이언트 프로그램을 깨지게 할 수도 있다.

**→ 상위 계층에서는 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외로 바꿔 던저야 한다.**

→ 예외 번역(exception translation)이라 한다.

```java
// 예외 번역
try {
		... // 저수준 추상화를 이용한다.
} catch (LowerLevelException e) {
		// 추상화 수준에 맞게 번역한다.		
		throw new HigherLevelException(...);
}
```

다음은 AbstractSequentialList에서 수행하는 예외 번역의 예다. AbstractSequentialLists는 List 인터페이스의 골격 구현이다. 이 예에서 수행한 예외 번역은 List<E> 인터페이스의 get 메서드 명세에 명시된 필수사항임을 기억해야 한다.

```java
public E get(int index) {
		ListIterator<E> i = listIterator(index);
		try {
				return i.next();
		} catch (NoSuchElementException e) {
				throw new IndexOutOfBoundsException("인덱스: " + index);
		}
}
```

예외를 번역할 때, 저수준 예외가 디버깅에 도움이 된다면 예외 연쇄(exception chaining)를 사용하는 게 좋다.

예외 연쇄란 문제의 근본 원인(cause)인 저수준 예외를 고수준 예외에 실어 보내는 방식이다. 그러면 별도의 접근자 메서드(Throwable의 getCause 메서드)를 통해 필요하면 언제든 저수준 예외를 꺼내 볼 수 있다.

```java
// 예외 연쇄
try {
		... // 저수준 추상화를 이용한다.
} catch (LowerLevelException cause) {
		// 저수준 예외를 고수준 예외에 실어 보낸다.
		throw new HigherLevelException(cause);
}
```

고수준 예외의 생성자는 (예외 연쇄용으로 설계된) 상위 클래스의 생성자에 이

```java
// 예외 연쇄용 생성자
class HigherLevelException extends Exception {
		HigherLevelException(Throwable cause) {
				super(cause);
		}
}
```

대부분의 표준 예외는 예외 연쇄용 생성자를 갖추고 있다. 그렇지 않은 예외라도 Throwable 의 initCause 메서드를 이용해 '원인'을 직접 못박을 수 있다. 예외 연쇄는 문제의 원인을 (getCause 메서드로) 프로그램에서 접근할 수 있게 해주며, 원인과 고수준 예외의 스택 추적 정보를 잘 통합해준다.

**예외 전파를 남발하는 것보다 예외 번역이 우수한 방법이지만, 그렇다고 해서 남용해서는 안된다.**

가능하다면 저수준 메서드가 반드시 성공하도록 하여 아래 계층에서는 예외가 발생하지 않도록 하는 것이 최선이다. 때론 상위 계층 메서드의 매개변수 값을 아래 계층 메서드로 건네기 전에 미리 검사하는 방법으로 이 목적을 달성할 수 있다.

## 차선책

아래 계층에서의 예외를 피할 수 없다면, 상위 계층에서 그 예외를 조용히 처리하여 문제를 API 호출자에게 까지 전파하지 않는 방법이 있다. 이 경우 발생한 예외는 java.util.logging 같은 적절한 로깅 기능을 활용하여 기록해두면 좋다. 그렇게 해두면 클라이언트 코드와 사용자에게 문제를 전파하지 않으면서도 프로그래머가 로그를 분석해 추가 조치를 취할 수 있게 해준다.