package net.shyshkin.study.webflux.userservice.repository;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
@ActiveProfiles("after-convert-test")
class UserAfterConvertCallbackTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void onAfterConvert() {

        //when
        Flux<Integer> flux = userRepository
                .findAll()
                .doOnNext(user -> log.debug("{}", user))
                .map(User::getBalance)
                .take(4);

        //then
        StepVerifier.create(flux)
                .expectNext(9000, 13500, 18000, 22500)
                .verifyComplete();
    }
}