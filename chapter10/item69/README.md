# 예외는 진짜 예외 상황에서만 사용하라

## 정리

- 진짜 예외 상황에서만 사용해야하는 이유?
  - 예외 처리에 들어가면 해당 코드는 보통 코드 보다 느려지기 때문.
  - 괜시리, 모든 것을 커버하기 위해 예외 처리를 하자 말라.
- 잘 설계된 API는, 클라이언트가 제어 흐름 속에서 예외를 사용하지 않아야 함.
- 특정 상태(그 다음 원소가 있는 경우에만) 에서만 호출할 수 있는 상태 의존적 메서드(next) 를 제공하는 클래스 (iterator) 는 상태 검사 메서드 (hasNext)

## QnA
- 공유된 URL
   - [스프링의 Exception 전략(프로젝트 할 때 큰 도움이 됨)](https://cheese10yun.github.io/spring-guide-exception)
   - [자바의 예외처리](https://johngrib.github.io/wiki/java-exception-handling/#from-%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94)
   - [EAFP vs LBYL](https://stackoverflow.com/questions/404795/lbyl-vs-eafp-in-java/405220#405220)

- EAFP vs LBYL
  - it's easier to ask forgiveness than permession vs Look before you leap
  - Python은 EAFP
    - 검사할 필요 없이, 우선 동작 시키고 그리고 예외를 발생시킴
    - 그래서 속도가 느리다.
  - Java는 LBYL 쪽인듯.. (책에서도 언급되었듯이)
    - 동작 시키지 전에, 한번 검사한다.
  - 책에서는 null, optional등의 특정 value를 반환하는 지침에 대해 언급하고 있지만, 에러가 발생할 수 있는 부분은 try문을 통해 잡는 게 나을 듯함. 
