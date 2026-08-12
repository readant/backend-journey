/*
 * R__demo_content.sql
 * ===================
 * 演示内容种子数据（可重复执行，幂等）
 *
 * 填充内容：栏目树、10 篇已发布文章、产品分类、6 个产品
 * 幂等策略：每条记录按唯一名称判断，已存在则跳过，重复执行不会产生脏数据
 * 配图说明：使用 Unsplash 免费图库直链（仅演示用，可随时替换为真实素材）
 */

-- ============================================================
-- 一、栏目（category）：顶层栏目即门户导航菜单项
-- ============================================================

INSERT INTO category (name, parent_id, sort_order, status)
SELECT '小组介绍', NULL, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '小组介绍');

INSERT INTO category (name, parent_id, sort_order, status)
SELECT '学习笔记', NULL, 2, 1
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '学习笔记');

INSERT INTO category (name, parent_id, sort_order, status)
SELECT '活动动态', NULL, 3, 1
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '活动动态');

INSERT INTO category (name, parent_id, sort_order, status)
SELECT '项目实践', NULL, 4, 1
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = '项目实践');

-- 子栏目
INSERT INTO category (name, parent_id, sort_order, status)
SELECT '成员风采', (SELECT id FROM category WHERE name = '小组介绍'), 1, 1
WHERE EXISTS (SELECT 1 FROM category WHERE name = '小组介绍')
  AND NOT EXISTS (SELECT 1 FROM category WHERE name = '成员风采');

INSERT INTO category (name, parent_id, sort_order, status)
SELECT '联系我们', (SELECT id FROM category WHERE name = '小组介绍'), 2, 1
WHERE EXISTS (SELECT 1 FROM category WHERE name = '小组介绍')
  AND NOT EXISTS (SELECT 1 FROM category WHERE name = '联系我们');

INSERT INTO category (name, parent_id, sort_order, status)
SELECT '前端进阶', (SELECT id FROM category WHERE name = '学习笔记'), 1, 1
WHERE EXISTS (SELECT 1 FROM category WHERE name = '学习笔记')
  AND NOT EXISTS (SELECT 1 FROM category WHERE name = '前端进阶');

INSERT INTO category (name, parent_id, sort_order, status)
SELECT '后端之路', (SELECT id FROM category WHERE name = '学习笔记'), 2, 1
WHERE EXISTS (SELECT 1 FROM category WHERE name = '学习笔记')
  AND NOT EXISTS (SELECT 1 FROM category WHERE name = '后端之路');

INSERT INTO category (name, parent_id, sort_order, status)
SELECT '算法与数据结构', (SELECT id FROM category WHERE name = '学习笔记'), 3, 1
WHERE EXISTS (SELECT 1 FROM category WHERE name = '学习笔记')
  AND NOT EXISTS (SELECT 1 FROM category WHERE name = '算法与数据结构');

-- ============================================================
-- 二、文章（article）：富文本内容（HTML），status=1 已发布
-- ============================================================

-- 小组介绍
INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  '兴华学习小组：一群人，把代码这件事学好',
  '我们是谁、我们在做什么、我们为什么聚在一起——来自兴华学习小组的自我介绍。',
  '<p>兴华学习小组成立于 2025 年春天，是一群热爱编程、愿意持续投入的学习者组成的互助社区。我们不追求速成，不迷信捷径，只相信一件事：扎实的基础 + 持续的输出 = 真正的成长。</p><h2>我们在做什么</h2><ul><li>每周固定一次技术分享会，主题覆盖前端、后端、算法与工程实践</li><li>以真实业务项目为主线，从需求分析到上线发布走完整流程</li><li>结对编程与 Code Review 常态化，让每一行代码都被认真对待</li></ul><h2>我们为什么聚在一起</h2><p>独自学习容易陷入三个困境：方向不清晰、问题没人讨论、成果无人反馈。小组存在的意义，就是互相补位——有人擅长架构，有人擅长调试，有人擅长表达。我们把各自擅长的事分享出来，所有人的短板就都被补齐了。</p><blockquote>一个人可以走得很快，但一群人才能走得更远。</blockquote><p>如果你也在学习编程的路上，欢迎关注我们，或者来现场旁听一次分享会。</p>',
  (SELECT id FROM category WHERE name = '小组介绍'),
  'https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=800&q=60',
  1, '兴华小组', 286, '2026-03-01 09:00:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '小组介绍')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = '兴华学习小组：一群人，把代码这件事学好');

