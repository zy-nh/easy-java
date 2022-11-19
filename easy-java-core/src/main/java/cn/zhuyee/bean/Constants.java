package cn.zhuyee.bean;

import cn.zhuyee.utils.PropertiesUtils;

/**
 * <h2>全局常量类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/14 21:58.
 */
public class Constants {
  // 作者名称
  public static String AUTHOR_COMMENT;

  // 需要忽略的属性
  public static String IGNORE_BEAN_2JSON_FIELD;
  public static String ignore_bean_2json_expression;
  public static String IGNORE_BEAN_2JSON_CLASS;

  // 日期时间序列化、反序列化
  public static String BEAN_DATE_FORMAT_EXPRESSION;
  public static String BEAN_DATE_FORMAT_CLASS;

  public static String BEAN_DATE_UNFORMAT_EXPRESSION;
  public static String BEAN_DATE_UNFORMAT_CLASS;

  // 是否忽略表前缀
  public static Boolean IGNORE_TABLE_PREFIX;
  // 参数 bean 后缀
  public static String PARAM_BEAN_SUFFIX;

  /** 输出文件的基础路径（绝对） */
  public static String PATH_BASE;
  // java 代码路径
  public static String PATH_JAVA = "java";
  // resources 资源路径
  public static String PATH_RESOURCES = "resources";

  /** 基础包路径（目录） */
  public static String PACKAGE_BASE;
  /** 包路径的绝对路径 */
  public static String PACKAGE_ABSOLUTE_PATH;

  /** PO实体的包路径（相对目录）=基础包路径+PO实体包 */
  public static String PACKAGE_ENTITY_PO;
  /** PO实体的绝对包路径 */
  public static String PO_ABSOLUTE_PATH;

  // 查询实体的包名
  public static String PACAAGE_ENTITY_PARAM;

  static{
    AUTHOR_COMMENT = PropertiesUtils.getString("author.comment");

    // 需要忽略的属性
    IGNORE_BEAN_2JSON_FIELD = PropertiesUtils.getString("ignore.bean.2json.field");
    ignore_bean_2json_expression = PropertiesUtils.getString("ignore.bean.2json.expression");
    IGNORE_BEAN_2JSON_CLASS = PropertiesUtils.getString("ignore.bean.2json.class");

    // 日期时间序列化、反序列化
    BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
    BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");

    BEAN_DATE_UNFORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.unformat.expression");
    BEAN_DATE_UNFORMAT_CLASS = PropertiesUtils.getString("bean.date.unformat.class");

    // 是否忽略表前缀
    IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
    PARAM_BEAN_SUFFIX = PropertiesUtils.getString("param.bean.suffix");

    // 文件
    PATH_BASE = PropertiesUtils.getString("path.base");

    // 包
    PACKAGE_BASE = PropertiesUtils.getString("package.base");
    PACKAGE_ABSOLUTE_PATH = PATH_BASE + PATH_JAVA + "/" + PACKAGE_BASE.replace(".", "/");

    // PO
    PACKAGE_ENTITY_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.entity.po");
    PO_ABSOLUTE_PATH = PACKAGE_ABSOLUTE_PATH + "/" + PropertiesUtils.getString("package.entity.po").replace(".", "/");
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

