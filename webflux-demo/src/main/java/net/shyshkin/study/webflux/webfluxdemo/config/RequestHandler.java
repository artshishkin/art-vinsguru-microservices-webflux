package net.shyshkin.study.webflux.webfluxdemo.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import net.shyshkin.study.webflux.webfluxdemo.dto.VinsValidationResponse;
import net.shyshkin.study.webflux.webfluxdemo.exception.VinsInputValidationException;
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
//        if (input < 10 || input > 20)
//            return Mono.error(new VinsInputValidationException(input));
        return mathService
                .findSquare(input)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    //BAD SOLUTION
    public Mono<ServerResponse> findSquareWithValidation(ServerRequest serverRequest) {

        int input = Integer.parseInt(serverRequest.pathVariable("input"));
        if (input >= 10 && input <= 20) {
            Mono<Response> square = mathService.findSquare(input);
            return ServerResponse.ok().body(square, Response.class);
        }
        return ServerResponse.badRequest().bodyValue(
                VinsValidationResponse.builder()
                        .errorCode(100)
                        .input(input)
                        .message("allowed range from 10 to 20")
                        .build());
    }

    public Mono<ServerResponse> findSquareVins(ServerRequest serverRequest) {

        int input = Integer.parseInt(serverRequest.pathVariable("input"));

        if (input < 10 || input > 20)
            return Mono.error(new VinsInputValidationException(input));

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

    public Mono<ServerResponse> multiply(ServerRequest serverRequest) {
        Mono<MultiplyRequestDto> dtoMono = serverRequest.bodyToMono(MultiplyRequestDto.class);
        Mono<Response> multiplyMono = mathService.multiply(dtoMono);
        return ServerResponse
                .created(null)
                .body(multiplyMono, Response.class);
    }
}
