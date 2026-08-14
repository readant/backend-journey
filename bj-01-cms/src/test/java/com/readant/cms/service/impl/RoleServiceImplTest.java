package com.readant.cms.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readant.cms.common.BusinessException;
import com.readant.cms.entity.Role;
import com.readant.cms.mapper.RoleMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@DisplayName("RoleServiceImpl 单元测试")
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role buildRole(Long id, String code) {
        Role role = new Role();
        role.setId(id);
        role.setName(code);
        role.setCode(code);
        role.setStatus(1);
        return role;
    }

    @Nested
    @DisplayName("listAll 方法")
    class ListAll {

        @Test
        @DisplayName("返回全部角色")
        void shouldReturnAllRoles() {
            when(roleMapper.selectList(null)).thenReturn(List.of(buildRole(1L, "ROLE_ADMIN")));

            List<Role> roles = roleService.listAll();

            assertThat(roles).hasSize(1);
            assertThat(roles.get(0).getCode()).isEqualTo("ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("assignRole 方法")
    class AssignRole {

        @Test
        @DisplayName("角色不存在 → 抛出业务异常 404")
        void shouldThrow_whenRoleNotFound() {
            when(roleMapper.selectById(9L)).thenReturn(null);

            assertThatThrownBy(() -> roleService.assignRole(1L, 9L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("角色不存在");
            verify(jdbcTemplate, never()).update(anyString(), anyLong(), anyLong());
        }

        @Test
        @DisplayName("角色已分配 → 抛出业务异常 400")
        void shouldThrow_whenAlreadyAssigned() {
            when(roleMapper.selectById(1L)).thenReturn(buildRole(1L, "ROLE_ADMIN"));
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyLong(), anyLong()))
                    .thenReturn(1);

            assertThatThrownBy(() -> roleService.assignRole(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("该管理员已有此角色");
            verify(jdbcTemplate, never()).update(anyString(), anyLong(), anyLong());
        }

        @Test
        @DisplayName("角色存在且未分配 → 分配成功")
        void shouldAssign_whenValid() {
            when(roleMapper.selectById(1L)).thenReturn(buildRole(1L, "ROLE_ADMIN"));
            when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyLong(), anyLong()))
                    .thenReturn(0);

            roleService.assignRole(1L, 1L);

            verify(jdbcTemplate).update(eq("INSERT INTO admin_role (admin_id, role_id) VALUES (?, ?)"), eq(1L), eq(1L));
        }
    }

    @Nested
    @DisplayName("removeRole 方法")
    class RemoveRole {

        @Test
        @DisplayName("管理员没有该角色 → 抛出业务异常 404")
        void shouldThrow_whenNotAssigned() {
            when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(0);

            assertThatThrownBy(() -> roleService.removeRole(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("该管理员没有此角色");
        }

        @Test
        @DisplayName("移除成功")
        void shouldRemove_whenAssigned() {
            when(jdbcTemplate.update(anyString(), anyLong(), anyLong())).thenReturn(1);

            roleService.removeRole(1L, 1L);

            verify(jdbcTemplate)
                    .update(eq("DELETE FROM admin_role WHERE admin_id = ? AND role_id = ?"), eq(1L), eq(1L));
        }
    }

    @Nested
    @DisplayName("getRolesByAdminId 方法")
    class GetRolesByAdminId {

        @Test
        @DisplayName("返回管理员的角色列表")
        void shouldReturnRoles() {
            Role role = buildRole(1L, "ROLE_ADMIN");
            when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong()))
                    .thenReturn(List.of(role));

            List<Role> roles = roleService.getRolesByAdminId(1L);

            assertThat(roles).hasSize(1);
            assertThat(roles.get(0).getCode()).isEqualTo("ROLE_ADMIN");
        }
    }
}
