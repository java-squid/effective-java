# try-finally 보다는 try-with-resources를 사용하라

- 자원을 닫는데 사용되는 `finalizer` 는 믿을만 하지 못하다 --> 언제 닫힐지 모르기 때문.
- 전통적으로 자원이 제대로 닫히는 것을 보장하는 수단으로 `try-finally` 를 사용했다.
- 예시 코드
  ```java
        static String firstLineOfFile(String path) throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(path));
            try {
                return br.readLine(); //여기서 예외가 발생하면?
            } finally {
                br.close(); //자원이 제대로 닫히지 않는다.
            }
        }
  ```
- `readLine()` 에서 예외가 발생한다면 , `br.close()` 가 실패 할 것
- 그러면 첫번째에서 발생한 에러가 두번째(br..)에서 발생한 에러에 의해 잡아 먹히므로, 궁극적으로 어디서 발생한 에러인지 알기 어렵다.
- 이러한 문제를 해결하고자 `try-with-resources` 가 등장!
  -  이 구조를 사용하려면, 해당 자원이 `AutoCloseable` 인터페이스를 구현해야한다.
- 만약 반드시 닫혀야하는 자원이라면, **해당 인터페이스를 구현 혹은 확장**하자
- `try-with-resources` 에서 `catch` 도 사용할 수 있다.
  - `catch` 를 통해 다수의 예외를 처리할 수 있다는 것이 장점!
  ```java
        static String firstLineOfFile(String path) {
            BufferedReader br = new BufferedReader(new FileReader(path));
            try {
                return br.readLine(); //여기서 예외가 발생하면?
            } catch(IOException e) {
                return defaultVal
            }
        }
  ```

# 참고 
> https://www.baeldung.com/java-try-with-resources

```java
public class AutoCloseableResourcesFirst implements AutoCloseable {
 
    public AutoCloseableResourcesFirst() {
        System.out.println("Constructor -> AutoCloseableResources_First");
    }
 
    public void doSomething() {
        System.out.println("Something -> AutoCloseableResources_First");
    }
 
    @Override
    public void close() throws Exception {
        System.out.println("Closed AutoCloseableResources_First");
    }
}
public class AutoCloseableResourcesSecond implements AutoCloseable {
 
    public AutoCloseableResourcesSecond() {
        System.out.println("Constructor -> AutoCloseableResources_Second");
    }
 
    public void doSomething() {
        System.out.println("Something -> AutoCloseableResources_Second");
    }
 
    @Override
    public void close() throws Exception {
        System.out.println("Closed AutoCloseableResources_Second");
    }
}
```
- `AutoCloseable`을 구현한 두 개의 클래스
- 실행하면 어떤 순서로 메서드들이 찍힐 것인가?
```java
private void orderOfClosingResources() throws Exception {
    try (AutoCloseableResourcesFirst af = new AutoCloseableResourcesFirst();
        AutoCloseableResourcesSecond as = new AutoCloseableResourcesSecond()) {
 
        af.doSomething();
        as.doSomething();
    }
}
```
- 결과
```text
Constructor -> AutoCloseableResources_First
Constructor -> AutoCloseableResources_Second
Something -> AutoCloseableResources_First
Something -> AutoCloseableResources_Second
Closed AutoCloseableResources_Second
Closed AutoCloseableResources_First
```
- 생성자 -> 중간 메서드 -> close 메서드 순으로 찍히는 것을 알 수있다.
- 즉 `close()` 를 명시적으로 하지 않았더라도, `AutoCloseable` 을 구현한 클래스라면 자동으로 실행됨을 확인할 수 있음.

# QnA
![image](https://user-images.githubusercontent.com/22140570/96356920-f49d0b80-112f-11eb-908e-b889ee25d21f.png)

