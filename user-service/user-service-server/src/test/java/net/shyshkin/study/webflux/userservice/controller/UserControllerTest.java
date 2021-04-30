package net.shyshkin.study.webflux.userservice.controller;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.dto.UserDto;
import net.shyshkin.study.webflux.userservice.entity.User;
import net.shyshkin.study.webflux.userservice.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    UserRepository userRepository;

    @Order(10)
    @ParameterizedTest
    @MethodSource
    void insertUser(Integer id) {
        //given
        UserDto userDto = UserDto.builder()
                .id(id)
                .balance(Faker.instance().random().nextInt(1, 100))
                .name(Faker.instance().name().fullName())
                .build();

        //when
        webClient.post()
                .uri("/users")
                .bodyValue(userDto)
                .exchange()

                //then
                .expectStatus().isCreated()
                .expectBody(UserDto.class)
                .value(
                        dto -> assertAll(
                                () -> assertThat(dto)
                                        .hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("balance", userDto.getBalance())
                                        .hasFieldOrPropertyWithValue("name", userDto.getName()),
                                () -> assertThat(dto.getId()).isNotEqualTo(userDto.getId())
                        )
                );
    }

    private static Stream<Integer> insertUser() {
        return Stream.of(null, Faker.instance().random().nextInt(10000));
    }

    @Test
    @Order(20)
    void getUserById_present() {
        //given
        Integer userId = getRandomUserId();
        log.debug("User ID: {}", userId);

        //when
        webClient.get()
                .uri("/users/{id}", userId)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(
                        dto -> assertThat(dto)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("id", userId)
                );
    }

    @Test
    @Order(20)
    void getUserById_absent() {
        //given
        Integer userId = -1;
        log.debug("User ID: {}", userId);

        //when
        webClient.get()
                .uri("/users/{id}", userId)
                .exchange()

                //then
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    @Order(30)
    void updateUser_present() {
        //given
        Integer userId = getRandomUserId();
        log.debug("User ID: {}", userId);
        UserDto dtoToUpdate = UserDto.builder()
                .name("New name")
                .balance(123)
                .build();

        //when
        webClient.put()
                .uri("/users/{id}", userId)
                .bodyValue(dtoToUpdate)
                .exchange()
                //then

                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(
                        dto -> assertThat(dto)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("id", userId)
                                .hasFieldOrPropertyWithValue("name", "New name")
                                .hasFieldOrPropertyWithValue("balance", 123)
                );
    }

    @Test
    @Order(31)
    void updateUser_absent() {
        //given
        Integer userId = getRandomUserId();
        log.debug("User ID: {}", userId);
        UserDto dtoToUpdate = UserDto.builder()
                .id(0)
                .name("New name")
                .balance(123)
                .build();

        //when
        webClient.put()
                .uri("/users/{id}", -1_000_000)
                .bodyValue(dtoToUpdate)
                .exchange()
                //then

                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    @Order(40)
    void deleteUser_present() {
        //given
        Integer userId = getRandomUserId();
        log.debug("User ID: {}", userId);

        //when
        webClient.delete()
                .uri("/users/{id}", userId)
                .exchange()

                //then
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();
    }

    @Test
    @Order(41)
    void deleteUser_absent() {
        //given
        Integer userId = -100;
        log.debug("User ID: {}", userId);

        //when
        webClient.delete()
                .uri("/users/{id}", userId)
                .exchange()

                //then
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();
    }

    @Test
    void getAll() {
        //given
        Integer randomUserId = getRandomUserId();

        //when
        Flux<UserDto> flux = webClient.get()
                .uri("/users")
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(UserDto.class)
                .getResponseBody()
                .doOnNext(dto -> log.debug("{}", dto));

        AtomicInteger counter = new AtomicInteger(0);
        StepVerifier.create(flux)
                .thenConsumeWhile(dto -> true, dto -> counter.incrementAndGet())
                .verifyComplete();
        assertThat(counter.get()).isGreaterThan(0);
    }

    private Integer getRandomUserId() {
        User user = User.builder()
                .balance(Faker.instance().random().nextInt(1, 100))
                .name(Faker.instance().name().fullName())
                .build();
        return userRepository
                .save(user)
                .doOnNext(p -> log.debug("Saved test user: {}", p))
                .map(User::getId)
                .block();
    }


}