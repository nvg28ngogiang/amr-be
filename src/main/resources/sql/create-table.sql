create table app_user (
    id serial primary key,
    username varchar(255),
    password varchar(512),
    name varchar(255)
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

create table amr_word
(
    id serial primary key,
    tree_id bigint,
    word_id bigint,
    parent_id bigint,
    path varchar(1000),
    word_label varchar(255),
    amr_label_id int,
    word_sense_id bigint
    corref_id bigint,
    corref_position varchar(255)
);

create table word_sense
(
    id serial primary key,
    word_content varchar(255),
    sense varchar(255),
    example varchar(2000)
);

create table amr_label
(
    id serial primary key,
    name varchar(255),
    group_code varchar(255)
);