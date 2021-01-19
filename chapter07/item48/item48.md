# 아이템48. 스트림 병렬화는 주의해서 적용하라

# 책정리

```sql

```

# 병렬

- 자바 5: java.util.concurrent 라이브러리, 실행자(Executor) 프레임워크
- 자바 7: 고성능 병렬 분해 프레임워크(포크-조인:fork-join) 패키지

자바 8부터는 parallel 메서드만 한 번 호출하면 파이프라인을 병렬 실행할 수 있는 스트림을 지원한다.

동시성 프로그래밍을 할 때는 안전성(safety)과 응답 가능(liveness) 상태를 유지하기 위해 애써야 한다.

```java
// 스트림을 사용해 처음 20개의 메르센 소수를 생성하는 프로그램
public static void main(String[] args) {
		primes().map(p -> TWO.pow(p.intValueExact()).subTract(ONE))
				.filter(mernsenne -> mersenne.isProbablePrime(50))
				.limit(20)
				.forEach(System.out::println);
}

static Stream<BigInteger> primes() {
		return Stream.iterate(TWO, BigInteger::nextProbalePrime);
}
```

만약 이 코드에서 parellel()를 호출 한다면, 이 프로그램은 강제 종료할 때까지 아무 결과도 출력하지 않는다.

# 파이프라인 병렬화

데이터 소스가 Stream.iterate거나 중간 연산으로 limit를 쓰면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다. 파이프라인 병렬화는 limit를 다룰 때 CPU 코어가 남는다면 원소를 몇 개 더 처리한 후 제한된 개수 이후의 결과를 버려도 아무런 해가 없다고 가정한다.

그런데 이 코드의 경우 새롭게 메르센 소수를 찾을 때마다 그 전 소수를 찾을 때마다 두 배 정도 더 오래 걸린다.

원소 하나를 계산하는 비용이 대략 그 이전까지의 원소 전부를 계산한 비용을 합친 것만큼 든다는 뜻이다. 그래서 이 파이프라인은 자동 병렬화 알고리즘이 제 기능을 못하게 마비시킨다.

→ 스트림 파이프라인을 마구잡이로 병렬화하면 안된다.

# 병렬화의 효과를 받을 수 있는 스트림 소스

ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int 범위, long 범위일 때 병렬화의 효과가 가장 좋다.

→ 다수의 스레드에 분배하기에 좋다는 특징을 가진다.

이 자료구조들의 공통점은 원소들을 순차적으로 실행할 때의 참조 지역성(locality of reference)이 뛰어나다는 것이다. 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻이다. 하지만 참조들이 가리키는 실제 객체가 메모리에서 서로 떨어져 있을 땐, 참조 지역성이 나빠진다.

참조 지역성이 낮으면 스레드는 데이터가 주 메모리에서 캐시 메모리로 전송되어 오기를 기다리며 대부분 시간을 멍하니 보내게 된다.

→ 참요 지역성은 다량의 데이터를 처리하는 벌크 연산을 병렬화할 때 아주 중요한 요소로 작용한다.

참조 지역성이 가장 뛰어난 자료구조는 기본 타입의 배열이다. 기본 타입 배열에서는 (참조가 아닌) 데이터 자체가 메모리에 연속해서 저장되기 때문이다.

## 스트림 파이프라인의 종단 연산의 동작 방식과 병렬 수행

종단 연산에서 수행하는 작업량이 파이프라인 전체 작업에서 상당 비중을 차지하면서 순차적인 연산이라면 파이프라인 병렬 수행의 효과는 제한될 수 밖에 없다. 종단 연산 중 병렬화에 가장 적합한 것은 축소(reduction)이다.

축소는 파이프라인에서 만들어진 모든 원소를 하나로 합치는 작업으로, Stream의 reduce 메서드 중 하나, 혹은 min, max, count, sum 같이 완성된 형태로 제공하는 메서드 중 하나를 선택해 수행한다.

anyMatch, allMatch, noneMatch처럼 조건에 맞으면 바로 반환되는 메서드도 병렬화에 적합하다.

