# 아이템46. 스트림에서는 부작용 없는 함수를 사용하라

## 파이로가 오해한 점 정리

### telescoping

- 헤드폰 밴드가 사람 머리 사이즈에 맞춰서 늘렸다 줄였다 할 수 있는 움직임을 telescoping이라고 표현
- **telescoping argument list 패턴**: 매개변수가 더 많은 overloaded 함수를 정의할 때 매개변수의 순서를 바꾸지 않는 것

### Call by Reference

#### Reference vs Address

- **address**: value that corresponds to a place in memory
- **reference**: name that refers to an existing object, rather than being it's own object.

자바는 기본적으로 **pass by value** 만을 언어적으로 허용한다. <br>
따라서 자바에서 address value 를 pass 해서 참조할 수는 있어도, <br>
reference 라는 개념은 자바에서 허용되지 않는다.

#### Reference 의 예시

아래의 c++ 코드에서 `&a` 와 `&b` 가 바로 reference 이다. <br>
자바에서는 value 에 대한 직접적인 reference 를 언어적으로 지원하지 않는다. <br>
기껏해야 value 를 객체로 감싸서, 객체의 address를 통해 객체 내부의 값을 변경할 수 있을 뿐이다.

```cpp
void switch_by_ref(int &a, int &b) {
    int temp = a;
    a = b;
    b = temp;
}

int main() {
    int a = 10;
    int b = 20;
    switch_by_ref(a, b); // a, b 의 값이 서로 바뀌어 있다.
    cout << "a : " << a << endl;
    cout << "b : " << b << endl;
    return 0;
}
```

## 질의응답

### 질문1.

```txt
> p277 부작용이 없는 함수...

부작용이 없는 함수란 무엇일까요?
우선 책에서는 side-effect가 없는 함수라고 이야기하고 있습니다.
그러면 사이드 이펙트가 없다는 말이 무엇일까요??
제가 이해했을 떄는 스트림 연산을 진행함에 있어, 외부 변수와 소통을 자주하면 이제 예상하지 못한 사이드 이펙트가 발생해서 그러지 않을까 생각해요
왜 이러한 사이드 이펙트가 발생하는 걸까요..?
```

### 응답1.

>  사이드 이펙트가 없다는 말이 무엇일까요?

공부를 하며 이해한 내용을 코드로 정리해보기 위해,
1 부터 5까지의 리스트 안 숫자들의 합을 구해서, 15 을 반환하는 함수 sum 을 구현하려고 한다고 예시를 들어보겠습니다.

```java
    public static void main(String[] args) {
        int sum = sum(Arrays.asList(1, 2, 3, 4, 5));
        System.out.println(sum);
    }
```

#### side-effect가 있는 함수

우선 side-effect 있는 함수부터 에를 들어보겠습니다.
아래의 sum 함수는 num -> {...} 람다 함수에서, 함수 외부의 int sum 변수에 접근하고, 이를 변형하려 하고 있습니다.
함수 외부의 상태를 접근해서 변형을 하고 있고, 이는 곧 함수와 관련 없는 side-effect 를 뜻합니다.
다행스럽게도 자바에서는 컴파일 단계에서 실행조차 되지 못하게, 에러를 띄워줍니다.
기본 자료형에 대해서는 함수 외부의 side effect 를 잘 막아주지만, 객체를 통한 call by reference 를 통한 접근일 경우, 컴파일러가 막아주지 못하고 side-effect 가 생기는 것을 경험할 수 있습니다.
그렇다면 지금과 같은 기본 자료형인 상황에서 자바 컴파일러가 통과시켜주는 코드 형태가 side-effect 가 없는 함수가 아닐까 예상을 해봅니다.
컴파일러 에러가 나타내듯이 lambda 표현식에서는 함수 외부의 기본 자료형이 모두 final 이라 변형 불가능해야할 테니깐요.

```java
    public static int sum(List<Integer> list) {
        int sum = 0;
        list.forEach(num -> {
            sum += num;
        });
        return sum;
    }
```

밑에서는 int sum 기본 자료형을 계산하기 위한 여러 해결 책들을 찾아보았습니다.

#### 해결책 1: 절차지향적으로 루프를 사용한다

잘 동작하지만, 람다와 스트림이 전혀 쓰이지 않게 되었으므로, 옛날 방식의 코드 같습니다.

