# 04 Optional：安全地处理"可能为空"

## 通俗理解

`Optional` 是一个**包装盒**，专门装"可能有值、也可能没值"的东西。它逼你在取用之前**先想清楚为空怎么办**，避免空指针（`NullPointerException`）到处炸。

```java
// 以前：手动判空，容易漏
String name = user != null ? user.getName() : "未知";

// Optional：把"判空逻辑"交给它
String name = Optional.ofNullable(user)
        .map(User::getName)
        .orElse("未知");
```

## 核心方法

| 方法 | 作用 |
|------|------|
| `Optional.of(x)` | 装一个肯定非空的值，为空抛异常 |
| `Optional.ofNullable(x)` | 装一个可能为空的值 |
| `.map(x -> ...)` | 有值就转换，没值就不做 |
| `.orElse(default)` | 有值返回值，没值返回默认值 |
| `.orElseGet(() -> ...)` | 没值才执行"懒加载"的默认值 |
| `.orElseThrow(() -> ...)` | 没值就抛指定异常 |
| `.isPresent()` | 是否有值 |
| `.ifPresent(x -> ...)` | 有值才执行 |

## 代码示例

```java
// 从配置取一个可能没有的值
String theme = Optional.ofNullable(config.get("theme"))
        .orElse("default-theme");

// 业务查询：查不到就抛业务异常
Admin admin = Optional.ofNullable(adminMapper.selectById(id))
        .orElseThrow(() -> new BusinessException("管理员不存在"));

// 链式取嵌套属性，全程安全
String city = Optional.ofNullable(user)
        .map(User::getAddress)
        .map(Address::getCity)
        .orElse("未知城市");
```

## orElse vs orElseGet

```java
// orElse：无论有没有值都会先算出默认值（可能浪费）
String a = opt.orElse(expensiveCompute());

// orElseGet：只有没值时才计算（懒加载）
String b = opt.orElseGet(() -> expensiveCompute());
```

## 设计意图

- 用类型系统提示"这里可能为空"，把判空从"到处 if 判断"集中到一处
- 链式 `.map` 避免嵌套 if，代码更清晰

## 常见坑

- ❌ 对 Optional 本身调用 `get()` 而没判空 → 相当于没解决问题
- ❌ 把 Optional 当作方法参数传 → 违反设计意图，直接传可空对象更简单
- ❌ Optional 用于字段/集合，而不是返回值 → Optional 主要设计用于**返回值**
- ✅ 频繁判空返回默认值/抛异常，优先考虑 Optional

## 自测题

1. `orElse` 和 `orElseGet` 的区别是什么？
2. 链式 `Optional.ofNullable(x).map(...).map(...)` 中任何一环为空会怎样？
3. `orElseThrow` 适合什么场景？

## 对照实战

在 `bj-01-cms` 的 Service 里搜 `Optional`，看它怎么用 `orElseThrow` 处理"查不到就抛业务异常"（比如查询管理员、栏目详情）。