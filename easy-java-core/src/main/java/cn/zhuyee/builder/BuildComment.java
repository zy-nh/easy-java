package cn.zhuyee.builder;

import cn.zhuyee.bean.Constants;

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
   * @param bw 字符缓冲输出流
   * @param classComment 表备注
   * @throws Exception 异常
   */
  public static void createClassComment(BufferedWriter bw,String classComment) throws Exception {
    bw.write("/**");
    bw.newLine();
    bw.write(" * @Description: " + classComment);
    bw.newLine();
    bw.write(" *");
    bw.newLine();
    bw.write(" * @Author: " + Constants.AUTHOR_COMMENT);
    bw.newLine();
    bw.write(" * @Date: "+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
    bw.newLine();
    bw.write(" */");
    bw.newLine();
  }

  /**
   * 创建属性注释
   *
   * @param bw 字符缓冲输出流
   * @param fieldComment 字段备注
   * @throws Exception 异常
   */
  public static void createFieldComment(BufferedWriter bw,String fieldComment) throws Exception {
    bw.write("\t/**");
    bw.newLine();
    bw.write("\t * " + (fieldComment == null ? "" : fieldComment));
    bw.newLine();
    bw.write("\t */");
    bw.newLine();
  }

  public static void createMethodComment() {

  }

}
