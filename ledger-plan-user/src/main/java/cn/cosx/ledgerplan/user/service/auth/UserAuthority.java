package cn.cosx.ledgerplan.user.service.auth;

import java.util.List;

/**
 * 鉴权上下文使用的用户权限快照。
 *
 * <p>这是应用层与 RBAC 持久化之间的防腐层模型，只暴露角色/权限编码，
 * 不把 t_role、t_permission、t_user_role 等表结构泄漏给 AuthService。</p>
 */
public record UserAuthority(List<String> roles, List<String> permissions) {

    public UserAuthority {
        roles = roles == null ? List.of() : List.copyOf(roles);
        permissions = permissions == null ? List.of() : List.copyOf(permissions);
    }

    public static UserAuthority empty() {
        return new UserAuthority(List.of(), List.of());
    }
}
