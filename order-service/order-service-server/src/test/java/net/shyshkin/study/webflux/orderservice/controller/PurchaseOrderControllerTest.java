package net.shyshkin.study.webflux.orderservice.controller;

import net.shyshkin.study.webflux.orderservice.client.ProductClient;
import net.shyshkin.study.webflux.orderservice.client.UserClient;
import net.shyshkin.study.webflux.orderservice.dto.OrderStatus;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.entity.PurchaseOrder;
import net.shyshkin.study.webflux.orderservice.repository.PurchaseOrderRepository;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
@AutoConfigureWebTestClient
class PurchaseOrderControllerTest {

    @Autowired
    WebTestClient webClient;

    @MockBean
    UserClient userClient;

    @MockBean
    ProductClient productClient;

    @Autowired
    PurchaseOrderRepository orderRepository;

    @ParameterizedTest
    @CsvSource({
            "APPROVED,COMPLETED",
            "DECLINED,FAILED",
    })
    void purchaseOrder(TransactionStatus transactionStatus, OrderStatus expectedOrderStatus) {
        //given
        int userId = 321;
        String productId = "Super Cool Car";
        int amount = 1_000_000;

        PurchaseOrderRequestDto orderRequestDto = PurchaseOrderRequestDto
                .builder()
                .productId(productId)
                .userId(userId)
                .build();

        given(productClient.getProductById(anyString())).willReturn(Mono.just(ProductDto.builder().price(amount).id(productId).description("Bla").build()));
        given(userClient.createTransaction(any())).willReturn(Mono.just(TransactionResponseDto.builder().amount(amount).userId(userId).status(transactionStatus).build()));

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

        then(productClient).should().getProductById(eq(productId));
        then(userClient).should().createTransaction(any(TransactionRequestDto.class));

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
}