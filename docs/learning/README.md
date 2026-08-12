# Java 语法学习导航

> 本目录是「零基础 → 能看懂项目代码」的语法阶梯。
> 它与 `knowledge-map.mdc` 的知识地图一一对应，先把语法打牢，再进 `bj-01-cms` 看实战。

---

## 为什么需要这个目录？

实战代码（`bj-01-cms`）能跑、能看，但**如果你是零基础，直接看 Spring Boot 代码会卡在语法上**——不知道 `class`、`implements`、`interface`、泛型、Lambda 到底在说什么。

这个目录把「用到的 Java 语法」按学习次序拆开，每一篇都是：

- **通俗比喻**：不依赖其他语言经验，生活化理解
- **代码示例**：最小可运行片段
- **设计意图**：为什么 Java 要这么设计
- **常见坑**：初学最容易踩的雷
- **自测题**：每个知识点末尾 3~5 题，先自测再看项目代码

---

## 学习路径（强烈建议按顺序）

```
语法基础（level1） → 语言核心（level2） → 项目一实战（bj-01-cms）
```

| 层级 | 目录 | 覆盖范围 | 对应知识地图 |
|------|------|---------|------------|
| 🟢 第一层 | [level1-java-basics](./level1-java-basics/) | 程序结构、类型、类与对象、继承接口、包、枚举、异常、注解、Maven | 知识地图第一层 |
| 🔵 第二层 | [level2-java-core](./level2-java-core/) | 集合、泛型、Lambda、Stream、Optional、时间 API、IO | 知识地图第二层 |
| 🟠 进阶（项目二/三展开） | 反射、自定义注解、并发 | 分别在 bj-02 / bj-03 深入学习 | 知识地图第二层 |

> **先学 level1 全系列 → 再学 level2 全系列**，每个知识点先做末尾自测题，答得上再看 `bj-01-cms` 里对应的实战代码，才算过关。

---

## 第一层：Java 语法基础

| # | 主题 | 知识点（对应知识地图） | 自测通过后去看实战 |
|---|------|----------------------|------------------|
| 1 | [程序结构](./level1-java-basics/01-program-structure.md) | class、main 方法 | `CmsApplication.java` |
| 2 | [变量与类型](./level1-java-basics/02-variables-types.md) | 变量、强类型、基本类型与包装类 | `R.java` 的 `Integer` |
| 3 | [类与对象](./level1-java-basics/03-class-object.md) | 类、对象、构造方法、this/super、static/final | `Admin.java`、`AdminController` |
| 4 | [继承与接口](./level1-java-basics/04-inheritance-interface.md) | extends、implements、抽象类 vs 接口 | `ServiceImpl`、`Service` 接口 |
| 5 | [包与访问修饰符](./level1-java-basics/05-package-modifier.md) | package、import、public/private/protected | `com.readant.cms` 分包 |
| 6 | [枚举](./level1-java-basics/06-enum.md) | enum | 文章状态、日志类型 |
| 7 | [异常处理](./level1-java-basics/07-exception.md) | try-catch-finally、throws、受检 vs 非受检 | `BusinessException` |
| 8 | [注解](./level1-java-basics/08-annotation.md) | @Annotation 基础 | `@RestController`、`@Data` |
| 9 | [Maven 基础](./level1-java-basics/09-maven.md) | pom.xml、依赖管理 | `pom.xml` |

---

## 第二层：Java 语言核心

| # | 主题 | 知识点（对应知识地图） | 自测通过后去看实战 |
|---|------|----------------------|------------------|
| 1 | [集合框架](./level2-java-core/01-collections.md) | List / Map / Set | 各种 `List<...>` 返回 |
| 2 | [泛型](./level2-java-core/02-generics.md) | `<T>` | `R<T>` 统一返回体 |
| 3 | [Lambda 与 Stream](./level2-java-core/03-lambda-stream.md) | `->`、Stream API | 列表映射、过滤 |
| 4 | [Optional](./level2-java-core/04-optional.md) | 空值安全 | 可选字段读取 |
| 5 | [日期时间](./level2-java-core/05-datetime.md) | LocalDateTime | 文章的创建时间字段 |

> 反射、自定义注解、并发（线程池）虽在知识地图第二层，但**首次出现是在项目二/三**，这里不展开，等进入对应项目再学。

---

## 配合使用

- **学习次序**：每篇末尾有「自测题」，全对再进下一篇
- **对照实战**：每篇文末都列出「自测通过后去看实战」的具体文件
- **进度追踪**：`knowledge-map.mdc` 的「知识点掌握情况」表格实时记录已掌握项