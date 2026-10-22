package cn.cosx.ledgerplan.business.transaction.dto;

import cn.cosx.ledgerplan.common.pair.IntegerStringPair;
import cn.cosx.ledgerplan.common.pair.LongStringPair;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private Long id;

    private LongStringPair ledgerPair;

    private LongStringPair categoryPair;

    private IntegerStringPair payChannel;

    private IntegerStringPair TransactionType;

    private BigDecimal amount;

    private LocalDateTime transactionTime;

    private String remark;

    private String tags;

    private String attachUrl;
}
