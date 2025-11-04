-- 用户表结构
create table `t_user`
(
    `id`                  int auto_increment      primary key,
    `username`            varchar(50)  not null   comment '账户',
    `password`            varchar(100) default '' comment '密码',
    `nickname`            varchar(50)  default '' comment '昵称',
    `avatar`              varchar(255) default '' comment '头像',
    `first_name`          varchar(50)  default '' comment '第一个名字',
    `last_name`           varchar(50)  default '' comment '最后一个名字',
    `country`             varchar(50)  default '' comment '国家',
    `city`                varchar(50)  default '' comment '城市',
    `language`            varchar(50)  default '' comment '语言',
    `registration_channel` varchar(50)  default '' comment '注册渠道',
    `last_login_ip`       varchar(50)  default '' comment '最后登录ip',
    `last_login_time`     datetime     null       comment '最后登录时间',
    `status`              tinyint      default 1  comment '状态：1-启用，0-禁用',
    `created_time`        datetime     default CURRENT_TIMESTAMP,
    `modified_time`       datetime     default CURRENT_TIMESTAMP,
    unique key idx_username(`username`)
)ENGINE=InnoDB comment '用户表';

create table `t_user_provider`
(
    `id`                  int auto_increment      primary key,
    user_id               int                     not null comment '用户id',
    provider              varchar(30)             not null comment '第三方平台',
    provider_id           varchar(50)             not null comment '第三方平台id',
    created_time          datetime                default CURRENT_TIMESTAMP comment '创建时间',
    key idx_user_id(`user_id`),
    key idx_provider(`provider`, `provider_id`)
)ENGINE=InnoDB comment '用户第三方平台表';

create table t_email_template
(
    id                  int auto_increment            primary key,
    code                varchar(30)                   not null comment 'code',
    email               varchar(100)                  not null comment '发送邮箱',
    password            varchar(100)                  default '' comment '发送邮箱密码',
    host_name           varchar(100)                  default '' comment '发送邮箱服务器地址',
    user_name           varchar(50)                   default '' comment '发件人名称',
    subject             varchar(100)                  default '' comment '邮件标题',
    template_text       text                          null comment '邮件模板',
    `status`            tinyint                       default 1 comment '状态：1-启用，0-禁用',
    `created_time`      datetime                      default CURRENT_TIMESTAMP,
    `modified_time`     datetime                      default CURRENT_TIMESTAMP,
    key idx_code(`code`)
)ENGINE=InnoDB comment '邮箱模板表';
