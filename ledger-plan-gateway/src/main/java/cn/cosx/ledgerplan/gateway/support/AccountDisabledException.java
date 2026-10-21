package cn.cosx.ledgerplan.gateway.support;

/**
 * 已登录但账号被禁用。由网关的鉴权异常处理器转换为 403。
 */
public class AccountDisabledException extends RuntimeException {

    public AccountDisabledException() {
        super("账号已被禁用");
    }
}
