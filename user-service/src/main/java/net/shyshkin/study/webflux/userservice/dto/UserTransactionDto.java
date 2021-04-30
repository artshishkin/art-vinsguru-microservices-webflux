package net.shyshkin.study.webflux.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTransactionDto {

    private Integer id;
    private Integer userId;
    private Integer amount;
    private LocalDateTime timestamp;

}