반면, 가변 축소(mutable reduction)을 수행하는 Stream의 collect 메서드는 병렬화에 적합하지 않다. 컬렉션들을 합치는 부담이 크기 때문이다.

## 스트림 병렬화

스트림을 잘못 병렬화하면 성능이 나빠질 뿐만 아니라 결과 자체가 잘못되거나 예상 못한 동작이 발생할 수 있다.

결과가 잘못되거나 오동작하는 것은 안전 실패(safety failure)라 한다. 안전 실패는 병렬화한 파이프라인이 사용하는 mappers, filters, 혹은 프로그래머가 제공한 다른 함수 객체가 명세대로 동작하지 않을 때 벌어진다.

Stream 명세는 이때 사용되는 함수 객체에 관한 규약을 정의해놨다.

Strema의 reduce 연산에 건네지는 accumulator, combiner 함수는 반드시 결합법칙을 만족하고, 간섭받지 않고, 상태를 갖지 않아야 한다.

출력 순서를 순차 버전처럼 정렬하고 싶다면 종단 연산 forEach를 forEachOrdered로 바꿔주면 된다. 이 연산은 병렬 스트림들을 순회하며 소수를 발견한 순서대로 출력되도록 보장해줄 것이다.

### 스트림 병렬화를 해야하나?

스트림 병렬화는 오직 성능 최적화 수단이다. 다른 최적화와 마찬가지로 변경 전후로 반드시 성능을 테스트하여 병렬화를 사용할 가치가 있는지 확인해야 한다. 보통은 병렬 스트림 파이프라인도 공통의 포크-조인풀에서 수행되므로(같은 스레드 풀을 사용함), 잘못된 파이프라인 하나가 시스템의 다른 부분의 성능까지 악영향을 줄 수 있다.

### 스트림 병렬화 예시

조건이 잘 갖춰지면 parallel 메서드 호출 하나로 거의 프로세서 코어 수에 비례하는 성능 향상을 만끽할 수 있다.

다음은 π(n), 즉 n보다 작거나 같은 소수의 개수를 계산하는 함수다.

```java
// 소수 계산 스트림 파이프라인 - 병렬화에 적합하다.
static long pi(long n) {
		return LongStream.rangeClosed(2, n)
				.mapToObj(BigInterger::valueOf)
				.filter(i -> i.isProbablePrime(50))
				.count();
}

// parallel() 추가
static long pi(long n) {
		return LongStream.rangeClosed(2, n)
				.parallel()
				.mapToObj(BigInteger::valueOf)
				.filter(i -> i.isProbablePrime(50))
				.count();
}
```

parallel()을 추가하면 병렬화 덕분에 더 빨라졌다. 만약 n이 크다면 레머의 공식이라는 알고리즘을 활용하자.

무작위 수들로 이뤄진 스트림을 병렬화하려거든 ThreadLocalRandom 보다는 SplittableRandom 인스턴스를 이용하자. 병렬화할 시 성능이 선형으로 증가한다. ThreadLocalRandom은 단일 스레드에서 쓰고자 만든것. 병렬 스트림용으로는 사용할 수 있지만 SplittableRandom보다는 느릴 것이다.

Random은 모든 연산을 동기화하기 때문에 병렬 처리하면 최악의 성능을 보일 것이다.





## 이슈 정리 및 Q&A

### 담당자 의견

