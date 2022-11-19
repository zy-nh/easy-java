package cn.zhuyee.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <h2>日期时间工具类</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/19 11:25.
 */
public class DateUtils {
  public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd hh:mm:ss";
  public static final String YYYY_MM_DD = "yyyy-MM-dd";
  public static final String YYYY_MM_DD_ = "yyyy/MM/dd";
  public static final String YYYYMMDD = "yyyyMMdd";

  public static String format(String date, String patten) {
    return new SimpleDateFormat(patten).format(date);
  }

  public static Date parse(String date, String patten) {
    new SimpleDateFormat(patten).format(date);
    return null;
  }
}
