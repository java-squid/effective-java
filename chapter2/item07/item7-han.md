# 다 쓴 객체 참조를 해제하라
- 자바는 가비지 컬렉터가 있어, 메모리 관리가 편하다.

- 어떤 객체의 참조를 다 썼을 때, 즉 더 이상 사용하지 않음이 보장될 때 참조 해제를 어떻게 해야할까?
    - `null` 처리를 하면 된다.
    ```java
      public object pop() {
          if(size = 0) throw new EmptyStackException();
          Object result = elements[--size];
          element[size] = null;
          return result;
      }
    ```
    
- `null`처리한 참조를 이용하려고 하면 NullPointerException이 발생한다.

- 객체 참조를 `null` 처리 하는 것은 예외적인 경우에만 사용한다.
    
    - 가장 좋은 방법은, 참조를 담은 변수를 **유효 범위** 밖으로 밀어 내는 것
    
- 사용자 `null` 처리는 언제해야할까?
    - stack의 경우를 보면, 자기 자신의 메모리를 직접 관리하는 경우에 해당 처리를 사용자가 해야할듯 싶다.
    - 즉 자기 메모리를 직접 관리하는 클래스라면, **메모리 누수**에 주의해야 한다!
    
- 캐시 역시 메모리 누수를 일으키는 주범이다!
    - 캐시 외부에서 키를 참조하는 동안만 살아있는 캐시가 필요하다면, `WeakHashMap` 을 사용해 캐시를 만들자.
    - 캐시를 만들 때, 보통 유효기간을 정확히 정의하기 어려우니까, 이따금 해당 엔트리를 청소해줘야한다. ex) LinkHashMap의 EldestEntry 메서드
    
- listener or callback 또한 메모리 누수의 주범
- `ArrayList` 의 `remove()` 메서드는 내부적으로 해당 key를 null처리해준다

---
> `https://d2.naver.com/helloworld/329631` 에 대한 정리

## Java Reference, GC
- Java GC(Garbage Collector) 의 기본적인 작업 2가지
    1. Heap 영역 내의 객체 중, garbage를 찾아낸다.
    2. 찾아낸 garbage를 처리하여, heap의 memory를 회수한다.
- java.lang.ref 패키지에서 제공하는 객체 참조
    - strong reference +  soft, weak, phantom class 
    
### GC, Reachability
- GC는 개체가 처리대상인지 아닌지 판별하기 위해서, reachability라는 개념을 사용한다.
- Heap영역에 있는 객체를 유효하게 참조하고 있으면... -> reachable, 없으면.. -> unreachable
    - unreachable 객체인 경우, GC 수행 대상으로 된다.
