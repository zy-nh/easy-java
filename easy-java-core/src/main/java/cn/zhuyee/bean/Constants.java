package cn.zhuyee.bean;

import cn.zhuyee.utils.PropertiesUtils;

/**
 * <h2>常量类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/14 21:58.
 */
public class Constants {
  // 是否忽略表前缀
  public static Boolean IGNORE_TABLE_PREFIX;
  // 参数 bean 后缀
  public static String PARAM_BEAN_SUFFIX;
  static{
    IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
    PARAM_BEAN_SUFFIX = PropertiesUtils.getString("param.bean.suffix");
  }
}

