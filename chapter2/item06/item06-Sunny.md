# 아이템6. 불필요한 객체 생성을 피하라



# 책정리

참조아이템

```
아이템01: 생성자 대신 정적 팩토리 메서드를 고려하라
아이템67: 최적화는 신중히 하라 
아이템61: 박싱된 기본 타입보다는 기본 타입을 사용하라
아이템50: 적시에 방어적 복사본을 만들라
```

똑같은 기능의 객체를 매번 생성하기보다는 객체 하나를 재사용하는 편이 낫다. 특히 불변 객체는 언제든 재사용이 가능하다.

```java
String s = new String("hello"); // 따라하지 말것!
```

이 예제는 실행될 때마다 String 인스턴스를 새로 만든다. → 쓸모 없는 행위.

개선된 버전

```java
String s = "hello";
```

새로운 인스턴스를 매번 만드는 대신 하나의 String 인스턴스를 사용한다. 이 방식을 사용한다면 같은 가상머신(JVM) 안에서 이와 똑같은 문자열 리터럴을 사용하는 모든 코드가 같은 객체를 재사용함이 보장된다.

정적 팩토리 메서드를 제공하는 불변 클래스에서는 불필요한 객체 생성을 피할 수 있다.

ex) Boolean(String) 생성자 대신 Boolean.valueOf(String) 팩토리 메서드를 사용하는 것이 좋다.

생성자는 호출할 때마다 새로운 객체를 만들지만, 팩토리 메서드는 전혀 그렇지 않다. 불변 객체만이 아니라 가변 객체라 해도 사용 중에 변경되지 않을 것임을 안다면 재사용할 수 있다.

## 생성 비용이 비싼 객체

비싼 객체가 반복해서 필요하다면 캐싱하여 재사용해야한다. 그러나, 자신이 만드는 객체가 비싼 객체인지 판단하기가 어렵다.

정규표현식을 활용한 예제

```java
static boolean isRomanNumeral(String s) {
	return s.matches("^(?=.)M*(C[MD]|D?C{0,3}"
 + "(X[CL]|L?X{0,3})(I{XV}|V?I{0,3})$")
}
```

이 방식의 문제는 String.matches 메서드를 사용한다는 데 있다.

**String.matches는 정규표현식으로 문자열 형태를 확인하는 가장 쉬운 방법이지만, 성능이 중요한 상황에서 반복해 사용하기엔 적합하지않다.**

이 메서드가 내부에서 만드는 정규표현식용 Pattern 인스턴스는, 한 번 쓰고 버려져서 곧바로 GC 대상이 된다.

Pattern은 입력받은 정규료현식에 해당하는 유한 상태 머신(finite state machine)을 만들기 때문에 인스턴스 생성 비용이 높다.

### 성능 개선하기

정규표현식을 표현하는 (불변인) Pattern 인스턴스를 클래스 초기화(정적 초기화) 과정에서 직접 생성해 캐싱해두고, 나중에 isRomanNumeral 메서드가 호출될 때마다 이 인스턴스를 재사용한다.

```java
public class RomanNumerals {
	private static final Pattern ROMAN = Pattern.compile(
			"^(?=.)M*(C[MD]|D?C{0,3}"
		+ "(X[CL]|L?X{0,3})(I{XV}|V?I{0,3})$");

	static boolean isRomanNumeral(String s) {
		return ROMAN.matchers(s).matches();
	}
}
```

개선하면 성능을 상당히 올릴 수 있다. 그뿐 아니라 코드도 더 명확해졌다. Pattern 인스턴스를 static final 필드로 끄집어내고 이름을 지어주어 코드의 의미가 훨씬 잘 드러난다.

만약 개선된 방식의 클래스가 초기화된 후 이 메서드를 한 번도 호출하지 않는다면 ROMAN 필드는 쓸데없이 초기화 된 꼴이다. `isRomanNumeral` 메서드가 처음 호출될 때 필드를 초기화하는 지연초기화(Lazy initialization)로 불필요한 초기화를 없앨수도 있다. 그러나 지연 초기화는 코드를 복잡하게 만드는데, 성능은 크게 개선되지 않을 때가 많아 추천하지 않는다.

## 객체가 불변이라도?

