package net.shyshkin.study.webflux.orderservice.client;

import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.repository.PurchaseOrderRepository;
import net.shyshkin.study.webflux.orderservice.service.OrderFulfillmentService;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import net.shyshkin.study.webflux.userservice.dto.UserDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

@SpringBootTest
@Disabled("Only for manual testing")
@DisplayName("Start `mongodb`, `user-service` and `product-service` first, then run test")
public class AssignmentClientsManualTest {

    @Autowired
    UserClient userClient;

    @Autowired
    ProductClient productClient;

    @Autowired
    OrderFulfillmentService orderFulfillmentService;

    @Autowired
    PurchaseOrderRepository orderRepository;

    @Test
    void assignment() {
        //given
        Flux<UserDto> userFlux = userClient.getAll();
        Flux<ProductDto> productFlux = productClient.getAll();

        Long expectedSize = userFlux.count()
                .zipWith(productFlux.count())
                .map(tuple -> Math.min(tuple.getT1(), tuple.getT2()))
                .block();

        //when
        Flux<PurchaseOrderResponseDto> flux = Flux
                .zip(userFlux, productFlux)
                .map(this::buildDto)
                .flatMap(reqDto -> orderFulfillmentService.processOrder(Mono.just(reqDto)))
                .log();

        //then
        StepVerifier.create(flux)
                .expectNextCount(expectedSize)
                .verifyComplete();

    }

    private PurchaseOrderRequestDto buildDto(Tuple2<UserDto, ProductDto> tuple2) {
        return PurchaseOrderRequestDto.builder()
                .productId(tuple2.getT2().getId())
                .userId(tuple2.getT1().getId())
                .build();
    }
}
