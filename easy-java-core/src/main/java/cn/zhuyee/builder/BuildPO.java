package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
      bufferedWriter.newLine();

      // 3.类定义信息
      bufferedWriter.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
      bufferedWriter.newLine();
      bufferedWriter.write("}");

      bufferedWriter.flush();
    } catch (Exception e) {
      logger.error("==> 创建PO失败！", e);
    }

    logger.info("==>结束创建文件");
  }
}