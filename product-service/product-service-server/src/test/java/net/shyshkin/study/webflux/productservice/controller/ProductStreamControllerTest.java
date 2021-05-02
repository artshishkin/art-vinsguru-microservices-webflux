package net.shyshkin.study.webflux.productservice.controller;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import net.shyshkin.study.webflux.productservice.service.ProductService;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductStreamControllerTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    ProductService productService;

    private Stream<Consumer<ProductDto>> getProductUpdates() {
        return Stream.of(
                this::insertProductDirectThroughService,
                this::postProductThroughWeb
        );
    }

    @ParameterizedTest(name = "[{index} {arguments}]")
    @MethodSource
    void getProductUpdates(Consumer<ProductDto> insertionMethod) {

        emitProducts(insertionMethod);

        sleep(0.05);

        //when
        Flux<ProductDto> broadcastFlux = webClient.get()
                .uri("/products/broadcast")
                .exchange()

                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody()
                .doOnNext(dto -> log.debug("Received: {}", dto))
                .take(5);

        //then
        StepVerifier.create(broadcastFlux)
                .expectNextCount(5)
                .verifyComplete();
    }

    private void emitProducts(Consumer<ProductDto> insertionMethod) {
        CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 5; i++) {
                sleep(0.1);
                ProductDto dto = generateRandomProduct();
                insertionMethod.accept(dto);
            }
        });
    }

    private void sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ProductDto generateRandomProduct() {
        return ProductDto.builder()
                .id(Faker.instance().random().hex())
                .price(Faker.instance().random().nextInt(100))
                .description(Faker.instance().lorem().sentence())
                .build();
    }

    private void insertProductDirectThroughService(ProductDto productDto) {
        productService
                .insertProduct(Mono.just(productDto))
                .subscribe(dto -> log.debug("Product saved {}", dto));
    }

    private void postProductThroughWeb(ProductDto productDto) {
        webClient.post()
                .uri("/products")
                .bodyValue(productDto)
                .exchange()

                //then
                .expectStatus().isCreated()
                .expectBody(ProductDto.class)
                .value(
                        dto -> assertAll(
                                () -> assertThat(dto)
                                        .hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("price", productDto.getPrice())
                                        .hasFieldOrPropertyWithValue("description", productDto.getDescription()),
                                () -> assertThat(dto.getId()).isNotEqualTo(productDto.getId())
                        )
                );
    }
}