package net.shyshkin.study.webflux.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {

    public static final String ADMIN_NAME = "ArtShyshkin";

    @Bean
    public ReactiveAuditorAware<String> myAuditorProvider() {
        return () -> Mono.just(ADMIN_NAME);
    }

}
