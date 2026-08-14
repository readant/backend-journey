package com.readant.cms.service.impl;

import com.readant.cms.common.BusinessException;
import com.readant.cms.entity.Role;
import com.readant.cms.mapper.RoleMapper;
import com.readant.cms.service.RoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 角色 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Role> listAll() {
        return roleMapper.selectList(null);
    }

    @Override
    public void assignRole(Long adminId, Long roleId) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "角色不存在");
        }

        // 检查是否已分配（唯一索引会防止重复，但提前检查可以返回友好提示）
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM admin_role WHERE admin_id = ? AND role_id = ?", Integer.class, adminId, roleId);
        if (count != null && count > 0) {
            throw new BusinessException(400, "该管理员已有此角色");
        }

        // 分配角色
        jdbcTemplate.update("INSERT INTO admin_role (admin_id, role_id) VALUES (?, ?)", adminId, roleId);
        log.info("分配角色: adminId={}, roleId={}", adminId, roleId);
    }

    @Override
    public void removeRole(Long adminId, Long roleId) {
        int affected =
                jdbcTemplate.update("DELETE FROM admin_role WHERE admin_id = ? AND role_id = ?", adminId, roleId);
        if (affected == 0) {
            throw new BusinessException(404, "该管理员没有此角色");
        }
        log.info("移除角色: adminId={}, roleId={}", adminId, roleId);
    }

    @Override
    public List<Role> getRolesByAdminId(Long adminId) {
        return jdbcTemplate.query(
                "SELECT r.* FROM role r INNER JOIN admin_role ar ON r.id = ar.role_id WHERE ar.admin_id = ?",
                (rs, rowNum) -> {
                    Role role = new Role();
                    role.setId(rs.getLong("id"));
                    role.setName(rs.getString("name"));
                    role.setCode(rs.getString("code"));
                    role.setDescription(rs.getString("description"));
                    role.setStatus(rs.getInt("status"));
                    return role;
                },
                adminId);
    }
}
