package cn.cosx.ledgerplan.business.transaction.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TransactionQueryRequest {

    @Valid
    private List<TransactionQuery> transactions;

    @Data
    public static class TransactionQuery{
        @NotNull
        private Long ledgerId;

        private Integer type;

        private Long categoryId;

        private Integer payChannel;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        private Long lastLedgerId;

        private int page;

        private int size;


    }
}
