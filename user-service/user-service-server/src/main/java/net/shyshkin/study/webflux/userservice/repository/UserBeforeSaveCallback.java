package net.shyshkin.study.webflux.userservice.repository;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.userservice.entity.User;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.r2dbc.core.Parameter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserBeforeSaveCallback implements BeforeSaveCallback<User> {

    @Override
    public Publisher<User> onBeforeSave(User user, OutboundRow outboundRow, SqlIdentifier sqlIdentifier) {

        outboundRow.put(SqlIdentifier.unquoted("created_by"), Parameter.from("art"));
        return Mono.just(user);
    }
}
