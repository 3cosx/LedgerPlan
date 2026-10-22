package cn.cosx.ledgerplan.business.transaction.dto;

import cn.cosx.ledgerplan.business.transaction.domain.entity.Transaction;
import cn.cosx.ledgerplan.common.exception.BizException;
import cn.cosx.ledgerplan.common.tool.CommonErrorCode;

import java.math.BigDecimal;

public final class TransactionRules {
    private TransactionRules() {
    }

    public static void validate(Transaction transaction) {
        if (transaction.getType() == null || (transaction.getType() != 1 && transaction.getType() != 2)) {
            throw new BizException(CommonErrorCode.BAD_REQUEST, "交易类型只能是收入或支出");
        }
        if (transaction.getAmount() == null || transaction.getAmount().signum() <= 0) {
            throw new BizException(CommonErrorCode.BAD_REQUEST, "交易金额必须大于零");
        }
    }

    public static BigDecimal signedAmount(Transaction transaction) {
        return transaction.getType() == 1 ? transaction.getAmount() : transaction.getAmount().negate();
    }
}
