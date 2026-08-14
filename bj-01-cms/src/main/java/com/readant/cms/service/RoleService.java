package com.readant.cms.service;

import com.readant.cms.entity.Role;
import java.util.List;

/**
 * 角色 Service 接口
 */
public interface RoleService {

    /**
     * 查询所有角色
     */
    List<Role> listAll();

    /**
     * 给管理员分配角色
     */
    void assignRole(Long adminId, Long roleId);

    /**
     * 移除管理员的角色
     */
    void removeRole(Long adminId, Long roleId);

    /**
     * 查询管理员的角色列表
     */
    List<Role> getRolesByAdminId(Long adminId);
}
