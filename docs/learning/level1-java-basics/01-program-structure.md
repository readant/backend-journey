# 01 程序结构：class 与 main

## 通俗理解

Java 程序就像一个**工厂**，所有生产活动（代码）都必须在一个个"车间"（class）里进行。`main` 方法是工厂的**总开关**，程序从这里启动。

---

## 代码示例

```java
// 一个 class 就是一个"车间"，类名必须和文件名一致（这里文件叫 Hello.java）
public class Hello {

    // main 是程序的入口：JVM 启动时从这里开始执行
    public static void main(String[] args) {
        System.out.println("Hello, Java!");  // 在控制台打印一行字
    }
}
```

## 拆解


| 部分                   | 含义                      |
| -------------------- | ----------------------- |
| `public`             | 这个类对外开放（别人能用它）          |
| `class`              | 关键字，声明一个"类"（车间）         |
| `Hello`              | 类名，习惯用大驼峰命名 `UpperCase` |
| `static`             | 静态的，无需 new 就能直接调用       |
| `void`               | 返回类型，表示这个方法不返回任何东西      |
| `String[] args`      | 命令行传入的参数数组              |
| `System.out.println` | 向控制台打印一行并换行             |




## 设计意图

- `main` 必须是 `public static void`：JVM 启动时**还没有任何对象**，所以 main 不能依赖对象实例（static），也不需要返回给谁（void）。
- 一个项目可以有很多 class，但启动入口只有一个 main。



## 常见坑

- ❌ 类名和文件名不一致 → 编译报错
- ❌ 忘记写 `public static void main` 或写错 → 报 "main method not found"
- ✅ 一个 `.java` 文件里 `public` 类只能有一个，且必须与文件名同名



## 自测题

1. Java 程序的入口是哪个方法？它为什么要加 `static`？
2. `void main` 里的 `void` 表示什么？
3. 一个 `.java` 文件里最多能有几个 `public` 类？它和文件名的关系是什么？



## 对照实战

打开 `bj-01-cms/src/main/java/com/readant/cms/CmsApplication.java`，找到 `main` 方法，看看它比上面的例子多了哪些注解（注解在第 8 篇会讲）。