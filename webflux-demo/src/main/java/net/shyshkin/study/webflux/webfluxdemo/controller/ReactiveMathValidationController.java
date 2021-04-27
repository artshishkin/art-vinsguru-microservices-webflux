package net.shyshkin.study.webflux.webfluxdemo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import net.shyshkin.study.webflux.webfluxdemo.exception.VinsInputValidationException;
import net.shyshkin.study.webflux.webfluxdemo.service.ReactiveMathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("reactive-math")
@RequiredArgsConstructor
public class ReactiveMathValidationController {

    private final ReactiveMathService mathService;

    @GetMapping("square/{input}/throw")
    public Mono<Response> findSquare(@PathVariable Integer input) {

        if (input < 10 || input > 20)
            throw new VinsInputValidationException(input);
        return mathService.findSquare(input);
    }

    @GetMapping("square/{input}/mono-error")
    public Mono<Response> monoError(@PathVariable Integer input) {
        return Mono
                .just(input)
                .filter(in -> in >= 10 && in <= 20)
                .switchIfEmpty(Mono.error(new VinsInputValidationException(input)))
                .flatMap(mathService::findSquare);
    }

    @GetMapping("square/{input}/mono-error-handle")
    public Mono<Response> monoErrorHandle(@PathVariable Integer input) {
        return Mono
                .just(input)
                .handle((in, sink) -> {
                    if (in >= 10 && in <= 20)
                        sink.next(in);
                    else
                        sink.error(new VinsInputValidationException(in));
                })
                .cast(Integer.class)
                .flatMap(mathService::findSquare);
    }

    @GetMapping("square/{input}/error-assignment")
    public Mono<ResponseEntity<Response>> errorAssignment(@PathVariable Integer input) {
        return Mono
                .just(input)
                .filter(in -> in >= 10 && in <= 20)
                .flatMap(mathService::findSquare)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
