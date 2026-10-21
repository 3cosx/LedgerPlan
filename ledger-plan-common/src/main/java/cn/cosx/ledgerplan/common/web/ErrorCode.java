package cn.cosx.ledgerplan.common.web;

/**
 * 业务错误码定义，同时携带建议的 HTTP 状态码。
 */
public interface ErrorCode {

    Integer code();

    String message();

    int httpStatus();
}
