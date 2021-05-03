package net.shyshkin.study.webflux.webfluxsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final Map<String, UserDetails> userDetailsMap;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.just(userDetailsMap.get(username));
    }
}
