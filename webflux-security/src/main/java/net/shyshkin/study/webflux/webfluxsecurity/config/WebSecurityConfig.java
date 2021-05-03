package net.shyshkin.study.webflux.webfluxsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
                .pathMatchers("/home/user").hasAnyRole("ADMIN", "USER")
                .pathMatchers("/home/admin").hasRole("ADMIN")
                .anyExchange().authenticated()
                .and()
                .formLogin();

        return http.build();
    }
}
