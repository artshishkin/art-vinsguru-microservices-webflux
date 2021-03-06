create table if not exists users
(
    id      serial,
    name    varchar(255),
    balance int,
    created_by varchar(50),
    created TIMESTAMP,
    modified TIMESTAMP,
    admin_name varchar(50),
    primary key (id)
);

create table if not exists user_transaction
(
    id        serial,
    user_id   bigint,
    amount    int,
    timestamp TIMESTAMP,
    primary key (id),
    foreign key (user_id) REFERENCES users (id) on DELETE cascade
);

-- insert into users (name, balance)
-- VALUES ('Art', 10000),
--        ('Kate', 15000),
--        ('Arina', 20000),
--        ('Nazar', 25000);
