package net.shyshkin.study.webflux.webfluxdemo.service;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MathServiceImplTest {

    private MathService mathService;

    @BeforeEach
    void setUp() {
        mathService = new MathServiceImpl();
    }

    @Test
    @Disabled("too long for ci/cd")
    void multiplicationTable() {
        //given
        int input = 3;

        //when
        Supplier<Publisher<? extends List<Response>>> supplier = () -> Mono
                .fromSupplier(() -> mathService.multiplicationTable(input));

        //then
        StepVerifier
                .withVirtualTime(supplier)
                .thenAwait(Duration.ofSeconds(11))
                .assertNext(list -> assertThat(list)
                        .hasSize(10)
                        .allMatch(response -> response.getOutput() % 3 == 0))
                .verifyComplete();

    }
}