package cn.zhuyee;

import cn.zhuyee.bean.TableInfo;
import cn.zhuyee.builder.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <h2>启动类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/14 0:13.
 */
public class RunApplication {
  // 定义一个日志对象
  private static final Logger logger = LoggerFactory.getLogger(RunApplication.class);
  public static void main(String[] args) {
    logger.info("==>开始执行");
    List<TableInfo> tableInfoList = BuildTable.getTables();
    BuildBase.execute(); // 生成工具包
    for (TableInfo tableInfo : tableInfoList) {
      BuildPO.execute(tableInfo);
      BuildQuery.execute(tableInfo);
      BuildMapper.execute(tableInfo);
      BuildMapperXml.execute(tableInfo);
      BuildService.execute(tableInfo);
      BuildServiceImpl.execute(tableInfo);
      BuildController.execute(tableInfo);
    }
  }
}
