package cn.cosx.ledgerplan.gateway.support;

import java.util.List;

public final class SessionKeys {

    public static final String USER_ID = "userId";
    public static final String EMAIL = "email";
    public static final String NICKNAME = "nickname";
    public static final String ROLES = "roles";
    public static final String PERMISSIONS = "permissions";
    public static final String STATUS = "status";

    public static final List<String> ALL = List.of(USER_ID, EMAIL, NICKNAME, ROLES, PERMISSIONS, STATUS);

    private SessionKeys() {
    }
}
