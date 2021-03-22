# 커스텀 직렬화 형태를 고려해보라


## 정리

- 객체의 물리적 표현 (코드로 나타낸 것..), 논리적 내용(실제 세계의 나타나는 논리적인 형태) 가 같다면...?
  - 기본 직렬화 형태(implement Serializable...)를 사용해도 무방함.
- 직렬화 되는 클래스에 속하는 멤버 변수들에 대해서 (private) 주석을 달아줘야한다.
  - `@serial`
- 커스텀 직렬화
  - 객체 -> 커스텀 객체 (직렬화)
  - 객체를 담을 커스텀한 객체를 만들고, 이를 직렬화 시키는 것.
  - 왜?
    - 기본 객체를 그냥 직렬화 함으로써 생길 수 있는 부작용...들을 방지하고 나아가 유연한 객체 사용을 위해서..?
- transient로 선언한 필드들은 역직렬화 될 때 기본값으로 초기화됨.
  - null, 0, false...



## 참고

- https://madplay.github.io/post/what-is-readobject-method-and-writeobject-method
  -  readObject, writeObject 메서드의 역할을 이해할 수 있음.

