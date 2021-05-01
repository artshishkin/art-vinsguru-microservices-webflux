package net.shyshkin.study.webflux.orderservice.controller;

import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "36000")
class PurchaseOrderControllerNoConnectionTest {

    @Autowired
    WebTestClient webClient;

    @Test
    void purchaseOrder_noConnection() {
        //given
        int userId = 321;
        String productId = "608aadc95dd22725cee842hh";

        PurchaseOrderRequestDto orderRequestDto = PurchaseOrderRequestDto
                .builder()
                .productId(productId)
                .userId(userId)
                .build();

        //when
        webClient.post()
                .uri("/orders")
                .bodyValue(orderRequestDto)
                .exchange()

                //then
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody().isEmpty();
    }

}