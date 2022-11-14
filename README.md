## 代码生成工具介绍

通过代码自动生成，减少代码，并通过这个项目学习Mybatis框架。

### 搭建项目框架
**核心代码生成模块**

这是一个普通的maven项目，基本结构如下：
```text
easy-java-core
    - src
        - main
            - java
                - cn
                    - zhuyee
                        - bean
                        - builder
                        - utils
                        - RunApplication.java
            - resources
                - application.yml
        - test
    - pom.xml
```

**自动生成测试模块**

这是一个springboot项目，搭建的基础模块，结构如下：
```text
easy-java-demo
    - src
        - main
            - java
                - cn
                    - zhuyee
                        - RunDemoApp.java
            - resources
                - application.yml
        - test
    - pom.xml
```

### 读取表基本信息
通过MySQL的几个查询语句可查出表的详细数据：
```mysql
show table status;
```

### 读取表字段


### 读取表索引


