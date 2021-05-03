package net.shyshkin.study.webflux.userservice.service;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.dto.UserDto;
import net.shyshkin.study.webflux.userservice.entity.User;
import net.shyshkin.study.webflux.userservice.entity.UserTransaction;
import net.shyshkin.study.webflux.userservice.repository.UserRepository;
import net.shyshkin.study.webflux.userservice.repository.UserTransactionRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserTransactionRepository transactionRepository;

    @Autowired
    UserService userService;

    @Order(11)
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
        Mono<UserDto> userDtoMono = userService
                .insertUser(Mono.just(userDto))
                .doOnNext(dto -> log.debug("Inserted {}", dto));

        //then
        AtomicInteger userId = new AtomicInteger();
        StepVerifier
                .create(userDtoMono.doOnNext(dto -> userId.set(dto.getId())))
                .assertNext(
                        dto -> assertAll(
                                () -> assertThat(dto)
                                        .hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("balance", userDto.getBalance())
                                        .hasFieldOrPropertyWithValue("name", userDto.getName()),
                                () -> assertThat(dto.getId()).isNotEqualTo(userDto.getId())
                        )
                )
                .verifyComplete();

        Integer newId = userId.get();
        StepVerifier.create(userRepository.findById(newId).log())
                .assertNext(
                        entity -> assertAll(
                                () -> assertThat(entity.getName()).isEqualTo(userDto.getName()),
                                () -> assertThat(entity.getBalance()).isEqualTo(userDto.getBalance()),
                                () -> assertThat(entity.getId()).isEqualTo(newId)
                        )
                )
                .verifyComplete();
    }

    private static Stream<Integer> insertUser() {
        return Stream.of(null, Faker.instance().random().nextInt(10000));
    }

    @Test
    void getAll() {
        //given
        Integer randomUserId = getRandomUserId();

        //when
        Flux<UserDto> flux = userService
                .getAll()
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        AtomicInteger counter = new AtomicInteger(0);
        StepVerifier.create(flux)
                .thenConsumeWhile(dto -> true, dto -> counter.incrementAndGet())
                .verifyComplete();
        assertThat(counter.get()).isGreaterThan(0);
    }

    @Test
    void getUserById() {
        //given
        Integer userId = getRandomUserId();
        log.debug("User ID: {}", userId);

        //when
        Mono<UserDto> mono = userService.getUserById(userId);

        //then
        StepVerifier.create(mono.log())
                .assertNext(dto -> assertThat(dto)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", userId))
                .verifyComplete();
    }

    @Test
    void updateUser_present() {
        //given
        Integer userId = getRandomUserId();
        log.debug("User ID: {}", userId);
        UserDto dtoToUpdate = UserDto.builder()
                .name("New name")
                .balance(123)
                .build();

        //when
        Mono<UserDto> mono = userService.updateUser(userId, Mono.just(dtoToUpdate));

        //then
        StepVerifier.create(mono.log())
                .assertNext(dto -> assertThat(dto)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", userId)
                        .hasFieldOrPropertyWithValue("name", "New name")
                        .hasFieldOrPropertyWithValue("balance", 123)
                )
                .verifyComplete();
        StepVerifier.create(userRepository.findById(userId).log())
                .assertNext(user -> assertThat(user).hasNoNullFieldsOrProperties())
                .verifyComplete();
    }

    @Test
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
        Mono<UserDto> mono = userService.updateUser(-1_000_000, Mono.just(dtoToUpdate));

        //then
        StepVerifier
                .create(mono.log())
                .verifyComplete();
    }

    @Test
    void deleteUser_present() {
        //given
        Integer userId = getRandomUserId();
        log.debug("User ID: {}", userId);

        StepVerifier
                .create(userRepository.existsById(userId))
                .expectNext(true)
                .verifyComplete();

        //when
        Mono<Void> mono = userService.deleteUser(userId);

        //then
        StepVerifier
                .create(mono.log())
                .verifyComplete();

        StepVerifier
                .create(userRepository.existsById(userId))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void deleteUser_absent() {
        //given
        Integer userId = -100;
        log.debug("User ID: {}", userId);

        //when
        Mono<Void> mono = userService.deleteUser(userId);

        //then
        StepVerifier
                .create(mono.log())
                .verifyComplete();

        StepVerifier
                .create(userRepository.existsById(userId))
                .expectNext(false)
                .verifyComplete();
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

    @Test
    void deleteUserWithTransactions() {
        //given
        Integer userId = getRandomUserId();
        UserTransaction userTransaction = UserTransaction.builder().userId(userId).amount(1).timestamp(LocalDateTime.now()).build();
        Mono<UserTransaction> transactionMono = transactionRepository.save(userTransaction);
        UserTransaction savedTransaction = transactionMono.block();
        Integer transactionId = savedTransaction.getId();

        StepVerifier
                .create(transactionRepository.existsById(transactionId))
                .expectNext(true)
                .verifyComplete();

        //when
        Mono<Void> mono = userService.deleteUser(userId);

        //then
        StepVerifier
                .create(mono)
                .verifyComplete();

        StepVerifier
                .create(userRepository.existsById(userId))
                .expectNext(false)
                .verifyComplete();

        StepVerifier
                .create(transactionRepository.existsById(transactionId))
                .expectNext(false)
                .verifyComplete();
    }
}