# 05 日期时间：LocalDateTime

## 通俗理解

`LocalDateTime` 是 Java 8 引入的**线程安全、好用的日期时间类型**，用来代替老旧的 `Date` / `Calendar`（又慢又容易出错）。

- `LocalDate`：只有日期（年-月-日）
- `LocalTime`：只有时间（时:分:秒）
- `LocalDateTime`：日期 + 时间（最常用）

## 创建与格式化

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 当前时间
LocalDateTime now = LocalDateTime.now();

// 指定时间
LocalDateTime t = LocalDateTime.of(2026, 8, 12, 10, 30, 0);

// 格式化输出（重要！存库和展示经常需要）
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String text = now.format(fmt);   // 2026-08-12 10:30:00

// 字符串转回时间
LocalDateTime parsed = LocalDateTime.parse("2026-08-12 10:30:00", fmt);
```

## 常见操作

```java
// 加减
LocalDateTime tomorrow = now.plusDays(1);
LocalDateTime anHourAgo = now.minusHours(1);

// 比较
boolean after = now.isAfter(another);
boolean before = now.isBefore(another);

// 取字段
int year = now.getYear();
int month = now.getMonthValue();  // 1~12
```

## 实战：MyBatis-Plus 的自动填充

项目里实体的创建时间/更新时间，常用 MyBatis-Plus 的自动填充自动写入，不用手动 set：

```java
// 实体字段
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createTime;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime;
```

配合一个 `MetaObjectHandler`（MyMetaObjectHandler），在插入/更新时自动填充当前时间。

## 常见坑

- ❌ 新手混用 `Date` / `Calendar` 和 `LocalDateTime`，导致类型不匹配
- ❌ 用 `new Date()` 旧的 API，建议统一用 `LocalDateTime`
- ❌ 数据库 `datetime` 类型和 Java `LocalDateTime` 映射时，注意 `application.yml` 里要不要配置时区（`serverTimezone`）
- ✅ 展示给前端通常转成字符串，用 `DateTimeFormatter` 格式化

## 自测题

1. `LocalDate`、`LocalTime`、`LocalDateTime` 区别是什么？
2. 怎么把 `LocalDateTime` 转成 `"2026-08-12 10:30:00"` 字符串？
3. `plusDays(1)` 返回的是新对象还是修改了原对象？（提示：这些类型是不可变的）

## 对照实战

在 `bj-01-cms` 里搜 `LocalDateTime` 和 `@TableField(fill`，看文章的 `createTime` / `updateTime` 是怎么声明和自动填充的。