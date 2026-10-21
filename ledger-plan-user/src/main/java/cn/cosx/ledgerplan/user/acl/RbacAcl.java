package cn.cosx.ledgerplan.user.acl;

import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.user.entity.Role;
import cn.cosx.ledgerplan.user.entity.UserRoleLink;
import cn.cosx.ledgerplan.user.mapper.RbacMapper;
import cn.cosx.ledgerplan.user.mapper.RoleMapper;
import cn.cosx.ledgerplan.user.mapper.UserRoleLinkMapper;
import cn.cosx.ledgerplan.user.service.auth.UserAuthority;
import cn.cosx.ledgerplan.user.service.auth.UserAuthorityPort;
import cn.cosx.ledgerplan.user.support.UserErrorCode;
import cn.cosx.ledgerplan.user.support.UserRole;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * RBAC 防腐层适配器。
 *
 * <p>把 t_role/t_permission/t_user_role 的持久化细节翻译成 {@link UserAuthority}，
 * 应用层（AuthService）只看到角色和权限编码，不接触 Mapper 或 RBAC 实体。</p>
 */
@Component
@RequiredArgsConstructor
public class RbacAcl implements UserAuthorityPort {

    private final RbacMapper rbacMapper;
    private final RoleMapper roleMapper;
    private final UserRoleLinkMapper userRoleLinkMapper;

    @Override
    public UserAuthority load(Long userId) {
        return new UserAuthority(selectRoleCodes(userId), selectPermissionCodes(userId));
    }

    @Override
    public UserAuthority loadForLogin(Long userId) {
        ensureDefaultRole(userId);
        return load(userId);
    }

    @Override
    @Transactional
    public UserAuthority replaceRoles(Long userId, Collection<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            throw new BizException(UserErrorCode.INVALID_ROLE);
        }

        Set<String> normalized = new LinkedHashSet<>();
        for (String roleCode : roleCodes) {
            if (!StringUtils.hasText(roleCode)) {
                throw new BizException(UserErrorCode.INVALID_ROLE);
            }
            normalized.add(roleCode.trim().toUpperCase(Locale.ROOT));
        }

        List<Long> roleIds = new ArrayList<>(normalized.size());
        for (String roleCode : normalized) {
            roleIds.add(requireRole(roleCode).getId());
        }

        userRoleLinkMapper.delete(Wrappers.<UserRoleLink>lambdaQuery()
                .eq(UserRoleLink::getUserId, userId));
        for (Long roleId : roleIds) {
            UserRoleLink link = new UserRoleLink();
            link.setUserId(userId);
            link.setRoleId(roleId);
            userRoleLinkMapper.insert(link);
        }
        return load(userId);
    }

    private List<String> selectRoleCodes(Long userId) {
        List<String> roles = rbacMapper.selectRoleCodes(userId);
        return roles == null || roles.isEmpty() ? List.of() : List.copyOf(roles);
    }

    private List<String> selectPermissionCodes(Long userId) {
        List<String> permissions = rbacMapper.selectPermissionCodes(userId);
        return permissions == null || permissions.isEmpty() ? List.of() : List.copyOf(permissions);
    }

    /**
     * 用户没有任何角色时补上默认的 NORMAL 角色。首次登录注册后调用。
     */
    private void ensureDefaultRole(Long userId) {
        if (!selectRoleCodes(userId).isEmpty()) {
            return;
        }
        Role role = requireRole(UserRole.NORMAL.name());
        UserRoleLink link = new UserRoleLink();
        link.setUserId(userId);
        link.setRoleId(role.getId());
        try {
            userRoleLinkMapper.insert(link);
        } catch (DuplicateKeyException ignored) {
            // 并发首次登录时另一个请求已经写入，忽略即可。
        }
    }

    private Role requireRole(String roleCode) {
        Role role = roleMapper.selectOne(Wrappers.<Role>lambdaQuery()
                .eq(Role::getCode, roleCode));
        if (role == null) {
            throw new BizException(UserErrorCode.INVALID_ROLE);
        }
        return role;
    }
}
