package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.BaseTest;
import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec53_Attributes_Test extends BaseTest {

    @Autowired
    WebClient webClient;

    @ParameterizedTest
    @ValueSource(strings = {"basic", "bearer"})
    void attributes(String authType) {
        //given
        int first = 4;
        int second = 5;
        MultiplyRequestDto multiplyRequestDto = new MultiplyRequestDto(first, second);

        //when
        Mono<ResponseEntity<Response>> responseEntityMono = webClient.post().uri("/reactive-math/multiply")
                .bodyValue(multiplyRequestDto)
                .attribute("auth", authType)
                .retrieve()
                .toEntity(Response.class)
                .doOnNext(entity -> log.debug("Receive entity: {}", entity));

        //then
        StepVerifier
                .create(responseEntityMono)
                .expectNextCount(1)
                .verifyComplete();
    }


}