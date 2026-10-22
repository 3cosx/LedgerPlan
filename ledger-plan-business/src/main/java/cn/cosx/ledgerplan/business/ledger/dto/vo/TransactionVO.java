package cn.cosx.ledgerplan.business.ledger.dto.vo;

import cn.cosx.ledgerplan.common.pair.IntegerStringPair;
import cn.cosx.ledgerplan.common.pair.LongStringPair;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易流水响应。
 */
@Data
public class TransactionVO {

    private Long id;

    private LongStringPair ledgerPair;

    private LongStringPair categoryPair; //仅展示最低等级的分类

    private IntegerStringPair payChannelPair;

    private LongStringPair transactionTypePair;

    private BigDecimal amount;

    private LocalDateTime transactionTime;

    private String remark;

    private String tags;

    private String attachUrl;
}
