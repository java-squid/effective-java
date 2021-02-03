# [아이템 64] 객체는 인터페이스를 사용하라

## 요약

- 매개변수뿐 아니라 반환값, 변수, 필드를 전부 인터페이스 타입으로 선언하라.
- 적합한 인터페이스가 없다면 클래스의 계층구조 중 가장 상위의 클래스를 사용하라.


```java
// 좋은 예시
Set<Son> sonSet = new LinkedHashSet<>();

// 나쁜 예시
LinkedHashSet<Son> sonSet = new LinkedHashSet<>();
```

## 소감

- SOLID 원칙 중 리스코프 치환 법칙이 떠올랐다.
- Spring 의 DI(Dependency Injection) 가 떠올랐다.


### DI(Dependency Injection)

토비의 스프링, 김영한님의 스프링 강의를 수강하면서 정리한 내용을 @kses1010 님이 작성

#### 제어의 역전 (IoC: Inversion of Control)

- AppConfig가 등장 후 구현 객체는 자신의 로직을 실행하는 역할만 한다. 프로그램의 제어 흐름은 AppConfig가 담당한다.
→ orderServiceImpl은 필요한 인터페이스들을 호출하지만 어떤 구현 객체들이 실행될지 모름.
- 프로그램의 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것
→ **제어의 역전(IoC)**

#### 프레임워크 vs 라이브러리

- 프레임워크: 내가 작성한 코드를 제어, 대신 실행하면 프레임워크(JUnit)
- 라이브러리: 내가 작성한 코드가 직접 제의 흐름을 담당. (객체 → JSON으로 바꾸기)

#### 의존관계 주입(DI: Dependency Injection)

- 인터페이스에 의존하기 때문에 실제 어떤 구현 객체가 사용될지는 모른다.
- 의존관계는 정적인 클래스 의존 관계와 실행 시점에 결정되는 동적인 객체(인스턴스) 의존 관계 둘을 분리해서 생각해야 한다.

#### 정적인 클래스 의존 관계

import 코드만 보고 의존관계를 쉽게 판단할 수 있다.

→ 정적인 의존관계는 애플리케이션을 실행하지 않아도 분석할 수 있다.
![image](https://user-images.githubusercontent.com/49144662/106373404-61c16d80-63bc-11eb-8d2e-c50e0c7f128b.png)


```java

public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
    // 고정 할인 정책
    public DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }
}

// OrderServiceImpl.class
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

이제 여기서 OrderSeviceImpl에서 고정 할인 정책이 아닌 할인율 정책으로 변경할 경우
```java
public DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
}
```
로만 바꾸면 됩니다. 서비스 로직인 OrderServiceImpl에서 바꿀 필요가 없겠죠? 이건 자바로직이고 여기서 만약 스프링으로 전환한다면
```java
@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
```
로 애너테이션으로 등록이 가능합니다. 김영한님 강의에서는 xml에서 애너테이션으로 바꾸는 추세라고 하셨고, xml을 쓴다고 하면 아마 레거시쪽이 많이 쓸 듯하네요.
IoC는 제어의 역전이며, DI는 스프링 프레임워크에서 지원하는 IoC의 형태라고 생각하시면 됩니다. 자세한건
인프런의 김영한님 [강의](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B8%B0%EB%B3%B8%ED%8E%B8/dashboard) 또는 [토비의 스프링](http://www.yes24.com/Product/Goods/7516911)을 추천합니다!
