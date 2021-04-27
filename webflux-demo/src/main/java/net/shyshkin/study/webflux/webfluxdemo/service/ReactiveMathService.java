package net.shyshkin.study.webflux.webfluxdemo.service;

import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveMathService {

    Mono<Response> findSquare(int input);

    Flux<Response> multiplicationTable(int input);

    Mono<Response> multiply(Mono<MultiplyRequestDto> requestMono);

}
