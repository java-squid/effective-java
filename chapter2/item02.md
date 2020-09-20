

# 생성자에 매개변수가 많으면 빌더를 고려하라



# 책 정리

참조아이템

```
아이템51: 메서드 시그니처를 신중히 설계하라. (메서드, 이름 변수명)
아이템17: 변경 가능성을 최소화하라. (불변 클래스)
아이템50: 적시에 방어적 복사본을 만들어라.
아이템30: 이왕이면 제네릭 메서드로 만들어라.
```

정적 팩토리와 생성자에는 똑같은 제약이 하나있다. 선택적 매개변수가 많을 때 적절히 대응하기 어렵다는 점이다. 클래스용 생성자 혹은 정적팩토리는 선배 프로그래머들은 이럴 때 생성자 패턴(telescoping constructor pattern)을 즐겨 사용했다. 매개변수를 필수적, 1개, 2개 등등 형태로 선택 매개변수를 전부 다 받는 생성자까지 늘려가는 방식이다.

### 01. 점층적 생성자 패턴

이 클래스의 인스턴스를 만들려면 원하는 매개변수를 모두 포함한 생성자 중 가장 짧은 것을 골라 호출

정확히는 **"필수 매개변수를 받는 생성자 1개, 그리고 선택매개변수를 하나씩 늘여가며 생성자를 만드는 패턴"**

문제는 사용자가 원치 않는 매개변수까지 포함하고, 값을 지정해줘야한다.

### 점층적 생성자 패턴도 쓸 수는 있지만, 매개변수 개수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.

### 02. 자바빈즈 패턴(JavaBeans Pattern)

매개변수가 없는 생성자로 객체를 만든 후, setter 메서드들을 호출해 원하는 매개변수의 값을 설정하는 방식.

Lombok에서도 볼 수 있는 `@setter`. 코드는 길어지지만 인스턴스를 만들기 쉽고, 이전 점층적 생성자 패턴보다 읽기 쉬운 코드가 되었다.

그러나, 객체 하나를 만드려면 메서드를 여러 개 호출해야 하고, 객체가 완전히 생성되기 전까지는 일관성(consistency)이 무너진 상태에 놓이게 된다. 매개변수들이 유효한지를 확인할 수 있는 장치가 없어진다.

일관성이 무너지는 문제로 클래스를 불변으로 만들 수 없으며 쓰레드 안전성을 얻으려면 프로그래머가 추가작업이 필요하다.

### 0x. 프리징

이 방법은 다루기 어려워서 실전에서는 거의 쓰이지 않는다.

### 03. 빌더 패턴(Builder Pattern)

클라이언트는 필요한 객체를 직접 만드는 대신, 필수 매개변수만으로 생성자(or 정적팩토리)를 호출해 빌더 객체를 얻는다. 그 다음에 setter를 이용해 선택 매개변수들을 설정한다. 마지막으로 매개변수가 없는 build 메서드를 호출해 드디어 우리에게 필요한 (불변) 객체를 얻는다.

빌더는 생성할 클래스 안에 정적 멤버 클래스로 만들어 두는 게 보통이다.

```java
public class NutritionFactsBuilder {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 - 기본값으로 초기화.
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) {
            calories = val;
            return this;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }

        public Builder sodium(int val) {
            sodium = val;
            return this;
        }

        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }

        public NutritionFactsBuilder build() {
            return new NutritionFactsBuilder(this);
        }
    }

    private NutritionFactsBuilder(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}
```

현재 NutritionFactsBuilder 클래스는 불변이며, 모든 매개변수의 기본값들을 한곳에 모았다. setter 메서드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출 할 수 있다. 이런 방식을 플루언트 API, 메서드 연쇄(Method Chaning) 이라 한다.

```java
NutritionFactsBuilder cola = new NutritionFactsBuilder.Builder(240, 8)
                .calories(100)
                .sodium(35)
                .carbohydrate(27)
                .build();8)
```

현재 이 클라이언트 코드는 쓰기 쉽고, 읽기 쉽다. 빌더 패턴은 명명된 선택전 매개변수(파이썬, 스칼라에 있는)를 흉내 낸 것이다. 잘못된 매개변수를 일찍 발견하려면 빌더의 생성자와 메서드에서 입력 매개변수를 검사하고, build 메서드가 호출하는 생성자에서 여러 매개변수에 거린 불변식(invariant)을 검사하자.

