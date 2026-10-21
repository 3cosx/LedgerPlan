package cn.cosx.ledgerplan.user.service.auth;

import java.util.Collection;

/**
 * 鉴权数据的应用层端口（防腐层接口）。
 *
 * <p>AuthService 只依赖本接口，不直接依赖 Mapper、RBAC 实体或具体数据库表。
 * 具体实现由 acl 包中的适配器提供。</p>
 */
public interface UserAuthorityPort {

    /**
     * 读取用户当前的角色与权限。
     */
    UserAuthority load(Long userId);

    /**
     * 登录时读取权限；用户还没有任何角色时补默认 NORMAL 角色。
     */
    UserAuthority loadForLogin(Long userId);

    /**
     * 覆盖式设置用户角色，供后续管理员用例使用。
     */
    UserAuthority replaceRoles(Long userId, Collection<String> roleCodes);
}
