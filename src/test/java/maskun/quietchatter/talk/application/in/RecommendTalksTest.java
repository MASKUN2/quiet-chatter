package maskun.quietchatter.talk.application.in;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import maskun.quietchatter.talk.domain.Talk;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class RecommendTalksTest {

    @Test
    void invariant() {
        List<Talk> talks = Instancio.ofList(Talk.class).size(7).create();
        assertThatIllegalArgumentException().isThrownBy(() -> new RecommendTalks(null));
        assertThatIllegalArgumentException().isThrownBy(() -> new RecommendTalks(talks));
    }

}
