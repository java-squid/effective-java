# [아이템 81] wait 와 notify 보다는 동시성 유틸리티를 애용하라

## 결론

wait 와 notify 를 사용할 거면 `java.util.concurrent` 패키지를 사용하자.

진짜루, wait 랑 notify 랑 notifyAll 을 사용할 일이 거의 없다. 머릿속에서 이 3개가 있다는 걸 지워버리자.

책 번역이 어렵게 되어서, [동시성(concurrent) 과 동기화(synchronization)](https://www.vogella.com/tutorials/JavaConcurrency/article.html) 가 전혀 다른 말임을 조심하면서 읽자.
발음이 헷갈린다.

### 동시성(Concurrency) vs 동기화(Synchronization)

- Concurrency: _the ability to run several programs or several parts of a program in parallel._
- Synchronization: _the coordination of events to operate a system in unison._

요약하자면, concurrency 는 "병렬 시행(Parallel Execution)" 이 핵심이고,
synchronization 은 OS 가 개발자 의도대로 명령을 실행하도록, 실행순서를 "정렬" 하는 작업이 핵심이라 할 수 있겠다.

개념적으로는 그렇고, 일반적으로 자바에서 synchronize 란 Critical Section 에 락을 거는 것을 뜻한다.

## java.util.concurrent

- Executor Framework (item80)
- Concurrent Collections
- Synchronizers

# Concurrent Collections

- List, Queue, Map 에 concurrency 를 추가
- Concurrent Collection 에게 굳이 락을 추가하지 말자. 느려진다.
- 이펙티브 자바에서 언급된 쓸모있는 collection 들
  - ConcurrentMap
  - Blocking Queue

### ConcurrentMap 을 통한 intern

- Collections.synchronizedMap 보다는 **ConcurrentHashMap** 을 사용하자

ConcurrentMap 은 get 과 같은 검색 기능에 최적화 되어 intern 이 slowintern 보다 빠르다.
꼭 String 이 아니더라도, 커스텀 Value Object 를 만들 때 아래 코드를 따라 하면 매우 유용할 것 같다.

get이 빠른 이유: safety failure 가 발생할리가 없으므로, 동기화로 인한 성능 저하가 없다.

```java
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

public class Intern {
    private static final ConcurrentMap<String, String> map
        = new ConcurrentHashMap<>();

    public static String intern(String s) {
        String result = map.get(s);
        if (result == null) {
            result = map.putIfAbsent(s, s);
            if (result == null) result = s;
        }
        return result;
    }

    public static String slowIntern(String s) {
        String previousValue = map.putIfAbsent(s, s);
        return previousValue == null
                ? s : previousValue;
    }
}
```

### Blocking Queue

- take 메소드는 큐가 비었다면 새로운 원소가 추가될 때까지 현재 쓰레드를 block 함
- Work Queue (Producer-Consumer Queue) 로 쓰기에 적합
- 멀티 쓰레드 환경에서 리액티브 프로그래밍을 하려고 할 때 유용할듯
- ThreadPoolExecutor 에서도 Blocking Queue 활용

# Synchronizers

- CountDownLatch
  - 넘겨받은 count 횟수만큼 .countDown() 함수가 호출 될때까지 await 을 한다.
- [Semaphore](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Semaphore.html):
  - countUp 도 있는 countDownLatch
- [CyclicBarrier](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CyclicBarrier.html)
  - allows a set of threads to all wait for each other to reach a common barrier point
  - 사용처: useful in programs involving a fixed sized party of threads that must occasionally wait for each other
- [Exchanger](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Exchanger.html)
  - synchronization point at which threads can pair and swap elements within pairs.
  - 사용처: swap buffers between threads
- **[Phaser](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Phaser.html)**
  - reusable synchronization barrier
  - CyclicBarrier, CountDownLatch 를 쓸 바에는 Phaser 를 쓰자.

결론: 모두 어디다 쓸지 잘 모르겠다. Executor Framework 나 열심히 써야징

시간 간격을 잴때는 항상 System.currentTimeMills 가 아니라, **System.nanoTime** 을 사용하자.

currentTimeMills → UTC 기준으로 시간, 현재 날짜를 알고 싶을 때 사용
nanoTime → 철저하게 간격을 잴때 쓰는 시간

# Wait / Notify 레거시 코드를 만난 경우

- wait: 스레드가 어떤 조건이 충족되기를 기다리도록 block 한다.
- notify: 기다리는 쓰레드를 임의로 하나 깨운다.
- notifyAll: 기다리는 쓰레드를 모두 깨운다.

wait / notify 를 사용하는 경우는 없다. 신규 코드라면 사용하지 말자.
만약 레거시라서 wait / notify 문법을 유지보수해야할 경우라면, 아래 원칙을 따르자.

- wait 메서드는 반드시 동기화(synchronized) 영역 안에서 호출해야한다.
- notify 대신에 **notifyAll** 을 사용하라.
  - 쓰레드가 악의적으로 wait 를 호출하는 공격으로부터 보호할 수 있다.

wait 메서드를 사용하는 방식

```java
synchronized (obj) {
  while (<조건이 충족되지 않음>) {
    obj.wait(); // 락을 놓고, notify 하면 아래를 실행한다.
  }
  // 조건이 충족되었을 경우의 코드를 실행한다.
}
```

기존의 busy lock 코드를 busy 하지 않게 수정한 느낌이다.
**wait 메서드는 절대로 반복문 밖에서 호출하지 말라.**
