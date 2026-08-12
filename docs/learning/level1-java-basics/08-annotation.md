# 08 注解：贴标签的便利贴

## 通俗理解

**注解（Annotation）** 就像贴在代码上的**便利贴**，告诉框架"这段代码有特殊用途"。框架看到便利贴后，会自动帮我们做对应的事情。

```java
@Data               // 贴标签：自动生成 getter/setter/toString
@RestController      // 贴标签：告诉 Spring 这是一个处理网页请求的类
```

## 注解不是代码，是"元数据"

注解本身**不写逻辑**，它只是信息（metadata）。真正干活的是**读注解的框架**。框架在运行时用反射去看"哪个类贴了哪个标签"，然后执行对应逻辑。

| 注解 | 谁在读它 | 干了什么 |
|------|---------|---------|
| `@Data` | Lombok | 编译时生成 getter/setter |
| `@RestController` | Spring | 注册为 Web 控制器、接收 HTTP 请求 |
| `@Override` | 编译器 | 校验确实是重写了父类/接口方法 |
| `@Deprecated` | 编译器/工具 | 标记过时，调用会警告 |

## 实战常见注解（项目一）

```java
@SpringBootApplication       // 启动类：开启自动配置 + 组件扫描
@RestController              // 声明这是返回 JSON 的接口控制器
@RequestMapping("/api/v1/admins")  // 类级路径前缀
@RequiredArgsConstructor      // Lombok：根据 final 字段生成构造方法（依赖注入用）
@Slf4j                       // Lombok：自动生成 log 日志对象
@TableName("admin")          // MyBatis-Plus：类映射到哪张表
@ControllerAdvice            // 全局异常处理
@Valid / @NotBlank / @Size   // 参数校验
```

## 自定义注解（项目二深入，这里只开个头）

```java
@Target(ElementType.METHOD)     // 用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时仍保留（反射能读到）
public @interface LogOperation {
    String module() default "";
    String action() default "";
}
```

项目一的 `@LogOperation` 就是自定义注解 + AOP 切面实现的操作日志（项目一已用到）。完整反射机制在项目二展开。

## 常见坑

- ❌ 以为注解本身能做逻辑 → 它只是标记，需要配合框架/AOP
- ❌ 忘了 `@Retention(RUNTIME)` → 运行时反射读不到自定义注解
- ✅ 加了 `@Data` 就不要手写 getter/setter（重复且易冲突）
- ✅ 每个 Service/Controller 都加 `@Slf4j`，用 `log.xxx()` 打日志

## 自测题

1. 注解本身能执行业务逻辑吗？真正执行逻辑的是谁？
2. `@Data` 帮我们做了什么？为什么加了它就不用写 getter/setter？
3. 自定义注解想让运行时反射能读到，必须加什么注解？

## 对照实战

打开 `bj-01-cms/src/main/java/com/readant/cms/`，数一数各文件顶部用了哪些注解。重点看 `AdminController`（`@RestController` + `@RequestMapping` + `@RequiredArgsConstructor` + `@Slf4j`）。