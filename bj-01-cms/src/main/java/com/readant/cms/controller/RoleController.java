package com.readant.cms.controller;

import com.readant.cms.common.R;
import com.readant.cms.entity.Role;
import com.readant.cms.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public R<List<Role>> listAll() {
        return R.success(roleService.listAll());
    }

    @PostMapping("/{roleId}/admins/{adminId}")
    public R<Void> assignRole(@PathVariable Long adminId, @PathVariable Long roleId) {
        roleService.assignRole(adminId, roleId);
        return R.success();
    }

    @DeleteMapping("/{roleId}/admins/{adminId}")
    public R<Void> removeRole(@PathVariable Long adminId, @PathVariable Long roleId) {
        roleService.removeRole(adminId, roleId);
        return R.success();
    }

    @GetMapping("/admins/{adminId}")
    public R<List<Role>> getRolesByAdminId(@PathVariable Long adminId) {
        return R.success(roleService.getRolesByAdminId(adminId));
    }
}