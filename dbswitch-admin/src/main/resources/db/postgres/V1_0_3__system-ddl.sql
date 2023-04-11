CREATE TABLE IF NOT EXISTS DBSWITCH_JOB_LOGBACK  (
  "id"                  bigserial             not null,
  "uuid"                varchar(128)          not null default '',
  "content"             text,
  "create_time"         timestamp(6) not null default (CURRENT_TIMESTAMP(0))::timestamp(0) without time zone,
  PRIMARY KEY ("id")
);

CREATE INDEX DBSWITCH_JOB_LOGBACK_UUID_IDX ON DBSWITCH_JOB_LOGBACK("uuid");
COMMENT ON TABLE DBSWITCH_JOB_LOGBACK IS 'JOB执行日志';
COMMENT ON COLUMN DBSWITCH_JOB_LOGBACK."id" IS '主键';
COMMENT ON COLUMN DBSWITCH_JOB_LOGBACK."uuid" IS 'job id';
COMMENT ON COLUMN DBSWITCH_JOB_LOGBACK."content" IS '日志内容';
COMMENT ON COLUMN DBSWITCH_JOB_LOGBACK."create_time" IS '创建时间';
