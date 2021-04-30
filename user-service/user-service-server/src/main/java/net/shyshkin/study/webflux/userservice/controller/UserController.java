package net.shyshkin.study.webflux.userservice.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.userservice.dto.UserDto;
import net.shyshkin.study.webflux.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<UserDto>> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDto> insertUser(@RequestBody Mono<UserDto> userDtoMono) {
        return userService.insertUser(userDtoMono);
    }

    @PutMapping("{id}")
    Mono<ResponseEntity<UserDto>> updateUser(@PathVariable Integer id, @RequestBody Mono<UserDto> userDtoMono) {
        return userService.updateUser(id, userDtoMono)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }
}
