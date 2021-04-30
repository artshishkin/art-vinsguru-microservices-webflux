package net.shyshkin.study.webflux.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;
import net.shyshkin.study.webflux.userservice.dto.UserTransactionDto;
import net.shyshkin.study.webflux.userservice.mapper.TransactionMapper;
import net.shyshkin.study.webflux.userservice.repository.UserRepository;
import net.shyshkin.study.webflux.userservice.repository.UserTransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final UserTransactionRepository transactionRepository;
    private final TransactionMapper mapper;

    @Override
    public Mono<TransactionResponseDto> createTransaction(final TransactionRequestDto requestDto) {
        return userRepository
                .updateUserBalance(requestDto.getUserId(), requestDto.getAmount())
                .filter(Boolean::booleanValue)
                .map(b -> requestDto)
                .map(mapper::toEntity)
                .flatMap(transactionRepository::save)
                .map(userTransaction -> mapper.toResponseDto(userTransaction, TransactionStatus.APPROVED))
                .defaultIfEmpty(mapper.toResponseDto(requestDto, TransactionStatus.DECLINED));
    }

    @Override
    public Flux<UserTransactionDto> getUserTransactions(Integer userId) {
        return transactionRepository
                .findByUserId(userId)
                .map(mapper::toDto);
    }
}
