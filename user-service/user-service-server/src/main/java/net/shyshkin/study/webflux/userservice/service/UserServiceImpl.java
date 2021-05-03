package net.shyshkin.study.webflux.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.dto.UserDto;
import net.shyshkin.study.webflux.userservice.mapper.UserMapper;
import net.shyshkin.study.webflux.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public Flux<UserDto> getAll() {
        return repository
                .findAll()
                .map(mapper::toDto);
    }

    @Override
    public Mono<UserDto> getUserById(Integer id) {
        return repository
                .findById(id)
                .map(mapper::toDto);
    }

    @Override
    public Mono<UserDto> insertUser(Mono<UserDto> userDtoMono) {
        return userDtoMono
                .map(mapper::toEntity)
                .doOnNext(user -> user.setId(null))
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<UserDto> updateUser(Integer id, Mono<UserDto> userDtoMono) {
        return repository
                .findById(id)
                .flatMap(u -> userDtoMono
                        .map(mapper::toEntity)
                        .doOnNext(user -> user.setCreated(u.getCreated()))
                        .doOnNext(user -> user.setAdminName(u.getAdminName())))
                .doOnNext(user -> user.setId(id))
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteUser(Integer id) {
        return repository
                .deleteById(id);
    }
}
