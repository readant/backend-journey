package com.readant.cms.service.impl;

import com.readant.cms.common.BusinessException;
import com.readant.cms.common.TokenService;
import com.readant.cms.dto.*;
import com.readant.cms.entity.Admin;
import com.readant.cms.mapper.AdminMapper;
import com.readant.cms.service.AdminService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 管理员 Service 实现
 *
 * @RequiredArgsConstructor：Lombok 自动生成构造器注入（final 字段）
 * 比 @Autowired 更推荐，因为构造器注入能保证依赖不为空
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final TokenService tokenService;

    /** BCrypt 密码编码器 —— 自动加盐，每次加密结果都不同 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(LoginReq req) {
        // 1. 根据用户名查找管理员
        Admin admin = adminMapper.selectByUsername(req.getUsername());
        if (admin == null) {
            log.warn("登录失败：用户名 {} 不存在", req.getUsername());
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 2. 检查账号状态
        if (admin.getStatus() == 0) {
            log.warn("登录失败：用户 {} 已被禁用", req.getUsername());
            throw new BusinessException(401, "账号已被禁用");
        }

        // 3. 校验密码（BCrypt.matches 会自动处理加盐比对）
        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            log.warn("登录失败：用户 {} 密码错误", req.getUsername());
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 4. 生成 Token
        String token = tokenService.createToken(admin.getId());
        log.info("管理员登录成功: username={}", admin.getUsername());

        // 5. 返回 Token + 管理员信息
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setAdmin(toVO(admin));
        return loginVO;
    }

    @Override
    public AdminVO create(AdminCreateReq req) {
        // 1. 检查用户名是否已存在
        Admin existing = adminMapper.selectByUsername(req.getUsername());
        if (existing != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 2. 创建实体并加密密码
        Admin admin = new Admin();
        BeanUtils.copyProperties(req, admin);
        admin.setPassword(passwordEncoder.encode(req.getPassword()));
        admin.setStatus(1); // 默认启用

        // 3. 保存到数据库
        adminMapper.insert(admin);
        log.info("创建管理员: username={}", admin.getUsername());

        // 4. 返回脱敏后的信息（不返回密码）
        return toVO(admin);
    }

    @Override
    public AdminVO getById(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }
        return toVO(admin);
    }

    @Override
    public List<AdminVO> listAll() {
        return adminMapper.selectList(null).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public AdminVO update(Long id, AdminUpdateReq req) {
        // 1. 检查管理员是否存在
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 2. 更新字段（只更新非 null 的字段）
        if (req.getUsername() != null) {
            Admin existing = adminMapper.selectByUsername(req.getUsername());
            if (existing != null && !existing.getId().equals(id)) {
                throw new BusinessException(400, "用户名已存在");
            }
            admin.setUsername(req.getUsername());
        }
        if (req.getPassword() != null) {
            admin.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getRealName() != null) {
            admin.setRealName(req.getRealName());
        }
        if (req.getEmail() != null) {
            admin.setEmail(req.getEmail());
        }
        if (req.getPhone() != null) {
            admin.setPhone(req.getPhone());
        }
        if (req.getStatus() != null) {
            admin.setStatus(req.getStatus());
        }

        // 3. 保存更新
        adminMapper.updateById(admin);
        log.info("更新管理员: id={}", id);

        return toVO(admin);
    }

    @Override
    public void delete(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }
        adminMapper.deleteById(id);
        log.info("删除管理员: id={}", id);
    }

    /**
     * 将实体转换为 VO（脱敏，不返回密码）
     */
    private AdminVO toVO(Admin admin) {
        AdminVO vo = new AdminVO();
        BeanUtils.copyProperties(admin, vo);
        return vo;
    }
}
