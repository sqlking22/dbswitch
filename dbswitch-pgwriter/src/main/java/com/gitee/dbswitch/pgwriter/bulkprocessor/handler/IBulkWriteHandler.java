package com.gitee.dbswitch.pgwriter.bulkprocessor.handler;

import java.util.List;

public interface IBulkWriteHandler<TEntity> {

  void write(List<TEntity> entities) throws Exception;

}
