package cn.cosx.ledgerplan.business.transaction.controller;

import cn.cosx.ledgerplan.business.transaction.dto.TransactionDTO;
import cn.cosx.ledgerplan.business.transaction.dto.request.TransactionQueryRequest;
import cn.cosx.ledgerplan.business.transaction.service.TransactionQueryService;
import cn.cosx.ledgerplan.common.tool.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@RequestMapping("/transaction")
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionQueryService transactionQueryService;

    @PostMapping("/listTransactionByLeger")
    public Result<List<List<TransactionDTO>>> listTransactionByLeger(@Valid @RequestBody TransactionQueryRequest request) {
        return Result.ok(transactionQueryService.listTransactionByLeger(request));
    }


}
