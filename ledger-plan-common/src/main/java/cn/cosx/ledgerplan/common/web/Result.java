package cn.cosx.ledgerplan.common.web;

/**
 * 统一接口返回结构。
 */
public record Result<T>(Integer code, boolean success, String message, T data) {

    public static final Integer SUCCESS_CODE = 200;

    public static final Integer ERROR_CODE = 500;

    public static <T> Result<T> ok() {
        return new Result<>(SUCCESS_CODE, true, "ok", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(SUCCESS_CODE, true, "ok", data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ERROR_CODE, false, message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, false, message, null);
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.code(), false, errorCode.message(), null);
    }
}
