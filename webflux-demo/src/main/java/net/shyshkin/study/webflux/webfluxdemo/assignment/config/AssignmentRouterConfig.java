package net.shyshkin.study.webflux.webfluxdemo.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AssignmentRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> assignmentRouter(AssignmentRequestHandler handler) {
        return RouterFunctions.route()
                .GET("calculator/{first}/{second}", handler::calculate)
                .onError(Exception.class, (ex, request) -> ServerResponse.badRequest().bodyValue(ex.getMessage()))
                .build();
    }
}
