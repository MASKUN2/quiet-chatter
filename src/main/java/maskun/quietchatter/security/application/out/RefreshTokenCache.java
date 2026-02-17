package maskun.quietchatter.security.application.out;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenCache {
    void save(String refreshTokenId, UUID memberId, Duration duration);

    Optional<UUID> findMemberIdByRefreshTokenId(String refreshTokenId);

    void deleteByRefreshTokenId(String refreshTokenId);
}
