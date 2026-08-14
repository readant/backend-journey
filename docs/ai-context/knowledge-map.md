# Java 后端知识地图

四层知识体系，从底到顶。标注每个知识点在哪个项目首次出现。

## 第一层：Java 语法基础

| 知识点                                 | 首次出现 | 说明                                 |
| -------------------------------------- | -------- | ------------------------------------ |
| 程序结构（class、main 方法）           | 项目一   | Java 程序入口、类的概念              |
| 变量与类型（强类型系统）               | 项目一   | 变量必须声明类型，类型不匹配编译报错 |
| 基本数据类型与包装类                   | 项目一   | int/Integer、String 等               |
| 访问修饰符（public/private/protected） | 项目一   | 封装性                               |
| 类与对象、构造方法                     | 项目一   | OOP 基础                             |
| this / super                           | 项目一   | 继承中的引用                         |
| 继承（extends）与接口（implements）    | 项目一   | 单继承多实现                         |
| 抽象类 vs 接口                         | 项目一   | 设计层面的区别                       |
| 包管理（package / import）             | 项目一   | 项目组织结构                         |
| 枚举（enum）                           | 项目一   | 状态、类型常量                       |
| 异常处理（try-catch-finally、throws）  | 项目一   | 受检异常 vs 非受检异常               |
| 注解（@Annotation）基础                | 项目一   | 元数据标记                           |
| static / final 关键字                  | 项目一   | 类级别 vs 实例级别                   |
| Maven 基础（pom.xml、依赖管理）        | 项目一   | 构建工具                             |

## 第二层：Java 语言核心

| 知识点                        | 首次出现 | 说明                  |
| ----------------------------- | -------- | --------------------- |
| 集合框架（List / Map / Set）  | 项目一   | ArrayList、HashMap 等 |
| 泛型（`<T>`）                 | 项目一   | 类型安全的集合        |
| Lambda 表达式                 | 项目一   | `->` 语法             |
| Stream API                    | 项目一   | 集合函数式操作        |
| Optional                      | 项目一   | 空值安全              |
| 日期时间 API（LocalDateTime） | 项目一   | Java 8 时间处理       |
| IO / NIO 基础                 | 项目一   | 文件读写              |
| 反射基础                      | 项目二   | 框架底层原理          |
| 自定义注解                    | 项目二   | 框架扩展能力          |
| 并发基础（线程、线程池）      | 项目三   | CompletableFuture     |

## 第三层：Spring Boot 框架

| 知识点                            | 首次出现 | 说明                             |
| --------------------------------- | -------- | -------------------------------- |
| Spring Boot 启动原理              | 项目一   | @SpringBootApplication           |
| IoC / DI（依赖注入）              | 项目一   | @Autowired、构造器注入           |
| AOP（面向切面）                   | 项目一   | 日志切面、事务                   |
| @RestController / @RequestMapping | 项目一   | RESTful 接口                     |
| @Service / @Component             | 项目一   | Bean 注册                        |
| MyBatis-Plus CRUD                 | 项目一   | BaseMapper、ServiceImpl          |
| 条件构造器（Wrapper）             | 项目一   | QueryWrapper、LambdaQueryWrapper |
| 分页插件                          | 项目一   | MyBatis-Plus Page                |
| 参数校验（jakarta.validation）    | 项目一   | @Valid、@NotNull 等              |
| 全局异常处理（@ControllerAdvice） | 项目一   | 统一异常捕获                     |
| 配置文件（application.yml）       | 项目一   | 多环境配置                       |
| SpringDoc OpenAPI                 | 项目一   | 接口文档自动生成                 |
| 文件上传                          | 项目一   | MultipartFile                    |
| 拦截器（HandlerInterceptor）      | 项目二   | 请求预处理                       |
| 过滤器（Filter）                  | 项目二   | Servlet 级别拦截                 |
| Spring Security 基础              | 项目二   | 认证与授权框架                   |
| JWT 鉴权                          | 项目二   | Token 生成与验证                 |
| 事务管理（@Transactional）        | 项目二   | 声明式事务                       |
| 事件机制（ApplicationEvent）      | 项目三   | 异步解耦                         |
| 定时任务（@Scheduled）            | 项目三   | 周期性任务                       |

## 第四层：中间件与架构

| 知识点                             | 首次出现 | 说明                      |
| ---------------------------------- | -------- | ------------------------- |
| Redis 基础（String/Hash/List/Set） | 项目二   | 缓存、计数器              |
| Spring Data Redis                  | 项目二   | RedisTemplate、@Cacheable |
| Redis 高级（过期策略、持久化）     | 项目二   | 缓存设计                  |
| Elasticsearch 基础                 | 项目二   | 全文搜索                  |
| 消息队列（RabbitMQ/Kafka）         | 项目三   | 异步解耦、削峰            |
| 对象存储（OSS/MinIO）              | 项目二   | 文件云存储                |
| 设计模式：策略模式                 | 项目二   | 支付方式切换              |
| 设计模式：模板方法                 | 项目二   | 订单流程                  |
| 设计模式：工厂模式                 | 项目二   | 对象创建                  |
| 多租户架构                         | 项目四   | 数据隔离方案              |
| RBAC 权限模型（完整版）            | 项目四   | 用户-角色-权限            |
| Docker 部署                        | 项目四   | 容器化                    |
| 接口限流                           | 项目四   | 防刷保护                  |
| 接口幂等性                         | 项目四   | 重复请求安全              |

---

# Java 核心概念速查

用通俗语言和生活化比喻解释，不依赖其他语言经验。
📖 系统化完整教学（比喻 + 代码 + 设计意图 + 常见坑 + 自测题）见 [level1-java-basics](../learning/level1-java-basics/) 和 [level2-java-core](../learning/level2-java-core/)。

## 程序结构

```java
// Java 程序入口：必须有 class 包裹
public class Application {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```

**通俗理解**：Java 程序就像一个工厂（class），所有生产活动（代码）都必须在工厂里进行。`main` 方法是工厂的总开关，程序从这里启动。

## 变量与类型

```java
// Java：强类型，必须声明类型
String name = "张三";
int age = 25;
List<String> names = new ArrayList<>();
```

**通俗理解**：Java 的变量就像有标签的盒子——`String name` 表示这个盒子只能装字符串，`int age` 只能装整数。装错了类型，编译时就报错。

## 类与对象

```java
// Java 类定义
public class User {
    private String name;       // 属性：用户有什么
    private int age;

    public String getName() {  // 方法：用户能做什么
        return this.name;
    }
}
```

**通俗理解**：类是一张"图纸"，对象是按图纸造出来的"实物"。`User` 是图纸，`new User()` 是造出来的具体某个人。

## 注解（Annotation）

```java
@Data          // 贴标签：自动生成 getter/setter/toString 等
@RestController // 贴标签：告诉 Spring 这是一个处理网页请求的类
```

**通俗理解**：注解就像贴在代码上的便利贴，告诉框架"这段代码有特殊用途"。框架看到便利贴后会自动做对应的事情。
