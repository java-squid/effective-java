# Comparable을 구현할지 고려하라

```
아이템34:
아이템10:
```

# 책정리

## Comparable 인터페이스의 유일무이한 메서드 compareTo()

compareTo()는 Object의 메서드가 아니다. compareTo() 단순 동치성 비교에 더해 순서까지 비교할 수 있으며, 제네릭하다. (타입을 지정이 가능하다)

Comparable 구현했다는 것은 그 클래스의 인스턴스들에는 자연적인 순서(natural order)가 있음을 뜻한다.

```java
Arrays.sort(a);
```

검색, 극단값 계산, 자동 정렬되는 컬렉션 관리도 가능하다.

다음은 명령줄 인수들을 알파벳순으로 출력한다.

```java
public class WordList{
		public static void main(String[] args) {
				Set<String> s = new TreeSet<>();
				Collections.addAll(s, args);
				System.out.printlns(s);
		}
}
```

자바 플랫폼 라이브러리의 모든 값 클래스와 열거 타입이 Comparable을 구현했다. 알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자.

```java
public interface Comparable<T> {
		int compareTo(T t);
}
```

## CompareTo()의 일반규약

객체의 순서를 비교한다. 이 객체가 주어진 객체보다 작으면 음의 정수를, 같으면 0을, 크면 양의 정수를 반환한다. 이 객체와 비교할 수 없는 타입의 객체가 주어지면 `ClassCastException` 을 던진다.

- Comparable을 구현한 클래스는 모든 x, y에 대해

```java
sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
```

x.compareTo(y)는 y.compareTo(x)가 예외를 던질 때에 한해 예외를 던져야 한다.

- Comparable을 구현한 클래스는 추이성을 보장해야 한다. 즉, (x.compareTo(y) > 0 && y.compareTo(z) > 0) 이면 x.compareTo(z) > 0 이다.
- Comparable을 구현한 클래스는 모든 z에 대해 x.compareTo(y) == 0 이면 sgn(x.compareTo(z)) == sgn(y.compareTo(z)) 다.
- 권고가 필수는 아니지만 (x.compareTo(y) == 0 ) == (x.equals(y)) 여야 한다. Comparable을 구현하고 이 권고를 지키지 않는 모든 클래스는 그 사실을 명시해야 한다.

> 이 클래스의 순서는 equals 메서드와 일관되지 않다.

모든 객체에 대해 전역 동치관계를 부여하는 equals 메서드와 달리, compareTo는 타입이 다른 객체를 신경쓰지 않아도 된다. 타입이 다른 객체가 주어지면 간단히 `ClassCastException` 을 던져도 된다.

비교를 활용하는 클래스의 예로는

- TreeSet, TreeMap(정렬된 컬렉션)
- Collections, Arrays(정렬 알고리즘을 활용하는 유틸리티 클래스)

## compareTo 규약 살펴보기

### 첫 번째 규약. 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다.

즉, 첫 번째 객체가 두 번째 객체보다 작으면, 두 번째가 첫 번째보다 커야 한다. 첫 번째가 두 번째와 크기가 같다면, 두 번째는 첫 번째와 같아야 한다. 마지막으로 첫 번째가 두 번째보다 크면, 두 번째는 첫 번째보다 작아야 한다.

### 두 번째 규약. 첫 번째가 두 번째보다 크고 두 번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야한다.

### 마지막 규약. 크기가 같은 객체들끼리는 어떤 객체와 비교해도 항상 같아야 한다.

→ 반사성, 대칭성, 추이성을 충족해야 할 것

## compareTo 우회법

Comparable을 구현한 클래스를 확장해 값 컴포넌트를 추가하고 싶다면, 확장하는 대신 독립된 클래스를 만들고, 이 클래스에 원래 클래스의 인스턴스를 가리키는 필드를 둘 것.

그 다음에 내부 인스턴스를 반환하는 '뷰' 메서드를 제공하면 된다.

→ 바깥 클래스에 원하는 compareTo 메서드를 구현해넣을 수 있다.

### 마지막 규약(크기가 같은 객체들은 어떤 객체와 비교해도 항상 같아야 한다.) 필수는 아니지만 꼭 지킬 것

마지막 규약은 간단히 말하면 compareTo 메서드로 수행한 동치성 테스트의 결과가 equals와 같아야 한다.

