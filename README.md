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
├── docs/cheatsheet/        # 跨项目速查卡片
├── .cursor/rules/          # Cursor AI 规则（8条）
├── .gitignore
├── AGENTS.md               # Cursor 代理说明
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

每个后端项目都有对应的前端实现，使用 Vue 3 + Vite + TypeScript：

| 后端项目 | 前端项目 |
|---------|---------|
| bj-01-cms | [pj-01-cms-frontend](https://github.com/readant/frontend-journey/tree/project/cms-frontend) |
| bj-02-ecommerce | pj-02-ecommerce-frontend（待创建） |
| bj-03-community | pj-03-community-frontend（待创建） |
| bj-04-saas | pj-04-saas-frontend（待创建） |

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

采用 Conventional Commits 规范，按阶段和功能拆分提交：

```
<type>(<scope>): <description>
```

示例：

```
feat(phase1): 骨架搭建 - Maven + Spring Boot + 公共基础 + Health API
feat(phase2): 管理员与权限 - CRUD + BCrypt登录 + RBAC角色权限
docs: 更新项目 README 和速查卡片
```

---

## 学习进度

详见 [知识地图](.cursor/rules/knowledge-map.mdc) 和 [速查卡片](docs/cheatsheet/INDEX.md)。

---

<div align="center">

学无止境，一起努力。如果觉得有帮助，点个 Star 鼓励一下吧 :)

_Last updated: 2026-08-12_

</div>