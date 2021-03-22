# [아이템 88] readObject 메서드는 방어적으로 작성하라

## 직렬화된 코드는 언제든 역직렬화가 가능하다.

- 부제목의 컨셉을 이용해서 역직렬화를 통한 참조가 가능하다 . 아래는 해당 메커니즘을 이용한 공격 방식이다.

```java
class Period {
    Date startDate;
    Date endDate;
    
}
```

```java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public class AttackPeriod {

    Period period;
    Date startDate;
    Date endDate;

    public void serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream();

        os.write(new Period(new Date(), new Date()));

        //ByteCode 를 통한 참조 진행....

        // 참조 설정 후
        ObjectInputStream ois = new ObjectInputStream();
        period = (Period) ois.readObject();
        startDate = (Date) ois.readObject();
        endDate = (Date) ois.readObject();
    }
}
```

```java
public static void main(String[]args){
        AttackPerioid at = new AttackPeriod();
        Period p = at.period;
        Date pEnd = at.endDate;
        
        pEnd.setYear(91);
        System.out.println(pEnd);
        }
```

위와 같이하면 91년도로 년도가 바뀌게 된다. 위와 같이 역직렬화 간의 참조를 통한 공격이 가능하므로, 
참조가 일어날 수 있는 부분은 방어적 가변복사를 시행해야 한다.

```java
private void readObject(ObjectInputStream s) {
    s.defaultReadObject();
    
    start = new Date(start.getTime());
    end = new Date(end.getTime());
        }
```
