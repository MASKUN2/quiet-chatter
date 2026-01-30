package maskun.quietchatter.security.adaptor;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.application.in.AuthMemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthTokenService authTokenService,
                                            AuthMemberService authMemberService) throws Exception {

        return http
                .anonymous(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(
                        new AuthFilter(authTokenService, authMemberService),
                        AnonymousAuthenticationFilter.class
                )
                .addFilterAfter(
                        new AnonymousToGuestPromotionFilter(authTokenService, authMemberService),
                        AnonymousAuthenticationFilter.class
                )
                .addFilterAfter(
                        new MdcFilter(),
                        AnonymousToGuestPromotionFilter.class
                )
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
