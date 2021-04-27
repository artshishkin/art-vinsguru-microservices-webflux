package net.shyshkin.study.webflux.webfluxdemo.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
public class VinsguruReactiveMathService implements ReactiveMathService {

    @Override
    public Mono<Response> findSquare(int input) {
        return Mono
                .fromSupplier(() -> input * input)
                .map(Response::new);
    }

    @Override
    public Flux<Response> multiplicationTable(int input) {
        return Flux.range(1, 10)
                .doOnNext(i -> SleepUtil.sleep(1))
                .doOnNext(i -> log.debug("vinsguru reactive-math-service processing : {}", i))
                .map(i -> new Response(i * input));
    }
}
