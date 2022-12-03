package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>生成基础类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/19 17:48.
 */
public class BuildBase {
  private static Logger logger = LoggerFactory.getLogger(BuildBase.class);

  public static void execute() {
    List<String> headerInfoList = new ArrayList<>();
    // 生成枚举类
    headerInfoList.add("package " + Constants.PACKAGE_ENUMS);
    build(headerInfoList, "DateTimePatternEnum", Constants.PACKAGE_ENUMS_PATH);
    headerInfoList.clear();
    // 生成时间工具类
    headerInfoList.add("package " + Constants.PACKAGE_UTILS);
    build(headerInfoList,"DateUtils", Constants.PACKAGE_UTILS_PATH);

  }

  private static void build(List<String> headerInfoList, String fileName, String outputPath) {
    File folder = new File(outputPath);
    // 文件名不存在就创建
    if (!folder.exists()) {
      folder.mkdirs();
    }
    // 输出的文件位置
    File javaFile = new File(outputPath + "/" + fileName + ".java");
    // 文件输出流读取文件到流
    OutputStream os = null;
    OutputStreamWriter osw = null;
    BufferedWriter bw = null;
    // 输入流从流中写入数据到文件
    InputStream is = null;
    InputStreamReader isr = null;
    BufferedReader br = null;
    try {
      // 1.读取文件 DateUtils.txt
      os = new FileOutputStream(javaFile);
      osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
      bw = new BufferedWriter(osw);

      // 模板路径
      String templatePath = BuildBase.class.getClassLoader().getResource("template/" + fileName + ".txt").getPath();
      is = new FileInputStream(templatePath);
      isr = new InputStreamReader(is, StandardCharsets.UTF_8);
      br = new BufferedReader(isr);

      // 导入类的包名
      for (String head : headerInfoList) {
        bw.write(head + ";");
        bw.newLine();
        if (head.contains("package")) {
          bw.newLine();
        }
      }

      // 2.将包路径通过输入流写入到文件 DateUtils.java 中
      // 3.将文件 DateUtils.java 输出到指定位置
      String lineInfo = null;
      while ((lineInfo = br.readLine()) != null) {
        // 读取一行写入
        bw.write(lineInfo);
        bw.newLine();
      }
      // 刷新
      bw.flush();

    } catch (Exception e) {
      logger.error("生成基础类：{} 失败：", fileName, e);
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (isr != null) {
        try {
          isr.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (bw != null) {
        try {
          bw.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (osw != null) {
        try {
          osw.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }
}
