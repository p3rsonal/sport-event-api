package com.arthurdream.sporteventapi.config.security;

import com.arthurdream.sporteventapi.model.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(h -> h.frameOptions(FrameOptionsConfig::sameOrigin))
            .authorizeHttpRequests(httpReqs -> httpReqs
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).hasRole(UserRole.ADMIN.toString())
                .anyRequest().authenticated())
            .formLogin(login -> login
                .defaultSuccessUrl("/swagger-ui.html", true)
                .permitAll())
            .logout(LogoutConfigurer::permitAll)
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Please authenticate to access this resource.\"}");
            }))
            .build();
    }
}

