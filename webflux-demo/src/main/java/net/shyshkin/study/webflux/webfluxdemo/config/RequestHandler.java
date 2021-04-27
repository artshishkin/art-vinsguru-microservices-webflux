package net.shyshkin.study.webflux.webfluxdemo.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import net.shyshkin.study.webflux.webfluxdemo.service.ReactiveMathService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RequestHandler {

    private final ReactiveMathService mathService;

    public Mono<ServerResponse> findSquare(ServerRequest serverRequest) {

        int input = Integer.parseInt(serverRequest.pathVariable("input"));
        return mathService
                .findSquare(input)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> findSquareVins(ServerRequest serverRequest) {

        int input = Integer.parseInt(serverRequest.pathVariable("input"));
        Mono<Response> square = mathService.findSquare(input);
        return ServerResponse.ok().body(square, Response.class);
    }

    public Mono<ServerResponse> multiplicationTable(ServerRequest serverRequest) {
        int input = Integer.parseInt(serverRequest.pathVariable("input"));
        Flux<Response> responseFlux = mathService.multiplicationTable(input);
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(responseFlux, Response.class);
    }
}
