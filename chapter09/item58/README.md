# 아이템58. 전통적인 for 문보다는 for-each문을 사용하라.

# 책정리

```java
아이템57
```

# 전통적인 for 문

```java
// 컬렉션
for (Iterator<Element> i = c.iterator(); i.hasNext(); ) {
		Element e = i.next();
		... // e Something...
}

// 배열
for (int i = 0; i < a.length; i++) {
		... // a[i] something...
}
```

전통적인 for문은 while문보다는 낫지만 가장 좋은 방법은 아니다. 반복자와 인덱스 변수 모두 코드를 지저분하게 할 뿐 진짜 필요한 건 원소들뿐이다.

더군다나 쓰이는 요소 종류가 늘어나면 오류가 생길 가능성이 높아진다. 1회 반복에서 반복자는 세 번 등장하며, 인덱스는 네 번이나 등장하여 변수를 잘못 사용할 틈새가 넓어진다.

혹시라도 잘못된 변수를 사용했을 때 컴파일러가 잡아주리라는 보장도 없다. 마지막으로, 컬렉션이냐 배열이냐에 따라 코드 형태가 상다잏 달라지므로 주의해야 한다.

# for-each 문

for-each 문의 정식 이름은 '향상된 for 문(enhanced for statement)'이다. 반복자와 인덱스 변수를 사용하지 않으니 코드가 깔끔해지고 오류가 날 일도 없다.

하나의 관용구로 컬렉션과 배열을 모두 처리할 수 있어서 어떤 컨테이너를 다루는지는 신경쓰지 않아도 된다.

```java
for (Element e : elements) {
		... // e something
}
```

여기서 콜론(:)은 "안의(in)" 라고 읽으면 된다.

→ elements 안의 각 원소 e에 대해 라고 읽는다.

반복 대상이 컬렉션이든 배열이든, for-each 문을 사용해도 속도는 똑같다. for-each 문이 만들어내는 코드는 사람이 손으로 최적화한 것과 같기 때문이다.

컬렉션을 중첩해 순회해야 한다면 for-each 문의 이점이 더욱 커진다.

# 반복문 실수

```java
enum Suit {CLUB, DIAMOND, HEART, SPADE}
enum Rank {ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
					 JACK, QUEEN, KING}
...
static Collection<Suit> suits = Arrays.asList(Suit.values());
static Collection<Rank> ranks = Arrays.asList(Rank.values());

List<Card> deck = new ArrayList<>();
for (Iterator<Suit> i = suits.iterator(); i.hasNext(); )
		for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
				deck.add(new Card(i.next(), j.next()));
```

여기서 문제는 바깥 컬렉션(suits)의 반복자에서 next 메서드가 너무 많이 불린다는 것이다.

마지막 줄의 i.next()를 보면 숫자(Suit) 하나당 한 번씩만 불려야 하는데, 안쪽 반복문에서 호출되는 바람에 '카드(Rank) 하나당' 한 번씩 불리고 있다.

→ 숫자가 바닥나면 반복문에서 `NoSuchElementException` 을 던진다.

### 주사위 모든 경우의 수

정말 운이 나빠서 바깥 컬렉션의 크기가 안쪽 컬렉션 크기의 배수라면 이 반복문은 예외를 던지지 않고 종료한다.

다음은 주사위를 두 번 굴렸을 때 나올 수 있는 모든 경우의 수를 출력하는 코드다.

```java
enum Face { ONE, TWO, THREE, FOUR, FIVE, SIX }
...
Collection<Face> faces = EnumSet.allOf(Face.class);

for (Iterator<Face> i = faces.iterator(); i.hasNext(); )
		for (Iterator<Face> j = faces.iterator(); j.hasNext(); )
				System.out.println(i.next() + " " + j.next());
```

이 프로그램은 예외를 던지지 않지만, 가능한 조합을 "ONE ONE" 부터 "SIX SIX" 까지 단 6쌍만 출력하고 끝나버린다. → 36개 조합이 나오지 않음.

이러한 문제들으 해결하려면 바깥 반복문에 바깥 원소를 저장하는 변수를 하나 추가해야 한다.

```java
for (Iterator<Suit> i = suits.iterator(); i.hasNext(); ) {
		Suit suit = i.next();
		for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); ) {
				deck.add(new Card(i.next(), j.next()));
		}
}
```

가장 간단한 방법은 for-each 문을 중첩하는 것으로 문제가 해결된다.

```java
for (Suit suit : suits) {
		for (Rank rank : ranks) {
				deck.add(new Card(suit, rank));
		}
}
```

# for-each 문을 사용할 수 없는 상황

### 파괴적인 필터링(destructive filtering)

컬렉션을 순회하면서 선택된 원소를 제거해야 한다면 반복자의 remove 메서드를 호출해야 한다. 자바 8부터는 Collection의 removeIf 메서드를 사용해 컬렉션을 명시적으로 순회하는 일을 피할 수 있다.

### 변형(transforming)

리스트나 배열을 순회하면서 그 원소의 값 일부 혹은 전체를 교체해야 한다면 리스트의 반복자나 배열의 인덱스를 사용해야 한다.

### 병렬 반복(parallel iteration)

여러 컬렉션을 병렬로 순회해야 한다면 각각의 반복자와 인덱스 변수를 사용해 엄격하고 명시적으로 제어해야 한다.

세가지 상황 중 하나에 속할 때는 일반적인 for 문을 사용하되 그 외에는 for-each 문을 사용하는 것을 권장한다.

for-each 문은 컬렉션과 배열은 물론 Iterable 인터페이스를 구현한 객체라면 무엇이든 순회할 수 있다.

Iterable 인터페이스는 다음과 같이 메서드가 단 하나뿐이다.

```java
public iterface Iterable<E> {
		// 이 객체의 원소들을 순회하는 반복자를 반환한다.
		Iterator<E> iterator();
}
```

# 결론

전통적인 for 문과 비교했을 때 for-each 문은 명료, 유연, 버그를 예방해준다. 성능 저하도 없다.

가능하면 for-each 문을 사용하자.