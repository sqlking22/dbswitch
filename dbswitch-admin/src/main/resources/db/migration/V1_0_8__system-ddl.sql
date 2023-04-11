ALTER TABLE `DBSWITCH_DATABASE_CONNECTION`
ADD COLUMN `version`  varchar(255) NOT NULL DEFAULT '' COMMENT '驱动版本' AFTER `type`;

update `DBSWITCH_DATABASE_CONNECTION` set `version`='db2-10.1' where `type`='DB2';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='dm-8' where `type`='DM';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='gbase8a-8.6' where `type`='GBASE8A';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='gbase8a-8.6' where `type`='GBASE8A';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='gbase8a-8.6' where `type`='GBASE8A';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='hive-3.1.2' where `type`='HIVE';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='kingbase-v8r6' where `type`='KINGBASE';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='mariadb-2.7' where `type`='MARIADB';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='mysql-8' where `type`='MYSQL';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='oracle-12c' where `type`='ORACLE';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='oscar-8' where `type`='OSCAR';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='postgresql-11.4' where `type`='POSTGRESQL';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='sqlite-3.31' where `type`='SQLITE3';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='sqlserver-7.2' where `type`='SQLSERVER';
update `DBSWITCH_DATABASE_CONNECTION` set `version`='sybase-4' where `type`='SYBASE';
