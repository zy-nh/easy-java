package cn.zhuyee.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h2>配置文件读取工具类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/13 23:43.
 */
public class PropertiesUtils {
  // 定义一个静态属性类
  private static final Properties props = new Properties();
  // 把所有属性放到map中
  private static final Map<String, String> PROPERTY_MAP = new ConcurrentHashMap();

  // 项目启动初始化时去读取配置
  static {
    InputStream is = null;
    try {
      // 读取配置文件：load的时候用输入流包装一下，并指定编码格式
      is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
      props.load(new InputStreamReader(is, StandardCharsets.UTF_8));

      // 定义一个迭代器，用来遍历并塞值到map中
      Iterator<Object> iterator = props.keySet().iterator();
      while (iterator.hasNext()) {
        String key = (String) iterator.next();
        PROPERTY_MAP.put(key, props.getProperty(key));
      }
    } catch (Exception e) {

    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * 通过key来获取值
   *
   * @param key key
   * @return value
   */
  public static String getString(String key) {
    return PROPERTY_MAP.get(key);
  }

  public static void main(String[] args) {
    System.out.println(getString("db.url"));
  }
}
