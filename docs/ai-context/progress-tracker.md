# 学习进度追踪

> ⚠️ 本文件由 AI 自动维护，人类不要手动编辑。进度不准请直接在对话中告诉 AI。

---

## 当前状态

- **当前项目**：bj-01-cms（兴华小组官网）
- **当前阶段**：阶段一（语法锚定期）
- **已完成功能**：F1.1 ~ F4.4 全部完成（骨架/管理员权限/内容管理核心/完善收尾）

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
