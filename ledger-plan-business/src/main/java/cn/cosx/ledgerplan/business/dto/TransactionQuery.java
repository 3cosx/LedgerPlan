package cn.cosx.ledgerplan.business.dto;

import java.time.LocalDateTime;

/**
 * 交易流水分页查询条件。
 */
public record TransactionQuery(
        Long ledgerId,
        Integer type,
        Long categoryId,
        Integer payChannel,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long page,
        Long size) {
}
