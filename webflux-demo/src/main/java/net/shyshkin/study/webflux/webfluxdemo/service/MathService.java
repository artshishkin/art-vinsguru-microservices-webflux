package net.shyshkin.study.webflux.webfluxdemo.service;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;

import java.util.List;

public interface MathService {

    Response findSquare(int input);

    List<Response> multiplicationTable(int input);

}
