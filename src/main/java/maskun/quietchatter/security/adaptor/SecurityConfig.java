package maskun.quietchatter.security.adaptor;

import lombok.RequiredArgsConstructor;
import maskun.quietchatter.security.application.in.AuthMemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final AppCorsProperties appCorsProperties;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthTokenService authTokenService,
                                            AuthMemberService authMemberService) throws Exception {

        return http
                .cors(Customizer.withDefaults())
                .anonymous(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(
                        new AuthFilter(authTokenService, authMemberService),
                        AnonymousAuthenticationFilter.class
                )
                .addFilterAfter(
                        new MdcFilter(),
                        AnonymousAuthenticationFilter.class
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/login/**", "/v1/auth/signup/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(appCorsProperties.allowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
