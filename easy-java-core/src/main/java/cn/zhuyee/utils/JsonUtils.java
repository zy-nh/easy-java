package cn.zhuyee.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * <h2>JSON工具类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/15 22:53.
 */
public class JsonUtils {
  /**
   * 将Object对象转为String
   *
   * @param object 对象
   * @return JSON串
   */
  public static String convertObj2Json(Object object) {
    if (null == object) {
      return null;
    }
    return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
  }
}
