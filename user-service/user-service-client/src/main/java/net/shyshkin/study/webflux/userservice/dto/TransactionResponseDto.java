package net.shyshkin.study.webflux.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto {

    private Integer userId;
    private Integer amount;
    private TransactionStatus status;
}
