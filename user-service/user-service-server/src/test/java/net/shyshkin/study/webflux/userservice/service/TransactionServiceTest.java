package net.shyshkin.study.webflux.userservice.service;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;
import net.shyshkin.study.webflux.userservice.dto.UserTransactionDto;
import net.shyshkin.study.webflux.userservice.entity.User;
import net.shyshkin.study.webflux.userservice.entity.UserTransaction;
import net.shyshkin.study.webflux.userservice.repository.UserRepository;
import net.shyshkin.study.webflux.userservice.repository.UserTransactionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static net.shyshkin.study.webflux.userservice.dto.TransactionStatus.DECLINED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserTransactionRepository transactionRepository;

    @Autowired
    TransactionService transactionService;
    private Integer userId;

    @BeforeAll
    void beforeAll() {
        User art = User.builder()
                .name("Art")
                .balance(10_000)
                .build();
        userId = userRepository.save(art).map(User::getId).block();
    }

    @Test
    void createTransaction_fail() {
        //given
        int amount = 1_000_000;

        TransactionRequestDto requestDto = TransactionRequestDto.builder()
                .userId(userId)
                .amount(amount)
                .build();

        //when
        Mono<TransactionResponseDto> mono = transactionService.createTransaction(requestDto);

        //then
        StepVerifier.create(mono)
                .assertNext(respDto ->
                        assertAll(
                                () -> assertThat(respDto.getAmount()).isEqualTo(amount),
                                () -> assertThat(respDto.getUserId()).isEqualTo(userId),
                                () -> assertThat(respDto.getStatus()).isEqualTo(DECLINED)
                        )
                )
                .verifyComplete();
    }

    @Test
    void createTransaction_success() {
        //given
        int amount = 1_000;

        TransactionRequestDto requestDto = TransactionRequestDto.builder()
                .userId(userId)
                .amount(amount)
                .build();

        //when
        Mono<TransactionResponseDto> mono = transactionService.createTransaction(requestDto);

        //then
        StepVerifier.create(mono)
                .assertNext(respDto ->
                        assertAll(
                                () -> assertThat(respDto.getAmount()).isEqualTo(amount),
                                () -> assertThat(respDto.getUserId()).isEqualTo(userId),
                                () -> assertThat(respDto.getStatus()).isEqualTo(TransactionStatus.APPROVED)
                        )
                )
                .verifyComplete();

        StepVerifier.create(userRepository.findById(userId))
                .assertNext(user -> assertThat(user.getBalance()).isEqualTo(10_000 - 1_000))
                .verifyComplete();


    }

    @Test
    void getUserTransactions_present() {
        //given
        Flux<UserTransaction> userTransactionFlux = Flux.range(1, 10)
                .map(i -> UserTransaction.builder()
                        .userId(userId)
                        .amount(Faker.instance().random().nextInt(100, 1000))
                        .timestamp(LocalDateTime.now())
                        .build());
        Flux<UserTransaction> saved = transactionRepository
                .saveAll(userTransactionFlux)
                .doOnNext(trans -> log.debug("saved {}", trans));
        StepVerifier
                .create(saved)
                .expectNextCount(10)
                .verifyComplete();

        //when
        Flux<UserTransactionDto> flux = transactionService.getUserTransactions(userId);

        //then
        AtomicInteger counter = new AtomicInteger(0);
        StepVerifier.create(flux)
                .thenConsumeWhile(transactionDto -> transactionDto.getUserId().equals(userId), dto -> counter.incrementAndGet())
                .verifyComplete();

        assertThat(counter.get()).isGreaterThanOrEqualTo(10);
    }

    @Test
    void getUserTransactions_absentUser() {
        //when
        Flux<UserTransactionDto> flux = transactionService.getUserTransactions(-1000);

        //then
        StepVerifier.create(flux)
                .verifyComplete();
    }

    @Test
    void getUserTransactions_absentTransaction() {

        //given
        User newUser = User.builder()
                .name("New User")
                .balance(10_000)
                .build();
        Integer newUserId = userRepository.save(newUser).map(User::getId).block();

        //when
        Flux<UserTransactionDto> flux = transactionService.getUserTransactions(newUserId);

        //then
        StepVerifier.create(flux)
                .verifyComplete();
    }
}