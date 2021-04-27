package net.shyshkin.study.webflux.webfluxdemo.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.webfluxdemo.dto.VinsValidationResponse;
import net.shyshkin.study.webflux.webfluxdemo.exception.VinsInputValidationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Configuration
@RequiredArgsConstructor
public class RouterConfig {

    private final RequestHandler handler;

    @Bean
    public RouterFunction<ServerResponse> serverResponseRouterFunction() {
        return RouterFunctions
                .route()
                .GET("router/square/{input}", handler::findSquare)
                .GET("router/square/{input}/bad-request", handler::findSquareWithValidation)
                .GET("router-vins/square/{input}", handler::findSquareVins)
                .GET("router/table/{input}", handler::multiplicationTable)
                .POST("router/multiply", handler::multiply)
                .onError(VinsInputValidationException.class, handleVinsValidationException())
                .build();
    }

    private BiFunction<VinsInputValidationException, ServerRequest, Mono<ServerResponse>> handleVinsValidationException() {
        return (ex, request) -> {

            VinsValidationResponse response = VinsValidationResponse.builder()
                    .message(ex.getMessage())
                    .errorCode(ex.getErrorCode())
                    .input(ex.getInput())
                    .build();
            return ServerResponse.badRequest().bodyValue(response);

        };
    }

}
