# [아이템 78] 공유 중인 가변 데이터는 동기화해 사용하라

## Recording

쓰레드간 쓰기 연산의 가시성 문제는 컴파일러의 코드 재배열(recording) 에 의해 더 악화됩니다.

예들 들면 쓰기 연산을 나중에 하는 것이 효율적으로 여길 수 있기 때문에 이러한 프로그래밍의 의미를 바꾸지 않는 선에서의 recording 이 일어날 수 있습니다.

예를들면 아래의 코드에서 writer() 와 reader() 를 각기 다른 Thread 에서 동시에 실행시킨다고 하면,

```java
class Reordering {
    int x = 0, y = 0;
    public void writer() {
        x = 1;
        y = 2;
    }

    public void reader() {
        int r1 = y;
        int r2 = x;
    }
}
```

동시에 실행될 경우 만약에 writer() 가 뒤로 밀릴 경우 reader() 는 0,0 을 보게 될수도 있고, reader() 가 뒤로 밀린다면 1,2 를 보게 될 수도 있습니다.

## Synchronized

```java
public class Test {

    private static boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(()->{
            int i = 0;
            while(!stopRequested){
                i++;
            }
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```

위의 코드는 1초 뒤에 종료될것 같지만, 거의 무한하게 실행된다.

위의 recording 의 이유로 생각해봤을때 만약 프로세스에서 while 문에서 돌고 있는 것의 우선순위를 둔다면 (read()) 즉 우리는 `stopRequested = true;` 의 결과가 언제 반영될지 알 수 없다.

계속해서 recording 이 될 수 있기 때문이다. 그래서 자바에서는 `volatile` 또는 `synchronized` 의 방법을 제공한다.

그래서 책에서 나온대로 동기화를 보장하여 스레드 간의 통신을 우리가 보장해주는 것이다.

```java
public class Test2 {

    private static boolean stopRequested;

    private static synchronized void requestStop(){
        stopRequested = true;
    }

    private static synchronized boolean stopRequested(){
        return stopRequested;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(()->{
            int i = 0;
            while(!stopRequested()){
                i++;
            }
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        requestStop();
    }
}
```

이럴 경우 1초가 지나고 나서 해당 코드는 종료가 된다.

우리가 synchronized 로 통신을 보장해줄 수 있는 이유는 해당 stopRequested 객체 자체에 락이 걸리기 때문에

한 MainThread 에서 1초가 지나고 requestStop() 을 날리면 stopRequested 에 lock 이 걸리고, stopRequested() 에서 lock 이 풀리면 해당 수정사항이 반영된 stopRequested 를 확인하여 프로그램이 종료 되게 된다.

## Volatile

근데 위의 방식은 굳이 배타적으로 수행될 이유가 없다. 왜냐하면 `long` 과 `double` 을 제외한 기본형은 원자적이기에, 우리는 배타적 수행방식을 지켜줄 이유가 없다.

(long 과 double 이 원자적인 이유는 기계어 레벨에서 많은 단계를 거치기에 그 도중에 방해를 받을 수도 있기 때문(?) 이라고 한다.)

여튼 그러니 배타적 통신을 제외한 속도를 조금은 올리기 위해 `volatile` 을 써보자.

```java
public class Test3 {

    private static volatile boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(()->{
            int i = 0;
            while(!stopRequested){
                i++;
            }
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```

이렇게 하면 정상적으로 수행된다. 그런데 아까 말했듯이 원자적인 수행과정을 보장하지 않는 곳에 volatile 에 효과를 기대하기는 어렵다. 예를 들면

```java
public class asdasd {

    private static volatile int number = 0;

    public static void main(String[] args) {
        Thread backgroundThread = new Thread(()->{
             plusNum();
        });
        Thread backgroundThread2 = new Thread(()->{
            plusNum();
        });
        backgroundThread.start();
        backgroundThread2.start();
        System.out.println(number);
    }

    public static int plusNum(){
        return number++;
    }
}
```

위와 같이 연산이 멀티 스레드에서 일어나고 있는데 끼어들게 되면, 0에서 두 쓰레드가 동시에 돌려서 2가 나와야 되는데 1이 나오게 될 수도 있다. 이런 오류를 `안전실패` 라고 한다고 한다. ㅂ

실제로 내 코드에서는 1이 나온다. 더웃긴건 ㅋㅋ 0이 나올때도 있다는 것이다. (`recording` 때문인것 같다.)

좀 중점적인건, 바이트 코드상에서 `a++` 은 몇가지의 단계를 보이는데 그 단계 중 다른 스레드가 끼어들 수 있다는 것이다. 그니까 뭐 `aload_0` 한다음 이제 연산을 진행할텐데?

그 도중에 다른 스레드가 끼어들 수 있다는 것이다. 그래서 이런 부분에서는 `synchronized` 를 사용해 주어야 한다.

## 내 생각

`volatile` 그리고 `synchronized` 를 가르는 기준은 배타적 수행까지 해야 하나, 아니면 단순히 스레드간 통신만 진행하면 되는 건가에 따라 갈리는 것 같다.

`volatile` 은 배타적 수행은 책임져 주지 않기에 Thread 간 통신만 보장해주기 때문이다. 보통은 가변 객체를 공유하지 않는 것이 좋으나 공유하게 되는 상황이 온다면 해당 지식이 도움이 상당한 도움이 될 것 같다.
