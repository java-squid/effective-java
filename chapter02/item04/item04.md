# 인스턴스화를 막으려면, private 생성자를 사용하라
- 정적 메서드, 정적 필드만을 담은 클래스도 나름 쓰임새가 있음.
    1. java.lang.Math
    - 정적으로 선언된 기본 타입 값, 메서드드를 모아둠.
      ```java
        public final class Math { 
            // 생성자는 private하게,  
            private Math() {}
      
            // 나머지 필드값들은 모두 static하게 선언 되어있음.
            public static final double E = 2.7182818284590452354;
            public static final double PI = 3.14159265358979323846;
            ...
       }
       ```
     2. java.util.Collection 처럼 특정 인터페이스를 구현하는 객체를 생성해주는 정적 메서드를 모아둘 수 도 있음.
        - list, set, map등을 생성하여 return하는 정적 메서드를 가지고 있음.
     3. final 클래스와 관련한 메서드들을 모아 놓을 때 사용.
- 정적 필드, 정적 메서드들을 담은 클래스를 **유틸리티 클래스** 라 부름.
- 유틸리티 클래스들은 인스턴스로 만드려고 설계한 것이 아님.
    - 즉 유틸리티 클래스 기능이다? 그려먼 생성자가 하는 역할이 없음.
    - 그렇지만 컴파일러는 **생성자를 명시하지 않으면 자동으로 기본 생성자를 만들어줌**
- 즉 생성자를 private하게 하지 않을 경우, 개발자들이 예상하지 못한 유틸리티 클래스의 인스턴스화가 가능할 수 있다.
- 이러한 오류를 방지하기 위해서 private한 생성자를 유틸리티 클래스에 명시해줘야 한다.

## 질문 : 추상 클래스로 유틸리티 클래스를 선언해주면 어떨까?
- 일단 추상클래스는 인스턴스를 만들 수 없음.
- 그렇지만, 추상 클래스를 상속받은 하위 클래스에서는 **인스턴스**가 가능하다.
- 즉 유틸리티 클래스가 추상 클래스로 선언되어 있다면, 사용자가 "아 상속받아서 생성자 만들어야 겠당!" 이라고 오해할 수도 있음.
- 항상 의도치 않은 오류를 막아야함..

## 인스턴스를 만들 수 없는 유틸리티 클래스
```java
public class UtilityClass {
    //private한 기본 생성자 정의를 통해, 컴파일러가 기본 생성자를 자동으로 생성해주는 것을 막고 이를 통해 의도치 않은 인스턴스화를 막는다.
   private UtilityClass() {
       throw new AssertionError(); //생성자가 호출 되었을 경우, 오류를 발생시키도록 설계한다.
   }
}
```
- 명시적인 생성자가 private이니, 클래스 바깥에서 해당 생성자에 접근할 수는 없음.
- 그렇지만 클래스 내부에서는 호출 가능하니 실수라도 생성자를 호출하지 않도록 막아줄 수 있음.
- 또한 private한 생성자를 만드는 방식은 상속을 불가능하게도 한다.
    - 모든 생성자는 상위 클래스의 생성자를 호출하는데, 상위클래스의 생성자가 private하게 선언되어있을 경우, 하위 클래스에서 상위 클래스에 생성자에 접근할 길이 막히게 됨.


---
## 질문 및 댓글
![image](https://user-images.githubusercontent.com/22140570/93709483-64dc5f80-fb79-11ea-9e7d-a33137473675.png)
- 언급된 링크 : https://stackoverflow.com/questions/2054022/is-it-unnecessary-to-put-super-in-constructor/2054040#2054040
![image](https://user-images.githubusercontent.com/22140570/93709495-85a4b500-fb79-11ea-8ddc-5d60fe432fe0.png)
![image](https://user-images.githubusercontent.com/22140570/93709506-a0772980-fb79-11ea-997f-c7ee213ad738.png)


