package net.shyshkin.study.webflux.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import net.shyshkin.study.webflux.orderservice.client.ProductClient;
import net.shyshkin.study.webflux.orderservice.client.UserClient;
import net.shyshkin.study.webflux.orderservice.dto.OrderStatus;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.entity.PurchaseOrder;
import net.shyshkin.study.webflux.orderservice.repository.PurchaseOrderRepository;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderFulfillmentServiceRetryTest {

    public static final String MOCK_PRODUCT_ID = "608aadc95dd22725cee84293";
    public static MockWebServer mockBackEndProductService;
    public static MockWebServer mockBackEndUserService;
    private ProductClient productClient;
    private UserClient userClient;
    private String baseUrlProductService;
    private String baseUrlUserService;

    private final int DEFAULT_PRICE = 123;
    private final int DEFAULT_USER_ID = 567;

    @Mock
    PurchaseOrderRepository orderRepository;

    OrderFulfillmentService orderFulfillmentService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEndProductService = new MockWebServer();
        mockBackEndProductService.start();
        mockBackEndUserService = new MockWebServer();
        mockBackEndUserService.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEndProductService.shutdown();
        mockBackEndUserService.shutdown();
    }

    @BeforeEach
    void initialize() {
        baseUrlProductService = String.format("http://localhost:%s/products/",
                mockBackEndProductService.getPort());

        productClient = new ProductClient(WebClient.builder().build(), baseUrlProductService);

        baseUrlUserService = String.format("http://localhost:%s/users/",
                mockBackEndUserService.getPort());

        userClient = new UserClient(WebClient.builder().build(), baseUrlUserService);

        orderFulfillmentService = new OrderFulfillmentServiceImpl(userClient, productClient, orderRepository);
    }

    @Test
    void processOrder_retriesExhausted() {
        //given
        String productId = MOCK_PRODUCT_ID;
        int userId = DEFAULT_USER_ID;

        PurchaseOrderRequestDto orderRequestDto = PurchaseOrderRequestDto
                .builder()
                .productId(productId)
                .userId(userId)
                .build();

        String errorMessage = "Retries exhausted: 4/4 in a row (4 total)";

        for (int i = 0; i < 5; i++) {
            mockBackEndProductService.enqueue(getProductServiceMock500Response());
        }

        //when
        Mono<PurchaseOrderResponseDto> dtoMono = orderFulfillmentService.processOrder(Mono.just(orderRequestDto));

        //then
        StepVerifier.create(dtoMono)
                .verifyErrorSatisfies(ex -> assertThat(ex)
                        .hasMessage(errorMessage)
                        .satisfies(throwable -> assertThat(throwable.getClass().getSimpleName()).contains("RetryExhaustedException")));
    }

    @Test
    void processOrder_3retries_and_success() throws JsonProcessingException {
        //given
        String productId = MOCK_PRODUCT_ID;
        int userId = DEFAULT_USER_ID;

        PurchaseOrderRequestDto orderRequestDto = PurchaseOrderRequestDto
                .builder()
                .productId(productId)
                .userId(userId)
                .build();

        String errorMessage = "Retries exhausted: 4/4";

        for (int i = 0; i < 3; i++) {
            mockBackEndProductService.enqueue(getProductServiceMock500Response());
        }
        mockBackEndProductService.enqueue(getProductServiceSuccessResponse());

        mockBackEndUserService.enqueue(getUserServiceSuccessResponse());
        given(orderRepository.save(any(PurchaseOrder.class))).willReturn(
                PurchaseOrder.builder()
                        .id(Faker.instance().random().nextInt(1_000))
                        .productId(MOCK_PRODUCT_ID)
                        .amount(DEFAULT_PRICE)
                        .userId(DEFAULT_USER_ID)
                        .status(OrderStatus.COMPLETED)
                        .build());

        //when
        Mono<PurchaseOrderResponseDto> dtoMono = orderFulfillmentService.processOrder(Mono.just(orderRequestDto));

        //then
        StepVerifier.create(dtoMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    private MockResponse getUserServiceSuccessResponse() throws JsonProcessingException {
        TransactionResponseDto transactionResponseDto = TransactionResponseDto.builder()
                .amount(DEFAULT_PRICE)
                .userId(DEFAULT_USER_ID)
                .status(TransactionStatus.APPROVED)
                .build();
        String json = objectMapper.writeValueAsString(transactionResponseDto);
        return new MockResponse()
                .setBody(json)
                .setResponseCode(201)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @NotNull
    private MockResponse getProductServiceMock500Response() {
        String errorResponseBody =
                "{\n" +
                        "\"timestamp\": \"2021-05-01T15:41:29.851+00:00\",\n" +
                        "\"path\": \"/products/608aadc95dd22725cee84293\",\n" +
                        "\"status\": 500,\n" +
                        "\"error\": \"Internal Server Error\",\n" +
                        "\"message\": \"\",\n" +
                        "\"requestId\": \"d09151c6-21\"\n" +
                        "}";
        return new MockResponse().setResponseCode(500).setBody(errorResponseBody);
    }

    private MockResponse getProductServiceSuccessResponse() throws JsonProcessingException {

        ProductDto productDto = ProductDto.builder()
                .id(MOCK_PRODUCT_ID)
                .description("Some description")
                .price(DEFAULT_PRICE)
                .build();
        String productJson = objectMapper.writeValueAsString(productDto);
        return new MockResponse()
                .setBody(productJson)
                .setResponseCode(201)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
}