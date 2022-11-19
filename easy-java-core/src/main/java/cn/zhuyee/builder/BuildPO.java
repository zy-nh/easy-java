package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.FieldInfo;
import cn.zhuyee.bean.TableInfo;
import cn.zhuyee.utils.DateUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static cn.zhuyee.builder.BuildComment.createFieldComment;

/**
 * <h2>创建JavaBean对象</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/16 20:55.
 */
public class BuildPO {
  // 定义一个日志对象
  private static final Logger logger = LoggerFactory.getLogger(BuildPO.class);

  /**
   * 根据表信息生成对应的实体类
   *
   * @param tableInfo 表基本信息
   */
  public static void execute(TableInfo tableInfo) {
    logger.info("==>开始创建文件：{}", tableInfo.getBeanName() + ".java");
    File folder = new File(Constants.PO_ABSOLUTE_PATH);
    // 文件目录不存在时则创建
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // 生成这个java实体类
    File poFile = new File(folder, tableInfo.getBeanName() + ".java");

    // 通过输出流向文件中写入数据
    // [优化] ==> 通过 try-with-resources 方式关闭资源
    try (
        OutputStream outputStream = new FileOutputStream(poFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)
    ) {
      // 开始创建文件
      // 1.写入包路径
      bufferedWriter.write("package " + Constants.PACKAGE_ENTITY_PO + ";");
      bufferedWriter.newLine();
      bufferedWriter.newLine();

      // 2.写入导包信息
      bufferedWriter.write("import java.io.Serializable;");
      bufferedWriter.newLine();
      // 有日期、时间类型就导包
      if (tableInfo.getHaveDateTime() || tableInfo.getHaveDate()) {
        bufferedWriter.write("import java.util.Date;");
        bufferedWriter.newLine();
        bufferedWriter.write(Constants.BEAN_DATE_FORMAT_CLASS);
        bufferedWriter.newLine();
        bufferedWriter.write(Constants.BEAN_DATE_UNFORMAT_CLASS);
        bufferedWriter.newLine();
      }
      // 有BigDecimal类型就导包
      if (tableInfo.getHaveBigDecimal()) {
        bufferedWriter.write("import java.math.BigDecimal;");
        bufferedWriter.newLine();
      }
      bufferedWriter.newLine();

      // 创建类的注释
      BuildComment.createClassComment(bufferedWriter, tableInfo.getComment());

      // 3.类定义信息
      bufferedWriter.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
      bufferedWriter.newLine();
      bufferedWriter.newLine();

      // 4.拿到所有的属性
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        createFieldComment(bufferedWriter, fieldInfo.getComment());
        // 日期时间类型
        if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
          bufferedWriter.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
          bufferedWriter.newLine();
          // 反序列化
          bufferedWriter.write("\t" + String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
          bufferedWriter.newLine();
        }
        // 日期类型
        if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
          bufferedWriter.write("\t" + String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
          bufferedWriter.newLine();
          // 反序列化
          bufferedWriter.write("\t" + String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
          bufferedWriter.newLine();
        }

        bufferedWriter.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getFieldName() + ";");
        bufferedWriter.newLine();
        bufferedWriter.newLine();
      }

      bufferedWriter.write("}");

      bufferedWriter.flush();
    } catch (Exception e) {
      logger.error("==> 创建PO失败！", e);
    }

    logger.info("==>结束创建文件");
  }
}
