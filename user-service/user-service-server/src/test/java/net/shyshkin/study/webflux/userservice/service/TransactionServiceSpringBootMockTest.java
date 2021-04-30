package net.shyshkin.study.webflux.userservice.service;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;
import net.shyshkin.study.webflux.userservice.entity.UserTransaction;
import net.shyshkin.study.webflux.userservice.repository.UserRepository;
import net.shyshkin.study.webflux.userservice.repository.UserTransactionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static net.shyshkin.study.webflux.userservice.dto.TransactionStatus.DECLINED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@SpringBootTest
class TransactionServiceSpringBootMockTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserTransactionRepository transactionRepository;

    @Autowired
    TransactionService transactionService;

    @Captor
    ArgumentCaptor<UserTransaction> captor;

    @Test
    void createTransaction_fail() {
        //given
        given(userRepository.updateUserBalance(anyInt(), anyInt()))
                .willReturn(Mono.just(false));

        int userId = 123;
        int amount = 321;

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
        then(userRepository).should().updateUserBalance(eq(userId), eq(amount));
    }

    @Test
    void createTransaction_success() {
        //given
        int userId = 123;
        int amount = 321;

        TransactionRequestDto requestDto = TransactionRequestDto.builder()
                .userId(userId)
                .amount(amount)
                .build();
        UserTransaction userTransaction = UserTransaction.builder()
                .id(Faker.instance().random().nextInt(1_000_000))
                .amount(amount)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

        given(userRepository.updateUserBalance(anyInt(), anyInt()))
                .willReturn(Mono.just(true));
        given(transactionRepository.save(any(UserTransaction.class)))
                .willReturn(Mono.just(userTransaction));

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
        then(userRepository).should().updateUserBalance(eq(userId), eq(amount));
        then(transactionRepository).should().save(captor.capture());
        UserTransaction transactionToSave = captor.getValue();
        assertThat(transactionToSave)
                .hasFieldOrPropertyWithValue("amount", amount)
                .hasFieldOrPropertyWithValue("userId", userId);
    }
}