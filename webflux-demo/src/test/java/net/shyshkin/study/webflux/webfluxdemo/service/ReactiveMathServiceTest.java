package net.shyshkin.study.webflux.webfluxdemo.service;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveMathServiceTest {

    private ReactiveMathService mathService;

    @Nested
    class VinsguruReactiveMathServiceTest {

        @BeforeEach
        void setUp() {
            mathService = new VinsguruReactiveMathService();
        }

        @Test
        void findSquare() {
            //given
            int input = 6;

            //when
            Mono<Response> mono = mathService.findSquare(input);

            //then
            StepVerifier.create(mono)
                    .assertNext(response -> assertThat(response)
                            .hasNoNullFieldsOrProperties()
                            .hasFieldOrPropertyWithValue("output", input * input))
                    .expectComplete()
                    .verify(Duration.ofMillis(20));
        }

        @Test
        @Disabled("Too long for ci/cd")
        void multiplicationTable() {
            //given
            int input = 3;

            //when
            StepVerifier
                    .withVirtualTime(() -> mathService.multiplicationTable(input))

                    //then
                    .thenAwait(Duration.ofSeconds(11))
                    .thenConsumeWhile(resp -> resp.getOutput() % input == 0)
                    .verifyComplete();
        }
    }

    @Nested
    class ArtReactiveMathServiceTest {

        @BeforeEach
        void setUp() {
            mathService = new ArtReactiveMathService();
        }

        @Test
        void findSquare() {
            //given
            int input = 6;

            //when
            Mono<Response> mono = mathService.findSquare(input);

            //then
            StepVerifier.create(mono)
                    .assertNext(response -> assertThat(response)
                            .hasNoNullFieldsOrProperties()
                            .hasFieldOrPropertyWithValue("output", input * input))
                    .expectComplete()
                    .verify(Duration.ofMillis(20));
        }

        @Test
        void multiplicationTable() {
            //given
            int input = 3;

            //when
            StepVerifier
                    .withVirtualTime(() -> mathService.multiplicationTable(input))

                    //then
                    .thenAwait(Duration.ofSeconds(11))
                    .thenConsumeWhile(resp -> resp.getOutput() % input == 0)
                    .verifyComplete();
        }
    }
}