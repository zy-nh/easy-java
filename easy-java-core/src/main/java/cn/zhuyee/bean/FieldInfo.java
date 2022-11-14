package cn.zhuyee.bean;

/**
 * <h2>表字段信息</h2>
 *
 * <br>
 * Created by zhuye at 2022/11/14 0:30.
 */
public class FieldInfo {
  /**
   * 字段名称
   */
  private String fieldName;
  /**
   * bean 属性名称
   */
  private String propertyName;
  /**
   * 字段在数据库中类型
   */
  private String sqlType;
  /**
   * 字段类型
   */
  private String javaType;
  /**
   * 字段备注
   */
  private String comment;
  /**
   * 字段是否是自增长
   */
  private Boolean isAutoIncrement;

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public String getSqlType() {
    return sqlType;
  }

  public void setSqlType(String sqlType) {
    this.sqlType = sqlType;
  }

  public String getJavaType() {
    return javaType;
  }

  public void setJavaType(String javaType) {
    this.javaType = javaType;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Boolean getAutoIncrement() {
    return isAutoIncrement;
  }

  public void setAutoIncrement(Boolean autoIncrement) {
    isAutoIncrement = autoIncrement;
  }
}
