package net.shyshkin.study.webflux.productservice.service;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Range;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Order(11)
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
        Mono<ProductDto> productDtoMono = productService
                .insertProduct(Mono.just(productDto))
                .doOnNext(dto -> log.debug("Inserted {}", dto));

        //then
        AtomicReference<String> productId = new AtomicReference<>("hi");
        StepVerifier
                .create(productDtoMono.doOnNext(dto -> productId.set(dto.getId())))
                .assertNext(
                        dto -> assertAll(
                                () -> assertThat(dto)
                                        .hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("price", productDto.getPrice())
                                        .hasFieldOrPropertyWithValue("description", productDto.getDescription()),
                                () -> assertThat(dto.getId()).isNotEqualTo(productDto.getId())
                        )
                )
                .verifyComplete();

        String newId = productId.get();
        StepVerifier.create(productRepository.findById(newId).log())
                .assertNext(
                        entity -> assertAll(
                                () -> assertThat(entity.getPrice()).isEqualTo(productDto.getPrice()),
                                () -> assertThat(entity.getDescription()).isEqualTo(productDto.getDescription()),
                                () -> assertThat(entity.getId()).isEqualTo(newId)
                        )
                )
                .verifyComplete();
    }

    private static Stream<String> insertProduct() {
        return Stream.of(null, Faker.instance().random().hex());
    }

    @Test
    void getAll() {
        //given
        String randomProductId = getRandomProductId();

        //when
        Flux<ProductDto> flux = productService
                .getAll()
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        AtomicInteger counter = new AtomicInteger(0);
        StepVerifier.create(flux)
                .thenConsumeWhile(dto -> true, dto -> counter.incrementAndGet())
                .verifyComplete();
        assertThat(counter.get()).isGreaterThan(0);
    }

    @Test
    void getProductById() {
        //given
        String productId = getRandomProductId();
        log.debug("Product ID: {}", productId);

        //when
        Mono<ProductDto> mono = productService.getProductById(productId);

        //then
        StepVerifier.create(mono.log())
                .assertNext(dto -> assertThat(dto)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", productId))
                .verifyComplete();
    }

    @Test
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
        Mono<ProductDto> mono = productService.updateProduct(productId, Mono.just(dtoToUpdate));

        //then
        StepVerifier.create(mono.log())
                .assertNext(dto -> assertThat(dto)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", productId)
                        .hasFieldOrPropertyWithValue("description", "New description")
                        .hasFieldOrPropertyWithValue("price", 123)
                )
                .verifyComplete();
    }

    @Test
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
        Mono<ProductDto> mono = productService.updateProduct("some fake id", Mono.just(dtoToUpdate));

        //then
        StepVerifier
                .create(mono.log())
                .verifyComplete();
    }

    @Test
    void deleteProduct_present() {
        //given
        String productId = getRandomProductId();
        log.debug("Product ID: {}", productId);

        StepVerifier
                .create(productRepository.existsById(productId))
                .expectNext(true)
                .verifyComplete();

        //when
        Mono<Void> mono = productService.deleteProduct(productId);

        //then
        StepVerifier
                .create(mono.log())
                .verifyComplete();

        StepVerifier
                .create(productRepository.existsById(productId))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void deleteProduct_absent() {
        //given
        String productId = "some fake id";
        log.debug("Product ID: {}", productId);

        //when
        Mono<Void> mono = productService.deleteProduct(productId);

        //then
        StepVerifier
                .create(mono.log())
                .verifyComplete();

        StepVerifier
                .create(productRepository.existsById(productId))
                .expectNext(false)
                .verifyComplete();
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

    @Test
    void getProductsByPriceInRange() {
        //given
        Flux<Product> createFlux = Flux.just(299, 300, 350, 370, 400, 401, 500)
                .map(i -> Product.builder().price(i).description("product " + i).build())
                .flatMap(productRepository::save);
        StepVerifier.create(createFlux)
                .thenConsumeWhile(p -> true)
                .verifyComplete();

        //when
        Flux<ProductDto> searchFlux = productService.getProductsByPriceInRange(300, 400);

        //then
        AtomicInteger counter = new AtomicInteger(0);
        StepVerifier.create(searchFlux)
                .thenConsumeWhile(
                        dto -> true,
                        dto -> {
                            assertThat(dto.getPrice()).isBetween(300, 400);
                            counter.incrementAndGet();
                        }
                )
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(4);
    }

    @Test
    void getProductsByPriceInRange_range() {
        //given
        Flux<Product> createFlux = Flux.just(1299, 1300, 1350, 1370, 1400, 1401, 1500)
                .map(i -> Product.builder().price(i).description("product " + i).build())
                .flatMap(productRepository::save);
        StepVerifier.create(createFlux)
                .thenConsumeWhile(p -> true)
                .verifyComplete();

        //when
        Flux<ProductDto> searchFlux = productService.getProductsByPriceInRange(Range.closed(1300, 1400));

        //then
        AtomicInteger counter = new AtomicInteger(0);
        StepVerifier.create(searchFlux)
                .thenConsumeWhile(
                        dto -> true,
                        dto -> {
                            assertThat(dto.getPrice()).isBetween(1300, 1400);
                            counter.incrementAndGet();
                        }
                )
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(4);
    }
}