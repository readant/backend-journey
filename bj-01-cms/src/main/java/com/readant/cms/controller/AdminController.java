package com.readant.cms.controller;

import com.readant.cms.common.LogOperation;
import com.readant.cms.common.R;
import com.readant.cms.dto.*;
import com.readant.cms.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @LogOperation(module = "管理员", action = "登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginReq req) {
        return R.success(adminService.login(req));
    }

    @LogOperation(module = "管理员", action = "创建")
    @PostMapping
    public R<AdminVO> create(@Valid @RequestBody AdminCreateReq req) {
        return R.success(adminService.create(req));
    }

    @GetMapping("/{id}")
    public R<AdminVO> getById(@PathVariable Long id) {
        return R.success(adminService.getById(id));
    }

    @GetMapping
    public R<List<AdminVO>> listAll() {
        return R.success(adminService.listAll());
    }

    @LogOperation(module = "管理员", action = "更新")
    @PutMapping("/{id}")
    public R<AdminVO> update(@PathVariable Long id, @Valid @RequestBody AdminUpdateReq req) {
        return R.success(adminService.update(id, req));
    }

    @LogOperation(module = "管理员", action = "删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        adminService.delete(id);
        return R.success();
    }
}