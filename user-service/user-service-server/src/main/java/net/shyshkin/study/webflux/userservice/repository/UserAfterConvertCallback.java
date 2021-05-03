package net.shyshkin.study.webflux.userservice.repository;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.entity.User;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Profile("after-convert-test")
public class UserAfterConvertCallback implements AfterConvertCallback<User> {

    @Override
    public Publisher<User> onAfterConvert(User user, SqlIdentifier sqlIdentifier) {
        Integer totalBalance = user.getBalance();
        int reserved = totalBalance / 10;
        int balanceToShow = totalBalance - reserved;
        log.debug("total balance: {}, reserved: {}, available: {}", totalBalance, reserved, balanceToShow);
        user.setBalance(balanceToShow);
        return Mono.just(user);
    }
}
