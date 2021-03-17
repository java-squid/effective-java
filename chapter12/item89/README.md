# [아이템 89] 인스턴스 수를 통제해야 한다면 ReadResolve 보다는 열거 타입을 사용하라

직렬화된 클래스(implements Serializable) 에 싱글턴을 적용하기 위한 갖가지 뻘짓을 다루는 아이템이다.
결론은 그런 뻘짓을 할 바에는 Enum 을 쓰라는 것이다.

## Enum 을 사용한 싱글턴

이것만 기억하고 Serializable 은 머릿속에서 지우자

```java
public enum Elvis {
    INSTANCE;
    private String[] favoriteSongs = {"Hound_Dog", "Heartbreak_Hotel"};
    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }
}
```

## readResolve 를 사용한 싱글턴

serializable instance-controlled class 를 작성해야 한다면, 컴파일 타임중에 직렬화된 인스턴스가 타입인지 알아낼 방도가 없으므로 readResolve 를 해야한다.
(나라면 이 경우 readResolve 을 사용하지 않고,
Serializable 을 사용한 코드 혹은 개발자 동료를 찾아내 enum 을 사용하도록 설득하려 한다.)

readResolve 를 사용하면 readObject 가 만든 새로운 인스턴스를, 기존의 인스턴스로 대체해서 바로 가비지 컬렉션이 되게 할 수 있다.
(객체 생성 후 가비지 컬렉션 작업이 이루어져야 한다는 데서, 이미 엄청난 오버헤드가 느껴진다.)

주의점은, readResolve 로 싱글턴을 흉내낼때는 object reference type 의 모든 instance field 는 **transient로 선언**해야한다.
그렇지 않으면 readResolve 메서드가 수행되기전에, "reference to the deserialized object" 를 하여서 가비지 컬렉션을 막아버리고, 새로 만들어진 객체가 유지되도록 할 수 있기 때문이다.

[transient란?](https://nesoy.github.io/articles/2018-06/Java-transient) Serialize에서 제외하겠다는 키워드. 평생 보고 싶지 않다.

또한 readResolve 메서드의 접근자를 무엇으로 해아할지 심각한 고민을 해야한다고 한다.

- private
    - final Class; 즉 하위 클래스에서 사용할 수 없게 하고 싶은 경우
- package-private
    - 같은 패키지의 하위 클래스에서만 사용하고 싶은 경우
- protected & public
    - 모든 하위 클래스에서 사용하게 하고 싶은 경우

만약 하위 클래스에서 readResolve 를 해당 하위 클래스 인스턴스를 반환하도록 override 하지 않는다면,
deserialize 시에 상위 클래스 인스턴스가 반환되기 때문에 ClassCastException 을 일으킬 수 있는 위험이 있다.
즉 상속받은 메서드 임에도, 매번 Override 를 해줘야 할 수도 있다. (이 무슨 병맛이란 말인가!) 

### 실제 구현 코드

```java
public class Elvis implements Serializable {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() {}

    private transient String[] favoriteSongs
            = {"Hound_Dog", "Heartbreak_Hotel"};
    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
```

transient 가 없을 경우, 나쁜짓 하는 방법

```java
public class ElvisStealer {
    static Elvis impersonator;
    private Elvis payload;

    private Object readResolve() {
        // readResolve 가 완료되기전에 Elvis 인스턴스의 참조를 저장
        // 원래대로라면 payload 인스턴스는 가비지 컬렉터에 의해서 삭제되어야 한다.
        // 근데 static 참조로 인해서 죽지 않고 남아있는다.
        impersonator = payload;

        // favoriteSongs 필드에 맞는 타입의 객체를 반환한다.
        return new String[] {"A Fool Such as I"};
    }

    private static final long serialVersionUID = 0;
}
```

```java
public class ElvisImpersonator {
    // 진짜 Elvis 인스턴스로는 만ㄷ르어질 수 없는 바이트 스트림!
    private static final byte[] serializedForm = {}; // 생략

    public static void main(String[] args) throws Exception {
        Elvis elvis = (Elvis) deserialize(serializedForm);
        Elvis impersonator = ElvisStealer.impersonator;

        // [Hound_Dog, Heartbreak_Hotel]
        elvis.printFavorites();

        // [A Foll Such as I]
        impersonator.printFavorites();
    }

    static Object deserialize(byte[] sf) throws Exception {
        return new ObjectInputStream(new ByteArrayInputStream(sf))
                .readObject();
    }
}
```