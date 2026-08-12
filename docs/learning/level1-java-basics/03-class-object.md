# 03 类与对象：图纸与实物

## 通俗理解

**类**是一张"图纸"，**对象**是按图纸造出来的"实物"。`User` 是图纸，`new User()` 是造出来的具体某个人。

一张图纸可以造出无数个实物，每个实物有自己独立的数据。

---

## 代码示例

```java
// 类是图纸：定义"用户长什么样、能干什么"
public class User {
    private String name;   // 属性：用户有什么
    private int age;

    // 构造方法：造实物的"入口"，与类同名，可重载
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 方法：用户能干什么
    public String getName() {
        return this.name;   // this 指向"当前这个对象"
    }

    public int getAge() {
        return this.age;
    }
}
```

```java
// 用类造对象
User alice = new User("爱丽丝", 18);   // 调用构造方法
User bob   = new User("鲍勃", 20);
System.out.println(alice.getName());   // 爱丽丝
System.out.println(bob.getName());     // 鲍勃（每个对象数据独立）
```

## 关键概念

### this 和 super
- `this`：指"当前这个对象"，用于区分成员变量和参数（`this.name = name`）
- `super`：指"父类"，用于调用父类的构造方法或方法（见 04 篇）

### static：属于类，不属于对象
```java
public class Config {
    public static final int MAX = 100;   // 类级常量，所有对象共享
    public static int count = 0;          // 类级变量

    public static void printCount() {     // 类级方法
        System.out.println(count);
    }
}
// 通过类名直接访问，不需要 new
System.out.println(Config.MAX);
```

### final：不可变
- `final` 修饰变量 → 值不可再改
- `final` 修饰方法 → 不能被子类重写
- `final` 修饰类 → 不能被继承

## 常见坑

- ❌ 不写构造方法时，Java 会默认给一个无参构造；但一旦你写了带参构造，默认无参构造就没了。
- ❌ `static` 方法里不能访问非 static 的成员变量（因为 static 不依赖对象，而成员变量属于对象）。
- ✅ 属性通常用 `private` 封装，对外通过 getter/setter 访问（封装性，见 05 篇）。

## 自测题

1. `this.name = name` 里两个 `name` 各指什么？
2. `static` 方法和普通方法的本质区别是什么？
3. `final` 修饰变量、方法、类分别有什么效果？

## 对照实战

打开 `bj-01-cms/.../entity/Admin.java`，看它用了 `@Data` 注解——这个注解会自动帮你生成 getter/setter，这就是为什么实体类里你只看到字段、没有手写方法。注解在 08 篇详解。