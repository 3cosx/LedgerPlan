package cn.cosx.ledgerplan.business.transaction.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易+分类联查聚合DTO（只读，用于数据库联查返回）
 */
@Data
public class TransactionAndCategoryDTO {

    private Long id;

    private Long ledgerId;

    private String ledgerName;

    private Long categoryId;

    private String categoryName;

    private Integer type; // 1收入 2支出

    private Integer payChannel;

    private BigDecimal amount;

    private LocalDateTime transactionTime;

    private String remark;

    private String tags;

    private String attachUrl;
}