-- 学习笔记
INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  'Vue 3 组合式 API 实战：从 setup 到 composable',
  '用真实场景讲清楚组合式 API 的核心思想，以及如何把重复逻辑提炼成可复用的 composable。',
  '<p>组合式 API 是 Vue 3 最重要的变化。理解它的关键不在语法，而在于一个思维转变：从按选项组织代码，变成按逻辑组织代码。</p><h2>setup 是什么</h2><p>setup 是组件的入口函数，所有响应式状态、计算属性、监听器和方法都在这里声明。它取代了 data、computed、watch 这些分散的选项，把同一块逻辑的代码聚拢在一起。</p><pre><code>import { ref, computed } from \'vue\'</code></pre><h2>为什么要提取 composable</h2><p>当一个页面里出现多个请求分页列表时，我们会发现 loading、dataList、pageNum、total 这些状态在每个组件里重复出现。把这一组状态和它们的方法打包成一个 usePagination 函数，就是 composable 的本质——它和普通函数没有区别，只是内部使用了响应式 API。</p><blockquote>composable 不是一个新概念，只是把函数式编程的复用思想带进了组件。</blockquote><h2>实践建议</h2><ul><li>以 use 开头命名，约定俗成</li><li>一个 composable 只解决一类问题</li><li>返回对象解构使用，保持响应式不被破坏</li></ul><p>掌握了组合式 API，你会发现组件文件变短了，逻辑边界变清晰了，复用也从复制粘贴变成了引用调用。</p>',
  (SELECT id FROM category WHERE name = '学习笔记'),
  'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?auto=format&fit=crop&w=800&q=60',
  1, '阿华', 412, '2026-03-08 10:30:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '学习笔记')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = 'Vue 3 组合式 API 实战：从 setup 到 composable');

INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  '从零搭建 Vite + Vue 3 + TypeScript 工程',
  '不依赖脚手架生成的模板，一步步手动理解工程里每个配置文件的职责。',
  '<p>用 create-vue 创建项目只要一条命令，但模板生成完毕后，很少有人清楚 node_modules 之外那些文件各自承担什么职责。这篇笔记带你把工程从零搭一遍。</p><h2>最小可运行结构</h2><ul><li>package.json：声明依赖与脚本</li><li>vite.config.ts：构建与服务配置，包括路径别名和代理</li><li>tsconfig.json：TypeScript 编译选项</li><li>index.html：Vite 的入口载体</li></ul><h2>路径别名的意义</h2><p>默认情况下引入组件要写相对路径，层级一深就变成一串 ../。在 vite.config.ts 里配置 alias 后，可以用 @/views/xxx 这种写法，可读性和可维护性都大幅提升，同时 tsconfig 里要同步声明 paths，否则类型检查会报错。</p><h2>代理不是跨域魔法</h2><p>开发环境的 proxy 只是把请求转发到后端服务，真正的跨域策略仍然由后端决定。理解这一点，排查接口问题时就不会绕弯路。</p><blockquote>脚手架帮你搭好了骨架，但理解每一块砖的用途，才能在你需要时改得动它。</blockquote><p>下一篇笔记会继续拆解路由懒加载和代码分割，敬请期待。</p>',
  (SELECT id FROM category WHERE name = '学习笔记'),
  'https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=60',
  1, '小林', 198, '2026-02-20 14:00:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '学习笔记')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = '从零搭建 Vite + Vue 3 + TypeScript 工程');

INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  'TypeScript 类型体操：让类型替你写代码',
  '从泛型约束到条件类型，聊聊类型系统能帮你提前发现多少问题。',
  '<p>很多人把 TypeScript 当成带类型的 JavaScript 来用，这没有错，但只发挥了一半价值。类型系统真正强大之处，是让编译器在代码运行前就帮你验证大量逻辑。</p><h2>泛型不是花架子</h2><p>一个 fetch 请求函数，如果返回值类型写死为 any，调用方就完全失去了类型保障。用泛型把它定义成 &lt;T&gt; 的签名，调用时传入具体类型，返回值就自动带上类型信息，编辑器的自动补全和重构提示都会变得可用。</p><h2>条件类型的实际场景</h2><p>处理接口返回值时，我们经常需要根据某个字段动态推导另一个字段的类型。条件类型允许我们在类型层面写 if-else，让复杂对象的结构自洽。</p><pre><code>type Unwrap&lt;T&gt; = T extends Promise&lt;infer U&gt; ? U : T</code></pre><p>上面这个工具类型可以把 Promise 包着的真实类型取出来，在封装异步 API 时非常常用。</p><blockquote>类型不是负担，是你和未来维护者之间的契约。</blockquote><h2>建议</h2><ul><li>先从禁用 any 开始，让类型真正流动起来</li><li>利用内置工具类型（Partial、Pick、Record 等）减少重复定义</li><li>类型体操适度即可，业务代码可读性优先</li></ul>',
  (SELECT id FROM category WHERE name = '学习笔记'),
  'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=800&q=60',
  1, '阿华', 267, '2026-02-25 20:00:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '学习笔记')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = 'TypeScript 类型体操：让类型替你写代码');

INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  '双指针技巧：从暴力解法到最优解',
  '以最长回文子串和三数之和为例，演示双指针如何把 O(n²) 优化到 O(n)。',
  '<p>双指针是算法面试里的高频套路，思路本身不难，难的是判断什么时候该用。这篇笔记用两道经典题目做对比，把使用场景讲清楚。</p><h2>什么时候想到双指针</h2><p>当一个线性结构的问题存在两个需要联动的下标时，就该考虑双指针。典型特征：数组有序、求区间最值、找满足条件的数对。</p><h2>案例一：有序数组的两数之和</h2><p>暴力做法是双重循环枚举所有组合，复杂度 O(n²)。改用一左一右两个指针，根据当前和与目标值的大小关系移动指针，一轮扫描就能完成，复杂度降到 O(n)。</p><h2>案例二：最长回文子串</h2><p>另一个常见套路是中心扩展：把每个字符（以及每两个字符之间的空隙）当作回文中心，向两边扩展，记录最长的一处。这也是双指针思想的变体，把 O(n³) 的枚举优化到 O(n²)。</p><blockquote>双指针的本质，是利用数据的有序性剪掉不可能的分支。</blockquote><p>建议做题时先写暴力解，再思考有哪些无效计算可以被跳过，指针移动的依据自然就浮现出来了。</p>',
  (SELECT id FROM category WHERE name = '学习笔记'),
  'https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&w=800&q=60',
  1, '小宋', 154, '2026-02-08 19:30:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '学习笔记')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = '双指针技巧：从暴力解法到最优解');

-- 活动动态
INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  '技术分享会回顾：一次高质量的 Code Review',
  '复盘本周分享会：我们如何评审一个订单模块的实现，以及从中提炼出的三条评审原则。',
  '<p>本周分享会的主题是 Code Review。我们以小组最近完成的订单模块为例，现场过了一遍完整评审流程，全程录制并整理了问题清单。</p><h2>评审中发现的典型问题</h2><ul><li>状态字段用魔法数字硬编码，缺少枚举语义</li><li>一个方法里塞了校验、组装、落库三段职责，难以测试</li><li>异常被吞掉，错误信息没有上下文</li></ul><h2>我们提炼的评审原则</h2><p>第一条：先问意图，再挑毛病。Review 的目的是帮作者把代码改好，而不是证明自己更厉害，讨论要围绕需求和设计意图展开。</p><p>第二条：一次只关注一个维度。性能和可读性不要混在一次评审里讨论，否则两边都聊不透。</p><p>第三条：评论给建议，不给命令。以开放式的提问引导作者自己发现问题，效果远好于直接指出答案。</p><blockquote>好的 Review 文化，是把每个人的代码都当作团队的代码。</blockquote><p>下一期分享会主题预告：数据库索引设计与慢查询排查，欢迎旁听。</p>',
  (SELECT id FROM category WHERE name = '活动动态'),
  'https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=800&q=60',
  1, '兴华小组', 329, '2026-03-05 09:30:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '活动动态')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = '技术分享会回顾：一次高质量的 Code Review');

INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  '48 小时黑客马拉松：我们造了一个学习工具',
  '从想法碰撞到产品雏形，记录小组第一次黑客马拉松的完整过程与收获。',
  '<p>上个月我们举办了小组的第一次黑客马拉松，48 小时内完成一个可演示的学习工具。这是当天从立项到展示的全过程记录。</p><h2>第一晚：想清楚做什么</h2><p>前两小时用来头脑风暴。我们最终收敛到一个方向：做一个帮组员规划学习路线的工具，输入目标技能，自动拆分出每周任务。选它是因为需求足够小、演示效果好、且每个人都能贡献。</p><h2>第二天：并行开发</h2><p>三个人负责前端页面，两个人写后端接口，一个人做数据与部署。约定好接口契约后两边并行，晚上第一次联调时已经能跑通主流程。</p><h2>最后六小时：打磨与展示</h2><p>演示前大家都在补细节：空状态、加载提示、边界输入。最终展示时虽然功能朴素，但每个环节都稳，评审组评价是完成度最高的一组。</p><blockquote>黑客马拉松的意义不在产品本身，而在于体验一次真实项目的完整节奏。</blockquote><p>活动复盘后我们达成共识：以后每季度举办一次，下一届主题是 AI 应用。</p>',
  (SELECT id FROM category WHERE name = '活动动态'),
  'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=800&q=60',
  1, '小杨', 245, '2026-01-18 16:00:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '活动动态')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = '48 小时黑客马拉松：我们造了一个学习工具');

INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  '新人导师计划：结对编程的经验与收获',
  '小组实行导师制三个月了，导师和新人的双视角复盘：结对编程到底带来了什么。',
  '<p>三个月前我们启动了新人导师计划，每位新成员与一位有经验的组员结对，每周至少完成两小时结对编程。这篇复盘同时采访了导师和新人的视角。</p><h2>新人的收获</h2><p>结对编程最大的好处是即时反馈。写错了代码不用等报错解释，旁边的人立刻指出问题所在，还能当场讲清楚为什么。三个月下来，新人平均能独立完成一个完整页面的开发。</p><h2>导师的收获</h2><p>教是最好的学。讲解一个概念的过程，会逼着自己把模糊的理解梳理成清晰的表达。多位导师反馈，在指导过程中发现了自己平时忽略的细节，比如 Vue 响应式更新的边界情况。</p><h2>踩过的坑</h2><ul><li>一开始节奏太快，新人跟不上，后来约定每 25 分钟轮换一次键盘</li><li>导师直接动手写而不是引导思考，会让新人失去练习机会</li><li>缺少结构化安排，结对容易变成各自写各自的</li></ul><blockquote>结对编程不是两个人写一份代码，而是两个人一起学会一份代码。</blockquote><p>计划会继续，下一批招募预计在春季开学后开始。</p>',
  (SELECT id FROM category WHERE name = '活动动态'),
  'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&w=800&q=60',
  1, '兴华小组', 176, '2026-01-05 10:00:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '活动动态')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = '新人导师计划：结对编程的经验与收获');

