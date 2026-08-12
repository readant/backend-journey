# 06 枚举：一组命名常量

## 通俗理解

**枚举（enum）** 是把一组"固定的、有限的取值"收集成一个类型。比如一周有七天、状态只有"待支付/已支付/已取消"——用枚举来约束，防止乱填字符串。

## 代码示例

```java
// 定义一组固定取值
public enum ArticleStatus {
    DRAFT,     // 草稿
    PUBLISHED, // 已发布
    ARCHIVED   // 已归档
}

// 使用
ArticleStatus status = ArticleStatus.PUBLISHED;

// 遍历
for (ArticleStatus s : ArticleStatus.values()) {
    System.out.println(s.name());  // DRAFT / PUBLISHED / ARCHIVED
}

// 比较
if (status == ArticleStatus.PUBLISHED) {
    System.out.println("已发布");
}
```

## 枚举还能带属性

```java
public enum OrderStatus {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    CANCELLED(2, "已取消");

    private final int code;
    private final String desc;

    OrderStatus(int code, String desc) {   // 私有构造，枚举不能 new
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }
}
```

## 设计意图

- 用字符串表示状态（如 `"DRAFT"` / `"draft"` / `"draft "`）容易写错、难统一
- 枚举把取值**限制死**，编译期就能发现错误，代码更安全、可读

## 常见坑

- ❌ 枚举不能 `new`（构造方法是私有的，只能写死的那几个值）
- ❌ 用 `==` 比较枚举是安全的（因为是同一个对象），不需要用 `equals`
- ✅ 需要"状态 + 说明/编号"时，给枚举加属性和构造方法

## 自测题

1. 枚举和 `static final` 常量比，优势是什么？
2. 枚举可以 `new` 吗？为什么？
3. 给枚举加属性时，需要写什么？（构造方法 + 字段 + getter）

## 对照实战

在 `bj-01-cms` 里搜索有没有 `enum`，或看文章/产品的"状态"字段在代码里是怎么表示的。如果还没有用枚举，思考一下：用枚举重写会更好吗？