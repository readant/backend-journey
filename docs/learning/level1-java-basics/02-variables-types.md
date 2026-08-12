# 02 变量与类型：强类型系统

## 通俗理解

Java 的变量就像**贴了标签的盒子**——`String name` 表示这个盒子只能装字符串，`int age` 只能装整数。装错了类型，编译时就报错，运行根本到不了那一步。

这就是**强类型**：类型必须声明，且一旦声明就不能装别的类型。

---

## 基本类型 vs 引用类型

| 类型 | 例子 | 特点 |
|------|------|------|
| 基本类型 | `int`、`double`、`boolean`、`char` | 存的是"值"本身，速度快，无方法 |
| 引用类型 | `String`、`Integer`、自定义类、数组 | 存的是"指向对象的地址"，有方法 |

## 代码示例

```java
public class TypesDemo {
    public static void main(String[] args) {
        // 基本类型：直接存值
        int age = 25;
        double price = 9.9;
        boolean isOk = true;
        char grade = 'A';

        // 引用类型：存地址
        String name = "张三";          // String 是引用类型
        int[] scores = {90, 85, 88};  // 数组也是引用类型

        System.out.println(name + " 今年 " + age + " 岁");
    }
}
```

## 包装类：基本类型的"对象化"

每个基本类型都有一个对应的包装类，**能把值装进对象里**，从而能放进集合、能调方法：

```java
Integer boxedAge = 25;        // 自动装箱：int → Integer
int unboxedAge = boxedAge;    // 自动拆箱：Integer → int

// 为什么需要？因为泛型和集合（List、Map）只能装对象，不能装基本类型
List<Integer> ages = new ArrayList<>();  // 不能写成 List<int>
```

| 基本类型 | 包装类 |
|---------|--------|
| int | Integer |
| double | Double |
| boolean | Boolean |
| char | Character |
| long | Long |

## 常见坑

- ❌ 给 int 赋小数 → 编译错误
- ❌ 给 int 赋 null（基本类型不能为 null）→ 编译错误
- ⚠️ 自动拆箱时包装类为 null → 运行时空指针（`NullPointerException`）
- ⚠️ 用 `==` 比较两个 `Integer` 对象，在 -128~127 之间是 true，超出范围是 false（因为比较的是地址）。**比较包装类要用 `.equals()`**

## 自测题

1. 基本类型和引用类型的本质区别是什么？
2. 为什么集合里要写 `Integer` 而不是 `int`？
3. 判断：`Integer a = 100; Integer b = 100; a == b` 结果是 true 还是 false？为什么？`a.equals(b)` 呢？

## 对照实战

打开 `bj-01-cms/.../common/R.java`，找到 `code` 字段，看它用的是 `Integer` 还是 `int`，想想为什么返回体里要用包装类（提示：需要区分"成功 code=200"和"没设置"）。