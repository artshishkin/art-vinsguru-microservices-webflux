package net.shyshkin.study.webflux.userservice.repository;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.entity.User;
import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserBeforeConvertCallback2 implements BeforeConvertCallback<User>, Ordered {

    @Override
    public Publisher<User> onBeforeConvert(User user, SqlIdentifier sqlIdentifier) {
        log.debug("some `User` modification/logging for: `{}`", user);
        return Mono.just(user);
    }

    @Override
    public int getOrder() {
        // int HIGHEST_PRECEDENCE = -2147483648;
        // int LOWEST_PRECEDENCE = 2147483647;
        return 0;
    }
}
