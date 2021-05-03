package net.shyshkin.study.webflux.userservice;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.config.R2dbcConfig;
import net.shyshkin.study.webflux.userservice.entity.User;
import net.shyshkin.study.webflux.userservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "logging.level.io.r2dbc.h2.client=DEBUG"
})
class ReactiveAuditorAwareTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("When saving new user created date must be set")
    void createdDate_test() {
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
                .assertNext(savedUser -> assertAll(
                        () -> assertThat(savedUser).hasNoNullFieldsOrProperties(),
                        () -> assertThat(savedUser.getName()).isEqualTo(user.getName()),
                        () -> assertThat(savedUser.getAdminName()).isEqualTo(R2dbcConfig.ADMIN_NAME),
                        () -> assertThat(savedUser.getCreated()).isEqualToIgnoringNanos(LocalDateTime.now()),
                        () -> assertThat(savedUser.getModified()).isEqualToIgnoringNanos(LocalDateTime.now())
                ))
                .verifyComplete();
    }

    @Test
    @DisplayName("When updating user LastModifiedDate field must change but CreatedDate must stay the same")
    void lastModifiedDate_test() {
        //given
        User user = User.builder()
                .name(Faker.instance().name().fullName())
                .balance(Faker.instance().random().nextInt(1000))
                .build();

        //when
        Mono<User> mono = userRepository
                .save(user)
                .doOnNext(u -> log.debug("saved: {}", u))
                .delayElement(Duration.ofSeconds(1))
                .doOnNext(u -> u.setName(u.getName().toUpperCase()))
                .flatMap(userRepository::save)
                .doOnNext(u -> log.debug("updated: {}", u));

        //then
        StepVerifier.create(mono)
                .assertNext(savedUser -> assertAll(
                        () -> assertThat(savedUser).hasNoNullFieldsOrProperties(),
                        () -> assertThat(savedUser.getName()).isEqualTo(user.getName()),
                        () -> assertThat(savedUser.getAdminName()).isEqualTo(R2dbcConfig.ADMIN_NAME),
                        () -> assertThat(savedUser.getCreated()).isEqualToIgnoringNanos(LocalDateTime.now().minusSeconds(1)),
                        () -> assertThat(savedUser.getModified()).isEqualToIgnoringNanos(LocalDateTime.now())
                ))
                .verifyComplete();
    }
}
