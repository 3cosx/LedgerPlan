package cn.cosx.ledgerplan.business.dto.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易流水响应。
 */
public record TransactionVO(
        Long id,
        Long ledgerId,
        Long categoryId,
        Integer payChannel,
        Integer type,
        BigDecimal amount,
        LocalDateTime transactionTime,
        String remark,
        String tags,
        String attachUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
