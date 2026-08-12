# 04 继承与接口：复用与契约

## 通俗理解

- **继承（extends）**：子类"继承"父类的代码，是"is-a"关系（猫是一种动物）。像儿子继承了父亲的基因，但还能长出自己的特点。
- **接口（implements）**：接口是一份"合同/契约"，规定"你必须能做什么"，但不规定怎么做。像一份岗位说明书，谁入职谁照做。

**核心区别**：
- 一个类只能继承**一个**父类（单继承）
- 一个类可以实现**多个**接口（多实现）

---

## 继承：extends

```java
public class Animal {
    protected String name;

    public void eat() {
        System.out.println(name + " 在吃东西");
    }
}

// Cat 继承 Animal，自动拥有 name 字段和 eat 方法
public class Cat extends Animal {
    public void meow() {
        System.out.println(name + " 喵喵叫");
    }
}

Cat c = new Cat();
c.eat();   // 从父类继承来的
c.meow();  // 自己新增的
```

## 抽象类 vs 接口

| | 抽象类（abstract） | 接口（interface） |
|---|---|---|
| 能定义字段 | ✅ 能 | ❌ 只能定义常量 |
| 能写实现方法 | ✅ 能 | ✅ Java 8+ 可用 default |
| 单继承/多实现 | 只能单继承一个 | 可实现多个 |
| 语义 | "是什么"（is-a） | "能做什么"（has a capability / 契约） |
| 选择时机 | 共享**代码**和状态 | 约定**行为**能力 |

```java
// 抽象类：有具体实现可继承
public abstract class BaseService {
    public void log(String msg) { System.out.println(msg); }  // 通用实现
    public abstract void doWork();                            // 子类必须实现
}

// 接口：只约定能力
public interface Runnable {
    void run();               // 只有签名，没有实现
    default void sleep() {}   // default：可给默认实现
}
```

## 常见坑

- ❌ 把"能做什么"误用继承去做 → 应该用接口
- ❌ 接口里定义的变量其实是 `public static final` 常量，不是普通成员变量
- ✅ 判断标准：如果"子类复用父类的具体代码"，用继承；如果"不同类型都要具备某种能力"，用接口。

## 自测题

1. 一个类能继承几个父类？能实现几个接口？
2. 抽象类和接口，什么时候用哪个？给一个场景判断。
3. 接口里声明的变量是什么性质的？

## 对照实战

打开 `bj-01-cms/.../service/AdminService.java`（接口）和 `AdminServiceImpl.java`（实现类），看看接口怎么"约定能力"、实现类怎么 `implements`。同时看它继承的 `ServiceImpl<...>`——这就是 MyBatis-Plus 提供的基类。