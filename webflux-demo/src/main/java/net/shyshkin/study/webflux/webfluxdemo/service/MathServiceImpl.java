package net.shyshkin.study.webflux.webfluxdemo.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class MathServiceImpl implements MathService {


    @Override
    public Response findSquare(int input) {
        return new Response(input * input);
    }

    @Override
    public List<Response> multiplicationTable(int input) {
        return IntStream
                .rangeClosed(1, 10)
                .peek(i -> SleepUtil.sleep(1))
                .peek(i -> log.debug("math-service processing : {}", i))
                .map(i -> i * input)
                .mapToObj(Response::new)
                .collect(Collectors.toList());
    }
}
