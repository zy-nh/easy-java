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
 * <h2>生成控制类</h2>
 *
 * <br>
 * Created by zhuye at 2022/12/27 20:26.
 */
public class BuildController {
  private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

  /**
   * 根据表信息生成对应的实体类的控制类
   *
   * @param tableInfo 表基本信息
   */
  public static void execute(TableInfo tableInfo) {
    logger.info("==>开始创建文件：{}", tableInfo.getBeanName() + "Controller.java");
    File folder = new File(Constants.PACKAGE_CONTROLLER_PATH);
    // 文件目录不存在时则创建
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // 生成类
    String className = tableInfo.getBeanName() + "Controller";
    String serviceName = tableInfo.getBeanName() + "Service";
    String serviceBeanName = StrUtils.lowerCaseFirstLetter(serviceName);
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
      bw.write("package " + Constants.PACKAGE_CONTROLLER + ";");
      bw.newLine();
      bw.newLine();

      // 2.写入导包信息
      bw.write("import " + Constants.PACKAGE_ENTITY_PO + "." + tableInfo.getBeanName() + ";");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_ENTITY_QUERY + "." + tableInfo.getBeanParamName() + ";");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_ENTITY_VO + ".ResponseVO;");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_SERVICE + "." + serviceName + ";");
      bw.newLine();
      bw.write("import org.springframework.web.bind.annotation.RequestBody;");
      bw.newLine();
      bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
      bw.newLine();
      bw.write("import org.springframework.web.bind.annotation.RestController;");
      bw.newLine();
      bw.write("import javax.annotation.Resource;");
      bw.newLine();
      bw.write("import java.util.List;");
      bw.newLine();
      bw.newLine();

      BuildComment.createClassComment(bw, tableInfo.getComment() + "对应的控制类");
      bw.write("@RestController");
      bw.newLine();
      bw.write("@RequestMapping(\"" + StrUtils.lowerCaseFirstLetter(tableInfo.getBeanName()) + "\")");
      bw.newLine();
      bw.write("public class " + className + " extends ABaseController {");
      bw.newLine();
      bw.newLine();

      // 全局变量
      bw.write("\t@Resource");
      bw.newLine();
      bw.write("\tprivate " + serviceName + " " + serviceBeanName + ";");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "根据条件分页查询");
      bw.write("\t@RequestMapping(\"loadDataList\")");
      bw.newLine();
      bw.write("\tpublic ResponseVO loadDataList(" + tableInfo.getBeanParamName() + " query) {");
      bw.newLine();
      bw.write("\t\treturn getSuccessResponseVO(" + serviceBeanName + ".findListByPage(query));");
      bw.newLine();
      bw.write("\t}");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "新增");
      bw.write("\t@RequestMapping(\"add\")");
      bw.newLine();
      bw.write("\tpublic ResponseVO add(" + tableInfo.getBeanName() + " bean) {");
      bw.newLine();
      bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".add(bean));");
      bw.newLine();
      bw.write("\t}");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "批量新增");
      bw.write("\t@RequestMapping(\"addBatch\")");
      bw.newLine();
      bw.write("\tpublic ResponseVO addBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
      bw.newLine();
      bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".addBatch(listBean));");
      bw.newLine();
      bw.write("\t}");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "批量新增或修改");
      bw.write("\t@RequestMapping(\"addOrUpdateBatch\")");
      bw.newLine();
      bw.write("\tpublic ResponseVO addOrUpdateBatch(@RequestBody List<" + tableInfo.getBeanName() + "> listBean) {");
      bw.newLine();
      bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".addOrUpdateBatch(listBean));");
      bw.newLine();
      bw.write("\t}");
      bw.newLine();
      bw.newLine();

      for (Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
        List<FieldInfo> keyFieldInfoList = entry.getValue();

        // 方法名称
        StringBuilder methodName = new StringBuilder();
        // 方法参数
        StringBuilder methodParams = new StringBuilder();
        // 方法参数（不带类型）
        StringBuilder params = new StringBuilder();

        Integer index = 0;
        for (FieldInfo fieldInfo : keyFieldInfoList) {
          index++;
          // 组装方法名称
          methodName.append(StrUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
          // 组装方法参数：参数大于2的，用逗号隔开（下面代码可优化）
          methodParams.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
          // 参数不带类型的
          params.append(fieldInfo.getPropertyName());
          if (index < keyFieldInfoList.size()) {
            methodName.append("And");
            methodParams.append(", ");
            params.append(", ");
          }
        }
        // ==> 查询操作
        BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
        bw.write("\t@RequestMapping(\"getBy" + methodName + "\")");
        bw.newLine();
        bw.write("\tpublic ResponseVO getBy" + methodName + "(" + methodParams + ") {");
        bw.newLine();
        bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".getBy" + methodName + "(" + params + "));");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();

        // ==> 更新操作
        BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
        bw.write("\t@RequestMapping(\"updateBy" + methodName + "\")");
        bw.newLine();
        bw.write("\tpublic ResponseVO updateBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
        bw.newLine();
        bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".updateBy" + methodName + "(bean, " + params + "));");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();

        // ==> 删除操作
        BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
        bw.write("\t@RequestMapping(\"deleteBy" + methodName + "\")");
        bw.newLine();
        bw.write("\tpublic ResponseVO deleteBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
        bw.newLine();
        bw.write("\t\treturn getSuccessResponseVO(this." + serviceBeanName + ".deleteBy" + methodName + "(bean, " + params + "));");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();
      }

      bw.write("}");
      // end ==> 生成类文件
      bw.flush();
    } catch (Exception e) {
      logger.error("==> 创建Controller失败！", e);
    }

    logger.info("==>结束创建文件");
  }
}
