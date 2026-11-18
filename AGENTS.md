# backend-journey

## 项目概述

一套**面向公开审查的个人技术作品集**：通过四个真实业务项目展示 Java 后端工程能力。
学习属性保留为附加价值（中文注释、学习导航、通俗解释），但**不以初学者为由降低工程标准**——作品标准由 `.cursor/rules/core.mdc` 的铁律、安全基线和交付门禁定义。

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
├── .cursor/rules/          # 🟦 Cursor Rules（7 个，AI 执行指令层）
│   ├── core.mdc            #    唯一 alwaysApply：专业定位+铁律10条+门禁+资源索引
│   ├── coding-java.mdc     #    **/*.java 触发：编码硬约束（含事务/并发/N+1）
│   ├── api-controller.mdc  #    **/*Controller.java 触发：接口规范+鉴权约定
│   ├── testing-java.mdc    #    **/*Test.java 触发：测试模板+反凑数字
│   ├── security.mdc        #    代码/配置文件触发：鉴权/密钥/校验/脱敏/上传
│   ├── quality-gate.mdc    #    声称完成时加载：三级门禁+文档举证
│   └── git-workflow.mdc    #    手动加载：Git 分支+提交格式
│   （local.mdc 为本地私有配置，已加入 .gitignore，不在仓库内）
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

- **专业作品标准**（不可豁免）：[.cursor/rules/core.mdc](.cursor/rules/core.mdc) 铁律 10 条 + [security.mdc](.cursor/rules/security.mdc) 安全基线 + [quality-gate.mdc](.cursor/rules/quality-gate.mdc) 三级门禁（core.mdc 与 local.mdc 为常驻规则，其余按 glob/描述触发）
- **进度追踪**（AI 自动维护，人类只读）：[docs/ai-context/progress-tracker.md](docs/ai-context/progress-tracker.md)
- **AI 参考资料库**（AI 按需读取）：[docs/ai-context/](docs/ai-context/)（知识地图 + 四项目路线图）
- **工程规范**（人类定义，AI 遵守）：[docs/standards/version-evolution.md](docs/standards/version-evolution.md)（API 版本、Flyway、CHANGELOG、高风险变更确认机制）
- **用户学习笔记**（人类主用）：[docs/learning/README.md](docs/learning/README.md)（语法学习路径）
- **速查卡片**：[docs/cheatsheet/INDEX.md](docs/cheatsheet/INDEX.md)

## 设计决策

- **多项目自包含**：每个项目独立 Spring Boot 工程，不共享代码模块，可单独参考和部署
- **通用用库，业务手写**：鉴权/工具/Excel/调度等成熟库直接用；业务建模与核心流程必须自己写
- **业务驱动**：按业务类型（内容型→交易型→社交型）递进，不是技术点堆砌
- **自适应协议**：只调整解释详略，不影响铁律、安全基线与交付门禁

## 配套前端项目

每个后端项目都有对应的前端实现，使用 Vue 3 + Vite + TypeScript：

| 后端项目 | 前端应用 | GitHub 分支 |
|---------|---------|------------|
| bj-01-cms | [pj-01-cms-admin](https://github.com/readant/frontend-journey/tree/project/cms-frontend)（管理后台）、[pj-01-cms-portal](https://github.com/readant/frontend-journey/tree/project/cms-frontend)（前台门户） | [project/cms-frontend](https://github.com/readant/frontend-journey/tree/project/cms-frontend) |

> 前端仓库本地路径等私有信息见 `docs/standards/local-paths.md`（已加入 .gitignore，仅本地可见）

## 代码托管仓库（双仓库镜像）

| 平台 | 地址 | 角色 |
|------|------|------|
| GitHub | https://github.com/readant/backend-journey | 主仓库（fetch + push ①） |
| Gitee | https://gitee.com/readant/backend-journey | 镜像仓库（push ②） |

- `origin` 配置了两个 push URL：一次 `git push origin <branch>` 自动推送到两端
- 推送流程、配置依据与风险提示见 `.cursor/rules/git-workflow.mdc` 零.5 节

## 用户偏好与长期约束

- **作品以"陌生人审查挑不出硬伤"为标准**，不做玩具 demo，不以"学习项目"为由豁免质量
- **改仓前先出方案 → 用户点头 → 才动手**；选定方向不等于授权动手
- 文档里的 ✅ 必须有代码/测试/运行结果佐证，禁止自报验收
- 新增依赖、公共组件接口变更等属于高风险变更，先报方案再改
- 用通俗语言讲解概念，但不以讲解代替验证；中文注释和解释
- Maven 构建，不用 Gradle
- Java 17 上限
- **Git 分支规范（GitHub Flow）**：禁止直接在 `main` 提交，`main` 永远保持可发布、干净；所有改动必须从 `main` 拉出 `feature/fix/docs` 分支，用 `--no-ff` 合回并删除分支。完整流程见 `.cursor/rules/git-workflow.mdc`
