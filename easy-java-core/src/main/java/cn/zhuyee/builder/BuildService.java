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

      // 开始生成类
      BuildComment.createClassComment(bw, tableInfo.getComment() + "对应的业务操作");
      bw.write("public interface " + className + " {");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "根据条件查询列表");
      bw.write("\tList<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query);");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "根据条件查询数量");
      bw.write("\tInteger findCountByParam(" + tableInfo.getBeanParamName() + " query);");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "分页查询");
      bw.write("\tPaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + tableInfo.getBeanParamName() + " query);");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "新增");
      bw.write("\tInteger add(" + tableInfo.getBeanName() + " bean);");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "批量新增");
      bw.write("\tInteger addBatch(List<" + tableInfo.getBeanName() + "> listBean);");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "批量新增或修改");
      bw.write("\tInteger addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean);");
      bw.newLine();
      bw.newLine();

      for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
        List<FieldInfo> keyFieldInfoList = entry.getValue();

        // 方法名称
        StringBuilder methodName = new StringBuilder();
        // 方法参数
        StringBuilder methodParams = new StringBuilder();

        int index = 0;
        for (FieldInfo fieldInfo : keyFieldInfoList) {
          index++;
          // 组装方法名称
          methodName.append(StrUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
          if (index < keyFieldInfoList.size()) {
            methodName.append("And");
          }
          // 组装方法参数：参数大于2的，用逗号隔开（下面代码可优化）
          methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
          if (index < keyFieldInfoList.size()) {
            methodParams.append(", ");
          }
        }
        // ==> 查询操作
        BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
        bw.write("\t" + tableInfo.getBeanName() + " getBy" + methodName + "(" + methodParams + ");");
        bw.newLine();
        bw.newLine();

        // ==> 更新操作
        BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
        bw.write("\tInteger updateBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ");");
        bw.newLine();
        bw.newLine();

        // ==> 删除操作
        BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
        bw.write("\tInteger deleteBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ");");
        bw.newLine();
        bw.newLine();
      }

      bw.write("}");
      // end ==> 生成类文件
      bw.flush();
    } catch (Exception e) {
      logger.error("==> 创建Service失败！", e);
    }

    logger.info("==>结束创建文件");
  }
}
