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

    // 生成BaseMapper
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_MAPPERS);
    build(headerInfoList,"BaseMapper", Constants.PACKAGE_MAPPERS_PATH);

    // 生成PageSize枚举类
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_ENUMS);
    build(headerInfoList, "PageSize", Constants.PACKAGE_ENUMS_PATH);

    // 生成SimplePage类
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_ENTITY_QUERY);
    headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".PageSize;");
    build(headerInfoList, "SimplePage", Constants.QUERY_ABSOLUTE_PATH);

    // 生成BaseQuery类
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_ENTITY_QUERY);
    build(headerInfoList, "BaseQuery", Constants.QUERY_ABSOLUTE_PATH);

    // 生成分页工具类: PaginationResultVO
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_ENTITY_VO);
    build(headerInfoList, "PaginationResultVO", Constants.VO_ABSOLUTE_PATH);

    // 生成响应实体类: ResponseVO
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_ENTITY_VO);
    build(headerInfoList, "ResponseVO", Constants.VO_ABSOLUTE_PATH);

    // 生成枚举类: ResponseCodeEnum
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_ENUMS);
    build(headerInfoList, "ResponseCodeEnum", Constants.PACKAGE_ENUMS_PATH);

    // 生成异常类: BusinessException
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_EXCEPTION);
    headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;");
    build(headerInfoList, "BusinessException", Constants.PACKAGE_EXCEPTION_PATH);

    // 生成基础控制类: ABaseController
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
    headerInfoList.add("import " + Constants.PACKAGE_ENTITY_VO + ".ResponseVO;");
    headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;");
    build(headerInfoList, "ABaseController", Constants.PACKAGE_CONTROLLER_PATH);

    // 生成全局异常控制类: AGlobalExceptionHandlerController
    headerInfoList.clear();
    headerInfoList.add("package " + Constants.PACKAGE_CONTROLLER);
    headerInfoList.add("import " + Constants.PACKAGE_ENTITY_VO + ".ResponseVO;");
    headerInfoList.add("import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;");
    headerInfoList.add("import " + Constants.PACKAGE_EXCEPTION + ".BusinessException;");
    build(headerInfoList, "AGlobalExceptionHandlerController", Constants.PACKAGE_CONTROLLER_PATH);
  }

  private static void build(List<String> headerInfoList, String fileName, String outputPath) {
    File folder = new File(outputPath);
    // 文件名不存在就创建
    if (!folder.exists()) {
      folder.mkdirs();
    }
    // 输出的文件位置
    File javaFile = new File(outputPath + "/" + fileName + ".java");
    // 模板路径
    String templatePath = BuildBase.class.getClassLoader().getResource("template/" + fileName + ".txt").getPath();

    try (
        // 文件输出流读取文件到流，1.读取文件 DateUtils.txt
        OutputStream os = new FileOutputStream(javaFile);
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(osw);
        // 输入流从流中写入数据到文件
        InputStream is = new FileInputStream(templatePath);
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
      ){
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
    }
  }
}
