# 아이템55. 옵셔널 반환은 신중히 하라

# 책정리

```java
아이템69
아이템71
아이템45
아이템67
```

# 자바8의 Optional

Optional<T>는 null이 아닌 T타입 참조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다.

옵셔널은 원소를 최대 1개 가질 수 있는 '불변' 컬렉션이다.

보통은 T를 반환해야 하지만 특정 조건에서는 아무것도 반환하지 않아야 할 때 T 대신 Optional<T>를 반환하도록 선언하면 된다. 그러면 유효한 반환값이 없을 때는 빈 결과를 반환하는 메서드가 만들어진다.

옵셔널을 반환하는 메서드는 예외를 던지는 메서드보다 유연하고 사용하기 쉬우며, null을 반환하는 메서드보다 오류 가능성이 작다.

```java
public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("빈 컬렉션");
        }
        
        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }
```

이 메서드를 다음처럼 수정이 가능하다.

```java
public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        if (c.isEmpty()) {
            return Optional.empty();
        }

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return Optional.of(result);
    }
```

적절한 정적 팩터리를 사용해 옵셔널을 생성해주기만 하면 된다. 이코드에서는 두 가지 팩터리를 사용했다.

빈 옵셔널은 Optional.empty()로 만들고, 값이 든 옵셔널은 Optional.of(value)로 생성했다.

Optional.of(value)에 null을 넣으면 NPE가 발생하니 주의해야 한다. null 값도 허용하는 옵셔널을 만들려면 Optional.ofNullable(value)를 사용하면 된다.

**옵셔널을 반환하는 메서드에서는 절대 null을 반환하지 말자.**

## 스트림에서의 옵셔널

스트림의 종단 연산 중 상당수가 옵셔널을 반환한다. 위 메서드를 스트림으로 작성하면 다음과 같다.

```java
public static <E extends Comparable<E>> Optional<E> streamMax(Collection<E> c) {
        return c.stream()
                .max(Comparator.naturalOrder());
    }
```

# 옵셔널 반환을 선택하는 기준

옵셔널은 검사 예외와 취지가 비슷하다.

→ 반환값이 없을 수도 있음을 API 사용자에게 명확히 알려준다.

비검사 예외를 던지거나 null을 반환한다면 API사용자가 그 사실을 인지하지 못할 수도 있다.

그러나 검사 예외를 던지면 클라이언트에서는 반드시 이에 대처하는 코드를 작성해야 한다.

## 옵셔널 활용

1. `orElse()` : 기본값 설정

2. ```
   orElseThrow()
   ```

    : 원하는 예외를 던짐

   - 실제로 발생하지 않는 예외 생성 비용은 들지 않음.

3. ```
   get()
   ```

    : 항상 값이 채워져 있다고 가정.

   - 기본값을 설정하는 비용이 아주 커서 부담이 될 때가 있다. 그럴 때 `orElseGet` 을 사용하면 값이 처음 필요할 때 Supplier<T>를 사용해 생성하므로 초기 설정 비용을 낮출 수 있다.

4. ```
   isPresent()
   ```

    : 옵셔널이 채워져 있으면 true, 비어 있으면 false를 반환.

   - 이 메서드의 상당수는 다른 메서드들로 대체할 수 있으며, 더 짧고 그 용도에 맞게 사용할 수 있음.

## 자바9에서는 stream() 추가

Optional을 Stream으로 변환해주는 어댑터다. 옵셔널에 값이 있으면 그 값을 원소로 담은 스트림으로, 값이 없다면 빈 스트림으로 변환한다.

이를 Stream의 flatMap 메서드와 조합하면 앞의 코드를 다음처럼 명료하게 바꿀 수 있다.

```java
streamOfOptionals
		.flatMap(Optional::stream)
```

# 옵셔널의 주의점

**컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 옵셔널로 감싸면 안된다.**

빈 Optional<List<T>>보다는 빈 List<T>를 반환하는 게 좋다.

그러나 [ProcessHandle.Info](http://ProcessHandle.Info) 인터페이스의 arguments 메서드는 Optional<String[]>을 반환하는데, 이는 예외적인 경우이니 따라하지 말 것.

## T 대신 Optional<T> 선언 기준

**결과가 없을 수 있으며, 클라이언트가 이 상황을 특별하게 처리해야 한다면 Optional<T>를 반환한다.**

그러나 Optional<T>를 반환하는 데는 대가가 따른다. Optional도 새로 할당하고 초기화해야 하는 객체이며, 그 안에서 값을 꺼내려면 메서드를 호출해야 하니 한 단계를 더 커치는 셈이다.

그래서 성능이 중요한 상황에서는 옵셔널이 맞지 않을 수 있다. 어떤 메서드가 이 상황에 처하는지 알아내려면 세심히 측정해보는 수밖에 없다.

## 전용 옵셔널 클래스

**OptionalInt, OptionalLong, OptionalDouble이 있으니 박싱된 기본 타입을 담은 옵셔널을 반환하는 일은 없도록 하자.**

## map과 flapMap을 이용한 연결

- map: Optional에 값이 존재한다면 주어진 람다 표현식 기반으로 값을 변경하고 리턴.
- flatMap: Optional에 값이 존재한다면 주어진 람다 표현식 기반으로 값을 변경하고 중첩된 Optional을 평면화하여 리턴.

map의 Optional 사용은 다음과 같다.

```java
int doubleLength = Optional.of("Hello World")
		.map(s -> s.length())
		.map(i -> i * 2)
		.get();
```

map은 함수가 처리한 결과값을 Optional 객체에 포함시켜 리턴한다. 만약 Optional 객체가 중첩되면 다른 메서드를 이용해서 계속 연결해서 사용하기 어렵다. flatMap의 경우 함수의 리턴 값이 Optional이라면 추가로 Optional 객체로 감싸지 않고 바로 리턴한다.

예제

```java
public class FlatMapExample {
    private String name;
		// 예시를 위한 Optional 필드사용
    private Optional<String> age;

    public FlatMapExample(String name, Optional<String> age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getAge() {
        return age;
    }

    public static void main(String[] args) {
        FlatMapExample ex = new FlatMapExample("Sunny", Optional.of("345"));
        Optional<FlatMapExample> optEx = Optional.of(ex);
        
        // map 이용
				// get()보다는 다른 메서드를 사용하자.
        Optional<Optional<String>> age = optEx.map(e -> e.getAge());
        System.out.println("map : " + age.get().get());
        
        // flatMap 이용 - 데이터 평면화
        Optional<String> flatAge = optEx.flatMap(e -> e.getAge());
        System.out.println("flatMap : " + flatAge.get());
    }
}
```