# 아이템60. 정확한 답이 필요하다면 float과 double은 피하라

# 책정리

```java

```

# float, double

float과 double 타입은 과학과 공학 계산용으로 설계되었다.

→ 정확한 결과가 필요할 때는 사용하면 안된다.

> float과 double 타입은 특히 금융 관련 계산과는 맞지 않는다.

## 소수로 표현하는 센트

```java
public static void main(String[] args) {
		double funds = 1.00;
		int itemsBought = 0;
		for (double price = 0.10; funds >= price; price += 0.10) {
				funds -= price;
				itemsBought++;
		}
		System.out.println(itemsBought + "개 구입");
		System.out.println("잔돈(달러):" + funds);
}
```

프로그램을 실행하면 사탕 3개를 구입한 후 잔돈은 0.399999999999....999 달러가 남게된다.

**금융 계산에서는 BigDecimal, int 혹은 long을 사용해야 한다.**

하지만 BigDecimal에는 단점이 두 가지 있다. 기본 타입보다 쓰기가 훨씬 불편하고, 훨씬 느리다.

단발성 계산이라면 느리다는 문제는 무시할 수 있지만, 쓰기 불편하다는 점은 아쉬울 것이다.

BigDecimal 대신 int, long을 쓸 땐, 값의 크기가 제한되고, 소수점을 직접 관리해야 한다.

# 결론

정확한 답이 필요한 계산에는 float나 double을 피하라.

BigDecimal은 여덟 가지 반올림 모드를 이용하여 반올림을 완벽히 제어하고 비즈니스 계산에서 매우 편리하다.

만약 성능상 int, long을 써야 한다면 int는 9자리, long은 18자리 십진수로 표현하도록 하자.

18자리 이상이라면 BigDecimal을 사용하자.