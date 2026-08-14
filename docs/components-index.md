# 后端组件索引

> 从项目实战中沉淀的**可复用资产清单**。生产项目/后续项目开发时，先查本索引——能摘的摘、能测的直接跑其测试做参照，不必从零写。
>
> 路径均相对仓库根目录。`🔒 依赖外部环境` 的组件摘取时需一并带上配置。

---

## 项目一（bj-01-cms）可复用组件

### 公共基础（`bj-01-cms/src/main/java/com/readant/cms/common/`）

| 组件         | 文件                          | 能力                                                           |     独立可用      | 测试 |
| ------------ | ----------------------------- | -------------------------------------------------------------- | :---------------: | :--: |
| 统一返回体   | `R.java`                      | `R<T>`：success / error 三组静态方法，全接口统一返回           |        ✅         |  ✅  |
| 业务异常     | `BusinessException.java`      | 业务层抛出的可预期异常，携带 msg                               |        ✅         |  -   |
| 全局异常处理 | `GlobalExceptionHandler.java` | `@ControllerAdvice` 统一捕获业务异常 + 参数校验异常 → 标准 `R` |        ✅         |  ✅  |
| Token 服务   | `TokenService.java`           | create / validate / getAdminId / remove（内存存储）            |        ✅         |  ✅  |
| 操作日志注解 | `LogOperation.java`           | 自定义注解，标记需记录日志的接口                               |        ✅         |  -   |
| 操作日志切面 | `LogOperationAspect.java`     | AOP 切面自动记录模块 + 动作 + 结果                             |   🔒 依赖日志表   |  -   |
| 元对象填充   | `MyMetaObjectHandler.java`    | MyBatis-Plus 自动填充 create_time / update_time                |  🔒 需数据库字段  |  -   |
| 分页插件配置 | `MyBatisPlusConfig.java`      | 分页拦截器 + `@MapperScan`                                     |        ✅         |  -   |
| 静态资源映射 | `WebMvcConfig.java`           | 上传文件目录映射为可访问 URL                                   | 🔒 需本地上传目录 |  -   |

### 业务组件（摘取时按需复制对应 entity + mapper + service）

| 组件             | 位置                                                          | 能力                               | 测试 |
| ---------------- | ------------------------------------------------------------- | ---------------------------------- | :--: |
| 树形结构         | `CategoryService` / `ProductCategoryService`                  | `parent_id` 自引用构建嵌套树       |  ✅  |
| RBAC             | `admin` / `role` / `admin_role` + `RoleService`               | 用户-角色-权限三表关联查询         |  ✅  |
| 文章状态机       | `ArticleServiceImpl`                                          | 草稿 / 发布状态流转 + 动态条件分页 |  ✅  |
| 数据字典         | `DictDataService` + `dict_data` 表                            | 字典项 CRUD + 按类型查询           |  ✅  |
| 数据看板统计     | `DashboardService`                                            | 聚合计数 + 文章状态分布 + 7 天趋势 |  ✅  |
| 文件上传         | `FileController`                                              | MultipartFile 上传，返回可访问 URL |  -   |
| AOP 操作日志链路 | `LogOperation` + `LogOperationAspect` + `OperationLogService` | 注解式日志记录 + 查询              |  ✅* |

> *：操作日志的查询 Service 已有测试；`LogOperationAspect` 切面本体未单独测试。

---

## 使用约定

1. **优先摘取公共基础组件**（无业务耦合），直接复制 + 保留原注释。
2. **业务组件**带实体/表结构依赖，摘取时需同时迁移 Flyway 脚本与 Mapper。
3. **依赖外部环境**（`🔒`）的组件，需确认目标项目的数据库/存储配置兼容后再引入。
4. 摘取后按目标项目编码规范微调包名（`com.readant.cms` → 对应项目包名）。

---

## 项目二（bj-02-ecommerce）规划中组件（占位）

> 随项目二开发逐步填充：JWT 鉴权、Spring Security 过滤器链、Redis 缓存工具、分页查询基类等。

| 组件           | 能力                    | 状态 |
| -------------- | ----------------------- | :--: |
| JWT 工具       | Token 签发 + 解析       |  ⬜  |
| 统一分页基类   | 分页参数 / 返回结构抽象 |  ⬜  |
| Redis 缓存工具 | 缓存穿透 / 击穿处理     |  ⬜  |
