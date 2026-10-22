package cn.cosx.ledgerplan.common.enums;

import lombok.Getter;

/**
 * 交易类型枚举
 * 1 收入，2 支出
 */
@Getter
public enum TransactionTypeEnum {

    INCOME(1, "收入"),
    EXPENSE(2, "支出");

    private final Integer code;
    private final String desc;

    TransactionTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static TransactionTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TransactionTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
