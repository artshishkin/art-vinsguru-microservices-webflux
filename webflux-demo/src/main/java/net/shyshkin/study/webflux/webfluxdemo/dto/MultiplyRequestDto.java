package net.shyshkin.study.webflux.webfluxdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiplyRequestDto {
    private int first;
    private int second;
}
