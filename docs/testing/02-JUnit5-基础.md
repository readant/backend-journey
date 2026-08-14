# 02 JUnit 5 基础

> JUnit 5 是本仓库的测试框架。这一篇只讲测试里**天天用到**的部分，完整参考见 [JUnit 5 官方文档](https://junit.org/junit5/docs/current/user-guide/)。

## 1. 生命周期注解

| 注解 | 作用 | 使用频率 |
| ---- | ---- | -------- |
| `@Test` | 标记一个测试方法 | ★★★★★ |
| `@DisplayName` | 测试类 / 测试方法的中文展示名 | ★★★★★ |
| `@Nested` | 内嵌分组，按被测方法组织用例 | ★★★★☆ |
| `@BeforeEach` | 每个用例执行前运行 | ★★☆☆☆ |
| `@AfterEach` | 每个用例执行后运行 | ★☆☆☆☆ |
| `@BeforeAll` / `@AfterAll` | 整个测试类执行前/后各运行一次（需 static） | ★☆☆☆☆ |

本仓库绝大多数测试只用到前三个 —— 因为**每个用例都是自包含的**（自己在 given 里准备数据），不需要 `@BeforeEach` 造数据。

## 2. 测试类的骨架

```java
@DisplayName("CategoryServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Nested
    @DisplayName("create 方法")
    class Create {

        @Test
        @DisplayName("父栏目不存在 → 抛出业务异常 400")
        void shouldThrow_whenParentMissing() {
            when(categoryMapper.selectById(5L)).thenReturn(null);
            Category category = buildCategory(null, "子栏目", 5L);

            assertThatThrownBy(() -> categoryService.create(category))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("父栏目不存在");
            verify(categoryMapper, never()).insert(any(Category.class));
        }
    }
}
```

要点：

- `@Nested` 内嵌类**不需要 static**（JUnit 5 支持非静态内嵌类）
- 每个 `@Nested` 类对应被测类的一个方法，类名就是方法名
- 测试方法用 `@Test`，不能有参数（本仓库不使用参数化测试，避免复杂度）

## 3. @Nested 分组的价值

```
CategoryServiceImplTest
├── GetTree    ← 组：平铺转树、空数据
├── Create     ← 组：顶级创建、父存在、父不存在
├── Update     ← 组：不存在、存在
└── Delete     ← 组：有子栏目、无子栏目
```

好处：

1. **和被测方法一一对应**，测试即文档
2. 失败时报告会显示 `GetTree > shouldBuildTree`，一眼定位
3. 同类用例的测试数据构造方法（如 `buildCategory`）在外部类共享

## 4. 断言

JUnit 5 自带 `Assertions` 类，但本仓库统一用 **AssertJ**（见 04 篇），原因只有一个：断言失败时的报错信息可读性差太多。

```java
// JUnit 自带（不推荐）——报错只说「expected: 1 but was: 2」
assertEquals(1, tree.size());

// AssertJ（推荐）——报错会说「Expecting size to be <1> but was <2>」
assertThat(tree).hasSize(1);
```

## 5. 生命周期顺序（了解即可）

```
@BeforeAll → @BeforeEach → @Test → @AfterEach → ...（每个用例重复中间三步）→ @AfterAll
```

本仓库测试不依赖执行顺序（每个用例自包含），这条只需要知道存在。

## 6. 常见误解

| 误解 | 事实 |
| ---- | ---- |
| 测试类必须继承某基类 | ❌ 普通类 + `@DisplayName` 即可 |
| 测试方法必须 public | ❌ JUnit 5 允许包私有，本仓库就用的包私有 |
| `@Nested` 类需要 static | ❌ JUnit 5 非静态内嵌类，可直接访问外部类字段 |
| 每个用例之间共享状态 | ❌ 默认每个用例新建测试类实例，状态不共享（这是好事） |