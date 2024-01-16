create table app_user (
    id serial primary key,
    username varchar(255),
    password varchar(512)
);

create table user_role (
    user_id integer,
    role varchar(255)
);

create table word (
     id bigserial primary key,
     div_id bigint,
     paragraph_id bigint,
     sentence_id bigint,
     word_order bigint,
     content varchar(512),
     pos_label varchar(100)
);

create table user_paragraph (
     id serial primary key,
     user_id integer,
     div_id bigint,
     paragraph_id bigint
);

create table amr_tree
(
    id serial primary key,
    user_id bigint,
    sentence_position varchar(255),
    name varchar(255)
);