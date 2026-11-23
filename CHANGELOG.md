# Changelog

本文件记录仓库所有重要变更，格式遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [Unreleased]

### Added

- 新增 `DashboardService`，数据看板统计逻辑从 Controller 下沉到 Service 层
- 补齐单元测试：`R`、`GlobalExceptionHandler`、`DictDataService`、`ProductService`、`OperationLogService`、`DashboardService`
- 新增 `Pages` 分页参数收敛工具，单页条数上限 100
- 登录返回体新增 `expiresIn`（令牌有效期秒数）
- 新增 `.mvn/jvm.config`，Maven 统一以 UTF-8 运行

### Fixed

- 访问不存在的路径返回 500 → 正确返回 404（`NoResourceFoundException` 单独处理）
- 前台门户请求 `/api/v1/product-categories/tree` 被鉴权拦截返回 401 → 只读分类树放行
- 文件上传因相对路径解析到 Tomcat 临时目录而必然失败 → 上传与静态映射统一使用绝对路径
- Windows 控制台中文日志乱码 → Maven 与测试 JVM 编码统一为 UTF-8

### Security

- Token 增加有效期（默认 2 小时）并定期清理过期条目，避免内存只增不减
- 文件上传增加扩展名白名单（jpg/jpeg/png/gif/webp/pdf），阻断上传 HTML/SVG 造成的存储型 XSS
- 分页参数增加上限保护，防止 `pageSize` 过大拖垮数据库
- 补充 CORS 白名单配置，未配置来源时不放开跨域

### Changed

- 统一响应增加 404 语义，前后端对"接口不存在"的判定不再依赖 500
- 文档品牌名统一为「雄心工作室」，README 项目状态表述改为「已完成 1 个 + 规划 3 个」

## [1.0.0] - 2026-08-14

项目一（bj-01-cms）首个稳定版本：企业官网 + CMS 内容管理系统全部功能交付。

### Added

- **骨架搭建（Phase 1）**：Maven 多环境配置、统一返回体 `R<T>`、全局异常处理、健康检查接口
- **管理员与权限（Phase 2）**：管理员 CRUD、BCrypt 密码加密、登录 Token（内存模式）、角色表 + 管理员-角色关联（RBAC 基础）
- **内容管理（Phase 3）**：多级栏目树、文章 CRUD（草稿/发布状态机）、富文本存储、封面图上传、产品分类树 + 产品 CRUD、前台无鉴权展示接口
- **完善收尾（Phase 4）**：AOP 操作日志、SpringDoc OpenAPI 接口文档、数据字典、MyBatis-Plus 自动填充 + 分页插件、静态资源映射
- **数据库迁移**：Flyway 脚本 V1~V7 + 种子数据（默认管理员、演示内容）
- **单元测试**：`AdminService`、`ArticleService`、`CategoryService`、`RoleService`、`TokenService` 核心分支覆盖
- **学习资料**：Java 语法学习笔记（level1~level2）、项目速查卡片、功能覆盖清单、可复用组件索引
