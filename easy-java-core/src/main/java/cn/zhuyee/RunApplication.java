package cn.zhuyee;

import cn.zhuyee.bean.TableInfo;
import cn.zhuyee.builder.BuildTable;

import java.util.List;

/**
 * <h2>启动类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/14 0:13.
 */
public class RunApplication {
  public static void main(String[] args) {
    List<TableInfo> tableInfoList = BuildTable.getTables();
  }
}
