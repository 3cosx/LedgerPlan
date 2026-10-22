package cn.cosx.ledgerplan.business.transaction.service.impl;

import cn.cosx.ledgerplan.business.transaction.build.TransactionBuilder;
import cn.cosx.ledgerplan.business.transaction.domain.service.TransactionService;
import cn.cosx.ledgerplan.business.transaction.dto.TransactionDTO;
import cn.cosx.ledgerplan.business.transaction.dto.TransactionSingleLedgerQueryDTO;
import cn.cosx.ledgerplan.business.transaction.dto.convert.TransactionConvertor;
import cn.cosx.ledgerplan.business.transaction.dto.request.TransactionQueryRequest;
import cn.cosx.ledgerplan.business.transaction.service.TransactionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionQueryServiceImpl implements TransactionQueryService {

    private final TransactionService transactionService;

    private final TransactionBuilder transactionBuilder;

    private final Executor ledgerPlanTaskExecutor;

    @Override
    public List<List<TransactionDTO>> listTransactionByLeger(TransactionQueryRequest request) {
        List<TransactionQueryRequest.TransactionQuery> queries = request.getTransactions();
        if (CollectionUtils.isEmpty(queries)) {
            throw new RuntimeException("请选择账本");
        }
        return queries.stream()
                .map(query -> CompletableFuture.supplyAsync(() -> listByLedger(query), ledgerPlanTaskExecutor))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private List<TransactionDTO> listByLedger(TransactionQueryRequest.TransactionQuery query) {
        TransactionSingleLedgerQueryDTO queryDTO = TransactionConvertor.INSTANCE.transactionQuery2Dto(query);
        return transactionBuilder.buildTransactionAndCategoryDTOList2TransactionDTO(
                transactionService.listLedgerTransactions(queryDTO));
    }
}
