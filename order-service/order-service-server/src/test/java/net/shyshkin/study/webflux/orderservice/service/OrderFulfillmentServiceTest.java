package net.shyshkin.study.webflux.orderservice.service;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@SpringBootTest
class OrderFulfillmentServiceTest {

    @MockBean
    UserClient userClient;

    @MockBean
    ProductClient productClient;

    @Autowired
    OrderFulfillmentService orderFulfillmentService;

    @Autowired
    PurchaseOrderRepository orderRepository;

    @ParameterizedTest
    @CsvSource({
            "APPROVED,COMPLETED",
            "DECLINED,FAILED",
    })
    void processOrder(TransactionStatus transactionStatus, OrderStatus expectedOrderStatus) {
        //given
        int userId = 321;
        String productId = "Super Cool Car";
        int amount = 1_000_000;

        PurchaseOrderRequestDto orderRequestDto = PurchaseOrderRequestDto
                .builder()
                .productId(productId)
                .userId(userId)
                .build();
        Mono<PurchaseOrderRequestDto> orderMono = Mono.just(orderRequestDto);

        given(productClient.getProductById(anyString())).willReturn(Mono.just(ProductDto.builder().price(amount).id(productId).description("Bla").build()));
        given(userClient.createTransaction(any())).willReturn(Mono.just(TransactionResponseDto.builder().amount(amount).userId(userId).status(transactionStatus).build()));

        AtomicInteger orderId = new AtomicInteger(0);

        //when
        Mono<PurchaseOrderResponseDto> processOrderMono = orderFulfillmentService
                .processOrder(orderMono)
                .doOnNext(resp -> orderId.set(resp.getOrderId()));

        //then
        StepVerifier.create(processOrderMono)
                .assertNext(orderResponseDto ->
                        {
                            assertThat(orderResponseDto)
                                    .hasNoNullFieldsOrProperties()
                                    .hasFieldOrPropertyWithValue("userId", userId)
                                    .hasFieldOrPropertyWithValue("productId", productId)
                                    .hasFieldOrPropertyWithValue("amount", amount)
                                    .hasFieldOrPropertyWithValue("status", expectedOrderStatus);
                        }
                )
                .verifyComplete();

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