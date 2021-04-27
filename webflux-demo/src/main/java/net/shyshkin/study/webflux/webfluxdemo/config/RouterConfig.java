package net.shyshkin.study.webflux.webfluxdemo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class RouterConfig {

    private final RequestHandler handler;

    @Bean
    public RouterFunction<ServerResponse> serverResponseRouterFunction() {
        return RouterFunctions
                .route()
                .GET("router/square/{input}", handler::findSquare)
                .GET("router-vins/square/{input}", handler::findSquareVins)
                .GET("router/table/{input}", handler::multiplicationTable)
                .build();
    }

}
