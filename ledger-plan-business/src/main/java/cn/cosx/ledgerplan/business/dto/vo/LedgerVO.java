package cn.cosx.ledgerplan.business.dto.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账本响应。
 */
public record LedgerVO(
        Long id,
        String ledgerName,
        String icon,
        String description,
        String currency,
        BigDecimal totalAmount,
        Integer isDefault,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
