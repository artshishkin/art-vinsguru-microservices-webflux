package net.shyshkin.study.webflux.userservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("user_transaction")
public class UserTransaction {

    @Id
    private Integer id;
    private Integer userId;
    private Integer amount;
    private LocalDateTime timestamp;

}
