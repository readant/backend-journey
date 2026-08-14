# 项目一速查卡片：兴华小组官网（bj-01-cms）

## 项目结构

```
bj-01-cms/
├── pom.xml
├── src/main/java/com/readant/cms/
│   ├── CmsApplication.java          ← 启动类
│   ├── common/                      ← 公共基础
│   │   ├── R.java                   ← 统一返回体
│   │   ├── BusinessException.java   ← 业务异常
│   │   ├── GlobalExceptionHandler.java ← 全局异常处理
│   │   ├── MyMetaObjectHandler.java ← 自动填充
│   │   ├── TokenService.java        ← Token 服务
│   │   ├── MyBatisPlusConfig.java   ← 分页插件
│   │   ├── WebMvcConfig.java        ← 静态资源映射
│   │   ├── LogOperation.java        ← 操作日志注解
│   │   └── LogOperationAspect.java  ← AOP 切面
│   ├── entity/                      ← 实体（7个）
│   ├── mapper/                      ← Mapper（7个）
│   ├── service/                     ← Service 接口+实现
│   └── controller/                  ← Controller（8个）
└── src/main/resources/
    ├── application.yml
    └── db/migration/                 ← Flyway 脚本（V1-V7 + R__seed_data）
```

## 数据库表

| 表名             | 说明             | Flyway 版本 |
| ---------------- | ---------------- | ----------- |
| admin            | 管理员           | V1          |
| role             | 角色             | V2          |
| admin_role       | 管理员-角色关联  | V2          |
| category         | 栏目（树形）     | V3          |
| article          | 文章             | V4          |
| product_category | 产品分类（树形） | V5          |
| product          | 产品             | V5          |
| operation_log    | 操作日志         | V6          |
| dict_data        | 数据字典         | V7          |

## API 清单

### 后台管理（需鉴权）

| 方法                | 路径                                | 说明        |
| ------------------- | ----------------------------------- | ----------- |
| POST                | `/api/v1/admins/login`              | 登录        |
| POST/GET/PUT/DELETE | `/api/v1/admins[/{id}]`             | 管理员 CRUD |
| GET/POST/DELETE     | `/api/v1/roles[/...]`               | 角色管理    |
| GET/POST/PUT/DELETE | `/api/v1/categories[/{id}]`         | 栏目管理    |
| GET                 | `/api/v1/categories/tree`           | 栏目树      |
| POST/GET/PUT/DELETE | `/api/v1/articles[/{id}]`           | 文章管理    |
| POST                | `/api/v1/files/upload`              | 文件上传    |
| GET/POST/PUT/DELETE | `/api/v1/product-categories[/{id}]` | 产品分类    |
| GET/POST/PUT/DELETE | `/api/v1/products[/{id}]`           | 产品管理    |
| GET/POST/PUT/DELETE | `/api/v1/dict-data[/...]`           | 数据字典    |

### 前台展示（无鉴权）

| 方法 | 路径                           | 说明          |
| ---- | ------------------------------ | ------------- |
| GET  | `/api/v1/site/home`            | 首页聚合      |
| GET  | `/api/v1/site/categories`      | 栏目树        |
| GET  | `/api/v1/site/articles[/{id}]` | 文章列表/详情 |
| GET  | `/api/v1/site/products`        | 产品列表      |

## 关键注解

```java
@SpringBootApplication    // Spring Boot 启动
@RestController           // RESTful 接口
@RequestMapping("/api/v1/...") // 请求路径映射
@RequiredArgsConstructor   // Lombok 构造器注入
@Slf4j                    // Lombok 日志
@Data                     // Lombok getter/setter
@TableName("xxx")         // MyBatis-Plus 表映射
@MapperScan("com.readant.cms.mapper") // Mapper 扫描
@Valid / @NotBlank / @Size / @Email // 参数校验
@ControllerAdvice         // 全局异常处理
@LogOperation(module, action) // AOP 操作日志
```

## 核心设计模式

1. **统一返回体** `R<T>`：所有接口返回 code + msg + data
2. **全局异常处理**：`@ControllerAdvice` 统一捕获异常
3. **树形结构**：`parent_id` 自引用实现多级树
4. **RBAC**：用户-角色-权限三张表
5. **AOP 日志**：自定义注解 + 切面自动记录操作日志
6. **DTO/VO 分层**：请求体和返回体分离，避免暴露内部字段

## 简历描述参考

> 独立设计并实现企业级 CMS 内容管理系统，支持多级栏目管理、文章发布、产品分类、RBAC 权限控制，采用 Spring Boot + MyBatis-Plus 分层架构，提供前台展示与后台管理分离的 RESTful API，集成 Flyway 数据库迁移、SpringDoc OpenAPI 接口文档、AOP 操作日志等基础设施。