[https://www.popit.kr/java8-stream%EC%9D%98-parallel-%EC%B2%98%EB%A6%AC/](https://www.popit.kr/java8-stream의-parallel-처리/)

### Stream parallel을 실제 환경에서는?

Stream의 parallel에 대해 여러가지 논쟁과 토론이 많다. 주로 내용들은 Deadlock 상황이나 Thread가 의도하지 않게 많이 만들어 질 수 있다라는 내용이다. 주로 원인은 ForkJoinPool을 사용하면서 발생하는 문제이다. 여러 블로그나 문서를 참조하면 다음과 같은 parallel 사용 시 주의 사항이 많이 언급되고 있다.

- parallel stream 내부에서 다시 parallel stream 사용할 경우 synchronized 키워드는 deadlock 을 발생시킬 수 있다.
- 특정 Container 내부에서 사용하는 경우에는 parallel은 신중하게 사용해야 하며, Container가 default pool을 어떻게 처리하는지 정확하게 모르는 경우에는 Default pool은 절대 사용하지 마라.
- Java EE Container에서는 Stream의 parallel을 사용하지 마라.

### 아직 많은 내용을 살펴 보지는 못했지만 현재까지 살펴본 내용으로 판단해보면 필자의 의견은 다음과 같다.

- 간단하게 독립된 프로그램으로 아주 큰 파일 또는 데이터를 가공하는 작업을 할때는 parallel을 이용하는 것도 쉽게 개발할 수 있는 하나의 방법이라고 생각한다.
- 하지만 데몬 프로그램이나 WAS에서 동작하는 기능 등에서는 권장하지 않는다.



아이템48과 다른 도서 [Practical 모던 자바](http://www.yes24.com/Product/Goods/92529658?OzSrank=1)을 참고하면서 느낀점은 기존의 Concurrent API, 포크/조인 프레임워크를 이해하고 제대로 활용한 다음에 스트림 병렬화를 시도해야할 듯 합니다.



### Q1.

> 데이터 소스가 Stream.iterate거나 중간 연산으로 limit를 쓰면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다. 파이프라인 병렬화는 limit를 다룰 때 CPU 코어가 남는다면 원소를 몇 개 더 처리한 후 제한된 개수 이후의 결과를 버려도 아무런 해가 없다고 가정한다.

본문 내용 중 궁금한 것이 있어서 질문남깁니다. limit 을 쓰면 왜 병렬화로는 성능 개선을 기대할 수 없는 것인가요?



### A1.

limit()는 요소의 순서에 의존하는 연산이라 성능이 더욱 떨어진다고 합니다.
오라클 공식문서에 따르면 [stream](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)

> For parallel streams, relaxing the ordering constraint can sometimes enable more efficient execution. Certain aggregate operations, such as filtering duplicates (distinct()) or grouped reductions (Collectors.groupingBy()) can be implemented more efficiently if ordering of elements is not relevant. Similarly, operations that are intrinsically tied to encounter order, such as limit(), may require buffering to ensure proper ordering, undermining the benefit of parallelism. In cases where the stream has an encounter order, but the user does not particularly care about that encounter order, explicitly de-ordering the stream with unordered() may improve parallel performance for some stateful or terminal operations. However, most stream pipelines, such as the "sum of weight of blocks" example above, still parallelize efficiently even under ordering constraints.

번역:
병렬 스트림의 경우 **순서 제한을 완화하면** 때때로 더 효율적인 실행이 가능할 수 있습니다. 중복 필터링 (distinct()) 또는 그룹화 된 축소 (Collectors.groupingBy())와 같은 특정 집계 작업은 요소 순서가 적절하지 않은 경우보다 효율적으로 구현할 수 있습니다. 마찬가지로 limit ()와 같이 발생 순서에 본질적으로 연결된 작업은 적절한 순서를 보장하기 위해 버퍼링이 필요할 수 있으며, 이는 병렬 처리의 이점을 약화시킵니다. 스트림에 발생 순서가 있지만 사용자가 그 발생 순서에 대해 특별히 신경 쓰지 않는 경우, unordered()를 사용하여 스트림의 순서를 명시적으로 취소하면 일부 상태 저장 또는 터미널 작업에 대한 병렬 성능이 향상 될 수 있습니다. 그러나 위의 "블록 가중치 합계"예제와 같은 대부분의 스트림 파이프 라인은 순서 제약 조건에서도 여전히 효율적으로 병렬화됩니다.

unordered()나 findAny()를 먼저 호출하고, limit()을 호출하는 법이 더욱 빨라질 가능성이 있습니다.