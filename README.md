# 糊涂判题推荐系统

## 项目简介
糊涂判题推荐系统是一个能够进行 ACM 在线答题的系统，提供了用户管理、题目管理、判题管理、题目推荐等核心功能。系统支持多种判题方式，包括 Java 原生 ACM 判题、Docker 交互式判题、Docker ACM 判题以及 AI 判题。
## 功能特性
- **用户管理**：提供用户相关的管理功能，方便用户的注册、登录、信息修改等操作。
- **题目管理**：对题目进行管理，包括题目的添加、编辑、删除等操作，支持题目标签、答案、判题用例和配置的管理。
- **判题管理**：负责对用户提交的代码进行判题，提供多种判题方式，确保判题的准确性和多样性。
- **判题服务**：目前可以实现 Java 原生 ACM 判题、Docker 交互式判题、Docker ACM 判题以及 AI 判题，满足不同场景的需求。

- **推荐系统模块**
1. 混合推荐策略：协同过滤与内容推荐
   a. 协同过滤推荐：通过分析用户的答题行为，找出与目标用户具有相似答题模式的其他用户群体。利用ALS（交替最小二乘法）模型，对用户和题目之间的潜在关系进行深度挖掘，为用户推荐那些被相似用户成功解答的题目。
   b. 内容推荐：深入分析题目的标签信息，构建用户 - 标签偏好模型。根据用户在不同标签下的答题表现，为每个标签赋予相应的权重。同时，系统会过滤掉用户已经正确解答过的题目，确保推荐的题目具有针对性和新颖性。
2. 动态调整与自适应优化
   a. 用户的知识水平和学习需求是不断变化的，本推荐算法具备强大的动态调整能力。它会实时跟踪用户的答题情况，根据用户的最新表现和反馈，及时调整推荐策略。

## 环境要求
- **JDK**：1.8
- **Spring Boot**：2.6.13
- **Spring Cloud**：2021.0.5
- **MySQL**：用于存储系统数据，如用户信息、题目信息等。
- **Redis**：用于 session 存储和缓存。
- **Nacos**：作为服务注册与发现中心。
- **RabbitMQ**：用于消息队列。

## 项目目录结构
```
hutuoj
├── hutuoj-backend-common           # 公共模块，包含通用工具类和常量
├── hutuoj-backend-gateway          # 网关模块，负责请求的路由和过滤
├── hutuoj-backend-judge-service    # 判题服务模块，实现代码判题功能
├── hutuoj-backend-model            # 模型模块，定义数据传输对象和实体类
├── hutuoj-backend-question-service # 题目服务模块，负责题目相关的业务逻辑
├── hutuoj-backend-service-client   # OpenFeign 模块，用于服务间的远程调用
├── hutuoj-backend-user-service     # 用户服务模块，处理用户相关的业务逻辑
└── pom.xml                         # Maven 项目配置文件
```

## 配置说明
### 数据库配置
在各个服务的 `application.yml` 文件中，需要配置数据库连接信息，例如：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/xxx
    username: xxx
    password: xxx
```

### Redis 配置
同样在 `application.yml` 中配置 Redis 信息：
```yaml
spring:
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
```

### Nacos 配置
各个服务需要配置 Nacos 的服务注册地址：
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
```

### RabbitMQ 配置
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    password: guest
    username: guest
```

## 使用方法
1. **克隆项目**：
```bash
git clone <项目仓库地址>
cd hutuoj
```

2. **安装依赖**：
   使用 Maven 安装项目依赖：
```bash
mvn clean install
```

3. **启动服务**：
   依次启动各个服务，例如：
```bash
# 启动网关服务
cd hutuoj-backend-gateway
mvn spring-boot:run

# 启动用户服务
cd hutuoj-backend-user-service
mvn spring-boot:run

# 启动题目服务
cd hutuoj-backend-question-service
mvn spring-boot:run

# 启动判题服务
cd hutuoj-backend-judge-service
mvn spring-boot:run
```

## 测试
启动所有服务后，可以通过以下链接访问 API 文档：
[Api Documentation](http://localhost:8101/doc.html#/home)

在 API 文档中，可以测试各个接口的功能。

## 注意事项
- 部分配置信息在 `application.yml` 文件中标记为 `todo`，需要根据实际情况进行替换，如数据库密码、Redis 密码等。
- 确保 MySQL、Redis、Nacos 和 RabbitMQ 服务正常运行，否则会影响系统的正常使用。

