package com.readant.cms.service.impl;

import com.readant.cms.common.BusinessException;
import com.readant.cms.common.TokenService;
import com.readant.cms.dto.AdminCreateReq;
import com.readant.cms.dto.AdminUpdateReq;
import com.readant.cms.dto.AdminVO;
import com.readant.cms.dto.LoginReq;
import com.readant.cms.dto.LoginVO;
import com.readant.cms.entity.Admin;
import com.readant.cms.mapper.AdminMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AdminServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private Admin buildAdmin(Long id, String username, String rawPassword, Integer status) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setUsername(username);
        admin.setPassword(encoder.encode(rawPassword));
        admin.setStatus(status);
        return admin;
    }

    @Nested
    @DisplayName("login 方法")
    class Login {

        @Test
        @DisplayName("用户名和密码正确 → 返回 Token 和管理员信息")
        void shouldReturnToken_whenCredentialsValid() {
            Admin admin = buildAdmin(1L, "admin", "admin123", 1);
            when(adminMapper.selectByUsername("admin")).thenReturn(admin);
            when(tokenService.createToken(1L)).thenReturn("token-abc");

            LoginReq req = new LoginReq();
            req.setUsername("admin");
            req.setPassword("admin123");

            LoginVO result = adminService.login(req);

            assertThat(result.getToken()).isEqualTo("token-abc");
            assertThat(result.getAdmin().getUsername()).isEqualTo("admin");
            verify(tokenService).createToken(1L);
        }

        @Test
        @DisplayName("用户名不存在 → 抛出业务异常 401")
        void shouldThrow_whenUsernameNotFound() {
            when(adminMapper.selectByUsername("ghost")).thenReturn(null);

            LoginReq req = new LoginReq();
            req.setUsername("ghost");
            req.setPassword("admin123");

            assertThatThrownBy(() -> adminService.login(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("用户名或密码错误");
            verify(tokenService, never()).createToken(any());
        }

        @Test
        @DisplayName("账号被禁用 → 抛出业务异常 401")
        void shouldThrow_whenAccountDisabled() {
            Admin admin = buildAdmin(1L, "admin", "admin123", 0);
            when(adminMapper.selectByUsername("admin")).thenReturn(admin);

            LoginReq req = new LoginReq();
            req.setUsername("admin");
            req.setPassword("admin123");

            assertThatThrownBy(() -> adminService.login(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("账号已被禁用");
        }

        @Test
        @DisplayName("密码错误 → 抛出业务异常 401")
        void shouldThrow_whenPasswordWrong() {
            Admin admin = buildAdmin(1L, "admin", "admin123", 1);
            when(adminMapper.selectByUsername("admin")).thenReturn(admin);

            LoginReq req = new LoginReq();
            req.setUsername("admin");
            req.setPassword("wrong-password");

            assertThatThrownBy(() -> adminService.login(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("用户名或密码错误");
        }
    }

    @Nested
    @DisplayName("create 方法")
    class Create {

        @Test
        @DisplayName("用户名不存在 → 创建成功且密码加密")
        void shouldCreate_whenUsernameAvailable() {
            when(adminMapper.selectByUsername("newuser")).thenReturn(null);

            AdminCreateReq req = new AdminCreateReq();
            req.setUsername("newuser");
            req.setPassword("123456");

            AdminVO result = adminService.create(req);

            // 返回体脱敏，不含密码
            assertThat(result.getUsername()).isEqualTo("newuser");

            ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
            verify(adminMapper).insert(captor.capture());
            Admin saved = captor.getValue();
            // 入库密码是 BCrypt 哈希，非明文
            assertThat(saved.getPassword()).isNotEqualTo("123456");
            assertThat(encoder.matches("123456", saved.getPassword())).isTrue();
            assertThat(saved.getStatus()).isEqualTo(1);
        }

        @Test
        @DisplayName("用户名已存在 → 抛出业务异常 400")
        void shouldThrow_whenUsernameTaken() {
            when(adminMapper.selectByUsername("admin")).thenReturn(buildAdmin(1L, "admin", "x", 1));

            AdminCreateReq req = new AdminCreateReq();
            req.setUsername("admin");
            req.setPassword("123456");

            assertThatThrownBy(() -> adminService.create(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("用户名已存在");
            verify(adminMapper, never()).insert(any(Admin.class));
        }
    }

    @Nested
    @DisplayName("update 方法")
    class Update {

        @Test
        @DisplayName("管理员不存在 → 抛出业务异常 404")
        void shouldThrow_whenAdminNotFound() {
            when(adminMapper.selectById(99L)).thenReturn(null);

            AdminUpdateReq req = new AdminUpdateReq();
            req.setRealName("张三");

            assertThatThrownBy(() -> adminService.update(99L, req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("管理员不存在");
        }

        @Test
        @DisplayName("更新为他人已占用的用户名 → 抛出业务异常 400")
        void shouldThrow_whenUsernameBelongsToOther() {
            Admin current = buildAdmin(1L, "admin", "x", 1);
            when(adminMapper.selectById(1L)).thenReturn(current);
            // 目标用户名被 id=2 的管理员占用
            when(adminMapper.selectByUsername("boss")).thenReturn(buildAdmin(2L, "boss", "x", 1));

            AdminUpdateReq req = new AdminUpdateReq();
            req.setUsername("boss");

            assertThatThrownBy(() -> adminService.update(1L, req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("用户名已存在");
            verify(adminMapper, never()).updateById(any(Admin.class));
        }

        @Test
        @DisplayName("更新自身用户名 → 不冲突，正常更新")
        void shouldUpdate_whenUsernameIsOwn() {
            Admin current = buildAdmin(1L, "admin", "x", 1);
            when(adminMapper.selectById(1L)).thenReturn(current);
            when(adminMapper.selectByUsername("admin")).thenReturn(current);

            AdminUpdateReq req = new AdminUpdateReq();
            req.setUsername("admin");

            AdminVO result = adminService.update(1L, req);

            assertThat(result.getUsername()).isEqualTo("admin");
            verify(adminMapper).updateById(any(Admin.class));
        }
    }

    @Nested
    @DisplayName("delete 方法")
    class Delete {

        @Test
        @DisplayName("管理员不存在 → 抛出业务异常 404")
        void shouldThrow_whenAdminNotFound() {
            when(adminMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> adminService.delete(9L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("管理员不存在");
            verify(adminMapper, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("管理员存在 → 删除成功")
        void shouldDelete_whenAdminExists() {
            when(adminMapper.selectById(1L)).thenReturn(buildAdmin(1L, "admin", "x", 1));

            adminService.delete(1L);

            verify(adminMapper).deleteById(1L);
        }
    }
}