공격에 대비해 불변식을 보장하려면 빌더로부터 매개변수를 복사한 후 해당 객체 필드들도 검사해야한다.

### 빌더 패턴과 계층적 클래스

추상 클래스는 추상 빌더를, 구체 클래스(concrete class)는 구체 빌더를 갖게 한다.

Pizza.java

```java
public abstract class Pizza {
    public enum Topping {
        HAM, MUSHROOM, ONION, PEPPER, SAUSAGE
    }

    final Set<Topping> toppings;

		// 재귀적인 타입 변수
    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();

        // 하위 클래스는 이 메서드를 재정의(overriding)하여 this를 반환
        protected abstract T self();
    }

    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone();
    }
} 
```

Pizza.Builder 클래스는 재귀적 타입한정을 이용하는 제네릭 타입이다. 여기에 추상 메서드인 self()를 더해 하위 클래스에서는 형변환하지 않고, 메서드 연쇄가 가능하다.

self 타입이 없는 자바를 위한 이 우회 방법을 시뮬레이트한 셀프 타입(simulated self-type) 관용구라 한다.

### Pizza, NYPizza, CalzonePizza

각 하위 클래스의 빌더가 정의한 build 메서드는 해당하는 구체 하위 클래스를 반환하도록 선언. NYPizza.builder 는 NYPizza를 반환, CalzonePizza.builder는 CalzonePizza를 반환한다.

하위 클래스의 메서드가 상위 클래스의 메서드가 정의한 반환 타입이 아닌, 그 하위 타입을 반환하는 기능을 공변반환 타이핑(convariant return typing)이라 한다. 이 이용하면 클라이언트가 형변환에 신경 쓰지 않고도 빌더를 사용할 수 있다.

클라이언트 코드

```java
NYPizza pizza = new Builder(SMALL)
                .addTopping(SAUSAGE)
                .addTopping(ONION)
                .build();

        CalzonePizza calzone = new CalzonePizza.Builder()
                .addTopping(HAM)
                .sauceInside()
                .build();
```

빌더를 이용하면 가변인수(varags) 매개변수를 여러 개 사용할 수 있다. 각각을 적절한 메서드로 나눠 선언하면 된다. 메서드를 여러 번 호출하도록 하고 각 호출 때 넘겨진 매개변수들을 하나의 필드로 모을 수 있다.

(생성자나 팩토리는 가변인자를 맨 마지막 매개변수에 한번밖에 못쓴다.)

빌더 패턴은 상당히 유연하다. 빌더 하나로 여러 객체를 순회하면서 만들 수 있고, 빌더에 넘기는 매개변수에 따라 다른 객체를 만들 수도 있다. 매개변수마다 부여되는 일련번호와 같은 특정 필드는 빌더가 알아서 채우도록 할 수도 있다.

단점은 객체를 만드려면, 그에 앞서 빌더부터 만들어야 한다. 점층적 생성자 패턴보다는 코드가 장황해서 매개변수가 4개 이상은 되어야 값어치가 있다. → API는 시간이 지날수록 매개변수가 많아지기 때문에 상관 X

### 핵심정리

생성자, 정적팩토리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하자.

------



## Q&A

### David

abstract static class Builder<T extends Builder<T>> 에서 보이는 recursive type parameter에 대해서 간단하게 설명해주시면 감사하겠습니다. 빌더 패턴이 상속 관계에 얽혀있을 때 구현하기 위해서는 기본적인 개념이나 문법을 이해해야 할 듯 하네요.

A: Recursive type parameter 즉, 재귀적 타입 한정이라고 부릅니다. 재귀적 타입 한정을 이용하는 제네릭 타입을 사용하면 추상메서드 self를 지원하여 하위클래스에서도 형변환 하지 않고도 상위타입에서 구현한 메서드를 연쇄적으로 호출할 수 있다고 합니다.

아이템30에서 Comparable 인터페이스를 소개하면서 재귀적 타입 한정을 소개하고 있습니다.

