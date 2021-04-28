package net.shyshkin.study.webflux.webfluxdemo.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.function.Predicate;

import static org.springframework.web.reactive.function.server.RequestPredicates.headers;

@Configuration
public class AssignmentRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> assignmentRouter(AssignmentRequestHandler handler) {
        return RouterFunctions.route()
                .GET("calculator/{first}/{second}", handler::calculate)
                .onError(Exception.class, (ex, request) -> ServerResponse.badRequest().bodyValue(ex.getMessage()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> assignmentRouter2(AssignmentRequestHandler handler) {
        return RouterFunctions.route()
                .GET("calculator2/{first}/{second}", headers(operator("+")), handler::plus)
                .GET("calculator2/{first}/{second}", headers(operator("-")), handler::minus)
                .GET("calculator2/{first}/{second}", headers(operator("*")), handler::multiply)
                .GET("calculator2/{first}/{second}", headers(operator("/")), handler::divide)
                .GET("calculator2/{first}/{second}", headers(operatorExists()),
                        req -> ServerResponse.badRequest().bodyValue("function for `" + req.headers().firstHeader("OP") + "` not found"))
                .GET("calculator2/{first}/{second}", req -> ServerResponse.badRequest().bodyValue("operator header 'OP' must not be empty or null"))
                .onError(Exception.class, (ex, request) -> ServerResponse.badRequest().bodyValue(ex.getMessage()))
                .build();
    }

    private Predicate<ServerRequest.Headers> operatorExists() {
        return headers -> headers.firstHeader("OP") != null;
    }

    private Predicate<ServerRequest.Headers> operator(String s) {
        return headers -> s.equals(headers.firstHeader("OP"));
    }
}
