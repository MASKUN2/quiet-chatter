package maskun.quietchatter.shared.security;

import static org.assertj.core.api.Assertions.*;
import static org.instancio.Instancio.of;
import static org.mockito.Mockito.*;

import com.redis.testcontainers.RedisContainer;
import java.util.Optional;
import java.util.UUID;
import maskun.quietchatter.WithTestContainerDatabases;
import maskun.quietchatter.member.application.out.MemberRepository;
import maskun.quietchatter.member.domain.Member;
import maskun.quietchatter.member.domain.Role;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class CacheableAuthMemberQueryServiceTest implements WithTestContainerDatabases {
    @MockitoBean
    private MemberRepository memberRepository;

    @Autowired
    private CacheableAuthMemberQueryService cacheableAuthMemberQueryService;

    @Autowired
    private RedisTemplate<String, AuthMember> redisTemplate;

    @BeforeEach
    void cleanupRedis() {
        redisTemplate.execute((RedisCallback<Object>)  conn -> {conn.serverCommands().flushDb(); return null;});
    }

    @Test
    void loadAuthMemberFromMainDB() {
        UUID myId = UUID.randomUUID();
        Member member = of(Member.class).set(Select.field(Member::getId), myId).set(Select.field(Member::getRole),
                Role.GUEST).create();
        when(memberRepository.findById(eq(myId))).thenReturn(Optional.ofNullable(member));
        Optional<AuthMember> found = cacheableAuthMemberQueryService.findById(myId);

        assertThat(found).isPresent();
    }

    @Test
    void cacheHit() {
        UUID myId = UUID.randomUUID();
        Member member = of(Member.class).set(Select.field(Member::getId), myId).set(Select.field(Member::getRole),
                Role.GUEST).create();
        when(memberRepository.findById(eq(myId))).thenReturn(Optional.ofNullable(member));
        // When
        AuthMember firstFind = cacheableAuthMemberQueryService.findById(myId)
                .orElseThrow();

        AuthMember secondFind = cacheableAuthMemberQueryService.findById(myId)
                .orElseThrow(); // 캐시에서 가져와야 함

        assertThat(firstFind).isEqualTo(secondFind);
        verify(memberRepository, times(1)).findById(myId); // DB 호출은 한 번만 발생했는지 검증
    }
}