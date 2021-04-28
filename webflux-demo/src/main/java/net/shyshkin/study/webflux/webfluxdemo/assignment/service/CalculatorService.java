package net.shyshkin.study.webflux.webfluxdemo.assignment.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiFunction;

@Service
public class CalculatorService {

    private static final Map<String, BiFunction<Integer, Integer, Integer>> FUNCTION_MAP =
            Map.of(
                    "+", Math::addExact,
                    "-", Math::subtractExact,
                    "/", Math::floorDiv,
                    "*", Math::multiplyExact
            );

    public Mono<BiFunction<Integer, Integer, Integer>> getFunction(String operator) {
        return Mono.justOrEmpty(FUNCTION_MAP.get(operator));
    }

}
