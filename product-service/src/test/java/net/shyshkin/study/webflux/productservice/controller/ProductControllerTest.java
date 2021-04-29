package net.shyshkin.study.webflux.productservice.controller;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import net.shyshkin.study.webflux.productservice.entity.Product;
import net.shyshkin.study.webflux.productservice.repository.ProductRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
class ProductControllerTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    ProductRepository productRepository;

    @Order(10)
    @ParameterizedTest
    @MethodSource
    void insertProduct(String id) {
        //given
        ProductDto productDto = ProductDto.builder()
                .id(id)
                .price(Faker.instance().random().nextInt(1, 100))
                .description(Faker.instance().lorem().sentence())
                .build();

        //when
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

    private static Stream<String> insertProduct() {
        return Stream.of(null, Faker.instance().random().hex());
    }

    @Test
    @Order(20)
    void getProductById_present() {
        //given
        String productId = getRandomProductId();
        log.debug("Product ID: {}", productId);

        //when
        webClient.get()
                .uri("/products/{id}", productId)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(ProductDto.class)
                .value(
                        dto -> assertThat(dto)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("id", productId)
                );
    }

    @Test
    @Order(20)
    void getProductById_absent() {
        //given
        String productId = "absent_product";
        log.debug("Product ID: {}", productId);

        //when
        webClient.get()
                .uri("/products/{id}", productId)
                .exchange()

                //then
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    @Order(30)
    void updateProduct_present() {
        //given
        String productId = getRandomProductId();
        log.debug("Product ID: {}", productId);
        ProductDto dtoToUpdate = ProductDto.builder()
                .id("no matter id - may be null or something else - we change it to correct product id")
                .description("New description")
                .price(123)
                .build();

        //when
        webClient.put()
                .uri("/products/{id}", productId)
                .bodyValue(dtoToUpdate)
                .exchange()
                //then

                .expectStatus().isOk()
                .expectBody(ProductDto.class)
                .value(
                        dto -> assertThat(dto)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("id", productId)
                                .hasFieldOrPropertyWithValue("description", "New description")
                                .hasFieldOrPropertyWithValue("price", 123)
                );
    }

    @Test
    @Order(31)
    void updateProduct_absent() {
        //given
        String productId = getRandomProductId();
        log.debug("Product ID: {}", productId);
        ProductDto dtoToUpdate = ProductDto.builder()
                .id("no matter id - may be null or something else - we change it to correct product id")
                .description("New description")
                .price(123)
                .build();

        //when
        webClient.put()
                .uri("/products/{id}", "some fake id")
                .bodyValue(dtoToUpdate)
                .exchange()
                //then

                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    @Order(40)
    void deleteProduct_present() {
        //given
        String productId = getRandomProductId();
        log.debug("Product ID: {}", productId);

        //when
        webClient.delete()
                .uri("/products/{id}", productId)
                .exchange()

                //then
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();
    }

    @Test
    @Order(41)
    void deleteProduct_absent() {
        //given
        String productId = "some fake";
        log.debug("Product ID: {}", productId);

        //when
        webClient.delete()
                .uri("/products/{id}", productId)
                .exchange()

                //then
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();
    }

    @Test
    void getAll() {
        //given
        String randomProductId = getRandomProductId();

        //when
        Flux<ProductDto> flux = webClient.get()
                .uri("/products")
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody()
                .doOnNext(dto -> log.debug("{}", dto));

        AtomicInteger counter = new AtomicInteger(0);
        StepVerifier.create(flux)
                .thenConsumeWhile(dto -> true, dto -> counter.incrementAndGet())
                .verifyComplete();
        assertThat(counter.get()).isGreaterThan(0);
    }

    private String getRandomProductId() {
        Product product = Product.builder()
                .price(Faker.instance().random().nextInt(1, 100))
                .description(Faker.instance().lorem().sentence())
                .build();
        return productRepository
                .save(product)
                .doOnNext(p -> log.debug("Saved test product: {}", p))
                .map(Product::getId)
                .block();
    }

}