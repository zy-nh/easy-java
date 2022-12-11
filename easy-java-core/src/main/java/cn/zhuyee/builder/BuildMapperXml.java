package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.FieldInfo;
import cn.zhuyee.bean.TableInfo;
import cn.zhuyee.utils.StrUtils;
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
        BufferedWriter br = new BufferedWriter(outputStreamWriter)
    ) {
      // 开始创建文件
      // start ==> 生成类文件
      // xml 头
      br.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      br.newLine();
      br.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\"");
      br.newLine();
      br.write("\t\t\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
      br.newLine();
      br.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPERS + "." + className + "\">");
      br.newLine();
      br.write("</mapper>");

      // end ==> 生成类文件

      br.flush();
    } catch (Exception e) {
      logger.error("==> 创建Mapper XML失败！", e);
    }

    logger.info("==>结束创建Mapper XML文件");
  }
}
