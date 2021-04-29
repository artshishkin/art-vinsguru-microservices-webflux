package net.shyshkin.study.webflux.userservice.service;

import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import reactor.core.publisher.Mono;

public interface TransactionService {

    Mono<TransactionResponseDto> createTransaction(TransactionRequestDto requestDto);
}
