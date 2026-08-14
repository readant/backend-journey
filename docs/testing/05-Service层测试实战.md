# 05 Service 层测试实战

> Service 层是测试的主战场。这一篇用 `bj-01-cms` 的真实用例讲透六种高频场景的写法与套路。
>
> 完整源码对照：`bj-01-cms/src/test/java/com/readant/cms/service/impl/` 下的 8 个 `*ServiceImplTest.java`。

## 0. 总览：一个典型测试类的组成

```java
@DisplayName("CategoryServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;      // 1. 替身：全部依赖

    @InjectMocks
    private CategoryServiceImpl categoryService; // 2. 真身：被测对象

    // 3. 测试数据工厂：共享构造方法，避免每个用例里重复 set
    private Category buildCategory(Long id, String name, Long parentId) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setParentId(parentId);
        return category;
    }

    @Nested
    @DisplayName("create 方法")   // 4. 按被测方法分组
    class Create {
        @Test
        @DisplayName("父栏目不存在 → 抛出业务异常 400")
        void shouldThrow_whenParentMissing() {
            // given
            when(categoryMapper.selectById(5L)).thenReturn(null);
            Category category = buildCategory(null, "子栏目", 5L);
            // when
            assertThatThrownBy(() -> categoryService.create(category))
            // then
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("父栏目不存在");
            verify(categoryMapper, never()).insert(any(Category.class));
        }
    }
}
```

**数据工厂是第一个实战技巧**：实体字段多，每个用例手写 set 会又长又乱。抽一个 `buildXxx(...)` 方法，只暴露用例关心的字段，其余用默认值。

## 1. 场景一：存在性校验（最典型）

几乎所有 `update` / `delete` 的第一行都是「查一下在不在」。两个用例锁死它：

```java
@Test
@DisplayName("文章不存在 → 抛出业务异常 404")
void shouldThrow_whenNotFound() {
    when(articleMapper.selectById(9L)).thenReturn(null);

    assertThatThrownBy(() -> articleService.delete(9L))
            .isInstanceOf(BusinessException.class)
            .hasMessage("文章不存在");
    verify(articleMapper, never()).deleteById(anyLong());   // 关键：确认没往下执行
}

@Test
@DisplayName("文章存在 → 删除成功")
void shouldDelete_whenExists() {
    when(articleMapper.selectById(1L)).thenReturn(buildArticle(1L, "标题", 1, null));

    articleService.delete(1L);

    verify(articleMapper).deleteById(1L);
}
```

套路总结：

- 异常分支：**异常断言 + `never()` 验证**（双重锁定：既抛错，又没副作用）
- 正常分支：verify 确认副作用发生

## 2. 场景二：默认值填充（返回值看不到，必须捕获）

创建文章时「不传状态 → 默认草稿」，这是典型业务规则。返回值是 void 或返回值里看不到，用 `ArgumentCaptor` 抓实际入库的对象：

```java
@Test
@DisplayName("未指定状态 → 默认为草稿(0)且浏览量初始为0")
void shouldDefaultToDraft_whenStatusNull() {
    ArticleCreateReq req = buildReq("标题", null);

    articleService.create(req);

    ArgumentCaptor<Article> captor = ArgumentCaptor.forClass(Article.class);
    verify(articleMapper).insert(captor.capture());
    Article saved = captor.getValue();
    assertThat(saved.getStatus()).isEqualTo(0);
    assertThat(saved.getViewCount()).isEqualTo(0);
}
```

同样的思路用在 `CategoryServiceImplTest`：捕获 insert 的实体，断言 `sortOrder == 0`、`status == 1`。

## 3. 场景三：多依赖协作组装（VO 组装）

Service 查完主表还要查关联表拼 VO。用例要**分别打桩**，断言组装结果：

```java
@Test
@DisplayName("文章存在 → 返回 VO 并附带栏目名称")
void shouldReturnArticle_whenExists() {
    Article article = buildArticle(1L, "标题", 1, 10L);
    when(articleMapper.selectById(1L)).thenReturn(article);          // 桩 1：主表
    Category category = new Category();
    category.setId(10L);
    category.setName("公司新闻");
    when(categoryMapper.selectById(10L)).thenReturn(category);       // 桩 2：关联表

    ArticleVO result = articleService.getById(1L);

    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getCategoryName()).isEqualTo("公司新闻");       // 组装产物
}
```

套路：**一个 Mock 一个桩，最终断言组装后的 VO 字段**。这就是「测行为契约」—— 不关心内部怎么组装，只关心输出对不对。

