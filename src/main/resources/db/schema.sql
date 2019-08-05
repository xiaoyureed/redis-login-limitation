create table user
(
    id int auto_increment,
    name varchar(50) default '' not null,
    pwd varchar(50) default '' not null,
    constraint user_pk
        primary key (id)
);