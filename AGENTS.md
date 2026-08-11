# backend-journey

## 项目概述

通过一系列真实业务项目，系统掌握 Java 后端开发。面向编程初学者。

## 技术栈

- Java 17、Spring Boot 3.2+、Maven、MyBatis-Plus
- MySQL 8.0、Redis、Elasticsearch
- JUnit 5、Mockito、AssertJ
- SpringDoc OpenAPI、Flyway

## 目录结构

```
backend-journey/
├── bj-01-cms/              # 项目一：企业官网+CMS（内容型业务）
├── bj-02-ecommerce/        # 项目二：电商系统（交易型业务）
├── bj-03-community/        # 项目三：社区/论坛（社交型业务）
├── docs/cheatsheet/        # 跨项目速查卡片索引
├── .cursor/rules/          # Cursor 规则文件
│   ├── core.mdc            # 核心规则 + 自适应协议（alwaysApply）
│   ├── knowledge-map.mdc   # 知识地图 + 进度追踪（alwaysApply）
│   ├── roadmap.mdc         # 项目路线图（alwaysApply）
│   ├── coding-standards.mdc # 编码规范（*.java 触发）
│   ├── api-design.mdc      # API 设计规范（*Controller.java 触发）
│   ├── testing.mdc         # 测试规范（*Test.java 触发）
│   └── evolution.mdc       # 版本演进规范（*.sql 等触发）
└── AGENTS.md               # 本文件
```

## 关键入口

- Cursor 规则：`.cursor/rules/core.mdc` 是核心入口
- 知识地图与进度追踪：`.cursor/rules/knowledge-map.mdc`
- 项目路线图：`.cursor/rules/roadmap.mdc`
- 速查卡片：`docs/cheatsheet/INDEX.md`

## 设计决策

- **多项目自包含**：每个项目独立 Spring Boot 工程，不共享代码模块，可单独参考和部署
- **业务驱动学习**：按业务类型（内容型→交易型→社交型）递进，不是技术点堆砌
- **自适应协议**：前期慢（语法+对比），后期快（直接方案+代码），AI 根据进度自动调整

## 用户偏好与长期约束

- 所有项目以能写入简历为标准，不做玩具 demo
- 用通俗语言和生活化比喻讲解 Java 概念，不依赖其他语言经验
- 中文注释和解释
- Maven 构建，不用 Gradle
- Java 17 上限
