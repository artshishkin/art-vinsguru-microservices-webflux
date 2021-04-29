package net.shyshkin.study.webflux.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;
import net.shyshkin.study.webflux.userservice.mapper.EntityMapper;
import net.shyshkin.study.webflux.userservice.repository.UserRepository;
import net.shyshkin.study.webflux.userservice.repository.UserTransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final UserTransactionRepository transactionRepository;
    private final EntityMapper mapper;

    @Override
    public Mono<TransactionResponseDto> createTransaction(final TransactionRequestDto requestDto) {
        return userRepository
                .updateUserBalance(requestDto.getUserId(), requestDto.getAmount())
                .filter(Boolean::booleanValue)
                .map(b -> requestDto)
                .map(mapper::toEntity)
                .flatMap(transactionRepository::save)
                .map(userTransaction -> mapper.toDto(userTransaction, TransactionStatus.APPROVED))
                .defaultIfEmpty(mapper.toDto(requestDto, TransactionStatus.DECLINED));
    }
}
