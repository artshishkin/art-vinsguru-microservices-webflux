package net.shyshkin.study.webflux.orderservice.controller;

import com.github.javafaker.Faker;
import net.shyshkin.study.webflux.orderservice.client.ProductClient;
import net.shyshkin.study.webflux.orderservice.client.UserClient;
import net.shyshkin.study.webflux.orderservice.dto.OrderStatus;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.entity.PurchaseOrder;
import net.shyshkin.study.webflux.orderservice.repository.PurchaseOrderRepository;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@Disabled("Only for manual testing")
@DisplayName("Start `mongodb`, `user-service` and `product-service` first, then run test")
class PurchaseOrderControllerManualTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    UserClient userClient;

    @Autowired
    ProductClient productClient;

    @Autowired
    PurchaseOrderRepository orderRepository;

    @Test
    void purchaseOrder() {
        //given
        int userId = 4;
        ProductDto randomProduct = getRandomProduct();
        String productId = randomProduct.getId();
        int amount = randomProduct.getPrice();
        OrderStatus expectedOrderStatus = OrderStatus.COMPLETED;

        PurchaseOrderRequestDto orderRequestDto = PurchaseOrderRequestDto
                .builder()
                .productId(productId)
                .userId(userId)
                .build();

        AtomicInteger orderId = new AtomicInteger(0);

        //when
        webClient.post()
                .uri("/orders")
                .bodyValue(orderRequestDto)
                .exchange()

                //then
                .expectStatus().isCreated()
                .expectBody(PurchaseOrderResponseDto.class)
                .value(orderResponseDto -> {
                    assertThat(orderResponseDto)
                            .hasNoNullFieldsOrProperties()
                            .hasFieldOrPropertyWithValue("userId", userId)
                            .hasFieldOrPropertyWithValue("productId", productId)
                            .hasFieldOrPropertyWithValue("amount", amount)
                            .hasFieldOrPropertyWithValue("status", expectedOrderStatus);
                    orderId.set(orderResponseDto.getOrderId());
                });

        Optional<PurchaseOrder> orderOptional = orderRepository.findById(orderId.get());
        assertThat(orderOptional)
                .hasValueSatisfying(
                        order -> assertThat(order)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("id", orderId.get())
                                .hasFieldOrPropertyWithValue("userId", userId)
                                .hasFieldOrPropertyWithValue("productId", productId)
                                .hasFieldOrPropertyWithValue("amount", amount)
                                .hasFieldOrPropertyWithValue("status", expectedOrderStatus)
                );

    }

    private ProductDto getRandomProduct() {
        return productClient
                .getAll()
                .collectList()
                .map(list -> list.get(Faker.instance().random().nextInt(list.size())))
                .block();
    }

}