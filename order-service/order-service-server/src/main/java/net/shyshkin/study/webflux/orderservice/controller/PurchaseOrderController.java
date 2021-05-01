package net.shyshkin.study.webflux.orderservice.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.service.OrderFulfillmentService;
import net.shyshkin.study.webflux.orderservice.service.OrderQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final OrderFulfillmentService orderFulfillmentService;
    private final OrderQueryService orderQueryService;

    @PostMapping
    public Mono<ResponseEntity<PurchaseOrderResponseDto>> purchaseOrder(@RequestBody Mono<PurchaseOrderRequestDto> requestDtoMono) {
        return orderFulfillmentService
                .processOrder(requestDtoMono)
                .map(respDto -> ResponseEntity.status(HttpStatus.CREATED).body(respDto))
                .onErrorReturn(WebClientResponseException.class, ResponseEntity.badRequest().build())
                .onErrorReturn(ex -> "RetryExhaustedException".equals(ex.getClass().getSimpleName()), ResponseEntity.badRequest().build())
                .onErrorReturn(WebClientRequestException.class, ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
    }

    @GetMapping(value = "users/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PurchaseOrderResponseDto> getOrdersByUserId(@PathVariable Integer userId) {
        return orderQueryService.getOrdersByUserId(userId);
    }
}
