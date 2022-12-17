package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.FieldInfo;
import cn.zhuyee.bean.TableInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * <h2>生成Mapper对应的XML文件</h2>
 *
 * <br>
 * Created by zhuye at 2022/12/11 16:25.
 */
public class BuildMapperXml {
  // 定义一个日志对象
  private static final Logger logger = LoggerFactory.getLogger(BuildMapperXml.class);

  /** 通用查询结果列-名称 */
  private static final String BASE_COLUMN_LIST = "base_column_list";
  /** 基础查询条件-名称 */
  private static final String BASE_QUERY_CONDITION = "base_query_condition";
  /** 通用查询条件-名称 */
  private static final String QUERY_CONDITION = "query_condition";

  /**
   * 根据表信息生成对应的Mapper XML
   *
   * @param tableInfo 表基本信息
   */
  public static void execute(TableInfo tableInfo) {
    logger.info("==>开始创建Mapper XML文件：{}", tableInfo.getBeanName() + Constants.SUFFIX_BEAN_MAPPER + ".java");
    File folder = new File(Constants.PATH_MAPPERS_XMLS);
    // 文件目录不存在时则创建
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // 生成这个java实体类
    String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_MAPPER;
    File poFile = new File(folder, className + ".xml");

    // 通过输出流向文件中写入数据
    // [优化] ==> 通过 try-with-resources 方式关闭资源
    try (
        OutputStream outputStream = new FileOutputStream(poFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(outputStreamWriter)
    ) {
      // 开始创建文件
      // start ==> 生成类文件
      // xml 头
      bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      bw.newLine();
      bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\"");
      bw.newLine();
      bw.write("\t\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
      bw.newLine();
      bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPERS + "." + className + "\">");
      bw.newLine();
      bw.newLine();

      // 1.构建映射实体 ==> start
      bw.write("\t<!-- 实体映射 -->");
      bw.newLine();
      String poClass = Constants.PACKAGE_ENTITY_PO + "." + tableInfo.getBeanName();
      bw.write("\t<resultMap id=\"base_result_map\" type=\"" + poClass + "\">");

      // 拿到主键字段
      FieldInfo idField = null;
      Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
      // 遍历拿到唯一索引
      for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
        if ("PRIMARY".equals(entry.getKey())) {
          List<FieldInfo> fieldInfoList = entry.getValue();
          // 唯一索引
          if (fieldInfoList.size() == 1) {
            idField = fieldInfoList.get(0);
            break;
          }
        }
      }
      // 实体属性
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        bw.write("\t\t<!-- " + fieldInfo.getComment() + " -->");
        bw.newLine();
        String key = "";
        if (idField != null && fieldInfo.getPropertyName().equals(idField.getPropertyName())) {
          key = "id";
        } else {
          key = "result";
        }
        bw.write("\t\t<" + key + " column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\"/>");
        bw.newLine();
      }
      bw.write("\t</resultMap>");
      bw.newLine();
      bw.newLine();
      // 1.构建映射实体 ==> end

      // 2.构建通用查询结果列 ==> start
      bw.write("\t<!-- 通用查询结果列 -->");
      bw.newLine();
      bw.write("\t<sql id=\"" + BASE_COLUMN_LIST + "\">");
      bw.newLine();
      // SQL 内容
      StringBuilder columnBuilder = new StringBuilder();
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        columnBuilder.append(fieldInfo.getFieldName()).append(", ");
      }
      String columnBuilderStr = columnBuilder.substring(0, columnBuilder.lastIndexOf(", "));
      bw.write("\t\t" + columnBuilderStr);
      bw.newLine();
      bw.write("\t</sql>");
      bw.newLine();
      bw.newLine();
      // 2.构建通用查询结果列 ==> end

      // 3.构建基础查询条件 ==> start
      bw.write("\t<!-- 基础查询条件 -->");
      bw.newLine();
      bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION + "\">");
      bw.newLine();
      // 内容
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        String stringQuery = "";
        if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
          // String 类型加上空字符串的判断
          stringQuery = " and query." + fieldInfo.getPropertyName() + " != ''";
        }
        bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null" + stringQuery +"\">");
        bw.newLine();
        bw.write("\t\t\t and " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
        bw.newLine();
        bw.write("\t\t</if>");
        bw.newLine();
      }
      bw.write("\t</sql>");
      bw.newLine();
      bw.newLine();
      // 3.构建基础查询条件 ==> end

      bw.write("</mapper>");
      // end ==> 生成类文件

      bw.flush();
    } catch (Exception e) {
      logger.error("==> 创建Mapper XML失败！", e);
    }

    logger.info("==>结束创建Mapper XML文件");
  }
}
