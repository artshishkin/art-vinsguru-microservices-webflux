package net.shyshkin.study.webflux.orderservice.controller;

import com.github.javafaker.Faker;
import net.shyshkin.study.webflux.orderservice.dto.OrderStatus;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.entity.PurchaseOrder;
import net.shyshkin.study.webflux.orderservice.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient
@SpringBootTest
class PurchaseOrderControllerQueryTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    PurchaseOrderRepository repository;

    @Test
    void getOrdersByUserId() {
        //given
        int userId = Faker.instance().random().nextInt(2, 1_000_000);
        int ordersSize = Faker.instance().random().nextInt(1, 100);

        List<PurchaseOrder> list1 = createOrderList(1, 10);
        List<PurchaseOrder> list2 = createOrderList(userId, ordersSize);
        repository.saveAll(list1);
        repository.saveAll(list2);

        //when
        webClient
                .get()
                .uri("/orders/users/{userId}", userId)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(PurchaseOrderResponseDto.class)
                .value(list -> assertThat(list)
                        .hasSize(ordersSize)
                        .allSatisfy(order -> assertThat(order).hasNoNullFieldsOrProperties().hasFieldOrPropertyWithValue("userId", userId)));
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