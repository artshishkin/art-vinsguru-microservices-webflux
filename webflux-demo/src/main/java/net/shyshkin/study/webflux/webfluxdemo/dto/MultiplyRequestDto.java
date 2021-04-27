package net.shyshkin.study.webflux.webfluxdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiplyRequestDto {

    @Min(value = 1, message = "must not be less then 1")
    @Max(value = 10, message = "must not be larger then 10")
    private int first;

    @Min(value = 1, message = "must not be less then 1")
    @Max(value = 10, message = "must not be larger then 10")
    private int second;
}
