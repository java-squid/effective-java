## 메서드 이름을 신중히 짓자

### 항상 표준 명명 규칙을 따라야 한다.
그니까 add, remove 를 자신이 만든 자료구조에 적용한다고 했을때, 일반적으로 add 는 자료구조에 추가하는것, remove 는 자료구조에서 없어지는것과 같이. 일반적으로 통용되는 단어들로 작성하는것이 옳다

### 편의 메서드를 너무 많이 만들지 마라
모든 메서드는 각각 자신의 소임을 다해야 한다. 근데 사실 음.. 이부분은 이해가 가지않는다. 관심사 분리가 덜 되어서 메소드가 많이 생기지 말라하는건지, 아니면 그냥 메소드가 많은건 안좋다고 하는건지

### 매개변수 목록은 짧게 유지하자. 4개 이하가 좋다.
만약에 매개변수가 6개라면 다른 메소드로 쪼개어 4 / 2 로 나누거나 2 / 2 / 2 로 나누어 쪼개는 것이 좋다. <br>
사실 관심사를 잘 나누자? 라는 이야기 같다. 도우미 클래스 등등을 생성하여

### 도우미 클래스

예전에 코드스쿼드 과제중에 나는 매개변수 getRange(x1,y1,x2,y2,x3,y3) 로 받았지만 파이로는 Point 라는 도우미 클래스를 만든뒤 <br>

```
ap = Point(x1,y1)
bp = Point(x2,y2)
cp = Point(x3,y3)
```

getRange(ab,bp,cp) 로 받았던 기억이난다. 그 이후로 나도 도우미 클래스를 많이 이용해봐야지 라는 생각이 들곤했다. <br>

### 빌더 패턴

Builder 패턴에 관한 이야기도 나오는데 뭐 앞에서 공부한사람들도 알겠지만, 선택 매개변수를 선언이 가능하거나, 매개변수가 많을때 사용하면 좋은 방법인데,
간단한 구현은 아래코드와 같다.

```java
public class NutritionFactsBuilder {


    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 => 기본값으로 초기화 해줘야함.
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int calories){
            this.calories = calories;
            return this;
        }

        public Builder fat(int fat){
            this.fat = fat;
            return this;
        }

        public Builder sodium(int sodium){
            if(sodium < 0){
                throw new IllegalArgumentException();
            }
            this.sodium = sodium;
            return this;
        }

        public Builder carbohydrate(int carbohydrate){
            this.carbohydrate = carbohydrate;
            return this;
        }

        public NutritionFactsBuilder build(){
            return new NutritionFactsBuilder(this);
        }
    }

    public NutritionFactsBuilder(Builder builder){
        servings = builder.servingSize;
        servingSize = builder.servingSize;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}

---- main

   public static void main(String[] args) {
        NutritionFactsBuilder build = new NutritionFactsBuilder.Builder(20, 84)
                .calories(10).fat(20).carbohydrate(30).sodium(40).build();
    }

```

### 매개변수의 타입으로는 인터페이스가 낫다

이건 조금 색달랐는데, 항상 DI 방식 말고는 인터페이스로 넘겨볼 생각을 잘 못했던것 같다. <br>
예를들면 (HashSet set) 이런것보다는 (Set set) 으로 했을때, 어차피 구현체는 바뀌어도 로직상에 문제는 생기지 않으니까.. <br>
이런걸 너무 늦게 알지는 않았나 싶다..?

### boolean 보다는 원소 2개짜리 열거타입이 낫다

```
enum TemperatureScale {FAHRENHEIT, CELSIUS}

Thermometer.newInstance(true);
Thermometer.newInstance(TemperatureScale.FAHRENHEIT);
```

저게더 보기 좋을때는 저렇게 사용하라는 뜻이다. 근데 true 를 받아줘야 작동되는 로직에는 true 를 쓰는게 좋다