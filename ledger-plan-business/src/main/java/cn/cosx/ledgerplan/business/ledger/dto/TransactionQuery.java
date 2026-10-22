package cn.cosx.ledgerplan.business.ledger.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 交易流水分页查询条件。
 */
@Data
public class TransactionQuery {

    private Long ledgerId;

    private Integer type;

    private Long categoryId;

    private Integer payChannel;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long page;

    private Long size;
}
