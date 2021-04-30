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
    foreign key (user_id) REFERENCES users (id) on DELETE cascade
);

insert into users (name, balance)
VALUES ('Art', 1000),
       ('Kate', 1500),
       ('Arina', 2000),
       ('Nazar', 2500);
