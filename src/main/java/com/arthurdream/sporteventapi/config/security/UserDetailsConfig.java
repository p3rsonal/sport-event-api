package com.arthurdream.sporteventapi.config.security;

import com.arthurdream.sporteventapi.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsConfig {

    private final PasswordEncoder passwordEncoder;
    private final static String ROLE_PREFIX = "ROLE_";

    @Autowired
    public UserDetailsConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        String userPassword = passwordEncoder.encode("userpassword");
        UserDetails user = User.withUsername("user")
                               .password(userPassword)
                               .roles(UserRole.USER.toString())
                               .build();

        String adminPassword = passwordEncoder.encode("adminpassword");
        UserDetails admin = User.withUsername("admin")
                                .password(adminPassword)
                                .roles(UserRole.ADMIN.toString())
                                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
