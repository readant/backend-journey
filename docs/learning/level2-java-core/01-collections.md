# 01 集合框架：数据的收纳箱

## 通俗理解

集合就是各种**收纳箱**，用来装一组对象。数组长度固定、不好增删，集合解决了这些问题。

最常用的是三大接口：
- **List**：有序、可重复，像排队（按顺序，能重复）
- **Set**：无序、不可重复，像一张记名名单（同一人只记一次）
- **Map**：键值对，像字典（查一个词 → 得到解释）

---

## List：有序、可重复

```java
import java.util.ArrayList;
import java.util.List;

List<String> names = new ArrayList<>();
names.add("张三");
names.add("李四");
names.add("张三");      // 可重复

String first = names.get(0);   // 按下标取：张三
names.size();                   // 3
names.remove(1);                // 按下标删

// 遍历
for (String n : names) {
    System.out.println(n);
}
```

> `ArrayList` 是最常用的 List 实现，底层是可变数组。

## Set：无序、不可重复

```java
import java.util.HashSet;
import java.util.Set;

Set<String> roles = new HashSet<>();
roles.add("admin");
roles.add("user");
roles.add("admin");     // 重复，不会被加进去

System.out.println(roles.size());  // 2，而不是 3
```

> `HashSet` 基于哈希，`contains()` 查得飞快，适合"去重 + 判断是否存在"。

## Map：键值对

```java
import java.util.HashMap;
import java.util.Map;

Map<String, Integer> score = new HashMap<>();
score.put("张三", 90);
score.put("李四", 85);
score.put("张三", 95);      // 键相同则覆盖 → 张三变成 95

int zs = score.get("张三");  // 95
boolean has = score.containsKey("王五");  // false

// 遍历
for (Map.Entry<String, Integer> e : score.entrySet()) {
    System.out.println(e.getKey() + " = " + e.getValue());
}
```

## 实战中常见用法

```java
// 返回列表：Controller 返回 List<ArticleVO>
return adminService.listAll();

// 分页查询
IPage<Article> page = articleService.page(new Page<>(1, 10));
List<Article> records = page.getRecords();
```

## 常见坑

- ❌ 忘记 `import java.util.*` → 编译报错
- ❌ Map 用 `get()` 取不存在的键返回 `null` → 可能空指针，用 `containsKey` 或 `getOrDefault`
- ✅ List 用 `get(下标)`，Set/Map 用遍历或 `contains`
- ⚠️ 循环里不要用 `list.remove()`，容易出错，用迭代器或 Stream 过滤

## 自测题

1. List 和 Set 的核心区别是什么？各自适用场景？
2. Map 用什么方法判断某个键是否存在？
3. `score.put("张三", 95)` 当"张三"已存在时会发生什么？

## 对照实战

在 `bj-01-cms` 里搜 `List<` 和 `Map<`，看返回列表和返回键值对的地方怎么用集合的。