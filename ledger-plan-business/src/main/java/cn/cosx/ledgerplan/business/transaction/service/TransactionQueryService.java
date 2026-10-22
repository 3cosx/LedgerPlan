package cn.cosx.ledgerplan.business.transaction.service;

import cn.cosx.ledgerplan.business.transaction.dto.TransactionDTO;
import cn.cosx.ledgerplan.business.transaction.dto.request.TransactionQueryRequest;

import java.util.List;


public interface TransactionQueryService {
    List<List<TransactionDTO>> listTransactionByLeger(TransactionQueryRequest request);
}
