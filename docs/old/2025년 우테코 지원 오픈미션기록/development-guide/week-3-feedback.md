# 3주 차 피드백

3주 차 미션의 학습 목표는 **클래스 분리와 단위 테스트를 통해 코드를 구조적으로 사고하는 것**이었습니다.
이 학습 목표는 우테코 정규 과정에서도 이어지기 때문에 아직 어렵더라도 너무 걱정하지 않으셔도 됩니다.
더 좋은 코드를 작성하는데 점진적으로 익숙해지는 과정이라고 생각해 주세요.

지난 몇 주 동안 새로운 방식을 배우고 직접 적용하면서 쉽지 않은 순간도 많았을텐데요.
마지막 미션까지 온 만큼 조금만 더 힘을 내서 이번 주 차도 즐거운 성장의 시간으로 마무리할 수 있기를 바랍니다.

남은 기간은 오픈미션 기간입니다.
이제는 미션을 완성하는 것보다, 도전과 몰입의 흔적 속에서 무엇을 배웠는가를 돌아볼 때입니다.

오픈미션에 대한 내용은 이따 진행할 설명회를 참고해 주세요.

우아한테크코스는 여러분이 완벽한 답을 찾는 개발자가 아니라, 스스로 성장의 이유를 만들어가는 사람으로 나아가길 응원합니다.

## 공통 피드백

### 함수(메서드) 라인에 대한 기준도 적용한다

프로그래밍 요구사항에는 함수의 길이를 15라인으로 제한하는 규칙이 포함되어 있다. 이 규칙은 **main()** 함수도 동일하게 적용되며, 공백 라인도 한 라인으로 간주한다. 만약 함수가 15라인을 초과한다면, 역할을 더 명확하게 나누고, 코드의 가독성과 유지보수성을 높일 수 있는 신호로 인식하고 함수 분리 또는 클래스 분리를 고려해야 한다.

### 예외 상황에 대한 고민한다

정상적인 상황을 구현하는 것보다 예외 상황을 모두 고려하여 프로그래밍하는 것이 훨씬 어렵다. 하지만, 이러한 예외 상황을 처리하는 습관을 들이는 것이 중요하다. 코드를 작성할 때는 예상되는 예외를 미리 고려하여, 프로그램이 비정상적으로 종료되거나 잘못된 결과를 내지 않도록 한다.

예를 들어, 로또 미션에서 고려할 수 있는 예외 상황은 다음과 같다.

- 로또 구입 금액에 1000 이하의 숫자를 입력
- 당첨 번호에 중복된 숫자를 입력
- 당첨 번호에 1~45 범위를 벗어나는 숫자를 입력
- 당첨 번호와 중복된 보너스 번호를 입력

### 비즈니스 로직과 UI 로직의 분리한다

비즈니스 로직과 UI 로직을 한 클래스에서 처리하는 것은 단일 책임 원칙(SRP)에 위배된다. 비즈니스 로직은 데이터 처리 및 도메인 규칙을 담당하고, UI 로직은 화면에 데이터를 표시하거나 입력을 받는 역할로 분리한다. 아래는 비즈니스 로직과 UI 로직이 혼재되어 있다.

```
public class Lotto {
    private List<Integer> numbers;

    // 로또 숫자가 포함되어 있는지 확인하는 비즈니스 로직
    public boolean contains(int number) {
        ...
    }

    // UI 로직
    private void print() {
        ...
    }
}
```

비즈니스 로직은 그대로 유지하고, UI 관련 코드는 별도 View 클래스로 분리하는 것이 좋다. 현재 객체의 상태를 보기 위한 로그 메시지 성격이 강하다면, **toString()** 메서드를 통해 상태를 표현한다. 만약 UI에서 사용할 데이터가 필요하다면 getter 메서드를 통해 View 계층으로 데이터를 전달한다.

### 연관성이 있는 상수는 static final 대신 enum을 활용한다

연관성이 있는 상수는 static final 대신 enum을 활용하는 것이 더 효과적이다. 이렇게 하면 관련된 상수를 그룹화하고, 각 상수에 관련된 속성과 행동을 부여할 수 있다. 이는 코드의 가독성과 유지보수성을 크게 향상시킨다. 아래처럼 로또의 당첨 등수를 나타내는 Rank 열거형은 각 등수마다 일치하는 숫자 개수와 상금을 정의할 수 있다.

```
public enum Rank {
    FIRST(6, 2_000_000_000),
    SECOND(5, 30_000_000),
    THIRD(5, 1_500_000),
    FOURTH(4, 50_000),
    FIFTH(3, 5_000),
    MISS(0, 0);

    private int countOfMatch;
    private int winningMoney;

    private Rank(int countOfMatch, int winningMoney) {
        this.countOfMatch = countOfMatch;
        this.winningMoney = winningMoney;
    }
}
```

