package net.shyshkin.study.webflux.webfluxdemo.service;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReactiveMathService {

    Mono<Response> findSquare(int input);

    Flux<Response> multiplicationTable(int input);

}
