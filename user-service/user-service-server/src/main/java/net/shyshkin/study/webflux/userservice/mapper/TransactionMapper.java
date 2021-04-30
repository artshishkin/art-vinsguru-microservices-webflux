package net.shyshkin.study.webflux.userservice.mapper;

import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;
import net.shyshkin.study.webflux.userservice.dto.UserTransactionDto;
import net.shyshkin.study.webflux.userservice.entity.UserTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(imports = LocalDateTime.class)
public interface TransactionMapper {

    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    UserTransaction toEntity(TransactionRequestDto requestDto);

    UserTransactionDto toDto(UserTransaction userTransaction);

    TransactionResponseDto toResponseDto(UserTransaction userTransaction, TransactionStatus status);

    TransactionResponseDto toResponseDto(TransactionRequestDto requestDto, TransactionStatus status);

}
