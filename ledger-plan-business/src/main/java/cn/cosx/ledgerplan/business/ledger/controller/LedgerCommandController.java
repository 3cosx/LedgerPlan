package cn.cosx.ledgerplan.business.ledger.controller;

import cn.cosx.ledgerplan.business.ledger.dto.request.LedgerCreateRequest;
import cn.cosx.ledgerplan.business.ledger.dto.request.LedgerUpdateRequest;
import cn.cosx.ledgerplan.business.ledger.dto.vo.LedgerVO;
import cn.cosx.ledgerplan.business.ledger.domain.service.LedgerAggregateService;
import cn.cosx.ledgerplan.common.tool.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ledger/command")
@RequiredArgsConstructor
@Validated
public class LedgerCommandController {

    private final LedgerAggregateService ledgerAggregateService;

    @PostMapping("/create")
    public Result<LedgerVO> createLedger(@Valid @RequestBody LedgerCreateRequest request) {
        return Result.ok(ledgerAggregateService.createLedger(request));
    }

    @PostMapping("/update/{ledgerId}")
    public Result<LedgerVO> updateLedger(@PathVariable @Positive Long ledgerId,
                                         @Valid @RequestBody LedgerUpdateRequest request) {
        return Result.ok(ledgerAggregateService.updateLedger(ledgerId, request));
    }

    @PostMapping("/delete/{ledgerId}")
    public Result<Void> deleteLedger(@PathVariable @Positive Long ledgerId) {
        ledgerAggregateService.deleteLedger(ledgerId);
        return Result.ok();
    }
}
