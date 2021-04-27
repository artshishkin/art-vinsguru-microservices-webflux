package net.shyshkin.study.webflux.webfluxdemo.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import net.shyshkin.study.webflux.webfluxdemo.service.ReactiveMathService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("reactive-math")
@RequiredArgsConstructor
public class ReactiveMathController {

    private final ReactiveMathService mathService;

    @GetMapping("square/{input}")
    public Mono<Response> findSquare(@PathVariable Integer input) {
        return mathService.findSquare(input);
    }

    @GetMapping(value = "table/{input}")
    public Flux<Response> multiplicationTable(@PathVariable Integer input) {
        // AbstractJackson2Encoder: for non-streaming -> `.collectList().map(list -> encodeValue(` ; for streaming -> `.map(value -> encodeStreamingValue(`
        return mathService.multiplicationTable(input);
    }

    @GetMapping(value = "table/{input}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Response> multiplicationTableStream(@PathVariable Integer input) {
        return mathService.multiplicationTable(input);
    }

    @PostMapping("multiply")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response> multiply(@RequestBody Mono<MultiplyRequestDto> requestMono) {
        return mathService.multiply(requestMono);
    }
}
