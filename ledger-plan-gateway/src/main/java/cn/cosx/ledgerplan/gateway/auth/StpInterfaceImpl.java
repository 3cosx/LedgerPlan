package cn.cosx.ledgerplan.gateway.auth;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.cosx.ledgerplan.gateway.support.SessionKeys;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 角色与权限在 user 服务登录时一次性写入 SaSession，网关直接读取，
 * 避免网关为了每次鉴权再访问用户库。角色/权限变更后需要重新登录生效。
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return readStringList(loginId, SessionKeys.PERMISSIONS);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return readStringList(loginId, SessionKeys.ROLES);
    }

    private List<String> readStringList(Object loginId, String key) {
        SaSession session = StpUtil.getSessionByLoginId(loginId, false);
        if (session == null) {
            return List.of();
        }
        Object value = session.get(key);
        if (value instanceof Collection<?> values) {
            return values.stream().map(String::valueOf).toList();
        }
        if (value instanceof String text && !text.isBlank()) {
            return List.of(text);
        }
        return List.of();
    }
}
