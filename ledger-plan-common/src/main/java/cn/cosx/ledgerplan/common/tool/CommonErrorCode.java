package cn.cosx.ledgerplan.common.tool;

public enum CommonErrorCode implements ErrorCode {

    BAD_REQUEST(400, "请求参数错误", 400),
    UNAUTHORIZED(401, "未登录或登录已过期", 401),
    FORBIDDEN(403, "没有操作权限", 403),
    NOT_FOUND(404, "请求的资源不存在", 404),
    METHOD_NOT_ALLOWED(405, "请求方法不支持", 405),
    INTERNAL_ERROR(500, "系统内部错误", 500);

    private final Integer code;
    private final String message;
    private final int httpStatus;

    CommonErrorCode(Integer code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public Integer code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }
}
