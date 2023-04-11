package com.gitee.dbswitch.pgwriter.pgsql.handlers;

import com.gitee.dbswitch.pgwriter.pgsql.constants.DataType;

public interface IValueHandlerProvider {

  <TTargetType> IValueHandler<TTargetType> resolve(DataType targetType);
}
