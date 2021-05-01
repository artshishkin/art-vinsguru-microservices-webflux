package net.shyshkin.study.webflux.orderservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class ProductClientTest {

    public static MockWebServer mockBackEnd;
    private ProductClient productClient;
    private String baseUrl;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        baseUrl = String.format("http://localhost:%s/products/",
                mockBackEnd.getPort());

        productClient = new ProductClient(WebClient.builder().build(), baseUrl);
    }

    @Test
    void getProductById_success() throws JsonProcessingException {
        //given
        String productId = "existing_product_id";
        ProductDto productDto = ProductDto.builder().id(productId).price(123).description("Some description").build();

        ObjectMapper objectMapper = new ObjectMapper();
        String productJson = objectMapper.writeValueAsString(productDto);
        mockBackEnd.enqueue(new MockResponse().setBody(productJson).addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));

        //when
        Mono<ProductDto> mono = productClient.getProductById(productId);

        //then
        StepVerifier.create(mono.log())
                .expectNext(productDto)
                .verifyComplete();
    }

    @Test
    void getProductById_absentProduct() {
        //given
        String productId = "absent_product_id";
        mockBackEnd.enqueue(new MockResponse().setResponseCode(404));
        String errorMessage = String.format("404 Not Found from GET %s%s", baseUrl, productId);

        //when
        Mono<ProductDto> mono = productClient.getProductById(productId);

        //then
        StepVerifier.create(mono.log())
                .verifyErrorSatisfies(ex -> assertThat(ex)
                        .isInstanceOf(WebClientResponseException.class)
                        .hasMessage(errorMessage));
    }

    @Test
    void getProductById_whenServerError() {
        //given
        String productId = "608aadc95dd22725cee84293";
        String errorResponseBody =
                "{\n" +
                "\"timestamp\": \"2021-05-01T15:41:29.851+00:00\",\n" +
                "\"path\": \"/products/608aadc95dd22725cee84293\",\n" +
                "\"status\": 500,\n" +
                "\"error\": \"Internal Server Error\",\n" +
                "\"message\": \"\",\n" +
                "\"requestId\": \"d09151c6-21\"\n" +
                "}";
        mockBackEnd.enqueue(new MockResponse().setResponseCode(500).setBody(errorResponseBody));
        String errorMessage = String.format("500 Internal Server Error from GET %s%s", baseUrl, productId);

        //when
        Mono<ProductDto> mono = productClient.getProductById(productId);

        //then
        StepVerifier.create(mono.log())
                .verifyErrorSatisfies(ex -> assertThat(ex)
                        .isInstanceOf(WebClientResponseException.class)
                        .hasMessage(errorMessage));
    }
}