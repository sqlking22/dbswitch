CREATE TABLE IF NOT EXISTS DBSWITCH_SYSTEM_USER  (
  "id"                  bigserial             not null,
  "username"            varchar(255)          not null,
  "password"            varchar(128)          not null,
  "salt"                varchar(128)          not null,
  "real_name"           varchar(255)          not null default '',
  "email"               varchar(255)          not null default '',
  "address"             varchar(255)          not null default '',
  "locked"              boolean               not null default false,
  "create_time"         timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  "update_time"         timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX DBSWITCH_SYSTEM_USER_USERNAME_IDX ON DBSWITCH_SYSTEM_USER("username");
COMMENT ON TABLE DBSWITCH_SYSTEM_USER IS '系统用户表';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."id" IS '主键id';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."username" IS '登录名称';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."password" IS '登录密码';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."salt" IS '密码盐值';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."real_name" IS '实际姓名';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."email" IS '电子邮箱';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."address" IS '所在地址';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."locked" IS '是否锁定';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."create_time" IS '创建时间';
COMMENT ON COLUMN DBSWITCH_SYSTEM_USER."update_time" IS '修改时间';

CREATE TABLE IF NOT EXISTS DBSWITCH_SYSTEM_LOG (
  "id"                  bigserial             not null,
  "type"                int2                  not null default 0,
  "username"            varchar(64)           not null default '',
  "ip_address"          varchar(64)           not null default '',
  "module_name"         varchar(64)           not null default '',
  "content"             text                                     ,
  "url_path"            varchar(64)           not null default '',
  "user_agent"          varchar(255)          not null default '',
  "failed"              boolean               not null default false,
  "exception"           text                                ,
  "elapse_seconds"      int8                  not null default 0,
  "create_time"         timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  PRIMARY KEY ("id")
);

COMMENT ON TABLE DBSWITCH_SYSTEM_LOG IS '操作日志';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."id" IS '主键';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."type" IS '日志类型:0-访问日志;1-操作日志';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."username" IS '操作用户';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."ip_address" IS '客户端ip';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."module_name" IS '模块名';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."content" IS '日志描述';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."url_path" IS 'path路径';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."user_agent" IS '客户端agent';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."failed" IS '是否异常(0:否 1:是)';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."exception" IS '异常堆栈信息';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."elapse_seconds" IS '执行时间（单位毫秒）';
COMMENT ON COLUMN DBSWITCH_SYSTEM_LOG."create_time" IS '创建时间';

CREATE TABLE IF NOT EXISTS DBSWITCH_DATABASE_CONNECTION (
  "id"                  bigserial             not null,
  "name"                varchar(200)          not null default '',
  "type"                varchar(200)          not null default '',
  "version"             varchar(255)          not null default '',
  "driver"              varchar(200)          not null default '',
  "url"                 text                                     ,
  "username"            varchar(200)          not null default '',
  "password"            varchar(200)          not null default '',
  "create_time"         timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  "update_time"         timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  primary key ("id")
);
CREATE UNIQUE INDEX DBSWITCH_DATABASE_CONNECTION_NAME_IDX ON DBSWITCH_DATABASE_CONNECTION("name");
COMMENT ON TABLE DBSWITCH_DATABASE_CONNECTION IS '数据库连接';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."id" IS '主键';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."name" IS '连接名称';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."type" IS '数据库类型';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."version" IS '驱动版本';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."driver" IS '驱动类名称';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."url" IS 'jdbc-url连接串';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."username" IS '连接账号';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."password" IS '账号密码';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."create_time" IS '创建时间';
COMMENT ON COLUMN DBSWITCH_DATABASE_CONNECTION."update_time" IS '修改时间';

CREATE TABLE IF NOT EXISTS DBSWITCH_ASSIGNMENT_TASK (
  "id"              bigserial             not null,
  "name"            varchar(200)          not null default '',
  "description"     text                                     ,
  "schedule_mode"   varchar(50)           null default null,
  "cron_expression" varchar(200)          not null default '',
  "published"       boolean               not null default false,
  "content"         text                                ,
  "job_key"         varchar(128) DEFAULT '',
  "create_time"     timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  "update_time"     timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  primary key ("id")
);
COMMENT ON TABLE DBSWITCH_ASSIGNMENT_TASK IS '任务信息表';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."id" IS '主键';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."name" IS '任务名称';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."description" IS '任务描述';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."schedule_mode" IS '调度方式(cron/无调度)';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."cron_expression" IS '调度cron表达式';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."published" IS '是否已发布(0:否 1:是)';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."content" IS '发布的配置JSON格式';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."job_key" IS 'JOB KEY';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."create_time" IS '创建时间';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_TASK."update_time" IS '修改时间';

CREATE TABLE IF NOT EXISTS DBSWITCH_ASSIGNMENT_CONFIG (
  "id"                          bigserial             not null,
  "assignment_id"               int8                  not null,
  "source_connection_id"        int8                  not null,
  "source_schema"               varchar(1024)         not null,
  "table_type"                  varchar(32)           not null default 'TABLE',
  "source_tables"               text                  ,
  "excluded"                    boolean               not null default false,
  "target_connection_id"        int8                  not null,
  "target_schema"               varchar(200)          not null,
  "table_name_map"              text                  ,
  "column_name_map"             text                  ,
  "target_drop_table"           boolean               not null default false,
  "target_only_create"          boolean               not null default false,
  "batch_size"                  int8                  not null default 10000,
  "first_flag"                  boolean               not null default false,
  "create_time"                 timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  primary key ("id"),
  foreign key ("assignment_id") references DBSWITCH_ASSIGNMENT_TASK ("id") on delete cascade on update cascade,
  foreign key ("source_connection_id") references DBSWITCH_DATABASE_CONNECTION ("id") on delete cascade on update cascade,
  foreign key ("target_connection_id") references DBSWITCH_DATABASE_CONNECTION ("id") on delete cascade on update cascade
);
CREATE UNIQUE INDEX DBSWITCH_ASSIGNMENT_CONFIG_AID_UNIQUE_IDX ON DBSWITCH_ASSIGNMENT_CONFIG ("assignment_id");
COMMENT ON TABLE DBSWITCH_ASSIGNMENT_CONFIG IS '任务配置表';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."id" IS '主键';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."assignment_id" IS '任务ID';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."source_connection_id" IS '来源端连接ID';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."source_schema" IS '来源端的schema';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."table_type" IS '表类型:TABLE;VIEW';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."source_tables" IS '来源端的table列表';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."excluded" IS '是否排除(0:否 1:是)';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."target_connection_id" IS '目的端连接ID';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."target_schema" IS '目的端的schema(一个)';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."table_name_map" IS '表名映射关系';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."column_name_map" IS '字段名映射关系';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."target_drop_table" IS '同步前是否先删除目的表(0:否 1:是)';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."target_only_create" IS '是否只建表';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."batch_size" IS '处理批次大小';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."first_flag" IS '首次加载数据';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_CONFIG."create_time" IS '创建时间';

CREATE TABLE IF NOT EXISTS DBSWITCH_ASSIGNMENT_JOB (
  "id"                      bigserial             not null,
  "assignment_id"           int8                  not null,
  "job_key"                 varchar(200)          not null default '',
  "start_time"              timestamp             not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  "finish_time"             timestamp             not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  "schedule_mode"           smallint              not null default 0,
  "status"                  smallint              not null default 0,
  "error_log"               text                                    ,
  "create_time"     timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  "update_time"     timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  primary key ("id"),
  foreign key ("assignment_id") references DBSWITCH_ASSIGNMENT_TASK ("id") on delete cascade on update cascade
);

COMMENT ON TABLE DBSWITCH_ASSIGNMENT_JOB IS 'JOB日志表';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."id" IS '主键';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."assignment_id" IS '任务ID';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."job_key" IS 'Quartz的Job名';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."start_time" IS '执行开始时间';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."finish_time" IS '执行结束时间';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."schedule_mode" IS '调度模式';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."status" IS '执行状态:0-未执行;1-执行中;2-执行失败;3-执行成功';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."error_log" IS '异常日志';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."create_time" IS '创建时间';
COMMENT ON COLUMN DBSWITCH_ASSIGNMENT_JOB."update_time" IS '修改时间';
