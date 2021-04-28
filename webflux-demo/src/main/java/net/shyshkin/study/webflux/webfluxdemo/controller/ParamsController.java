package net.shyshkin.study.webflux.webfluxdemo.controller;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("params")
public class ParamsController {

    @GetMapping
    public Mono<Response> product(@RequestParam Integer first, @RequestParam Integer second) {
        return Mono.fromSupplier(() -> new Response(first * second));
    }

}
