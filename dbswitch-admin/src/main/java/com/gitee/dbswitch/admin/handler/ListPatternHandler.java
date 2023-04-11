package com.gitee.dbswitch.admin.handler;

import com.gitee.dbswitch.data.util.JsonUtils;
import com.gitee.dbswitch.common.entity.PatternMapper;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class ListPatternHandler extends BaseTypeHandler<List<PatternMapper>> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, List<PatternMapper> list,
      JdbcType jdbcType) throws SQLException {
    ps.setString(i, list2string(list));
  }

  @Override
  public List<PatternMapper> getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    String r = rs.getString(columnName);
    if (rs.wasNull()) {
      return null;
    }
    return string2list(r);

  }

  @Override
  public List<PatternMapper> getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    String r = rs.getString(columnIndex);
    if (rs.wasNull()) {
      return null;
    }
    return string2list(r);

  }

  @Override
  public List<PatternMapper> getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    String r = cs.getString(columnIndex);
    if (cs.wasNull()) {
      return null;
    }
    return string2list(r);
  }

  private String list2string(List<PatternMapper> list) {
    if (list == null || list.isEmpty()) {
      return null;
    }
    return JsonUtils.toJsonString(list);
  }

  private List<PatternMapper> string2list(String str) {
    if (str == null || str.isEmpty()) {
      return new ArrayList<>();
    }
    return JsonUtils.toBeanList(str, PatternMapper.class);
  }
}
