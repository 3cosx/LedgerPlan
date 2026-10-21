package cn.cosx.ledgerplan.gateway.support;

/**
 * 网关不依赖 common（避免把 Servlet/MyBatis 带入 reactive 应用），
 * 因此保留一份字段完全一致的统一响应结构。
 */
public record GatewayResult<T>(Integer code, boolean success, String message, T data) {

    public static final Integer SUCCESS_CODE = 200;

    public static <T> GatewayResult<T> ok(T data) {
        return new GatewayResult<>(SUCCESS_CODE, true, "ok", data);
    }

    public static <T> GatewayResult<T> error(Integer code, String message) {
        return new GatewayResult<>(code, false, message, null);
    }
}
