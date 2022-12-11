package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.FieldInfo;
import cn.zhuyee.bean.TableInfo;
import cn.zhuyee.utils.StrUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * <h2>生成Mapper</h2>
 *
 * <br>
 * Created by zhuye at 2022/12/4 16:18.
 */
public class BuildMapper {
  // 定义一个日志对象
  private static final Logger logger = LoggerFactory.getLogger(BuildMapper.class);

  /**
   * 根据表信息生成对应的Mapper
   *
   * @param tableInfo 表基本信息
   */
  public static void execute(TableInfo tableInfo) {
    logger.info("==>开始创建Mapper文件：{}", tableInfo.getBeanName() + Constants.SUFFIX_BEAN_MAPPER + ".java");
    File folder = new File(Constants.PACKAGE_MAPPERS_PATH);
    // 文件目录不存在时则创建
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // 生成这个java实体类
    String className = tableInfo.getBeanName() + Constants.SUFFIX_BEAN_MAPPER;
    File poFile = new File(folder, className + ".java");

    // 通过输出流向文件中写入数据
    // [优化] ==> 通过 try-with-resources 方式关闭资源
    try (
        OutputStream outputStream = new FileOutputStream(poFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)
    ) {
      // 开始创建文件
      // start ==> 生成类文件
      // 1.写入包路径
      bufferedWriter.write("package " + Constants.PACKAGE_MAPPERS + ";");
      bufferedWriter.newLine();
      bufferedWriter.newLine();

      // 2.写入导包信息
      bufferedWriter.write("import org.apache.ibatis.annotations.Param;");
      bufferedWriter.newLine();

      // 创建类的注释
      BuildComment.createClassComment(bufferedWriter, tableInfo.getComment() + "Mapper");
      // 3.类定义信息
      bufferedWriter.write("public interface " + className + "<T, P> extends BaseMapper {");
      bufferedWriter.newLine();
      bufferedWriter.newLine();

      // 4.根据索引来创建
      Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
      for (Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()) {
        List<FieldInfo> keyFieldInfoList = entry.getValue();

        // 方法名称
        StringBuilder methodName = new StringBuilder();
        // 方法参数
        StringBuilder methodParams = new StringBuilder();

        Integer index = 0;
        for (FieldInfo fieldInfo : keyFieldInfoList) {
          index++;
          // 组装方法名称
          methodName.append(StrUtils.upperCaseFirstLetter(fieldInfo.getPropertyName()));
          if (index < keyFieldInfoList.size()) {
            methodName.append("And");
          }
          // 组装方法参数：参数大于2的，用逗号隔开（下面代码可优化）
          methodParams.append("@Param(\"" + fieldInfo.getPropertyName() + "\") " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
          if (index < keyFieldInfoList.size()) {
            methodParams.append(", ");
          }
        }
        // ==> 查询操作
        BuildComment.createFieldComment(bufferedWriter, "根据" + methodName + "查询");
        bufferedWriter.write("\tT selectBy" + methodName + "(" + methodParams + ");");
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        // ==> 更新操作
        BuildComment.createFieldComment(bufferedWriter, "根据" + methodName + "更新");
        bufferedWriter.write("\tInteger updateBy" + methodName + "(@Param(\"bean\") T t, " + methodParams + ");");
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        // ==> 删除操作
        BuildComment.createFieldComment(bufferedWriter, "根据" + methodName + "删除");
        bufferedWriter.write("\tInteger deleteBy" + methodName + "(@Param(\"bean\") T t, " + methodParams + ");");
        bufferedWriter.newLine();
        bufferedWriter.newLine();
      }

      bufferedWriter.write("}");
      // end ==> 生成类文件

      bufferedWriter.flush();
    } catch (Exception e) {
      logger.error("==> 创建Mapper失败！", e);
    }

    logger.info("==>结束创建Mapper文件");
  }
}
