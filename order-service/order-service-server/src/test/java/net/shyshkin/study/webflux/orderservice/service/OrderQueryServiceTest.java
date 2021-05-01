package net.shyshkin.study.webflux.orderservice.service;

import com.github.javafaker.Faker;
import net.shyshkin.study.webflux.orderservice.dto.OrderStatus;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.entity.PurchaseOrder;
import net.shyshkin.study.webflux.orderservice.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class OrderQueryServiceTest {

    @Autowired
    OrderQueryService orderQueryService;

    @MockBean
    PurchaseOrderRepository repository;

    @Test
    void getOrdersByUserId() {
        //given
        int userId = 123;
        int ordersSize = 10;
        List<PurchaseOrder> purchaseOrderList = createOrderList(userId, ordersSize);

        given(repository.findAllByUserId(anyInt())).willReturn(purchaseOrderList);

        //when
        Flux<PurchaseOrderResponseDto> responseDtoFlux = orderQueryService
                .getOrdersByUserId(userId)
                .log();

        //then
        AtomicInteger counter = new AtomicInteger(0);
        StepVerifier.create(responseDtoFlux)
                .thenConsumeWhile(
                        responseDto -> responseDto.getUserId() == userId,
                        dto -> assertThat(Thread.currentThread().getName())
                                .doesNotContain("main")
                                .satisfies(name -> counter.incrementAndGet())
                )
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(ordersSize);
    }

    private PurchaseOrder createRandomOrder(int id, int userId) {
        return PurchaseOrder.builder()
                .id(id)
                .status(Faker.instance().random().nextBoolean() ? OrderStatus.COMPLETED : OrderStatus.FAILED)
                .userId(userId)
                .amount(Faker.instance().random().nextInt(100, 500))
                .productId(Faker.instance().random().hex())
                .build();
    }

    private List<PurchaseOrder> createOrderList(int userId, int size) {
        return IntStream.rangeClosed(1, size)
                .mapToObj(i -> createRandomOrder(i, userId))
                .collect(Collectors.toList());
    }
}