package maskun.quietchatter.adaptor.web.security;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import maskun.quietchatter.adaptor.web.WebConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.Builder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(GuestPromotionTest.FakeController.class)
@Import({SecurityConfig.class,
        WebConfig.class,
        AuthTokenProvider.class,
        GuestPromotionTest.FakeController.class})
class GuestPromotionTest {
    static final String PROMOTION_PATH = "/api/promotion";
    static final String ANONYMOUS_PATH = "/api/anonymous";

    @MockitoBean
    private AuthTokenProvider authTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        when(authTokenProvider.getGuest()).thenReturn(getGuestAuthToken());
    }

    private static @NotNull UsernamePasswordAuthenticationToken getGuestAuthToken() {
        return new UsernamePasswordAuthenticationToken(UUID.randomUUID(), null,
                List.of(new SimpleGrantedAuthority("ROLE_GUEST")));
    }

    @Test
    @DisplayName("필요한 경우 자동 승급")
    void promotion() throws Exception {
        mockMvc.perform(get(PROMOTION_PATH).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(SecurityMockMvcResultMatchers.authenticated().withRoles("GUEST"));
    }

    @Test
    @DisplayName("승급이 필요하지 않은 경우 session을 만들지 않으므로 Authentication이 null임")
    void anonymous() throws Exception {
        mockMvc.perform(get(ANONYMOUS_PATH))
                .andExpect(status().isOk())
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @TestConfiguration
    public static class SecurityTestConfig {

        @Primary
        @Bean
        public GuestPromotion guestPromotion() {
            return () -> {
                Builder builder = PathPatternRequestMatcher.withDefaults();
                return List.of(
                        builder.matcher(HttpMethod.GET, PROMOTION_PATH)
                );
            };
        }
    }

    @RestController
    public static class FakeController {

        @GetMapping(PROMOTION_PATH)
        public ResponseEntity<?> getPromotions() {
            return ResponseEntity.ok().build();
        }

        @GetMapping(ANONYMOUS_PATH)
        public ResponseEntity<?> handleAnonymousRequest() {
            return ResponseEntity.ok().build();
        }
    }
}
