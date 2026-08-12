# 03 Lambda 与 Stream：把集合当流水线处理

## 通俗理解

- **Lambda（`->`）**：一种简写"一段代码/一个行为"的方式，可以当作参数传给别人。
- **Stream**：把集合变成**流水线**，数据从一头进，经过一个个"加工环节"（过滤、映射、排序、汇总），从另一头出。

## Lambda 是什么

```java
// 传统写法：匿名内部类
Comparator<String> c1 = new Comparator<>() {
    public int compare(String a, String b) { return a.length() - b.length(); }
};

// Lambda 简写：去掉样板代码
Comparator<String> c2 = (a, b) -> a.length() - b.length();
```

语法：`参数 -> 行为`

| 场景 | Lambda |
|------|--------|
| 一个参数 | `x -> x * 2` |
| 多个参数 | `(a, b) -> a + b` |
| 多行语句 | `x -> { System.out.println(x); return x; }` |

## Stream：集合的流水线

```java
import java.util.List;
import java.util.stream.Collectors;

List<String> names = List.of("张三", "李四", "王五", "张伟");

// 过滤出姓张的，并转成大写
List<String> result = names.stream()
        .filter(n -> n.startsWith("张"))   // 过滤：留下姓张的
        .map(n -> n + "先生")              // 映射：每个转成新值
        .collect(Collectors.toList());      // 收集成 List

System.out.println(result);  // [张三先生, 张伟先生]
```

## 常用 Stream 操作

```java
// 过滤
list.stream().filter(x -> x > 10)

// 映射（类型转换）
list.stream().map(Admin::getUsername)   // 对象列表 → 用户名列表

// 排序
list.stream().sorted((a, b) -> b - a)

// 去重
list.stream().distinct()

// 统计
list.stream().count()
list.stream().max(Comparator.naturalOrder())

// 匹配
boolean any = list.stream().anyMatch(x -> x > 10)   // 是否有 >10 的

// 收集成各种结构
list.stream().collect(Collectors.toList())    // List
list.stream().collect(Collectors.toSet())     // Set
list.stream().collect(Collectors.joining(",")) // 拼接字符串
```

## 实战常见模式

```java
// 实体列表 → VO 列表
List<AdminVO> vos = entityList.stream()
        .map(e -> toVO(e))
        .collect(Collectors.toList());

// 过滤出某个状态
List<Article> published = articles.stream()
        .filter(a -> a.getStatus() == ArticleStatus.PUBLISHED)
        .collect(Collectors.toList());
```

## 常见坑

- ❌ Lambda 里用到外部变量时，该变量必须是 `final` 或"不可变"的（effectively final）
- ❌ 忘了 `.collect(...)`，Stream 是惰性的，不收集/不终止就不真正执行
- ⚠️ 数据量小时用 Stream 提升可读性；追求极致性能时可能不如传统 for
- ✅ `map`（转换）和 `filter`（过滤）是最常用的两个，先掌握它们

## 自测题

1. `map` 和 `filter` 分别做什么？
2. Stream 为什么必须 `.collect()` 或其它终止操作？
3. 把一个 `List<Admin>` 转成 `List<String>`（取 username）用 Stream 怎么写？

## 对照实战

在 `bj-01-cms` 的 Service 实现里搜 `.stream()`，看实体转 VO、过滤列表时怎么用 Lambda 和 Stream 的。