package net.shyshkin.study.webflux.webfluxdemo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import net.shyshkin.study.webflux.webfluxdemo.service.ReactiveMathService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
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
    public Mono<ResponseEntity<Response>> multiply(@RequestBody Mono<MultiplyRequestDto> requestMono,
                                                   @RequestHeader HttpHeaders httpHeaders) {

        log.debug("{}", httpHeaders);
        Optional<String> headerRespOptional = Optional
                .ofNullable(httpHeaders.get("X-art-request"))
                .filter(list -> list.size() > 0)
                .map(list -> list.get(0).toUpperCase() + "_resp");

        return mathService
                .multiply(requestMono)
                .map(response -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .headers(headers -> headerRespOptional
                                .ifPresent(headerToReturn -> headers.add("X-art-response", headerToReturn)))
                        .body(response));
    }
}