```java
    public static int sum(List<Integer> list) {
        int sum = 0;
        for (int num : list) {
            sum += num;
        }
        return sum;
    }
```

#### 해결책 2: reduce 를 사용한다.

개인적으로 제일 좋아하는 방법입니다. int sum 에 해당하는 변수를 함수 외부에 선언하지 않고,
reduce 함수의 indentity 인자에 재귀적으로 넘겨주어서 함수 내부에서만 상태가 변하도록 합니다.

```java
    public static int sum(List<Integer> list) {
        return list.stream()
                .reduce(0, Integer::sum);
    }
```

#### 해결책 3: Collectors 를 활용한다.

Effective-Java 에서 공식적으로 권장하는 방법입니다.
`이처럼 Collectors 의 멤버를 정적 임포트하여 쓰면 스트림 파이프라인 가독성이 좋아져, 흔히들 사용한다.` (p.279)

저는 아직 Collectors 내부의 함수를 제대로 숙지하고 있지 못하기 때문에, 잘 사용하지는 못할 것 같습니다.

```java
    public static int sum(List<Integer> list) {
        return list.stream()
                .collect(Collectors.summingInt(Integer::intValue));
    }
```

#### 해결책 4: IntStream 을 활용한다.

일반적은 stream 을 mapToInt 하면 IntStream 으로 바뀌고,
그 후에는 IntStream 의 sum 함수를 사용할 수 있습니다.
개인적으로는 Collectors.summingInt 함수보다 더 가독성이 좋다고 느꼈습니다.

```java
    public static int sum(List<Integer> list) {
        return list.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
```

### 응답 2.

> 제가 이해했을 떄는 스트림 연산을 진행함에 있어, 외부 변수와 소통을 자주하면 이제 예상하지 못한 사이드 이펙트가 발생해서 그러지 않을까 생각해요

위에서 side-effecct 가 없는 함수를 4가지 해결책을 통해 예시로 들어보았습니다.
질문에서 힌트를 주신대로, 부작용이 없는 함수란, 함수 외부의 변수 혹은 상태를 변경하지 않는 것을 뜻하지 않을까 생각합니다.
접근해서 읽는 것은 가능하되, 외부 상태를 변경하는 순간, 그것이 side-effect 가 된다고 생각합니다.

### 응답 3.

> 왜 이러한 사이드 이펙트가 발생하는 걸까요..?

제 생각에는 개발자 입장과, Java 라는 언어적 입장에서의 원인이 둘다 있다고 생각합니다.

개발자 입장에서는 기존의 절차지향과 객체지향의 패러다임으로 코딩하는 것에 익숙해져 있어서,
함수형 패러다임에 어긋나는 코딩 실수를 하게 될 때에 사이드 이펙트가 발생되는 함수를 작성하게 된다고 생각합니다.
(객체지향적 입장에서는 객체 단위로, 객체 외부에 대한 side-effect 만 주의할 뿐 함수 단위로 side-effect 를 주의하지는 않으니깐요 )