compareTo로 줄지은 순서와 equals의 결과가 일관되게 된다.

## compareTo 메서드 작성 요령

Comparable은 타입을 인수로 받는 제네릭 인터페이스이므로 compareTo 메서드의 인수 타입은 컴파일타임에 정해진다. 인수 타입을 확인하거나 형변환할 필요가 없음.

compareTo 메서드는 각 필드가 동치인지를 비교하는게 아니라 그 순사를 비교한다. 객체 참조 필드를 비교하려면 compareTo 메서드를 재귀적으로 호출한다.

Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야 한다면 비교자(Comparator)를 대신 사용한다. 비교자는 직접 만들거나 자바가 제공하는 것 중 골라 쓰면 된다.

예시) 아이템10 - 객체 참조 필드가 하나뿐인 비교자

```java
public final class CaseInsensitiveString implements Comparable<CaseInsensitiveString> {
		public int compareTo(CaseInsensitiveString cis) {
				return String.CASE_INSENSITIVE_ORDER.compare(s, cis.s);
		}
}
```

CaseInsensitiveString의 참조는 CaseInsensitiveString 참조만 비교할 수 있다는 뜻으로, Comparable을 구현할 때 일반적으로 따르는 패턴이다.

→ compareTo 메서드에서 관계 연산자 `<` 와 `>` 를 사용하는 이전 방식은 거추장스럽고 오류를 유발하니, 추천하지 않음.

### 핵심필드가 여러개라면

가장 핵심적인 필드부터 비교할 것. 비교 결과가 0이 아니라면, 즉 순서가 결정되면 거기서 끝.

똑같지 않은 필드를 찾을 때까지 그 다음으로 중요한 필드를 비교해나간다.

예시) 아이템10 - 기본 타입 필드가 여럿일 때의 비교자

```java
public int compareTo(PhoneNumber pn) {
		int result = Short.compare(areaCode, pn.areaCode); // 가장 중요한 필드
		if (result == 0) {
				result = Short.compare(prefix, pn.prefix); // 두 번째로 중요한 필드
				if (result == 0) {
						result = Short.compare(lineNum, pn.lineNum); // 세 번째로 중요한 필드
				}
		}
		return result;
}
```

## 자바8에서의 Comparator 인터페이스

비교자 생성 메서드와 팀을 꾸려 메서드 연쇄 방식으로 비교자를 생성할 수 있다. 이 비교자들을 Compaable 인터페이스가 원하는 compareTo 메서드를 구현하는 데 멋지게 활용할 수 있다.

but, 약간의 성능 저하가 뒤따른다.

예시) 아이템10 - 비교자 생성 메서드를 활용한 비교자

```java
private static final Comparator<PhoneNumber> COMPARATOR = 
		comparingInt((PhoneNumber pn) -> pn.areaCode)
				.thenComparingInt(pn -> pn.prefix)
				.thenComparingInt(pn -> pn.lineNum);

public int compareTo(PhoneNubmer pn) {
			return COMPARATOR.compare(this, pn);
}
```

비교자 생성 메서드 2개를 이용해 비교자를 생성했다.

첫 번째인 comparingInt는 객체 참조를 int 타입 키에 매핑하는 키 추출 함수(key extractor function)를 인수로 받아, 그 키를 기준으로 순서를 정하는 비교자를 반환하는 정적 메서드이다.

그 다음 comparingInt는 람다를 인수로 받으며, 람다는 PhoneNumber에서 추출한 지역 코드를 기준으로 전화번호의 순서를 정하는 Comparator<PhoneNumber> 를 반환한다.

두 번째 비교자 생성 메서드인 thenComparingInt를 수행할 때는 타입을 명시 하지 않아도 자바의 타입 추론 능력이 있어 추론할 수 있다. 그 뒤로 차례로 비교한다.

## Comparator

Comparator는 수많은 보조 생성 메서드들을 가지고 있다.

long과 double용으로는 comparingInt와 thenComparingInt의 변형 메서드를 준비했다.

short처럼 더 작은 정수 타입에는 int용 버전을 사용하면 된다.

객체 참조용 비교자 생성 메서드도 준비되어 있다.

### comparing이라는 정적 메서드 2개가 다중정의되어 있다.

