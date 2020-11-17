# 톱레벨 클래스는 한 파일에 하나만 담으라
![](https://user-images.githubusercontent.com/13347548/75746762-9ec15200-5d5e-11ea-872f-c0b73dedc39c.png)
- 두 클래스가 한 파일에 정의되어 있음.
  - Dessert.java(Dessert.class, Utensil.class)
  - 컴파일 오류!
  - 왜?

- 컴파일러는 어떻게 동작하려 할까..
  ```java
  public class Main {
      public static void main(String[] args) {
          System.out.println(Utensil.NAME + Dessert.NAME);
      }
  }
  
  ```
  1. Main.java를 컴파일
  2. Utensil.java 컴파일 --> 그 안에 Dessert, Utensil 클래스 있음을 확인함.
  3. 그리고 Dessert.java 를 컴파일 하는데.. 어? 이미 정의되었는 클래스들이 있음.
  4. 에러!
    - 컴파일러가 어떤 소스파일을 먼저 읽느냐에 따라서 **다른 결과**가 나오면 안됨.

## 정리
- 톱레벨 클래스를 서로 다른 소스 파일로 분리하자.
- 만약 한 파일에 담고 싶으면 정적 멤버 클래스를 고려해보자

# QnA
![image](https://user-images.githubusercontent.com/22140570/99402569-d4c65680-292c-11eb-8106-fb0e2ee9739f.png)
