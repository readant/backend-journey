# 05 包与访问修饰符：组织的收纳盒与门禁

## 通俗理解

- **包（package）**：像文件夹/收纳盒，把类按功能归类，避免重名、方便管理。
- **访问修饰符**：像房间的**门禁等级**，决定"谁能进这个房间（访问这个成员）"。

---

## 包与 import

```java
// 声明类所属的包（必须写在文件最顶部，一个文件只能有一个）
package com.readant.cms.entity;

import com.readant.cms.common.R;   // 引入别的包里的类

public class Admin {
    // ...
}
```

| 要点 | 说明 |
|------|------|
| `package` | 声明当前类在哪个包，用点分隔，通常反写域名 |
| `import` | 引入其他包的类，之后就可以直接用类名 |
| 同一个包内 | 无需 import 可直接使用 |
| `java.lang` | 最基础的包（String、Integer），自动导入，无需写 |

## 访问修饰符：四档门禁

```java
public class Demo {
    public    int a;   // 任何地方都能访问
    protected int b;   // 同包 + 子类能访问
    private   int c;   // 仅本类能访问
    /*默认*/  int d;   // 同包能访问
}
```

| 修饰符 | 同类 | 同包 | 子类 | 其他包 |
|--------|:---:|:---:|:---:|:---:|
| `private` | ✅ | ❌ | ❌ | ❌ |
| 默认（不写） | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

## 封装性

**封装**：属性用 `private` 藏起来，对外提供 `getXxx()` / `setXxx()` 或方法控制访问。好处是：
1. 外部不能随意改内部状态
2. 可以加校验逻辑（比如年龄不能为负）
3. 内部实现改了，外部调用不受影响

## 常见坑

- ❌ 把数据字段设成 `public`，破坏封装
- ❌ 忘记写 `package` 声明，类落进默认包，无法被规范管理
- ✅ Controller/Service/Mapper 分层放在不同包，职责清晰（对应 `com.readant.cms.controller` / `.service` / `.mapper`）

## 自测题

1. `private` 和 `protected` 都能被谁访问？区别在哪？
2. 为什么实体类的字段通常用 `private`？封装带来什么好处？
3. `import` 的作用是什么？哪些包不用 import？

## 对照实战

打开 `bj-01-cms/src/main/java/com/readant/cms/`，看它是怎么按 `entity` / `mapper` / `service` / `controller` 分包组织的。这四层就是经典的分层架构。