# 지연 초기화는 신중히 사용하라

## 질문

- volatile 을 언제 사용하는 걸까?
  - 한 메서드가 volatile로 선언된 값을 읽었을 때, 최종적인 값이 읽어야 한다. 
  - 어떻게 구현될지는 벤더마다 다르므로, 약간 인터페이스 적인 느낌.
- synchronize 를 사용해도 volatile을 사용해하는 걸까..?
  - 어떤 목적인지가 중요할듯.
  - 전자는 락이 필요하거나, 메서드가 concurrently한 실행이 요구될 때
  - 후자는 해당 변수로 행해지는 읽거나 쓰는 모든 접근들이 메인 메모리에 적용되는 것이 요구될 때 (이 경우에 접근하는 순서는 중요하지 않을 때..)
- [difference-between-volatile-and-synchronized-in-java](https://stackoverflow.com/questions/3519664/difference-between-volatile-and-synchronized-in-java)

## 정리

- 지연 초기화는 필요할 때 까지 하지마라
  - 대부분 상황에서 일반적인 초기화가 지연 초기화 보다 낫다.
- 필드 타입이 long, double을 제외한 다른 기본 타입이라면, 단일 검사 필드 선언에서 volatile 한정자를 없애도 된다.
- 인스턴트 필드는 이중 검사 관용구..
  - 이중 검사 관용구?
  - 두번 검사하는 것..
- 정적 필드는 지연 초기화 홀더 클래스 관용구...
  - 지연 초기화 홀더 클래스..?
  - 특성 메서드가 호출 되는 순간, 정적필드가 생성되도록 만들어주는 것.



## QnA
- Spring Lazy Loading에 대해서..
  - https://www.baeldung.com/spring-boot-lazy-initialization
  - https://www.baeldung.com/hibernate-lazy-eager-loading
- 초기화 순환성(initialization circularity) 에 대해 읽어볼 만한 글 
  - https://link.springer.com/content/pdf/10.1007/3-540-45739-9_5.pdf
  - 1,2 페이지는 흥미롭다!
