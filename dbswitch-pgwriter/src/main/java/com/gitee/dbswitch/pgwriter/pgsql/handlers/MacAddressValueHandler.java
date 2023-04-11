package com.gitee.dbswitch.pgwriter.pgsql.handlers;

import com.gitee.dbswitch.pgwriter.pgsql.model.network.MacAddress;
import java.io.DataOutputStream;
import java.io.IOException;

public class MacAddressValueHandler extends BaseValueHandler<MacAddress> {

  @Override
  protected void internalHandle(DataOutputStream buffer, final MacAddress value)
      throws IOException {
    buffer.writeInt(6);
    buffer.write(value.getAddressBytes());
  }

  @Override
  public int getLength(MacAddress value) {
    return 6;
  }
}
