package net.shyshkin.study.webflux.productservice.config;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;

@Slf4j
@SpringBootTest
class SinkConfigTest {

    @Autowired
    Sinks.Many<ProductDto> sink;

    @Autowired
    Flux<ProductDto> productBroadcast;

    @Test
    void sinkTest() throws InterruptedException {
        //given
        ProductDto productDto = generateRandomProduct();

        //when
        sink.tryEmitNext(productDto);

        Thread.sleep(200);
        CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ProductDto dto = generateRandomProduct();
                sink.tryEmitNext(dto);
            }
            sink.tryEmitComplete();
        });

        //then
        StepVerifier
                .create(productBroadcast.log())
                .expectNext(productDto)
                .expectNextCount(5)
                .verifyComplete();
    }

    private ProductDto generateRandomProduct() {
        return ProductDto.builder()
                .id(Faker.instance().random().hex())
                .price(Faker.instance().random().nextInt(100))
                .description(Faker.instance().lorem().sentence())
                .build();
    }
}