## 4. 场景四：分页查询（MyBatis-Plus Page）

```java
@Test
@DisplayName("无栏目ID → 栏目名称为空")
void shouldReturnPage_whenNoCategory() {
    Article article = buildArticle(1L, "标题", 1, null);
    Page<Article> articlePage = new Page<>(1, 10, 1);                 // current, size, total
    articlePage.setRecords(Collections.singletonList(article));
    when(articleMapper.selectPage(any(Page.class), any())).thenReturn(articlePage);

    Page<ArticleVO> result = articleService.page(1, 10, 1, null);

    assertThat(result.getTotal()).isEqualTo(1);
    assertThat(result.getRecords()).hasSize(1);
    assertThat(result.getRecords().get(0).getCategoryName()).isNull();
}
```

要点：

- Mapper 的 `selectPage` 用 `any(Page.class)` 打桩（断言具体分页参数价值不大，SQL 组装不是单测职责）
- 构造 `Page` 时第三个参数是 total，和 `setRecords` 搭配模拟「有数据的一页」
- 断言 `total` + `records` 数量 + records 里元素的字段

## 5. 场景五：树形组装（纯逻辑，无 SQL）

这是「没有 mapper 交互也能测」的代表 —— 组装逻辑全在内存里：

```java
@Test
@DisplayName("平铺数据 → 组装成树形结构")
void shouldBuildTree() {
    Category root = buildCategory(1L, "关于我们", null);
    Category child = buildCategory(2L, "公司简介", 1L);
    Category orphan = buildCategory(3L, "无父级", 99L);     // 脏数据：父级不存在
    when(categoryMapper.selectList(null)).thenReturn(Arrays.asList(root, child, orphan));

    List<Category> tree = categoryService.getTree();

    assertThat(tree).hasSize(1);                                 // 只有真正顶级
    assertThat(tree.get(0).getId()).isEqualTo(1L);
    assertThat(tree.get(0).getChildren()).hasSize(1);            // 挂上了子节点
    assertThat(tree.get(0).getChildren().get(0).getId()).isEqualTo(2L);
    // 孤儿节点(99L)被丢弃 —— 边界行为被锁定
}
```

技巧：**把脏数据（孤儿节点）混进测试数据**，让「丢弃无效节点」这个边界行为变成显式断言。

## 6. 场景六：原生 SQL 协作（JdbcTemplate）

Service 直接用了 `JdbcTemplate`（如角色分配），Mock 方式一样，但参数核对要用 `eq`：

```java
@Test
@DisplayName("角色存在且未分配 → 分配成功")
void shouldAssign_whenValid() {
    when(roleMapper.selectById(1L)).thenReturn(buildRole(1L, "ROLE_ADMIN"));
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyLong(), anyLong()))
            .thenReturn(0);

    roleService.assignRole(1L, 1L);

    verify(jdbcTemplate).update(
            eq("INSERT INTO admin_role (admin_id, role_id) VALUES (?, ?)"),
            eq(1L), eq(1L));
}
```

教训：

- 打桩 `queryForObject` 时必须**全部用匹配器**（`anyString()` + `eq(...)`），混用编译都过不了
- 返回值 `0/1` 代表「存在/不存在」，测试时用 `thenReturn(0)` 和 `thenReturn(1)` 各走一遍分支

## 7. 密码加密类依赖（BCrypt）

`AdminServiceImpl` 依赖 `BCryptPasswordEncoder` —— 这是**真实现**，不是 Mock：

```java
private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

private Admin buildAdmin(Long id, String username, String rawPassword, Integer status) {
    Admin admin = new Admin();
    admin.setUsername(username);
    admin.setPassword(encoder.encode(rawPassword));   // 真实加密，登录用例才能通过
    return admin;
}
```

原则：**工具类、无状态组件用真实现；有 IO / 外部依赖的用 Mock**。BCrypt 加密是纯函数，跑得快，直接 new 真身。

## 8. 实战清单（写完自检）

- [ ] 每个 `@Nested` 组里，正常路径 + 所有异常分支 + 边界（空数据）都有用例？
- [ ] 异常分支是否同时验证了 `never()` 无副作用？
- [ ] 有默认值填充的逻辑是否用了 `ArgumentCaptor` 断言字段？
- [ ] 组装类方法是否断言了最终 VO 的关键字段（而非只断言不为 null）？
- [ ] 每个用例是否自包含（不依赖其他用例的执行顺序）？
- [ ] 有没有打桩了却没用的 `when`（会被 Mockito 严格模式报错）？