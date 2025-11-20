package maskun.quietchatter.member.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IdTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "12",//짧음, 김
            " ", "", //공백
            "영문자가아님," //영숫자
    })
    void invariant(String bad) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Id(bad));
    }
}
