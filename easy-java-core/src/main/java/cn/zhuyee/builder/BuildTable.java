package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.FieldInfo;
import cn.zhuyee.bean.TableInfo;
import cn.zhuyee.utils.PropertiesUtils;
import cn.zhuyee.utils.StrUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <h2>根据JDBC读取表信息</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/13 23:58.
 */
public class BuildTable {
  // 定义一个日志对象
  private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
  private static Connection conn = null;

  // 拿到表基本信息
  private static final String SQL_SHOW_TABLE_STATUS = "show table status";
  // 拿到表字段信息
  private static final String SQL_SHOW_TABLE_FIELDS = "show full fields from %s";
  // 拿到表索引信息
  private static final String SQL_SHOW_TABLE_INDEX = "show index from %s";

  // 初始化类时获取数据库连接
  static {
    String driverName = PropertiesUtils.getString("db.driver.name");
    String url = PropertiesUtils.getString("db.url");
    String username = PropertiesUtils.getString("db.username");
    String password = PropertiesUtils.getString("db.password");
    try {
      Class.forName(driverName);
      conn = DriverManager.getConnection(url, username, password);
    } catch (Exception e) {
      logger.error("数据库连接失败",e);
    }
  }

  /**
   * 读取表信息：表名和表描述
   *
   * @return 表信息
   */
  public static List<TableInfo> getTables() {
    PreparedStatement ps = null;
    ResultSet tableResult = null;
    List<TableInfo> tableInfoList = new ArrayList();
    try {
      // 通过连接来调用执行器执行SQL，并返回结果
      ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
      tableResult = ps.executeQuery();
      while (tableResult.next()) {
        String tableName = tableResult.getString("name");
        String tableComment = tableResult.getString("comment");

        String beanName = tableName;
        // 忽略表名前缀
        if (Constants.IGNORE_TABLE_PREFIX) {
          beanName = tableName.substring(beanName.indexOf("_") + 1);
        }
        beanName = camelField(beanName, true);

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setBeanName(beanName);
        tableInfo.setComment(tableComment);
        tableInfo.setBeanParamName(beanName + Constants.PARAM_BEAN_SUFFIX);
        readFieldInfo(tableInfo);
        getKeyIndexInfo(tableInfo);
        //logger.info("表信息:{}", JsonUtils.convertObj2Json(tableInfo));
        //logger.info("字段:{}", JsonUtils.convertObj2Json(fieldInfoList));
        tableInfoList.add(tableInfo);
      }
    } catch (Exception e) {
      logger.error("读取表失败",e);
    } finally {
      if (tableResult != null) {
        try {
          tableResult.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (ps != null) {
        try {
          ps.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return tableInfoList;
  }

  /**
   * 读取表字段信息，并将表字段信息集合塞进表对象中
   *
   * @param tableInfo 表
   */
  private static void readFieldInfo(TableInfo tableInfo) {
    PreparedStatement ps = null;
    ResultSet fieldResult = null;

    // 返回的字段集合
    List<FieldInfo> fieldInfoList = new ArrayList();
    try {
      // 通过连接来调用执行器执行SQL，并返回结果
      ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableInfo.getTableName()));
      fieldResult = ps.executeQuery();
      while (fieldResult.next()) {
        String field = fieldResult.getString("field");
        String type = fieldResult.getString("type");
        String extra = fieldResult.getString("extra");
        String comment = fieldResult.getString("comment");

        // 类型去掉括号后面的，只保留前面单词
        if (type.indexOf("(") > 0) {
          // 用 ( 分割，取前面的
          type = type.substring(0, type.indexOf("("));
        }

        // 字段名称小驼峰命名
        String propertyName = camelField(field, false);

        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setFieldName(field);
        fieldInfo.setPropertyName(propertyName);
        fieldInfo.setSqlType(type);
        fieldInfo.setJavaType(processJavaType(type));
        fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra));
        fieldInfo.setComment(comment);

        // 判断是否有日期时间类型：有就设置为true，否则else
        //tableInfo.setHaveDateTime(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type));
        // 判断是否有时间类型：有就设置为true，否则else
        //tableInfo.setHaveDate(ArrayUtils.contains(Constants.SQL_DATE_TYPES, type));
        // 判断是否有BigDecimal类型：有就设置为true，否则else
        //tableInfo.setHaveBigDecimal(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type));

        // 判断是否有日期时间类型
        if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
          tableInfo.setHaveDateTime(true);
        }
        // 判断是否有时间类型
        if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
          tableInfo.setHaveDate(true);
        }
        // 判断是否有BigDecimal类型
        if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
          tableInfo.setHaveBigDecimal(true);
        }
        fieldInfoList.add(fieldInfo);
      }
      // 将拿到的表字段信息塞进表对象中
      tableInfo.setFieldList(fieldInfoList);

    } catch (Exception e) {
      logger.error("读取表字段失败",e);
    } finally {
      if (fieldResult != null) {
        try {
          fieldResult.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (ps != null) {
        try {
          ps.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static List<FieldInfo> getKeyIndexInfo(TableInfo tableInfo) {
    PreparedStatement ps = null;
    ResultSet fieldResult = null;

    // 返回的字段集合
    List<FieldInfo> fieldInfoList = new ArrayList();
    try {
      // 缓存map
      Map<String, FieldInfo> tempMap = new HashMap();
      // 循环一次，将字段名作为key，字段对象作为值进行存储，下面取到索引后，通过字段来取对应的字段对象
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        tempMap.put(fieldInfo.getFieldName(), fieldInfo);
      }
      // 通过连接来调用执行器执行SQL，并返回结果
      ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX, tableInfo.getTableName()));
      fieldResult = ps.executeQuery();
      while (fieldResult.next()) {
        String keyName = fieldResult.getString("key_name");
        // non_unique 值为0时，表示唯一索引
        int nonUnique = fieldResult.getInt("non_unique");
        String columnName = fieldResult.getString("column_name");

        if (nonUnique == 1) {
          continue;
        }

        // 拿到索引字段集合，可能为空
        List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
        // 读取表的索引字段，若为空，则新建一个，并put到map中
        if (null == keyFieldList) {
          keyFieldList = new ArrayList();
          tableInfo.getKeyIndexMap().put(keyName,keyFieldList);
        }
        /*for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
          if (fieldInfo.getFieldName().equals(columnName)) {
            keyFieldList.add(fieldInfo);
          }
        }*/
        // [优化] 这样来取到索引字段后，通过缓存Map来取到对应的字段对象进行add到list中，减少循环
        keyFieldList.add(tempMap.get(columnName));
      }
    } catch (Exception e) {
      logger.error("读取表索引失败",e);
    } finally {
      if (fieldResult != null) {
        try {
          fieldResult.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (ps != null) {
        try {
          ps.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return fieldInfoList;
  }

  /**
   * 字段驼峰处理
   *
   * @param field 字段
   * @param upperCamelFirstLetter 是否大驼峰处理
   * @return 处理好的字段
   */
  private static String camelField(String field, Boolean upperCamelFirstLetter) {
    StringBuffer stringBuffer = new StringBuffer();
    String[] fields = field.split("_");
    // 首字母大写处理
    stringBuffer.append(upperCamelFirstLetter ? StrUtils.upperCaseFirstLetter(fields[0]) : fields[0]);
    // 遍历将每个字段大写后进行拼接
    for (int i = 1, len = fields.length; i < len; i++) {
      stringBuffer.append(StrUtils.upperCaseFirstLetter(fields[i]));
    }
    return stringBuffer.toString();
  }

  /**
   * SQL字段匹配Java类型
   *
   * @param type SQL字段类型
   * @return 返回匹配的Java属性类型
   */
  private static String processJavaType(String type) {
    if (ArrayUtils.contains(Constants.SQL_INTEGER_TYPES, type)) {
      return "Integer";
    } else if (ArrayUtils.contains(Constants.SQL_LONG_TYPES, type)) {
      return "Long";
    } else if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, type)) {
      return "String";
    } else if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
      return "Date";
    } else if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
      return "BigDecimal";
    } else {
      throw new RuntimeException("无法识别的类型：" + type);
    }
  }
}