보통 객체가 불변이라면 재사용해도 안전하다. 하지만 훨씬 덜 명확하거나, 심지어 직관에 반대되는 상황도 있다.

어댑터(Gamma95(디자인패턴), 뷰(view)라고도 한다.) 에선 실제 작업은 뒷단 객체에 위임하고, 자신은 제2의 인터페이스 역할을 해주는 객체다. 어댑터는 뒷단 객체만 관리하면 된다. 즉, 뒷단 객체 외에는 관리할 상태가 없으므로 뒷단 객체 하나당 어댑터 하나씩만 만들면 충분하다.

### Map 인터페이스의 어댑터

Map 인터페이스의 keySet 메서드는 Map 객체 안의 키 전부를 담은 Set 뷰를 반환한다. kesSet을 호출할 때마다 새로운 Set 인스턴스가 만들어질수도 있고 매번 같은 Set 인스턴스를 반환할지도 모른다.

반환된 Set 인스턴스가 일반적으로 가변이더라도 반환된 인스턴스들은 기능적으로 모두 똑같다. 즉, 반환한 객체 중 하나를 수정하면 다른 모든 객체가 따라서 바뀐다. 모두가 똑같은 Map 인스턴스를 대변하기 때문이다.

→ keySet이 뷰 전체를 여러 개 만들어도 상관은 없지만, 그럴필요가 없다.

### 오토박싱(auto boxing)

오토박싱은 프로그래머가 기본 타입(primitive)과 박싱된 기본 타입을 섞어 쓸 때 자동으로 상호 변환해주는 기술이다.

**오토박싱은 기본 타입과 그에 대응하는 박싱된 기본 타입의 구분을 흐릿하게 해주지만, 완전히 없애주진 못한다.**

의미상으로는 별다를 것 없지만 성능에서 차이가 난다.

```java
// 아주 느린 메서드
private static long sum() {
	Long sum = 0L;
	for (long i = 0; i <= Integer.MAX_VALUE; i++) {
		sum += i;
	}

	return sum;
}
```

sum 변수를 long이 아닌 Long으로 선언해서 불필요한 Long 인스턴스가 약 231개 정도 만들었다.(대략, long타입인 i가 Long 타입인 sum에 더해질 때마다).

단순히 sum의 타입을 long으로만 바꿔주면 저자의 컴퓨터에서는 6.3초에서 0.59초로 빨라진다.

> 박싱된 기본타입보다는 기본 타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의하자.

## 결론

이번 아이템을 "객체 생성은 비싸니 피해야 한다" 로 오해하면 안된다. 특히나 요즘의 JVM에서는 별다른 일을 하지 않은 작은 객체를 생성하고 회수하는 일이 크게 부담되지 않는다.

프로그램의 명확성, 간결성, 기능을 위해서 객체를 추가로 생성하는 것이라면 일반적으로 좋은 일이다.

### 반대로

아주 무거운 객체가 아닌 다음에야 단순히 객체 생성을 피하려고 나만의 객체 풀(pool)을 만들지는 말자. 객체 풀을 만드는게 나은 예가 있으나, DB 연결 같은 경우 생성 비용이 워낙 비싸서 재사용하는 편이 낫다.

그러나, 일반적으로는 자체 객체 풀은 코드를 헷갈리게 만들고 메모리 사용량을 늘리고 성능을 떨어뜨린다. 요즘 JVM의 GC는 상당히 잘 최적화되어서 가벼운 객체용을 다룰 때는 직접 만든 객체 풀보다 훨씬 빠르다.

이번 아이템은 방어적 복사(defensive copy)를 다루는 아이템50과 대조적이다.

아이템50: "새로운 객체를 만들어야 한다면 기존 객체를 재사용하지 마라" 이다.

방어적 복사가 필요한 상황에서 객체를 재사용했을 때의 피해가, 필요 없는 객체를 반복 생성했을 때의 피해보다 훨씬 크다. (아이템50 > 아이템 6)

방어적 복사에 실패하면 무수히 많은 버그와 보안 구멍으로 이어지지만, 불필요한 객체 생성은 그저 코드 형태와 성능에만 영향을 준다.



# Q&A

### Q1.

>  p.32 (1번째 문단 마지막) Pattern은 입력받은 정규표현식에 해당하는 유한 상태 머신(finite state machine)을 만들기 때문에 인스턴스 생성 비용이 높다. 