- root set : 유효한 참조 여부를 파악할 때 필요한 항상 유효한 최초의 참조를 의미한다.
![image](https://user-images.githubusercontent.com/71244638/93884567-08667500-fd1e-11ea-90a8-bee1dd265e1b.png)
- Heap 영역의 객체 참조는 4가지 있음.
    1. Heap 영역내, 다른 객체에 의한 참조
    2. Java Stack(각각 스레드 생성 시에 독자적으로 생성되는..), 메서드 실행 시에 사용되는 지역 변수와 파라미터에 의한 참조
    3. Native Stack, 즉 JNI(Java Native Interface) 에 의해 생성된 객체에 대한 참조
    4. Method Area의 정적 변수에 의한 참조
- 이들 중 1번을 제외한 나머지 3가지 종류가 root set임
      ![image](https://user-images.githubusercontent.com/71244638/93885035-9e9a9b00-fd1e-11ea-8b37-ca8bd943b8f1.png)
- 위 그림에서 참조는 모두 java.lang.ref패키지를 사용하지 않은 일반적인 참조 이며, strong reference라 부름.
  

### Soft, Weak, Phantom Reference
- WeakReference class
    - 참조 대상인 객체를 캡슐화 한 WeakReference객체를 생성함.
    - 이는 GC가 특별하게 취급함.
    - 생성 예시
    ![image](https://user-images.githubusercontent.com/71244638/93885970-d1915e80-fd1f-11ea-9868-1bf480fe0338.png)
        - Sample 객체를 WeakReference로 감싸서 만들었음.
          ![image](https://user-images.githubusercontent.com/71244638/93885526-30a2a380-fd1f-11ea-9889-e67bbc370f10.png)
        - 마지막 코드 `ex = null;` 에 따라서..
          ![image](https://user-images.githubusercontent.com/71244638/93885652-5e87e800-fd1f-11ea-82a3-4f290036519e.png)
        - WeakReference 내부에서만 참조되고, 이 상태의 Sample 객체를 weakly reachable 객체라 부름
- 용어 정리
    - SoftReference, WeakReference, PhantomReference 3가지 클래스에 의해 생성된 객체를 `reference object`라고 부름
        - strong reference로 표현되는 일반적인 참조나 다른 클래스의 객체와는 달리 **3가지 Reference 클래스의 객체**에 대해서만 사용되는 용어 
    - reference object에 의해 참조된 객체는 `referent`라고 부름.

### Reference, Reachability
- java.lang.ref 패키지를 이용하여, 4가지(strong, softly, weakly, phantomly)로 reachable 객체들을 구분, GC때 동작을 좀 더 세분화할 수 있게 되었음.
- 즉 GC 대상 판별 여부에 사용자 코드가 개입될 수 있게 되었다!
![image](https://user-images.githubusercontent.com/71244638/93886546-888dda00-fd20-11ea-8e12-b837c130283d.png)
- 녹색 네모 : Weakly reachable 객체, 왜? WeakReference로 참조되고 있으니까..
- 파란 네모 : Strongly reachable 객체
- 빨강 네모 : Unreachable 객체
- 일반적으로 GC 동작 할 때, 빨강, 녹색이 대상임
- GC는 어떤 객체는 weakly reachable 객체로 판단하면, WeakReference객체 있는(가리키고 있는) weakly reachable 객체에 대한 참조를 `null` 로 설정한다.
    - 그러면 해당 객체는 빨강 네모, 즉 unreachable 객체와 같은 상태이므로 메모리 회수 대상이 됨.

### Strengths of Reachability
- reachable(strongly reachable, softly reachable, weakly reachable) + unreachable = 5가지, GC가 객체를 처리하는 기준!
- Java GC는 root set으로부터 시작해서 객체에 대한 모든 경로를 탐색하고, 그 경로에 있는 reference object들을 조사하여 그 객체에 대한 reachability를 5가지 중에 결정
  ![image](https://user-images.githubusercontent.com/71244638/93887310-80826a00-fd21-11ea-939d-c5745ab6d986.png)
- ex) softly reachable
  ![image](https://user-images.githubusercontent.com/71244638/93887388-9a23b180-fd21-11ea-82e8-8395614db5c2.png)

### Softly Reachable, SoftReference
- Softly Reachable 객체란?
    - 오직 SoftReference 객체로만 참조된 객체
    - Heap에 남아있는 메모리 크기, 해당 객체의 사용빈도에 따라 GC여부가 결정
    - 즉 weakly reachable 객체보다는 오래 살아 남게 됨.
- Oracle HotSpot VM에서 제공하는 JVM 옵션
    - `-XX:SoftRefLRUPolicyMSPerMB=<N>` , 기본 값은 1000
    - 100초 이상 softly reachable 객체가 사용되지 않으면 GC에 의해 회수 된다는 의미.
- softly reachable 객체가 GC 대상이 되면, SoftReference 객체 내의, softly reachable 객체에 대한 참조가 null로 설정됨.
    - 이 후 해당 객체는 unreachable상태가 되어 메모리가 회수 된다.
### Weakly Reachable, WeakReference
- weakly reachable객체는 GC를 수행할 때마다 회수 대상이 됨.
- 그렇지만 GC의 알고리즘에 따라 다르므로, 반드시 메모리가 회수된다고 보장하지는 않음.
    - 이는 unreachable 객체도 마찬가지
    - GC가 GC 대상을 찾는 과정, 메모리를 회수하는 과정은 연속적인 작업이 아니다.
- softly reachable 객체의 경우, 일정 부분 힙 공간을 점유하고 있을 확률이 높으므로, GC가 더 자주 일어나고, 걸리는 시간도 길어진다.

### ReferenceQueue
> SoftReference 객체나 WeakReference 객체가 참조하는 객체가 GC 대상이 되면 SoftReference 객체, WeakReference 객체 내의 참조는 null로 설정되고 
> SoftReference 객체, WeakReference 객체 자체는 ReferenceQueue에 enqueue된다
- Reference 객체들을 넣는 Queue인듯
- enqueue작업은 GC에 의해 자동으로 수행됨(GC 대상이된 referent 객체를 넣는 것이니까)
- Java Collections 클래스 중, 캐시를 구현하는 용도로 사용되는 WeakHashMap 크래스는 ReferenceQueue와 WeakReference를 사용한다.
- SoftReference, WeakReference의 객체 내부 참조가 null 설정 이후에 enqueue되지만 PhantomReference는 다름

### Phantomly Reachable, PhantomReference
- GC대상 객체를 찾는 작업, 해당 객체를 처리하는 작업은 연속적이지 않다.
- 또한, 객체를 처리하는 작업과, 메모리를 회수하는 작업도 연속적이지 않다.
- phantomly reachable은 객체 finalize와 메모리 회수 사이에 관여한다.
    - 즉 객체에 대한 참조가 PhantomReference만 남게되면, 해당 객체는 바로 finalize된다.
 
- GC가 객체를 처리하는 순서
  - ![image](https://user-images.githubusercontent.com/71244638/93897997-af064200-fd2d-11ea-97e5-8e4505b99d5a.png)
- phatomly reachable을 판별하지 전에, 객체의 finalize를 진행하고, PhantomReference가 있다면 ReferenceQueue에 넣는다.
- 한 번 phantomly reachable로 판명된 객체는 더 이상 사용될 수 없게 됨.
- phantomly reachable로 판명된 객체에 대한 참조를 GC가 자동으로 null로 설정하지 않으므로, 후처리 작업 후에 사용자 코드에서 명시적으로 clear() 메서드를 실행하여 null로 설정해야 메모리 회수가 진행됨을 명심하자

### Conclusion
![image](https://user-images.githubusercontent.com/71244638/93898385-21772200-fd2e-11ea-9341-82cee4acb9f9.png)
- ~Phantom은 거의사용하지 않음...

> Reference
- https://d2.naver.com/helloworld/329631
- https://www.baeldung.com/java-weakhashmap
- https://www.crocus.co.kr/1533
- http://blog.breakingthat.com/2018/08/26/java-collection-map-weakhashmap/

---
# QnA 
![image](https://user-images.githubusercontent.com/22140570/94357480-2f34fa80-00d4-11eb-86c8-15ee987db611.png)
![image](https://user-images.githubusercontent.com/22140570/94357514-686d6a80-00d4-11eb-982a-eba3e120ab78.png)
![image](https://user-images.githubusercontent.com/22140570/94357523-7ae7a400-00d4-11eb-9245-8b18ddf0de54.png)
![image](https://user-images.githubusercontent.com/22140570/94357548-ae2a3300-00d4-11eb-94ec-6fe2ce9074a0.png)
![image](https://user-images.githubusercontent.com/22140570/94357554-c601b700-00d4-11eb-892f-03d6363efd02.png)


