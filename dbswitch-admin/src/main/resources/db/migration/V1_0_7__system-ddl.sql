CREATE TABLE IF NOT EXISTS `DBSWITCH_JOB_LOGBACK` (
  `id`          bigint          not null auto_increment                 comment '自增id',
  `uuid`        varchar(128)    not null                                comment 'job id',
  `content`     longtext                                                comment '日志内容',
  `create_time` timestamp       not null default current_timestamp      comment '创建时间',
  PRIMARY KEY (`id`),
  KEY `uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='JOB执行日志';
