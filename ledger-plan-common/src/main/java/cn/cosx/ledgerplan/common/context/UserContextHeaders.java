package cn.cosx.ledgerplan.common.context;

/**
 * 网关传递用户信息使用的内部请求头。
 */
public final class UserContextHeaders {

    public static final String USER_ID = "X-User-Id";
    public static final String EMAIL = "X-User-Email";
    public static final String ROLES = "X-User-Roles";
    public static final String PERMISSIONS = "X-User-Permissions";

    private UserContextHeaders() {
    }
}
