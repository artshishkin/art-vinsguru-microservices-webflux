package net.shyshkin.study.webflux.userservice.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;
import net.shyshkin.study.webflux.userservice.entity.User;
import net.shyshkin.study.webflux.userservice.repository.UserRepository;
import net.shyshkin.study.webflux.userservice.repository.UserTransactionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
}