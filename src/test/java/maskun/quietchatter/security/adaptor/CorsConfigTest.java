package maskun.quietchatter.security.adaptor;

import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.web.WebConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CorsConfigTest.DummyController.class)
@Import({SecurityConfig.class, WebConfig.class})
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthTokenService authTokenService;

    @MockitoBean
    private AuthMemberService authMemberService;

    @Test
    @DisplayName("허용된 오리진(서브도메인 포함)에서 OPTIONS 요청 시 CORS 헤더가 포함되어야 한다")
    void shouldAllowCorsFromAllowedOrigins() throws Exception {
        String[] allowedOrigins = {
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "https://quiet-chatter.com",
                "http://quiet-chatter.com",
                "https://www.quiet-chatter.com",
                "http://sub.quiet-chatter.com"
        };

        for (String origin : allowedOrigins) {
            mockMvc.perform(options("/api/v1/any")
                            .header("Origin", origin)
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", origin))
                    .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        }
    }

    @Test
    @DisplayName("허용되지 않은 오리진에서 OPTIONS 요청 시 CORS 헤더가 없거나 거부되어야 한다")
    void shouldRejectCorsFromUnallowedOrigins() throws Exception {
        mockMvc.perform(options("/api/v1/any")
                        .header("Origin", "http://malicious-site.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }

    @RestController
    static class DummyController {
        @GetMapping("/api/v1/any")
        public String any() {
            return "ok";
        }
    }
}
