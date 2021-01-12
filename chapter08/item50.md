# [ITEM 50] - 적시에 방어적 복사본을 만들라 

책에서는 자바를 OverFlow 혹은 UnderFlow 등등의 오류 에서 안전하다고, 안전한 언어라고 말하고 있다. <br>
사실 와닿지 않아서 인터넷에서 조금 검색을 해보니 C 코딩 표준의 규칙은 100여가지이고, 자바는 170여가지가 있다고 한다. <br>
여기서 **심각도가 높은 규칙**이란 단어가 나오는데, 해당 규칙은 메모리의 변형을 일으키는 정도에 따라 심각도를 분류한다고 한다 <br>
그래서 C 언어를 보면 여러가지 메모리를 참조하거나 주소값 참조등의 방식이 많아, 심각도를 일으킬 수 있는 규칙이 많고, <br>
Java 는 그에 비해 우리가 직접적으로 메모리의 주소등을 건드릴 방법이 없어 더 안전하다고 한다. <br>

뭐 이건 중요한 내용이 아닌것 같으니, 일단 본문의 내용으로 가겠다! <br>
집중해야 할 내용은 이거 같다. **클라이언트가 여러분의 불변식을 깨뜨리려 혈안이 되어 있다고 가정하고, 방어적으로 프로그래밍 해야한다** <br>

아래의 예시를 보자

```java
public class Item50 {

    private final Date start;
    private final Date end;

    /**
     * @Param start 시작 시간
     * @Param end 종료 시간
     * @throws IllegalArgumentException 시작시간이 종료시간보다 늦을때 발생한다.
     */

    public Item50(Date start, Date end){
        if(start.compareTo(end) == 1){
            throw new IllegalArgumentException();
        }
        this.start = start;
        this.end = end;
    }

    public Date getStart(){
        return start;
    }

    public Date getEnd(){
        return end;
    }
}
```

이렇게 코드를 짤경우 앞의 start 가 더크면 compareTo 함수에서 1이 반환되어 IllegalArgumentException 을 발생시키게 된다. <br>
매우 정상적인 로직이다. 

```java
    public static void main(String[] args) {
        Date start = new Date();
        start.setYear(10);
        Date end = new Date();
        end.setYear(11);
        Item50 item50 = new Item50(start, end);
    }
```

위의 구문은 매우 정상적으로 작동하며, 아래와같이 end 를 바꾸면 정상적으로 Error 를 return 해준다.

```java
    public static void main(String[] args) {
        Date start = new Date();
        start.setYear(10);
        Date end = new Date();
        end.setYear(11);
        Item50 item50 = new Item50(end, start);
    }
```

하지만 이를 침범할 수 있는 경우가 있다. 우리가 자바에서 알아야될껀 우리는 지금 객체 참조를 넘겨준다는 것이다 <br>
아래와 같이 코드를 바꿔보자.

```java

    public static void main(String[] args) {
        Date start = new Date();
        start.setYear(10);
        Date end = new Date();
        end.setYear(11);
        Item50 item50 = new Item50(start, end);
        end.setYear(9);

        Date end1 = item50.getEnd();
        int year = end1.getYear();

        System.out.println(year);
    }
```

이렇게하면 저기 sout 에 어떤것이 나올까? 바로 9가 나온다. 우리는 객체의 참조값을 넘겨주었기때문에, <br>
우리가 객체를 바꾸게 되도, 우리의 item50 객체속의 end 객체또한 그 객체를 참조하고 있으므로, 이런 문제가 발생하는 것이다. <br>
우리는 그래서 이러한 공격으로 부터 방어할 수단을 클래스 내에 설계해야 한다. <br>

## 가변 매개변수를 방어적으로 복사

우리가 객체 참조 공격으로 부터 우리의 로직을 방어하려면 어떻게 해야할까? <br>
우리의 클래스는 객체의 값만 복사하여, 그것을 바탕으로 다른 객체를 생성해주는 것이다. <br>
아래의 코드 처럼말이다. <br>

```java
    public Item50(Date start, Date end){
        if(start.compareTo(end) == 1){
            throw new IllegalArgumentException();
        }
        this.start = new Date(start.getTime()) ;
        this.end = new Date(end.getTime());
    }
```

위의 코드를 보면 매개변수로 오는 값을 받아서, 또 다른 Date 객체를 생성해준다. <br>
이제 우리의 this.start 는 새롭게 생성된 Date 를 기반으로 힙영역에서 객체를 참조하게 된다. <br>
그럼 이렇게 코드를 바꾸고 아까의 테스트를 한번 돌려보자 <br>

```java
public class Main {

    public static void main(String[] args) {
        Date start = new Date();
        start.setYear(10);
        Date end = new Date();
        end.setYear(11);
        Item50 item50 = new Item50(start, end);
        end.setYear(9);

        Date end1 = item50.getEnd();
        int year = end1.getYear();

        System.out.println(year);
    }

}
```

이번에는 출력값이 9가 아닌 11로 정상적으로 나온다. <br>
우리는 이로써 객체 방어에 성공해 냈다. 근데 책을 읽다보니 한가지 의구심이 든다. 단순히 setter 에만 객체복사를 하는것이 맞는가? <br>
만약 코드를 다음과 같이 바꿔보자. <br>

```java
    public static void main(String[] args) {
        Date start = new Date();
        start.setYear(10);
        Date end = new Date();
        end.setYear(11);
        Item50 item50 = new Item50(start, end);
        end.setYear(9);

        Date end1 = item50.getEnd();

        end1.setYear(180);

        Date end2 = item50.getEnd();

        System.out.println(end2.getYear());
    }
```

이렇게 하니, getter 를 통해서도 우리는 참조를 통한 공격이 가능해졌다. <br>
따라서 우리는 getter 도 방어해줄 의무가 생겼다. <br>
그래서 아래와 같이 코드를 변경해 주었다. <br>

```java
public class Item50 {

    private final Date start;
    private final Date end;

    /**
     * @Param start 시작 시간
     * @Param end 종료 시간
     * @throws IllegalArgumentException 시작시간이 종료시간보다 늦을때 발생한다.
     */

    public Item50(Date start, Date end){
        if(start.compareTo(end) == 1){
            throw new IllegalArgumentException();
        }
        this.start = new Date(start.getTime()) ;
        this.end = new Date(end.getTime());
    }

    public Date getStart(){
        return new Date(start.getTime());
    }

    public Date getEnd(){
        return new Date(end.getTime());
    }
}
```

이렇게 바꾼후 테스트를 돌려보면 정상적으로 11이 출력된다.

## 느낀점

근데 이 방법은 정말 심사숙고 해서 써야한다는 생각이 들었다. <br>
현재 getter 와 setter 를 포함하면 도합 4개의 인스턴스를 생성해내게 되는데, 이런 방식은 많이 호출되는 구조에서는 오히려 부하를 일으킬 수 있다고 생각된다. <br>
Date 의 경우 요즈음에는 LocalDate 를 통해 안전하게 방어해낼 수 있다고 한다. 클래스를 보니 setter 는 존재하지 않는다. <br>
문득 든 생각인데 이런 구조로 불가피하게 짜야한다면 어쩔 수 없지만, 애초에 Setter 를 제한적으로 제공하거나, 필요하지않다면 없애는 것이 좋다고 생각이든다 <br>
무분별한 Setter 의 남발이 안좋다고 말하는 이유 중 한가지가 아닐까? 라는 생각이 든다. <br>
