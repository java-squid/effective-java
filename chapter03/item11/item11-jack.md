# 아이템 11. equals를 재정의하려거든 hashCode도 재정의하라 

p.69 2번째 문단. hashCode를 다 구현했다면 이 메서드가 동치인 인스턴스에 대해 똑같은 해시코드를 반환할지 자문해보자. 그리고 여러분의 직관을 검증할 단위 테스트를 작성하자(equals와 hashCode 메서드를 AutoValue로 생성했다면 건너뛰어도 좋다.)

p.72 equals를 재정의할 때는 hashcode도 반드시 재정의해야 한다. 그렇지 않으면 프로그램이 제대로 동작하지 않을 것이다. 재정의한 hashcode는 Object의 API문서에 기술된 일반 규약을 따라야 하며, 서로 다른 인스턴스라면 되도록 해시코드도 서로 다르게 구현해야 한다. 

## AutoValue
### 무엇인가? 
[구글에서 지원하는 라이브러리](https://github.com/google/auto/tree/master/value)로 POJO를 생성할 때 필요한 equals(), hashcode(), toString() 메서드를 쉽게 생성해준다. 이렇게 하면 필요한 재정의가 적용되고 수동 코딩으로 발생할 수 있는 잠재적 오류를 방지할 수 있습니다. 

- [AutoValue 코드 생성기를 사용하여 POJO(Plain Old Java Object) 생성](https://cloud.google.com/solutions/e-commerce/patterns/generating-pojos?hl=ko)

## IntelliJ에서 사용하는 equals, hashcode 자동 생성

Intellij에서는 5가지 방법을 제공하는데 대표적인 2가지 방식은 아래 2가지입니다. 둘 다 equals()에서 getClass()를 사용하는데 이 방식은 코드 10 - 4에서처럼 리스코프 치환 원칙을 위배한다고 합니다. 이유는 상위 타입을 상속받은 하위 타입에 equals()를 실행하면 상위 타입과 다른 클래스라 인식하기 때문이다. 대신 instance of를 사용하는 방법을 추천하네요 (p.59)

## Intellij Default (코드11-2 방식)

```java
public class Item {

    private String name;
    private int price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (price != item.price) return false;
        return name != null ? name.equals(item.name) : item.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + price;
        return result;
    }
}
```

## Object를 이용하는 방식 (위 방법과 equals는 동일한데 hashcode는 Objects.hash()를 사용)

- 위 방식의 단점은 성능이 좋지 않다는 점이고 이유는 입력 인수를 위한 배열이 만들어지고 입력 중 기본 타입이 있다면 박싱과 언박싱도 거쳐야 하기 때문이라고 하네요.

```java
public class Item {

    private String name;
    private int price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return price == item.price &&
                Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
```

## 비결정적 요소

# 비결정적 요소

> In mathematics, computer science and physics, a deterministic system is a system in which no randomness is involved in the development of future states of the system.[1] A deterministic model will thus always produce the same output from a given starting condition or initial state.[2]

- [참고 위키피디아](https://en.wikipedia.org/wiki/Deterministic_system)

undeterministic에 대한 의미는 잘 안나와서 derterministic을 찾아보니 결과가 도출될 때 랜덤요소(상황에 따라 값이 달라지는)가 없는 상태를 의미하네요. 그래서 주어진 초기 상태에서 항상 같은 결과를 도출한다는 의미네요. 이 의미로 파악해볼 땐 underministic은 상황에 따라 값이 달라져서 결과가 항상 일관되지 못한 요소를 의미하는 것 같아요. 

예시는 이펙티브 자바 코드11-2를 차용하겠습니다. 이 예에서 만약 areaCode가 매번 랜덤으로 정해지는 값이라면 비결정적인 요소가 되기 때문에 equals() 함수가 동작하면 매번 다른 해시코드가 나올 것 같습니다! 

## 지연 초기화

> 아이템 83 참고해서 작성했습니다. 

## 무엇인가? 
지연초기화는 필드의 초기화 시점을 그 값이 처음 필요할 때까지 늦추는 기법입니다. 그래서 값이 전혀 쓰이지 않으면 초기화도 일어나지 않습니다. 주로 성능 최적화 용도로 쓰이지만, 클래스와 인스턴스 초기화 때 발생하는 위험한 순환 문제를 해결하는 효과도 있다. (다른 인스턴스를 참조하는 필드가 있고 해당 인스턴스가 현재 인스턴스를 참조할 때 발생하는 순환문제라고 이해했습니다.) 

## 사용방법 
다른 모든 최적화와 마찬가지로 지연 초기화에 대해 해줄 최선의 조언은 "필요할 때까지는 하지 마라"다. 이유는 생성 시 초기화 비용은 줄지만 해당 필드로 접근하는 비용이 클 수 있다. 만약 10개 필드가 전부 지연 초기화 필드인데 반드시 사용되는 필드라면 차라리 미리 초기화를 하는 비용이 매번 하나 필드 접근해서 초기화해서 사용하는 비용보다 작을 수 있다는 의미로 보여진다. 

## 주의 
멀티스레드 환경에서는 지연 초기화를 하기가 까다롭다. 지연 초기화하는 필드를 둘 이상의 스레드가 공유한다면 어떤 형태로든 반드시 동기화해야 한다. 아니면 심각한 버그로 이어질 것이다. 