Java 언어 입장에서 말씀드리자면, 제 생각에 Java 는 철저한 객체지향 언어로써, 아직까지 완전한 함수형 언어를 지원하고 있지는 않다고 생각합니다.
가령, [Java 는 true closure를 지원하지 않습니다.](https://riptutorial.com/ko/java/example/14441/%EB%9E%8C%EB%8B%A4%EC%8B%9D%EC%9D%B4%EC%9E%88%EB%8A%94-java-closure-)
맨 처음에 함수 외부의 변수에 접근하기 위해, closure 를 만드는 방법을 찾아보았는데,
Java 는 객체단위로 접근을 제한하거나 풀 수 있을 뿐, 함수단위로 접근을 제어하는 기능을 아직 지원하지 않는다는 것을 알게 되었습니다.

## 질문2.

```
참고해보면 좋은 문서
https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html#toMap-java.util.function.Function-java.util.function.Function-java.util.function.BinaryOperator-

> p282 groupingBy 메서드는 telescoping argument list pattern에 어긋난다.

- 이게 무슨말인지 확실히 이해가 잘 안되네요.
- 점증적 인수목록 패턴, mapFactory 매개변수, downStream 매개변수가 어떤걸 의미하는 지 간단하게나마 알아보면 좋을 것 같네요..
```

### 응답

> groupingBy 메서드는 telescoping argument list pattern에 어긋난다.

저도 잘 이해가 안갑니다.
이해가 안가서 일단 모르는 영어 단어부터 검색을 해보았습니다.
telescoping 이 대체 무슨 의미인지 도통 모르겠네요.

이것저것 찾아서 공부를 해보니, 아무래도 [수학의 telescoping series](https://ko.wikipedia.org/wiki/%EB%A7%9D%EC%9B%90%EA%B8%89%EC%88%98)
에서 나온용어처럼 보입니다.

이럴수가! 호몰로지라니... 학교 다닐 때 대수학 좀 열심히 들어둘걸 그랬네요.
그래도 수학 공식을 쭉 읽다보니, `telescoping argument list` 라고 하면 argument 들이 서로 상쇄되어 소거가 될 것 처럼 느껴집니다.
[Cambridge 백과사전](https://dictionary.cambridge.org/ko/%EC%82%AC%EC%A0%84/%EC%98%81%EC%96%B4/telescoping) 에서는 `to make or become shorter by reducing the length of the parts` 이라고 정의를 하고 있습니다.

Effective-Java 에서 telescoping 이라는 표현이 처음 사용된 것은 p.14 의 item 2 에서 나온 점층적 생성자 패턴(Telescoping Constructor Pattern)입니다.
코드 2-1 내용의 점층적 생성자 패턴에서는 NutritionFacts 라는 생성자를 오버로딩을 통해 계속 파라미터를 늘려나가고 있습니다.
그리고 p15 에서 telescoping 패턴에 대해서 악담을 합니다.
`점층적 생성자 패턴도 쓸 수는 있지만, 매개변수 개수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.`

뭔지는 잘 모르겠지만 telescoping 을 하면 안 좋은 것 같습니다.
그런 의미에서 `groupingBy 메서드는 telescoping argument list pattern에 어긋난다.` 라는 말은
어긋나서 매우 좋다고 장점을 어필하는 것처럼 느껴집니다.
그럼 대체 뭐가 그렇게 좋은 걸까요?

점층적 인수 목록 패턴이 어긋남으로 인해, mapFactory 매개변수(String::toLowerCase) 가, downStream 매개변수(counting()) 보다 앞에 놓였습니다.
일단 String::toLowerCase 가 couting() 보다 앞에 있어서 좋다는 사실은 동의할 수 있어보입니다.
덕분에 apple 과 Apple 을 같이 count 하여 숫자 2를 셀수 있을 것이고, 덕분에 apple 과 Apple 을 같은 키로 하는 map 을 반환할 수 있을 테니 말입니다.
덕분에 collect 를 하기전에 map 을 한번 더 해주어야 하는 수고가 덜었습니다.

그럼 counting() 을 앞세우고 String::toLowerCase 를 뒤로 보내는 것은 telescoping argument list pattern 에 부합하는 것일까요?
대체  telescoping argument list pattern 은 정확히 무엇일까요?
아, 도저히 모르겠습니다!
구글에 검색해도 나오지 않네요.

머리를 쥐어 뜯으며 추측을 해보며 떠올린 것은 currying 의 개념입니다.
[Javascript Currying](https://javascript.info/currying-partials)

함수 인자를 전달할 때, `sum(a, b)` 를 `sum(a) (b)` 이런 식으로 전달할 수 있게 구성하고,
[언어가 currying 문법을 지원한다면](http://pages.cs.wisc.edu/~fischer/cs538.s08/lectures/Lecture27.pdf) `sum: a -> b`  이런식으로 우아하게 작성할 수도 있습니다.
(안타깝지만 Java 는 함수형 지원이 빈약해서 currying 이 안됩니다.)

즉 telescoping 하게 한다는 것은
```java
words
.map(String::toLowerCase)
.collect(groupingBy(counting()))
```
이런식으로 점증적으로 인자가 전달되어야 합니다.

하지만 예시에서 나온 groupBy 에서는 2개의 인자를 그냥 다 때려박습니다.
```java
words
.collect(groupingBy(String::toLowerCase, groupingBy(counting())))
```

즉 String::toLowerCase 가 먼저 앞에 나와있는게 문제가 아니라,
groupingBy 안에 2개의 인자를 한꺼번에 넣게 된 것이 telescoping argument list pattern 에 부합하지 않은 것이 아닐까 추측해보았습니다.
