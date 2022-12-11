package cn.zhuyee.bean;

import cn.zhuyee.utils.PropertiesUtils;

/**
 * <h2>全局常量类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/14 21:58.
 */
public class Constants {
  /** 作者名称 */
  public static String AUTHOR_COMMENT;

  /** 需要忽略的属性[多个用逗号分隔] */
  public static String IGNORE_BEAN_2JSON_FIELD;
  /** 需要忽略的属性[属性上添加的注解] */
  public static String IGNORE_BEAN_2JSON_EXPRESSION;
  /** 需要忽略的属性[注解类导包] */
  public static String IGNORE_BEAN_2JSON_CLASS;

  /** 日期时间序列化[属性上添加的注解及表达式] */
  public static String BEAN_DATE_FORMAT_EXPRESSION;
  /** 日期时间序列化[注解类导包] */
  public static String BEAN_DATE_FORMAT_CLASS;

  /** 日期时间反序列化[属性上添加的注解及表达式] */
  public static String BEAN_DATE_UNFORMAT_EXPRESSION;
  /** 日期时间反序列化[注解类导包] */
  public static String BEAN_DATE_UNFORMAT_CLASS;

  /** 是否忽略表前缀 */
  public static Boolean IGNORE_TABLE_PREFIX;
  /** 参数 bean 后缀 */
  public static String SUFFIX_BEAN_QUERY;
  /** 查询参数-模糊搜索 */
  public static String SUFFIX_BEAN_QUERY_FUZZY;
  /** 查询参数-时间搜索开始 */
  public static String SUFFIX_BEAN_QUERY_TIME_START;
  /** 查询参数-时间搜索结束 */
  public static String SUFFIX_BEAN_QUERY_TIME_END;

  /** mapper后缀 */
  public static String SUFFIX_BEAN_MAPPER;

  /** 输出文件的基础路径（绝对） */
  public static String PATH_BASE;
  // java 代码路径名
  public static String PATH_JAVA = "java";
  /** java 包绝对路径 */
  public static String PATH_ABSOLUTE_JAVA;

  // resources 资源路径
  public static String PATH_RESOURCES = "resources";
  /** resources 资源绝对路径 */
  public static String PATH_ABSOLUTE_RESOURCES;

  /** 基础包路径（目录） */
  public static String PACKAGE_BASE;
  /** 包路径的绝对路径 */
  public static String PACKAGE_ABSOLUTE_PATH;

  /** PO实体的包路径（相对目录）=基础包路径+PO实体包 */
  public static String PACKAGE_ENTITY_PO;
  /** PO实体的绝对包路径 */
  public static String PO_ABSOLUTE_PATH;

  /** 查询实体的包名 */
  public static String PACAAGE_ENTITY_QUERY;
  /** 查询实体的包路径 */
  public static String QUERY_ABSOLUTE_PATH;

  /** 工具类包 */
  public static String PACKAGE_UTILS;
  /** 工具类包路径 */
  public static String PACKAGE_UTILS_PATH;

  /** 枚举类包 */
  public static String PACKAGE_ENUMS;
  /** 枚举类包路径 */
  public static String PACKAGE_ENUMS_PATH;

  /** mapper */
  public static String PACKAGE_MAPPERS;
  /** mapper包路径 */
  public static String PACKAGE_MAPPERS_PATH;

  static{
    AUTHOR_COMMENT = PropertiesUtils.getString("author.comment");

    // ==> 需要忽略的属性
    IGNORE_BEAN_2JSON_FIELD = PropertiesUtils.getString("ignore.bean.2json.field");
    IGNORE_BEAN_2JSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.2json.expression");
    IGNORE_BEAN_2JSON_CLASS = PropertiesUtils.getString("ignore.bean.2json.class");

    // ==> 日期时间序列化、反序列化
    BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
    BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");

    BEAN_DATE_UNFORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.unformat.expression");
    BEAN_DATE_UNFORMAT_CLASS = PropertiesUtils.getString("bean.date.unformat.class");

    // ==> 是否忽略表前缀
    IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
    SUFFIX_BEAN_QUERY = PropertiesUtils.getString("suffix.bean.query");
    // 查询参数
    SUFFIX_BEAN_QUERY_FUZZY = PropertiesUtils.getString("suffix.bean.query.fuzzy");
    SUFFIX_BEAN_QUERY_TIME_START = PropertiesUtils.getString("suffix.bean.query.time.start");
    SUFFIX_BEAN_QUERY_TIME_END = PropertiesUtils.getString("suffix.bean.query.time.end");

    // mapper后缀
    SUFFIX_BEAN_MAPPER = PropertiesUtils.getString("suffix.bean.mapper");

    // ==> 文件
    PATH_BASE = PropertiesUtils.getString("path.base");
    // ==> java 代码资源绝对路径
    PATH_ABSOLUTE_JAVA = PATH_BASE + PATH_JAVA;
    // ==> resources 资源绝对路径
    PATH_ABSOLUTE_RESOURCES = PATH_BASE + PATH_RESOURCES;

    // ==> 包
    PACKAGE_BASE = PropertiesUtils.getString("package.base");
    PACKAGE_ABSOLUTE_PATH = PATH_ABSOLUTE_JAVA + "/" + PACKAGE_BASE.replace(".", "/");

    // ==> PO
    PACKAGE_ENTITY_PO = PACKAGE_BASE + "." + PropertiesUtils.getString("package.entity.po");
    PO_ABSOLUTE_PATH = PATH_ABSOLUTE_JAVA + "/" + PACKAGE_ENTITY_PO.replace(".", "/");

    // ==> QUERY
    PACAAGE_ENTITY_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getString("package.entity.query");
    QUERY_ABSOLUTE_PATH = PATH_ABSOLUTE_JAVA + "/" + PACAAGE_ENTITY_QUERY.replace(".", "/");

    // ==> UTILS
    PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.utils");
    PACKAGE_UTILS_PATH = PATH_ABSOLUTE_JAVA + "/" + PACKAGE_UTILS.replace(".", "/");

    // ==> ENUMS
    PACKAGE_ENUMS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.enums");
    PACKAGE_ENUMS_PATH = PATH_ABSOLUTE_JAVA + "/" + PACKAGE_ENUMS.replace(".", "/");

    // ==> MAPPERS
    PACKAGE_MAPPERS = PACKAGE_BASE + "." + PropertiesUtils.getString("package.mappers");
    PACKAGE_MAPPERS_PATH = PATH_ABSOLUTE_JAVA + "/" + PACKAGE_MAPPERS.replace(".", "/");
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

