package net.shyshkin.study.webflux.userservice.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.UserTransactionDto;
import net.shyshkin.study.webflux.userservice.service.TransactionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("users/transactions")
    public Mono<TransactionResponseDto> createTransaction(@RequestBody Mono<TransactionRequestDto> requestDto) {
        return requestDto
                .flatMap(transactionService::createTransaction);
    }

    @GetMapping(value = "users/{userId}/transactions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserTransactionDto> getAllTransactionsOfUser(@PathVariable Integer userId) {
        return transactionService.getUserTransactions(userId);
    }
}
