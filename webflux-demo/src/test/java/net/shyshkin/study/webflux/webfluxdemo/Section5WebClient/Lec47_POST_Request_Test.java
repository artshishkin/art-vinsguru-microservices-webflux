package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.BaseTest;
import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
public class Lec47_POST_Request_Test extends BaseTest {

    @Autowired
    WebClient webClient;

    @Test
    void multiply_entity() {
        //given
        int first = 4;
        int second = 5;
        MultiplyRequestDto multiplyRequestDto = new MultiplyRequestDto(first, second);

        //when
        Mono<ResponseEntity<Response>> responseEntityMono = webClient.post().uri("/reactive-math/multiply")
                .bodyValue(multiplyRequestDto)
                .retrieve()
                .toEntity(Response.class);

        //then
        StepVerifier
                .create(responseEntityMono)
                .assertNext(
                        entity -> assertAll(
                                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                                () -> assertThat(entity.getBody().getOutput()).isEqualTo(first * second)
                        )
                )
                .verifyComplete();
    }

    @Test
    void multiply_body() {
        //given
        int first = 4;
        int second = 5;
        MultiplyRequestDto multiplyRequestDto = new MultiplyRequestDto(first, second);

        //when
        Mono<Response> responseMono = webClient.post().uri("/reactive-math/multiply")
                .bodyValue(multiplyRequestDto)
                .retrieve()
                .bodyToMono(Response.class)
                .doOnNext(response -> log.debug("Received: {}", response));

        //then
        StepVerifier
                .create(responseMono)
                .assertNext(
                        response -> assertThat(response.getOutput()).isEqualTo(first * second)
                )
                .verifyComplete();
    }
}
