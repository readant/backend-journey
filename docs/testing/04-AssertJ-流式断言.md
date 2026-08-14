# 04 AssertJ 流式断言

> AssertJ 是本仓库的断言库。和 JUnit 自带断言最大的区别：**断言方法按对象类型自由组合（流式），且失败信息自动生成、可读性好**。

## 1. 为什么用 AssertJ 而不是 assertEquals

```java
// JUnit 自带：失败了只说 expected: <1> but was: <2>，看不出哪个变量
assertEquals(1, result.size());
assertEquals("admin", result.getUsername());

// AssertJ：失败信息自动带上下文，一眼看出断言意图
assertThat(result.getUsername())
        .isEqualTo("admin");
```

本仓库统一 `import static org.assertj.core.api.Assertions.assertThat;`，所有断言从 `assertThat(实际值)` 开始。

## 2. 按类型选断言（本仓库高频用法）

### 数值 / 普通对象

```java
assertThat(result.getId()).isEqualTo(1L);
assertThat(result.getCode()).isEqualTo(401);
```

### 字符串

```java
assertThat(token).isNotBlank();
assertThat(result.getMsg()).isEqualTo("用户名或密码错误");
assertThat(result.getMsg()).contains("不存在");   // 模糊匹配，适合异常消息
```

> 建议：断言异常消息优先用 `isEqualTo`（精确锁定契约），只在消息里有动态部分时用 `contains`。

### 集合

```java
assertThat(roles).hasSize(1);                     // 长度
assertThat(tree).isEmpty();                        // 空集合
assertThat(result.getRecords()).hasSize(1);        // 分页 records
```

### 空值

```java
assertThat(result.getData()).isNull();
assertThat(result.getRecords().get(0).getCategoryName()).isNull();  // 无栏目时栏目名为 null
```

## 3. 异常断言：assertThatThrownBy

断言「调用会抛异常」的专用写法，**必须把调用包进 lambda**：

```java
assertThatThrownBy(() -> categoryService.create(category))
        .isInstanceOf(BusinessException.class)   // 异常类型
        .hasMessage("父栏目不存在");              // 异常消息（精确）
```

```java
assertThatThrownBy(() -> articleService.getById(9L))
        .isInstanceOf(BusinessException.class)
        .hasMessage("文章不存在");
```

要点：

- `isInstanceOf` 断言异常类型（业务异常是 `BusinessException`，不要断言成 `RuntimeException`）
- `hasMessage` 断言消息，锁死「用户看到的提示语」
- **lambda 里只有一行调用**，不要塞多行逻辑，否则异常来源不清晰

## 4. 链式断言的顺序

```java
R<Void> result = handler.handleBusinessException(e);

// 链式：同一个对象连续断言多个字段
assertThat(result.getCode()).isEqualTo(401);
assertThat(result.getMsg()).isEqualTo("用户名或密码错误");
assertThat(result.getData()).isNull();
```

每条链一个对象。不同对象之间重新 `assertThat`，不要为了链而链。

## 5. 集合元素的精细断言

```java
// 断言列表第一个元素的具体字段（树形结构验证）
assertThat(tree).hasSize(1);
assertThat(tree.get(0).getId()).isEqualTo(1L);
assertThat(tree.get(0).getChildren()).hasSize(1);
assertThat(tree.get(0).getChildren().get(0).getId()).isEqualTo(2L);
```

这是本仓库验证「树形组装正确」的标准写法：**一层一层往下钻**，每层都断言关键字段。

## 6. 总结：一张表记住用法

| 想断言什么 | 写法 |
| ---------- | ---- |
| 值相等 | `assertThat(x).isEqualTo(y)` |
| 非空字符串 | `assertThat(x).isNotBlank()` |
| 为空 | `assertThat(x).isNull()` / `assertThat(list).isEmpty()` |
| 集合长度 | `assertThat(list).hasSize(n)` |
| 抛异常 | `assertThatThrownBy(() -> call).isInstanceOf(X.class).hasMessage("...")` |
| 字符串包含 | `assertThat(s).contains("...")` |

> 反模式：断言用 `assertTrue(x == 1)` / `assertTrue(list.size() > 0)` —— 失败信息只显示 `true/false`，等于没报错。所有场景都能用 AssertJ 语义化方法替代。