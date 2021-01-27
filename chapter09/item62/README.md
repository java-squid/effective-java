# [아이템 62] 다른 타입이 적절하다면 문자열 사용을 피하라

### 중요포인트 위주 정리
1. 문자열은 다른 값 타입을 대신하기에 좋지 않다 -> 예를 들어 정수형으로 수치를 받아야 하는데 문자열로 받지말고, Integer.parseInt 등을 통해서 변환하거나
YES / NO 같은 경우도 문자열로 받는 것보다는 적절한 열거타입이나 boolean 형태로 받는 것이 좋다고 한다.
   
2. 문자열은 열거 타입을 대신하기에 적합학지 않다. 

3. 문자열은 혼합 타입을 대신하기에 적합하지 않다. 
```java
String compoundKey = className + "#" + i.next();
```
위처럼 적는 것은 만약 className 이나 i.next() 에 '#' 이 있다면서 혼란스러워 질테고, 이런 키값은 쓰기도 불편할 뿐더러 매번 키값을 조합하고 파싱해야하는 최악의 성능을 보여준다.
불편한것 뿐만 아니라, 데이터 안에 구분작 있는 경우이를 escape 처리를 해줘야 하고 읽기/쓰기 과정에서 이를 별도로 고려하는 코드를 매 문자열마다 수행해야한다.
그래서 별도의 private 정적 멤버 클래스로 선언하라고 한다. 사실 아래방식이 조슈아가 말하는 방식인지는 잘 모르겠다. 근데 아래 방식처럼 사용한다면 일단 구분자가 겹치더라도
getAddr 등을 통해서 간단하게 구분해 낼수 있으니까 더 편하지 않을까? 이 부분을 구현하실수 있으면 이슈에 코드 작성 부탁드립니다.
```java
class Logic{

    public static void main(String[] args) {
        CompoundKey compoundKeyClass = new CompoundKey("AClass", "#1");
        String compoundKey = compoundKeyClass.getCompoundKey();
        String className = compoundKeyClass.getClassName();
        String addr = compoundKeyClass.getAddr();
    }

    private static class CompoundKey{
        private String className;
        private final String seperator = "#";
        private String addr;

        public CompoundKey(String className, String addr){
            this.className = className;
            this.addr = addr;
        }

        public String getCompoundKey(){
            return className + "#" + addr;
        }

        public String getClassName() {
            return className;
        }


        public String getAddr() {
            return addr;
        }

    }
}
```

4. 문자열은 권한을 표현하기에 적합하지 않다. 아래의 코드는 스레드별로 고유 지역변수를 가지기 위해서 사용했던 코드다.
```java
public class ThreadLocal{
    private ThreadLocal(){}
    
    public static void set(String key, Object value);
    
    public static void get(String key);
}
```
위 와 같은 코드로 작성하게 되면, String 은 전역 이름 공간에서 공유된다. 그니까 "A" 라는 단어랑 "A" 라는 단어는 하나의 주소값을 가진다. 다른 개체가 아니다.
new String("A") 와 같은 방식을 하지 않는다면 동일 단어는 같은 개체이다. 그래서 위의 코드는 전역 이름 공간에서 공유되므로 문제가 있다. 그래서 아래와 같은 방식으로 바꿔주어야 한다.
```java
class ThreadLocal {
    private ThreadLocal(){}

    public static class Key {
        Key() {}
    }

    public static Key getKey(){
        return new Key();
    }

    public static void set(Key key, Object value){};
    public static void get(Key key){};

}

class Test{
    public static void main(String[] args) {
        ThreadLocal.Key key = ThreadLocal.getKey();
        ThreadLocal.set(key, "a");
    }
}
```

뭐 위와 같은 방식으로 이용할 수 있을 것 같다. 