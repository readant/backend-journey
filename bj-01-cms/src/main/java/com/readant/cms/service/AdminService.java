package com.readant.cms.service;

import com.readant.cms.dto.AdminCreateReq;
import com.readant.cms.dto.AdminUpdateReq;
import com.readant.cms.dto.AdminVO;
import com.readant.cms.dto.LoginReq;
import com.readant.cms.dto.LoginVO;

import java.util.List;

/**
 * 管理员 Service 接口
 */
public interface AdminService {

    /**
     * 管理员登录
     */
    LoginVO login(LoginReq req);

    /**
     * 创建管理员
     */
    AdminVO create(AdminCreateReq req);

    /**
     * 根据 ID 查询管理员
     */
    AdminVO getById(Long id);

    /**
     * 查询所有管理员列表
     */
    List<AdminVO> listAll();

    /**
     * 更新管理员
     */
    AdminVO update(Long id, AdminUpdateReq req);

    /**
     * 删除管理员
     */
    void delete(Long id);
}