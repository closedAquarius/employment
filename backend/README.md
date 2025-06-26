# 毕业生就业信息分析系统 - 后端

这是毕业生就业信息分析系统的后端部分，采用标准的MVC架构进行设计，基于Spring Boot框架实现。系统主要用于管理和分析高校毕业生的就业信息数据。

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── gr/
│   │   │           └── geias/
│   │   │               ├── aspect/           # 切面类，用于AOP功能实现
│   │   │               ├── config/           # 配置类，包含Web配置、拦截器配置等
│   │   │               ├── controller/       # 控制器层，处理HTTP请求
│   │   │               ├── dto/              # 数据传输对象，用于不同层间数据交互
│   │   │               ├── enums/            # 枚举类，定义常量和状态
│   │   │               ├── interceptor/      # 拦截器，实现权限控制和请求处理
│   │   │               ├── model/            # 模型层，包含所有实体类
│   │   │               ├── repository/       # 数据访问层，与数据库交互
│   │   │               ├── service/          # 服务接口层，定义业务逻辑接口
│   │   │               │   └── impl/         # 服务实现层，实现业务逻辑
│   │   │               ├── util/             # 工具类，提供通用功能
│   │   │               └── GeiasApplication.java  # 应用程序入口类
│   │   └── resources/
│   │       ├── mappers/                # MyBatis XML映射文件
│   │       ├── static/                 # 静态资源
│   │       └── application.yml         # 应用配置文件
│   └── test/                           # 测试目录
├── pom.xml                             # Maven配置文件
└── README.md                           # 项目说明文档
```

## 技术栈

- **基础框架**：
  - Java 8+
  - Spring Boot 2.2.5
  - MyBatis 2.1.0
  
- **数据库相关**：
  - MySQL 8.0+
  - Druid 连接池 1.1.10
  
- **工具库**：
  - Lombok：简化Java代码
  - EasyExcel 2.1.6：Excel导入导出
  - AOP：面向切面编程
  - 百度AI SDK 4.12.0：人脸识别功能

## MVC架构说明

本项目严格遵循MVC（Model-View-Controller）架构设计模式：

### Model（模型层）

- 位于`model`包下的实体类，映射数据库表结构
- 包括`Area`、`ClassGrade`、`College`、`EmploymentInformation`等实体类
- 使用Lombok注解简化getter/setter方法

### Controller（控制器层）

- 位于`controller`包下，处理HTTP请求和响应
- 主要控制器包括：
  - `EmploymentInformationController`: 处理就业信息相关请求
  - `OrganizationController`: 处理组织结构相关请求
  - `PersonInfoController`: 处理用户信息相关请求
- 遵循RESTful API设计规范

### Repository（数据访问层）

- 位于`repository`包下，定义数据库操作接口
- 对应的XML映射文件位于`resources/mappers`目录下
- 使用`@MapperScan`注解进行扫描

### Service（服务层）

- 接口定义在`service`包下
- 实现类在`service/impl`包下
- 包含核心业务逻辑，处理事务

## 权限控制

系统采用拦截器实现多级权限控制：

- `LoginInterceptor`: 基础登录拦截器
- `AdminInterceptor`: 管理员权限拦截器
- `SuperAdminInterceptor`: 超级管理员权限拦截器
- `SpecialtyInterceptor`: 专业管理权限拦截器
- `ClassGradeInterceptor`: 班级管理权限拦截器

## 数据库配置

系统默认使用MySQL数据库，配置参数在`application.yml`中：

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      driver-class-name: com.mysql.jdbc.Driver
      url: jdbc:mysql://localhost:3306/school_spring?useUnicode=true&characterEncoding=utf8
      username: root
      password: your_password
```

请根据实际环境修改数据库连接参数。

## API接口说明

系统提供以下主要API接口：

### 用户认证

- `POST /personinfo/login`: 用户登录
- `GET /personinfo/getuser`: 获取当前用户信息
- `PUT /personinfo/updateuser`: 更新用户信息

### 就业信息管理

- `GET /employmentinformation/getemploymentinfo`: 获取就业信息列表
- `POST /employmentinformation/addemploymentinfo`: 添加就业信息
- `PUT /employmentinformation/updateemploymentinfo`: 更新就业信息
- `GET /employmentinformation/getcountbyarea`: 按地区统计就业信息
- `GET /employmentinformation/download`: 导出就业信息Excel

### 组织结构管理

- `GET /organizationcontroller/getcollegelist`: 获取学院列表
- `POST /organizationcontroller/addcollege`: 添加学院
- `GET /organizationcontroller/getclassgrade`: 获取班级列表
- `POST /organizationcontroller/addclassgrade`: 添加班级

## 构建与运行

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 构建步骤

1. 构建项目：

```bash
mvn clean package
```

2. 运行项目：

```bash
java -jar target/geias-backend-0.0.1-SNAPSHOT.jar
```

或使用Spring Boot Maven插件：

```bash
mvn spring-boot:run
```
