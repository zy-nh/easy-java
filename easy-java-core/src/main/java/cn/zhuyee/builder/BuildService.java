package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * <h2>构建业务层代码</h2>
 *
 * <br>
 * Created by zhuye at 2022/12/26 23:25.
 */
public class BuildService {
  private static final Logger logger = LoggerFactory.getLogger(BuildService.class);

  /**
   * 根据表信息生成对应的实体类的业务类
   *
   * @param tableInfo 表基本信息
   */
  public static void execute(TableInfo tableInfo) {
    logger.info("==>开始创建文件：{}", tableInfo.getBeanName() + "Service.java");
    File folder = new File(Constants.PACKAGE_SERVICE_PATH);
    // 文件目录不存在时则创建
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // 生成类
    String className = tableInfo.getBeanName() + "Service";
    File poFile = new File(folder, className + ".java");

    // 通过输出流向文件中写入数据
    try (
        OutputStream outputStream = new FileOutputStream(poFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(outputStreamWriter)
    ) {
      // 开始创建文件
      // start ==> 生成类文件
      // 1.写入包路径
      bw.write("package " + Constants.PACKAGE_SERVICE + ";");
      bw.newLine();
      bw.newLine();

      // 2.写入导包信息
      bw.write("import " + Constants.PACKAGE_ENTITY_PO + "." + tableInfo.getBeanName() + ";");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_ENTITY_QUERY + "." + tableInfo.getBeanParamName() + ";");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_ENTITY_VO + ".PaginationResultVO;");
      bw.newLine();
      bw.write("import java.util.List;");
      bw.newLine();
      // 有日期、时间类型就导包
      if (tableInfo.getHaveDateTime() || tableInfo.getHaveDate()) {
        bw.write("import java.util.Date;");
        bw.newLine();
        bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
        bw.newLine();
        bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS);
        bw.newLine();

        bw.write("import " + Constants.PACKAGE_UTILS + ".DateUtils;");
        bw.newLine();
        bw.write("import " + Constants.PACKAGE_ENUMS + ".DateTimePatternEnum;");
        bw.newLine();
      }

      BuildComment.createClassComment(bw, tableInfo.getComment() + "对应的业务操作");
      bw.write("public interface " + className + " {");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "根据条件查询列表");
      bw.write("\tList<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query);");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "根据条件查询数量");
      bw.write("\tLong findCountByParam(" + tableInfo.getBeanParamName() + " query);");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "分页查询");
      bw.write("\tPaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + tableInfo.getBeanParamName() + " query);");
      bw.newLine();
      bw.newLine();

      bw.write("}");
      // end ==> 生成类文件
      bw.flush();
    } catch (Exception e) {
      logger.error("==> 创建Service失败！", e);
    }

    logger.info("==>结束创建文件");
  }
}