-- 项目实践
INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  'CMS 项目复盘：从需求分析到上线发布',
  '以我们正在做的官网 CMS 为例，复盘一个完整业务项目的五个阶段与踩坑记录。',
  '<p>这个网站本身就是一个学习项目：一套 Vue 3 前端 + Spring Boot 后端的 CMS 系统。借这个机会，我们把项目从零到上线的过程完整复盘一遍。</p><h2>阶段一：需求澄清</h2><p>第一步是确认到底要做什么。我们列了内容管理、权限控制、前台展示三大模块，并明确砍掉了两个不紧急的需求。需求列表写清楚之后，后续所有排期都有依据。</p><h2>阶段二：技术选型</h2><p>前端选择 Vue 3 + Vite + TypeScript + Element Plus，后端选择 Spring Boot + MyBatis-Plus，都是社区活跃、资料丰富的组合。选型的标准不是最潮，而是出了问题能找到答案。</p><h2>阶段三：分模块推进</h2><p>按后端接口、管理后台、门户前台三个方向并行。约定好接口文档后各自推进，每周两次同步会解决联调问题。这个阶段最大的教训是：接口字段一旦发布就不要随意改名，否则前后端会互相踩。</p><h2>阶段四：测试与打磨</h2><p>除了功能测试，我们还做了边界测试（空数据、超长文本、并发提交）。上线前统一过了无障碍和移动端适配。</p><h2>阶段五：上线与维护</h2><p>上线后我们持续收集问题，第一周修复了 12 个 bug，之后逐渐稳定。现在这个网站就是项目成果的最好展示。</p><blockquote>项目的价值不在于做完，而在于做完之后还能被持续改进。</blockquote>',
  (SELECT id FROM category WHERE name = '项目实践'),
  'https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=800&q=60',
  1, '阿华', 501, '2026-03-10 08:30:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '项目实践')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = 'CMS 项目复盘：从需求分析到上线发布');

INSERT INTO article (title, summary, content, category_id, cover_image, status, author, view_count, created_at)
SELECT
  'Git 协作工作流：让多人开发不再手忙脚乱',
  '分支策略、提交规范、冲突处理——小组经过两次事故后总结出的 Git 协作守则。',
  '<p>多人协作开发，Git 用不好是会出事故的。我们小组在项目里经历过误覆盖、冲突风暴、提交信息混乱，总结出下面这套适合小团队的协作流程。</p><h2>分支策略</h2><p>长期保留 main 分支作为稳定分支，所有功能在 feature 分支上开发，合并前必须经过 Review。功能分支命名统一为 feature/功能名，修复分支为 fix/问题描述。</p><h2>提交规范</h2><p>提交信息使用统一格式：类型（作用域）+ 中文描述，例如 feat(cms)：新增文章发布功能。一次提交只做一件事，方便回溯和回滚。</p><h2>冲突处理</h2><p>冲突不可怕，可怕的是不了解冲突怎么产生。我们的经验是：小步提交、频繁同步远端、避免长时间不更新本地分支。真遇到冲突时，先看双方改动意图再合并，不要盲目选择一边。</p><h2>两条铁律</h2><ul><li>不向 main 直接推送代码，必须走合并请求</li><li>不强制推送已经共享的历史</li></ul><blockquote>Git 规范不是为了束缚，而是为了让团队协作的每一步都可回溯、可恢复。</blockquote>',
  (SELECT id FROM category WHERE name = '项目实践'),
  'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=800&q=60',
  1, '小林', 223, '2025-12-22 15:00:00'
WHERE EXISTS (SELECT 1 FROM category WHERE name = '项目实践')
  AND NOT EXISTS (SELECT 1 FROM article WHERE title = 'Git 协作工作流：让多人开发不再手忙脚乱');

-- ============================================================
-- 三、产品分类（product_category）
-- ============================================================

INSERT INTO product_category (name, parent_id, sort_order, status)
SELECT '学习资源', NULL, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE name = '学习资源');

INSERT INTO product_category (name, parent_id, sort_order, status)
SELECT '效率工具', NULL, 2, 1
WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE name = '效率工具');

INSERT INTO product_category (name, parent_id, sort_order, status)
SELECT '开源组件', NULL, 3, 1
WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE name = '开源组件');

-- ============================================================
-- 四、产品（product）：status=1 已发布
-- ============================================================

