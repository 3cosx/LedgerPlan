package cn.cosx.ledgerplan.common.context;

import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.common.tool.CommonErrorCode;

/**
 * 当前请求的用户上下文持有者。
 *
 * <p>必须在请求结束时清理，避免线程池复用导致用户信息串请求。</p>
 */
public final class UserContextHolder {

    private static final ThreadLocal<UserContext> HOLDER = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(UserContext context) {
        HOLDER.set(context);
    }

    public static UserContext get() {
        return HOLDER.get();
    }

    public static UserContext getRequired() {
        UserContext context = HOLDER.get();
        if (context == null) {
            throw new BizException(CommonErrorCode.UNAUTHORIZED, "未获取到登录用户");
        }
        return context;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
