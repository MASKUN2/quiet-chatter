package maskun.quietchatter.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthManager {
    private final JwtParser jwtParser;
    private final AuthMemberQueryable authMemberQueryable;

    public JwtAuthManager(SecretKey secretKey, AuthMemberQueryable authMemberQueryable) {
        jwtParser = Jwts.parser().verifyWith(secretKey).build();
        this.authMemberQueryable = authMemberQueryable;
    }

    public Authentication parse(String accessToken) throws AuthenticationException {
        UUID memberId = getMemberId(accessToken);
        AuthMember authMember = authMemberQueryable.findById(memberId)
                .orElseThrow(() -> new AuthMemberNotfoundException("member not found for id: " + memberId));
        return new MyAuthToken(authMember);
    }

    private UUID getMemberId(String accessToken) {
        try {
            Jws<Claims> claims = jwtParser.parseSignedClaims(accessToken);
            String subject = claims.getPayload().getSubject();
            return UUID.fromString(subject);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthException(e.getMessage(), e);
        }
    }
}
