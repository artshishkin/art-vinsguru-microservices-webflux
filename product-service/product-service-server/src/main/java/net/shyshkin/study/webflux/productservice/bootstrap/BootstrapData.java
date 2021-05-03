package net.shyshkin.study.webflux.productservice.bootstrap;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import net.shyshkin.study.webflux.productservice.entity.Product;
import net.shyshkin.study.webflux.productservice.repository.ProductRepository;
import net.shyshkin.study.webflux.productservice.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final ProductRepository repository;
    private final ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        Flux<Integer> indexFlux = Flux.range(1, 100);
        repository.count()
                .filter(count -> count == 0)
                .doOnNext(c -> log.debug("------------Bootstrap data started------------"))
                .flatMapMany(c -> indexFlux)
                .map(i ->
                        Product.builder()
                                .price(Faker.instance().random().nextInt(100, 2000))
                                .description(Faker.instance().lorem().sentence())
                                .build())
                .flatMap(repository::save)
                .doOnNext(product -> log.debug("saved {}", product))
                .doOnError(ex -> log.error("{}:{}", ex.getClass().getName(), ex.getMessage()))
                .doOnComplete(() -> log.debug("------------Bootstrap data completed------------"))
                .thenMany(f())
                .subscribe(
                        dto -> log.debug("inserted dto {}", dto),
                        ex -> log.error("{}:{}", ex.getClass().getName(), ex.getMessage())
                );
    }

    private Flux<ProductDto> f() {
        return Flux.range(1, 1000)
                .delayElements(Duration.ofSeconds(1))
                .map(i -> ProductDto.builder()
                        .price(Faker.instance().random().nextInt(10, 100))
                        .description("product-" + i)
                        .build())
                .flatMap(dto -> productService.insertProduct(Mono.just(dto)));
    }
}
