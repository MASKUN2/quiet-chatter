package maskun.quietchatter.security.adaptor;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import maskun.quietchatter.WithTestContainerDatabases;
import maskun.quietchatter.security.application.in.AuthMemberService;
import maskun.quietchatter.security.domain.AuthMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(JwtAuthenticateTest.TestControllerConfig.class)
class JwtAuthenticateTest implements WithTestContainerDatabases {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private AuthMemberService authMemberService;

    @Autowired
    private RedisTemplate<String, AuthMember> redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @TestConfiguration
    static class TestControllerConfig {
        @RestController
        static class AuthTestController {
            @GetMapping("/test/auth/me")
            Authentication me() {
                return SecurityContextHolder.getContext().getAuthentication();
            }
        }
    }

    @BeforeEach
    void setUp() {
        redisTemplate.execute((RedisCallback<Object>) conn -> {
            conn.serverCommands().flushDb();
            return null;
        });

        jdbcTemplate.execute("SET session_replication_role = 'replica'");
        List<String> tableNames = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = 'public' AND table_type = 'BASE TABLE'",
                String.class
        );
        for (String tableName : tableNames) {
            jdbcTemplate.execute("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE");
        }
        jdbcTemplate.execute("SET session_replication_role = 'origin'");
    }

    @Test
    void authenticate_with_valid_access_token() throws Exception {
        // given
        AuthMember member = authMemberService.createNewGuest();
        String accessToken = authTokenService.createNewAccessToken(member.id());
        Cookie accessCookie = new Cookie("access_token", accessToken);

        // when & then
        mockMvc.perform(get("/test/auth/me").cookie(accessCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal.id").value(member.id().toString()))
                .andExpect(jsonPath("$.authenticated").value(true));
    }

    @Test
    void authenticate_with_expired_access_token_and_valid_refresh_token_should_rotate_tokens() throws Exception {
        // given
        AuthMember member = authMemberService.createNewGuest();
        
        // Create expired access token
        SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
        String expiredAccessToken = Jwts.builder()
                .signWith(key)
                .subject(member.id().toString())
                .expiration(Date.from(Instant.now().minus(1, ChronoUnit.HOURS))) // Expired 1 hour ago
                .compact();
        
        String refreshToken = authTokenService.createAndSaveRefreshToken(member.id());

        Cookie accessCookie = new Cookie("access_token", expiredAccessToken);
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);

        // when & then
        mockMvc.perform(get("/test/auth/me")
                        .cookie(accessCookie, refreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal.id").value(member.id().toString()))
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().value("refresh_token", not(equalTo(refreshToken))));
    }

    @Test
    void request_without_token_should_be_promoted_to_guest() throws Exception {
        mockMvc.perform(get("/test/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal.role").value("GUEST"))
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    void authenticate_with_invalid_token_should_be_promoted_to_guest() throws Exception {
        Cookie invalidCookie = new Cookie("access_token", "invalid.token.value");

        mockMvc.perform(get("/test/auth/me").cookie(invalidCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal.role").value("GUEST"))
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"));
    }
}
