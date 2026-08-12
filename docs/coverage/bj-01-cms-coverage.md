# 项目一功能覆盖清单：兴华小组官网（bj-01-cms）

> 功能覆盖的**可核对依据**。每个功能点勾选即代表「已实现 + 有验收标准」。
> 本清单作为后续项目（二/三/四）功能覆盖的模板和参照。

## 如何阅读

- `✅` 已实现并通过验收
- `🔄` 实现中 / 待补测试
- `⬜` 未实现
- 每个功能点给出**验收标准**，便于后续项目对齐深度

---

## Phase 1：骨架搭建

| 功能 | 状态 | 验收标准 | 关联文件 |
|------|------|---------|---------|
| 项目初始化（Maven + Spring Boot） | ✅ | `mvn compile` 通过，启动类存在 | `pom.xml`、`CmsApplication.java` |
| 多环境配置 | ✅ | `application.yml` 含数据源、端口、MyBatis-Plus | `src/main/resources/application.yml` |
| 统一返回体 `R<T>` | ✅ | 成功/失败/自定义 code 三组静态方法 | `common/R.java` |
| 全局异常处理 | ✅ | `@ControllerAdvice` 捕获业务异常 + 参数校验异常 | `common/GlobalExceptionHandler.java` |
| Health API | ✅ | `GET /api/v1/health` 返回服务状态 | `controller/HealthController.java` |

## Phase 2：管理员与权限（RBAC）

| 功能 | 状态 | 验收标准 | 关联文件 |
|------|------|---------|---------|
| 管理员表设计 + Flyway | ✅ | `admin` 表、V1 脚本 | `db/migration/V1__init_schema.sql` |
| 管理员 CRUD | ✅ | 新增/查询/修改/删除，含参数校验 | `AdminService`、`AdminController` |
| BCrypt 密码加密 | ✅ | 密码入库为 BCrypt 哈希，不回传原文 | `AdminServiceImpl` |
| 登录 + Token 发放 | ✅ | 校验密码 → 发 token；错误密码抛业务异常 | `AdminServiceImpl`、`TokenService` |
| 角色表 + 管理员-角色关联 | ✅ | `role`、`admin_role` 表，多对多 | `db/migration/V2__add_role_tables.sql` |
| 角色管理 CRUD | ✅ | 角色增删改查 + 查询角色下管理员 | `RoleService` |

## Phase 3：内容管理核心

| 功能 | 状态 | 验收标准 | 关联文件 |
|------|------|---------|---------|
| 栏目管理（树形） | ✅ | `parent_id` 自引用，增删改查 | `CategoryService` |
| 栏目树查询 | ✅ | 返回嵌套树结构，含树工具 | `CategoryController /tree` |
| 文章 CRUD | ✅ | 增删改查，分页查询 | `ArticleService` |
| 文章草稿/发布状态 | ✅ | 状态字段流转（草稿↔发布），发布才前台可见 | `ArticleServiceImpl` |
| 文章动态条件查询 | ✅ | LambdaQueryWrapper 按标题/栏目/状态过滤 | `ArticleServiceImpl` |
| 富文本内容存储 | ✅ | 内容字段存储富文本 HTML | `article` 表 `content` 字段 |
| 封面图上传 | ✅ | MultipartFile 上传，返回可访问 URL | `FileController` |
| 产品分类（树形） | ✅ | 同栏目树实现 | `ProductCategoryService` |
| 产品 CRUD | ✅ | 增删改查，关联产品分类 | `ProductService` |
| 前台展示接口（无鉴权） | ✅ | 首页聚合、栏目树、文章列表/详情、产品列表 | `SiteController` |

## Phase 4：完善与收尾

| 功能 | 状态 | 验收标准 | 关联文件 |
|------|------|---------|---------|
| 操作日志（AOP） | ✅ | 自定义注解 + 切面自动记录操作 | `LogOperation`、`LogOperationAspect` |
| 操作日志查询 | ✅ | 日志分页查询 | `OperationLogService` |
| 接口文档（SpringDoc） | ✅ | `/swagger-ui.html` 可访问 | `springdoc` 依赖 |
| 数据字典 | ✅ | `dict_data` 表，字典项 CRUD | `DictDataService` |
| 自动填充（创建/更新时间） | ✅ | MyBatis-Plus 元对象处理 | `MyMetaObjectHandler` |
| 分页插件 | ✅ | MyBatis-Plus 分页拦截器配置 | `MyBatisPlusConfig` |
| 静态资源映射 | ✅ | 上传文件可通过 URL 访问 | `WebMvcConfig` |

---

## 质量维度（贯穿所有模块）

| 维度 | 状态 | 说明 |
|------|------|------|
| 分层架构 | ✅ | controller → service → mapper，Controller 不写业务逻辑 |
| 参数校验 | ✅ | 请求体 `@Valid` + 字段注解 |
| DTO / VO 分层 | ✅ | 请求体与返回体分离 |
| 统一返回 | ✅ | 所有接口返回 `R` |
| 单元测试 | 🔄 | **当前缺失，待补**（见测试计划） |
| 日志规范 | ✅ | `@Slf4j` + 占位符 |
| 数据库迁移 | ✅ | Flyway V1~V7 + 种子数据 |

---

## 测试计划（Phase 4 收尾项）

| 被测对象 | 覆盖点 | 优先级 |
|---------|--------|--------|
| `AdminServiceImpl` | 登录成功/密码错误/管理员不存在、BCrypt 校验 | 核心 |
| `TokenService` | create/validate/get/remove | 核心 |
| `ArticleServiceImpl` | 分页、状态流转、动态条件、草稿前台不可见 | 核心 |
| `CategoryServiceImpl` | 树形构建、循环引用防护 | 核心 |
| `R` / `GlobalExceptionHandler` | 静态方法、异常映射 | 公共组件 |

> 详见根目录「测试规范」，单元测试框架为 JUnit 5 + Mockito + AssertJ。