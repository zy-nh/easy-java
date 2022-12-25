package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.FieldInfo;
import cn.zhuyee.bean.TableInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
  /** 扩展查询条件-名称 */
  private static final String BASE_QUERY_CONDITION_EXTEND = "base_query_condition_extend";
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

      // 4.构建扩展的查询条件 ==> start
      bw.write("\t<!-- 扩展查询条件 -->");
      bw.newLine();
      bw.write("\t<sql id=\"" + BASE_QUERY_CONDITION_EXTEND + "\">");
      bw.newLine();
      // 内容
      for (FieldInfo fieldInfo : tableInfo.getFieldExtendList()) {
        String andWhere = "";
        if (ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())) {
          andWhere = "and " + fieldInfo.getFieldName() + " like concat('%', #{query." + fieldInfo.getPropertyName() + "}, '%')";
        } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
          if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)) {
            andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " >= str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d') ]]>";
          } else if (fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)) {
            // 截止日期往后推一天
            andWhere = "<![CDATA[ and " + fieldInfo.getFieldName() + " < date_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d'), interval -1 day) ]]>";
          }
        }
        bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null and query." + fieldInfo.getPropertyName() + " != ''\">");
        bw.newLine();
        bw.write("\t\t\t " + andWhere);
        bw.newLine();
        bw.write("\t\t</if>");
        bw.newLine();
      }
      bw.write("\t</sql>");
      bw.newLine();
      bw.newLine();
      // 4.构建扩展的查询条件 ==> end

      // 5.构建通用的查询条件 ==> start
      bw.write("\t<!-- 通用查询条件 -->");
      bw.newLine();
      bw.write("\t<sql id=\"" + QUERY_CONDITION + "\">");
      bw.newLine();
      // 内容
      bw.write("\t\t<where>");
      bw.newLine();
      bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION + "\"/>");
      bw.newLine();
      bw.write("\t\t\t<include refid=\"" + BASE_QUERY_CONDITION_EXTEND + "\"/>");
      bw.newLine();
      bw.write("\t\t</where>");
      bw.newLine();
      bw.write("\t</sql>");
      bw.newLine();
      bw.newLine();
      // 5.构建通用的查询条件 ==> end

      // 6.构建列表查询 ==> start
      bw.write("\t<!-- 列表集合查询 -->");
      bw.newLine();
      bw.write("\t<select id=\"selectList\" resultMap=\"base_result_map\">");
      bw.newLine();
      // 内容
      bw.write("\t\tSELECT <include refid=\"" + BASE_COLUMN_LIST + "\"/> FROM " + tableInfo.getTableName() + " <include refid=\"" + QUERY_CONDITION + "\"/>");
      bw.newLine();
      bw.write("\t\t<if test=\"query.orderBy != null\">\n\t\t\torder by #{query.orderBy}\n\t\t</if>");
      bw.newLine();
      bw.write("\t\t<if test=\"query.simplePage != null\">\n\t\t\tlimit #{query.simplePage.start}, #{query.simplePage.end}\n\t\t</if>");
      bw.newLine();
      bw.write("\t</select>");
      bw.newLine();
      bw.newLine();
      // 6.构建列表查询 ==> end

      // 7.构建数量查询 ==> start
      bw.write("\t<!-- 查询数量 -->");
      bw.newLine();
      bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Long\">");
      bw.newLine();
      // 内容
      bw.write("\t\tSELECT count(1) FROM " + tableInfo.getTableName() + " <include refid=\"" + QUERY_CONDITION + "\"/>");
      bw.newLine();
      bw.write("\t</select>");
      bw.newLine();
      bw.newLine();
      // 7.构建数量查询 ==> end

      /***********************************
       * 插入语句
       ***********************************/
      // 1.单条插入 ==> start
      bw.write("\t<!-- 单条插入 -->");
      bw.newLine();
      bw.write("\t<insert id=\"insert\" parameterType=\"" + Constants.PACKAGE_ENTITY_PO + "." + tableInfo.getBeanName() + "\">");
      bw.newLine();
      // 拿到自增长的字段
      FieldInfo autoIncrementField = null;
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        if (fieldInfo.getAutoIncrement() != null && fieldInfo.getAutoIncrement()) {
          autoIncrementField = fieldInfo;
          break;
        }
      }
      // 自增字段语句
      if (autoIncrementField != null) {
        bw.write("\t\t<selectKey keyProperty=\"bean." + autoIncrementField.getFieldName() + "\" resultType=\"" + autoIncrementField.getJavaType() + "\" order=\"AFTER\">");
        bw.newLine();
        bw.write("\t\t\tSELECT LAST_INSERT_ID()");
        bw.newLine();
        bw.write("\t\t</selectKey>");
        bw.newLine();
      }
      bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
      bw.newLine();
      // 要插入的字段名
      bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
      bw.newLine();
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
        bw.newLine();
        bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
        bw.newLine();
        bw.write("\t\t\t</if>");
        bw.newLine();
      }
      bw.write("\t\t</trim>");
      bw.newLine();
      // 要插入的值
      bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
      bw.newLine();
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
        bw.newLine();
        bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
        bw.newLine();
        bw.write("\t\t\t</if>");
        bw.newLine();
      }
      bw.write("\t\t</trim>");
      bw.newLine();
      bw.write("\t</insert>");
      bw.newLine();
      bw.newLine();
      // 1.单条插入 ==> end

      // 2.插入或更新 ==> start
      bw.write("\t<!-- 插入或更新（匹配有值的字段） -->");
      bw.newLine();
      bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\"" + Constants.PACKAGE_ENTITY_PO + "." + tableInfo.getBeanName() + "\">");
      bw.newLine();
      bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
      bw.newLine();
      // 要插入的字段名
      bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
      bw.newLine();
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
        bw.newLine();
        bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
        bw.newLine();
        bw.write("\t\t\t</if>");
        bw.newLine();
      }
      bw.write("\t\t</trim>");
      bw.newLine();
      // 要插入的值
      bw.write("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">");
      bw.newLine();
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
        bw.newLine();
        bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
        bw.newLine();
        bw.write("\t\t\t</if>");
        bw.newLine();
      }
      bw.write("\t\t</trim>");
      bw.newLine();
      // 更新操作
      bw.write("\t\ton DUPLICATE key update");
      bw.newLine();
      // 把主键拿出来，不允许更新主键
      Map<String, String> keyTempMap = new HashMap();
      for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
        List<FieldInfo> fieldInfoList = entry.getValue();
        for (FieldInfo item : fieldInfoList) {
          keyTempMap.put(item.getFieldName(), item.getFieldName());
        }
      }
      bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
      bw.newLine();
      for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
        // 若有主键或唯一索引，则跳过不生成对应的更新字段
        if (keyTempMap.get(fieldInfo.getFieldName()) != null) {
          continue;
        }
        bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
        bw.newLine();
        bw.write("\t\t\t\t" + fieldInfo.getFieldName() +" = VALUES("+fieldInfo.getFieldName()+ "),");
        bw.newLine();
        bw.write("\t\t\t</if>");
        bw.newLine();
      }
      bw.write("\t\t</trim>");
      bw.newLine();

      bw.write("\t</insert>");
      bw.newLine();
      bw.newLine();
      // 2.插入或更新 ==> end

      bw.write("</mapper>");
      // end ==> 生成类文件

      bw.flush();
    } catch (Exception e) {
      logger.error("==> 创建Mapper XML失败！", e);
    }

    logger.info("==>结束创建Mapper XML文件");
  }
}
