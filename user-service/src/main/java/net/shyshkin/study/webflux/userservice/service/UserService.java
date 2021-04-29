package net.shyshkin.study.webflux.userservice.service;

import net.shyshkin.study.webflux.userservice.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Flux<UserDto> getAll();

    Mono<UserDto> getUserById(Integer id);

    Mono<UserDto> insertUser(Mono<UserDto> userDtoMono);

    Mono<UserDto> updateUser(Integer id, Mono<UserDto> userDtoMono);

    Mono<Void> deleteUser(Integer id);

}