val 키워드를 사용해 값의 변경을 막는다

값의 변경을 방지하기 위해 **final** 키워드를 사용하는 것은 매우 중요한 습관이다. 불변 객체(Immutable Object)를 생성함으로써, 값이 한 번 설정되면 그 이후에는 변경되지 않도록 보장할 수 있다. 이는 예기치 않은 값의 변경으로 인한 오류를 방지하고, 코드의 안정성을 높일 수 있다. 최근의 많은 프로그래밍 언어들은 기본적으로 불변성을 추구하고 있으며, 자바에서도 **final** 키워드를 통해 변경을 막을 수 있다.

```
public class Money {
    private final int amount;

    public Money(final int amount) {
        ...
    }
}
```

### 객체의 상태 접근을 제한한다

객체의 상태 접근을 제한하는 것은 캡슐화(Encapsulation)의 중요한 원칙 중 하나다. 인스턴스 변수의 접근 제어자를 private으로 설정하면 외부에서 직접 해당 변수에 접근하거나 수정하는 것을 방지하여 객체의 상태는 외부에서 통제되지 않고, 객체 내에서만 관리될 수 있다.

```
public class WinningLotto {
    private Lotto lotto;
    private Integer bonusNumber;

    // 생성자에서만 상태를 설정
    public WinningLotto(Lotto lotto, Integer bonusNumber) {
        this.lotto = lotto;
        this.bonusNumber = bonusNumber;
    }
}
```

### 객체는 객체답게 사용한다

**Lotto** 클래스는 **numbers**를 상태 값으로 가지는 객체이다. 하지만 아래 객체는 로직 구현 없이 **numbers**에 대한 getter 메서드만을 제공하고 있다.

```
public class Lotto {
    private final List<Integer> numbers;
    
    public Lotto(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public int getNumbers() {
        return numbers;
    }
}

public class LottoGame {
    public void play() {
        Lotto lotto = new Lotto(...);

        // 숫자가 포함되어 있는지 확인한다.
        lotto.getNumbers().contains(number);
        
        // 당첨 번호와 몇 개가 일치하는지 확인한다.
        lotto.getNumbers().stream()...
    }
}
```

**Lotto**에서 데이터를 꺼내지(get) 말고 메시지를 던지도록 구조를 바꿔 데이터를 가지는 객체가 일하도록 한다. 이처럼 **Lotto** 객체에서 데이터를 꺼내(get) 사용하기보다는, 데이터가 가지고 있는 객체가 스스로 처리할 수 있도록 구조를 변경해야 한다. 아래와 같이 데이터를 외부에서 가져와(get) 처리하지 말고, 객체가 자신의 데이터를 스스로 처리하도록 메시지를 던지게 한다.

```
public class Lotto {
    private final List<Integer> numbers;

    public boolean contains(int number) {
        // 숫자가 포함되어 있는지 확인한다.
        ...
    }
    
    public int matchCount(Lotto other) {
        // 당첨 번호와 몇 개가 일치하는지 확인한다.
        ...
    }
}

public class LottoGame {
    public void play() {
        Lotto lotto = new Lotto(...);
        lotto.contains(number);
        lotto.matchCount(...); 
    }
}
```

