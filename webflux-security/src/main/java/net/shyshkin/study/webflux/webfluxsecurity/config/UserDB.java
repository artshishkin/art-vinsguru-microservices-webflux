package net.shyshkin.study.webflux.webfluxsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Map;

@Configuration
public class UserDB {

    @Bean
    public Map<String, UserDetails> userDetailsMap(PasswordEncoder encoder) {
        return Map.of(
                "user", User.withUsername("user").password(encoder.encode("user")).roles("USER").build(),
                "admin", User.withUsername("admin").password(encoder.encode("admin")).roles("ADMIN").build(),
                "any", User.withUsername("any").password(encoder.encode("any")).authorities(Collections.emptyList()).build()
        );
    }
}