```java
public interface Comparable<T> {
	int compareTo(T o);
}

public static <E extends Comparable<E>> E max(Collection<E> c);

// 재귀적 타입 한정 빌더
abstract static class Builder<T extends Builder<T>> {

}
```

타입 한정인 `<E extends Comparable<E>>` 는 "모든 타입 E는 자신과 비교할 수 있다" 라고 읽을 수 있습니다. 그렇다면 `Builder<T extends Builder<T>>` 는 "모든 타입 T는 자신과 빌더를 사용할 수 있다." 라고 읽을 수도 있겠죠.

하위 클래스에서 타입 캐스팅없이 사용할 수 있습니다. 제가 정확하게 재귀적 타입 한정을 이해를 한 건지 모르겠군요. 그러나, 재귀적 타입 한정이 이보다 훨씬 복잡해질 가능성이 있으나, 다행히 그런일은 잘 일어나지 않는다고 합니다.

------

### 상속에 관하여

빌더 패턴에서 상속은 사실 다른 상속관계와 크게 다르지 않습니다. 피자 예제처럼 피자 → NY피자, 칼초네피자로 상속하고 오버라이딩할 메서드는 build()(인스턴스 생성), self()를 구현하면 됩니다. 여기서 self는 시뮬레이트한 셀프 타입 관용구라하여 파이썬, 스칼라에서 있는 self타입을 구현을 해야합니다. Java에선 self타입이 없거든요.

실제 빌더를 구현하지 않고 롬복을 사용할 때는 다릅니다. 롬복에서의 `@Builder` 는 필수매개변수가 없고 선택적 매개변수만 존재합니다. 그 뿐만 아니라 재귀적 타입한정을 이용한 빌더 생성을 하지 않습니다. 최소한의 기능만을 사용을 하겠다는 겁니다.

롬복을 이용한 빌더 상속은 부모 클래스에서 자식 클래스를 넘겨줄 때는 큰 문제는 없습니다.

```java
@Getter
@AllArgsConstructor
public class Parent {
    private final String parentName;
    private final int parentAge;
}
 
@Getter
public class Child extends Parent {
    private final String childName;
    private final int childAge;
 
    @Builder
    public Child(String parentName, int parentAge, String childName, int childAge) {
        super(parentName, parentAge);
        this.childName = childName;
        this.childAge = childAge;
    }
```

그러나, 만약 부모 클래스에서 빌더를 생성한다면,

```java
@Getter
@AllArgsConstructor
public class Parent {
    private final String parentName;
    private final int parentAge;

	  @Builder
    public Parent(String parentName, int parentAge) {
        this.parentName = parentName;
        this.parentAge = parentAge;
    }
}
 
@Getter
public class Child extends Parent {
    private final String childName;
    private final int childAge;
 
    @Builder
    public Child(String parentName, int parentAge, String childName, int childAge) {
        super(parentName, parentAge);
        this.childName = childName;
        this.childAge = childAge;
    }
```

이런 경우에는 빌더이름이 중복이 되어 컴파일 에러가 발생합니다. 이름이 중복되어 나는 컴파일 에러 같은경우 자식클래스에서 빌더의 이름을 바꿔주면 됩니다. 클라이언트 코드에선 자식클래스를 생성 시 `builderMethodName` 에서 지정한 빌더를 사용합니다.

```java
@Getter
public class Child extends Parent {
    private final String childName;
    private final int childAge;
    
    @Builder(builderMethodName = "childBuilder")
    public Child(String parentName, int parentAge, String childName, int childAge) {
        super(parentName, parentAge);
        this.childName = childName;
        this.childAge = childAge;
    }
}

// 클라이언트 코드
Child child = Child.childbuilder()
  .parentName("Cloud")
  .parentAge(345)
  .childName("Sunny")
  .childAge(29)
  .build();
```

만약 자신이 롬복 **1.18버전**을 쓰신다면 `@SuperBuilder` 를 사용하는것도 나쁘지 않습니다.

`@SuperBuilder`는 부모, 자식클래스 구분없이 빌더를 만들어 클라이언트 코드에 사용이 가능합니다.

```java
@Getter
@SuperBuilder
public class Parent {
    // same as before...
 
@Getter
@SuperBuilder
public class Child extends Parent {
   // same as before...

// 클라이언트 코드
Child child = Child.childbuilder()
  .parentName("Cloud")
  .parentAge(345)
  .childName("Sunny")
  .childAge(29)
  .build(); 
```

