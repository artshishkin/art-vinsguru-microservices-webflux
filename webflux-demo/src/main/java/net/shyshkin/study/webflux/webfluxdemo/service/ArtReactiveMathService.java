package net.shyshkin.study.webflux.webfluxdemo.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@Primary
public class ArtReactiveMathService implements ReactiveMathService {

    @Override
    public Mono<Response> findSquare(int input) {
        return Mono
                .fromSupplier(() -> input * input)
                .map(Response::new);
    }

    @Override
    public Flux<Response> multiplicationTable(int input) {
        return Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(i -> log.debug("reactive-math-service processing : {}", i))
                .map(i -> new Response(i * input))
                .doFinally(signalType -> log.debug("reactive-math-service flux ended with: {}", signalType));
    }

    @Override
    public Mono<Response> multiply(Mono<MultiplyRequestDto> requestMono) {
        return requestMono
                .map(dto -> dto.getFirst() * dto.getSecond())
                .map(Response::new);
    }
}
