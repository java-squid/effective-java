# 아이템45. 스트림은 주의해서 사용하라 

## 핵심 
- 스트림을 과용하면 프로그램이 읽거나 유지보수하기 어려워진다. 
- 스트림과 반복 중 어느 쪽이 나은지 확신하기 어렵다면 둘 다 해보고 더 나은 쪽을 택하라. 

# 지연평가(lazy evaluation) 

의미대로 나중에 평가(계산할지 말지)를 한다는 의미. 스트림에서는 종단연산에서 평가가 이루어진다. 예를 들어 아래 코드에서 filter에서 연산을 수행하지 않는다. 마지막 forEach에서 filter에 연산을 수행하고, 해당 값이 종단 연산에 해당 안되면(평가를 해서) 적용하지 않는다.

```java

import java.util.*;

public class MyClass {
    public static void main(String args[]) {
      List<Integer> list = List.of(1,2,3);
      list.stream().filter(s -> s > 0).forEach(System.out::println);
    }
}


```
