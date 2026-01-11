package maskun.quietchatter.shared.security;

import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final GuestPromotion guestPromotion;
    private final GuestAuthenticationProvider authenticationProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {

        return http
                .anonymous(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(IF_REQUIRED))
                .addFilterAfter(getGuestFilter(), AnonymousAuthenticationFilter.class)
                .authorizeHttpRequests(getEndpointsAuthorizer())
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(Customizer.withDefaults())
                .build();
    }

    private @NonNull GuestAuthenticationFilter getGuestFilter() {
        return new GuestAuthenticationFilter(authenticationProvider, guestPromotion.getRequestMatchers());
    }

    private static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
    getEndpointsAuthorizer() {
        return authorize -> authorize.anyRequest().permitAll();
    }
}
