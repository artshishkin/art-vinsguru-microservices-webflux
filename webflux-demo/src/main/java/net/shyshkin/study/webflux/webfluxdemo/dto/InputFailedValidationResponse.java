package net.shyshkin.study.webflux.webfluxdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InputFailedValidationResponse {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private Integer status;
    private String error;
    private String message;
}
