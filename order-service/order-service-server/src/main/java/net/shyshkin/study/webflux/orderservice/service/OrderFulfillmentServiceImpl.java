package net.shyshkin.study.webflux.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.orderservice.client.ProductClient;
import net.shyshkin.study.webflux.orderservice.client.UserClient;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.dto.RequestContext;
import net.shyshkin.study.webflux.orderservice.repository.PurchaseOrderRepository;
import net.shyshkin.study.webflux.orderservice.util.EntityDtoUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFulfillmentServiceImpl implements OrderFulfillmentService {

    private final UserClient userClient;
    private final ProductClient productClient;
    private final PurchaseOrderRepository orderRepository;

    @Override
    public Mono<PurchaseOrderResponseDto> processOrder(Mono<PurchaseOrderRequestDto> requestDtoMono) {
        return requestDtoMono
                .map(RequestContext::new)
                .flatMap(this::productRequestResponse)
                .doOnNext(EntityDtoUtil::setTransactionDtoUtil)
                .flatMap(this::userRequestResponse)
                .map(EntityDtoUtil::getPurchaseOrder)
                .map(orderRepository::save) //blocking
                .doOnNext(purchaseOrder -> log.debug("Saved {}", purchaseOrder))
                .map(EntityDtoUtil::toDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<RequestContext> productRequestResponse(RequestContext rc) {
        return productClient
                .getProductById(rc.getPurchaseOrderRequestDto().getProductId())
                .doOnNext(rc::setProductDto)
                .doOnError(ex -> log.error("------------{}:{}", ex.getClass().getName(), ex.getMessage()))
                .retryWhen(Retry.from(retrySignalFlux ->

                        retrySignalFlux
                                .handle((rs, synchronousSink) -> {
                                    Throwable failure = rs.failure();
                                    if (failure instanceof WebClientResponseException) {
                                        WebClientResponseException exception = (WebClientResponseException) failure;
                                        switch (exception.getStatusCode()) {
                                            case NOT_FOUND:
                                                synchronousSink.error(failure);
                                                break;
                                            case INTERNAL_SERVER_ERROR:
                                                if (rs.totalRetriesInARow() > 3) {
                                                    synchronousSink.error(
                                                            Exceptions
                                                                    .retryExhausted(
                                                                            String.format(
                                                                                    "Retries exhausted: %d/4 in a row (%d total)",
                                                                                    rs.totalRetriesInARow(), rs.totalRetries()),
                                                                            failure)
                                                    );
                                                } else
                                                    synchronousSink.next(1);
                                                break;
                                            default:
                                                synchronousSink.error(failure);
                                                break;
                                        }
                                    } else if (failure instanceof WebClientRequestException) {
                                        synchronousSink.error(failure);
                                    } else {
                                        synchronousSink.error(failure);
                                    }
                                })
                                .delayElements(Duration.ofMillis(100))))
                .thenReturn(rc);
    }

    private Mono<RequestContext> userRequestResponse(RequestContext rc) {
        return userClient
                .createTransaction(rc.getTransactionRequestDto())
                .doOnNext(rc::setTransactionResponseDto)
                .thenReturn(rc);
    }

}
