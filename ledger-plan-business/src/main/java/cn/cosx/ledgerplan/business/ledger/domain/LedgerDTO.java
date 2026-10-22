package cn.cosx.ledgerplan.business.ledger.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LedgerDTO {

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
