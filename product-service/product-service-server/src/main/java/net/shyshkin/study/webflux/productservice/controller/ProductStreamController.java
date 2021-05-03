package net.shyshkin.study.webflux.productservice.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductStreamController {

    private final Flux<ProductDto> productBroadcast;

    @GetMapping(value = "broadcast", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductDto> getProductUpdates(@RequestParam(defaultValue = "0x7fffffff") Integer maxPrice) {
        return productBroadcast.filter(dto -> dto.getPrice() <= maxPrice);
    }
}
