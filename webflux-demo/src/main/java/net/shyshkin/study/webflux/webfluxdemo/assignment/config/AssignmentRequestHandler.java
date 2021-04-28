package net.shyshkin.study.webflux.webfluxdemo.assignment.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.webfluxdemo.assignment.service.CalculatorService;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.IntBinaryOperator;

@Component
@RequiredArgsConstructor
public class AssignmentRequestHandler {

    private final CalculatorService calculatorService;

    public Mono<ServerResponse> calculate(ServerRequest serverRequest) {

        String operator = serverRequest.headers().firstHeader("OP");
        int firstOperand = getValue(serverRequest, "first");
        int secondOperand = getValue(serverRequest, "second");

        return Mono
                .justOrEmpty(operator)
                .switchIfEmpty(Mono.error(new RuntimeException("operator header 'OP' must not be empty or null")))
                .flatMap(calculatorService::getFunction)
                .switchIfEmpty(Mono.error(new RuntimeException("function for `" + operator + "` not found")))
                .map(func -> func.applyAsInt(firstOperand, secondOperand))
                .map(Response::new)
                .flatMap(resp -> ServerResponse.ok().bodyValue(resp));
    }

    private int getValue(ServerRequest serverRequest, String first) {
        return Integer.parseInt(serverRequest.pathVariable(first));
    }

    public Mono<ServerResponse> plus(ServerRequest serverRequest) {
        return process(serverRequest, Math::addExact);
    }

    public Mono<ServerResponse> minus(ServerRequest serverRequest) {
        return process(serverRequest, Math::subtractExact);
    }

    public Mono<ServerResponse> multiply(ServerRequest serverRequest) {
        return process(serverRequest, Math::multiplyExact);
    }

    public Mono<ServerResponse> divide(ServerRequest serverRequest) {
        try {
            return process(serverRequest, Math::floorDiv);
        } catch (ArithmeticException ex) {
            return Mono.error(ex);
        }
    }

    private Mono<ServerResponse> process(ServerRequest serverRequest, IntBinaryOperator operator) {
        int firstOperand = getValue(serverRequest, "first");
        int secondOperand = getValue(serverRequest, "second");
        int operationResult = operator.applyAsInt(firstOperand, secondOperand);
        return ServerResponse.ok().bodyValue(operationResult);
    }
}
