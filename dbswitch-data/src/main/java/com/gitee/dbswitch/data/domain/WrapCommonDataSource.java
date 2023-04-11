package com.gitee.dbswitch.data.domain;

import com.gitee.dbswitch.common.entity.CloseableDataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WrapCommonDataSource implements CloseableDataSource {

  private DataSource commonDataSource;
  private URLClassLoader urlClassLoader;

  public WrapCommonDataSource(DataSource commonDataSource, URLClassLoader urlClassLoader) {
    this.commonDataSource = Objects.requireNonNull(commonDataSource);
    this.urlClassLoader = Objects.requireNonNull(urlClassLoader);
  }

  @Override
  public Connection getConnection() throws SQLException {
    return commonDataSource.getConnection();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return commonDataSource.getConnection(username, password);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return commonDataSource.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return commonDataSource.isWrapperFor(iface);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return commonDataSource.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    commonDataSource.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    commonDataSource.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return commonDataSource.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return commonDataSource.getParentLogger();
  }

  public void close() {
    try {
      urlClassLoader.close();
    } catch (IOException e) {
      log.warn(e.getMessage(), e);
    }
  }
}
