package cn.cosx.ledgerplan.business.ledger.controller;

import cn.cosx.ledgerplan.business.ledger.dto.vo.LedgerListVO;
import cn.cosx.ledgerplan.business.ledger.domain.service.LedgerAggregateService;
import cn.cosx.ledgerplan.business.ledger.dto.vo.LedgerVO;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import cn.cosx.ledgerplan.common.tool.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ledger/query")
@RequiredArgsConstructor
@Validated
public class LedgerQueryController {

    private final LedgerAggregateService ledgerAggregateService;


    @PostMapping("/list")
    public Result<LedgerListVO> getLedgerListByUser(){
        return Result.ok(ledgerAggregateService.getLedgerList());
    }

    @PostMapping("/detail/{ledgerId}")
    public Result<LedgerVO> getLedger(@PathVariable @Positive Long ledgerId) {
        return Result.ok(ledgerAggregateService.getLedger(ledgerId));
    }


}
