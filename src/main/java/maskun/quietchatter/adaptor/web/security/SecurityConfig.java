package maskun.quietchatter.adaptor.web.security;

import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationProvider authenticationProvider,
                                                   GuestPromotion guestPromotion) throws Exception {

        return http
                .anonymous(Customizer.withDefaults())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(IF_REQUIRED)
                )
                .addFilterBefore(
                        new GuestAuthenticationFilter(authenticationProvider, guestPromotion.getRequestMatchers()),
                        AnonymousAuthenticationFilter.class
                )
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
