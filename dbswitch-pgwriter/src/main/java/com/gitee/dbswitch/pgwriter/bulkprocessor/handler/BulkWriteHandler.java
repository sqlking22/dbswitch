package com.gitee.dbswitch.pgwriter.bulkprocessor.handler;

import com.gitee.dbswitch.pgwriter.IPgBulkInsert;
import com.gitee.dbswitch.pgwriter.util.PostgreSqlUtils;
import java.sql.Connection;
import java.util.List;
import java.util.function.Supplier;
import org.postgresql.PGConnection;

public class BulkWriteHandler<TEntity> implements IBulkWriteHandler<TEntity> {

  private final IPgBulkInsert<TEntity> client;

  private final Supplier<Connection> connectionFactory;

  public BulkWriteHandler(IPgBulkInsert<TEntity> client, Supplier<Connection> connectionFactory) {
    this.client = client;
    this.connectionFactory = connectionFactory;
  }

  @Override
  public void write(List<TEntity> entities) throws Exception {
    // Obtain a new Connection and execute it in a try with resources block, so it gets closed properly:
    try (Connection connection = connectionFactory.get()) {
      // Now get the underlying PGConnection for the COPY API wrapping:
      final PGConnection pgConnection = PostgreSqlUtils.getPGConnection(connection);
      // And finally save all entities by using the COPY API:
      client.saveAll(pgConnection, entities.stream());
    }
  }
}
