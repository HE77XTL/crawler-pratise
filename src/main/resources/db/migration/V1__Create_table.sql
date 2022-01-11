DROP TABLE IF EXISTS LINK;

create table LINK
(
    id          int not null primary key auto_increment,
    url         varchar(1000),
    status      tinyint,
    create_time datetime,
    modify_time datetime
);