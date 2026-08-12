# 兴华小组企业官网 + CMS（bj-01-cms）

项目一 · 内容型业务。作为仓库首个完整工程，同时承担「后续项目的样板」职责：分层结构、公共组件、编码规范均在此沉淀。

- **技术栈**：Java 17 / Spring Boot 3.2.5 / MyBatis-Plus 3.5.7 / MySQL 8.0 / Flyway / SpringDoc
- **学习阶段**：阶段一（语法锚定期）→ 阶段二（框架认知期）
- **配套前端**：`pj-01-cms-admin`（管理后台）、`pj-01-cms-portal`（前台门户）

---

## 快速开始

前置条件：本地 MySQL 8.0，库名 `bj_cms`，账号 `root` / 密码 `123456`（可在 `application.yml` 修改）。

```bash
# 编译
mvn compile

# 启动（Flyway 自动建表 + 种子数据）
mvn spring-boot:run

# 访问接口文档
open http://localhost:8080/swagger-ui.html
```

启动后自动执行 Flyway 脚本（`src/main/resources/db/migration/`），初始化所有表并注入种子数据。
默认管理员：`admin` / `admin123`。

---

## 架构与目录

分层：`controller → service → mapper`，公共能力抽到 `common`。

```
src/main/java/com/readant/cms/
├── CmsApplication.java          # 启动类
├── common/                      # 公共基础（可复用组件，见 docs/components-index.md）
│   ├── R.java                   # 统一返回体
│   ├── BusinessException.java   # 业务异常
│   ├── GlobalExceptionHandler.java
│   ├── TokenService.java        # Token 鉴权（内存）
│   ├── LogOperation.java / LogOperationAspect.java  # AOP 操作日志
│   ├── MyMetaObjectHandler.java # 时间自动填充
│   ├── MyBatisPlusConfig.java   # 分页插件
│   └── WebMvcConfig.java        # 静态资源映射
├── entity/ mapper/              # 8 张表的实体 + Mapper
├── service/ service/impl/       # Service 接口 + 实现
├── dto/                         # 请求体 / 返回体
└── controller/                  # RESTful 接口
src/main/resources/
├── application.yml
└── db/migration/                # Flyway V1~V7 + 种子数据
```

---

## 功能总览

| 模块 | 能力 | 鉴权 |
|------|------|------|
| 健康检查 | `GET /api/v1/health` | 无 |
| 管理员 + RBAC | 管理员 CRUD、BCrypt 登录、角色管理 | 后台 |
| 内容管理 | 栏目树、文章（草稿/发布）、产品分类、产品 | 后台 |
| 系统 | 数据字典、操作日志、文件上传 | 后台 |
| 前台展示 | 首页聚合、栏目树、文章、产品 | 无 |

> 完整可勾选清单见 [docs/coverage/bj-01-cms-coverage.md](../docs/coverage/bj-01-cms-coverage.md)。

---

## 数据库表

| 表 | 说明 | 迁移脚本 |
|----|------|---------|
| `admin` | 管理员 | V1 |
| `role` / `admin_role` | 角色 + 关联 | V2 |
| `category` | 栏目（树形） | V3 |
| `article` | 文章 | V4 |
| `product_category` / `product` | 产品分类（树形）/ 产品 | V5 |
| `operation_log` | 操作日志 | V6 |
| `dict_data` | 数据字典 | V7 |

---

## 本项目的参考价值

- **分层架构样板**：`controller → service → mapper` + `common` 公共层，后续项目直接套用。
- **可复用组件**：`R`、`GlobalExceptionHandler`、`TokenService` 等已登记在 [docs/components-index.md](../docs/components-index.md)。
- **工程规范落地**：Flyway 迁移、SpringDoc、AOP 日志、统一异常、统一返回的完整示例。

---

## 已知待办

- 核心 Service 单元测试待补（JUnit 5 + Mockito），见覆盖清单「测试计划」。

## 相关文档

- [知识地图与进度](../.cursor/rules/knowledge-map.mdc)
- [速查卡片](../docs/cheatsheet/bj-01-cms-cheatsheet.md)
- [功能覆盖清单](../docs/coverage/bj-01-cms-coverage.md)