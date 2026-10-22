package cn.cosx.ledgerplan.common.enums;

import lombok.Getter;

/**
 * 支付渠道枚举
 * 1 现金，2 储蓄卡，3 信用卡，4 支付宝，5 微信
 */
@Getter
public enum PayChannelEnum {

    CASH(1, "现金"),
    DEBIT_CARD(2, "储蓄卡"),
    CREDIT_CARD(3, "信用卡"),
    ALIPAY(4, "支付宝"),
    WECHAT(5, "微信");

    private final Integer code;
    private final String desc;

    PayChannelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static PayChannelEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PayChannelEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
