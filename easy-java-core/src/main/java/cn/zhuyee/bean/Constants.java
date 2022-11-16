package cn.zhuyee.bean;

import cn.zhuyee.utils.PropertiesUtils;

/**
 * <h2>全局常量类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/14 21:58.
 */
public class Constants {
  // 是否忽略表前缀
  public static Boolean IGNORE_TABLE_PREFIX;
  // 参数 bean 后缀
  public static String PARAM_BEAN_SUFFIX;
  static{
    IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
    PARAM_BEAN_SUFFIX = PropertiesUtils.getString("param.bean.suffix");
  }

  // start ==> MySQL数据库关键字归类
  // 日期时间类型
  public final static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
  // 日期类型
  public final static String[] SQL_DATE_TYPES = new String[]{"date"};
  // 金额类型
  public final static String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "double", "float"};
  // 字符串类型
  public final static String[] SQL_STRING_TYPES = new String[]{"char", "varchar", "text", "mediumtext", "longtext"};
  // Integer
  public final static String[] SQL_INTEGER_TYPES = new String[]{"int", "tinyint"};
  // Long
  public final static String[] SQL_LONG_TYPES = new String[]{"bigint"};
  // end ==> MySQL数据库关键字归类
}

