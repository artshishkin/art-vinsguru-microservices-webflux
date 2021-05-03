package net.shyshkin.study.webflux.userservice.repository;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.entity.User;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserBeforeConvertCallback implements BeforeConvertCallback<User> {

    private static final String PATTERN = "[^a-zA-Z .']";

    @Override
    public Publisher<User> onBeforeConvert(User user, SqlIdentifier sqlIdentifier) {
        String updatedName = user.getName().replaceAll(PATTERN, "");
        log.debug("original name: `{}`, updated name: `{}`", user.getName(), updatedName);
        user.setName(updatedName);
        return Mono.just(user);
    }
}