첫 번째는 키 추출자를 받아서 그 키의 자연적 순서를 사용한다.

두 번째는 키 추출자 하나와 추출된 키를 비교할 비교자까지 총 2개의 인수를 받는다.

### 또한 thenComparing이란 인스턴스 메서드가 3개 다중정의 되어있다.

1. 비교자 하나만 인수로 받아 그 비교자로 부차 순서를 정한다.
2. 키 추출자를 인수로 받아 그 키의 자연적 순서로 보조 순서를 정한다.
3. 키 추출자 하나와 추출된 키를 비교할 비교자까지 총 2개의 인수를 받는다.

### Comparator 비교해보기

예시) 해시코드 값의 차를 기준으로 하는 비교자 - 추이성 위배!

```java
static Comparator<Object> hashCodeOrder = new Comparator<>() {
		public int compare(Object o1, Object o2) {
				return o1.hashCode() - o2.hashCode();
		}
}
```

이 방식은 정수 오버플로우를 일으키거나 부동소수점 계산 방식에 따른 오류를 낼 수 있다.

다음과 같은 방식 두가지 중 하나만을 사용할 것!

예시) 정적 compare 메서드를 활용한 비교자 // 비교자 생성 메서드를 활용한 비교자

```java
static Comparator<Object> hashCodeOrder = new Comparator<>() {
		public int compare(Object o1, Object o2) {
				return Integer.compare(o1.hashCode(), o2.hashCode(); // compare 사용
		}
}

static Comparator<Object> hashCodeOrder = 
		Comparator.comparingInt(o -> o.hashCode());
```

## 결론

순서를 고려해야 하는 값 클래스를 작성한다면 꼭 Comparable 인터페이스를 구현하여, 그 인스턴스들을 쉽게 정렬, 검색, 비교 기능을 제공하는 컬렉션과 어우러지게 해야 한다.

박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사용하자.

# Q&A

### Q1

> p89... 정렬된 컬렉션인 TreeSet과, TreeMap... 유틸리티 클래스인 Collections와 Arrays가 있다.

TreeSet, TreeMap, Collections, Arrays에 대해 간단하게 알아보면 좋을 것 같아요!뒷부분 유틸리티 클래스는 몇번 사용해본 적있지만, Tree 계열은 좀 생소하네요...

### A

Tree 계열은 데이터 입력의 순서를 보장해주는 Table 자료구조로 기존의 Set, Map에 비해 성능은 보장해주지 않습니다. 언어 Python에서는 ordereddict 라는 딕셔너리 자료구조와 비슷하다고 할 수 있습니다.

### Q2

compareTo, Comparator를 활용한 코드 예제를 통해 차이점을 알 수 있을까요?책만 읽으니까 두개의 차이점을 잘 모르겠어요.

### A

Comparator는 인터페이스이고 compareTo는 Comparable 인터페이스의 선언된 메서드입니다. 저는 주로 알고리즘에서 정렬을 커스텀하여 사용할 때 사용하는데요. 아래 간단한 예제를 볼게요.

```
public void sort() {
  Integer[] numbers = {5, 3, 2, 9, 7};
  Arrays.sort(numbers, new Comparator<Integer>() {
    @Override
    public int compare(Integer o1, Integer o2) {
      return o1.compareTo(o2);
    }
  });
}

// result
[2, 3, 5, 7, 9]
```

보시는 것처럼 배열에 Comparator의 인터페이스를 새로 구현 클래스로 생성하여 사용했습니다. Comparaor 인터페이스의 compare 메서드를 compareTo 메서드를 활용하여 재정의 하였구요. 아래는 동일한 코드를 람다식으로 보기좋게 변환한 코드입니다.

```
public void sort() {
  Integer[] numbers = {5, 3, 2, 9, 7};
  Arrays.sort(numbers, (o1, o2) -> o1.compareTo(o2));
}
```

한결 보기 좋아졌습니다. Comparator는 비교값들은 원하는 중요순위 순서로 정렬할 수 있어 활용도가 높습니다. 아래는 제가 Comparator를 학습하면서 풀었던 알고리즘 문제인데요. 풀어보시면 도움이 될 겁니다.

henry 블로그 : https://wooody92.github.io/algorithm/Algorithm-Comparator/