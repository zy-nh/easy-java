package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;
import cn.zhuyee.bean.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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

    // 先生成这个java实体类
    File file = new File(folder, tableInfo.getBeanName() + ".java");
    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.info("==>结束创建文件");
  }
}
