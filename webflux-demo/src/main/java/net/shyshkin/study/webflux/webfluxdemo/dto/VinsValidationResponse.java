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
public class VinsValidationResponse {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private int errorCode;
    private int input;
    private String message;
}
