package net.shyshkin.study.webflux.webfluxdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Response {

    private final LocalDateTime date = LocalDateTime.now();

    private int output;

}
