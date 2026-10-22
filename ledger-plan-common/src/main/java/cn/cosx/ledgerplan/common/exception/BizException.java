package cn.cosx.ledgerplan.common.exception;

import cn.cosx.ledgerplan.common.tool.ErrorCode;

import java.io.Serial;

/**
 * 可预期的业务异常，由全局异常处理器转换为统一 JSON。
 */
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Integer code;
    private final int httpStatus;

    public BizException(ErrorCode errorCode) {
        this(errorCode, errorCode.message());
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.code();
        this.httpStatus = errorCode.httpStatus();
    }

    /**
     * 临时错误码，默认使用 400。
     */
    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = 400;
    }

    public Integer getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