- [getter를 사용하는 대신 객체에 메시지를 보내자](https://tecoble.techcourse.co.kr/post/2020-04-28-ask-instead-of-getter/)

### 필드(인스턴스 변수)의 수를 줄이기 위해 노력한다

필드의 수가 많아지면 객체의 복잡도가 증가하고, 관리가 어려워지며, 버그가 발생할 가능성도 높아진다. 따라서 필드에 중복이 있거나 불필요한 필드가 없는지 확인하고, 이를 최소화한다.

```
public class LottoResult {
    private Map<Rank, Integer> result = new HashMap<>();
    private double profitRate;
    private int totalPrize;
}
```

위 객체의 **profitRate**와 **totalPrize**는 등수 별 당첨 내역(**result**)만 있어도 모두 구할 수 있는 값이다. 따라서 위 객체는 다음과 같이 하나의 필드만으로 구현할 수 있다.

```
public class LottoResult {
    private Map<Rank, Integer> result = new HashMap<>();

    public double calculateProfitRate() { ... }
    
    public int calculateTotalPrize() { ... }
}
```

### 성공하는 케이스 뿐만 아니라 예외 케이스도 테스트한다

테스트를 작성할 때 성공하는 케이스만 집중하는 경우가 많지만, 예외 상황에 대한 테스트도 중요하다. 특히 프로그램에서 결함이 자주 발생하는 경계값이나 잘못된 입력에 대한 테스트를 꼼꼼히 작성하여 예기치 않은 오류를 방지해야 한다.

```
@DisplayName("보너스 번호가 당첨 번호와 중복되는 경우에 대한 예외 처리")
@Test
void duplicateBonus() {
    assertThatThrownBy(() ->
            new WinningLotto(new Lotto(List.of(1, 2, 3, 4, 5, 6), 6))
    ).isInstanceOf(IllegalArgumentException.class);
}
```

### 테스트 코드도 코드다

테스트 코드 역시 코드의 일환이므로, 리팩터링을 통해 지속적으로 개선해 나가는 것이 중요하다. 특히, 반복적으로 수행하는 부분이 있다면 중복을 제거하여 유지보수성을 높이고 가독성을 향상시켜야 한다. 단순히 파라미터 값만 바뀌는 경우라면, 파라미터화된 테스트를 통해 중복을 줄일 수 있다.

```
@DisplayName("천원 미만의 금액에 대한 예외 처리")
@ValueSource(strings = {"999", "0", "-123"})
@ParameterizedTest
void underLottoPrice(Integer input) {
    assertThatThrownBy(() -> new Money(input))
            .isInstanceOf(IllegalArgumentException.class);
}
```

### 테스트를 위한 코드는 구현 코드에서 분리되어야 한다

테스트를 위해 구현 코드를 변경하는 것은 좋지 않은 습관이다. 테스트 코드를 작성하다 보면 테스트를 더 쉽게 하기 위해 접근 제어자를 변경하거나, 테스트에서만 사용되는 메서드를 구현 코드에 추가하는 경우가 있다. 그러나 이렇게 하면 구현 코드가 테스트에 종속되며, 캡슐화가 깨지고 코드의 일관성이 저해된다. 아래 두 케이스는 특히 유의하자.

- 테스트를 위해 **접근 제어자**를 바꾸는 경우
- 테스트 코드에서만 사용되는 메서드

### 단위 테스트하기 어려운 코드를 단위 테스트하기

아래 코드는 **Random** 때문에 **Lotto**에 대한 단위 테스트를 하기 힘들다. 단위 테스트가 가능하도록 리팩터링한다면 어떻게 하는 것이 좋을까?

```
import camp.nextstep.edu.missionutils.Randoms;

public class Lotto {
    private List<Integer> numbers;

    public Lotto() {
        this.numbers = Randoms.pickUniqueNumbersInRange(1, 45, 6);
    }
}

——————

public class LottoMachine {
    public void execute() {
        Lotto lotto = new Lotto();
    }
}
```

올바른 로또 번호가 생성되는 것을 테스트하기 어렵다. 테스트하기 어려운 것을 클래스 내부가 아닌 외부로 분리하는 시도를 해 본다.

```
public class Lotto {
    private List<Integer> numbers;

    public Lotto(List<Integer> numbers) {
        this.numbers = numbers;
    }
}

——————

public class LottoMachine {
    public void execute() {
        List<Integer> numbers = Randoms
            .pickUniqueNumbersInRange(1, 45, 6);
        Lotto lotto = new Lotto(numbers);
    }
}
```

위 코드는 A 상황을 B로 바꾼 것이다.

```
A.
main(테스트하기 어려움)
     ⬇️
LottoMachine(테스트하기 어려움)
     ⬇️
Lotto(테스트하기 어려움) ➡️ Randoms(테스트하기 어려움)
```

```
B.
main(테스트하기 어려움) 
     ⬇️
LottoMachine(테스트하기 어려움) ➡️ Randoms(테스트하기 어려움) 
     ⬇️
Lotto(테스트하기 쉬움)
```

- [메서드 시그니처를 수정하여 테스트하기 좋은 메서드로 만들기](https://tecoble.techcourse.co.kr/post/2020-05-07-appropriate_method_for_test_by_parameter/)

단위 테스트하기 어려운 코드를 단위 테스트하기 쉽게 만드는 방법은 위 설명처럼 테스트하기 어려운 의존성을 외부에서 주입하거나 분리하여 테스트 가능한 상태로 만드는 것이다. 남은 **LottoMachine**은 어떻게 테스트하기 쉽게 바꿀 수 있을지 고민해 본다.

### private 함수를 테스트 하고 싶다면 클래스(객체) 분리를 고려한다

private 메서드는 외부에서 직접적으로 테스트할 수 없으며, 일반적으로 public 메서드를 통해 간접적으로 테스트된다. 하지만, private 메서드가 단순히 가독성을 위한 분리일 뿐만 아니라, 중요한 역할을 수행하는 경우에는 클래스 분리를 고려할 필요가 있다. 이는 단일 책임 원칙(SRP)을 따르는 것이며, 테스트 가능성을 높이는 동시에 코드의 응집도를 향상시킨다.