정규표현식용 Patten 클래스와 유한 상태 머신이 낯선데 이 2가지 개념을 간략하게 설명해주시면 좋을 것 같습니다.

### A1.

@David

이거는 제가 요즘 수업에서 듣는 내용이라 예시와 함께 간단하게 설명해볼게요.
유한 상태 머신을 간단하게 설명하면:

1. 시작, 도착 상태를 포함한 상태들 (그래프에서의 노드)
2. 상태 사이의 전이 (그래프에서의 엣지)로 이루어져 있는데

쉽게 예를 들어 "squid"라는 패턴을 입력 문자열이 포함하고 있는지 확인하려면:

- 아직 아무 문자도 보지 못한 상태에서 시작
- 's'라는 문자를 보면 's'를 본 상태로 전이
- 'q'를 보게 되면 'sq'까지 본 상태로 전이되지만 'q'가 아닌 다른 문자를 보게 되면 아직 아무 문자도 보지 못한 상태로 전이
- 입력 문자열을 모두 소진할 때까지 반복...

이런 식으로 동작을 하는 머신인데 보통 꽤 간단한 정규표현식이라도 유한 상태 머신으로 변환하면 엄청나게 사이즈가 커집니다.
예를 들어 0, 1로 이루어진 문자열 중에 마지막에서 네 번째 문자가 1인 문자열을 표현하는 정규표현식은 `(0|1)*1(0|1){3}`인데 유한 상태 머신으로 변환하면

