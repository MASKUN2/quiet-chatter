package maskun.quietchatter.hexagon.domain.member;

public interface PasswordEncoder {
    Password encode(Secret secret);

    boolean matches(Secret secret, Password password);
}
