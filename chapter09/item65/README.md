# [아이템 65] 리플렉션보다는 인터페이스를 사용하라

## 요약

- 리플렉션은 인스턴스 생성에만 사용해라
- 리플렉션으로 만든 인스턴스는 인터페이스나 상위 클래스로 참조해 사용해라

## 리플렉션 이란?

- `java.lang.reflect` 패키지에서 제공하는 기능들
  - Constructor, Method, Field
  - [java.lang.Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)
- 리플렉션으로 가능한 것
  - Constructor, Method, Field 인스턴스 가져오기
  - 클래스의 멤버 이름, 필드 타입, 메서드 시그니처 가져오기
  - 연결된 생성자, 메서드 필드 조작하기
  - 조작 후 클래스의 인스턴스를 생성하거나, 메서드를 호출하거나, 필드에 접근 가능

## 리플렉션의 장점

- 컴파일 당시에 존재하지 않던 클래스를 이용가능
- 런타임에 존재하지 않을 수도 있는 클래스, 메서드, 필드와의 의존성을 관리할 때 적합
- 버전이 여러개 존재하는 외부 패키지를 다룰 때 유용

## 리플렉션의 단점

- 컴파일타임 때 가능한 타입 검사 및 예외 검사가 런타임으로 늦춰짐
- 코드가 지저분하고 장황해짐
- [성능이 떨어짐](https://yjacket.tistory.com/73)

## 리플렉션의 사용법

- 가장 오래된 패키지만을 지원하도록 컴파일 후, 새로운 버전의 패키지는 리플렉션으로 인스턴스 생성 후 접근
- 리플렉션으로 접근하는 클래스나 메서드가 런타임에 존재하지 않는 경우에 대한 예외처리 필요

## [백기선님 인프런 강의](https://www.inflearn.com/course/the-java-code-manipulation/dashboard)

### 리플렉션 사용시 주의해야할 사항

- 지나친 사용은 성능 이슈가 발생. (잘못 적용시)
- 컴파일 타임에 확인되지 않고 런타임 시에만 발생하는 문제를 만들 수 있다. (잘못 적용시)
- 접근 지시자를 무시할 수 있다. (의도적으로 한다면)

### 리플렉션의 활용 예시

- [Spring](https://sas-study.tistory.com/271)
  - DI, Annotation
  - MVC 에서 뷰에 데이터를 바인딩할 때
- 하이버네이트
  - `@Entity` 클래스에 Setter가 없을 시 리플렉션을 사용한다.
- [JUnit](https://junit.org/junit5/docs/5.0.3/api/org/junit/platform/commons/util/ReflectionUtils.html)

### 예시 코드와 실행 결과

```sh
> java Item65.java java.util.HashSet 1 2 3
[1, 2, 3]
```

```java
import java.util.*;
import java.lang.reflect.*;

public class Reflection {
    public static void main(String[] args) {
        // 클래스 이름을 Class 객체로 변환
        Class<? extends Set<String>> cl = null;
        try {
            cl = (Class<? extends Set<String>>) Class.forName(args[0]);
        } catch (ClassNotFoundException e) {
            fatalError("클래스를 찾을 수 없습니다.");
        }

        // 생성자를 얻는다.
        Constructor<? extends Set<String>> cons = null;
        try {
            cons = cl.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            fatalError("매개변수 없는 생성자를 찾을 수 없습니다.");
        }

        // 집합의 인스턴스를 만든다.
        Set<String> s = null;
        try {
            s = cons.newInstance();
        } catch (IllegalAccessException e) {
            fatalError("생성자에 접근할 수 없습니다.");
        } catch (InstantiationException e) {
            fatalError("클래스를 인스턴스화할 수 없습니다.");
        } catch (InvocationTargetException e) {
            fatalError("생성자가 예외를 던졌습니다: " + e.getCause());
        } catch (ClassCastException e) {
            fatalError("Set을 구현하지 않은 클래스입니다.");
        }

        //생상한 집합을 사용한다.
        s.addAll(Arrays.asList(args).subList(1, args.length));
        System.out.println(s);
    }

    private static void fatalError(String msg){
        System.err.println(msg);
        System.exit(1);
    }

    public static class CustomSet extends HashSet {}
}
```
