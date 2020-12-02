# 아이템33. 타입 안전 이종 컨테이너를 고려하라

# 책정리

```
아이템26
아이템28
```

# 컨테이너

제네릭은 Set, Map 컬렉션과 ThreadLocal<T>, AtomicReference<T> 단일원소 컨테이너에도 흔히 쓰인다.이런 모든 쓰임에는 매개변수화되는 대상은 컨테이너 자신이다. ex) Set<T> 하나, Map<K,V> 두 개

그러나 더 유연한 수단이 필요할 때도 종조있다. 예를 들어 데이터베이스의 행은 임의 개수의 열을 가질 수 있는데,  모두 열을 타입 안전하게 이용할 수 있다면 좋을 것이다.

## 해법

컨테이너 대신 키를 매개변수화한 다음, 컨테이너에 값을 넣거나 뺄 때 매개변수화한 키를 함께 제공하면 된다. 이렇게 하면 제네릭 타입 시스템이 값의 타입이 키와 같음을 보장해줄 것이다.

> 타입 안전 이종 컨테이너 패턴(type safe heterogeneous container pattern)

# 타입 안전 이종 컨테이너 패턴

예시) 타입별로 즐겨 찾는 인스턴스를 저장하고 검색할 수 있는 Favorites 클래스

```java
public class Favorites {
    
    public <T> void putFavorite(Class<T> type, T instance) {

    }

    public <T> T getFavorite(Class<T> type) {
        
    }
}
public class Main {
    public static void main(String[] args) {
        Favorties f = new Favorties();

        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 0xcafebebe);
        f.putFavorite(Class.class, Favorties.class);

        String favoriteString = f.getFavorite(String.class);
        int favoriteInteger = f.getFavorite(Integer.class);
        Class<?> favoriteClass = f.getFavorite(Class.class);
    }
}
```

Favorites 인스턴스는 타입 안전하다. 모든 키의 타입이 제각각이라, 일반적인 맵과 달리 여러 가지 타입의 원소를 담을 수 있다.

→ Favorites 는 타입 안전 이종 컨테이너라 한다.

### 타입 안전 패턴 컨테이너 패턴 - 구현

```java
public class Favorites {
		private Map<Class<?>, Object> favorites = new HashMap<>();
    
    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), instance);
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}
```

Favorites가 사용하는 private 맵 변수인 favorites의 타입은 Map<Class<?>, Object> 이다. 비한정적 와일드카드타입이라 이 맵 안에 아무것도 넣을 수 없다고 생각할 수 있지만, 사실은 그 반대다.

와일드카드 타입이 중첩(nested)되었다는 점을 알아야 한다. 맵이 아니라 키가 와일드카드 타입이다. → 모든 키가 서로 다른 매개변수화 타입일 수 있다는 뜻으로

Class<String> or Class<Integer>식으로 될 수 있다.

------

favorites 맵의 값 타입은 단순히 Object이다.

→ 이 맵은 키와 값 사이의 타입 관계를 보증하지 않는다는 뜻이다. → 즉, 모든 값이 키로 명시한 타입임을 보증하지 않는다.

### putfavorite() 구현

Class 객체와 즐겨찾기 인스턴스를 favorites에 추가해 관계를 지으면 끝이다. 키와 값 사이의 '타입 링크' 정보는 사라진다.

그러나 getFavorite 메서드에서 이 관계를 되살릴 수 있다.

### getFavortie() 구현

먼저, 주어진 Class 객체에 해당하는 값을 favorites 맵에서 꺼낸다. 이 객체가 바로 반환해야 할 객체가 맞지만, 잘못된 컴파일타임 타입을 가지고있다. 이 객체의 타입은 Object이나, 이를 T로 바꿔 반환해야 한다.

따라서 getFavortie() 구현은 Class의 cast 메서드를 사용해 이 객체 참조를 Class 객체가 가리키는 타입으로 동적 형변환한다.

### cast()

cast 메서드는 형변환 연산자의 동적 버전이다. 이 메서드는 단순히 주어진 인수가 Class 객체가 알려주는 타입의 인스턴스인지를 검사한 다음, 맞다면 그 인수를 그대로 반환하고, 아니면 ClassCastException을 던진다.

클라이언트 코드가 깔끔히 컴파일된다면 getFavorite이 호출하는 cast는 ClassCastException을 던지지 않을 것이다.

→ favorties 맵 안의 값은 해당 키의 타입과 항상 일치함을 알고 있다.

그런데 cast 메서드가 단지 인수를 그대로 반환하기만 한다면 왜 사용할까?

cast 메서드의 시그니처가 Class 클래스가 제네릭이라는 이점을 활용하기 때문이다.

```java
public class Class<T> {
		T cast(Object obj);
}
```

