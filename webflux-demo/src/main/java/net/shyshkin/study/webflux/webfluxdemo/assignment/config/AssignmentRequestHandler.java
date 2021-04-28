package net.shyshkin.study.webflux.webfluxdemo.assignment.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.webfluxdemo.assignment.service.CalculatorService;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AssignmentRequestHandler {

    private final CalculatorService calculatorService;

    public Mono<ServerResponse> calculate(ServerRequest serverRequest) {

        String operator = serverRequest.headers().firstHeader("OP");
        int firstOperand = Integer.parseInt(serverRequest.pathVariable("first"));
        int secondOperand = Integer.parseInt(serverRequest.pathVariable("second"));

        return Mono
                .justOrEmpty(operator)
                .switchIfEmpty(Mono.error(new RuntimeException("operator header 'OP' must not be empty or null")))
                .flatMap(calculatorService::getFunction)
                .switchIfEmpty(Mono.error(new RuntimeException("function for `" + operator + "` not found")))
                .map(func -> func.apply(firstOperand, secondOperand))
                .map(Response::new)
                .flatMap(resp -> ServerResponse.ok().bodyValue(resp));
    }

    public Mono<ServerResponse> plus(ServerRequest serverRequest) {

        int firstOperand = Integer.parseInt(serverRequest.pathVariable("first"));
        int secondOperand = Integer.parseInt(serverRequest.pathVariable("second"));

        return ServerResponse.ok().bodyValue(firstOperand + secondOperand);
    }

    public Mono<ServerResponse> minus(ServerRequest serverRequest) {

        int firstOperand = Integer.parseInt(serverRequest.pathVariable("first"));
        int secondOperand = Integer.parseInt(serverRequest.pathVariable("second"));

        return ServerResponse.ok().bodyValue(firstOperand - secondOperand);
    }

    public Mono<ServerResponse> multiply(ServerRequest serverRequest) {

        int firstOperand = Integer.parseInt(serverRequest.pathVariable("first"));
        int secondOperand = Integer.parseInt(serverRequest.pathVariable("second"));

        return ServerResponse.ok().bodyValue(firstOperand * secondOperand);
    }

    public Mono<ServerResponse> divide(ServerRequest serverRequest) {

        int firstOperand = Integer.parseInt(serverRequest.pathVariable("first"));
        int secondOperand = Integer.parseInt(serverRequest.pathVariable("second"));

        return ServerResponse.ok().bodyValue(firstOperand / secondOperand);
    }
}
