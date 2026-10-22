package cn.cosx.ledgerplan.business.transaction.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionSingleLedgerQueryDTO {

    private Long ledgerId;

    private Integer type;

    private Long categoryId;

    private Integer payChannel;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long LastLedgerId;

    private int page;

    private int size;
}