단, `@Builder` 와 같이 쓸 수없기 때문에 주의 해야 합니다.

### Henry

비슷한 질문으로 추상 클래스로 abstract static class Builder<T extends Builder<T>>를 만들고 상속하여 사용하는 과정이 잘 이해가 안가는데요.. 저희가 이해하기 쉽도록 간단한 예제를 만들어서 이야기 해보면 좋을거같아요.

A: 자바봄에서 본 예제에서는 카드를 예시로 하여 표현했는데 간단한 예제는 어떻게 구현을 해야할지 모르겠습니다. EnumSet존재와 재귀적 타입 한정이 어려운 관계로 구현이 어렵네요. 여기에 대해선 스터디할 때 같이 이야기를 나누었으면 좋겠습니다.

자바봄을 바탕으로한 구현은 다음과 같습니다.

PayCard.java

```java
public abstract class PayCard {
    public enum Benefit {
        POINT("포인트"), SALE("할인"), SUPPORT("연회비지원");
        Benefit(String benefit) {

        }
    }

    final Set<Benefit> benefits;

    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Benefit> benefits = EnumSet.noneOf(Benefit.class);

        public T addBenefit(Benefit benefit) {
            this.benefits.add(benefit);
            return self();
        }

        abstract PayCard build();

        protected abstract T self();
    }

    PayCard(Builder<?> builder) {
        benefits = builder.benefits.clone();
    }

}
```

KakaoCard.java

```java
public class KakaoCard extends PayCard {
    public enum Sale {
        GAME, KAKAO_STORE
    }

    private final Sale sale;

    public static class Builder extends PayCard.Builder<Builder> {
        private final Sale sale;

        public Builder(Sale sale) {
            this.sale = sale;
        }

        @Override
        public KakaoCard build() {
            return new KakaoCard(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    KakaoCard(Builder builder) {
        super(builder);
        sale = builder.sale;
    }
}

// 클라이언트

KakaoCard kakaoCard = new KakaoCard.Builder(GAME)
                .addBenefit(POINT)
                .build();
```

### Han

> p16 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태가 된다..

여기서 객체의 일관성이란 어떤걸 의미하는 걸까요....?> 아마도 생성된 객체가 setter를 통해 변화가능함을 의미하지 않나 싶네요. 그 다음 문단에서 javabeans setter, final class등이 나오는 걸 보니까..

A: 객체의 일관성은 클라이언트가 일관된 접근 방식을 이용하여 일관된 접근 방식을 제공하는걸 의미합니다. 일관성이 없으면 접근방식(변수에 직접 사용, 엑세스 함수 사용)에 주의력을 소진하게 되어, 뒤에 있는 버그가 있을 경우에 찾기 힘들어집니다.

자바가 멀티쓰레드 환경이라 setter를 쓰면 일관성이 깨지는 게 개발자들 이야기입니다. 객체가 유효하지 않은 상태를 가질 수 없도록 하는 것이 바람직한 설계이고, 생성자 주입으로 유효성 검사와 함께 필수값을 모두 제공하고, 추후에 불필요한 값 변경을 금지하도록 하고 있습니다.

그 중 빌더 패턴은 매개변수가 많을 경우에 사용하는게 좋을 듯 합니다.

### Han의 답변

[@kses1010](https://github.com/kses1010)

> 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다. (여기서 무슨뜻인지 몰라서 넘겼습니다.)

아마도 멤버 변수의 접근 제어자를 private하게 선언하라 라는 의미 같아요.
맥락상 가변 컴포넌트라는 게, 멤버 변수보다 더 넓은 의미라고도 보여지긴 하는 데.. 제 생각은 그렇습니다.



## 스터디 토론

### Q) 재귀적 한정 제어자(item30) 에 대해

- 대략적인 내용만 이용하고, Item 30나왔을 때 다시 한번 보자



### Q) 일관성에 대해서

- Item17에 좀 더 잘 정의되어있음.
- [@david215](https://github.com/david215) 이 책의 독자는 API, Library를 만드는 설계자, 개발자들을 대상으로 하는듯