INSERT INTO product (name, description, category_id, cover_image, price, status, created_at)
SELECT
  '《Vue 3 入门实战手册》',
  '面向初学者的 Vue 3 电子书，覆盖组合式 API、组件通信、路由与状态管理，配套完整示例工程，从零到一完成一个可部署的实战项目。',
  (SELECT id FROM product_category WHERE name = '学习资源'),
  'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=800&q=60',
  29.90, 1, '2026-02-15 09:00:00'
WHERE EXISTS (SELECT 1 FROM product_category WHERE name = '学习资源')
  AND NOT EXISTS (SELECT 1 FROM product WHERE name = '《Vue 3 入门实战手册》');

INSERT INTO product (name, description, category_id, cover_image, price, status, created_at)
SELECT
  '《前端面试题库：1000 题》',
  '按 JavaScript、Vue、工程化、算法分类整理的高频面试题，每题附解析思路与参考答案，适合面试前系统复习，也适合日常查漏补缺。',
  (SELECT id FROM product_category WHERE name = '学习资源'),
  'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=800&q=60',
  39.90, 1, '2026-01-20 10:00:00'
WHERE EXISTS (SELECT 1 FROM product_category WHERE name = '学习资源')
  AND NOT EXISTS (SELECT 1 FROM product WHERE name = '《前端面试题库：1000 题》');

INSERT INTO product (name, description, category_id, cover_image, price, status, created_at)
SELECT
  '《TypeScript 进阶之路》',
  '从类型基础到类型体操，系统讲解 TypeScript 在真实项目中的进阶用法，包含泛型、条件类型、装饰器与工程配置，附大量可运行的代码示例。',
  (SELECT id FROM product_category WHERE name = '学习资源'),
  'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=800&q=60',
  49.90, 1, '2026-03-02 14:00:00'
WHERE EXISTS (SELECT 1 FROM product_category WHERE name = '学习资源')
  AND NOT EXISTS (SELECT 1 FROM product WHERE name = '《TypeScript 进阶之路》');

INSERT INTO product (name, description, category_id, cover_image, price, status, created_at)
SELECT
  'XH-UI 轻量级组件库',
  '小组自研的 Vue 3 组件库，按需加载、TypeScript 全类型支持，内置暗色主题与无障碍支持，已在多个内部项目中落地验证。',
  (SELECT id FROM product_category WHERE name = '开源组件'),
  'https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?auto=format&fit=crop&w=800&q=60',
  19.90, 1, '2025-12-10 11:00:00'
WHERE EXISTS (SELECT 1 FROM product_category WHERE name = '开源组件')
  AND NOT EXISTS (SELECT 1 FROM product WHERE name = 'XH-UI 轻量级组件库');

INSERT INTO product (name, description, category_id, cover_image, price, status, created_at)
SELECT
  'XH-CLI 前端脚手架',
  '一条命令生成规范化的 Vue 3 + TypeScript 工程，内置代码规范检查、提交校验、自动化部署脚本，让项目初始化从半小时缩短到三十秒。',
  (SELECT id FROM product_category WHERE name = '效率工具'),
  'https://images.unsplash.com/photo-1542831371-29b0f74f9713?auto=format&fit=crop&w=800&q=60',
  9.90, 1, '2026-01-08 16:00:00'
WHERE EXISTS (SELECT 1 FROM product_category WHERE name = '效率工具')
  AND NOT EXISTS (SELECT 1 FROM product WHERE name = 'XH-CLI 前端脚手架');

INSERT INTO product (name, description, category_id, cover_image, price, status, created_at)
SELECT
  '学习笔记管理工具',
  '专为学习场景设计的笔记管理工具，支持 Markdown 编辑、标签体系与知识图谱，可以把零散笔记自动关联成结构化知识网络，支持多端同步。',
  (SELECT id FROM product_category WHERE name = '效率工具'),
  'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?auto=format&fit=crop&w=800&q=60',
  99.00, 1, '2026-03-06 09:30:00'
WHERE EXISTS (SELECT 1 FROM product_category WHERE name = '效率工具')
  AND NOT EXISTS (SELECT 1 FROM product WHERE name = '学习笔记管理工具');