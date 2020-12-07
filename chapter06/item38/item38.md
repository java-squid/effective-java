# 아이템 38. 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

# 책정리

```
아이템33
아이템31
아이템36
아이템37
```

# 열거 타입 확장

타입 안전 열거 패턴은 확장할 수 있으나 열거 타입은 확장할 수 없다.

사실 대부분 상황에서 열거 타입을 확장하는 건 좋지 않은 생각이다. 확장한 타입의 원소는 기반 타입의 원소로 취급하지만 그 반대는 성립하지 않는다면 이상할 것이다.

기반 타입과 확장된 타입들의 원소 모두를 순회할 방법도 마땅치 않다.

확장성을 높이려면 고려할 요소가 늘어나 설계와 구현이 더 복잡해진다.

## 연산 코드(opcode)

확장할 수 있는 열거 타입이 어울리는 쓰임이 바로 연산 코드이다. 연산코드의 각 원소는 특정 기계가 수행하는 연산을 뜻한다. 이따금 API가 제공하는 기본 연산 외에 사용자 확장 연산을 추가할 수 있도록 열어줘야 할 때가 있다.

열거 타입으로 이 효과를 내는 방법이 있다. 기본 아이디어는 열거 타입이 임의의 인터페이스를 구현할 수 있다는 사실을 이용하는 것이다.

```java
public enum BasicOperation implements Operation{
    PLUS("+") {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/"){
        public double apply(double x, double y) {
            return x / y;
        }
    };
    
    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```

열거 타입인 BasicOperation은 확장할 수 없지만 인터페이스인 Operation은 확장할 수 있고, 이 인터페이스를 연산의 타입으로 사용하면 된다.

Operation을 구현한 또 다른 열거 타입을 정의해 기본 타입인 BasicOperation을 대체할 수 있다.

### 지수 연산(EXP), 나머지 연산(REMAINDER) 추가하기

```java
public enum ExtendedOperation implements Operation {
    EXP("^") {
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        public double apply(double x, double y) {
            return x % y;
        }
    };

    private final String symbol;

    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```

개별 인스턴스 수준에서뿐 아니라 타입 수준에서도, 기본 열거 타입 대신 확장된 열거 타입을 넘겨 확장된 열거 타입의 원소 모두를 사용하게 할 수도 있다.

```java
public class Main {
    public static void main(String[] args) {
        double x = 4;
        double y = 2;
        test(ExtendedOperation.class, x, y);
    }
		// ExtendedOperation의 모든 원소를 테스트 하는 메서드
    private static <T extends Enum<T> & Operation> void test(
								Class<T> opEnumType, double x, double y) {
        for (Operation op : opEnumType.getEnumConstants()) {
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
        }
    }
}
```

main 메서드는 test 메서드에 ExtendedOperation의 class 리터럴을 넘겨 확장된 연산들이 무엇인지 알려준다. 여기서 class 리터럴은 한정적 타입 토큰역할을 한다.

opEnumType 매개변수의 선언 (<T extends Enum<T> & Operation>) 복잡한데, Class 객체가 열거 타입인 동시에 Operation의 하위 타입이어야 한다는 뜻이다. 열거 타입이어야 원소를 순회할 수 있고, Operation이어야 원소가 뜻하는 연산을 수행할 수 있기 때문이다.

### Collection<? extends Operation> 넘기는 방법

```java
public class Main {
    public static void main(String[] args) {
        double x = 4;
        double y = 2;
        test(Arrays.asList(ExtendedOperation.values()), x, y);
    }

    private static void test(
							Collection<? extends Operation> opSet, double x, double y) {
        for (Operation op : opSet) {
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
        }
    }
}
```

이 코드는 덜 복잡하고 test 메서드가 살짝 더 유연해졌다.

→ 여러 구현 타입의 연산을 조합해 호출할 수 있게 되었다.

반면, 특정연산에서는 EnumSet, EnumMap을 사용하지 못한다.

# 인터페이스를 이용해 확장 가능한 열거타입의 문제점

열거 타입끼리 구현을 상속할 수 없다는 점이다. 아무 상태에도 의존하지 않는 경우에는 디폴트 구현을 이용해 인터페이스에 추가하는 방법이 있다.

반면 Operation 예는 연산 기호를 저장하고 찾는 로직이 BasicOperation과 ExtendedOperation 모두에 들어가야만 한다.

이 경우에는 중복량이 적으니 문제되지 않지만, 공유하는 기능이 많다면 그 부분을 별도의 도우미 클래스나 정적 도우미 메서드로 분리하는 방식으로 코드 중복을 없앨 수 있을 것이다.

자바 라이브러리에도 java.nio.file.LinkOption 열거 타입은 CopyOption과 OpenOption 인터페이스를 구현 했다.

# 결론

열거 타입 자체는 확장할 수 없지만, 인터페이스와 그 인터페이스를 구현하는 기본 열거 타입과 함께 사용해 같은 효과를 낼 수 있다.

클라이언트는 이 인터페이스를 구현해 자신만의 열거 타입을 만들 수 있다.

API가 인터페이스 기반으로 작성되었다면 기본 열거 타입의 인스턴스가 쓰이는 모든 곳을 새로 확장한 열거 타입의 인스턴스로 대체해 사용할 수 있다.

## Q&A

> p235 예시 코드 바로 밑에,
> ... 반면, 특정 연산에서는 EnumSet과 EnumMap을 사용하지 못한다..

- 어떠한 특정 연산에서 왜 사용하지 못하는 걸까요..?



### A.

특정 연산이라는 뜻이 첫번 째 방법(T extends Enum<T> & Operation>)을 의미하는 것 같습니다. p235의 예시 코드 장점에서 유연하고 여러 구현 타입의 연산을 조합해 호출할 수 있다고 설명이 있습니다.
또 다른 곳을 찾아보니 두번째 방법(Collection<? extends Coperation>) 방법이 EnumSet, EnumMap을 사용할 수 있는 유연성을 가지고 있다고 하네요.