package maskun.quietchatter.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import maskun.quietchatter.talk.domain.NotTalkOwnerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@WebMvcTest(controllers = WebExceptionHandlerTest.TestController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import({WebConfig.class, WebExceptionHandlerTest.TestController.class})
class WebExceptionHandlerTest {

    @Autowired
    private MockMvcTester tester;

    @Test
    void testIllegalArgumentException() {
        tester.get().uri("/test/illegal-argument")
                .assertThat()
                .hasStatus(400);
    }

    @Test
    void testNoSuchElementException() {
        tester.get().uri("/test/no-such-element")
                .assertThat()
                .hasStatus(404);
    }

    @Test
    void testNotTalkOwnerException() {
        tester.get().uri("/test/forbidden")
                .assertThat()
                .hasStatus(403);
    }

    @Test
    void testUncaughtException() {
        tester.get().uri("/test/uncaught")
                .assertThat()
                .hasStatus(500);
    }

    @RestController
    public static class TestController {

        @GetMapping("/test/illegal-argument")
        public void throwIllegalArgumentException() {
            throw new IllegalArgumentException("잘못된 입력 테스트");
        }

        @GetMapping("/test/no-such-element")
        public void throwNoSuchElementException() {
            throw new NoSuchElementException("그러한 요소가 없음 테스트");
        }

        @GetMapping("/test/forbidden")
        public void throwNotTalkOwnerException() {
            throw new NotTalkOwnerException();
        }

        @GetMapping("/test/uncaught")
        public void throwUncaughtException() throws Exception {
            throw new Exception("catch되지 않은 예외 테스트");
        }
    }
}
