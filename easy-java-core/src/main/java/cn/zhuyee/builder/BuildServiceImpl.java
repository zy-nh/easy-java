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
 * <h2>生成Service实现类</h2>
 *
 * <br>
 * Created by zhuye at 2022/12/27 20:26.
 */
public class BuildServiceImpl {
  private static final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);

  /**
   * 根据表信息生成对应的实体类的业务实现类
   *
   * @param tableInfo 表基本信息
   */
  public static void execute(TableInfo tableInfo) {
    logger.info("==>开始创建文件：{}", tableInfo.getBeanName() + "ServiceImpl.java");
    File folder = new File(Constants.PACKAGE_SERVICE_IMPL_PATH);
    // 文件目录不存在时则创建
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // 生成类
    String interfaceName = tableInfo.getBeanName() + "Service";
    String className = tableInfo.getBeanName() + "ServiceImpl";
    String mapperName = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_MAPPER;
    String mapperBeanName = StrUtils.lowerCaseFirstLetter(mapperName);
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
      bw.write("package " + Constants.PACKAGE_SERVICE_IMPL + ";");
      bw.newLine();
      bw.newLine();

      // 2.写入导包信息
      bw.write("import " + Constants.PACKAGE_ENTITY_PO + "." + tableInfo.getBeanName() + ";");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_ENTITY_QUERY + ".SimplePage;");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_ENUMS + ".PageSize;");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_ENTITY_QUERY + "." + tableInfo.getBeanParamName() + ";");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_ENTITY_VO + ".PaginationResultVO;");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_MAPPERS + "." + mapperName + ";");
      bw.newLine();
      bw.write("import " + Constants.PACKAGE_SERVICE + "." + interfaceName + ";");
      bw.newLine();
      bw.write("import org.springframework.stereotype.Service;");
      bw.newLine();
      bw.write("import javax.annotation.Resource;");
      bw.newLine();
      bw.write("import java.util.List;");
      bw.newLine();

      // 开始生成类
      BuildComment.createClassComment(bw, tableInfo.getComment() + "对应的业务操作");
      bw.write("@Service(\"" + StrUtils.lowerCaseFirstLetter(interfaceName) + "\")");
      bw.newLine();
      bw.write("public class " + className + " implements " + interfaceName + " {");
      bw.newLine();
      bw.newLine();

      // 全局变量
      bw.write("\t@Resource");
      bw.newLine();
      bw.write("\tprivate " + mapperName + "<" + tableInfo.getBeanName() + ", " + tableInfo.getBeanParamName() + "> " + mapperBeanName + ";");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "根据条件查询列表");
      bw.write("\tpublic List<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query) {");
      bw.newLine();
      bw.write("\t\treturn this." + mapperBeanName + ".selectList(query);");
      bw.newLine();
      bw.write("\t}");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "根据条件查询数量");
      bw.write("\tpublic Integer findCountByParam(" + tableInfo.getBeanParamName() + " query) {");
      bw.newLine();
      bw.write("\t\treturn this." + mapperBeanName + ".selectCount(query);");
      bw.newLine();
      bw.write("\t}");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "分页查询");
      bw.write("\tpublic PaginationResultVO<" + tableInfo.getBeanName() + "> findListByPage(" + tableInfo.getBeanParamName() + " query) {");
      bw.newLine();
      bw.write("\t\tInteger count = this.findCountByParam(query);");
      bw.newLine();
      bw.write("\t\tint pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();");
      bw.newLine();
      bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(), count, pageSize);");
      bw.newLine();
      bw.write("\t\tquery.setSimplePage(page);");
      bw.newLine();
      bw.write("\t\tList<" + tableInfo.getBeanName() + "> list = this.findListByParam(query);");
      bw.newLine();
      bw.write("\t\tPaginationResultVO<" + tableInfo.getBeanName() + "> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);");
      bw.newLine();
      bw.write("\t\treturn result;");
      bw.newLine();
      bw.write("\t}");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "新增");
      bw.write("\tpublic Integer add(" + tableInfo.getBeanName() + " bean) {");
      bw.newLine();
      bw.write("\t\treturn this." + mapperBeanName + ".insert(bean);");
      bw.newLine();
      bw.write("\t}");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "批量新增");
      bw.write("\tpublic Integer addBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
      bw.newLine();
      bw.write("\t\tif (listBean == null || listBean.isEmpty())");
      bw.newLine();
      bw.write("\t\t\treturn 0;");
      bw.newLine();
      bw.write("\t\treturn this." + mapperBeanName + ".insertBatch(listBean);");
      bw.newLine();
      bw.write("\t}");
      bw.newLine();
      bw.newLine();

      BuildComment.createFieldComment(bw, "批量新增或修改");
      bw.write("\tpublic Integer addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> listBean) {");
      bw.newLine();
      bw.write("\t\tif (listBean == null || listBean.isEmpty())");
      bw.newLine();
      bw.write("\t\t\treturn 0;");
      bw.newLine();
      bw.write("\t\treturn this." + mapperBeanName + ".insertOrUpdateBatch(listBean);");
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

        int index = 0;
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
        bw.write("\tpublic " + tableInfo.getBeanName() + " getBy" + methodName + "(" + methodParams + ") {");
        bw.newLine();
        bw.write("\t\treturn this." + mapperBeanName + ".selectBy" + methodName + "(" + params + ");");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();

        // ==> 更新操作
        BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
        bw.write("\tpublic Integer updateBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
        bw.newLine();
        bw.write("\t\treturn this." + mapperBeanName + ".updateBy" + methodName + "(bean, " + params + ");");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();

        // ==> 删除操作
        BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
        bw.write("\tpublic Integer deleteBy" + methodName + "(" + tableInfo.getBeanName() + " bean, " + methodParams + ") {");
        bw.newLine();
        bw.write("\t\treturn this." + mapperBeanName + ".deleteBy" + methodName + "(bean, " + params + ");");
        bw.newLine();
        bw.write("\t}");
        bw.newLine();
        bw.newLine();
      }

      bw.write("}");
      // end ==> 生成类文件
      bw.flush();
    } catch (Exception e) {
      logger.error("==> 创建ServiceImpl失败！", e);
    }

    logger.info("==>结束创建文件");
  }
}
