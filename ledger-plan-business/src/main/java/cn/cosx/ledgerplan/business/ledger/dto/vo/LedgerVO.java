package cn.cosx.ledgerplan.business.ledger.dto.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账本响应。
 */
@Data
public class LedgerVO {

    private Long id;

    private String ledgerName;

    private String icon;

    private String description;

    private String currency;

    private BigDecimal totalAmount;

    private Integer isDefault;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
