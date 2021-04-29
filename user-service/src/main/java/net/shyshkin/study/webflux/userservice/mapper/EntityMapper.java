package net.shyshkin.study.webflux.userservice.mapper;

import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;
import net.shyshkin.study.webflux.userservice.dto.UserDto;
import net.shyshkin.study.webflux.userservice.entity.User;
import net.shyshkin.study.webflux.userservice.entity.UserTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(imports = LocalDateTime.class)
public interface EntityMapper {

    User toEntity(UserDto dto);

    UserDto toDto(User user);

    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    UserTransaction toEntity(TransactionRequestDto requestDto);

    TransactionResponseDto toDto(UserTransaction userTransaction, TransactionStatus status);

    TransactionResponseDto toDto(TransactionRequestDto requestDto, TransactionStatus status);

}
