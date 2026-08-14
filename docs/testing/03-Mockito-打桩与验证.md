# 03 Mockito 打桩与验证

> Mockito 负责「造假的依赖」和「验证交互」。这一篇覆盖本仓库用到的全部技巧：`@Mock` / `@InjectMocks` / `when` / `verify` / `ArgumentCaptor` / 匹配器。

## 1. 三个注解的分工

| 注解 | 作用 | 比喻 |
| ---- | ---- | ---- |
| `@Mock` | 创建一个假对象，不执行真实逻辑 | 替身演员 |
| `@InjectMocks` | 创建被测类真实实例，并把 `@Mock` 按类型注入进去 | 导演给真演员配上替身 |
| `@ExtendWith(MockitoExtension.class)` | 让上面两个注解生效 | 幕后工作人员 |

```java
@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleMapper articleMapper;   // 替身

    @Mock
    private CategoryMapper categoryMapper; // 替身

    @InjectMocks
    private ArticleServiceImpl articleService; // 真身，构造时自动注入两个替身
}
```

注意：

- `@InjectMocks` 优先按**构造器**注入，没有构造器时按字段注入
- 被测类用 Lombok `@RequiredArgsConstructor` 生成的构造器也能被 Mockito 识别
- **只 mock 被测类的直接依赖**，不要 mock 被测类自己（那是测了个寂寞）

## 2. 打桩：when().thenReturn()

`when` 决定「当调用这个方法时，返回什么」：

```java
when(articleMapper.selectById(1L)).thenReturn(article);
when(categoryMapper.selectById(10L)).thenReturn(category);
when(articleMapper.selectPage(any(Page.class), any())).thenReturn(articlePage);
```

### 参数匹配器

| 匹配器 | 含义 | 示例 |
| ---- | ---- | ---- |
| `any()` | 任意参数（含 null） | `any(Category.class)` |
| `anyLong()` / `anyString()` | 任意 long / String | `anyLong()` |
| `eq()` | 精确等于某个值 | `eq(1L)` |

**铁律：混用时，一个方法的参数要么全用精确值，要么全用匹配器。**

```java
// ❌ 编译都过不了
when(jdbcTemplate.queryForObject(anyString(), Integer.class, 1L, anyLong()));

// ✅ 全部用匹配器
when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyLong(), anyLong()))
        .thenReturn(1);
```

### 打桩失败的常见症状

```java
// ❌ selectById(9L) 没有打桩 → 返回 null → 被测方法可能 NPE 或走了错误分支
Category result = categoryService.update(9L, req);

// ✅ 被测方法内部到底调了什么参数？用 verify + ArgumentCaptor 先看，再补桩
```

## 3. 验证：verify()

`verify` 回答「这个方法**有没有被调用**、调了几次、用什么参数调的」：

```java
verify(articleMapper).updateById(any(Article.class));          // 恰好调用 1 次
verify(categoryMapper, never()).insert(any(Category.class));   // 一次都没调
verify(jdbcTemplate).update(eq("DELETE FROM admin_role WHERE admin_id = ? AND role_id = ?"), eq(1L), eq(1L)); // 精确 SQL + 参数
```

### 什么时候必须用 verify？

| 场景 | 示例 |
| ---- | ---- |
| 断言「**不应该**发生」 | 父栏目不存在时 `never()` 调 insert |
| 断言「副作用发生了」 | 删除成功时确实调了 `deleteById(1L)` |
| 返回值无法体现行为时 | 删除方法返回 void，只能靠 verify 确认 |
| 参数需要精确核对 | 原生 SQL 的完整 SQL 串 + 参数 |

> 规则：**有返回值 → 优先断言返回值；无返回值或返回值不足以说明行为 → 用 verify。**

## 4. 捕获参数：ArgumentCaptor

`verify` 只能核对「打桩时已知的参数」，捕获器能**拿到被测方法实际传给 Mock 的对象**，再断言它的内部字段：

```java
ArgumentCaptor<Article> captor = ArgumentCaptor.forClass(Article.class);
verify(articleMapper).insert(captor.capture());   // 捕获实际传入的 Article

Article saved = captor.getValue();
assertThat(saved.getStatus()).isEqualTo(0);       // 断言默认状态被正确填充
assertThat(saved.getViewCount()).isEqualTo(0);    // 断言默认值被正确初始化
```

典型用途：**验证「被测方法往实体里塞了什么默认值」** —— 这是最常见的业务规则，返回值又看不到，只能捕获。

## 5. 常用组合套路

| 套路 | 代码 |
| ---- | ---- |
| 异常分支 + 确认没有副作用 | `assertThatThrownBy(...)` + `verify(xxx, never()).insert(...)` |
| 默认值填充 | `ArgumentCaptor` 捕获 + 断言字段 |
| 多依赖协作 | 分别打桩两个 Mock，断言最终组装结果（如 VO 带栏目名） |
| 空数据边界 | `when(...).thenReturn(List.of())` + `assertThat(result).isEmpty()` |

## 6. 常见坑

### 坑 1：`when` 打桩不生效

```java
// 症状：打桩了但被测方法拿到 null
// 排查顺序：
// 1. 参数不匹配 —— 被测方法调的是 selectById(1L)，你桩的是 selectById(9L)
// 2. 被测方法调的不是 mock 的引用 —— 检查 @InjectMocks 是否注入了同一个 mock
// 3. 调用了 final / static / private 方法 —— Mockito 默认 mock 不了
```

### 坑 2：MockitoExtension 严格模式报 UnnecessaryStubbingException

`MockitoExtension` 默认严格模式：**打桩了但没用到 → 直接报错**。这不是 bug，是在提醒你「桩是多余的」。

```java
// ❌ 这个桩根本没被被测方法调用 → 报 UnnecessaryStubbing
when(categoryMapper.selectById(99L)).thenReturn(category);

// 解法：删掉没用的桩，或用 lenient()（不推荐，治标不治本）
```

### 坑 3：Mock 对象默认返回值

- 对象类型 → `null`
- 基本类型 → `0` / `false`
- 集合 → **空集合（不是 null）**

所以 `when(roleMapper.selectList(null)).thenReturn(List.of())` 和不打桩返回的结果可能一样 —— 但**建议显式打桩**，让「空数据是预期行为」写进用例。

### 坑 4：JdbcTemplate 的 mock

原生 SQL 的参数核对要用 `eq()` 精确匹配，否则验证不到：

```java
verify(jdbcTemplate)
        .update(eq("INSERT INTO admin_role (admin_id, role_id) VALUES (?, ?)"), eq(1L), eq(1L));
```

SQL 字符串变了用例就挂 —— 这既是约束（锁定了 SQL 契约），也是负担（改 SQL 要同步改测试）。权衡取舍在 07 篇展开。