package cn.zhuyee.utils;



/**
 * <h2>字符串工具类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/14 22:27.
 */
public class StrUtils {
  /**
   * 将单词转为首字母大写
   *
   * @param field
   * @return
   */
  public static String upperCaseFirstLetter(String field) {
    if (org.apache.commons.lang3.StringUtils.isEmpty(field)) {
      return field;
    }
    return field.substring(0, 1).toUpperCase() + field.substring(1);
  }

  /**
   * 将单词转为首字母小写
   *
   * @param field
   * @return
   */
  public static String lowerCaseFirstLetter(String field) {
    if (org.apache.commons.lang3.StringUtils.isEmpty(field)) {
      return field;
    }
    return field.substring(0, 1).toLowerCase() + field.substring(1);
  }

}