![screenshot-ivanzuzak info-2020 09 22-14_22_20](https://user-images.githubusercontent.com/12704057/93847339-a5ef8380-fce1-11ea-9829-dc1a5c13189d.png)

이렇게 됩니다... 일반적인 탐색 문제와는 다르게 모든 상태와 전이를 찾아놓고 매칭을 하기 때문에 생성비용이 높지만 생성 이후에는 매칭을 빠르게 할 수 있기 때문에 컴파일러를 만들 때 꼭 사용되는 개념입니다.

Java Pattern와 연결지어 생각해보자면 Pattern 객체를 사용할 때 한 번 compile()하고 반복적으로 사용할 수 있는 시나리오에서 사용하는 것이 좋습니다.



### Q2.

> p.33 (2번째 문단) 어댑터를 생각해보자. 어댑터는 실제 작업은 뒷단 객체에 위임하고, 자신은 제2의 인터페이스 역할을 해주는 객체다.

디자인 패턴 중 하나로 어댑터를 본 것 같은데 어댑터 패턴의 예시를 한번 보여주시면 좋을 것 같습니다.

### A2.

### 출처

- https://www.tutorialspoint.com/design_pattern/adapter_pattern.htm
- https://yaboong.github.io/design-pattern/2018/10/15/adapter-pattern/

어댑터 패턴은 의외로 간단한 패턴이었습니다.
[![image](https://user-images.githubusercontent.com/49144662/94134921-ab91c880-fe9d-11ea-86c8-deb590bde478.png)](https://user-images.githubusercontent.com/49144662/94134921-ab91c880-fe9d-11ea-86c8-deb590bde478.png)

다음 사진과 같은 클래스 구조를 나누고, 클래스를 간략하게 구현한다면 클라이언트 로직에서는 이런식으로 가능합니다.

```
public class AdapterPatternDemo {
   public static void main(String[] args) {
      AudioPlayer audioPlayer = new AudioPlayer();

      audioPlayer.play("mp3", "beyond the horizon.mp3");
      audioPlayer.play("mp4", "alone.mp4");
      audioPlayer.play("vlc", "far far away.vlc");
      audioPlayer.play("avi", "mind me.avi");
   }
}
```

출력

```
Playing mp3 file. Name: beyond the horizon.mp3
Playing mp4 file. Name: alone.mp4
Playing vlc file. Name: far far away.vlc
Invalid media. avi format not supported
```

어댑터 패턴의 가장 대표적인 예시는 바로 `InputStreamReader` 가 있습니다.

```
BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
```

BufferedReader 클래스를 까서 위 구문이 실행될때 사용되는 생성자를 보면 아래와 같이 Reader 타입을 받습니다.

```
public BufferedReader(Reader in) {
    this(in, defaultCharBufferSize);
}
```

하지만 System.in 은 InputStream 타입을 반환합니다.

```
public final static InputStream in = null;
```

자바의 InputStream 은 바이트 스트림을 읽습니다. 하지만, BufferedReader 는 캐릭터인풋 스트림을 읽습니다. 둘은 호환되지 않지만, 이 둘을 연결시켜 주는 어댑터가 InputStreamReader 클래스입니다. UML 로 보면 아래와 같은 구조.
[![image](https://user-images.githubusercontent.com/49144662/94142134-c1f15180-fea8-11ea-817c-072619f5105e.png)](https://user-images.githubusercontent.com/49144662/94142134-c1f15180-fea8-11ea-817c-072619f5105e.png)

### 어댑터 패턴 정리

- Adaptee 를 감싸고, Target Interface 만을 클라이언트에게 드러낸다.
- Target Interface 를 구현하여 클라이언트가 예상하는 인터페이스가 되도록 Adaptee 의 인터페이스를 간접적으로 변경한다.
- Adaptee 가 기대하는 방식으로 클라이언트의 요청을 간접적으로 변경한다.
- 호환되지 않는 우리의 인터페이스와 Adaptee 를 함께 사용할 수 있다.

사용해야하는 인터페이스가 현재의 시스템과 호환되지 않는다고 해서 현재의 시스템을 변경을 해야하는 것은 아니다.

### 어댑터 추가

이러한 패턴은 외부 라이브러리를 사용할 때 활용 될 수 있다. 실제 비즈니스 로직을 구현하는데는 외부 라이브러리가 많이 혼재되어 있다면 굉장히 이해하기 어려울 것이다. 이런 경우 내가 사용하고자 하는 인터페이스를 잘 정의하고 그의 구현에서 외부 라이브러리의 클래스들을 사용한다면 훨씬 깔끔하게 우리의 비즈니스 로직을 구현할 수 있을 것이다.



### Q3.

> Generally speaking, however, maintaining your own object pools clutters your code, increases memory footprint, and harms performance.

아이템 마지막 부분에 데이터베이스 커넥션 객체를 예시로 들며 객체 풀을 직접 관리하는 것은 안 좋다고 하는데 그 이유를 잘 모르겠네요.



### A3.

> DB 연결 같은경우 생성비용이 비싸서 재사용하는 경우가 있다.

객체 풀은 객체를 필요로 할때 풀에 요청을 하고, 반환하고 일련의 작업을 수행하는 디자인 패턴으로 많은 수의 인스턴스를 생성할때 혹은 무거운 오브젝트를 매번 인스턴스화 할때 성능 향상을 위해서 사용합니다.
문제는 객체 풀이 코드를 헷갈리게 만들고 잘못 설정한 경우 메모리 사용량을 늘리고 성능을 떨어뜨립니다. 게다가 요즘 JVM의 가비지 컬렉터는 상당히 잘 최적화되어서 가벼운 객체용으로 다룰 때는 사용하지 않은게 더 낫다고 하네요.

찾아보니 주의 사항이 다음과 같습니다.

- 객체 풀에서 사용되지 않은 객체는 메모리 낭비의 원인이 된다.
- 한번에 사용 가능한 객체 갯수가 정해져있다. (객체 풀의 모든 객체가 사용중이라서 재사용할 객체를 반환받지 못할 때를 대비해야한다.)
- 객체를 위한 메모리 크기는 고정되어있다.
- 재사용되는 객체는 저절로 초기화 되지 않는다. (여기서 초기화하는 걸 잊어 메모리 낭비에 원인이 될 수도 있겠군요)



### Q4.

advanced 질문입니다.

코쿼 수업중에 한번 언급되었던 내용 같은데,, String을 생성하는 방법에 대해서 간단하게나마 알아보면 좋을듯 합니다.
예를 들면,

```
String str1 = new String("new!!");
String str2 = "new!!";
```

String constant pool과 관련 있을 것 같긴 한데... 그리고 위 두 방법 간에 어떻게 차이가 있는 지에 대해 알아보면 좋을 듯해요!

![image](https://user-images.githubusercontent.com/55608425/94984082-3f8d1f80-0583-11eb-9bbd-02c0452f1266.png)