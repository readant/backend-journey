<div align="center">

# Backend Journey

### 从零开始的 Java 后端学习之旅

![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis_Plus-3.5-red?style=flat-square)
![Flyway](https://img.shields.io/badge/Flyway-9.22-CC0200?style=flat-square)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=flat-square&logo=junit5&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

---

通过一系列真实业务项目，系统掌握 Java 后端开发。

从零开始，一步步搭建企业级应用。

> 🎯 **前后端配套学习**：每个后端项目都有对应的 Vue 3 前端实现（一个后端可能对应「管理后台 + 前台门户」多个应用），前后端联动，完整还原真实项目开发流程。前端仓库见文末「[配套前端项目](#配套前端项目)」。

</div>

---

## 项目路线

四个项目按业务类型递进，每个项目自包含，可独立参考和部署。

| 项目 | 业务类型 | 状态 | 学习重点 |
|------|---------|------|---------|
| [bj-01-cms](./bj-01-cms/) | 企业官网 + CMS | ✅ 已完成 | Java 语法、Spring Boot 入门、分层架构 |
| bj-02-ecommerce | B2C 电商 | ⬜ 待开始 | 缓存/安全/事务、设计模式、第三方集成 |
| bj-03-community | 社区/论坛 | ⬜ 待开始 | 社交/消息队列、Feed 流设计、异步解耦 |
| bj-04-saas | SaaS 工作台 | ⬜ 待开始 | 多租户架构、Docker 部署、完整 RBAC |

---

## 技术栈

| 类别 | 技术 |
|------|------|
| **语言** | Java 17 |
| **框架** | Spring Boot 3.2+, MyBatis-Plus 3.5+ |
| **数据库** | MySQL 8.0, Flyway 9.22 |
| **缓存/搜索** | Redis, Elasticsearch（后续项目引入） |
| **文档/测试** | SpringDoc OpenAPI, JUnit 5, Mockito, AssertJ |
| **构建** | Maven 3.9+ |

---

## 仓库结构

```
backend-journey/
├── bj-01-cms/              # 项目一：企业官网 + CMS（已完成）
├── bj-02-ecommerce/        # 项目二：电商系统（待创建）
├── bj-03-community/        # 项目三：社区/论坛（待创建）
├── bj-04-saas/             # 项目四：SaaS 工作台（待创建）
├── docs/ai-context/        # AI 专属区：进度追踪/知识地图/项目路线图
├── docs/standards/         # 工程规范：版本演进/Flyway/CHANGELOG
├── docs/learning/          # Java 语法学习导航（零基础 → 能看代码）
├── docs/cheatsheet/        # 跨项目速查卡片
├── docs/coverage/          # 测试报告（AI 生成）
├── docs/tools/             # 工具配置
├── .cursor/rules/          # Cursor AI 规则（5 条，核心入口 core.mdc）
├── .gitattributes          # 统一仓库行尾为 LF
├── .gitignore
├── AGENTS.md               # Cursor 代理说明
├── CHANGELOG.md            # 版本变更记录
└── README.md               # 本文件
```

---

## 设计原则

1. **多项目自包含** — 每个项目独立 Spring Boot 工程，不共享代码模块，可单独参考和部署
2. **业务驱动学习** — 按业务类型（内容型 → 交易型 → 社交型）递进，不是技术点堆砌
3. **分层架构** — controller → service → mapper，各层职责分明
4. **测试同步** — 核心 Service 必须配套 JUnit 5 单元测试
5. **统一返回** — 所有接口返回 `R` 对象（code + msg + data）

---

## 配套前端项目

每个后端项目都有对应的前端实现，使用 Vue 3 + Vite + TypeScript，与后端接口真实联调。一个后端项目可能对应多个前端应用（管理后台 + 前台门户）：

| 后端项目 | 前端应用 |
|---------|---------|
| bj-01-cms | [pj-01-cms-admin（管理后台）](https://github.com/readant/frontend-journey/tree/project/cms-frontend)、[pj-01-cms-portal（前台门户）](https://github.com/readant/frontend-journey/tree/project/cms-frontend) |
| bj-02-ecommerce | pj-02-ecommerce-frontend（待创建） |
| bj-03-community | pj-03-community-frontend（待创建） |
| bj-04-saas | pj-04-saas-frontend（待创建） |

> 前端学习同样遵循「项目驱动」路线，按业务类型递进，可与本仓库各后端项目一一对应。

---

## 快速开始

```bash
# 克隆仓库
git clone https://github.com/readant/backend-journey.git
cd backend-journey/bj-01-cms

# 编译
mvn compile

# 启动（需要本地 MySQL，配置见 application.yml）
mvn spring-boot:run

# 访问 API 文档
open http://localhost:8080/swagger-ui.html
```

---

## Git 提交规范

采用 Conventional Commits + GitHub Flow 分支模型，按阶段和功能拆分提交：

```
<type>(<scope>): <description>
```

**分支规范（强制）**：禁止直接在 `main` 提交。所有改动先从 `main` 拉出分支，用 `--no-ff` 合回后删除：

```bash
git checkout main && git pull
git checkout -b feature/xxx        # 或 fix/xxx、docs/xxx
# ...开发并拆分提交...
git checkout main
git merge --no-ff feature/xxx -m "Merge branch 'feature/xxx' into main"
git push origin main
git branch -d feature/xxx
```

示例：

```
feat(phase1): 骨架搭建 - Maven + Spring Boot + 公共基础 + Health API
feat(phase2): 管理员与权限 - CRUD + BCrypt登录 + RBAC角色权限
docs: 更新项目 README 和速查卡片
```

---

## 学习进度

- **零基础入门**：先走 [Java 语法学习导航](docs/learning/README.md)（语法层），再看实战代码
- **进度追踪**：详见 [知识地图](docs/ai-context/knowledge-map.md) 和 [速查卡片](docs/cheatsheet/INDEX.md)

---

<div align="center">

学无止境，一起努力。如果觉得有帮助，点个 Star 鼓励一下吧 :)

_Last updated: 2026-08-14_

</div>