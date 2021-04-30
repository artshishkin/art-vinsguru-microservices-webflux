package net.shyshkin.study.webflux.userservice.service;

import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.UserTransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

    Mono<TransactionResponseDto> createTransaction(TransactionRequestDto requestDto);

    Flux<UserTransactionDto> getUserTransactions(Integer userId);
}
