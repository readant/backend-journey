/*
 * R__seed_data.sql
 * ================
 * 初始化基础数据（可重复执行，幂等）
 *
 * 密码：admin123（BCrypt 加密）
 */

-- 默认管理员
INSERT IGNORE INTO admin (username, password, real_name, status)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '超级管理员', 1);

-- 默认角色
INSERT IGNORE INTO role (name, code, description, status) VALUES
('超级管理员', 'ROLE_ADMIN', '系统最高权限，可执行所有操作', 1),
('编辑', 'ROLE_EDITOR', '内容管理权限，可管理文章和栏目', 1),
('查看者', 'ROLE_VIEWER', '只读权限，仅可查看内容', 1);

-- 给 admin 分配超级管理员角色
INSERT IGNORE INTO admin_role (admin_id, role_id)
SELECT a.id, r.id FROM admin a, role r
WHERE a.username = 'admin' AND r.code = 'ROLE_ADMIN';