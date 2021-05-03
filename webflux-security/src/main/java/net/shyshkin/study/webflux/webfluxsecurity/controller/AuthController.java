package net.shyshkin.study.webflux.webfluxsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("home")
public class AuthController {

    @GetMapping("user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Mono<String> userHome() {
        return Mono.just("user home");
    }

    @GetMapping("admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<String> adminHome() {
        return Mono.just("admin home");
    }

    @GetMapping("any")
    public Mono<String> anyHome() {
        return Mono.just("authenticated home");
    }

}
