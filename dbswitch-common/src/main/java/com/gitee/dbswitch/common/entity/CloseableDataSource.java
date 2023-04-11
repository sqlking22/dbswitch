package com.gitee.dbswitch.common.entity;

import java.io.Closeable;
import javax.sql.DataSource;

public interface CloseableDataSource extends DataSource, Closeable {

}
