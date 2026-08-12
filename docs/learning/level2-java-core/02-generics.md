# 02 泛型：给容器贴上"只能装什么"的标签

## 通俗理解

**泛型（Generic）** 就是给类/方法加上**类型标签**，告诉它"你只能装这种类型的东西"。用 `<T>` 表示"某个类型"，在真正使用时再确定具体是什么类型。

好处：
1. **类型安全**：装错类型编译就报错，不会运行到一半才发现
2. **避免强转**：不用每次取出来都手动 `(String)` 转换

## 代码示例

```java
// 泛型类：T 表示"任意类型"，使用时再定
public class Box<T> {
    private T content;

    public void set(T content) { this.content = content; }
    public T get() { return content; }
}

Box<String> strBox = new Box<>();
strBox.set("你好");
String s = strBox.get();       // 不用强转，类型安全

// 如果放错类型，编译直接报错：
// strBox.set(123);   // 编译错误！Box<String> 只能装 String
```

## 泛型方法

```java
public <T> T firstOf(List<T> list) {
    return list.get(0);
}
```

## 实战重点：泛型统一返回体

项目里统一返回体就是泛型的经典应用：

```java
// R<T>：data 字段的类型由调用方决定
public class R<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        return r;
    }
}

// 返回"文章列表"：R<List<ArticleVO>>
return R.success(articleService.listAll());

// 返回"单个管理员"：R<AdminVO>
return R.success(adminService.getById(1L));
```

**好处**：同一个 `R` 类能装任意类型的数据，不需要为每种返回类型写一个类。

## 常见坑

- ❌ 基本类型不能做泛型实参 → 必须用包装类：`List<Integer>` 不能写 `List<int>`
- ❌ 泛型的 `?` 通配符（`? extends` / `? super`）容易绕晕，初学先避开，够用就行
- ✅ 泛型的类型信息在运行时会被"擦除"，但编译期能保证安全

## 自测题

1. 泛型解决了什么问题？核心好处是什么？
2. 为什么 `List<int>` 会编译报错而 `List<Integer>` 可以？
3. `R<T>` 这种统一返回体用了泛型的什么优势？

## 对照实战

打开 `bj-01-cms/.../common/R.java`，看它怎么用泛型定义统一返回体，以及 `success()` 静态方法里 `<T>` 的写法。再看 `AdminController` 里 `R.success(...)` 返回的类型是怎么被推断出来的。