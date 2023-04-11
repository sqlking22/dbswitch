package com.gitee.dbswitch.pgwriter.pgsql.handlers;

import com.gitee.dbswitch.pgwriter.pgsql.handlers.utils.GeometricUtils;
import com.gitee.dbswitch.pgwriter.pgsql.model.geometric.Point;
import java.io.DataOutputStream;
import java.io.IOException;

public class PointValueHandler extends BaseValueHandler<Point> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final Point value) throws IOException {
    buffer.writeInt(16);

    GeometricUtils.writePoint(buffer, value);
  }

  @Override
  public int getLength(Point value) {
    return 16;
  }
}