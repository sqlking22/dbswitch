package com.gitee.dbswitch.dbwriter.util;

import com.gitee.dbswitch.common.util.TypeConvertUtils;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public final class ObjectCastUtils {

  private ObjectCastUtils() {
    throw new IllegalStateException("Utility class can not create instance!");
  }

  /**
   * 将java.sql.Clob类型转换为java.lang.String类型
   *
   * @param clob java.sql.Clob类型对象
   * @return java.lang.String类型数据
   */
  public static String clob2Str(java.sql.Clob clob) {
    return TypeConvertUtils.clob2Str(clob);
  }

  /**
   * 将java.sql.Blob类型转换为byte数组
   *
   * @param blob java.sql.Blob类型对象
   * @return byte数组
   */
  public static byte[] blob2Bytes(java.sql.Blob blob) {
    return TypeConvertUtils.blob2Bytes(blob);
  }

  /**
   * 将Object对象转换为字节数组
   *
   * @param in 对象
   * @return 字节数组
   */
  public static byte[] toByteArray(Object in) {
    return TypeConvertUtils.castToByteArray(in);
  }

  /**
   * 将任意类型转换为java.lang.String类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.String类型
   */
  public static String castToString(final Object in) {
    return TypeConvertUtils.castToString(in);
  }

  /**
   * 将任意类型转换为java.lang.Byte类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Byte类型
   */
  public static Byte castToByte(final Object in) {
    if (in instanceof java.lang.Number) {
      return ((java.lang.Number) in).byteValue();
    } else if (in instanceof java.util.Date) {
      return Long.valueOf(((java.util.Date) in).getTime()).byteValue();
    } else if (in instanceof java.lang.String) {
      try {
        return Byte.parseByte(in.toString());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.lang.Byte类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Character) {
      try {
        return Byte.parseByte(in.toString());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.Character类型转换为java.lang.Byte类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        return Byte.parseByte(clob2Str((java.sql.Clob) in));
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.lang.Byte类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Boolean) {
      return (java.lang.Boolean) in ? (byte) 1 : (byte) 0;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Short类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Short类型
   */
  public static Short castToShort(final Object in) {
    if (in instanceof java.lang.Number) {
      return ((java.lang.Number) in).shortValue();
    } else if (in instanceof java.lang.Byte) {
      return (short) (((byte) in) & 0xff);
    } else if (in instanceof java.util.Date) {
      return (short) ((java.util.Date) in).getTime();
    } else if (in instanceof java.util.Calendar) {
      return (short) ((java.util.Calendar) in).getTime().getTime();
    } else if (in instanceof java.time.LocalDateTime) {
      return (short) java.sql.Timestamp.valueOf((java.time.LocalDateTime) in).getTime();
    } else if (in instanceof java.time.OffsetDateTime) {
      return (short) java.sql.Timestamp.valueOf(((java.time.OffsetDateTime) in).toLocalDateTime())
          .getTime();
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Short.valueOf((short) 1);
        } else if (s.equalsIgnoreCase("false")) {
          return Short.valueOf((short) 0);
        } else {
          return Short.parseShort(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.lang.Short类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        String s = clob2Str((java.sql.Clob) in).trim();
        if (s.equalsIgnoreCase("true")) {
          return Short.valueOf((short) 1);
        } else if (s.equalsIgnoreCase("false")) {
          return Short.valueOf((short) 0);
        } else {
          return Short.parseShort(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.lang.Short类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Boolean) {
      return (java.lang.Boolean) in ? (short) 1 : (short) 0;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Integer类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Integer类型
   */
  public static Integer castToInteger(final Object in) {
    if (in instanceof java.lang.Number) {
      return ((java.lang.Number) in).intValue();
    } else if (in instanceof java.lang.Byte) {
      return (((byte) in) & 0xff);
    } else if (in instanceof java.util.Date) {
      return (int) ((java.util.Date) in).getTime();
    } else if (in instanceof java.util.Calendar) {
      return (int) ((java.util.Calendar) in).getTime().getTime();
    } else if (in instanceof java.time.LocalDateTime) {
      return (int) java.sql.Timestamp.valueOf((java.time.LocalDateTime) in).getTime();
    } else if (in instanceof java.time.OffsetDateTime) {
      return (int) java.sql.Timestamp.valueOf(((java.time.OffsetDateTime) in).toLocalDateTime())
          .getTime();
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Integer.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Integer.valueOf(0);
        } else {
          return Integer.parseInt(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.lang.Integer类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        String s = clob2Str((java.sql.Clob) in).trim();
        if (s.equalsIgnoreCase("true")) {
          return Integer.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Integer.valueOf(0);
        } else {
          return Integer.parseInt(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.lang.Integer类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Boolean) {
      return (java.lang.Boolean) in ? (int) 1 : (int) 0;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Long类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Long类型
   */
  public static Long castToLong(final Object in) {
    if (in instanceof java.lang.Number) {
      return ((java.lang.Number) in).longValue();
    } else if (in instanceof java.lang.Byte) {
      return (long) (((byte) in) & 0xff);
    } else if (in instanceof java.util.Date) {
      return ((java.util.Date) in).getTime();
    } else if (in instanceof java.util.Calendar) {
      return ((java.util.Calendar) in).getTime().getTime();
    } else if (in instanceof java.time.LocalDateTime) {
      return java.sql.Timestamp.valueOf((java.time.LocalDateTime) in).getTime();
    } else if (in instanceof java.time.OffsetDateTime) {
      return java.sql.Timestamp.valueOf(((java.time.OffsetDateTime) in).toLocalDateTime())
          .getTime();
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Long.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Long.valueOf(0);
        } else {
          return Long.parseLong(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.lang.Long类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        String s = clob2Str((java.sql.Clob) in).trim();
        if (s.equalsIgnoreCase("true")) {
          return Long.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Long.valueOf(0);
        } else {
          return Long.parseLong(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.lang.Long类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Boolean) {
      return (java.lang.Boolean) in ? (long) 1 : (long) 0;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Number类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Number类型
   */
  public static Number castToNumeric(final Object in) {
    if (in instanceof java.lang.Number) {
      return (java.lang.Number) in;
    } else if (in instanceof java.util.Date) {
      return ((java.util.Date) in).getTime();
    } else if (in instanceof java.util.Calendar) {
      return ((java.util.Calendar) in).getTime().getTime();
    } else if (in instanceof java.time.LocalDateTime) {
      return java.sql.Timestamp.valueOf((java.time.LocalDateTime) in).getTime();
    } else if (in instanceof java.time.OffsetDateTime) {
      return java.sql.Timestamp.valueOf(((java.time.OffsetDateTime) in).toLocalDateTime())
          .getTime();
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Integer.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Integer.valueOf(0);
        } else {
          return new java.math.BigDecimal(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.lang.Number类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        String s = clob2Str((java.sql.Clob) in).trim();
        if (s.equalsIgnoreCase("true")) {
          return Integer.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Integer.valueOf(0);
        } else {
          return new java.math.BigDecimal(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.lang.Number类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Boolean) {
      return (java.lang.Boolean) in ? (long) 1 : (long) 0;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Float类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Float类型
   */
  public static Float castToFloat(final Object in) {
    if (in instanceof java.lang.Number) {
      return ((java.lang.Number) in).floatValue();
    } else if (in instanceof java.util.Date) {
      return (float) ((java.util.Date) in).getTime();
    } else if (in instanceof java.util.Calendar) {
      return (float) ((java.util.Calendar) in).getTime().getTime();
    } else if (in instanceof java.time.LocalDateTime) {
      return (float) java.sql.Timestamp.valueOf((java.time.LocalDateTime) in).getTime();
    } else if (in instanceof java.time.OffsetDateTime) {
      return (float) java.sql.Timestamp.valueOf(((java.time.OffsetDateTime) in).toLocalDateTime())
          .getTime();
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Float.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Float.valueOf(0);
        } else {
          return Float.parseFloat(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.lang.Float类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        String s = clob2Str((java.sql.Clob) in).trim();
        if (s.equalsIgnoreCase("true")) {
          return Float.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Float.valueOf(0);
        } else {
          return Float.parseFloat(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.lang.Float类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Boolean) {
      return (java.lang.Boolean) in ? 1f : 0f;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.lang.Double类型
   *
   * @param in 任意类型的对象实例
   * @return java.lang.Double类型
   */
  public static Double castToDouble(final Object in) {
    if (in instanceof java.lang.Number) {
      return ((java.lang.Number) in).doubleValue();
    } else if (in instanceof java.util.Date) {
      return (double) ((java.util.Date) in).getTime();
    } else if (in instanceof java.util.Calendar) {
      return (double) ((java.util.Calendar) in).getTime().getTime();
    } else if (in instanceof java.time.LocalDateTime) {
      return (double) java.sql.Timestamp.valueOf((java.time.LocalDateTime) in).getTime();
    } else if (in instanceof java.time.OffsetDateTime) {
      return (double) java.sql.Timestamp.valueOf(((java.time.OffsetDateTime) in).toLocalDateTime())
          .getTime();
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        String s = in.toString().trim();
        if (s.equalsIgnoreCase("true")) {
          return Double.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Double.valueOf(0);
        } else {
          return Double.parseDouble(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将将java.lang.String类型转换为java.lang.Double类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        String s = clob2Str((java.sql.Clob) in).trim();
        if (s.equalsIgnoreCase("true")) {
          return Double.valueOf(1);
        } else if (s.equalsIgnoreCase("false")) {
          return Double.valueOf(0);
        } else {
          return Double.parseDouble(s);
        }
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.lang.Double类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Boolean) {
      return (java.lang.Boolean) in ? 1d : 0d;
    }

    return null;
  }

  /**
   * 将任意类型转换为java.time.LocalDate类型
   *
   * @param in 任意类型的对象实例
   * @return java.time.LocalDate类型
   */
  public static LocalDate castToLocalDate(final Object in) {
    if (in instanceof java.sql.Time) {
      java.sql.Time date = (java.sql.Time) in;
      LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
          .toLocalDate();
      return localDate;
    } else if (in instanceof java.sql.Timestamp) {
      java.sql.Timestamp t = (java.sql.Timestamp) in;
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime.toLocalDate();
    } else if (in instanceof java.util.Date) {
      java.util.Date date = (java.util.Date) in;
      LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
          .toLocalDate();
      return localDate;
    } else if (in instanceof java.util.Calendar) {
      java.sql.Date date = new java.sql.Date(((java.util.Calendar) in).getTime().getTime());
      LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
          .toLocalDate();
      return localDate;
    } else if (in instanceof java.time.LocalDate) {
      return (java.time.LocalDate) in;
    } else if (in instanceof java.time.LocalTime) {
      return java.time.LocalDate.MIN;
    } else if (in instanceof java.time.LocalDateTime) {
      return ((java.time.LocalDateTime) in).toLocalDate();
    } else if (in instanceof java.time.OffsetDateTime) {
      return ((java.time.OffsetDateTime) in).toLocalDate();
    } else if (in.getClass().getName().equals("oracle.sql.TIMESTAMP")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("timestampValue");
        java.sql.Timestamp date = (java.sql.Timestamp) m.invoke(in);
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("getTimestamp");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime.toLocalDate();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        java.sql.Time date = java.sql.Time.valueOf(in.toString());
        LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
            .toLocalDate();
        return localDate;
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.sql.Time类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        java.sql.Time date = java.sql.Time.valueOf(clob2Str((java.sql.Clob) in));
        LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
            .toLocalDate();
        return localDate;
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.sql.Time类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Number) {
      java.sql.Timestamp t = new java.sql.Timestamp(((java.lang.Number) in).longValue());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime.toLocalDate();
    }

    return null;
  }

  /**
   * 将任意类型转换为java.time.LocalTime类型
   *
   * @param in 任意类型的对象实例
   * @return java.time.LocalDate类型
   */
  public static LocalTime castToLocalTime(final Object in) {
    if (in instanceof java.sql.Time) {
      java.sql.Time date = (java.sql.Time) in;
      LocalTime localTime = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault())
          .toLocalTime();
      return localTime;
    } else if (in instanceof java.sql.Timestamp) {
      java.sql.Timestamp t = (java.sql.Timestamp) in;
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime.toLocalTime();
    } else if (in instanceof java.util.Date) {
      return LocalTime.of(0, 0, 0);
    } else if (in instanceof java.util.Calendar) {
      java.sql.Date date = new java.sql.Date(((java.util.Calendar) in).getTime().getTime());
      LocalDateTime localDateTime = Instant.ofEpochMilli(date.getTime())
          .atZone(ZoneId.systemDefault())
          .toLocalDateTime();
      return localDateTime.toLocalTime();
    } else if (in instanceof java.time.LocalDate) {
      return LocalTime.of(0, 0, 0);
    } else if (in instanceof java.time.LocalTime) {
      return (java.time.LocalTime) in;
    } else if (in instanceof java.time.LocalDateTime) {
      return ((java.time.LocalDateTime) in).toLocalTime();
    } else if (in instanceof java.time.OffsetDateTime) {
      return ((java.time.OffsetDateTime) in).toLocalTime();
    } else if (in.getClass().getName().equals("oracle.sql.TIMESTAMP")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("timestampValue");
        java.sql.Timestamp date = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        return localDateTime.toLocalTime();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("getTimestamp");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime.toLocalTime();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        java.sql.Time date = java.sql.Time.valueOf(in.toString());
        return LocalTime.ofSecondOfDay(date.getTime());
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.sql.Time类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        java.sql.Time date = java.sql.Time.valueOf(clob2Str((java.sql.Clob) in));
        return LocalTime.ofSecondOfDay(date.getTime());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.sql.Time类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Number) {
      java.sql.Timestamp t = new java.sql.Timestamp(((java.lang.Number) in).longValue());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime.toLocalTime();
    }

    return null;
  }

  /**
   * 将任意类型转换为java.time.LocalDateTime类型
   *
   * @param in 任意类型的对象实例
   * @return java.time.LocalDateTime类型
   */
  public static LocalDateTime castToLocalDateTime(final Object in) {
    if (in instanceof java.sql.Timestamp) {
      java.sql.Timestamp t = (java.sql.Timestamp) in;
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    } else if (in instanceof java.sql.Date) {
      java.sql.Date date = (java.sql.Date) in;
      LocalDate localDate = date.toLocalDate();
      LocalTime localTime = LocalTime.of(0, 0, 0);
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      return localDateTime;
    } else if (in instanceof java.sql.Time) {
      java.sql.Time date = (java.sql.Time) in;
      java.sql.Timestamp t = new java.sql.Timestamp(date.getTime());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    } else if (in instanceof java.util.Date) {
      java.sql.Timestamp t = new java.sql.Timestamp(((java.util.Date) in).getTime());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    } else if (in instanceof java.util.Calendar) {
      java.sql.Timestamp t = new java.sql.Timestamp(((java.util.Calendar) in).getTime().getTime());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    } else if (in instanceof java.time.LocalDate) {
      LocalDate localDate = (java.time.LocalDate) in;
      LocalTime localTime = LocalTime.of(0, 0, 0);
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      return localDateTime;
    } else if (in instanceof java.time.LocalTime) {
      LocalDate localDate = java.time.LocalDate.MIN;
      LocalTime localTime = (java.time.LocalTime) in;
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      return localDateTime;
    } else if (in instanceof java.time.LocalDateTime) {
      return (java.time.LocalDateTime) in;
    } else if (in instanceof java.time.OffsetDateTime) {
      return ((java.time.OffsetDateTime) in).toLocalDateTime();
    } else if (in.getClass().getName().equals("oracle.sql.TIMESTAMP")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("timestampValue");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
      Class<?> clz = in.getClass();
      try {
        Method m = clz.getMethod("getTimestamp");
        java.sql.Timestamp t = (java.sql.Timestamp) m.invoke(in);
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        java.sql.Timestamp t = java.sql.Timestamp.valueOf(in.toString());
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime;
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.sql.TimeStamp类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        java.sql.Timestamp t = java.sql.Timestamp.valueOf(clob2Str((java.sql.Clob) in));
        LocalDateTime localDateTime = LocalDateTime
            .ofInstant(t.toInstant(), ZoneId.systemDefault());
        return localDateTime;
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.sql.TimeStamp类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.lang.Number) {
      java.sql.Timestamp t = new java.sql.Timestamp(((java.lang.Number) in).longValue());
      LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
      return localDateTime;
    }

    return null;
  }

  /**
   * 将任意类型转换为byte[]类型
   *
   * @param in 任意类型的对象实例
   * @return byte[]类型
   */
  public static byte[] castToByteArray(final Object in) {
    if (in instanceof byte[]) {
      return (byte[]) in;
    } else if (in instanceof java.util.Date) {
      return in.toString().getBytes();
    } else if (in instanceof java.sql.Blob) {
      return blob2Bytes((java.sql.Blob) in);
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      return in.toString().getBytes();
    } else if (in instanceof java.sql.Clob) {
      return clob2Str((java.sql.Clob) in).toString().getBytes();
    } else {
      return toByteArray(in);
    }
  }

  /**
   * 将任意类型转换为Boolean类型
   *
   * @param in 任意类型的对象实例
   * @return Boolean类型
   */
  public static Boolean castToBoolean(final Object in) {
    if (in instanceof java.lang.Boolean) {
      return (java.lang.Boolean) in;
    } else if (in instanceof java.lang.Number) {
      return ((java.lang.Number) in).intValue() != 0;
    } else if (in instanceof java.lang.String || in instanceof java.lang.Character) {
      try {
        return Boolean.parseBoolean(in.toString());
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(
            String.format("无法将java.lang.String类型转换为java.lang.Boolean类型:%s", e.getMessage()));
      }
    } else if (in instanceof java.sql.Clob) {
      try {
        return Boolean.parseBoolean(clob2Str((java.sql.Clob) in));
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("无法将java.sql.Clob类型转换为java.lang.Boolean类型:%s", e.getMessage()));
      }
    }

    return null;
  }

  public static Object castByDetermine(final Object in) {
    if (null == in) {
      return null;
    }

    if (in instanceof BigInteger) {
      return ((BigInteger) in).longValue();
    } else if (in instanceof BigDecimal) {
      BigDecimal decimal = (BigDecimal) in;
      if (decimal.doubleValue() > 2.147483647E9D || decimal.doubleValue() < -2.147483648E9D) {
        return 0D;
      }
      return decimal.doubleValue();
    } else if (in instanceof java.sql.Clob) {
      return clob2Str((java.sql.Clob) in);
    } else if (in instanceof java.sql.Array
        || in instanceof java.sql.SQLXML) {
      try {
        return objectToString(in);
      } catch (Exception e) {
        log.warn("Unsupported type for convert {} to java.lang.String", in.getClass().getName());
        return null;
      }
    } else if (in instanceof java.sql.Blob) {
      try {
        return blob2Bytes((java.sql.Blob) in);
      } catch (Exception e) {
        log.warn("Unsupported type for convert {} to byte[] ", in.getClass().getName());
        return null;
      }
    } else if (in instanceof java.sql.Struct) {
      log.warn("Unsupported type for convert {} to java.lang.String", in.getClass().getName());
      return null;
    }

    return in;
  }

  public static String objectToString(final Object in) {
    String v = in.toString();
    String a = in.getClass().getName() + "@" + Integer.toHexString(in.hashCode());
    if (a.length() == v.length() && StringUtils.equals(a, v)) {
      throw new UnsupportedOperationException("Unsupported convert "
          + in.getClass().getName() + " to java.lang.String");
    }

    return v;
  }

}
