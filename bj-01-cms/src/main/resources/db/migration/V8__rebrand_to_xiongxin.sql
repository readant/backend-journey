/*
 * V8__rebrand_to_xiongxin.sql
 * ===========================
 * 变更原因：
 *   站点主体由「兴华小组」变更为「雄心工作室」，需同步已存在的演示数据。
 *   R__demo_content.sql 采用 WHERE NOT EXISTS 幂等写法，只会补新行、不会更新旧行，
 *   因此必须用一条版本化迁移把存量数据改名。
 *
 * 影响范围：
 *   article.author / article.title / article.summary / article.content
 *   product.name / product.description
 *   （栏目名称、分类名称不在本迁移范围，属于后续信息架构调整）
 *
 * 回滚方案：
 *   反向执行同样的 REPLACE 即可（'雄心工作室' → '兴华小组' 等）。本迁移只改文本，不删数据。
 */

-- 一、文章作者
UPDATE article SET author = '雄心工作室' WHERE author = '兴华小组';

-- 二、工作室介绍文章（标题同时是 R__demo_content 的幂等键，必须与种子脚本保持一致）
UPDATE article
SET title = '雄心工作室：把想法做成作品'
WHERE title = '兴华学习小组：一群人，把代码这件事学好';

-- 三、正文与摘要中的旧主体名
UPDATE article
SET summary = REPLACE(summary, '兴华学习小组', '雄心工作室')
WHERE summary LIKE '%兴华学习小组%';

UPDATE article
SET content = REPLACE(REPLACE(content, '兴华学习小组', '雄心工作室'), '兴华小组', '雄心工作室')
WHERE content LIKE '%兴华%';

-- 四、作品中带旧缩写前缀的名称与描述
UPDATE product
SET name = '雄心UI 轻量级组件库'
WHERE name = 'XH-UI 轻量级组件库';

UPDATE product
SET name = '雄心CLI 前端脚手架'
WHERE name = 'XH-CLI 前端脚手架';

UPDATE product
SET description = REPLACE(description, '小组自研', '工作室自研')
WHERE description LIKE '%小组自研%';
