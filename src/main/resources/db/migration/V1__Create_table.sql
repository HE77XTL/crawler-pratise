DROP TABLE IF EXISTS LINK;
DROP TABLE IF EXISTS NEWS;

create table LINK
(
    id          int           not null primary key auto_increment,
    url         varchar(1000) not null,
    status      tinyint       not null,
    create_time datetime,
    modify_time datetime
);

create table NEWS
(
    id          int           not null primary key auto_increment,
    url         varchar(1000) not null,
    title       varchar(1000),
    content     text,
    create_time datetime,
    modify_time datetime
);