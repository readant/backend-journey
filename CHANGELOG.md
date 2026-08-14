# Changelog

本文件记录仓库所有重要变更，格式遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [Unreleased]

### Added

- 新增 `DashboardService`，数据看板统计逻辑从 Controller 下沉到 Service 层
- 补齐单元测试：`R`、`GlobalExceptionHandler`、`DictDataService`、`ProductService`、`OperationLogService`、`DashboardService`

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
