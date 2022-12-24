package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.FieldInfo;
import cn.zhuyee.bean.TableInfo;
import cn.zhuyee.utils.StrUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static cn.zhuyee.builder.BuildComment.createFieldComment;

/**
 * <h2>创建JavaBean 查询对象</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/16 20:55.
 */
public class BuildQuery {
  // 定义一个日志对象
  private static final Logger logger = LoggerFactory.getLogger(BuildQuery.class);

  /**
   * 根据表信息生成对应的实体类
   *
   * @param tableInfo 表基本信息
   */
  public static void execute(TableInfo tableInfo) {
    logger.info("==>开始创建文件：{}", tableInfo.getBeanName() + ".java");
    File folder = new File(Constants.QUERY_ABSOLUTE_PATH);
    // 文件目录不存在时则创建
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // 生成这个java实体类
    String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY;
    File poFile = new File(folder, className + ".java");

    // 通过输出流向文件中写入数据
    // [优化] ==> 通过 try-with-resources 方式关闭资源
    try (
        OutputStream outputStream = new FileOutputStream(poFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)
    ) {
      // 开始创建文件
      // start ==> 生成类文件
      // 1.写入包路径
      bufferedWriter.write("package " + Constants.PACKAGE_ENTITY_QUERY + ";");
      bufferedWriter.newLine();
      bufferedWriter.newLine();

      // 2.写入导包信息
      // 有日期、时间类型就导包
      if (tableInfo.getHaveDateTime() || tableInfo.getHaveDate()) {
        bufferedWriter.write("import java.util.Date;");
        bufferedWriter.newLine();
      }
      // 有BigDecimal类型就导包
      if (tableInfo.getHaveBigDecimal()) {
        bufferedWriter.write("import java.math.BigDecimal;");
        bufferedWriter.newLine();
      }

      // 创建类的注释
      BuildComment.createClassComment(bufferedWriter, tableInfo.getComment() + "查询对象");

      // 3.类定义信息
      bufferedWriter.write("public class " + className + " extends BaseQuery {");
      bufferedWriter.newLine();
      bufferedWriter.newLine();

      // 4.拿到所有的属性
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        createFieldComment(bufferedWriter, fieldInfo.getComment());
        bufferedWriter.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        // String 类型参数模糊查询属性
        if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
          bufferedWriter.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY + ";");
          bufferedWriter.newLine();
        }

        // 日期 类型参数查询 开始|结束 属性
        if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
          bufferedWriter.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START + ";");
          bufferedWriter.newLine();
          bufferedWriter.newLine();

          bufferedWriter.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END + ";");
          bufferedWriter.newLine();
        }
      }

      // 5.给属性生成getter|setter方法
      // 表实体属性的getter和setter
      buildGetSet(bufferedWriter, tableInfo.getFieldList());
      // 扩增属性的getter和setter
      buildGetSet(bufferedWriter, tableInfo.getFieldExtendList());

      bufferedWriter.write("}");
      // end ==> 生成类文件

      bufferedWriter.flush();
    } catch (Exception e) {
      logger.error("==> 创建PO失败！", e);
    }

    logger.info("==>结束创建文件");
  }

  public static void buildGetSet(BufferedWriter bufferedWriter, List<FieldInfo> fieldInfoList) throws Exception{
    for (FieldInfo field : fieldInfoList) {
      // 将属性首字母大写
      String tempField = StrUtils.upperCaseFirstLetter(field.getPropertyName());

      // start ==> getter
      bufferedWriter.write("\tpublic " + field.getJavaType() + " get" + tempField + "() {");
      bufferedWriter.newLine();
      bufferedWriter.write("\t\treturn this." + field.getPropertyName() + ";");
      bufferedWriter.newLine();
      bufferedWriter.write("\t}");
      bufferedWriter.newLine();
      // end ==> getter

      // start ==> setter
      bufferedWriter.write("\tpublic void set" + tempField + "(" + field.getJavaType() + " " + field.getPropertyName() + ") {");
      bufferedWriter.newLine();
      bufferedWriter.write("\t\tthis." + field.getPropertyName() + " = " + field.getPropertyName() + ";");
      bufferedWriter.newLine();
      bufferedWriter.write("\t}");
      bufferedWriter.newLine();
      bufferedWriter.newLine();  // 一组getter|setter隔一行
      // end ==> setter
    }
  }
}
