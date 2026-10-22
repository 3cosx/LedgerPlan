package cn.cosx.ledgerplan.common.context;

import java.util.Set;

/**
 * 当前请求的用户上下文，由网关写入的请求头解析而来。
 */
public record UserContext(Long userId, String email, Set<String> roles, Set<String> permissions) {

    public UserContext {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
