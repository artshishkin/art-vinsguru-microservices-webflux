package net.shyshkin.study.webflux.productservice.bootstrap;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.productservice.entity.Product;
import net.shyshkin.study.webflux.productservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final ProductRepository repository;

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
                .subscribe(
                        product -> log.debug("saved {}", product),
                        ex -> log.error("{}:{}", ex.getClass().getName(), ex.getMessage()),
                        () -> log.debug("------------Bootstrap data completed------------")
                );
    }
}
