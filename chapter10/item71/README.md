# [아이템 71] 필요 없는 검사 예외 사용은 피하라

- 검사 예외 (Checked Exception)
- [Checked Exception 을 대하는 자세](https://cheese10yun.github.io/checked-exception/)

## Checked Exception 이 고통스러운 경우

- Checked Exception 을 던지는 메서드는 [스트림 안에서 직접 사용할 수 없다.](https://www.baeldung.com/java-lambda-exceptions)
- 메서드가 단 하나의 검사 예외만 던질 때 (sole checked exception)

## Checked Exception 을 피하는 방법

### 예외를 복구하기 어려울 때 -> RuntimeException

복구하기 어려울때는 RuntimeException 을 발생시키는게 속 편하다.

### 예외를 복구해야할 때

#### 1. Optional 을 반환하기(아이템 55)

단점: 예외가 발생한 이유를 알려주는 부가 정보를 담기 어려움

#### 2. 검사 예외를 던지는 메서드를 2개로 쪼개서 비검사 예외로 바꾸기

##### AS-IS

```java
try {
    obj.action(args);
} catch (CheckedException e) {
    handleException(e); // 예외 상황에 대처한다.
}
```

##### TO-BE

```java
if (obj.actionPermitted(args)) {
    obj.action(args);
} else {
    handleException(); // 예외 상황에 대처한다.
}
```

- 이 리팩터링이 적절치 않을 때
  - 멀티 쓰레드 환경에서 동기화 문제가 발생할 때
  - 외부 요인에 의해 actionPermitted 의 결과가 달라질 수 있을 때
  - actionPermitted 의 연산으로 인한 병목이 심할 때

그렇다면??

무식하게 아래처럼 해도 괜찮다. 어차피 실패 시 스레드가 중단되기 때문이다.

```java
obj.action(args);
```
