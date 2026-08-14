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
│
├── .cursor/rules/          # 🟦 Cursor Rules（仅 5 个，AI 执行指令层）
│   ├── core.mdc            #    唯一 alwaysApply：身份+进度指针+铁律+资源索引（~50行）
│   ├── coding-java.mdc     #    **/*.java 触发：Java 编码硬约束
│   ├── api-controller.mdc  #    **/*Controller.java 触发：接口规范
│   ├── testing-java.mdc    #    **/*Test.java 触发：测试模板
│   └── git-workflow.mdc    #    手动加载：Git 分支+提交格式
│
├── docs/                   # 📚 参考文档层（按读写权限分区，见下方关键入口）
│   ├── ai-context/         #    🟠 AI 专属区：progress-tracker / knowledge-map / project-roadmap（人类只读，别手动改）
│   ├── standards/          #    🔵 工程规范区：版本演进/Flyway/CHANGELOG
│   ├── learning/           #    🟢 人类学习区：level1~level2 语法学习笔记
│   ├── cheatsheet/         #    🟢 速查卡片区
│   ├── coverage/           #    🟠 测试报告区（AI 生成）
│   └── tools/              #    🟢 工具配置区
│
└── AGENTS.md               # 本文件
```

## 关键入口

- **Cursor 规则核心入口**（AI 的常驻工作记忆）：[.cursor/rules/core.mdc](.cursor/rules/core.mdc)（唯一 alwaysApply，其他规则 glob 自动触发）
- **学习进度追踪**（AI 自动维护，人类只读）：[docs/ai-context/progress-tracker.md](docs/ai-context/progress-tracker.md)
- **AI 参考资料库**（AI 按需读取）：[docs/ai-context/](docs/ai-context/)（知识地图 + 四项目路线图）
- **工程规范**（人类定义，AI 遵守）：[docs/standards/version-evolution.md](docs/standards/version-evolution.md)（API 版本、Flyway、CHANGELOG、高风险变更确认机制）
- **用户学习笔记**（人类主用）：[docs/learning/README.md](docs/learning/README.md)（语法学习路径）
- **速查卡片**：[docs/cheatsheet/INDEX.md](docs/cheatsheet/INDEX.md)

## 设计决策

- **多项目自包含**：每个项目独立 Spring Boot 工程，不共享代码模块，可单独参考和部署
- **业务驱动学习**：按业务类型（内容型→交易型→社交型）递进，不是技术点堆砌
- **自适应协议**：前期慢（语法+对比），后期快（直接方案+代码），AI 根据进度自动调整

## 配套前端项目

每个后端项目都有对应的前端实现，使用 Vue 3 + Vite + TypeScript：

| 后端项目 | 前端应用 | GitHub 分支 |
|---------|---------|------------|
| bj-01-cms | [pj-01-cms-admin](https://github.com/readant/frontend-journey/tree/project/cms-frontend)（管理后台）、[pj-01-cms-portal](https://github.com/readant/frontend-journey/tree/project/cms-frontend)（前台门户） | [project/cms-frontend](https://github.com/readant/frontend-journey/tree/project/cms-frontend) |

> 前端仓库本地路径等私有信息见 `docs/standards/local-paths.md`（已加入 .gitignore，仅本地可见）

## 用户偏好与长期约束

- 所有项目以能写入简历为标准，不做玩具 demo
- 用通俗语言和生活化比喻讲解 Java 概念，不依赖其他语言经验
- 中文注释和解释
- Maven 构建，不用 Gradle
- Java 17 上限
- **Git 分支规范（GitHub Flow）**：禁止直接在 `main` 提交，`main` 永远保持可发布、干净；所有改动必须从 `main` 拉出 `feature/fix/docs` 分支，用 `--no-ff` 合回并删除分支。完整流程见 `.cursor/rules/git-workflow.mdc`
