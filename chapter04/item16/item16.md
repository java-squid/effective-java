# 클래스와 인터페이스 (4장)

## #16 : public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

> 필드값을 private으로 설정하고 getter를 사용하자.
>



### 핵심 정리

- public 클래스에서는 절대 가변 필드를 직접 노출해서는 안된다. (불변 필드값에 대해서도 직접 노출을 권장하지 않는다.)
- 필드값을 private으로 설정하고 접근자(getter)를 사용하도록 하자.
- 하지만 package-private 클래스 또는 private 중첩 클래스에서는 필드를 노출하는 편이 나을 때도 있다.



## References

- Effective Java 3/E - Joshua Bloch

-----

# QNA

- [Item16 issue](https://github.com/java-squid/effective-java/issues/16)

<img width="941" alt="item16-1" src="https://user-images.githubusercontent.com/58318041/98538413-12443780-22ce-11eb-85ed-56684709d570.png">

<img width="941" alt="item16-2" src="https://user-images.githubusercontent.com/58318041/98538431-18d2af00-22ce-11eb-816c-61cd5f87a582.png">

<img width="941" alt="item16-3" src="https://user-images.githubusercontent.com/58318041/98538456-20925380-22ce-11eb-9dbb-b11e94127928.png">