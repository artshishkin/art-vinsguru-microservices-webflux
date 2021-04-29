create table users
(
    id      bigint auto_increment,
    name    varchar(255),
    balance int,
    primary key (id)
);

create table user_transaction
(
    id        bigint auto_increment,
    user_id   bigint,
    amount    int,
    timestamp TIMESTAMP,
    primary key (id),
    foreign key (user_id) REFERENCES users (id)
);
