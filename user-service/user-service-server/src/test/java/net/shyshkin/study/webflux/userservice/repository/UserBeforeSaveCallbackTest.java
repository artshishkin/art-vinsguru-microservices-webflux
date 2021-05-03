package net.shyshkin.study.webflux.userservice.repository;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "logging.level.io.r2dbc.h2.client=DEBUG"
})
class UserBeforeSaveCallbackTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void onBeforeSave() {
        //given
        User user = User.builder()
                .name(Faker.instance().name().fullName())
                .balance(Faker.instance().random().nextInt(1000))
                .build();

        //when
        Mono<User> mono = userRepository
                .save(user)
                .doOnNext(u -> log.debug("saved: {}", u));

        //then
        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
        log.debug("View logs to see `INSERT INTO users (name, balance, created_by) VALUES ($1, $2, $3) {1: 'Kacey Reinger', 2: 53, 3: 'art'}`");
        log.debug("Table `users` has additional column `created_by` but entity does not - for maintenance purposes");
    }
}