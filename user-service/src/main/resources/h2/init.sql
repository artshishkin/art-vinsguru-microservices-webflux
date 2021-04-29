create table if not exists users
(
    id      bigint auto_increment,
    name    varchar(255),
    balance int,
    primary key (id)
);

create table if not exists user_transaction
(
    id        bigint auto_increment,
    user_id   bigint,
    amount    int,
    timestamp TIMESTAMP,
    primary key (id),
    foreign key (user_id) REFERENCES users (id)
);
