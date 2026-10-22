package cn.cosx.ledgerplan.user.support;

import cn.cosx.ledgerplan.common.tool.ErrorCode;

public enum UserErrorCode implements ErrorCode {

    EMAIL_CODE_TOO_FREQUENT(429, "验证码发送过于频繁，请稍后再试", 429),
    EMAIL_CODE_INVALID(4001, "验证码错误或已过期", 400),
    EMAIL_CODE_ATTEMPTS_EXCEEDED(4002, "验证码错误次数过多，请重新获取", 400),
    INVALID_NICKNAME(4003, "昵称不能为空", 400),
    INVALID_ROLE(4004, "角色不存在或为空", 400),
    ACCOUNT_DISABLED(4031, "账号已被禁用", 403),
    USER_NOT_FOUND(4041, "用户不存在", 404),
    EMAIL_SEND_FAILED(5001, "验证码邮件发送失败，请稍后重试", 500),
    RBAC_ROLE_NOT_INITIALIZED(5002, "角色数据未初始化", 500);

    private final Integer code;
    private final String message;
    private final int httpStatus;

    UserErrorCode(Integer code, String message, int httpStatus) {
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
