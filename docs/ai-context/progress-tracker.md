# 学习进度追踪

> ⚠️ 本文件由 AI 自动维护，人类不要手动编辑。进度不准请直接在对话中告诉 AI。

---

## 当前状态

- **当前项目**：bj-01-cms（兴华小组官网）
- **当前阶段**：作品达标期（从"功能完成"转向"专业成熟"）
- **已完成功能**：F1.1 ~ F4.4 全部完成（骨架/管理员权限/内容管理核心/完善收尾）
- **工程标准**：`.cursor/rules/core.mdc` 铁律 10 条 + `security.mdc` 安全基线 + `quality-gate.mdc` 三级门禁，全项目统一、不因阶段豁免
- **阶段用语说明**：文档中的"阶段一/二/三"仅表示**解释详略**（见 core.mdc 自适应协议），不代表质量标准高低
- **下一动作**：按 P0 → P1 → P2 分批补齐 bj-01-cms 的达标缺口（清单见下方），校正 `docs/coverage/` 中的失真验收项

---

## bj-01-cms 达标缺口清单（2026-11-18 建立）

> P0（鉴权接线 + 密码外置）已完成。下表为剩余缺口，按新规则逐条对照。

### P1 — 违反铁律 / 文档失真

| # | 缺口 | 证据位置 | 对应新规则 |
|---|---|---|---|
| 1 | 6 个 Controller 直接收/返 Entity | Category / DictData / ProductCategory / Product / Role / OperationLog | 铁律 2 |
| 2 | 上述接口无 `@Valid`，Entity 无校验注解 | 同上 | 铁律 3 / security #3 |
| 3 | `pageSize` 无 `@Max(100)` | Article / Product / OperationLog 的 page() | api-controller #4 |
| 4 | 6 处文档虚假 ✅ | `docs/coverage/bj-01-cms-coverage.md` | 铁律 6 |
| 5 | 角色 CRUD 缺失（文档声称有） | RoleController 仅 list + 分配/移除 | 铁律 6 |
| 6 | 文章无标题过滤（文档声称有） | ArticleServiceImpl.page() | 铁律 6 |
| 7 | 草稿凭 ID 可直接访问 | SiteController.articleDetail() | security #1 |
| 8 | `R<Map>` / `R<?>` 通配返回 | DashboardController / SiteController | 铁律 2 |
| 9 | FileController 直接做文件 IO | FileController | 铁律 1 |
| 10 | 2 个 Service 缺 `@Slf4j` | DashboardServiceImpl / OperationLogServiceImpl | coding-java #2 |
| 11 | ProductCategoryServiceImpl 无测试 | `src/test/` 无对应类 | testing #1 |

### P2 — 质量隐患

| # | 缺口 | 对应新规则 |
|---|---|---|
| 1 | 写操作普遍无 `@Transactional` | 铁律 4 |
| 2 | `BeanUtils.copyProperties` 把 null 覆盖到状态字段，响应与落库不一致 | coding-java #8 |
| 3 | 删除栏目/分类不检查引用 → 孤儿数据 | security #5 |
| 4 | N+1 查询（文章→栏目、首页→产品） | coding-java #7 |
| 5 | 日志切面：失败操作不记录 / adminId 恒 null / targetId 取错 | security #6 |
| 6 | 富文本未清洗、上传扩展名未白名单 | security #3 |
| 7 | token 内存 Map 无过期、无清理、重启失效 | security #1 |
| 8 | 0 个 Web 层 / 集成测试，测不到鉴权与校验缺陷 | testing #1.5 |

### 建议批次

- **批次 A（低风险先行）**：P1 #4~#7、#10、#11 — 文档校正 + 草稿可见性 + 补测试，不改 API 契约
- **批次 B（API 契约变更）**：P1 #1~#3、#8、#9 — DTO/VO 全量化 + `@Valid` + 分页上限，需前端同步适配
- **批次 C（结构性重构）**：P2 全部 — 事务、N+1、日志切面、富文本安全，逐项先出方案

每批执行前先出文件级方案，用户确认后动手。

---

## 知识点掌握情况

> 状态标记：⬜ 未开始 | 🔄 本次学习中 | ✅ 已掌握（能独立使用） | 🔁 需复习

| 知识点                                | 状态      | 首次出现 | 关联功能                                |
| ------------------------------------- | --------- | -------- | --------------------------------------- |
| 程序结构（class、main 方法）          | ✅ 已掌握 | 项目一   | CmsApplication 启动类                   |
| Maven 基础（pom.xml、依赖管理）       | ✅ 已掌握 | 项目一   | pom.xml 依赖配置                        |
| 包管理（package / import）            | ✅ 已掌握 | 项目一   | com.readant.cms 分包结构                |
| 注解（@Annotation）基础               | ✅ 已掌握 | 项目一   | @SpringBootApplication、@RestController |
| 泛型（`<T>`）                         | ✅ 已掌握 | 项目一   | R<T> 统一返回体                         |
| 异常处理（try-catch-finally、throws） | ✅ 已掌握 | 项目一   | BusinessException、全局异常处理         |
| Spring Boot 启动原理                  | ✅ 已掌握 | 项目一   | @SpringBootApplication                  |
| @RestController / @RequestMapping     | ✅ 已掌握 | 项目一   | HealthController、AdminController       |
| 全局异常处理（@ControllerAdvice）     | ✅ 已掌握 | 项目一   | GlobalExceptionHandler                  |
| 配置文件（application.yml）           | ✅ 已掌握 | 项目一   | application.yml 数据源配置              |
| Flyway 数据库迁移                     | ✅ 已掌握 | 项目一   | V1**init_schema.sql、R**seed_admin.sql  |
| MyBatis-Plus CRUD（BaseMapper）       | ✅ 已掌握 | 项目一   | AdminMapper、AdminService               |
| @Service / @Component                 | ✅ 已掌握 | 项目一   | AdminServiceImpl                        |
| IoC / DI（依赖注入）                  | ✅ 已掌握 | 项目一   | @RequiredArgsConstructor 构造器注入     |
| 参数校验（jakarta.validation）        | ✅ 已掌握 | 项目一   | AdminCreateReq、@Valid                  |
| DTO / VO 分层                         | ✅ 已掌握 | 项目一   | AdminCreateReq、AdminVO                 |
| BCrypt 密码加密                       | ✅ 已掌握 | 项目一   | AdminServiceImpl 登录校验               |
| Token 鉴权（内存模式）                | ✅ 已掌握 | 项目一   | TokenService、LoginVO                   |
| RBAC 权限模型（基础）                 | ✅ 已掌握 | 项目一   | role 表、admin_role 关联表              |
| 多表关联查询（JOIN）                  | ✅ 已掌握 | 项目一   | RoleServiceImpl 查询管理员角色          |
| MyBatis-Plus 分页插件                 | ✅ 已掌握 | 项目一   | ArticleServiceImpl 分页查询             |
| LambdaQueryWrapper 条件构造器         | ✅ 已掌握 | 项目一   | ArticleServiceImpl 动态查询             |
| 文章发布流程（草稿/发布）             | ✅ 已掌握 | 项目一   | ArticleServiceImpl 状态管理             |
| 文件上传（MultipartFile）             | ✅ 已掌握 | 项目一   | FileController 封面图上传               |
| 静态资源映射                          | ✅ 已掌握 | 项目一   | WebMvcConfig 上传文件访问               |
