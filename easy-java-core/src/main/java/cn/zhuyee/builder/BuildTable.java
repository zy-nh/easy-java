package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.TableInfo;
import cn.zhuyee.utils.PropertiesUtils;
import cn.zhuyee.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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

  // 拿到表及其注释
  private static final String SQL_SHOW_TABLE_STATUS = "show table status";

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
   */
  public static void getTables() {
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
        logger.info("表:{},备注:{},JavaBean:{},JavaParamBean:{}", tableInfo.getTableName(), tableInfo.getComment(), tableInfo.getBeanName(), tableInfo.getBeanParamName());

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
}
