# 가능한 한 실패 원자적으로 만들라

## 정리

- 호출된 메서드가 실패하더라도 -> 해당 객체는 메서드 호출 전 상태를 유지해야한다. (failure-atomic)
  - 어떻게? ->불변 객체로 설계
  - 가변 객체라면 어떻게 실패 원자적으로 만들까? --> 메서드 수행 전, **매개 변수의 유효성**을 검사한다.
- 혹은 객체의 임시 복사본을 만들어서, 작업을 수행 후, 성공한다면 -> 원래 객체와 교체
  - 실패한 다면? -> 원래 객체를 사용
  - 즉 실패가 발생한다면? -> 복구 코드를 이용하여 객체를 작업 전 상태로 만드는 방법

## QnA
- [failure-atomic](https://stackoverflow.com/questions/29842845/what-is-failure-atomicity-used-by-j-bloch-and-how-its-beneficial-in-terms-of-i)
![image](https://user-images.githubusercontent.com/22140570/108712791-a2934900-755a-11eb-910d-03456512b0c0.png)
