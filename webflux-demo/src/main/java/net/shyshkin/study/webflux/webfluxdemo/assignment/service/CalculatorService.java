package net.shyshkin.study.webflux.webfluxdemo.assignment.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.IntBinaryOperator;

@Service
public class CalculatorService {

    private static final Map<String, IntBinaryOperator> FUNCTION_MAP =
            Map.of(
                    "+", Math::addExact,
                    "-", Math::subtractExact,
                    "/", Math::floorDiv,
                    "*", Math::multiplyExact
            );

    public Mono<IntBinaryOperator> getFunction(String operator) {
        return Mono.justOrEmpty(FUNCTION_MAP.get(operator));
    }

}