이것이 정확히 getFavorite 메서드에 필요한 기능으로, T로 비검사 형변환하는 손실 없이다 Favorites를 타입 안전하게 만드는 비결이다.

## Favorite클래스의 제약사항

### 1. 악의적인 클라이언트가 Class 객체를 제네릭이 아닌 로타입으로 넘기면 Favorites 인스턴스의 타입 안전성이 깨진다.

이렇게 짜여진 클라이언트 코드에서는 컴파일할 때 비검사 경고가 뜰 것이다.

Favorites가 타입 불변식을 어기는 일이 없도록 보장하려면 putFavorite 메서드에서 인수로 주어진 instance의 타입이 type으로 명시한 타입과 같은지 확인하면 된다.

```java
// 동적 형변환으로 런타임 타입 안전성 확보
public <T> void putFavorite(Class<T> type, T instance) {
		favorites.put(Objects.requireNonNull(type), type.cast(instance));
}
```

java.util.Collections에는 checkedSet, checkedList, checkedMap 같은 메서드가 있는데, 바로 이 방식을 적용한 컬렉션 래퍼들이다. 이 정적 팩터리들은 컬렉션, 맵과 함께 1개(또는 2개)의 Class 객체를 받는다.

이 메서드들은 모두 제네릭이라 Class 객체와 컬렉션의 컴파일타임 타입이 같음을 보장한다. 또한 이 래퍼들은 내부 컬렉션들을 실체화한다.

ex) Coin을 Collection<Stamp> 에 넣으려 하면 ClassCastExcpetion을 던진다.

이 래퍼들은 제네릭과 로 타입을 섞어 사용하는 애플리케이션에서 클라이언트 코드가 컬렉션에 잘못된 타입의 원소를 넣지 못하게 추적하는데 도움을 준다.

### 2. 실체화 불가 타입에는 사용할 수 없다.

String이나 String[]은 저장할 수 있어도 즐겨 찾는 List<String>은 저장할 수 없다. List<String>을 저장하려는 코드는 컴파일되지 않는다. List<String>용 Class 객체를 얻을 수 없기 때문이다.

## Favorites가 사용하는 타입 토근은 비한정적이다.

getFavorite와 pubtFavorite는 어떤 Class 객체든 받아들인다.

이 메서드들이 허용하는 타입을 제한하고 싶을 수 있는데, 한정적 타입 토큰을 활용하면 된다.

## **한정적 타입 토큰이란?**

한정적 타입 매개변수나 한정적 와일드카드를 사용하여 표현 가능한 타입을 제한하는 타입 토큰이다.

애너테이션 API는 한정적 타입 토큰을 적극적으로 사용한다.

ex) AnnotatedElement 인터페이스에 선언된 메서드로, 대상 요소에 달려 있는 애너테이션을 런타임에 읽어 오는 기능을 한다. 이 메서드는 리플렉션의 대상이 되는 타입들, 즉 클래스(java.lang.Class<T>), 메서드(java.lang.reflect.Method), 필드(java.lang.reflect.Field) 같이 프로그램 요소를 표현하는 타입들에서 구현한다.

```java
public <T extends Annotation>
		T getAnnotation(Class<T> annotationType);
```

annotationType 인수는 애너테이션 타입을 뜻하는 한정적 타입 토큰이다.

이 메서드는 토큰으로 명시한 타입의 애너테이션이 대상 요소에 달려 있다면 그 애너테이션을 반환하고, 없다면 null을 반환한다. 즉, 애너테이션된 요소는 그 키가 애너테이션 타입인, 타입 안전 이종 컨테이너인 것이다.

컴파일 시점에서 타입을 알 수 없는 애너테이션을 asSubclass 메서드를 사용해 런타임에 읽어내는 예시.

```java
static Annotation getAnnotation(AnnotatedElement element,
																String annotationTypeName) {
		Class<?> annotationType = null; // 비한정적 타입 토큰
		try {
				annotationType = Class.forName(annotationTypeName)
		} catch (Exception ex) {
				throw new IllegalArgumentException(ex);
		}
		return element.getAnnotation(
				annotationType.asSubclass(Annotation.class));
}
```

이 메서드는 오류나 경고 없이 컴파일 된다.

## 결론

컬렉션 API로 대표되는 일반적인 제네릭 형태에서는 한 컨테이너가 다룰 수 있는 타입 매개변수의 수가 고정되어 있다.

하지만 컨테이너 자체가 아닌 키를 타입 매개변수로 바꾸면 이런 제약이 없는 타입 안전 이종 컨테이너를 만들 수 있다.

타입 안전 이종 컨테이너는 Class를 키로 쓰며, 이런 식으로 쓰이는 Class 객체를 타입 토큰이라 한다.

예를 들어 데이터베이스의 행(컨테이너)을 표현한 DatabaseRow 타입에는 제네릭 타입인 Column<T> 를 키로 사용할 수 있다.