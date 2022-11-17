package cn.zhuyee.builder;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <h2>用于在类和字段上创建注释</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/17 23:52.
 */
public class BuildComment {

  /**
   * 创建类上的注释
   *
   * @param bw
   * @param classComment
   * @throws Exception
   */
  public static void createClassComment(BufferedWriter bw,String classComment) throws Exception {
    bw.write("/**");
    bw.newLine();
    bw.write(" * @Description: " + classComment);
    bw.newLine();
    bw.write(" * @Date: "+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
    bw.newLine();
    bw.write(" */");
    bw.newLine();
  }

  public static void createFieldComment() {

  }

  public static void createMethodComment() {

  }

}
